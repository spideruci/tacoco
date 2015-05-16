package org.spideruci.tacoco.reporting.misc;
import java.util.*;
import java.io.*;

import org.jacoco.core.*;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.*;

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
