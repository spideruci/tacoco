package org.spideruci.tacoco.testrunners.micro;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MicroHarnessSpec {
    public final String methodName;
    public final boolean methodIsStatic;
    public final int argCount;
    public final String corelId; 
    public final MethodArgument[] arguments;

    public MicroHarnessSpec(
        String methodName,
        boolean methodIsStatic,
        int argCount,
        String corelId,
        MethodArgument[] arguments
    ) {
        this.methodName = methodName;
        this.methodIsStatic = methodIsStatic;
        this.argCount = argCount;
        this.corelId = corelId;
        this.arguments = arguments;
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

        MicroHarnessSpec microHarnessSpec = new MicroHarnessSpec(
            methodName,
            isStatic,
            argCount,
            correlId,
            methodArguments.toArray(new MethodArgument[methodArguments.size()])
        );

        reader.close();
        return microHarnessSpec;
    }

    public String methodClassName() {
        return methodName.split(".")[0].replaceAll("/", ".");
    }

    public Method reflectedMethod(Class<?> methodClass) {
        try {
            Method method = methodClass.getMethod(methodName, null);
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            // we may have run into a private method
            try {
                Method method = methodClass.getDeclaredMethod(methodName, null);
                return method;
            } catch (NoSuchMethodException | SecurityException e1) {
                // failued to fetch the method. 
                return null;
            }
        }
    }
}
