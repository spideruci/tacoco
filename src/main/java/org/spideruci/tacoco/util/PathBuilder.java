package org.spideruci.tacoco.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tariq.ibrahim on 10-11-2015.
 */
public class PathBuilder {

    private List<String> paths;

    public PathBuilder path(final String directory) {
        if (paths == null) {
            paths = new ArrayList<>();
        }

        paths.add(directory);
        return this;
    }

    public PathBuilder path(final List<String> directories) {
        if (paths == null || paths.isEmpty()) {
            paths = directories;
        } else {
            paths.addAll(directories);
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
        StringBuffer path = new StringBuffer();
        for (int i = 0; i < paths.size(); i++) {
            if (i > 0)
                path.append(delimiter);
            path.append(paths.get(i));
        }
        return path.toString();
    }

}
