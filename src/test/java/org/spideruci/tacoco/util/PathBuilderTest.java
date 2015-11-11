package org.spideruci.tacoco.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tariq.ibrahim on 10-11-2015.
 */
public class PathBuilderTest {


    @Test
    public void test() {
        PathBuilder pathBuilder = new PathBuilder();
        pathBuilder.path("spider").path("uci").path("tacoco");
        assertEquals("spider" + File.separator +"uci" + File.separator + "tacoco", pathBuilder.buildFilePath());
        assertEquals("spider" + File.pathSeparator +"uci" + File.pathSeparator + "tacoco", pathBuilder.buildClassPath());
    }

    @Test
    public void testStringList() {
        PathBuilder pathBuilder = new PathBuilder();
        final List<String> directories = new ArrayList<>();
        directories.add("org");
        directories.add("spider");
        pathBuilder.path(directories).path("uci").path("tacoco");
        assertEquals("org" + File.separator +"spider" + File.separator +"uci" + File.separator + "tacoco", pathBuilder.buildFilePath());
        assertEquals("org" + File.pathSeparator +"spider" + File.pathSeparator +"uci" + File.pathSeparator + "tacoco", pathBuilder.buildClassPath());
    }

    @Test
    public void testStringList2() {
        PathBuilder pathBuilder = new PathBuilder();
        final List<String> directories = new ArrayList<>();
        directories.add("uci");
        directories.add("tacoco");
        pathBuilder.path("org").path("spider").path(directories);
        assertEquals("org" + File.separator +"spider" + File.separator +"uci" + File.separator + "tacoco", pathBuilder.buildFilePath());
        assertEquals("org" + File.pathSeparator +"spider" + File.pathSeparator +"uci" + File.pathSeparator + "tacoco", pathBuilder.buildClassPath());
    }
}
