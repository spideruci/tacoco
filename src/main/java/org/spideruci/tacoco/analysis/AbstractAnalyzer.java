package org.spideruci.tacoco.analysis;

import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;

import org.spideruci.tacoco.AbstractBuildProbe;

/**
 * 
 * @author vpalepu
 *
 */
public abstract class AbstractAnalyzer {
	
	protected AbstractBuildProbe buildProbe;
	
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
	public abstract void setup();
	
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

}
