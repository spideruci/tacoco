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
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class MavenBuildProbe extends AbstractBuildProbe {
	private String classpath=null;
	private String targetDir;
	private String[] modules = {};

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
			if(!new File(p).exists()) return ret;
			Files.walkFileTree(Paths.get(p), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String str = file.toString();
					//
					if(str.endsWith(".class") && !str.matches("(.*)\\$(.*)") && accept(file)) {
						//if(str.matches(".*CharMatcher.*")) System.out.println(str.replaceAll(p.endsWith("/")?p:p+"/","").replace('/','.').replaceAll("\\.class",""));
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

	private Model getModel(){
		Model model = null;
		try{
			MavenXpp3Reader reader = new MavenXpp3Reader();
			model = reader.read(new FileInputStream(new File(targetDir,"pom.xml")));
		}catch(Exception e){
			e.printStackTrace();
		}
		return model;
	}

	private void makeFilter(){
		Xpp3Dom dom=null;
		includes = new ArrayList<>();
		excludes = new ArrayList<>();

		if(getModel().getBuild() != null){
			for(Plugin p : getModel().getBuild().getPlugins()){
				if(p.getKey().equals("org.apache.maven.plugins:maven-surefire-plugin")){
					dom = (Xpp3Dom) p.getConfiguration();
				}
			}
		}
		
		Xpp3Dom node = null;
		if(dom !=null) node = dom.getChild("includes");
		if(node != null){
			for(Xpp3Dom n : node.getChildren("include"))
				includes.add(n.getValue().replace("**/", "").replace("*", "").replaceAll("\\.java", ""));
		}
		if(dom !=null) node = dom.getChild("excludes");
		if(node != null){
			for(Xpp3Dom n : node.getChildren("exclude"))
				excludes.add(n.getValue().replace("**/", "").replace("*", "").replaceAll("\\.java", ""));
		}
		//add default filter
		if(includes.size()==0) includes.add("Test");
		
		System.out.println("----------------Filters");
		System.out.println("----------------includes"+includes);
		System.out.println("----------------excludes"+excludes);
	}

	private boolean accept(Path file) {

		if(includes.size() == 0 && excludes.size() ==0) return true;

		for(String in : includes){
			if(file.toString().matches(".*"+in+".class")) return true;
		}
		for(String ex : excludes){
			if(file.toString().matches(".*"+ex+".class")) return false;
		}
		return false;
	}

	@Override
	public BuilderType getBuilderType() {
		return BuilderType.MAVEN;
	}

	@Override
	public String getClasspath(){
		try{
		if(classpath != null) return classpath;
		if(!new File(targetDir+"/tacoco.cp").exists()) {
			ProcessBuilder builder = new ProcessBuilder(
					"mvn","dependency:build-classpath","-Dmdep.outputFile=tacoco.cp").inheritIO();
			builder.directory(new File(targetDir));
			Process p = builder.start();
			p.waitFor();
		}
		classpath = new String(Files.readAllBytes(Paths.get(targetDir,"tacoco.cp")))
				+":"+ targetDir + "/target/test-classes"
				+":"+ targetDir + "/target/classes";
		}catch(Exception e){
			e.printStackTrace();
		}
		return classpath;
	}
	
	@Override
	public boolean hasChild() {
		return !getModel().getModules().isEmpty();
	}

	@Override
	public List<Child> getChildren() {
		List<Child> list = new ArrayList<>();
		for(String module: getModel().getModules()){
			if(moduleSharesParentTarget(module)) continue;
			String childDir=targetDir+"/"+module;
			MavenBuildProbe p = new MavenBuildProbe(childDir);
			list.add(new Child(p.getId(), p.getClasspath(), childDir, null));
		}
		return list;
	}
	
	private boolean moduleSharesParentTarget(String module) {
	  return module.endsWith(".xml");
	}

	@Override
	public String getId() {
		String id = getModel().getName();
		String group = getModel().getGroupId();
		if(group != null) id = group + "." + id;
		return id;
	}
}
