package org.spideruci.tacoco.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tariq.ibrahim on 10-11-2015.
 */
public class PathBuilder {

	private List<String> paths;
	
	public static PathBuilder dirs(final String ... directories) {
		PathBuilder builder = new PathBuilder();
		builder.path(directories);
		return builder;
	}

	public PathBuilder() {
		paths = new ArrayList<>();
	}

	public PathBuilder path(final String directory) {
		if(directory != null) {
			paths.add(directory);
		}
		
		return this;
	}
	
	public PathBuilder path(final String ... directories) {
		for(String directory : directories) {
			this.path(directory);
		}
		return this;
	}

	public PathBuilder path(final List<String> directories) {
		for(String directory : directories) {
			this.path(directory);
		}		
		return this;
	}

	public String buildFilePath() {
		return buildPath(File.separator);
	}

	public String buildClassPath() {
		return buildPath(File.pathSeparator);
	}

	private String buildPath(final String delimiter) {
		if(paths.isEmpty()) {
			return "";
		}

		StringBuffer path = new StringBuffer();
		for (int i = 0; i < paths.size() - 1; i++) {
			path.append(paths.get(i));
			path.append(delimiter);
		}

		path.append(paths.get(paths.size() - 1));
		return path.toString();
	}

}
