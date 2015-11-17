package org.spideruci.tacoco.probe;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.spideruci.tacoco.util.PathBuilder;

public class MavenBuildProbe extends AbstractBuildProbe {

	private String targetDir;
	private List<MavenModule> submodules;

	public MavenBuildProbe(String absolutTargetPath) {
		this.targetDir = absolutTargetPath;
		this.submodules = new ArrayList<>();
		
		this.submodules.add(new MavenModule(this.targetDir));
		
		for(String module: getModel().getModules()){
			if(module.endsWith(".xml")) continue;
			String childDir = new PathBuilder().path(this.targetDir).path(module).buildFilePath();
			this.submodules.add(new MavenModule(childDir));
		}
	}

	@Override
	public List<String> getTestClasses() {
		List<String> ret = new ArrayList<>();
		for(MavenModule m: this.submodules){
			ret.addAll(m.getTestClasses());
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

	@Override
	public BuilderType getBuilderType() {
		return BuilderType.MAVEN;
	}

	@Override
	public String getClasspath(){
		StringBuilder sb = new StringBuilder();
		for(MavenModule m: this.submodules){
			sb.append(m.getClasspath()+":");
		}
		return sb.toString();
	}

	@Override
	public boolean hasChild() {
		return !getModel().getModules().isEmpty();
	}

	@Override
	public List<Child> getChildren() {
		List<Child> list = new ArrayList<>();
		for(String module: getModel().getModules()){
			if(module.endsWith(".xml")) continue;
			String childDir = new PathBuilder().path(targetDir).path(module).buildFilePath();
			MavenBuildProbe p = new MavenBuildProbe(childDir);
			list.add(new Child(p.getId(), p.getClasspath(), childDir, null));
		}
		return list;
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
		
		List<String> ret = new ArrayList<>();
		for(MavenModule m: this.submodules){
			ret.addAll(m.getClasses());
		}
		return ret;
	}
}
