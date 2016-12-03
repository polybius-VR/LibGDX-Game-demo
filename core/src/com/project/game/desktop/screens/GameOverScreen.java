package com.project.game.desktop.screens;

import java.io.File;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
 
public class GameOverScreen extends AbstractScreen {
	TextureRegion intro;
	SpriteBatch batch;
	float time = 0;
	float finalTime = 0;
 
	public GameOverScreen (Game game, float finalTime) {
		super(game);
		this.finalTime = finalTime;
	}
 
	@Override
	public void show () {
		intro = new TextureRegion(new Texture(Gdx.files.internal("assets" + File.separator + "images" + File.separator + "gameover.png")));
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 1280, 720);
	}
 
	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(intro, 0, 0);
		batch.end();
 
		time += delta;
		if (time > 1) {
			if (Gdx.input.isKeyPressed(Keys.ANY_KEY) ) {
				game.setScreen(new MainMenuScreen(game));
			}
		}
	}
 
	@Override
	public void hide () {
		System.out.println("Player: " + GameScreen.getPLAYERNAME() + "\tScore: " + finalTime);
		Gdx.app.debug("Platformer", "dispose intro");
		batch.dispose();
		intro.getTexture().dispose();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
	
	
}
