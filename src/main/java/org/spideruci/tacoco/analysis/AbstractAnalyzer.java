package org.spideruci.tacoco.analysis;

import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.AbstractCli.ANALYZER;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;

import org.spideruci.tacoco.AbstractBuildProbe;

/**
 * 
 * @author vpalepu
 *
 */
public abstract class AbstractAnalyzer {
	
	protected AbstractBuildProbe buildProbe;
	protected AnalysisResults result;
	
	public void setBuildProbe() {
		String targetDir = readArgumentValue(SUT);
		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(targetDir);
		this.setBuildProbe(probe);
	}
	
	public void setBuildProbe(AbstractBuildProbe buildProbe) {
		if(buildProbe == null) {
			throw new NullPointerException("buildProbe is null.");
		}
		
		this.buildProbe = buildProbe;
	}
	
	/**
	 * This method is responsible for setting up the initial context of the 
	 * analyzer, such as the build-probe, test-runner (in case it is a run-time
	 * analyzer), etc. The {@link #analyze()} method will assume that all necessary 
	 * setup is completed (ideally in this method).
	 */
	public void setup() {
		this.setBuildProbe();
	}
	
	/**
	 * This method is responsible for implementing the actual analysis associated
	 * with this analyzer. Chances are that this analysis will use the 
	 * {@linkplain org.spideruci.tacoco.testrunners test-runners}
	 * in case this turns out to be a 
	 * {@linkplain org.spideruci.tacoco.analysis.AbstractRuntimeAnalyzer run-time-analysis}.
	 */
	public abstract void analyze();
	
	/**
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * 
	 */
	public abstract void printAnalysisSummary();
	
	public static AbstractAnalyzer getInstance() {
		String analyzerClassString = readArgumentValue(ANALYZER);
		try {
			Class<?> analyzerClass = Class.forName(analyzerClassString);
			AbstractAnalyzer analyzer = (AbstractAnalyzer) analyzerClass.newInstance();
			return analyzer;
		} catch (InstantiationException
				| IllegalAccessException 
				| ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This is the main command line entry point for the execution of a desired
	 * analysis. 
	 * @param args
	 */
	public static void main(String[] args) {
		AbstractAnalyzer analyzer = AbstractAnalyzer.getInstance();
		analyzer.setup();
		analyzer.analyze();
		analyzer.printAnalysisSummary();
	}

}
