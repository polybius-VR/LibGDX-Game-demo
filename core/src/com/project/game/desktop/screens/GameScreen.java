package com.project.game.desktop.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.project.game.desktop.static_entities.Map;
import com.project.game.desktop.static_entities.MapRenderer;

public class GameScreen extends AbstractScreen {
	
	Map map;
	MapRenderer mapRendered;

	public GameScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void show() {
		map = new Map();
		mapRendered = new MapRenderer(map);
	}

	@Override
	public void render(float delta) {
		
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		map.update(delta);
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mapRendered.render(delta);
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
			game.setScreen(new MainMenu(game));
		}
	}

	@Override
	public void hide() {
		Gdx.app.debug("Platformer Demo", "dispose game screen");
	}

}
