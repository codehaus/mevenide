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

package org.mevenide.plugins;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
class PluginInfoParser {
    private static Log logger = LogFactory.getLog(PluginInfoParser.class);
    
    private File cachedDir;
    private File artToPluginFile;
//    private File validCacheFile;
    
    private long artToPluginFileTimestamp = 0;
//    private long validCacheFileTimestamp = 0;
    
    private Properties artToPluginModel;
    private Set infoList;
    
    /** Creates a new instance of PluginInfoParser */
    public PluginInfoParser(File cacheDir) {
        cachedDir = cacheDir;
        artToPluginFile = new File(cachedDir, "artifactIdToPlugin.cache");
//        validCacheFile = new File(cachedDir, "valid.cache");
        artToPluginModel = new Properties();
        infoList = new TreeSet(new Comp());
    }
    
    File getCachedDir() {
        return cachedDir;
    }
    
    private void checkReloadModel() {
        if (artToPluginFile == null || !artToPluginFile.exists()) {
            //TODO read from maven plugin directory with jars..
            // shall we clear or is better to keep possibly previously cached values?
            artToPluginModel.clear();
            infoList.clear();
            return;
        }
        long lastModified = artToPluginFile.lastModified();
        if (lastModified > artToPluginFileTimestamp) {
            artToPluginModel.clear();
            infoList.clear();
            BufferedInputStream stream = null;
            try {
                stream = new BufferedInputStream(new FileInputStream(artToPluginFile));
                artToPluginModel.load(stream);
                Enumeration en = artToPluginModel.propertyNames();
                while (en.hasMoreElements()) {
                    String art = (String)en.nextElement();
                    DefaultPluginInfo info = new DefaultPluginInfo(cachedDir);
                    info.setArtifactId(art);
                    info.setName(artToPluginModel.getProperty(art));
                    info.setVersion(info.getArtifactId().substring(info.getName().length() + 1));
                    infoList.add(info);
                }
            } catch (IOException exc) {
                logger.error("Cannot read file", exc);
            }
            finally {
                artToPluginFileTimestamp = lastModified;
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException exc) {
                        //ignore
                        logger.error("Cannot close file", exc);
                    }
                }
            }
        }
    } 
    
     synchronized IPluginInfo[] getInfos() {
        checkReloadModel();
        IPluginInfo[] infos = new IPluginInfo[infoList.size()];
        return (IPluginInfo[])infoList.toArray(infos);
    }
    
    private static class Comp implements Comparator {
        public int compare(Object obj, Object obj1) {
            IPluginInfo change1 = (IPluginInfo)obj;
            IPluginInfo change2 = (IPluginInfo)obj1;
            return change1.getName().compareTo(change2.getName());        }
        
    }
    
}
