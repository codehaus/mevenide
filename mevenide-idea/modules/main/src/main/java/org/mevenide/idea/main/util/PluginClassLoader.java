package org.mevenide.idea.main.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @author Arik
 */
public class PluginClassLoader extends URLClassLoader {
    private static final Log LOG = LogFactory.getLog(PluginClassLoader.class);
    private static final String[] ISOLATED_PREFIXES = new String[] {
                            "org.jdom"
                            };


    public PluginClassLoader(URL[] urls) {
        super(urls);
    }

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public PluginClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    /** @noinspection DuplicateStringLiteralInspection*/
    protected synchronized Class loadClass(String pClassName, boolean pResolveClass)
            throws ClassNotFoundException {

        //
        //if a JDOM class is searched for, use own own URLs for loading it
        //otherwise, perform standard lookup
        //
        Class c = findLoadedClass(pClassName);
        if (c == null) {
            try {
                if (LOG.isTraceEnabled())
                    LOG.trace("Class '" + pClassName + "' was not loaded yet - trying...");
                c = findClass(pClassName);

                if (pResolveClass)
                    resolveClass(c);

                if (LOG.isTraceEnabled())
                    LOG.trace("The class '" + pClassName + "' is now loaded by plugin classloader.");
            }
            catch (ClassNotFoundException e) {
                for(int i = 0; i < ISOLATED_PREFIXES.length; i++)
                    if(pClassName.startsWith(ISOLATED_PREFIXES[i]))
                        throw new ClassNotFoundException("Could not find class named '" + pClassName + "'");

                if (LOG.isTraceEnabled())
                    LOG.trace("Class not found in this class loader, trying parent...");
                c = super.loadClass(pClassName, pResolveClass);
                if (LOG.isTraceEnabled())
                    LOG.trace("Class '" + pClassName + "' was found in parent class loader.");
            }
        }
        else if (pResolveClass)
            resolveClass(c);

        return c;
    }
}
