package org.spideruci.tacoco.db;


import java.io.File;
import java.io.IOException;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.spideruci.tacoco.probe.AbstractBuildProbe;


public class DataParser implements IExecutionDataVisitor {

	private final DBDumper dbDumper;
	private String coverageTitle;
	private ExecutionDataStore execDataStore = new ExecutionDataStore();
	private int projectID;
	private boolean updateSourceInfo;
	private AbstractBuildProbe probe;
	
	public DataParser(final AbstractBuildProbe probe,
			final DBDumper dumper, int projectID) {
		this.coverageTitle = probe.getId();
		this.dbDumper = dumper;
		this.projectID = projectID;
		updateSourceInfo = true;
		this.probe = probe;
	}

	public void visitClassExecution(final ExecutionData data) {
		if(data == null) return;
		execDataStore.put(data);
	}

	public void resetExecDataStore(String nextSessionName) {
		if(execDataStore.getContents().size() == 0) {
			execDataStore = new ExecutionDataStore();
			this.setCoverageTitle(nextSessionName);
			return;
		}

		try {
			System.out.printf("analyzing exec-data for: %s%n", coverageTitle);
			IBundleCoverage coverage = this.analyzeStructure(execDataStore);
			printCoverage(coverage, updateSourceInfo);
			updateSourceInfo = false;
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setCoverageTitle(nextSessionName);
		execDataStore = new ExecutionDataStore();
	}

	private int count = 0; 
	private void printCoverage(IBundleCoverage coverage, boolean updateSourceIfno) {
		SQLitePrinter printer = 
				new SQLitePrinter(coverage, dbDumper.getDBUtil(),projectID, updateSourceInfo);
		

		printer.printCoverage();
		if(this.coverageTitle == null
				|| this.coverageTitle.isEmpty()
				|| this.coverageTitle.equals("end")) {
		} else {
		}
		System.out.printf("completed printing coverage bundle for %s.%n", coverage.getName());
		System.out.printf("completed printing %d coverage bundle(s).%n%n", ++count);
	}

	public void forcePrintEnd() {
	}

	public String getCoverageTitle() {
		return "title";
	}

	public void setCoverageTitle(final String title) {
		this.coverageTitle = title;
	}

	private IBundleCoverage analyzeStructure(final ExecutionDataStore data) 
			throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(data, coverageBuilder);
		
		for(String dir : this.probe.getClassDirs()){
			File classDir = new File(dir);
			if(classDir.exists()){
				analyzer.analyzeAll(classDir);
			}
		}
		
		return coverageBuilder.getBundle(coverageTitle);
	}

	@SuppressWarnings("unused")
	private int getHitCount(final boolean[] data) {
		int count = 0;
		for (final boolean hit : data) {
			if (hit) {
				count++;
			}
		}
		return count;
	}

	public void close() {
		
	}



}
