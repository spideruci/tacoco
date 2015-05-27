<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Developer Notes](#developer-notes)
  - [Old Suggested POM Changes](#old-suggested-pom-changes)
  - [Testing](#testing)
    - [Exploratory Testing Session for `tacoco/TacocoRunner`](#exploratory-testing-session-for-tacocotacocorunner)
      - [Preparing SourceSurfer (system-under-analysis)](#preparing-sourcesurfer-system-under-analysis)
      - [Preparing tacoco](#preparing-tacoco)
      - [Export SourceSurfer dependencies and classes to $CLASSPATH](#export-sourcesurfer-dependencies-and-classes-to-classpath)
      - [Export tacoco dependencies and classes to $CLASSPATH](#export-tacoco-dependencies-and-classes-to-classpath)
      - [Run SourceSurfer's Test Classes from within tacoco](#run-sourcesurfers-test-classes-from-within-tacoco)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Developer Notes

## Old Suggested POM Changes
This was useful back when we had to manually change the pom.xml of the system under test. This stopped being necessary ever since the Pull Request #11. This note is purely for archival purposes, in case we need to learn something from the past.

~~~xml
  <build>
    <plugins>
    ...
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.18.1</version>
        <configuration>
          <properties>
            <property>
              <name>listener</name>
              <value>org.spideruci.tacoco.TacocoListener</value>
            </property>
          </properties>
          <additionalClasspathElements>
            <additionalClasspathElement>tacoco/target/tacoco-0.1.jar</additionalClasspathElement>
          </additionalClasspathElements>
        </configuration>
      </plugin>
      <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.7.4.201502262128</version>
        <configuration>
          <destFile>jacoco.exec</destFile>
          <append>true</append>
        </configuration>
        <executions>
          <execution>
            <id>jacoco-initialize</id>
            <phase>initialize</phase>
            <goals>
              <goal>prepare-agent</goal>
            </goals>
          </execution>
          <execution>
            <id>jacoco-report</id>
            <phase>verify</phase>
            <goals>
              <goal>report</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    ...
~~~

## Testing

### Exploratory Testing Session for `tacoco/TacocoRunner`

**Ref.: Pull-Request #11**  
**System-under-analysis: SourceSurfer**  
**Tester: @vijaykrishna**  


#### Preparing SourceSurfer (system-under-analysis)
- Get a fresh copy of SourceSurfer (system under analysis) ... `git clone https://github.com/inf295uci-2015/SourceSurfer.git`
- Enter the system's directory ... `cd SourceSurfer/`
- Compile the sources of the system ... `mvn compile`
    - Build was successful.
    - `Total time: 2.760 s`; `Finished at: 2015-05-26T16:23:30-08:00`; `Final Memory: 11M/110M`
- Test the compiled system ... `mvn test`
    - Tests were successful: `Tests run: 25, Failures: 0, Errors: 0, Skipped: 0`
    - `Total time: 3.464 s`; `Finished at: 2015-05-26T16:32:25-08:00`; `Final Memory: 13M/110M`
- The system-under-analysis is now ready.

#### Preparing tacoco
- (note: i already had primitive-hamcrest installed on my local machine. check README.md for more details on how to install primitive-hamcrest.)
- switch to home: `cd`
- Get a fresh copy of tacoco: `git clone https://github.com/inf295uci-2015/tacoco`
- Enter tacoco's directory: `cd tacoco`
    - I also had to pull the latest version of the `TacocoRunner` branch: `git pull origin TacocoRunner`. This step will not be necessary if pull request #11 has been approved, merged and closed.
- Compile the sources of tacoco: `mvn compile`
    + Build was successful.
    + `Total time: 2.285 s`; `Finished at: 2015-05-26T16:45:59-08:00`, `Final Memory: 16M/109M`.
- Test the tacoco build: `mvn test`
    + Tests were successful: `Tests run: 4108, Failures: 0, Errors: 0, Skipped: 0`.
    + `Total time: 5.274 s`; `Finished at: 2015-05-26T16:47:27-08:00`; `Final Memory: 10M/174M`.
- tacoco, with the `TacocoRunner` is now ready.

#### Export SourceSurfer dependencies and classes to $CLASSPATH
- Switch to ~/SourceSurfer
- Spit out the paths to SourceSurfer's dependencies with maven dependency into a text file: `SourceSurfer$ mvn dependency:build-classpath -Dmdep.outputFile=cp.txt`
    - Build was successful
    - The contents of cp.txt as a result of the build:
```tex
/home/vijay/.m2/repository/com/esotericsoftware/yamlbeans/yamlbeans/1.
09/yamlbeans-1.09.jar:/home/vijay/.m2/repository/org/mockito/mockito-core/1.10.
19/mockito-core-1.10.19.jar:/home/vijay/.m2/repository/org/hamcrest/hamcrest-
core/1.1/hamcrest-core-1.1.jar:/home/vijay/.
m2/repository/org/objenesis/objenesis/2.1/objenesis-2.1.jar:/home/vijay/.
m2/repository/com/cedarsoftware/json-io/3.0.2/json-io-3.0.2.jar:/home/vijay/.
m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:/home/vijay/.
m2/repository/junit/junit/4.12/junit-4.12.jar:/home/vijay/.
m2/repository/org/hamcrest/hamcrest-library/1.3/hamcrest-library-1.3.
jar:/home/vijay/.m2/repository/org/hamcrest/hamcrest-junit/2.0.0.0/hamcrest-
junit-2.0.0.0.jar:/home/vijay/.m2/repository/org/hamcrest/java-hamcrest/2.0.0.
0/java-hamcrest-2.0.0.0.jar
```
- Verify $CLASSPATH: `echo $CLASSPATH`. $CLASSPATH was empty.
- Export contents of cp.txt to $CLASSPATH: `export CLASSPATH=``cat cp.txt``:$CLASSPATH`
- Verify $CLASSPATH: `echo $CLASSPATH`.
    - Result -- Success! The paths to SourceSurfer's dependencies were added to the classpath, as shown below:
```tex
/home/vijay/.m2/repository/com/esotericsoftware/yamlbeans/yamlbeans/1.
09/yamlbeans-1.09.jar:/home/vijay/.m2/repository/org/mockito/mockito-core/1.10.
19/mockito-core-1.10.19.jar:/home/vijay/.m2/repository/org/hamcrest/hamcrest-
core/1.1/hamcrest-core-1.1.jar:/home/vijay/.
m2/repository/org/objenesis/objenesis/2.1/objenesis-2.1.jar:/home/vijay/.
m2/repository/com/cedarsoftware/json-io/3.0.2/json-io-3.0.2.jar:/home/vijay/.
m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:/home/vijay/.
m2/repository/junit/junit/4.12/junit-4.12.jar:/home/vijay/.
m2/repository/org/hamcrest/hamcrest-library/1.3/hamcrest-library-1.3.
jar:/home/vijay/.m2/repository/org/hamcrest/hamcrest-junit/2.0.0.0/hamcrest-
junit-2.0.0.0.jar:/home/vijay/.m2/repository/org/hamcrest/java-hamcrest/2.0.0.
0/java-hamcrest-2.0.0.0.jar:
```
- Export paths of the classfiles in the target directory (`./target/classes` and `./target/test-classes`) to $CLASSPATH: `export CLASSPATH=./target/test-classes:./target/classes:$CLASSPATH`
- Verify $CLASSPATH: `echo $CLASSPATH`. 
    - Result: Failed, because it added the related paths to the classpath. need to export the absolute paths of the target classes.
- Export **absolute** paths of the classfiles in the target directory to $CLASSPATH: `export CLASSPATH=/home/vijay/SourceSurfer/target/test-classes:/home/vijay/SourceSurfer/target/classes:$CLASSPATH`
- Verify $CLASSPATH: `echo $CLASSPATH`
    + Result: Success! The following string was added in front of $CLASSPATH variable: `/home/vijay/phd-open-source/SourceSurfer/target/test-classes:/home/vijay/phd-open-source/SourceSurfer/target/classes`
- The paths to the dependencies and the target classes of SourceSurfer were successfully exported to the $CLASSPATH variable.

#### Export tacoco dependencies and classes to $CLASSPATH
- Switch to tacoco's directory: `cd ~/tacoco`
- Spit out the paths of tacoco's dependencies into cp.txt `mvn dependency:build-classpath -Dmdep.outputFile=cp.txt`
    - Build was successful, and a mountain of dependencies are now available in cp.txt inside the tacoco directory. (the skipping the contents here, because there just too many dependencies).
- Verify $CLASSPATH to ensure that we have SourceSurfer's dependencies and classes on the $CLASPATH: `echo $CLASSPATH`. 
    - Result: $CLASSPATH has SourceSurfer's dependencies and classes.
    - Note: make sure that you do not open a new termnial session, otherwise $CLASSPATH will be empty --- the $CLASSPATH variable is local for each terminal session.
- Export cp.txt to $CLASSPATH: `export CLASSPATH=``cat cp.txt``:$CLASSPATH`.
    - Result: $CLASSPATH now has the dependencies of tacoco, in addition to the paths of SourceSurfer's dependencies and target classes.
- Export tacoco's target classes and test-classes to $CLASSPATH: `export CLASSPATH=/home/vijay/tacoco/target/test-classes:/home/vijay/tacoco/target/classes:$CLASSPATH`
- Verify $CLASSPATH: `echo $CLASSPATH`
    - Success! -- The paths for the classes and target-classes were added in front of the $CLASSPATH.
- Copy tacoco's dependencies (jar files) into a folder called lib: `mvn dependency:copy-dependencies -DoutputDirectory=lib`.
    + Result: All of tacoco's dependent jars are now in a newly created folder called `lib` under tacoco (i.e. `~/tacoco/lib`).

#### Run SourceSurfer's Test Classes from within tacoco
- Before running the following command, there was no jacoco.exec file in tacoco's project directory. I expect there to be one, when i run the following command.
- Run: `java -javaagent:lib/org.jacoco.agent-0.7.4.201502262128-runtime.jar=destfile=jacoco.exec,dumponexit=false org.spideruci.tacoco.TacocoRunner /home/vijay/SourceSurfer/target/test-classes`
- Results:
    - the tacocolistener seems to have been invoked as i see the following familiar messages on the terminal: "Test case finished. Going to sleep for 10 ms. Done sleeping."
    - there was a jacoco.exec file created after the execution of the above command: `-rw-rw-r-- 1 vijay vijay 315822 May 26 17:39 jacoco.exec`.
    - Successfully executed the `ExecAnalyzer` to spit out the json file from jacoco.exec (i had to change the mainClass to ExecAnalyzer in pom.xml): `mvn exec:java -Dexec.args="/home/vijay/SourceSurfer /home/vijay/tacoco/jacoco.exec dense.json DENSE true"`
