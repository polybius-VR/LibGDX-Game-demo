package com.project.game.desktop.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.project.game.desktop.static_entities.Map;
import com.project.game.desktop.static_entities.MapRenderer;

public class GameScreen extends AbstractScreen {
	
	private static String PLAYERNAME;
	Map map;
	MapRenderer mapRendered;

	public GameScreen(Game game) {
		super(game);
		map = new Map();
		mapRendered = new MapRenderer(map);
	}

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		map.update(delta);
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mapRendered.render(delta);
		
		if (map.player.bounds.overlaps(map.endDoor.bounds)) {
			game.setScreen(new GameOverScreen(game, mapRendered.getGameTime()));
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
			game.setScreen(new MainMenuScreen(game, this));
		}
	}

	@Override
	public void hide() {
		Gdx.app.debug("Platformer Demo", "dispose game screen");
	}

	public static String getPLAYERNAME() {
		if (PLAYERNAME == null)
			return "Player1";
		else
			return PLAYERNAME;
	}

	public static void setPLAYERNAME(String pLAYERNAME) {
		PLAYERNAME = pLAYERNAME;
	}

}
