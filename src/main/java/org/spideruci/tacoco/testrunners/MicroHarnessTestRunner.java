package org.spideruci.tacoco.testrunners;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.spideruci.tacoco.analysis.AnalysisResults;
import org.spideruci.tacoco.testlisteners.ITacocoTestListener;
import org.spideruci.tacoco.testrunners.micro.MethodArgument;
import org.spideruci.tacoco.testrunners.micro.MicroHarnessSpec;
import org.spideruci.tacoco.testrunners.micro.MicroHarnessTest;

import com.thoughtworks.xstream.XStream;

public class MicroHarnessTestRunner extends AbstractTestRunner {

    public ArrayList<MicroHarnessTest> tests(ArrayList<MicroHarnessSpec> specs) {
        ArrayList<MicroHarnessTest> microTests = new ArrayList<>();

        for (MicroHarnessSpec spec : specs) {
            MicroHarnessTest test = new MicroHarnessTest() {
                public AnalysisResults test() {
                    AnalysisResults results = new AnalysisResults();
                    StringBuilder beforeArgs = new StringBuilder();
                    StringBuilder afterArgs = new StringBuilder();

                    try {
                        Class<?> methodClass = Class.forName(spec.methodClassName());
                        Method method = spec.reflectedMethod(methodClass);
                        if (method == null) {
                            results.put("[error 🆘]", "Method not found");
                            return results;
                        }

                        int argCount = spec.arguments.length;

                        for (MethodArgument argument : spec.arguments) {
                            beforeArgs.append("arg\n");
                            beforeArgs.append(argument.value.trim());
                            beforeArgs.append("\n");
                        }

                        if (argCount == 0) {
                            method.invoke(null);
                        } else if (argCount == 1) {
                            Object arg = spec.arguments[0].getObject();

                            if (spec.methodIsStatic) {
                                method.invoke(null, arg);
                            } else {
                                method.invoke(arg);
                            }

                            afterArgs.append("arg\n");
                            afterArgs.append(new XStream().toXML(arg).trim());
                            afterArgs.append("\n");
                        } else {
                            Object firstArg = spec.arguments[0].getObject();
                            Object[] remainigObjects = new Object[argCount - 1];
                            for (int idx = 1; idx < argCount; idx += 1) {
                                remainigObjects[idx - 1] = spec.arguments[idx].getObject();
                            }

                            if (spec.methodIsStatic) {
                                Object[] allObjects = new Object[remainigObjects.length + 1];
                                allObjects[0] = firstArg;
                                for (int idx = 0; idx < remainigObjects.length; idx += 1) {
                                    allObjects[idx + 1] = remainigObjects[idx];
                                }
                                method.invoke(null, allObjects);
                            } else {
                                method.invoke(firstArg, remainigObjects);
                            }

                            afterArgs.append("arg\n");
                            afterArgs.append(new XStream().toXML(firstArg).trim());
                            afterArgs.append("\n");

                            for (Object object : remainigObjects) {
                                afterArgs.append("arg\n");
                                afterArgs.append(new XStream().toXML(object).trim());
                                afterArgs.append("\n");
                            }
                        }

                        final String beforeArgsString = beforeArgs.toString().trim();
                        final String afterArgsString = afterArgs.toString().trim();
                        

                        if (!beforeArgsString.equals(afterArgsString)) {
                            StringBuilder diffString = new StringBuilder();
                            diffString.append("Before\n");
                            diffString.append(beforeArgsString.indent(1)); 
                            diffString.append("\n");
                            diffString.append("After\n");
                            diffString.append(afterArgsString.indent(1)); 
                            diffString.append("\n");
                            results.put("[diff 🆎]", diffString.toString());
                        }
                    } catch (ClassNotFoundException | SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                        StringBuilder diffString = new StringBuilder();

                        StringWriter stackTraceWriter = new StringWriter();
                        PrintWriter p = new PrintWriter(stackTraceWriter, true);
                        e.printStackTrace(p);
                        p.flush();
                        stackTraceWriter.flush();

                        diffString.append("Before\n");
                        diffString.append(beforeArgs.toString().indent(1)); 
                        diffString.append("\n");
                        diffString.append("After\n");
                        diffString.append(stackTraceWriter.toString().indent(1)); 
                        diffString.append("\n");

                        results.put("[diff 🆎]", diffString.toString());
                    }

                    return results;
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