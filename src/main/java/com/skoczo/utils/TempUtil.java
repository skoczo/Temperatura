package com.skoczo.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.skoczo.db.DbManager;

@Stateless
public class TempUtil {

	@Inject
	private DbManager dbMan;
	
	public void addOutTemp(Date date, int temp) throws SQLException {
		dbMan.executeUpdate(buildSqlQuery("temp_out", date, temp));
	}
	
	
	private String buildSqlQuery(String table, Date date, int temp) {
		return "insert into " + table + "(date, temp) values( " + date.getTime() + ", " + temp + ");";
	}

	public void addSH1(Date date, int temp) throws SQLException {
		dbMan.executeUpdate(buildSqlQuery("temp_sh1", date, temp));
	}
	
	public void addSH2(Date date, int temp) throws SQLException {
		dbMan.executeUpdate(buildSqlQuery("temp_sh2", date, temp));
	}
	
	public ResultSet getOutTemp(Date start, Date finish) {
		ResultSet result = null;
		try {
			result = dbMan.execute(buildFindQuery("temp_out", start, finish));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public ResultSet getSh1Temp(Date start, Date finish) {
		ResultSet result = null;
		try {
			result = dbMan.execute(buildFindQuery("temp_sh1", start, finish));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ResultSet getSh2Temp(Date start, Date finish) {
		ResultSet result = null;
		try {
			result = dbMan.execute(buildFindQuery("temp_sh2", start, finish));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String buildFindQuery(String table, Date start, Date finish) {
		return "select date, temp from " + table + " where date < " + finish.getTime() + " and date > " + start.getTime() + " order by date;";
	}
}
