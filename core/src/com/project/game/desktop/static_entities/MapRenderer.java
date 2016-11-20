package com.project.game.desktop.static_entities;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;
import com.project.game.desktop.dynamic_entities.PlayerCharacter;

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
	
	TextureRegion cube;
	Animation cubeFixed;
	
	TextureRegion cubeControlled;
	TextureRegion dispenser;
	
	Animation spawn;
	Animation dying;
	
	TextureRegion spikes;
	
	Animation rocket;
	Animation rocketExplosion;
	
	TextureRegion rocketPad;
	TextureRegion endDoor;
	TextureRegion movingSpikes;
	TextureRegion laser;
	
	float stateTime = 0;
	Vector3 lerpTarget = new Vector3();
 
	public MapRenderer (Map map) {
		
		this.map = map;
		this.cam = new OrthographicCamera(24, 16);
		this.cam.position.set(map.player.pos.x, map.player.pos.y, 0);
		this.cache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
		this.blocks = new int[(int)Math.ceil(this.map.tiles.length / 24.0f)][(int)Math.ceil(this.map.tiles[0].length / 16.0f)];
 
		createAnimations();
		createBlocks();
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
		
		Gdx.app.debug("Cubocy", "blocks created");
	}
 
	private void createAnimations () {
		
		this.tile = new TextureRegion(new Texture(Gdx.files.internal("assets" + File.separator + "tile.png")), 0, 0, 20, 20);
		
		Texture playerTexture = new Texture(Gdx.files.internal("assets" + File.separator + "Bob.png"));
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
		cube = split[0];
		cubeFixed = new Animation(1, split[1], split[2], split[3], split[4], split[5]);
		
		split = new TextureRegion(playerTexture).split(20, 20)[2];
		
		cubeControlled = split[0];
		spawn = new Animation(0.1f, split[4], split[3], split[2], split[1]);
		dying = new Animation(0.1f, split[1], split[2], split[3], split[4]);
		dispenser = split[5];
		
		split = new TextureRegion(playerTexture).split(20, 20)[3];
		
		rocket = new Animation(0.1f, split[0], split[1], split[2], split[3]);
		rocketPad = split[4];
		
		split = new TextureRegion(playerTexture).split(20, 20)[4];
		rocketExplosion = new Animation(0.1f, split[0], split[1], split[2], split[3], split[4], split[4]);
		
		split = new TextureRegion(playerTexture).split(20, 20)[5];
		
		endDoor = split[2];
		movingSpikes = split[0];
		laser = split[1];
	}
 
	public void render (float deltaTime) {
		
		if (map.cube.state != Cube.CONTROLLED){
			cam.position.lerp(lerpTarget.set(map.player.pos.x, map.player.pos.y, 0), 2f * deltaTime);
		}else{
			cam.position.lerp(lerpTarget.set(map.cube.pos.x, map.cube.pos.y, 0), 2f * deltaTime);
 
		}
		
		cam.update();
 
		cache.setProjectionMatrix(cam.combined);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		cache.begin();
		
		for (int blockY = 0; blockY < 4; blockY++) {
			for (int blockX = 0; blockX < 6; blockX++) {
				cache.draw(blocks[blockX][blockY]);
			}
		}
		
		cache.end();
		stateTime += deltaTime;
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
 
		renderPlayerCharacter();
		renderCube();
		renderDispensers();
 
		batch.end();
 
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
 
	private void renderCube () {
		
		if (map.cube.state == Cube.FOLLOW){
			batch.draw(cube, map.cube.pos.x, map.cube.pos.y, 1.5f, 1.5f);
		}
		if (map.cube.state == Cube.FIXED)
		{
			batch.draw(cubeFixed.getKeyFrame(map.cube.stateTime, false), map.cube.pos.x, map.cube.pos.y, 1.5f, 1.5f);
		}
		if (map.cube.state == Cube.CONTROLLED){
			batch.draw(cubeControlled, map.cube.pos.x, map.cube.pos.y, 1.5f, 1.5f);
		}
		
	}
 
	private void renderDispensers () {
		for (int i = 0; i < map.dispensers.size; i++) {
			Dispenser dispenser = map.dispensers.get(i);
			batch.draw(this.dispenser, dispenser.bounds.x, dispenser.bounds.y, 1, 1);
		}
	}
 
	public void dispose () {
		cache.dispose();
		batch.dispose();
		tile.getTexture().dispose();
		cube.getTexture().dispose();
	}
}