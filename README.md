# tacoco
[![Build Status](https://travis-ci.org/inf295uci-2015/tacoco.svg?branch=master)](https://travis-ci.org/inf295uci-2015/tacoco)
[![Coverage Status](https://coveralls.io/repos/inf295uci-2015/tacoco/badge.svg?branch=master)](https://coveralls.io/r/inf295uci-2015/tacoco?branch=master)

## Compiling Tacoco
1. Install [Primitive Hamcrest (https://github.com/inf295uci-2015/primitive-hamcrest)](https://github.com/inf295uci-2015/primitive-hamcrest) in your local repository (check instruction in given in primitive-hamcrest's Readme.md).
2. Run `mvn comile` as a sanity check to make sure that there are no compile-time errors.
3. Run `mvn test` to make sure that tacoco is working against its own test-cases.

## Analyzing with Tacoco

* Get fresh tacoco
~~~
  git clone https://github.com/spideruci/tacoco
  mvn compile
~~~
* Run tacoco 
~~~
cd /to/your/project/root
mvn test
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
export CLASSPATH=`cat cp.txt`:$CLASSPATH
export CLASSPATH={your project absolute path}/target/test-classes:{your project absolute path}/target/classes:$CLASSPATH

cd /to/tacoco/project/root
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
export CLASSPATH=`cat cp.txt`:$CLASSPATH
export CLASSPATH={tacoco project absolute path}/target/test-classes:{tacoco project absolute path}/target/classes:$CLASSPATH
mvn dependency:copy-dependencies -DoutputDirectory=lib
java -javaagent:lib/org.jacoco.agent-0.7.4.201502262128-runtime.jar=destfile=jacoco.exec,dumponexit=false org.spideruci.tacoco.TacocoRunner {your project absolute path}/target/test-classes
~~~
* Alternatively use the `export-sut-cp` to Run tacoco
~~~
## make sure that you run `mvn test` on your project
cd /to/your/project/root
mvn test
## switch to tacoco and run export-sut-cp
cd /to/tacoco/project/root
cd scripts
chmod +x export-sut-cp # you need to do this just once.
./export-sut-cp absolute/path/of/your/project/root absolute/path/of/tacoco/root
## this should create a jacoco.exec file in the `absolute/path/of/tacoco/root`
~~~

## Running ExecAnalyzer utility to parse the `jacoco.exec` file

### Help Menu

```
tacoco$ mvn -q exec:java -Panalyzer -Dtacoco.help

Tacoco: Exec-file Analyzer
usage: mvn exec:java -q -Panalyzer [arguments] 

Arguments:
-Dtacoco.sut=<dir>                  (Required) Absolute-path of system-
                                    under-test's root.
-Dtacoco.exec=<*.exec>              (Required) Absolute-path of input exec
                                    binary.
-Dtacoco.json=<*.json>              (Default: STDOUT) Absolute-path of per-test
                                    coverage output.
-Dtacoco.fmt=<LOOSE|COMPACT|DENSE>  (Default: DENSE) Compression format of
                                    coverage data.
-Dtacoco.pp                         Pretty prints coverage data to json file.
-Dtacoco.help                       Prints this message and exits (with 0).
```

### Step-wise instructions
1. Compile the ExecAnalyzer as stated above.
2. Use the `mvn` command on the command line to execute the ExecAnalyzer as shown above in the help menu, e.g.: `mvn exec:java -q -Panalyzer -Dtacoco.sut=/home/vijay/misc_Programming/pmd/pmd-java -Dtacoco.json=pmd-java-compact.json -Dtacoco.exec=jacoco.exec -Dtacoco.pp -Dtacoco.fmt=COMPACT`
    * `-Dtacoco.pp` and `-Dtacoco.help` are treated as flags.
    * `-Dtacoco.json=[*.json]` and `Dtacoco.fmt=[LOOSE|COMPACT|DENSE]`. Not specifying those options will result in the selection of default options for each of those arguments.
    * The default options are:
        * `-Dtacoco.json=[*.json]` -- **`System.out` i.e. Standard-Out**
        * `-Dtacoco.fmt=[LOOSE|COMPACT|DENSE]` -- **`DENSE`**
    * `-Dtacoco.pp`, being a flag, is also optional. Not providing it means that you do not want pretty-printed coverage-data.
3. NOTE: Coverage Compression Formats and Pretty-printing are two different things. Pretty-printing simply ensures that the Json output is printed in a non-minified manner. Continue reading to learn more about coverage compression formats.

### Notes on Line-coverage Compression Formats

You have 3 choices for `-Dtacoco.fmt=`: **`LOOSE`, `COMPACT`, `DENSE`**

#### LOOSE Format
- This is a space-inefficient formatting of the coverage information.

#### COMPACT Format

- Bit-based encoding for each line-level coverage information.
- Line-level coverage information encoded to single 32-bit int.
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

## Running CoverageJsonReader

```tex
tacoco$ mvn -q exec:java -Preader -Dtacoco.help

Tacoco: Coverage Json-file Reader
usage: mvn exec:java -q -Preader [arguments] 

Arguments:
-Dtacoco.json=<*.json>  (Required) Absolute-path of per-test coverage file.
-Dtacoco.out=<*.json>   Absolute-path of per-sourcefile coverage matrix.
-Dtacoco.pp             Pretty prints coverage data to json file.
-Dtacoco.help           Prints this message and exits (with 0).
```

1. Use the following maven command on the command line to execute the CoverageJsonReader: `mvn exec:java -Preader -Dtacoco.json="/path/to/your/json/coverage-data-file.json"`

## Space Optimized Coverage Matrix

### Per source file Coverage matrix
- The coverage matrix for a project is split into smaller coverage matrices for each source file.
- The statements in this matrix are localized to only the statements in the sourcefile in question.
- We maintain a list of tests for which the test-statement matrix is built.
- Tests that do not execute a single statement in the sourcefile are considered IRRELEVANT and are not a part of the test-statement matrix.
- Check `org.spideruci.tacoco.coverage.CoverageMatrix2` and `org.spideruci.tacoco.coverage.SourceSpecificCoverageMatrix` to study the models for per-source coverage.
- More to come ...
  + converting the boolean arrays into bit arrays/vectors
  + (optional) including an index to the test-case names. currently only the test-case-ids are listed in the per-source-coverage-matrices.
