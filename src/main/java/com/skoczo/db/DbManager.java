package com.skoczo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class DbManager {
	private static String url = "jdbc:postgresql://localhost/Wedzarnia";
	private static String user = "postgres";
	private static String password = "postgres";
	private Connection con;

	@PostConstruct
	public void init() throws SQLException {
		con = DriverManager.getConnection(url, user, password);
	}

	public void executeUpdate(String sql) throws SQLException {
		Statement statement = con.createStatement();
		try {
			statement.executeUpdate(sql);
		} finally {
			statement.close();
		}
	}

	public ResultSet execute(String sql) throws SQLException {
		Statement statement = con.createStatement();
		return statement.executeQuery(sql);

	}
}
