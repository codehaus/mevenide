package org.mevenide.idea.main;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.main.settings.global.GlobalSettings;
import org.mevenide.idea.main.util.PluginClassLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Arik
 */
public abstract class ModelDelegatingComponent {
    private static final Log LOG = LogFactory.getLog(ModelDelegatingComponent.class);
    private static final ClassLoader MODEL_CLASS_LOADER;

    static {
        MODEL_CLASS_LOADER = createClassLoader();
    }

    protected ModelDelegatingComponent(final String pModelClassName) {
        try {
            final Class modelClass = MODEL_CLASS_LOADER.loadClass(pModelClassName);
            final Object model = modelClass.newInstance();
            setModel(model);
        }
        catch (ClassNotFoundException e) {
            Messages.showErrorDialog(e.getMessage(), UI.ERR_TITLE);
            LogFactory.getLog(this.getClass()).error(e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
            Messages.showErrorDialog(e.getMessage(), UI.ERR_TITLE);
            LogFactory.getLog(this.getClass()).error(e.getMessage(), e);
        }
        catch (InstantiationException e) {
            Messages.showErrorDialog(e.getMessage(), UI.ERR_TITLE);
            LogFactory.getLog(this.getClass()).error(e.getMessage(), e);
        }

    }

    private static ClassLoader createClassLoader() {

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
            final ClassLoader parent = GlobalSettings.class.getClassLoader();
            return new PluginClassLoader(urls, parent);
        }
        catch (MalformedURLException e) {
            final Throwable ex = new IllegalStateException(e.getMessage()).initCause(e);
            throw (IllegalStateException) ex;
        }
    }

    protected abstract void setModel(final Object pModelInstance);
}
