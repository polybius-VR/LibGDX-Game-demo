package com.project.game.desktop.screens;

import java.io.File;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HelpScreen extends AbstractScreen{

	MainMenuScreen mainMenu;
	
	public HelpScreen(Game game) {
		super(game);
		mainMenu = new MainMenuScreen(game);
	}
	
	public HelpScreen(Game game, MainMenuScreen mainMenu) {
		super(game);
		this.mainMenu = mainMenu;
	}

	TextureRegion controlHelp;
	SpriteBatch batch;
	float time = 0;

	@Override
	public void show() {
		controlHelp = new TextureRegion(new Texture("assets" + File.separator + "images" + File.separator + "control-help.png"), 0, 0, 1280, 720);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 1280, 720);		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(controlHelp, 0, 0);
		batch.end();

		time += delta;
		if (time > 1){
			if (Gdx.input.isKeyPressed(Keys.ANY_KEY))
				game.setScreen(mainMenu);
		}
	}

	@Override
	public void hide() {
		Gdx.app.debug("Platformer Demo", "dispose of help");
		batch.dispose();
		controlHelp.getTexture().dispose();
	}
}
