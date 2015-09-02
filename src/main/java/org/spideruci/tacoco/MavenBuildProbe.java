package org.spideruci.tacoco;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class MavenBuildProbe extends AbstractBuildProbe {
	private String classpath=null;
	private String targetDir;
	public MavenBuildProbe(String absolutTargetPath) {
		this.targetDir = absolutTargetPath;
	}

	@Override
	public ArrayList<String> getClasses() {
		final ArrayList<String> ret = new ArrayList<String>();

		try {

			//parse pom.xml file to decide which test case is executed
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(new FileInputStream(new File("pom.xml")));
			//System.out.println(model.getProperties());


			final String p = targetDir+"/target/test-classes"; //MAVEN TEST CLASS FOLDER
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
	public String getBuilderName() {
		return "MAVEN";
	}

	@Override
	public String getClasspath() throws Exception{
		if(classpath != null) return classpath;
		if(!new File(targetDir+"/cp.txt").exists()) {
			ProcessBuilder builder = new ProcessBuilder(
					"/usr/local/bin/mvn","dependency:build-classpath","-Dmdep.outputFile=cp.txt").inheritIO();
			builder.directory(new File(targetDir));
			Process p = builder.start();
			p.waitFor();
		}
		classpath = new String(Files.readAllBytes(Paths.get("cp.txt")))
				+":"+ targetDir + "/target/classes"
				+":"+ targetDir + "/target/test-classes";
		return classpath;
	}
}
