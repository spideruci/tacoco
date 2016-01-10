package org.spideruci.tacoco.probe;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spideruci.tacoco.module.GradleModule;
import org.spideruci.tacoco.util.PathBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tariq on 1/9/2016.
 */
public class GradleModuleTest {

    public static GradleModule module;

    @BeforeClass
    public static void setUp() throws IOException {
        final String targetPath = new PathBuilder().path("resources").path("spiderMath_Gradle").path("addition").buildFilePath();
        GradleModuleTest.module = new GradleModule(targetPath);
    }

    @Test
    public void getTestClassesTest(){
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        set1.add("org.spideruci.benchmark.spiderMath.AdditionParamTest");
        set1.add("org.spideruci.benchmark.spiderMath.AdditionTest");
        set2.addAll(this.module.getTestClasses());
        assertEquals(set1, set2);
    }

    @Test
    public void getClassesTest(){
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        set1.add("org.spideruci.benchmark.spiderMath.Addition");
        set2.addAll(this.module.getClasses());
        assertEquals(set1, set2);
    }

    @Test
    public void getClasspathTest(){
        System.out.print(this.module.getClasspath());
    }
}
