package org.rs.samples;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class TransactionConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resource
        classes.add(TransactionResource.class);
        classes.add(TransactionExceptions.class);
        return classes;
    }
}
