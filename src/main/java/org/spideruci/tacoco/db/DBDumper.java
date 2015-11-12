package org.spideruci.tacoco.db;

public class DBDumper {

	private final DBUtil db;

	@SuppressWarnings("resource")
	public static DBDumper createDBDumper(DBUtil db) {

		DBDumper printMgr = new DBDumper(db);
		return printMgr;
	}

	public DBUtil getDBUtil(){
		return db;
	}

	private DBDumper(DBUtil db) {
		this.db = db;
	}
}
