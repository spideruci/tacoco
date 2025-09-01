package org.spideruci.tacoco.analysis;

import java.io.IOException;
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

import org.spideruci.tacoco.testrunners.micro.MicroHarnessSpec;

// import org.apache.tools.ant.taskdefs.Exec;

public class MicroTestAnalyzer extends AbstractAnalyzer {
    final static boolean DEBUG = true;

    @Override
    public void analyze() {
        int cores = Runtime.getRuntime().availableProcessors(); logDebug("core count: " + (cores));
        ExecutorService threadExecutorService = Executors.newFixedThreadPool(cores);
        List<Future<MicroHarnessSpec>> futureAnalysisResults = new ArrayList<>();

        System.out.println("Starting analysis");
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

                HashMap<String /*methodName*/, ArrayList<MicroHarnessSpec>> indexedMicroHarnesses = new HashMap<>();

                for (Future<MicroHarnessSpec> future : futureAnalysisResults) {
                    count += 1;
                    MicroHarnessSpec harnessSpec = future.get();
                    
                    if (harnessSpec != null) {
                        usableCount += 1;

                        String methodName = harnessSpec.methodName;
                        if (!indexedMicroHarnesses.containsKey(methodName)) {
                            indexedMicroHarnesses.put(methodName, new ArrayList<>());
                        }

                        indexedMicroHarnesses.get(methodName).add(harnessSpec);
                    }
                }
                
                System.out.println(count);
                System.out.println(usableCount);

                System.out.println(indexedMicroHarnesses.size());

                for (Entry<String, ArrayList<MicroHarnessSpec>> entry : indexedMicroHarnesses.entrySet()) {
                    System.out.println("\t" + String.format("%6d", entry.getValue().size()) + "\t" + entry.getKey());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }



            
        } catch (IOException e) {
            e.printStackTrace();
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
    
    private void logDebug(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }
}
