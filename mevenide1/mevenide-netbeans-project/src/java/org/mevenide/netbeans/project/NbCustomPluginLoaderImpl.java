/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */


package org.mevenide.netbeans.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.plugins.ICustomPluginLoader;
import org.mevenide.plugins.PluginProperty;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;

/**
 * netbeans implementation of the goal-grabber's ICustomPluginLoader, finds the
 * custom plugin properties definition file in the system filesystem (layers).
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public final class NbCustomPluginLoaderImpl implements ICustomPluginLoader {
    private static Logger LOGGER = Logger.getLogger(NbCustomPluginLoaderImpl.class.getName());
    
    private static final String ROOT = "Plugins/Maven/PluginProperties"; //NOI18N
    
    /** Creates a new instance of NbCustomPluginLoaderImpl */
    public NbCustomPluginLoaderImpl() {
    }

    public PluginProperty[] loadProperties(String plugin, String version, boolean exactMatch) {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject root = fs.findResource(ROOT);
        String match = plugin + "-" + version;
        if (root != null) {
            Enumeration en = root.getChildren(false);
            FileObject candidate = null;
            while (en.hasMoreElements()) {
                FileObject fo = (FileObject)en.nextElement();
                String name = fo.getName();
                if (exactMatch && name.equalsIgnoreCase(match)) {
                    candidate = fo;
                    break;
                }
                if (!exactMatch) {
                    if (name.startsWith(plugin)) {
                        String vers = name.substring(plugin.length() + 1);
                        //TODO - have some logic to use the latest version before the one required
                        candidate = fo;
                    }
                }
            }
            if (candidate != null) {
                return loadProps(candidate);
            }
        }
        return null;
    }
    
    private PluginProperty[] loadProps(FileObject fo) {
        List toReturn = null;
        InputStream stream = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            stream = fo.getInputStream();
            Document document = builder.build(stream);
            Element root = document.getRootElement();
            List categories = root.getChildren("category"); //NOI18N
            Iterator it = categories.iterator();
            toReturn = new ArrayList();
            while (it.hasNext()) {
                Element cat = (Element)it.next();
                List props = cat.getChildren("property"); //NOI18N
                Iterator it2 = props.iterator();
                while (it2.hasNext()) {
                    Element pr = (Element)it2.next();
                    PluginProperty prop = new PluginProperty(
                            pr.getAttributeValue("name"),  //NOI18N
                            pr.getAttributeValue("label"),  //NOI18N
                            pr.getAttributeValue("default"),  //NOI18N
                            Boolean.getBoolean(pr.getAttributeValue("required")),  //NOI18N
                            pr.getAttributeValue("description")  //NOI18N
                    );
                    toReturn.add(prop);
                }
                    
            }
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "Exception while reading file " + fo.getPath(), exc);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException x) {
                    LOGGER.log(Level.SEVERE, "Cannot close", x);
                }
            }
        }
        if (toReturn != null) {
            PluginProperty[] ret = new PluginProperty[toReturn.size()];
            return (PluginProperty[])toReturn.toArray(ret);
        }
        return null;
    }
    
}
