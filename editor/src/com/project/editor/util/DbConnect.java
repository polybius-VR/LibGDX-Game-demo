package com.project.editor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DbConnect {
	/**
	 * Store the image in the database
	 * @param imgfile
	 */
	public void imageDb(File imgfile){
		System.out.println("Insert Image Example!");
		String driverName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://polybius.is-into-games.com:3306/";
		String dbName = "java_game_project_db";
		String userName = "project-user";
		String password = "comsc207";
		Connection con = null;
		try{
		   Class.forName(driverName);
		   con = DriverManager.getConnection(url+dbName,userName,password);
		  
		  FileInputStream fin = new FileInputStream(imgfile);
		 
		   PreparedStatement pre =
		   con.prepareStatement("insert into levels values(NULL,?,?,?,?, NULL)");
		 
		   pre.setString(1,"TestLevel01");
		   pre.setInt(2,3);
		   pre.setBinaryStream(3,(InputStream)fin,(int)imgfile.length());
		   pre.setInt(4, 1);
		   pre.executeUpdate();
		   System.out.println("Successfully inserted the file into the database!");

		   pre.close();
		   con.close(); 
		}catch (Exception e1){
			System.out.println(e1.getMessage());
		}
	}
	
	/**
	 * delete all current images in the database
	 * this is mostly for testing purposes now
	 */
	public void deleteImages(){
		System.out.println("Insert Image Example!");
		String driverName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://polybius.is-into-games.com:3306/";
		String dbName = "java_game_project_db";
		String userName = "project-user";
		String password = "comsc207";
		Connection con = null;
		try{
		   Class.forName(driverName);
		   con = DriverManager.getConnection(url+dbName,userName,password);
		   
		   PreparedStatement pre = con.prepareStatement("delete from levels where 1=1");
		   pre.executeUpdate();
		   
		   System.out.println("Successfully deleted levels from the database!");

		   pre.close();
		   con.close(); 
		}catch (Exception e1){
			System.out.println(e1.getMessage());
		}
	}
}
