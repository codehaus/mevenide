package org.mevenide.ui.jbuilder;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * URL class loader that prefers our local classes to the ones in the parent
 * class loaders, so that we may use more recent versions of the libraries.
 *
 * This implementation also allows us to still go to the system class loader
 * for the JDK classes and certain JBuilder classes.
 *
 * @author Serge Huber
 * @version 1.0
 */

public class FilteringClassLoader extends URLClassLoader {

    public FilteringClassLoader(URL[] urls) {
        super(urls);
    }

    public Class findClass(String name) throws ClassNotFoundException {
        // System.out.println("Finding class " + name);
        if (name.startsWith("java.") ||
            name.startsWith("javax.swing.") ||
            name.startsWith("sun.") ||
            name.startsWith("com.borland.") ||
            name.startsWith("org.apache.xerces.") ||
            name.startsWith("org.xml.sax.") ||
            name.startsWith("org.apache.xalan.") ||
            name.equals("org.mevenide.ui.jbuilder.MavenFileNode") ||
            name.equals("org.mevenide.ui.jbuilder.MavenGoalNode") ||
            name.equals("org.mevenide.ui.jbuilder.IFileNodeWorker")) {
            return super.findSystemClass(name);
        }
        return super.findClass(name);
    }

    public Class loadClass(String name)
        throws ClassNotFoundException {
        return loadClass(name, false);
    }

    protected Class loadClass(String name,
                              boolean resolve)
        throws ClassNotFoundException {
        Class result = null;
        result = findLoadedClass(name);
        if (result == null) {
            result = findClass(name);
        }
        if ((result != null) && resolve) {
            resolveClass(result);
        }
        return result;
    }

}
