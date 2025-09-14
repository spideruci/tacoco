package org.spideruci.tacoco.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.spideruci.tacoco.cli.AbstractCli;
import org.spideruci.tacoco.cli.AnalyzerCli;
import org.spideruci.tacoco.testrunners.MicroHarnessTestRunner;
import org.spideruci.tacoco.testrunners.micro.MicroHarnessSpec;
import org.spideruci.tacoco.testrunners.micro.MicroHarnessTest;

// import org.apache.tools.ant.taskdefs.Exec;

public class MicroTestAnalyzer extends AbstractAnalyzer {
    final static boolean DEBUG = false;

    @Override
    public void analyze() {
        System.out.println("Starting analysis");

        String analyzerMUT = AnalyzerCli.readOptionalArgumentValue(AbstractCli.ANALYZER_METHOD_UNDER_TEST, null);
        logDebug("MUT: " + analyzerMUT);
        
        int cores = Runtime.getRuntime().availableProcessors(); logDebug("core count: " + (cores));
        ExecutorService threadExecutorService = Executors.newFixedThreadPool(cores);
        List<Future<MicroHarnessSpec>> futureAnalysisResults = new ArrayList<>();

        HashMap<String /*methodName*/, ArrayList<String /*specPath*/>> pathMap = new HashMap<>();
        HashMap<String /*methodName*/, ArrayList<MicroHarnessSpec>> indexedMicroHarnesses = new HashMap<>();
        final String pathMapLogfileName = "pathmap.log";

        Path pathMapLogFilePath = Path.of(pathMapLogfileName);
        if (Files.exists(pathMapLogFilePath)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(pathMapLogFilePath.toFile()));
                List<String> lines = reader.lines().toList();
                for (String line : lines) {
                    int tabIndex = line.indexOf('\t');
                    String methodName = line.substring(0, tabIndex).trim();
                    String harnessPathString = line.substring(tabIndex, line.length()).trim();

                    if (!pathMap.containsKey(methodName)) {
                        pathMap.put(methodName, new ArrayList<>());
                    }

                    pathMap.get(methodName).add(harnessPathString);
                }
                reader.close();

                if (analyzerMUT != null) {
                    ArrayList<String /*specPath*/> specPaths = pathMap.get(analyzerMUT);
                    ArrayList<MicroHarnessSpec> harnessPaths = new ArrayList<>();
                    for (String specPathString : specPaths) {
                        Path specPath = Path.of(specPathString);
                        MicroHarnessSpec spec = MicroHarnessSpec.create(specPath);
                        harnessPaths.add(spec);
                    }

                    indexedMicroHarnesses.put(analyzerMUT, harnessPaths);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        } else {
            Path sutPath = sutPath(); logDebug("sutPath:" + sutPath);
            final String sutName = sutName(); logDebug("sutPath:" + sutName);
            final String harnessPrefix = sutName + "-"; logDebug("harnessPrefix:" + harnessPrefix);
            try (Stream<Path> lazySubPaths = Files.list(sutPath)) {
                List<Path> harnessClusters = lazySubPaths.filter( path -> 
                    path.getFileName()
                        .toString()
                        .startsWith(harnessPrefix)
                ).toList();
                logDebug("harnessClusters Count:" + harnessClusters.size());
                
                for (Path harnessClusterPath : harnessClusters) {
                    for (Path specPath : getHarnessSpecPaths(harnessClusterPath)) {
                        Callable<MicroHarnessSpec> callable = new Callable<MicroHarnessSpec>() {
                            @Override
                            public MicroHarnessSpec call() throws Exception {
                                MicroHarnessSpec harnessSpec = MicroHarnessSpec.create(specPath);
                                return harnessSpec;
                            }
                        };
                        Future<MicroHarnessSpec> future = threadExecutorService.submit(callable);
                        futureAnalysisResults.add(future);
                    }
                }

                // Stop accepting newer jobs, and finish up what has already been submitted.
                threadExecutorService.shutdown();

                try {
                    // this is a blocking call; and it means that we cannot operate on the Futures
                    // till all Futures complete.
                    threadExecutorService.awaitTermination(Long.MAX_VALUE,TimeUnit.MILLISECONDS);

                    int count = 0;
                    int usableCount = 0;

                    for (Future<MicroHarnessSpec> future : futureAnalysisResults) {
                        count += 1;
                        MicroHarnessSpec harnessSpec = future.get();
                        
                        if (harnessSpec != null) {
                            usableCount += 1;

                            String methodName = harnessSpec.methodName;
                            String specPathString = harnessSpec.specPath.toAbsolutePath().toString();
                            if (!indexedMicroHarnesses.containsKey(methodName)) {
                                indexedMicroHarnesses.put(methodName, new ArrayList<>());
                            }
                            indexedMicroHarnesses.get(methodName).add(harnessSpec);

                            if (!pathMap.containsKey(methodName)) {
                                pathMap.put(methodName, new ArrayList<>());
                            }
                            pathMap.get(methodName).add(specPathString);
                        }
                    }
                    
                    System.out.println(count);
                    System.out.println(usableCount);

                    System.out.println(indexedMicroHarnesses.size());

                    for (Entry<String, ArrayList<MicroHarnessSpec>> entry : indexedMicroHarnesses.entrySet()) {
                        System.out.println("\t" + String.format("%6d", entry.getValue().size()) + "\t" + entry.getKey());
                    }

                    try (PrintStream pathMapStream = new PrintStream(new File(pathMapLogfileName))) {
                        for (Entry<String, ArrayList<String>> entry : pathMap.entrySet()) {
                            String methodName = entry.getKey();
                            for (String path : entry.getValue()) {
                                pathMapStream.println(methodName + "\t" + path);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (analyzerMUT != null) {
            logDebug("running " + analyzerMUT);
            ArrayList<MicroHarnessSpec> testsForMethodUnderTest = indexedMicroHarnesses.get(analyzerMUT);
            if (testsForMethodUnderTest != null) {
                MicroHarnessTestRunner testRunner = new MicroHarnessTestRunner();
                ArrayList<MicroHarnessTest> microTests = testRunner.tests(testsForMethodUnderTest);
                for (MicroHarnessTest microTest : microTests) {
                    
                    AnalysisResults results = microTest.test();
                    if (results.size() == 0) {
                        System.out.println("[µTest][Method] ✅ " + analyzerMUT);
                    } else {
                        System.out.println("[µTest][Method] 🟥 " + analyzerMUT);
                        for (Entry<String, Object> entry : results) {
                            System.out.println("[µTest][Results]" + entry.getKey());
                            System.out.println(entry.getValue());
                        }
                    }
                }
            }
        }

        System.out.println("Finishing analysis");
    }

    private Path sutPath() {
        String sutPathString = this.buildProbe.getAbsoluteTargetPath();
        return Path.of(sutPathString);
    }

    private String sutName() {
        Path sutPath = sutPath();
        return sutPath.getFileName().toString();
    }

    private ArrayList<Path> getHarnessSpecPaths(Path harnessCLusterPath) {
        ArrayList<Path> specPaths = new ArrayList<>();
        
        try {
            List<Path> paths = Files.list(harnessCLusterPath)
                                    // .filter(path -> path.toString().endsWith(".log"))
                                    .toList();
            for (Path path : paths) {
                specPaths.add(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return specPaths;
    }

    @Override
    public String getName() {
        return "MicroTestAnalyzer";
    }

    @Override
    public void printAnalysisSummary() {
        // do nothing for now
    }
    
    public static void logDebug(String message) {
        if (DEBUG) {
            System.out.println("[µTest DEBUG] " + message);
        }
    }
}
