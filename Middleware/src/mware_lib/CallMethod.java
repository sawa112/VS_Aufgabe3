package mware_lib;

import java.io.Serializable;

/**
 * CallMethod
 */
public class CallMethod implements Serializable{
    private Reference reference;
    private String methodName;
    private Object[] args;
    private Class<?>[] types;

    public CallMethod(Reference reference, String methodName, Object[] args, Class<?>[] types){
        this.reference = reference;
        this.methodName = methodName;
        this.args = args;
        this.types = types;
    }

    public Reference getReference() {
        return reference;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public Class<?>[] getTypes() {
        return types;
    }
}
