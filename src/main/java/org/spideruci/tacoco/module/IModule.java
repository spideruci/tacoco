package org.spideruci.tacoco.module;

import java.util.List;

/**
 * Created by Tariq on 1/7/2016.
 */
public interface IModule {


    List<String> getClasses();

    List<String> getTestClasses();

    String getClasspath();

    String getClassDir();

    String getTestclassDir();





}
