package org.spideruci.tacoco.testrunners.micro;

import com.thoughtworks.xstream.XStream;

public class MethodArgument {
    public final String value; 
    public final int index;
    public final String corelId; 
    public final String methodName; 
    public final boolean methodIsStatic;
    public final int argCount;

    public MethodArgument(
        String value,
        int index,
        String corelId,
        String methodName,
        boolean methodIsStatic,
        int argCount
    ) {
        this.value = value; 
        this.index = index;
        this.corelId = corelId; 
        this.methodName = methodName; 
        this.methodIsStatic = methodIsStatic;
        this.argCount = argCount;
    }

    public Object getObject() {
        return new XStream().fromXML(value);
    }
}
