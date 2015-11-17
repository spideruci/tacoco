package org.spideruci.tacoco.mutation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PITHandler {

	Connection conn;

	public void updateTacocoDB(String tacocodb, String pitReportDir){

		//open files: tacocodb, mutation_xml
		this.conn = getConnection(tacocodb);
		File reportDir = new File(pitReportDir);
		String[] dirs = reportDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		File mutationXml = new File(pitReportDir+"/"+dirs[dirs.length-1]+"/mutations.xml");

		//create mutation table
		createMutationTable();

		//parsing mutationXML and update tacocodb
		try{
			this.conn.setAutoCommit(false);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(mutationXml);
			NodeList mutations = doc.getElementsByTagName("mutation");

			for(int i=0; i<mutations.getLength(); ++i){
				Node node = mutations.item(i);
				Mutant m = new Mutant(i, node);
				insertDB(m);
			}
			
			this.conn.setAutoCommit(true);
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void insertDB(Mutant m) {
		
		//insert MUTATION table
		String sql_insert_mutions = "INSERT INTO MUTATION VALUES(?,?,?,?,?,?,?,?,?)";
		int src_id = getSourceID(m.FQN_target_class+".java");
		execPsmt(sql_insert_mutions, m.m_id,m.detected,m.status,m.FQN_target_class,m.FQN_target_method,m.lineNum,m.mutator,m.index,src_id);
		
		
		//insert MUTATION_TEST table
		@SuppressWarnings("unchecked")
		Set<String> killingTests = new HashSet<String>(Arrays.asList(m.killingTests));
		System.out.println(killingTests);
		String sql_get_tests = "SELECT A.TEST_ID, B.FQN "
							+"FROM STMT_COVERAGE A, TESTCASE B "
							+"WHERE A.TEST_ID = B.TEST_ID AND A.SOURCE_ID=? AND A.LINE_NUM=?";
		try{
			PreparedStatement psmt = this.conn.prepareStatement(sql_get_tests);
			psmt.setInt(1, src_id);
			psmt.setInt(2, m.lineNum);
			ResultSet rs = psmt.executeQuery();
			while(rs.next()){
				int test_id = rs.getInt(1);
				String test_fqn = rs.getString(2);
				boolean status = killingTests.contains(test_fqn); 
				System.out.println(test_fqn +" : "+status);
				String sql = "INSERT INTO MUTATION_TEST VALUES(?,?,?);";
				execPsmt(sql, m.m_id, test_id, status);
			}
			rs.close();
			psmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private int getSourceID(String fqn) {
		String sql="SELECT SOURCE_ID FROM SOURCE WHERE FQN=?";
		int s_id = -1;
		try{
			PreparedStatement psmt = this.conn.prepareStatement(sql);
			psmt.setString(1, fqn);
			ResultSet rs = psmt.executeQuery();
			if(rs.next()){
				s_id = rs.getInt(1);
			}
			psmt.close();
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return s_id;
	}

	private void execPsmt(String sql, Object... args) {
		try{
			PreparedStatement psmt = this.conn.prepareStatement(sql);
			for(int i=0; i<args.length; ++ i){
				psmt.setObject(i+1, args[i]);
			}
			psmt.execute();
		}catch(Exception e){
			System.out.println(sql);
			e.printStackTrace();
		}

	}	


	private void createMutationTable(){

		String d_mutation="DROP TABLE IF EXISTS MUTATION ";
		String d_mutation_test="DROP TABLE IF EXISTS MUTATION_TEST";


		String mutation="CREATE TABLE IF NOT EXISTS `MUTATION` ( "
				+ "`MUTATION_ID`	INTEGER,"
				+ "`DETECTED`	    TEXT, "
				+ "`STATUS`      	TEXT, "
				+ "`FQN_TARGET_CLASS`      	TEXT, "
				+ "`FQN_TARGET_METHOD`      	TEXT, "
				+ "`LINENUM`      	INTEGER, "
				+ "`MUTATOR`      	TEXT, "
				+ "`INDEX`      	INTEGER, "
				+ "`SOURCE_ID`      	INTEGER, "
				+ "PRIMARY KEY(MUTATION_ID)"
				+ ");";

		String mutation_test="CREATE TABLE IF NOT EXISTS `MUTATION_TEST` ( "
				+ "`MUTATION_ID`	INTEGER,"
				+ "`TEST_ID`	    TEXT, "
				+ "`STATUS`	    	TEXT "
				+ ");";

		exec(d_mutation);
		exec(d_mutation_test);
		exec(mutation);
		exec(mutation_test);
	}

	private Connection getConnection(String file) {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+file);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return c;
	}

	private void exec(String sql){
		try {
			Statement st = this.conn.createStatement();
			st.execute(sql);
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	public void runPit(String id, String classpath, String targetDir, AbstractBuildProbe probe, String outdir, String tacocoHome) {
		StringBuffer testClasses= new StringBuffer();
		StringBuffer classes= new StringBuffer();

		Set<String> excludeTests = null;
		File pitErrFile = new File(outdir, id+".pit.err");
		if(pitErrFile.exists()){
			excludeTests = getPITexcludeTests(pitErrFile);
		}

		for(String s : probe.getTestClasses()){
			if(excludeTests != null && excludeTests.contains(s)) continue;
			testClasses.append(s+",");
		}

		for(String s : probe.getClasses()){
			classes.append(s+",");
		}

		String pitPath = tacocoHome+"/lib/pitest-command-line-1.1.7.jar:"
				+tacocoHome+"/lib/pitest-1.1.7-SNAPSHOT.jar";

		File err = new File(outdir, id+".pit.err");
		File log = new File(outdir, id+".pit.log");
		if(err.exists()) err.delete();
		if(log.exists()) log.delete();


		ProcessBuilder pitRunner = new ProcessBuilder(
				"java",
				"-cp", pitPath+":"+classpath,
				"-Xmx2048M",
				"org.pitest.mutationtest.commandline.MutationCoverageReport",
				"--reportDir="+outdir+"/"+id,
				"--targetClasses="+classes,
				"--targetTests="+testClasses,
				"--sourceDirs="+targetDir+"/src",
				"--outputFormats=XML");
		pitRunner.directory(new File(targetDir));
		pitRunner.redirectError(err);
		pitRunner.redirectOutput(log);

		final Process pit;
		try{
			pit= pitRunner.start();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					pit.destroy();
				}
			}); 
			pit.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}

		//updateTacocoDB(outdir+"/"+id+".db",outdir+"/"+id);
	}

	private Set<String> getPITexcludeTests(File pitErrFile) {

		Set<String> set = new HashSet<>();
		Pattern p = Pattern.compile("testClass=.*,");

		try {
			for(String line:Files.readAllLines(Paths.get(pitErrFile.toURI()))){
				if(line.endsWith("did not pass without mutation.")){
					Matcher m = p.matcher(line);
					if(m.find()) {
						String match = m.group(0);
						String exClass = match.substring(10,match.length()-1);
						set.add(exClass);
					}
				}
			}


		} catch (IOException e) {
			set = null;
		}

		return set;
	}
}
