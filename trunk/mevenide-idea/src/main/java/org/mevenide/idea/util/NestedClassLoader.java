package org.mevenide.idea.util;

import com.intellij.openapi.application.PathManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.global.MavenManager;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * A classloader that first checks if a class being loaded is found in its own URLs, and if so it loads it,
 * regardless of whether the parent has it or not.
 *
 * <p>This provides isolation from interferences from parent classloaders that might have different versions
 * of various dependencies.</p>
 *
 * <p>This classloader is also capable of isolating certain packages or prefixes, to completely prevent
 * conflicting versions of classes to be accidently loaded from the parents. This means that if a class that
 * fits the isolated prefix is attempted to be loaded, and is not found within this classloader, the parent is
 * not searched for and a {@link ClassNotFoundException} is thrown.</p>
 *
 * @author Arik
 */
public class NestedClassLoader extends URLClassLoader {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(NestedClassLoader.class);


    /**
     * Isolated prefixes that will not be searched in parent classloaders.
     */
    private static final String[] ISOLATED_PREFIXES = new String[]{"org.jdom"};

    /**
     * The singleton instance.
     */
    private static NestedClassLoader INSTANCE = null;

    /**
     * Creates an instance using the given URLs.
     *
     * @param urls the urls of this class loader.
     */
    protected NestedClassLoader(final URL[] urls) {
        super(urls);
    }

    /**
     * Creates an instance using the given URLs and parent classloader.
     *
     * @param urls   the urls of this class loader.
     * @param parent the parent classloader
     */
    protected NestedClassLoader(final URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Creates an instance using the given URLs, parent classloader and a URL stream handler factory.
     *
     * @param urls    the urls of this class loader.
     * @param parent  the parent classloader
     * @param factory the factory to use for obtaining URL streams
     */
    protected NestedClassLoader(final URL[] urls,
                                final ClassLoader parent,
                                final URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    /**
         * Loads the specified class.
         *
         * <p>The sequence of the search is as follows:</p> <ol> <li>the class is searched in this class loader's
         * URLs</li> <li>if found, it is returned</li> <li>otherwise, if the class belongs to an isolated prefix,
         * a {@link ClassNotFoundException} is thrown.</li> <li>otherwise, the parent is delegated to</li> </ol>
         */
    protected synchronized Class loadClass(final String pClassName,
                                           final boolean pResolveClass)
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
                for (int i = 0; i < ISOLATED_PREFIXES.length; i++)
                    if (pClassName.startsWith(ISOLATED_PREFIXES[i]))
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

    /**
     * Returns the classloader singleton instance used for isolating Mevenide.
     *
     * @return classloader instance
     */
    public static ClassLoader getInstance() {

        //
        //locate all jars in the plugin's "resources" dir
        //
        final String pluginsPath = PathManager.getPluginsPath();
        final File pluginsDir = new File(pluginsPath, "mevenide-idea/resources");
        File[] jars = pluginsDir.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jar");
                    }
                });
        if (jars == null) jars = new File[0];

        try {
            //
            //convert the File instances to URLs
            //
            final URL[] urls = new URL[jars.length];
            for (int i = 0; i < jars.length; i++) {
                final File jar = jars[i];
                urls[i] = jar.toURL();
            }

            //
            //create a classloader for them
            //
            final ClassLoader parent = MavenManager.class.getClassLoader();
            return new NestedClassLoader(urls, parent);
        }
        catch (MalformedURLException e) {
            final Throwable ex = new IllegalStateException(e.getMessage()).initCause(e);
            throw (IllegalStateException) ex;
        }
    }
}
