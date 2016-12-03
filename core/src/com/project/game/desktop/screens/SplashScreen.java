package com.project.game.desktop.screens;

import java.io.File;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SplashScreen extends AbstractScreen {

	private Image javaImage;
	private Image javaFXImage; 
	private Image libgdxImage; 
	private Stage stage;

	public SplashScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void show() {
		stage = new Stage(); 

		/* Load splash image */ 
		javaImage = new Image(new Texture(Gdx.files.internal("assets" + File.separator + "images" + File.separator + "java-splash.png")));
		javaFXImage = new Image(new Texture(Gdx.files.internal("assets" + File.separator + "images" + File.separator + "javafx-splash.png")));
		libgdxImage = new Image(new Texture(Gdx.files.internal("assets" + File.separator + "images" + File.separator + "libgdx-splash.png"))); 

		/* Set the splash image in the center of the screen */ 
		float width = Gdx.graphics.getWidth(); 
		float height = Gdx.graphics.getHeight(); 

		/* Fade in the image*/ 
		javaImage.setPosition((width - javaFXImage.getWidth()) / 2,(height - javaFXImage.getHeight()) / 2);
		javaImage.getColor().a = 0f; 
		javaImage.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.delay(1, Actions.fadeOut(0.5f)))); 

		javaFXImage.setPosition((width - javaFXImage.getWidth()) / 2,(height - javaFXImage.getHeight()) / 2); 
		javaFXImage.getColor().a = 0f; 
		javaFXImage.addAction(Actions.delay(2, Actions.sequence(Actions.fadeIn(0.5f), Actions.delay(1, Actions.fadeOut(0.5f))))); 

		libgdxImage.setPosition((width - libgdxImage.getWidth()) / 2, (height - libgdxImage.getHeight()) / 2);  
		libgdxImage.getColor().a = 0f; 
		libgdxImage.addAction(Actions.delay(4, Actions.sequence(Actions.fadeIn(0.5f), Actions 
				.delay(1, Actions.moveBy(0, -(height - libgdxImage.getHeight() / 4), 1, Interpolation.swingIn)), Actions.run(new Runnable() { 
					@Override 
					public void run() { 

						/* Show main menu after swing out */ 
						((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game)); 
					} 
				})))); 

		stage.addActor(javaImage);
		stage.addActor(javaFXImage); 
		stage.addActor(libgdxImage); 
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1); 
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 

		stage.act(); 

		stage.draw(); 
		if (Gdx.input.isKeyPressed(Keys.ANY_KEY))
			game.setScreen(new MainMenuScreen(game));

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
