package org.spideruci.tacoco.module;

import org.codehaus.plexus.util.DirectoryScanner;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.ExternalDependency;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.spideruci.tacoco.util.PathBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tariq on 1/7/2016.
 */
public class GradleModule extends AbstractModule {

    public GradleModule(String targetDir){
        this.targetDir = targetDir;
    }

    @Override
    public List<String> getClasses() {
        DirectoryScanner classScanner = new DirectoryScanner();
        String baseDir = getClassDir();
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
            ret.add(s.replaceAll("/", ".").replaceAll("\\\\", ".").replaceAll("\\.class",""));
        }
        return ret;
    }

    @Override
    public List<String> getTestClasses() {
        List<String> ret = new ArrayList<>();
        DirectoryScanner scanner = new DirectoryScanner();
        String baseDir = getTestclassDir();
        if(!new File(baseDir).exists()) {
            return ret;
        }
        scanner.setBasedir(baseDir);
        scanner.setCaseSensitive(true);
        scanner.scan();

        for(String s: scanner.getIncludedFiles()){
            ret.add(s.replaceAll("/", ".").replaceAll("\\\\", ".").replaceAll("\\.class",""));
        }
        return ret;
    }

    @Override
    public String getClasspath() {
        if(this.classpath != null) return this.classpath;
        final String tacocoCpPath =
                new PathBuilder().path(this.targetDir).path("tacoco.cp").buildFilePath();

        if (!new File(tacocoCpPath).exists())
            generateGradleClasspath();
        try {
            final String tacocoDependencies = new String(Files.readAllBytes(Paths.get(this.targetDir, "tacoco.cp")));
            final String targetPath = getClassDir();
            final String targetTestPath = getTestclassDir();

            PathBuilder classpathBuilder = new PathBuilder();
            if (!tacocoDependencies.isEmpty()) {
                classpathBuilder.path(tacocoDependencies);
            }
            classpath = classpathBuilder
                    .path(targetPath)
                    .path(targetTestPath)
                    .buildClassPath();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return classpath;
    }

    private void generateGradleClasspath() {

            GradleConnector connector = GradleConnector.newConnector();
            File target = new File(targetDir);
            connector.forProjectDirectory(target);
            ProjectConnection connection = null;

            try {
                connection = connector.connect();

                EclipseProject eclipseProject = connection.getModel(EclipseProject.class);
                StringBuilder gradleClasspath = new StringBuilder();
                for (ExternalDependency externalDependency : eclipseProject.getClasspath()) {
                    if(gradleClasspath.toString().isEmpty()) {
                        gradleClasspath.append(externalDependency.getFile().getAbsolutePath());
                    } else {
                        gradleClasspath.append(File.pathSeparator + externalDependency.getFile().getAbsolutePath());
                    }
                }
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(new File(targetDir + File.separator + "tacoco.cp"));
                    pw.write(gradleClasspath.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                pw.close();


            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }

    @Override
    public String getTestclassDir() {
        return new PathBuilder().path(targetDir).
                path("build").
                path("classes").
                path("java").
                path("test").buildFilePath();
    }

    @Override
    public String getClassDir() {
        return new PathBuilder().path(targetDir).
                path("build").
                path("classes").
                path("java").
                path("main").buildFilePath();
    }

    @Override
    public int clean() {
        // TODO
        return 1;
    }

    @Override
    public int compile() {
        // TODO
        return 1;
    }

}
