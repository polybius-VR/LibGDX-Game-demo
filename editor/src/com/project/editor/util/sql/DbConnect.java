package com.project.editor.util.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbConnect {
	private static Connection conn;

	private void Connect() throws ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loaded");

		SQLDriver mysqlDriver = new SQLDriver();
		mysqlDriver.setDbms("mysql");
		mysqlDriver.setDbName("java_game_project_db");
		mysqlDriver.setServerName("polybius.is-into-games.com");
		mysqlDriver.setUserName("project-user");
		mysqlDriver.setPassword("comsc207");
		mysqlDriver.setPortNumber(3306);

		try {
			conn = mysqlDriver.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

		Connect();

		FileInputStream fin = new FileInputStream(imgfile);

		PreparedStatement pre =
				conn.prepareStatement("insert into levels values(NULL,?,?,?,?, NULL)");

		pre.setString(1,"TestLevel01");
		pre.setInt(2,3);
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

		Connect();

		PreparedStatement pre = conn.prepareStatement("delete from levels where 1=1");
		pre.executeUpdate();

		System.out.println("Successfully deleted levels from the database!");

		pre.close();
		conn.close();
		System.out.println("Connection closed gracefully");
	}
}
