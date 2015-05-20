# tacoco

## Getting started...


* Get fresh tacoco.jar. Type followings from your project root folder.
~~~
  git clone https://github.com/inf295uci-2015/tacoco
  cd tacoco
  mvn package
  cd ..
~~~
* Change your pom.xml  
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
              <value>TacocoListener</value>
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
* Run your testcase and get jacoco.exec file
~~~
   mvn test
~~~
* If it works, add followings to your .travis.yml
~~~
before_install:
  - git clone https://github.com/inf295uci-2015/tacoco
  - cd tacoco
  - mvn package
  - cd ../
~~~

## Compiling and Running ExecAnalyzer utility to read the `jacoco.exec` file

### Compiling ExecAnalyzer
0. Install [Primitive Hamcrest (https://github.com/inf295uci-2015/primitive-hamcrest)](https://github.com/inf295uci-2015/primitive-hamcrest) in your local repository (check instruction in given in primitive-hamcrest's Readme.md).
1. Run `mvn test` as a sanity check to make sure that the encoders are working properly.

### Running ExecAnalyzer
1. Use the following maven command on the command line: `mvn exec:java -Dexec.args="/project/path/of/your/system/under/test/ /path/to/your/jacoco.exec /path/to/your/json/output-file.json <compression-opt> <pretty-print>"`
    * You have 3 choices for `<compression-opt>`: **`LOOSE`, `COMPACT`, `DENSE`**
    * You have 2 choices for `<pretty-print>`: **`true`** or **`false`**
    * The last three arguments, i.e. `/path/to/your/json/output-file.json` `<compression-opt>` `<pretty-print>` are optional. Not specifying those options will result in the selection of default options for each of those arguments.
    * The default options for the last three commands are:
        * `/path/to/your/json/output-file.json` -- **`System.out` i.e. Standard-Out**
        * `<compression-opt>` -- **`DENSE`**
        * `<pretty-print>` -- **`false`**
