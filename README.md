# tacoco

## Getting started...
0. get fresh tacoco.jar. Type followings from your project root folder.
~~~
  git clone https://github.com/inf295uci-2015/tacoco
  cd tacoco
  mvn package
  cd ..
~~~
1. change your pom.xml  
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
2. run your testcase and get jacoco.exec file
~~~
   mvn test
~~~

## Running ExecDump utility to read the `jacoco.exec` file

0. Change the path of the jacoco.exec in pom.xml, where specified.
1. Use the following maven command on the command line: `mvn exec:java -Dexec.args="/path/to/your/jacoco.exec"`
