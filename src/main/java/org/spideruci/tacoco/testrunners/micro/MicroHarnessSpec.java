package org.spideruci.tacoco.testrunners.micro;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spideruci.tacoco.analysis.MicroTestAnalyzer;
import org.spideruci.tacoco.cli.AbstractCli;
import org.spideruci.tacoco.cli.AnalyzerCli;
import org.spideruci.analysis.dynamic.util.MethodDescSplitter;


public class MicroHarnessSpec {
    public final String methodName;
    public final boolean methodIsStatic;
    public final int argCount;
    public final String corelId; 
    public final MethodArgument[] arguments;
    public final Path specPath;

    public MicroHarnessSpec(
        String methodName,
        boolean methodIsStatic,
        int argCount,
        String corelId,
        MethodArgument[] arguments,
        Path specPath
    ) {
        this.methodName = methodName;
        this.methodIsStatic = methodIsStatic;
        this.argCount = argCount;
        this.corelId = corelId;
        this.arguments = arguments;
        this.specPath = specPath;
    }

    /*
     * Example:
        org/apache/commons/validator/util/Flags.isOff(J)Z
        isStatic:1
        2
        3f2b6d70-11d5-497d-a54f-c9dd2e4f89fe
        Argument (3f2b6d70-11d5-497d-a54f-c9dd2e4f89fe) : 0/1
        <org.apache.commons.validator.util.Flags>
        <flags>5</flags>
        </org.apache.commons.validator.util.Flags>
        Argument (3f2b6d70-11d5-497d-a54f-c9dd2e4f89fe) : 1/1
        <long>2</long>
     */
    public final static MicroHarnessSpec create(Path specPath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(specPath.toFile()));
        List<String> lines = reader.lines().toList();
        if (lines.size() < 4) {
            reader.close();
            return null;
        }


        String methodName = lines.get(0);
        boolean isStatic = lines.get(1).equals("isStatic:0");
        int argCount = Integer.parseInt(lines.get(2)); 
        String correlId = lines.get(3);
        ArrayList<MethodArgument> methodArguments = new ArrayList<>();

        // parse Argument sections
        int lineIdx = 4;
        int argIndex = -1;
        StringBuilder argumentInProgress = null;
        while (lineIdx < lines.size()) {
            String line = lines.get(lineIdx);

            if (line.startsWith("Argument")) {
                // Starting a new argument:
                // 1. Save the old
                if (argumentInProgress != null) {
                    String argumentValue = argumentInProgress.toString();
                    MethodArgument argument = new MethodArgument(
                        argumentValue, 
                        ++argIndex,
                        correlId,
                        methodName,
                        isStatic,
                        argCount
                    );
                    methodArguments.add(argument);
                }

                // 2. Start a new.
                argumentInProgress = new StringBuilder();
            } else {
                argumentInProgress.append(line);
                argumentInProgress.append("\n");
            }

            // endmatter
            lineIdx += 1;
        }

        // last argument
        if (argumentInProgress != null) {
            String argumentValue = argumentInProgress.toString();
            MethodArgument argument = new MethodArgument(
                argumentValue, 
                ++argIndex,
                correlId,
                methodName,
                isStatic,
                argCount
            );
            methodArguments.add(argument);
        }

        reader.close();

        if (argCount != methodArguments.size()) {
            String mut =  AnalyzerCli.readOptionalArgumentValue(AbstractCli.ANALYZER_METHOD_UNDER_TEST, null);
            if (mut != null && mut.equals(methodName)) {
                for (String line : lines) {
                    MicroTestAnalyzer.logDebug(line);
                }
                MicroTestAnalyzer.logDebug("");
            }

            return null;
        }

        MicroHarnessSpec microHarnessSpec = new MicroHarnessSpec(
            methodName,
            isStatic,
            argCount,
            correlId,
            methodArguments.toArray(new MethodArgument[methodArguments.size()]),
            specPath
        );

        
        return microHarnessSpec;
    }

    public String methodClassName() {
        int splitIndex = methodName.indexOf('.', 0);
        String className = methodName.substring(0, splitIndex).replaceAll("/", ".");
        MicroTestAnalyzer.logDebug("methodClassName:" + className);
        return className;
    }

    public String shortmethodName() {
        int splitIndex = methodName.indexOf('.', 0);
        String shortMehthodName = methodName.substring(splitIndex + 1, methodName.length());
        MicroTestAnalyzer.logDebug("short methodName:" + shortMehthodName);
        return shortMehthodName;
    }

    public String justMethodName() {
        String shortMethodName = shortmethodName();
        int index = shortMethodName.indexOf('(');
        return shortMethodName.substring(0, index);
    }

    public String methodParamString() {
        String shortMethodName = shortmethodName();
        int openBraceIndex = shortMethodName.indexOf('(');
        int closeBraceIndex = shortMethodName.indexOf(')');
        if (openBraceIndex + 1 == closeBraceIndex) { 
            return "";
        }

        return shortMethodName.substring(openBraceIndex + 1, closeBraceIndex);
    }

    public String methodReturnType() {
        String shortMethodName = shortmethodName();
        int closeBraceIndex = shortMethodName.indexOf(')');
        return shortMethodName.substring(closeBraceIndex + 1, shortMethodName.length());
    }

    public Method reflectedMethod(Class<?> methodClass) {
        String justMethodName = justMethodName();
        Class<?>[] paramClasses = null;
        try {
            paramClasses = methodParametertypes();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        try {
            MicroTestAnalyzer.logDebug("reflectedMethod: " + methodName);
            Method method = methodClass.getMethod(justMethodName, paramClasses);
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            // we may have run into a private method
            try {
                Method method = methodClass.getDeclaredMethod(justMethodName, paramClasses);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException | SecurityException e1) {
                // failued to fetch the method. 
                e.printStackTrace();
                return null;
            }
        }
    }

    private Class<?>[] methodParametertypes() throws ClassNotFoundException {
        String shortMethodName = shortmethodName();
        String[] paramsSplit = MethodDescSplitter.getArgTypeSplit(shortMethodName);
        ArrayList<Class<?>> paramClasses = new ArrayList<>();
        for (String param : paramsSplit) {
            switch(param) {
            case "Z": paramClasses.add(boolean.class); break;
            case "B": paramClasses.add(byte.class); break;
            case "C": paramClasses.add(char.class); break;
            case "S": paramClasses.add(short.class); break;
            case "I": paramClasses.add(int.class); break;
            case "F": paramClasses.add(float.class); break;
            case "J": paramClasses.add(long.class); break;
            case "D": paramClasses.add(double.class); break;
            default:
                if (param.startsWith("[")) {
                    // this should theoretically handle and work for cases like "[I" or "[[J"
                    String arrayClassDescriptor = param.replace('/', '.');
                    Class<?> arrayClass = Class.forName(arrayClassDescriptor);
                    paramClasses.add(arrayClass);
                    break;
                }

                if (param.startsWith("L")) {
                    String arrayClassDescriptor = param.substring(1, param.length() - 1) // skip the leading L and trailing ;
                                                       .replace('/', '.');
                    Class<?> arrayClass = Class.forName(arrayClassDescriptor);
                    paramClasses.add(arrayClass);
                    break;
                }
            }

        }

        return paramClasses.toArray(new Class<?>[paramClasses.size()]);
    }
}
