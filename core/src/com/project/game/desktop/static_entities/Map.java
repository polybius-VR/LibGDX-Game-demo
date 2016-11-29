package com.project.game.desktop.static_entities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.project.game.desktop.dynamic_entities.PlayerCharacter;

public class Map {

	static int EMPTY = 0;
	public static int TILE = 0xffffff;
	static int START = 0xff0000;
	static int END = 0xff00ff;
	static int DISPENSER = 0xff0100;
	static int SPIKES = 0x00ff00;
	static int ROCKET = 0x0000ff;
	static int MOVING_SPIKES = 0xffff00;
	static int LASER = 0x00ffff;

	public int[][] tiles;

	public PlayerCharacter player;

	Array<Dispenser> dispensers;
	Dispenser activeDispenser = null;

	public Cube cube;

	public Map () {
		loadBinary();
	}

	private void loadBinary () {

		dispensers = new Array<Dispenser>();

		retrieveImage();
		Pixmap pixmap = new Pixmap(Gdx.files.internal("assets" + File.separator + "levels.png"));

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
					cube = new Cube(this, activeDispenser.bounds.x, activeDispenser.bounds.y);
					cube.state = Cube.DEAD;
				} else if (match(pix, DISPENSER)) {
					Dispenser dispenser = new Dispenser(x, pixmap.getHeight() - 1 - y);
					dispensers.add(dispenser);
				} else {
					tiles[x][y] = pix;
				}
			}
		}

	}

	public void retrieveImage(){
		System.out.println("Retrive Image Example!");
		String driverName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://polybius.is-into-games.com:3306/";
		String dbName = "java_game_project_db";
		String userName = "project-user";
		String password = "comsc207";
		Connection con = null;
		try{
			Class.forName(driverName);
			con = DriverManager.getConnection(url+dbName,userName,password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select image from levels where name = 'TestLevel01'");
			
			while (rs.next()) {
				InputStream in = rs.getBinaryStream(1);
				OutputStream f = new FileOutputStream(new File(Gdx.files.local("assets" + File.separator + "levels.png").path()));
				int c = 0;
				while ((c = in.read()) > -1) {
					f.write(c);
				}
				f.close();
				in.close();
			}
		}catch(Exception ex){
			System.out.println(ex.getMessage());
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

		cube.update(deltaTime);

		if (cube.state == Cube.DEAD) {
			cube = new Cube(this, player.bounds.x, player.bounds.y);
		}

		for (int i = 0; i < dispensers.size; i++) {
			if (player.bounds.overlaps(dispensers.get(i).bounds)) {
				activeDispenser = dispensers.get(i);
			}
		}

		if (cube.state == Cube.DEAD){ 
			cube = new Cube(this, player.bounds.x, player.bounds.y);
		}
	}

	public boolean isDeadly (int tileId) {
		return tileId == SPIKES;
	}
}
