package com.project.game.desktop.static_entities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.project.editor.util.sql.DbConnect;
import com.project.game.desktop.dynamic_entities.PlayerCharacter;
 
public class Map {
	
	static int EMPTY = 0;
	public static int TILE = 0xffffff;
	static int START = 0xff0000;
	static int END = 0xff00ff;
	static int DISPENSER = 0xff0100;
	static int SPIKES = 0x00ff00;
 
	public int[][] tiles;
	
	public PlayerCharacter player;
	public EndDoor endDoor;
	
	Array<Dispenser> dispensers;
	Dispenser activeDispenser = null;
 
	public Map (String level) {
		loadBinary(level);
	}
 
	private void loadBinary (String level) {

		dispensers = new Array<Dispenser>();
		Pixmap pixmap;

		try {
			retrieveImage(level);
			System.out.println("Level Image retrieved successfully!");
			pixmap = new Pixmap(Gdx.files.internal("assets" + File.separator + "levels" + File.separator + "level.png"));
		} catch (IOException e) {
			e.printStackTrace();
			pixmap = new Pixmap(Gdx.files.internal("assets" + File.separator + "levels" + File.separator + "levelTest.png"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			pixmap = new Pixmap(Gdx.files.internal("assets" + File.separator + "levels" + File.separator + "levelTest.png"));
		} catch (SQLException e) {
			e.printStackTrace();
			pixmap = new Pixmap(Gdx.files.internal("assets" + File.separator + "levels" + File.separator + "levelTest.png"));
		}

		tiles = new int[pixmap.getWidth()][pixmap.getHeight()];

		for (int y = 0; y < pixmap.getHeight(); y++) {

			for (int x = 0; x < pixmap.getWidth(); x++) {

				int pix = (pixmap.getPixel(x, y) >>> 8) & 0xffffff;

				if (match(pix, START)) {
					Dispenser dispenser = new Dispenser(x, pixmap.getHeight() - 1 - y);
					dispensers.add(dispenser);
					activeDispenser = dispenser;
					player = new PlayerCharacter(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
					player.state = PlayerCharacter.SPAWN;
				} else if (match(pix, DISPENSER)) {
					Dispenser dispenser = new Dispenser(x, pixmap.getHeight() - 1 - y);
					dispensers.add(dispenser);
				} else if (match(pix, END)) {
						endDoor = new EndDoor(x, pixmap.getHeight() - 1 - y);
				} else {
					tiles[x][y] = pix;
				}
			}
		}

	}
	
	public void retrieveImage(String level) throws IOException, ClassNotFoundException, SQLException{
		DbConnect connection = new DbConnect();
		ResultSet rs = connection.retrieveImage(level);
		
		while (rs.next()){
			InputStream in = rs.getBinaryStream(1);
			OutputStream f = new FileOutputStream(new File(Gdx.files.local("assets" + File.separator + "levels" + File.separator + "level.png").path()));
			int c = 0;
			while ((c = in.read()) > -1) {
				f.write(c);
			}
			f.close();
			in.close();
		}
	}
 
	boolean match (int src, int dst) {
		return src == dst;
	}
 
	public void update (float deltaTime) {

		player.update(deltaTime);

		if (player.state == PlayerCharacter.DEAD) {
			player = new PlayerCharacter(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
		}

		for (int i = 0; i < dispensers.size; i++) {
			if (player.bounds.overlaps(dispensers.get(i).bounds)) {
				activeDispenser = dispensers.get(i);
			}
		}
	}
 
	public boolean isDeadly (int tileId) {
		return tileId == SPIKES;
	}
}