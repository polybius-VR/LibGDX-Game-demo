package com.project.game.desktop.static_entities;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.project.game.desktop.dynamic_entities.PlayerCharacter;
import com.project.game.desktop.screens.GameScreen;
 
public class MapRenderer {
 
	Map map;
	
	OrthographicCamera cam;
	SpriteCache cache;
	SpriteBatch batch = new SpriteBatch(5460);
	
	ImmediateModeRenderer20 renderer = new ImmediateModeRenderer20(false, true, 0);
	
	int[][] blocks;
	TextureRegion tile;
	
	Animation playerLeft;
	Animation playerRight;
	Animation playerJumpLeft;
	Animation playerJumpRight;
	Animation playerIdleLeft;
	Animation playerIdleRight;
	Animation playerDead;
	Animation zap;
	TextureRegion dispenser;
	
	Animation spawn;
	Animation dying;
	
	TextureRegion spikes;
	TextureRegion endDoor;
	
	Skin skin;
	
	float stateTime = 0;
	Vector3 lerpTarget = new Vector3();
	float offsetY = 5.0f;
	
	Stage stage;
	Label timeLabel;
	Label fpsLabel;
	float gameTime;
 
	public MapRenderer (Map map) {
		
		this.map = map;
		this.cam = new OrthographicCamera(24, 16);
		this.cam.position.set(map.player.pos.x, map.player.pos.y, 0);
		this.cache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
		this.blocks = new int[(int)Math.ceil(this.map.tiles.length / 24.0f)][(int)Math.ceil(this.map.tiles[0].length / 16.0f)];
		
		stage = new Stage(new ScreenViewport());
				
		skin = new Skin(Gdx.files.internal("assets" + File.separator + "UI" + File.separator + "uiskin.json"));
 
		createAnimations();
		createBlocks();
		createLabel();
	}
	
	
	private void createLabel(){
		
		timeLabel = new Label("Score : 0", skin, "default");
		timeLabel.setColor(Color.GREEN);
		timeLabel.setPosition(5, Gdx.graphics.getHeight() - timeLabel.getHeight() - 5);
 
		stage.addActor(timeLabel);
		
		fpsLabel = new Label("60 FPS", skin, "default");
		fpsLabel.setColor(Color.GREEN);
		fpsLabel.setPosition(Gdx.graphics.getWidth() - fpsLabel.getWidth() - 5, Gdx.graphics.getHeight() - fpsLabel.getHeight() - 5);
 
		stage.addActor(timeLabel);
		stage.addActor(fpsLabel);
	}
 
	private void createBlocks () {
		
		int width = map.tiles.length;
		int height = map.tiles[0].length;
		
		for (int blockY = 0; blockY < blocks[0].length; blockY++) {
			for (int blockX = 0; blockX < blocks.length; blockX++) {
				cache.beginCache();
				for (int y = blockY * 16; y < blockY * 16 + 16; y++) {
					for (int x = blockX * 24; x < blockX * 24 + 24; x++) {
						
						if (x > width) continue;
						if (y > height) continue;
						int posX = x;
						int posY = height - y - 1;
						if (map.match(map.tiles[x][y], Map.TILE)){
							cache.add(tile, posX, posY, 1, 1);
						}
						if (map.match(map.tiles[x][y], Map.SPIKES)){
							cache.add(spikes, posX, posY, 1, 1);
						}
					}
				}
				blocks[blockX][blockY] = cache.endCache();
			}
		}
		
		Gdx.app.debug("Game-Demo", "blocks created");
	}
 
