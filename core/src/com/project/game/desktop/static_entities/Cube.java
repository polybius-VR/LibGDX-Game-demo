package com.project.game.desktop.static_entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.project.game.desktop.dynamic_entities.PlayerCharacter;

public class Cube {
	static final int FOLLOW = 0;
	public static final int FIXED = 1;
	public static final int CONTROLLED = 2;
	static final int DEAD = 3;
	static final float ACCELERATION = 20;
	static final float MAX_VELOCITY = 4;
	static final float DAMP = 0.80f;
 
	Map map;
 
	Vector2 pos = new Vector2();
	Vector2 accel = new Vector2();
	Vector2 vel = new Vector2();
 
	public Rectangle bounds = new Rectangle();
 
	public int state = FOLLOW;
	float stateTime = 0;
 
	Rectangle controllButtonRect = new Rectangle(480 - 64, 320 - 64, 64, 64);
	Rectangle followButtonRect = new Rectangle(480 - 64, 320 - 138, 64, 64);
	Rectangle dpadRect = new Rectangle(0, 0, 128, 128);
 
	Rectangle[] collisionRectangles = {
			new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()
	};
 
	public Cube (Map map, float x, float y) {
		this.map = map;
		this.pos.x = x;
		this.pos.y = y;
		this.bounds.x = pos.x + 0.2f;
		this.bounds.y = pos.y + 0.2f;
		this.bounds.width = this.bounds.height = 1.0f;
	}
 
	Vector2 target = new Vector2();
 
	public void update (float deltaTime) {
		
		processKeys();
 
		if (state == FOLLOW) {
			
			target.set(map.player.pos);
			
			if (map.player.dir == PlayerCharacter.RIGHT) target.x--;
			if (map.player.dir == PlayerCharacter.LEFT) target.x++;
			
			target.y += 0.2f;
 
			vel.set(target).sub(pos).scl(Math.min(4, pos.dst(target)) * deltaTime);
			if (vel.len() > MAX_VELOCITY) vel.nor().scl(MAX_VELOCITY);
			tryMove();
		}
 
		if (state == CONTROLLED) {
			
			accel.scl(deltaTime);
			vel.add(accel.x, accel.y);
			if (accel.x == 0) vel.x *= DAMP;
			if (accel.y == 0) vel.y *= DAMP;
			if (vel.x > MAX_VELOCITY) vel.x = MAX_VELOCITY;
			if (vel.x < -MAX_VELOCITY) vel.x = -MAX_VELOCITY;
			if (vel.y > MAX_VELOCITY) vel.y = MAX_VELOCITY;
			if (vel.y < -MAX_VELOCITY) vel.y = -MAX_VELOCITY;
			vel.scl(deltaTime);
			tryMove();
			vel.scl(1.0f / deltaTime);
		}
 
		if (state == FIXED) {
			if (stateTime > 5.0f) {
				stateTime = 0;
				state = FOLLOW;
			}
		}
 
		stateTime += deltaTime;
	}
 
	private void processKeys () {
 
		if ((Gdx.input.isKeyPressed(Keys.SPACE)) && state == FOLLOW && stateTime > 0.5f) {
			stateTime = 0;
			state = CONTROLLED;
			return;
		}
 
		if ((Gdx.input.isKeyPressed(Keys.SPACE)) && state == CONTROLLED && stateTime > 0.5f) {
			stateTime = 0;
			state = FIXED;
			return;
		}
 
		if ((Gdx.input.isKeyPressed(Keys.SPACE)) && state == FIXED && stateTime > 0.5f) {
			stateTime = 0;
			state = CONTROLLED;
			return;
		}
 
		if ((Gdx.input.isKeyPressed(Keys.F)) && stateTime > 0.5f) {
			stateTime = 0;
			state = FOLLOW;
			return;
		}
 
		if (state == CONTROLLED) {
			if (Gdx.input.isKeyPressed(Keys.A)) {
				accel.x = -ACCELERATION;
			} else if (Gdx.input.isKeyPressed(Keys.D)) {
				accel.x = ACCELERATION;
			} else {
				accel.x = 0;
			}
 
			if (Gdx.input.isKeyPressed(Keys.W)) {
				accel.y = ACCELERATION;
			} else if (Gdx.input.isKeyPressed(Keys.S)) {
				accel.y = -ACCELERATION;
			} else {
				accel.y = 0;
			}
		}
	}
 
	private void tryMove () {
 
		bounds.x += vel.x;
 
		fetchCollidableRects();
 
		for (int i = 0; i < collisionRectangles.length; i++) {
			Rectangle rect = collisionRectangles[i];
			if (bounds.overlaps(rect)) {
				if (vel.x < 0){
					bounds.x = rect.x + rect.width + 0.01f;
				}
				else{
					bounds.x = rect.x - bounds.width - 0.01f;
				}
				vel.x = 0;
			}
		}
 
		bounds.y += vel.y;
 
		fetchCollidableRects();
 
		for (int i = 0; i < collisionRectangles.length; i++) {
			Rectangle rect = collisionRectangles[i];
			if (bounds.overlaps(rect)) {
				if (vel.y < 0) {
 
					bounds.y = rect.y + rect.height + 0.01f;
 
				} else {
					bounds.y = rect.y - bounds.height - 0.01f;
				}
				vel.y = 0;
			}
		}
 
		pos.x = bounds.x - 0.2f;
		pos.y = bounds.y - 0.2f;
	}
 
	private void fetchCollidableRects () {
		
		int p1x = (int)bounds.x;
		int p1y = (int)Math.floor(bounds.y);
		int p2x = (int)(bounds.x + bounds.width);
		int p2y = (int)Math.floor(bounds.y);
		int p3x = (int)(bounds.x + bounds.width);
		int p3y = (int)(bounds.y + bounds.height);
		int p4x = (int)bounds.x;
		int p4y = (int)(bounds.y + bounds.height);
 
		int[][] tiles = map.tiles;
		int tile1 = tiles[p1x][map.tiles[0].length - 1 - p1y];
		int tile2 = tiles[p2x][map.tiles[0].length - 1 - p2y];
		int tile3 = tiles[p3x][map.tiles[0].length - 1 - p3y];
		int tile4 = tiles[p4x][map.tiles[0].length - 1 - p4y];
 
		if (tile1 != Map.EMPTY)
			collisionRectangles[0].set(p1x, p1y, 1, 1);
		else
			collisionRectangles[0].set(-1, -1, 0, 0);
		
		if (tile2 != Map.EMPTY)
			collisionRectangles[1].set(p2x, p2y, 1, 1);
		else
			collisionRectangles[1].set(-1, -1, 0, 0);
		
		if (tile3 != Map.EMPTY)
			collisionRectangles[2].set(p3x, p3y, 1, 1);
		else
			collisionRectangles[2].set(-1, -1, 0, 0);
		
		if (tile4 != Map.EMPTY)
			collisionRectangles[3].set(p4x, p4y, 1, 1);
		else
			collisionRectangles[3].set(-1, -1, 0, 0);
		
	}
 
	public void setControlled () {
		if (state == FOLLOW) {
			state = CONTROLLED;
			stateTime = 0;
		}
	}
}
