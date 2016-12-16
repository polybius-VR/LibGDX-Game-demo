package com.project.game.desktop.screens;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.project.editor.util.sql.DbConnect;

public class MainMenuScreen extends AbstractScreen {

	Skin skin;
	Stage stage;
	SpriteBatch batch;
	GameScreen gameScreen;

	TextButton contolsButton;
	//SelectBox
	List<String> levels = null;
	SelectBox<String> levelList = null;

	public MainMenuScreen(Game game) {
		super(game);
		create();
		gameScreen = new GameScreen(game, "TestLevel01");
	}

	public MainMenuScreen(Game game, GameScreen gameScreen){
		super(game);
		create();
		this.gameScreen = gameScreen;
	}

	public void create(){
		batch = new SpriteBatch();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
		skin = new Skin();
		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap((int)Gdx.graphics.getWidth()/4,(int)Gdx.graphics.getHeight()/10, Pixmap.Format.RGB888);
		pixmap.setColor(Color.LIGHT_GRAY);
		pixmap.fill();

		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		BitmapFont bfont=new BitmapFont();
		skin.add("default",bfont);

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.WHITE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

		textButtonStyle.font = skin.getFont("default");

		skin.add("default", textButtonStyle);
		
		Skin skin2 = new Skin(Gdx.files.internal("assets" + File.separator + "UI" + File.separator + "uiskin.json"));

		
		
		
		try {
			levels = retrieveLevelList();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		String[] array = levels.toArray(new String[0]);
		
		SelectBox<String> levelList = new SelectBox<String>(skin2);
		levelList.setItems((String[]) array);
		levelList.setWidth(150);
		levelList.setPosition(Gdx.graphics.getWidth()/2 - levelList.getWidth()/2, 0);

		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		TextButton nameButton = new TextButton("Name",textButtonStyle);
		nameButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , (Gdx.graphics.getHeight()/2) + nameButton.getHeight()*2);

		TextButton newGameButton = new TextButton("New Game",textButtonStyle);
		newGameButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , (Gdx.graphics.getHeight()/2) + newGameButton.getHeight());

		TextButton continueButton = new TextButton("Continue",textButtonStyle);
		continueButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , Gdx.graphics.getHeight()/2);

		contolsButton = new TextButton("Controls",textButtonStyle);
		contolsButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , (Gdx.graphics.getHeight()/2) - contolsButton.getHeight());

		TextButton refreshList = new TextButton("Refresh Level List",textButtonStyle);
		refreshList.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , (Gdx.graphics.getHeight()/2) - refreshList.getHeight() * 2);
		
		TextButton exitButton = new TextButton("Exit",textButtonStyle);
		exitButton.setPosition(Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth()/8 , (Gdx.graphics.getHeight()/2) - exitButton.getHeight() * 3);

		stage.addActor(levelList);
		stage.addActor(nameButton);
		stage.addActor(newGameButton);
		stage.addActor(continueButton);		
		stage.addActor(contolsButton);
		stage.addActor(refreshList);
		stage.addActor(exitButton);

		// Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
		// Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
		// ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
		// revert the checked state.
		MainMenuScreen mainMenu = this;

		nameButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				MyTextInputListener listener = new MyTextInputListener();
				Gdx.input.getTextInput(listener, "Game Input", "", "Player's Name");
			}
		});

		newGameButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				newGameButton.setText("Starting new game");
				gameScreen = new GameScreen(game, levelList.getSelected());
				((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen); 
			}
		});

		continueButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				continueButton.setText("Loading game");
				((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen); 
			}
		});

		contolsButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new HelpScreen(game, mainMenu)); 
				contolsButton.setChecked(false);
			}
		});
		
		refreshList.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				try {
					levels = retrieveLevelList();
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				String[] array = levels.toArray(new String[0]);
				levelList.setItems((String[]) array);
			}
		});

		exitButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				exitButton.setText("Closing Game");
				Gdx.app.exit(); 
			}
		});
	}
	
	private List<String> retrieveLevelList() throws ClassNotFoundException, SQLException {
		DbConnect connection = new DbConnect();
		List<String> list = new ArrayList<String>();
		ResultSet rs = connection.retrieveLevelList();

		while (rs.next()){
			list.add(rs.getString(1));
		}
		return list;
	}

	public void render (float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void dispose () {
		stage.dispose();
		skin.dispose();
		batch.dispose();
		gameScreen.dispose();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}

class MyTextInputListener implements TextInputListener {
	
	@Override
	public void input (String text) {
		GameScreen.setPLAYERNAME(text);
	}

	@Override
	public void canceled () {
	}

}
