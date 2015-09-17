package org.spideruci.tacoco.db;

import static org.spideruci.tacoco.cli.CliAble.HOME;
import static org.spideruci.tacoco.cli.CliAble.OUTDIR;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readOptionalArgumentValue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateSQLiteDB {
	
	Connection c = null;
	String tacocoHome;
	String outdir;
	
	public CreateSQLiteDB(String home, String out) {
		tacocoHome = home;
		outdir = out;	
	}

	public static void main(String[] args) {
		String tacocoHome =  readOptionalArgumentValue(HOME,System.getProperty("user.dir"));
		String outdir = readOptionalArgumentValue(OUTDIR, tacocoHome+"/tacoco_output");
		CreateSQLiteDB db = new CreateSQLiteDB(tacocoHome, outdir);
		db.getConnection();
		db.buildTables();
		db.close();
	}

	private void buildTables() {
		
	}

	private void close() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getConnection() {
		try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+outdir+"/tacoco.db");
	    } catch ( Exception e ) {
	      e.printStackTrace();
	    }
	}
	
	private void run(String sql){
		try {
			Statement st = c.createStatement();
			st.executeQuery(sql);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}
