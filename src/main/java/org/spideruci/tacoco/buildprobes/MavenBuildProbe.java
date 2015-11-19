package org.spideruci.tacoco.buildprobes;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.spideruci.tacoco.util.PathBuilder;

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
		List<String> ret = new ArrayList<>();
		final String testClassPath = new PathBuilder().path(targetDir).path("target").path("test-classes").buildFilePath(); //MAVEN TEST CLASS FOLDER
		if(!new File(testClassPath).exists()) {
			return ret;
		}
		scanner.setBasedir(testClassPath);
		scanner.setCaseSensitive(true);
		scanner.scan();

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
			final String tacocoCpPath = 
					new PathBuilder().path(targetDir).path("tacoco.cp").buildFilePath();

			if(!new File(tacocoCpPath).exists()) {
				MavenCli mavenCli = new MavenCli();
				mavenCli.doMain(new String[]{"dependency:build-classpath", "-Dmdep.outputFile=tacoco.cp"}, targetDir,
						System.out, System.out);
			}

			final String tacocoDependencies = new String(Files.readAllBytes(Paths.get(targetDir, "tacoco.cp")));
			final String targetPath = new PathBuilder().path(targetDir).path("target").path("classes").buildFilePath();
			final String targetTestPath = new PathBuilder().path(targetDir).path("target").path("test-classes").buildFilePath();

			classpath = new PathBuilder().path(tacocoDependencies)
					.path(targetPath)
					.path(targetTestPath)
					.buildClassPath();

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
			String childDir = new PathBuilder().path(targetDir).path(module).buildFilePath();
			MavenBuildProbe p = new MavenBuildProbe(childDir);
			Child child = new Child(
					p.getId(), /*name*/ 
					p.getClasspath(), /*classpath*/ 
					childDir, /*directory*/
					null); /*jvmArgs*/
			
			list.add(child);
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
		String baseDir = targetDir+"/target/classes";
		
		List<String> ret = new ArrayList<>();
		if(!new File(baseDir).exists()) {
			return ret;
		}
		classScanner.setBasedir(baseDir);
		classScanner.setCaseSensitive(true);
		classScanner.setIncludes(new String[]{"**/*class"});
		classScanner.setExcludes(new String[]{"**/*$*.class"});
		classScanner.scan();
		
		for(String s: classScanner.getIncludedFiles()){
			ret.add(s.replaceAll("/", ".").replaceAll("\\.class",""));
		}
		return ret;
	}
}
