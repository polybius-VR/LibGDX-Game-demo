package com.project.editor.util.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.project.editor.util.EncryptDecrypt;

public class DbConnect {
	private static Connection conn;
	String propertyFileName = "../java-game-project.properties";    

	private void connect() throws ClassNotFoundException{
	    String userPwdKey = "java-game-project.DatabasePassword" ;
	    String isPwdEcnryptedKey = "java-game-project.Password.IsEncrypted";
	    
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loaded");

		SQLDriver mysqlDriver = new SQLDriver();

		Properties properties = new Properties();
		try {
			EncryptDecrypt app = new EncryptDecrypt(propertyFileName,userPwdKey,isPwdEcnryptedKey);
	        String result = app.getDecryptedUserPassword();
	        
			properties.load(new FileInputStream(propertyFileName));
			
			mysqlDriver.setDbms("mysql");
			mysqlDriver.setDbName(properties.getProperty("java-game-project.DatabaseName"));
			mysqlDriver.setServerName(properties.getProperty("java-game-project.DatabaseServer"));
			mysqlDriver.setPortNumber(properties.getProperty("java-game-project.DatabasePort"));
			mysqlDriver.setUserName(properties.getProperty("java-game-project.DatabaseUser"));
			mysqlDriver.setPassword(result);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			conn = mysqlDriver.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Store the image in the database
	 * @param imgfile Image to be stored on the database
	 * @throws FileNotFoundException  Image file not found
	 * @throws ClassNotFoundException  SQL Driver jar not found
	 * @throws SQLException  SQL Exception
	 */
	public void imageDb(String imgName, String author, File imgfile) throws FileNotFoundException, ClassNotFoundException, SQLException{
		int authorid = 1;
		if (author.equalsIgnoreCase("Emil"))
			authorid = 2;
		if (author.equalsIgnoreCase("Philip"))
			authorid = 3;

		connect();

		FileInputStream fin = new FileInputStream(imgfile);

		PreparedStatement pre =
				conn.prepareStatement("insert into levels values(NULL,?,?,?,?, NULL)");

		pre.setString(1,imgName);
		pre.setInt(2,1);
		pre.setBinaryStream(3,(InputStream)fin,(int)imgfile.length());
		pre.setInt(4, authorid);
		pre.executeUpdate();
		System.out.println("Successfully inserted the file into the database!");

		pre.close();
		conn.close(); 
		System.out.println("Connection closed gracefully");
	}

	/**
	 * delete all current images in the database
	 * this is mostly for testing purposes now
	 * @throws ClassNotFoundException SQL Driver jar not found
	 * @throws SQLException SQL Exception
	 */
	public void deleteImages() throws ClassNotFoundException, SQLException{
		connect();

		PreparedStatement pre = conn.prepareStatement("delete from levels where 1=1");
		pre.executeUpdate();

		System.out.println("Successfully deleted levels from the database!");

		pre.close();
		conn.close();
		System.out.println("Connection closed gracefully");
	}
	
	public ResultSet retrieveImage(String name) throws ClassNotFoundException{
		connect();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select image from levels where name = '" + name + "'");
			
			return rs;
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return null;
	}
	
	public ResultSet retrieveLevelList() throws ClassNotFoundException{
		connect();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select name from levels");
			
			return rs;
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return null;
	}
	
	public ResultSet retrieveAuthorList() throws ClassNotFoundException{
		connect();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select name from authors");
			
			return rs;
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return null;
	}
	
	public ResultSet retrieveScores() throws ClassNotFoundException{
		connect();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT players.name, levels.name, scores.score, scores.date FROM scores INNER JOIN players ON players.id=scores.player_id INNER JOIN levels ON levels.id=scores.level_id");
			
			return rs;
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return null;
	}
	
	public void insertScore(String playerName, String levelName, float time) throws SQLException, ClassNotFoundException{
		Integer playerid = getPlayer(playerName);
		Integer levelid = getLevelId(levelName);
		connect();

		PreparedStatement pre =
				conn.prepareStatement("insert into scores values(NULL,?,?,?,NULL)");

		pre.setInt(1, playerid);
		pre.setInt(2, levelid);
		pre.setFloat(3, time);
		pre.executeUpdate();
		System.out.println("Successfully inserted score into the database!");

		pre.close();
		conn.close(); 
		System.out.println("Connection closed gracefully");
	}
	
	private int getPlayer(String playerName) throws ClassNotFoundException{
		connect();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select id from players where name='" + playerName + "'");
			
			if (rs.next()){
				return rs.getInt(1);
			} else {
				insertPlayer(playerName);
				return getPlayer(playerName);
			}
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return 0;
	}
	
	private int getLevelId(String levelName) throws ClassNotFoundException{
		connect();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select id from levels where name='" + levelName + "'");
			
			if (rs.next()){
				return rs.getInt(1);
			}
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return 0;
	}
	
	private void insertPlayer(String name) throws SQLException, ClassNotFoundException{
		connect();

		PreparedStatement pre =
				conn.prepareStatement("insert into players values(NULL,?)");

		pre.setString(1, name);
		pre.executeUpdate();
		System.out.println("Successfully inserted new player into the database!");

		pre.close();
		conn.close(); 
		System.out.println("Connection closed gracefully");
	}
}
