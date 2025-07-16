package org.spideruci.tacoco;

import static org.spideruci.tacoco.AnalysisOptions.CP_ARG;
import static org.spideruci.tacoco.AnalysisOptions.AGENT_ARGS;
import static org.spideruci.tacoco.cli.AbstractCli.ANALYZER_OPTS;
import static org.spideruci.tacoco.cli.AbstractCli.HELP;
import static org.spideruci.tacoco.cli.AbstractCli.HOME;
import static org.spideruci.tacoco.cli.AbstractCli.LAUNCHER_CLI;
import static org.spideruci.tacoco.cli.AbstractCli.LOG;
import static org.spideruci.tacoco.cli.AbstractCli.OUTDIR;
import static org.spideruci.tacoco.cli.AbstractCli.PROJECT;
import static org.spideruci.tacoco.cli.AbstractCli.DEBUG;
import static org.spideruci.tacoco.cli.AbstractCli.SUT;
import static org.spideruci.tacoco.cli.AbstractCli.arg;
import static org.spideruci.tacoco.cli.AbstractCli.argEquals;
import static org.spideruci.tacoco.cli.AbstractCli.readBooleanArgument;
import static org.spideruci.tacoco.cli.LauncherCli.readArgumentValue;
import static org.spideruci.tacoco.cli.LauncherCli.readOptionalArgumentValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.cli.MavenCli;
import org.spideruci.tacoco.analysis.InstrumenterConfig;
import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.spideruci.tacoco.testrunners.AbstractTestRunner;
import org.spideruci.tacoco.testrunners.JUnitRunner;
import org.spideruci.tacoco.testrunners.TestNGRunner;
import org.spideruci.tacoco.util.PathBuilder;


public class Launcher {

	private final String tacocoHome;
	private final String sutHome;
	private final AbstractBuildProbe probe;

	private Launcher(final String tacocoHome, final String sutHome){
		this.tacocoHome = tacocoHome;
		this.sutHome = sutHome;
		this.probe = AbstractBuildProbe.getInstance(this.sutHome);
	}

	public Launcher(final String sutHome){
		this.tacocoHome = readArgumentValue(HOME);
		this.sutHome = sutHome;
		this.probe = AbstractBuildProbe.getInstance(this.sutHome);
	}

	public static void main(final String[] args) throws Exception{

		if(System.getProperties().containsKey(HELP)) {
			LAUNCHER_CLI.printHelp();
		}

		final String userDir = System.getProperty("user.dir");
		System.setProperty("maven.multiModuleProjectDirectory", userDir); // how does this help us?

		String sutHome = readArgumentValue(SUT);
		if(sutHome.endsWith(File.separator)) {
			sutHome = sutHome.substring(0, sutHome.length());
		}

		final String tacocoHome = readOptionalArgumentValue(HOME, userDir);
		final Launcher launcher = new Launcher(tacocoHome, sutHome);

		final AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.sutHome);
		final String projectName = readOptionalArgumentValue(PROJECT, probe.getId());
		final String classpath = probe.getClasspath() + File.pathSeparator + launcher.getTacocoClasspath();

