package org.spideruci.tacoco.testrunners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;
import org.spideruci.tacoco.testrunners.micro.MicroHarnessSpec;
import org.spideruci.tacoco.testrunners.micro.MicroHarnessTest;

public class MicroHarnessTestRunner extends AbstractTestRunner {

    public ArrayList<MicroHarnessTest> tests(ArrayList<MicroHarnessSpec> specs) {
        ArrayList<MicroHarnessTest> microTests = new ArrayList<>();

        for (MicroHarnessSpec spec : specs) {
            MicroHarnessTest test = new MicroHarnessTest() {
                public void test() {
                    try {
                        Class<?> methodClass = Class.forName(spec.methodClassName());
                        Method method = spec.reflectedMethod(methodClass);
                        if (method == null) {
                            return;
                        }

                        int argCount = spec.arguments.length;

                        if (argCount == 0) {
                            method.invoke(null);
                        } else if (argCount == 1) {
                            Object arg = spec.arguments[0].getObject();
                            method.invoke(arg);
                        } else {
                            Object firstArg = spec.arguments[0].getObject();
                            Object[] remainigObjects = new Object[argCount - 1];
                            for (int idx = 1; idx < argCount; idx += 1) {
                                remainigObjects[idx - 1] = spec.arguments[idx].getObject();
                            }
                            
                            method.invoke(firstArg, remainigObjects);
                        }
                    } catch (ClassNotFoundException | SecurityException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            };
            microTests.add(test);
        }

        return microTests;
    }

    @Override
    public boolean shouldRun(Class<?> test) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shouldRun'");
    }

    @Override
    public void listenThrough(ITacocoTestListener listener) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listenThrough'");
    }

    @Override
    public Callable<AnalysisResults> getExecutableTest(Class<?> test) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExecutableTest'");
    }

    @Override
    public void printTestRunSummary(AnalysisResults results) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'printTestRunSummary'");
    }
}