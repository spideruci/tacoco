package org.spideruci.tacoco.reporting.misc;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;

public class PerJUnitTestCaseVisitor implements ISessionInfoVisitor,IExecutionDataVisitor
{
	private ExecutionDataStore executionDataStore;
	private SessionInfo currentInfo;
	private File classesDirectory;
	private boolean firstRun = true;

	private Map<SessionInfo, Collection<ISourceFileCoverage>> results;

	private String classesToAnalyze;


	public PerJUnitTestCaseVisitor(File classesDirectory, String classesToAnalyze)
	{
		executionDataStore = new ExecutionDataStore();
		this.classesDirectory = classesDirectory;
		this.classesToAnalyze = classesToAnalyze;
		results = new HashMap<SessionInfo, Collection<ISourceFileCoverage>>();
	}

	public void visitClassExecution(ExecutionData executionData)
	{
		executionDataStore.put(executionData);
	}

	public void visitSessionInfo(SessionInfo info)
	{
		if(!firstRun)
		{
			this.analyzeCurrentDataStore();
		}
		else
		{
			firstRun = false;
		}
		this.currentInfo = info;
		executionDataStore.reset();
	}

	//public Collection<ISourceFileCoverage> analyzeCurrentDataStore()
	private void analyzeCurrentDataStore()
	{
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);

		try
		{
			analyzer.analyzeAll(classesToAnalyze, classesDirectory);
		}
		catch(IOException e)
		{

		}

		/*
		System.out.printf("Session \"%s\": %s - %s%n", currentInfo.getId(),
				new Date(currentInfo.getStartTimeStamp()),
				new Date(currentInfo.getDumpTimeStamp()));
				*/

		Collection<ISourceFileCoverage> sourceFiles = coverageBuilder.getSourceFiles();	

		results.put(currentInfo, sourceFiles);
		
		for(ISourceFileCoverage sourceFile : sourceFiles)
		{
			ExecAnalyze.print_counters(sourceFile);
			
			ExecAnalyze.print_source_counter_verbose(sourceFile);
		}
		
	}


	public void close()
	{
		this.analyzeCurrentDataStore();
	}

	public Map<SessionInfo, Collection<ISourceFileCoverage>> getResults()
	{
		return results;
	}
}
