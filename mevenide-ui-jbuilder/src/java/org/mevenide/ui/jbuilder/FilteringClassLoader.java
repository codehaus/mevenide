package org.mevenide.ui.jbuilder;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jahia Ltd</p>
 * <p>Company: Jahia Ltd</p>
 * @author not attributable
 * @version 1.0
 */

public class FilteringClassLoader extends URLClassLoader {

    public FilteringClassLoader(URL[] urls) {
        super(urls);
    }

    public Class findClass(String name) throws ClassNotFoundException {
        // System.out.println("Finding class " + name);
        if (name.startsWith("java.") ||
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
