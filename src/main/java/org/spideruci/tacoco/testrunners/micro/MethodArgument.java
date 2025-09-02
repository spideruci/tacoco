package org.spideruci.tacoco.testrunners.micro;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.security.TypePermission;

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
        XStream xstream = new XStream(new Xpp3Driver());
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.addPermission(new AllowAllTypes());
        return xstream.fromXML(value);
    }
}

class AllowAllTypes implements TypePermission {

    @Override
    public boolean allows(Class type) {
        return true;
    }

}
