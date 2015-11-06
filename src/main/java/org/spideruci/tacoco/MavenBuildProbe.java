package org.spideruci.tacoco;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class MavenBuildProbe extends AbstractBuildProbe {
	private static final boolean String = false;
	private String classpath=null;
	private String targetDir;
	private String[] modules = {};

	//file filters from Maven surefire configuration
	DirectoryScanner scanner;

	public MavenBuildProbe(String absolutTargetPath) {
		this.targetDir = absolutTargetPath;
		scanner = new DirectoryScanner(); 
	}

	@Override
	public List<String> getTestClasses() {
		makeFilter();
		scanner.setBasedir(targetDir+"/target/test-classes"); //MAVEN TEST CLASS FOLDER
		scanner.setCaseSensitive(true);
		scanner.scan();
		
		List<String> ret = new ArrayList<>();
		for(String s: scanner.getIncludedFiles()){
			ret.add(s.replaceAll("/", ".").replaceAll("\\.class",""));
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

		if(getModel().getBuild() != null){
			for(Plugin p : getModel().getBuild().getPlugins()){
				if(p.getKey().equals("org.apache.maven.plugins:maven-surefire-plugin")){
					dom = (Xpp3Dom) p.getConfiguration();
				}
			}
		}

		Xpp3Dom node = null;
		List<String> includes = new ArrayList<>();
		List<String> excludes = new ArrayList<>();
		
		if(dom !=null) {
			node = dom.getChild("includes");
			if(node != null){
				for(Xpp3Dom n : node.getChildren("include"))
					includes.add(n.getValue().replaceAll("\\.java", "\\.class"));
			}
			node = dom.getChild("test");
			if(node != null){
				includes.add(node.getValue().replaceAll("\\.java", "\\.class"));
			}
			
			node = dom.getChild("excludes");
			if(node != null){
				for(Xpp3Dom n : node.getChildren("exclude"))
					excludes.add(n.getValue().replaceAll("\\.java", "\\.class"));
			}
		}
		//excludes inner classes
		excludes.add("**/*$*.class");
		
		if(includes.size() == 0) this.scanner.setIncludes(new String[]{"**/Test*.class","**/*Test.class","**/*TestCase.class"});
		else this.scanner.setIncludes(includes.toArray(new String[0]));
		this.scanner.setExcludes(excludes.toArray(new String[0]));
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
						"/usr/bin/mvn","dependency:build-classpath","-Dmdep.outputFile=tacoco.cp").inheritIO();
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
		String id = getModel().getArtifactId();
		String group = getModel().getGroupId();
		if(group != null) id = group + "." + id;
		return id;
	}

	@Override
	public List<java.lang.String> getClasses() {
		DirectoryScanner classScanner = new DirectoryScanner();
		classScanner.setBasedir(targetDir+"/target/classes");
		classScanner.setCaseSensitive(true);
		classScanner.setIncludes(new String[]{"**/*class"});
		classScanner.setExcludes(new String[]{"**/*$*.class"});
		classScanner.scan();
		
		List<String> ret = new ArrayList<>();
		for(String s: classScanner.getIncludedFiles()){
			ret.add(s.replaceAll("/", ".").replaceAll("\\.class",""));
		}
		return ret;
	}
}
