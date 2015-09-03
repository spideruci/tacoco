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
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class MavenBuildProbe extends AbstractBuildProbe {
	private String classpath=null;
	private String targetDir;
	
	//file filters from Maven surefire configuration
	ArrayList<String> includes;
	ArrayList<String> excludes;
	
	public MavenBuildProbe(String absolutTargetPath) {
		this.targetDir = absolutTargetPath;
	}

	@Override
	public ArrayList<String> getClasses() {
		final ArrayList<String> ret = new ArrayList<>();
		
		try{
			makeFilter();
			final String p = targetDir+"/target/test-classes"; //MAVEN TEST CLASS FOLDER
			Files.walkFileTree(Paths.get(p), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String str = file.toString();
					if(str.endsWith(".class") && !str.matches("(.*)\\$(.*)") && accept(file)) {
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

	private void makeFilter() throws Exception{
		Xpp3Dom dom=null;
		includes = new ArrayList<>();
		excludes = new ArrayList<>();
		
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileInputStream(new File("pom.xml")));
		for(Plugin p : model.getBuild().getPlugins()){
			if(p.getKey().equals("org.apache.maven.plugins:maven-surefire-plugin")){
				dom = (Xpp3Dom) p.getConfiguration();
			}
		}
		Xpp3Dom node = dom.getChild("includes");
		if(node != null){
			for(Xpp3Dom n : node.getChildren("include"))
				includes.add(n.getValue().replace("**/", "").replaceAll("\\.java", ""));
		}
		node = dom.getChild("excludes");
		if(node != null){
			for(Xpp3Dom n : node.getChildren("exclude"))
				excludes.add(n.getValue());
		}
	}
	
	private boolean accept(Path file) {
		
		if(includes.size() == 0 && excludes.size() ==0) return true;
		
		for(String in : includes){
			if(file.toString().matches(in)) return true;
		}
		for(String ex : excludes){
			if(file.toString().matches(ex)) return false;
		}
		return false;
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