		final String defaultOutputPath =  
				PathBuilder.dirs(tacocoHome, "tacoco_output").buildFilePath();
		final String outdir = readOptionalArgumentValue(OUTDIR, defaultOutputPath);
		launcher.startAnalysis(projectName, classpath, launcher.sutHome, new String[0], outdir);
	}

	public boolean startAnalysis(final String projectName, final String outDir){
		String classpath="";
		try {
			classpath = probe.getClasspath() + File.pathSeparator + getTacocoClasspath();
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		startAnalysis(projectName, classpath, sutHome, new String[0], outDir);
		return true;
	}

	/**

	 * @param projectName
	 * @param classpath
	 * @param sutHome
	 * @param jvmArgs
	 */
	public void startAnalysis(final String projectName, String classpath, final String sutHome, final String[] jvmArgs, final String outdir) {

		if(!new File(outdir).exists()) {
			new File(outdir).mkdirs();
		}

		final List<String> command = new ArrayList<>();
		command.add("java");

		// Added an option to stop let the process wait for a debugger to attach.
		if (System.getProperty(DEBUG) != null) {
			command.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000");
		}

		final String analyzerOptsFilePath = readArgumentValue(ANALYZER_OPTS);
		final File analyzerOptsFile = new File(analyzerOptsFilePath);
		if(analyzerOptsFile == null 
				|| !analyzerOptsFile.exists()
				|| analyzerOptsFile.isDirectory()
				|| !analyzerOptsFile.isFile()) {
			throw new RuntimeException("Analyzer Options File is missing or "
					+ "is not a file -- " + analyzerOptsFilePath);
		}

		final ArrayList<String> javaOptions = AnalysisOptions.readOptions(analyzerOptsFile);

		for(String option : javaOptions) {
			if(option.contains("$TACOCO_HOME$")) {
				option = option.replace("$TACOCO_HOME$", tacocoHome);
			}
			if(option.contains("$OUTDIR$")) {
				option = option.replace("$OUTDIR$", outdir);
			}
			if(option.contains("$PROJECT_NAME$")) {
				option = option.replace("$PROJECT_NAME$", projectName);
			}
			System.out.println(option);

			if(option.startsWith(CP_ARG)) {
				option = option.substring(CP_ARG.length());

				if(!option.isEmpty()) {
					classpath += ":" + option;
				}
			} else if (option.startsWith(InstrumenterConfig.JAVAAGENT)) {
				// 
				final String javaagentArgs = readArgumentValue(AGENT_ARGS);
				if (javaagentArgs != null && !javaagentArgs.isEmpty()) {
					final String javaagent = option + "=" + javaagentArgs;
					command.add(javaagent);
				} else {
					command.add(option);
				}
			} else {
				command.add(option);
			}
		}

		command.add("-cp");
		command.add(classpath);
		command.add(argEquals(SUT, sutHome));
		command.add(argEquals(OUTDIR, outdir));
		command.add(argEquals(PROJECT, projectName));
		if(readBooleanArgument(LOG)) {
			command.add(arg(LOG));
		}

		for(final String jvmArg : jvmArgs) {
			if(jvmArg == null || jvmArg.isEmpty() || !jvmArg.startsWith("-")) {
				continue;
			}
			command.add(jvmArg);
		}

		command.add("org.spideruci.tacoco.analysis.AbstractAnalyzer");

		StringBuilder cmdStringBuilder = new StringBuilder();
		for (String cmd : command) {
			cmdStringBuilder.append(" ").append(cmd);
		}

		System.out.println(String.format("[RUN-COMMAND] %s", cmdStringBuilder));

		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(sutHome));

		builder.inheritIO();

		@SuppressWarnings("unused")
		final File err = getFile(outdir, projectName+".err");
		@SuppressWarnings("unused")
		final File log = getFile(outdir, projectName+".log");

		final Process p;
		try {
			p = builder.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					p.destroy();
				}
			}); 
			p.waitFor();
		}catch(final Exception e){
			e.printStackTrace();
		}
	}

	private String getTacocoClasspath() throws Exception {
		final String tacocoCpPath = 
				new PathBuilder().path(tacocoHome, "cp.txt").buildFilePath();
		if(!new File(tacocoCpPath).exists()) {
			final MavenCli mavenCli = new MavenCli();
			mavenCli.doMain(
					new String[] {"dependency:build-classpath", "-Dmdep.outputFile=cp.txt"}, 
					tacocoHome,
					System.out, 
					System.out);
		}

		final String cpDependencies = new String(Files.readAllBytes(Paths.get(tacocoHome+ File.separator +"cp.txt")));
		final String tacocoTargetPath = 
				new PathBuilder()
				.path(tacocoHome, "target", "classes")
				.buildFilePath();

		final String tacocoClasspath = 
				new PathBuilder()
				.path(cpDependencies, tacocoTargetPath)
				.buildClassPath();
		return tacocoClasspath;
	}

	private static File getFile(final String dir, final String name) {
		File file = new File(dir, name);

		if(file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch(final IOException e) {
			file = null;
		}

		return file;
	}


	public String getTestSystem(){

		System.out.println(AbstractTestRunner.getInstance(this.probe));

		if(AbstractTestRunner.getInstance(this.probe) instanceof JUnitRunner) return "JUNIT";
		else if(AbstractTestRunner.getInstance(this.probe) instanceof TestNGRunner) return "TESTNG";

		return "NOTESTS";
	}

}
