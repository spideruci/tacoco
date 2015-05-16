package org.spideruci.tacoco.reporting.misc;
import java.io.*;
import java.util.*;

import org.jacoco.core.*;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.*;

public class ExecAnalyze 
{
	private final File executionDataFile;
	private final File classesDirectory;
	private String title;
	//private final File sourceDirectory;
	//private final File reportDirectory;

	private ExecutionDataStore executionDataStore;
	private SessionInfoStore sessionInfoStore;

	private PerJUnitTestCaseVisitor myVisitor;

	private Map<SessionInfo, Collection<ISourceFileCoverage>> results;

	public ExecAnalyze(final File execFile, String classesToAnalyze)
	{
		this.executionDataFile = execFile;
		this.title = "Title1";
		this.classesDirectory = new File(execFile.getParent(),".");
		//this.sourceDirectory = new File(execFile.getParent(),"src");
		//this.reportDirectory = execFile;
		this.myVisitor = new PerJUnitTestCaseVisitor(this.classesDirectory, classesToAnalyze);

		this.results = new HashMap<SessionInfo, Collection<ISourceFileCoverage>>();
	}


//	public void create() throws IOException
//	{
//		loadExecutionData();
//		IBundleCoverage bundleCoverage = analyzeStructure();
//
//		Collection<IPackageCoverage> packages = bundleCoverage.getPackages();
//
//		//System.out.println("Package Count: "+packages.size());
//		
//
//		for(IPackageCoverage my_package : packages)
//		{
//			Collection<ISourceFileCoverage> sourceFiles = my_package.getSourceFiles();
//			for(ISourceFileCoverage sourceFile : sourceFiles)
//			{
//				//print_counters(sourceFile);
//				//print_source_counter(sourceFile);
//				
//				//print_source_counter_verbose(sourceFile);
//
//				
//			}
//		}
//
//
//		//print_counters(bundleCoverage);
//	}

	public static void print_source_counter_verbose(ISourceNode source)
	{
		int firstLine = source.getFirstLine();
		int lastLine = source.getLastLine();
		System.out.printf("%18s: %s\n", "First Line", firstLine);
		System.out.printf("%18s: %s\n", "Last Line", lastLine);
		for(int i = firstLine; i <= lastLine; i++)
		{
			ILine line = source.getLine(i);
			ICounter instructionCounter = line.getInstructionCounter();
			int status = instructionCounter.getStatus();
			if(status != ICounter.EMPTY)
			{
				String status_string = "";
				switch(status)
				{
					case ICounter.NOT_COVERED:
						status_string = "NOT_COVERED";
						break;
					case ICounter.FULLY_COVERED:
						status_string = "FULLY_COVERED";
						break;
					case ICounter.PARTLY_COVERED:
						status_string = "PARTLY_COVERED";
						break;
					default:
				}
				System.out.printf("%18s: %6s (%s)\n", "Line "+i, instructionCounter.getCoveredCount() + "/" + instructionCounter.getTotalCount(), status_string);
			}
		}
	}
	/*
	public static void print_source_counter(ISourceNode source)
	{
		int firstLine = source.getFirstLine();
		int lastLine = source.getLastLine();
		System.out.printf("%18s: %s\n", "First Line", firstLine);
		System.out.printf("%18s: %s\n", "Last Line", lastLine);
	}
	*/

	public static void print_counters(ICoverageNode node)
	{
		String name = node.getName();

		ICounter lineCounter = node.getLineCounter();
		ICounter instructionCounter = node.getInstructionCounter();
		ICounter classCounter = node.getClassCounter();
		ICounter methodCounter = node.getMethodCounter();
		ICounter branchCounter = node.getBranchCounter();
		ICounter complexityCounter = node.getComplexityCounter();

		System.out.printf("%18s: %s\n", "ICoverageNode name", name);
		System.out.printf("%18s: %s\n", "lineCounter", lineCounter.getCoveredCount() + "/" + lineCounter.getTotalCount());
		System.out.printf("%18s: %s\n", "instructionCounter", instructionCounter.getCoveredCount() + "/" + instructionCounter.getTotalCount());
		System.out.printf("%18s: %s\n", "classCounter", classCounter.getCoveredCount() + "/" + classCounter.getTotalCount());
		System.out.printf("%18s: %s\n", "methodCounter", methodCounter.getCoveredCount() + "/" + methodCounter.getTotalCount());
		System.out.printf("%18s: %s\n", "branchCounter", branchCounter.getCoveredCount() + "/" + branchCounter.getTotalCount());
		System.out.printf("%18s: %s\n", "complexityCounter", complexityCounter.getCoveredCount() + "/" + complexityCounter.getTotalCount());

	}


//	//private void loadExecutionData(final String executionFile) throws IOException
//	private void loadExecutionData() throws IOException
//	{
//		final FileInputStream fis = new FileInputStream(executionDataFile);
//
//		final ExecutionDataReader executionDataReader = new ExecutionDataReader(fis);
//
//		executionDataStore = new ExecutionDataStore();
//		sessionInfoStore = new SessionInfoStore();
//
//		executionDataReader.setSessionInfoVisitor(myVisitor);
//		executionDataReader.setExecutionDataVisitor(myVisitor);
//
//		while(executionDataReader.read())
//		{
//		}
//		//myVisitor.analyzeCurrentDataStore();
//
//		fis.close();
//	}


	public void analyze() throws IOException
	{
		final FileInputStream fis = new FileInputStream(executionDataFile);

		final ExecutionDataReader executionDataReader = new ExecutionDataReader(fis);

		executionDataReader.setSessionInfoVisitor(myVisitor);
		executionDataReader.setExecutionDataVisitor(myVisitor);

		while(executionDataReader.read())
		{
		}
		myVisitor.close();
		this.results = myVisitor.getResults();
		fis.close();
	}

	public void printResults()
	{
		for(SessionInfo currentInfo : results.keySet())
		{
			Collection<ISourceFileCoverage> sourceFiles = results.get(currentInfo);

			System.out.printf("Session \"%s\": %s - %s%n", currentInfo.getId(),
					new Date(currentInfo.getStartTimeStamp()),
					new Date(currentInfo.getDumpTimeStamp()));

			for(ISourceFileCoverage sourceFile : sourceFiles)
			{
				ExecAnalyze.print_counters(sourceFile);
				
				ExecAnalyze.print_source_counter_verbose(sourceFile);
			}
		}

	}

	/*
	private IBundleCoverage analyzeStructure() throws IOException
	{
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);

		analyzer.analyzeAll("ClassA.class:ClassATest.class",classesDirectory);

		return coverageBuilder.getBundle(title);
	}
	*/

	public static void main(String[] args) throws IOException
	{
		File file = new File("../bin/jacoco.exec");
		String classesToAnalyze = "ClassA.class:ClassATest.class";
		ExecAnalyze execAnalyze = new ExecAnalyze(file, classesToAnalyze);
		//execAnalyze.create();
		execAnalyze.analyze();
		execAnalyze.printResults();
	}
}
