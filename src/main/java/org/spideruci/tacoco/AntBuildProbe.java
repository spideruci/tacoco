package org.spideruci.tacoco;

import java.util.ArrayList;
import java.util.List;

public class AntBuildProbe extends AbstractBuildProbe {

	String targetPath;
	
	public AntBuildProbe(String absolutTargetPath) {
		// TODO Auto-generated constructor stub
		this.targetPath = absolutTargetPath;
	}

	@Override
	public ArrayList<String> getTestClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuilderType getBuilderType() {
		// TODO Auto-generated method stub
		return BuilderType.ANT;
	}

	@Override
	public String getClasspath() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

}
