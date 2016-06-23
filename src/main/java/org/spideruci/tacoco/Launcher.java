package org.spideruci.tacoco;

import static org.spideruci.tacoco.AnalysisOptions.CP_ARG;
import static org.spideruci.tacoco.cli.AbstractCli.ANALYZER_OPTS;
import static org.spideruci.tacoco.cli.AbstractCli.HELP;
import static org.spideruci.tacoco.cli.AbstractCli.HOME;
import static org.spideruci.tacoco.cli.AbstractCli.LANUCHER_CLI;
import static org.spideruci.tacoco.cli.AbstractCli.LOG;
import static org.spideruci.tacoco.cli.AbstractCli.OUTDIR;
import static org.spideruci.tacoco.cli.AbstractCli.PROJECT;
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
import org.spideruci.tacoco.probe.AbstractBuildProbe;
import org.spideruci.tacoco.testrunners.AbstractTestRunner;
import org.spideruci.tacoco.testrunners.JUnitRunner;
import org.spideruci.tacoco.testrunners.TestNGRunner;
import org.spideruci.tacoco.util.PathBuilder;


public class Launcher {

	private final String tacocoHome;
	private final String sutHome;
	private AbstractBuildProbe probe;

	private Launcher(String tacocoHome, String sutHome){
		this.tacocoHome = tacocoHome;
		this.sutHome = sutHome;
		this.probe = AbstractBuildProbe.getInstance(this.sutHome);
	}

	public Launcher(String sutHome){
		this.tacocoHome = readArgumentValue(HOME);
		this.sutHome = sutHome;
		this.probe = AbstractBuildProbe.getInstance(this.sutHome);
	}

	public static void main(String[] args) throws Exception{

		if(System.getProperties().containsKey(HELP)) {
			LANUCHER_CLI.printHelp();
		}

		final String userDir = System.getProperty("user.dir");
		System.setProperty("maven.multiModuleProjectDirectory", userDir); // how does this help us?

		String sutHome = readArgumentValue(SUT);
		if(sutHome.endsWith(File.separator)) {
			sutHome = sutHome.substring(0, sutHome.length());
		}

		String tacocoHome = readOptionalArgumentValue(HOME, userDir);
		Launcher launcher = new Launcher(tacocoHome, sutHome);

		AbstractBuildProbe probe = AbstractBuildProbe.getInstance(launcher.sutHome);
		String projectName = readOptionalArgumentValue(PROJECT, probe.getId());
		String classpath = probe.getClasspath() + File.pathSeparator + launcher.getTacocoClasspath();

		final String defaultOutputPath =  
				PathBuilder.dirs(tacocoHome, "tacoco_output").buildFilePath();
		String outdir = readOptionalArgumentValue(OUTDIR, defaultOutputPath);
		launcher.startAnalysis(projectName, classpath, launcher.sutHome, new String[0], outdir);
	}

	public boolean startAnalysis(String projectName, String outDir){
		String classpath="";
		try {
			classpath = probe.getClasspath() + File.pathSeparator + getTacocoClasspath();
		} catch (Exception e) {
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
	public void startAnalysis(String projectName, String classpath, String sutHome, String[] jvmArgs, String outdir) {

		if(!new File(outdir).exists()) {
			new File(outdir).mkdirs();
		}

		List<String> command = new ArrayList<>();
		command.add("java");

		String analyzerOptsFilePath = readArgumentValue(ANALYZER_OPTS);
		File analyzerOptsFile = new File(analyzerOptsFilePath);
		if(analyzerOptsFile == null 
				|| !analyzerOptsFile.exists()
				|| analyzerOptsFile.isDirectory()
				|| !analyzerOptsFile.isFile()) {
			throw new RuntimeException("Analyzer Options File is missing or "
					+ "is not a file -- " + analyzerOptsFilePath);
		}

		ArrayList<String> javaOptions = AnalysisOptions.readOptions(analyzerOptsFile);

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

		for(String jvmArg : jvmArgs) {
			if(jvmArg == null || jvmArg.isEmpty() || !jvmArg.startsWith("-")) {
				continue;
			}
			command.add(jvmArg);
		}

		command.add("org.spideruci.tacoco.analysis.AbstractAnalyzer");

		System.out.println(command.toString());

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(sutHome));

		builder.inheritIO();

		@SuppressWarnings("unused") File err = getFile(outdir, projectName+".err");
		@SuppressWarnings("unused") File log = getFile(outdir, projectName+".log");

		final Process p;
		try {
			p = builder.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					p.destroy();
				}
			}); 
			p.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private String getTacocoClasspath() throws Exception {
		final String tacocoCpPath = 
				new PathBuilder().path(tacocoHome, "cp.txt").buildFilePath();
		if(!new File(tacocoCpPath).exists()) {
			MavenCli mavenCli = new MavenCli();
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

		String tacocoClasspath = 
				new PathBuilder()
				.path(cpDependencies, tacocoTargetPath)
				.buildClassPath();
		return tacocoClasspath;
	}

	private static File getFile(String dir, String name) {
		File file = new File(dir, name);

		if(file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch(IOException e) {
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
