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
* Run your testcase and get jacoco.exec file
~~~
   mvn test
~~~
* If it works, add followings to your .travis.yml
~~~
before_install:
  
  - git clone https://github.com/inf295uci-2015/primitive-hamcrest.git  
    cd primitive-hamcrest  
    mvn install 
    cd ../
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
1. Compile the ExecAnalyzer as stated above.
2. Use the following maven command on the command line to execute the ExecAnalyzer: `mvn exec:java -Dexec.args="/project/path/of/your/system/under/test/ /path/to/your/jacoco.exec /path/to/your/json/output-file.json <compression-opt> <pretty-print>"`
    * You have 3 choices for `<compression-opt>`: **`LOOSE`, `COMPACT`, `DENSE`**
    * You have 2 choices for `<pretty-print>`: **`true`** or **`false`**
    * The last three arguments, i.e. `/path/to/your/json/output-file.json` `<compression-opt>` `<pretty-print>` are optional. Not specifying those options will result in the selection of default options for each of those arguments.
    * The default options for the last three commands are:
        * `/path/to/your/json/output-file.json` -- **`System.out` i.e. Standard-Out**
        * `<compression-opt>` -- **`DENSE`**
        * `<pretty-print>` -- **`false`**

### Notes on Line-coverage Compression

#### COMPACT Format

- Bit-based encoding for each line-level coverage information.
- Line-level coverage infromation encoded to single 32-bit int.
- Each (source) line has the following two counters:
  - Bytecode Instruction Coverage Counter (number of bytecode instructions covered and missed)
  - Branch Coverage Counter (number of branches covered and missed)
- Compression Scheme is as follows:
  - (starting from the most significant bits)
  - First 8 bits encode number of bytecode instructions covered,
  - Next 8 bits encode number of bytecode instructions missed,
  - Next 8 bits encode number of branches covered,
  - Next 8 bits encode number of branches missed.
- Assuming: each line contains max 255 bytecode instructions or branches.

#### DENSE Format

- Only the statuses of the lines i.e. EMPTY, NOT_COVERED, PARTLY_COVERED, FULLY_COVERED are encoded in DENSE formatting.
- 2 bits are used per line for each line status: EMPTY=00, NOT_COVERED=01, PARTLY_COVERED=11, FULLY_COVERED=10.
- Instruction and Branch counters are **disregarded** in DENSE formatting.
- DENSE formatting encodes 16 (at most) line-statuses into a single 32-bit integer.
- DENSE formatting is implemented in LinesStatusCoder.
