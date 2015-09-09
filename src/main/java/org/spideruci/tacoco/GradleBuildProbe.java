package org.spideruci.tacoco;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class GradleBuildProbe extends AbstractBuildProbe {

	private String classpath=null;
	private String targetDir = null;
	
	public GradleBuildProbe(String absolutTargetPath) {
		targetDir = absolutTargetPath;
	}

	@Override
	public ArrayList<String> getClasses() {
		final ArrayList<String> ret = new ArrayList<>();

		try{
			if(!new File(targetDir+"/tacoco.testDir").exists()) runGradleTaskForTacoco();
			
			final String p = new String(Files.readAllBytes(Paths.get(targetDir+"/tacoco.testDir"))); //GRADLE TEST CLASS FOLDER
			Files.walkFileTree(Paths.get(p), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String str = file.toString();
					if(str.endsWith(".class") && !str.matches("(.*)\\$(.*)")) {
						//System.out.println(str.replaceAll(p.endsWith("/")?p:p+"/","").replace('/','.').replaceAll("\\.class","")); 
						ret.add(str.replaceAll(p.endsWith("/")?p:p+"/","").replace('/','.').replaceAll("\\.class",""));
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public BuilderType getBuilderType() {
		return BuilderType.GRADLE;
	}

	@Override
	public String getClasspath() {
		if(classpath != null) return classpath;

		try{
		if(!new File(targetDir+"/tacoco.cp").exists()) runGradleTaskForTacoco();
		classpath = new String(Files.readAllBytes(Paths.get(targetDir+"/tacoco.cp")));
		}catch(Exception e){
			e.printStackTrace();
		}
		return classpath;
	}

	private void runGradleTaskForTacoco() throws Exception{

		//inject tacoco task to build.gradle
		String task = "\ntask tacoco {\n"
				+"File td = new File('tacoco.testDir')\n"
				+"td<<sourceSets.test.output.classesDir\n"
				+"File cp = new File('tacoco.cp')\n"
				+"sourceSets.main.runtimeClasspath.each { cp<<it<<':'}\n"
				+"cp<<sourceSets.test.output.classesDir}";
				
		Files.write(Paths.get(targetDir+"/build.gradle"), task.getBytes(), StandardOpenOption.APPEND);	

		//run tacoco task
		ProcessBuilder builder = new ProcessBuilder(
				"./gradlew", "tacoco").inheritIO();
		builder.directory(new File(targetDir));
		Process p = builder.start();
		p.waitFor();	
	}

	@Override
	public boolean hasChild() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Child> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
}
