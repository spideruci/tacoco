package org.spideruci.tacoco.analysis;

import static org.spideruci.tacoco.cli.AbstractCli.PIT_NO_UNIT_TEST;

import java.io.File;

import org.spideruci.tacoco.mutation.PITHandler;

public class PITAnalyzer extends TacocoAnalyzer {
	
	private boolean runTacoco = true;
	private PITHandler pit;
	
	@Override
	public void setup() {
		super.setup();
		if(System.getProperty(PIT_NO_UNIT_TEST)!=null){
			File dbFile = new File(this.dbFileName);
			if(dbFile.exists()) this.runTacoco = false;
		}
		this.pit = new PITHandler(this.buildProbe, this.outDir, this.name, this.sutHome);
	}
	
	@Override
	public void analyze() {
		if(this.runTacoco)super.analyze();
		this.pit.runPit();
	}
	
	@Override
	public void printAnalysisSummary() {
		if(this.runTacoco)super.printAnalysisSummary();
		this.pit.updateTacocoDB();
	}

	@Override
	public String getName() {
		return "PIT";
	}

}
