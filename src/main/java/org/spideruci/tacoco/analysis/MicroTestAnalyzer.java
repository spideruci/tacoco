package org.spideruci.tacoco.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
        List<Future<Integer[]>> futureAnalysisResults = new ArrayList<>();

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
                    Callable<Integer[]> callable = new Callable<Integer[]>() {
                        @Override
                        public Integer[] call() throws Exception {
                            MicroHarnessSpec harnessSpec = MicroHarnessSpec.create(specPath);
                            final int usableCount = harnessSpec == null ? 0 : 1;

                            return new Integer[] { 1 /*count*/, usableCount };
                        }
                    };
                    Future<Integer[]> future = threadExecutorService.submit(callable);
                    futureAnalysisResults.add(future);
                }
            }

            // Stop accepting newer jobs, and finish up what has already been submitted.
            threadExecutorService.shutdown();

            try {
                threadExecutorService.awaitTermination(Long.MAX_VALUE,TimeUnit.MILLISECONDS);

                int count = 0;
                int usableCount = 0;

                for (Future<Integer[]> future : futureAnalysisResults) {
                    Integer[] counts = future.get();
                    count += counts[0];
                    usableCount += counts[1];
                }

                System.out.println(count);
                System.out.println(usableCount);
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
