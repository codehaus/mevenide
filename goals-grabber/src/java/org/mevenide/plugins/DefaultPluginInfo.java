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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class DefaultPluginInfo implements IPluginInfo {
     private static Log logger = LogFactory.getLog(DefaultPluginInfo.class);
   
    private String artifactId;
    private String version;
    private String name;
    private File cacheDir;
    private String longName;
    private String description;
    private Properties props;
    
    /** Creates a new instance of DefaultPluginInfo */
    DefaultPluginInfo(File mavenCacheDir) {
        cacheDir = mavenCacheDir;
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    void setArtifactId(String artifact) {
        this.artifactId = artifact;
    }
    
    public String getVersion() {
        return version;
    }
    
    void setVersion(String vers) {
        this.version = vers;
    }
    
    public String getName() {
        return name;
    }
    
    void setName(String nm) {
        this.name = nm;
    }
    
    public String getDescription() {
        if (description == null) {
            readProjectValues(getProjectFile());
        }
        return description;
    }
    
    public String getLongName() {
        if (longName == null) {
            readProjectValues(getProjectFile());
        }
        return longName;
    }
    
    public Set getPropertyKeys() {
        if (props == null) {
            props = new Properties();
            File fil = new File(cacheDir, getArtifactId() + File.separator + "plugin.properties");
            if (fil.exists()) {
                InputStream stream = null;
                try {
                    stream = new BufferedInputStream(new FileInputStream(fil));
                    props.load(stream);
                } catch (Exception exc) {
                    logger.error("Cannot read");
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Exception exc) {
                            
                        }
                    }
                }
            }
        }
        return new HashSet(props.keySet());
    }
    
    private File getProjectFile() {
        return new File(cacheDir, getArtifactId() + File.separator + "project.xml");
    }
    
    void readProjectValues(File fil) {
        BufferedReader read = null;
        String desc = "";
        String nm = "";
        try {
            read = new BufferedReader(new InputStreamReader(new FileInputStream(fil)));
            String line = read.readLine();
            int descState = 0;
            int nameState = 0;
            while (line != null && (nameState < 3 || descState < 3)) {
                if (descState == 0) {
                    int index = line.indexOf("<shortDescription>");
                    if (index > -1) {
                        desc = line.substring(index + "<shortDescription>".length()).trim();
                        descState = 1;
                    }
                }
                if (descState == 1 || descState == 2) {
                    if (descState == 2)  {
                         desc = desc + " " + line.trim();
                    }
                    descState = 2;
                    int index = desc.indexOf("</shortDescription>");
                    if (index > -1) {
                        desc = desc.substring(0, index);
                        descState = 3;
                    }
                }
                if (nameState == 0) {
                    String lowercase = line.toLowerCase();
                    int index = lowercase.indexOf("<name>");
                    if (index > -1) {
                        nm = line.substring(index + "<name>".length()).trim();
                        nameState = 1;
                    }
                }
                if (nameState == 1 || nameState == 2) {
                    if (nameState == 2) {
                        nm = nm + " " + line.trim();
                    }
                    int index = nm.indexOf("</name>");
                    if (index > -1) {
                        nm = nm.substring(0, index);
                        nameState = 3;
                    }
                }
                line = read.readLine();
            }
        } catch (Exception exc) {
            
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException exc) {
                    
                }
            }
        }
        longName = nm;
        description = desc;
        
    }
    
}
