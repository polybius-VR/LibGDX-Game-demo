package com.project.editor.util.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLDriver {
	private String userName;
	private String password;
	private String dbms;
	private String serverName;
	private String dbName;
	private Integer portNumber;

	public Connection getConnection() throws SQLException {

		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		if (this.dbms.equals("mysql")) {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + "://" + this.serverName + ":" + this.portNumber + "/" + this.dbName, connectionProps);
		} else if (this.dbms.equals("derby")) {
			conn = DriverManager.getConnection(
					"jdbc:" + this.dbms + ":" + this.dbName + ";create=true", connectionProps);
		}
		System.out.println("Connected to database");
		return conn;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Integer getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}
}
