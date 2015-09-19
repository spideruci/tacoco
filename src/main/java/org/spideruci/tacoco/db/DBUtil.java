package org.spideruci.tacoco.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class  DBUtil{

	private static DBUtil instance = null;

	private Map<String, Integer> pid = new HashMap<>();
	private Map<String, Integer> sid = new HashMap<>();
	private Map<String, Integer> tid = new HashMap<>();

	private static int nextSourceID=0; 
	private static int nextProjectID=0;
	private static int nextTestID=0; 

	private Connection c;

	public int getSourceID(String FQN){
		int id;
		if(!sid.containsKey(FQN)){
			id = nextSourceID++;
			sid.put(FQN, id);
		}
		else id = sid.get(FQN);
		return id;
	}

	public int getTestID(String FQN){
		int id;
		if(!tid.containsKey(FQN)){
			id = nextTestID++;
			tid.put(FQN, id);
		}
		else id = tid.get(FQN);
		return id;
	}

	public int getProjectID(String FQN){
		int id;
		if(!pid.containsKey(FQN)){
			id = nextProjectID++;
			pid.put(FQN, id);
		}
		else id = pid.get(FQN);
		return id;
	}


	public static DBUtil getInstance(String dbFile){
		if(instance != null) return instance;
		instance = new DBUtil(dbFile);
		return instance;
	};

	private DBUtil(String file){
		getConnection(file);
		buildTables();
	}

	private void buildTables() {

		String project="CREATE TABLE IF NOT EXISTS `PROJECT` ( "
				+ "`PROJECT_ID`	INTEGER,"
				+ "`FQN`	    TEXT, "
				+ "`BUILD`      TEXT, "
				+ "PRIMARY KEY(project_id)"
				+ ");";

		String testcase = "CREATE TABLE IF NOT EXISTS `TESTCASE` ( "
				+ "`TEST_ID`	INTEGER,"
				+ "`FQN`	    TEXT, "
				+ "`STATUS`     INTEGER,"
				+ "`PROJECT_ID` INTEGER,"
				+ "PRIMARY KEY(TEST_ID, PROJECT_ID),"
				+ "FOREIGN KEY(PROJECT_ID) REFERENCES PROJECT(PROJECT_ID)"
				+ ");";

		String src="CREATE TABLE IF NOT EXISTS `SOURCE` ( "
				+ "`SOURCE_ID`	INTEGER,"
				+ "`FQN`	    TEXT, "
				+ "`SLOC`       INTEGER,"
				+ "`PROJECT_ID` INTEGER,"
				+ "PRIMARY KEY(SOURCE_ID, PROJECT_ID),"
				+ "FOREIGN KEY(PROJECT_ID) REFERENCES PROJECT(PROJECT_ID)"
				+ ");";

		String lineC ="CREATE TABLE IF NOT EXISTS `LINE_COVERAGE` ( "
				+ "`TEST_ID`	INTEGER,"
				+ "`SOURCE_ID`	INTEGER,"
				+ "`LINE_NUM`   INTEGER,"
				+ "`PROJECT_ID`  INTEGER,"
				+ "PRIMARY KEY(TEST_ID, SOURCE_ID, LINE_NUM, PROJECT_ID)"
				+ ");";

		exec(project);
		exec(testcase);
		exec(src);
		exec(lineC);
	}

	private void exec(String sql){
		try {
			Statement st = c.createStatement();
			st.execute(sql);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	private void getConnection(String file) {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+file);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}


	public void close() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertProject(String FQN, String builderType) {
		int id = getProjectID(FQN);

		String sql = "INSERT INTO PROJECT"
				+ " VALUES(?,?,?)";
		execPsmt(sql, id, FQN, builderType);	
	}

	private void execPsmt(String sql, Object... args) {
		try{
			PreparedStatement psmt = c.prepareStatement(sql);
			for(int i=0; i<args.length; ++ i){
				psmt.setObject(i+1, args[i]);
			}
			psmt.execute();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public void insertTest(String FQN, boolean pass, int projectID) {
		String sql = "INSERT INTO TESTCASE "
				+"VALUES(?,?,?,?)";
		int status = pass?0:1;
		execPsmt(sql, getTestID(FQN), FQN, status, projectID);
	}

	public void insertSource(String FQN, int sloc, int projectID) {
		String sql = "INSERT INTO SOURCE "
				+"VALUES(?,?,?,?)";
		execPsmt(sql, getSourceID(FQN), FQN, sloc, projectID);
	}

	public void insertLineCoverage(int testID, int sourceID, int lineNumber, int projectID) {
		String sql = "INSERT INTO LINE_COVERAGE "
				+"VALUES(?,?,?,?)";
		execPsmt(sql, testID, sourceID, lineNumber, projectID);
	}

	public void insertExecutableLineNumber(int sourceID, int lineNumber, int projectID){

	}

	public void prepareDBFor(String projectFQN) {
		String sql = "SELECT PROJECT_ID, FQN FROM PROJECT";
		try{
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				sid.put(rs.getString("FQN"), rs.getInt("PROJECT_ID"));
				++nextProjectID;
			}
			stmt.close();
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(sid.containsKey(projectFQN)) {
			deleteTablesFor(getProjectID(projectFQN));
		}
	}

	private void deleteTablesFor(int project_id) {

		String deleteProject="DELETE FROM PROJECT WHERE PROJECT_ID=?";
		String deleteTest="DELETE FROM TESTCASE WHERE PROJECT_ID=?";
		String deleteSource="DELETE FROM SOURCE WHERE PROJECT_ID=?";
		String deleteLineCoverage="DELETE FROM LINE_COVERAGE WHERE PROJECT_ID=?";

		execPsmt(deleteProject, project_id);
		execPsmt(deleteTest, project_id);
		execPsmt(deleteSource, project_id);
		execPsmt(deleteLineCoverage, project_id);	
	}

	public void setAutoCommit(boolean flag){
		try{
			c.setAutoCommit(flag);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
