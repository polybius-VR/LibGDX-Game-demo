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
	public void imageDb(File imgfile) throws FileNotFoundException, ClassNotFoundException, SQLException{
		System.out.println("Insert Image Example!");

		connect();

		FileInputStream fin = new FileInputStream(imgfile);

		PreparedStatement pre =
				conn.prepareStatement("insert into levels values(NULL,?,?,?,?, NULL)");

		pre.setString(1,"TestLevel01");
		pre.setInt(2,1);
		pre.setBinaryStream(3,(InputStream)fin,(int)imgfile.length());
		pre.setInt(4, 1);
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
		System.out.println("Delete Previous Image Example!");

		connect();

		PreparedStatement pre = conn.prepareStatement("delete from levels where 1=1");
		pre.executeUpdate();

		System.out.println("Successfully deleted levels from the database!");

		pre.close();
		conn.close();
		System.out.println("Connection closed gracefully");
	}
	
	public ResultSet retrieveImage() throws ClassNotFoundException{
		System.out.println("Retrive Image Example!");

		connect();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select image from levels where name = 'TestLevel01'");
			
			return rs;
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return null;
	}
}
