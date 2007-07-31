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
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * abstract plugininfo implementation
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
abstract class AbstractPluginInfo implements IPluginInfo {
    private final static Logger LOGGER = Logger.getLogger(AbstractPluginInfo.class.getName());
   
    private String artifactId;
    private String version;
    private String name;
    private String longName;
    private String description;
    private Properties props;
    private PluginProperty[] enhanced;
    
    /** Creates a new instance of DefaultPluginInfo */
    AbstractPluginInfo() {
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    void setArtifactId(String artifact) {
        artifactId = artifact;
    }
    
    public String getVersion() {
        return version;
    }
    
    void setVersion(String vers) {
        version = vers;
    }
    
    public String getName() {
        return name;
    }
    
    void setName(String nm) {
        name = nm;
    }
    
    public String getDescription() {
        if (description == null) {
            readProjectValues();
        }
        return description;
    }
    
    public String getLongName() {
        if (longName == null) {
            readProjectValues();
        }
        return longName;
    }
    
    public Set getPropertyKeys() {
        if (props == null) {
            if (enhanced == null) {
                // try load enhanced;
                getEnhancedPropertyInfo();
            }
            if (enhanced.length > 0) {
                HashSet set = new HashSet(enhanced.length);
                for (int i = 0; i < enhanced.length; i++) {
                    set.add(enhanced[i].getName());
                }
                return set;
            }
            props = new Properties();
            InputStream stream = getPluginPropsFileInputStream();
            if (stream != null) {
                try {
                    stream = new BufferedInputStream(stream);
                    props.load(stream);
                } catch (Exception exc) {
                    LOGGER.log(Level.SEVERE,"Cannot read", exc);
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Exception exc) {
                            LOGGER.log(Level.SEVERE, "Cannot close", exc);
                        }
                    }
                }
            }
        }
        return new HashSet(props.keySet());
    }
    
   /**
     * Set of <code>PluginProperty></code> instances
     */
    public Set getEnhancedPropertyInfo() {
        if (enhanced == null) {
            PluginProperty[] pros = null;
            ICustomPluginLoader loader = PluginInfoFactory.getInstance().getCustomLoader();
            if (loader != null) {
                pros = loader.loadProperties(getName(), getVersion(), false);
            }
            if (pros == null) {
                // not found;
                pros = new PluginProperty[0];
            } 
            enhanced = pros;
        }
        return new HashSet(Arrays.asList(enhanced));
    }
    
    /**
     * get the input stream for the project.xml file of teh plugin.
     * for subclasses to implement.
     */
    protected abstract InputStream getProjectFileInputStream();
    
    /**
     * input stream for plugin.properties file.
     */
    protected abstract InputStream getPluginPropsFileInputStream();
    
    
    protected final void readProjectValues() {
        readProjectValues(getProjectFileInputStream());
    }
    /**
     * package private because of tests
     */
    void readProjectValues(InputStream projectInputStream) {
        BufferedReader read = null;
        String desc = "";
        String nm = "";
        try {
            read = new BufferedReader(new InputStreamReader(projectInputStream));
            String line = read.readLine();
            int descState = 0;
            int nameState = 0;
            while (line != null && (nameState < 3 || descState < 3)) {
                if (descState == 0) {
                    int index = line.indexOf("<shortDescription>"); //NOI18N
                    if (index > -1) {
                        desc = line.substring(index + "<shortDescription>".length()).trim(); //NOI18N
                        descState = 1;
                    }
                }
                if (descState == 1 || descState == 2) {
                    if (descState == 2)  {
                         desc = desc + " " + line.trim();
                    }
                    descState = 2;
                    int index = desc.indexOf("</shortDescription>"); //NOI18N
                    if (index > -1) {
                        desc = desc.substring(0, index);
                        descState = 3;
                    }
                }
                if (nameState == 0) {
                    String lowercase = line.toLowerCase();
                    int index = lowercase.indexOf("<name>"); //NOI18N
                    if (index > -1) {
                        nm = line.substring(index + "<name>".length()).trim(); //NOI18N
                        nameState = 1;
                    }
                }
                if (nameState == 1 || nameState == 2) {
                    if (nameState == 2) {
                        nm = nm + " " + line.trim();
                    }
                    int index = nm.indexOf("</name>"); //NOI18N
                    if (index > -1) {
                        nm = nm.substring(0, index);
                        nameState = 3;
                    }
                }
                line = read.readLine();
            }
        } catch (Exception exc) {
            LOGGER.log(Level.SEVERE, "Cannot read", exc);            
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException exc) {
                    LOGGER.log(Level.SEVERE, "Cannot close", exc);            
                }
            }
        }
        longName = nm;
        description = desc;
        if (name == null) {
            name = nm;
        }
    }
    
}