	private void createAnimations () {
		
		this.tile = new TextureRegion(new Texture(Gdx.files.internal("assets" + File.separator + "images" + File.separator + "tile.png"), true), 1, 1, 32, 32);
		this.tile.getTexture().setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		
		Texture playerTexture = new Texture(Gdx.files.internal("assets" + File.separator + "images" + File.separator + "player.png"));
		TextureRegion[] split = new TextureRegion(playerTexture).split(20, 20)[0];
		TextureRegion[] mirror = new TextureRegion(playerTexture).split(20, 20)[0];
		
		for (TextureRegion region : mirror){
			region.flip(true, false);
		}
		
		spikes = split[5];
		playerRight = new Animation(0.1f, split[0], split[1]);
		playerLeft = new Animation(0.1f, mirror[0], mirror[1]);
		
		playerJumpRight = new Animation(0.1f, split[2], split[3]);
		playerJumpLeft = new Animation(0.1f, mirror[2], mirror[3]);
		
		playerIdleRight = new Animation(0.5f, split[0], split[4]);
		playerIdleLeft = new Animation(0.5f, mirror[0], mirror[4]);
		
		playerDead = new Animation(0.2f, split[0]);
		
		split = new TextureRegion(playerTexture).split(20, 20)[1];
		split = new TextureRegion(playerTexture).split(20, 20)[2];
		
		spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);
		dying = new Animation(0.1f, split[1], split[2], split[3], split[4]);
		dispenser = split[5];
		
		split = new TextureRegion(playerTexture).split(20, 20)[5];
		endDoor = split[2];
	}
	
private void processKeys(){
		
		if (Gdx.input.isKeyPressed(Keys.S)){
			if (offsetY > 0)
				offsetY -= 0.1f;
		} else {
			if (offsetY < 5.0f)
				offsetY += 0.1f;
		}
		
	}
 
	public void render (float deltaTime) {
		
		processKeys();
		
		cam.position.lerp(lerpTarget.set(map.player.pos.x , map.player.pos.y + offsetY, 0), 2 * deltaTime);
		
		cam.update();
		
		cache.setProjectionMatrix(cam.combined);
		
		/**
		 * Calculate Transparency for the SpriteCache
		 */
		Gdx.gl.glEnable(GL20.GL_BLEND); 
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		cache.begin();
		
		for (int blockY = 0; blockY < 10; blockY++) {
			for (int blockX = 0; blockX < 10; blockX++) {
				cache.draw(blocks[blockX][blockY]);
			}
		}
		
		cache.end();
		
		stateTime += deltaTime;
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
 
		if (map.endDoor != null) batch.draw(endDoor, map.endDoor.bounds.x, map.endDoor.bounds.y, 1, 1);
		renderPlayerCharacter();		
		renderDispensers();
		updateLabels(deltaTime);
 
		batch.end();
		
		stage.draw();	
 
	}
 
	private void renderPlayerCharacter () {
 
		Animation anim = null;
		boolean loop = true;
 
		if (map.player.state == PlayerCharacter.RUN) {
			if (map.player.dir == PlayerCharacter.LEFT)
				anim = playerLeft;
			else
				anim = playerRight;
		}
 
		if (map.player.state == PlayerCharacter.IDLE) {
			if (map.player.dir == PlayerCharacter.LEFT)
				anim = playerIdleLeft;
			else
				anim = playerIdleRight;
		}
 
		if (map.player.state == PlayerCharacter.JUMP) {
			if (map.player.dir == PlayerCharacter.LEFT)
				anim = playerJumpLeft;
			else
				anim = playerJumpRight;
		}
 
		if (map.player.state == PlayerCharacter.SPAWN) {
			anim = spawn;
			loop = false;
		}
 
		if (map.player.state == PlayerCharacter.DYING) {
			anim = dying;
			loop = false;
		}
 
		batch.draw(anim.getKeyFrame(map.player.stateTime, loop), map.player.pos.x, map.player.pos.y, 1, 1);
	}
 
	private void renderDispensers () {
		for (int i = 0; i < map.dispensers.size; i++) {
			Dispenser dispenser = map.dispensers.get(i);
			batch.draw(this.dispenser, dispenser.bounds.x, dispenser.bounds.y, 1, 1);
		}
	}
	
	private void updateLabels(float deltaTime){
		gameTime += deltaTime;
		long minutes = (long) Math.floor(gameTime / 60.0f);
		long seconds = (long) (gameTime - minutes * 60.0f);
        timeLabel.setText(String.format(GameScreen.getPLAYERNAME() + "\n" + "%02d:%02d", minutes, seconds));
		
		fpsLabel.setText(String.format("%d FPS", Gdx.graphics.getFramesPerSecond()));
	}
 
	public void dispose () {
		cache.dispose();
		batch.dispose();
		tile.getTexture().dispose();
	}


	public float getGameTime() {
		return gameTime;
	}


	public void setGameTime(float gameTime) {
		this.gameTime = gameTime;
	}
	
	
}
