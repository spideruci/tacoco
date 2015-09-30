package org.spideruci.tacoco.db;

import static org.spideruci.tacoco.cli.CliAble.EXEC;
import static org.spideruci.tacoco.cli.CliAble.HOME;
import static org.spideruci.tacoco.cli.CliAble.OUTDIR;
import static org.spideruci.tacoco.cli.CliAble.SUT;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readArgumentValue;
import static org.spideruci.tacoco.cli.CliAble.AnalyzerCli.readOptionalArgumentValue;
import static org.spideruci.tacoco.db.DBDumper.createDBDumper;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.spideruci.tacoco.AbstractBuildProbe;

public class CreateSQLiteDB {

	Connection c = null;
	String tacocoHome;
	String outdir;

	public CreateSQLiteDB(String home, String out) {
		tacocoHome = home;
		outdir = out;	
	}

	public static void main(String[] args) throws Exception {
		String tacocoHome =  readOptionalArgumentValue(HOME,System.getProperty("user.dir"));
		String dbFile = readOptionalArgumentValue(OUTDIR, tacocoHome+"/tacoco_output") + "/tacoco.db";
		
		String sut = readArgumentValue(SUT);
		String exec = readArgumentValue(EXEC);
		
		dump(dbFile, sut, exec);

	}

	public static void dump(String dbFile, String sut, String exec) throws Exception{
		File projectRoot = new File(sut);
		File execFile = new File(exec);
		final FileInputStream in = new FileInputStream(execFile);
		final ExecutionDataReader reader = new ExecutionDataReader(in);

		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(sut);
		final String projectFQN = probe.getId();
		final DBUtil db = DBUtil.getInstance(dbFile);
		db.prepareDBFor(projectFQN, probe.getBuilderType().toString());
		DBDumper dumper = createDBDumper(db);
		final DataParser parser = new DataParser(projectRoot, dumper, db.getProjectID(projectFQN));
		

		reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
			public void visitSessionInfo(final SessionInfo info) {
				String nextSessionName = info.getId();
				parser.resetExecDataStore(nextSessionName);
				System.out.println("\n" + nextSessionName);
				boolean pass = nextSessionName.endsWith("_F")?false:true;
				String FQN = pass?nextSessionName:nextSessionName.replace("_F","");
				db.insertTest(FQN,pass,db.getProjectID(projectFQN));
			}
		});

		reader.setExecutionDataVisitor(parser);
		db.setAutoCommit(false);
		while(reader.read()) {};
		db.setAutoCommit(true);
		parser.resetExecDataStore(parser.getCoverageTitle());
		parser.forcePrintEnd();

		in.close();
		parser.close();
		db.close();
	}
}
