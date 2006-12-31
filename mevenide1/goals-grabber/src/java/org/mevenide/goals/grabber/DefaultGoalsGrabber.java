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
package org.mevenide.goals.grabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * read goals from ${maven.home}/plugins/goals.cache file
 *
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class DefaultGoalsGrabber extends AbstractGoalsGrabber {
    private static Log log = LogFactory.getLog(DefaultGoalsGrabber.class);
    
    private ILocationFinder finder;
    private XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    
    public DefaultGoalsGrabber() throws Exception {
        this(ConfigUtils.getDefaultLocationFinder());
    }
    
    public DefaultGoalsGrabber(ILocationFinder find) throws Exception {
        finder = find;
        refresh();
    }
    
    public String getName() {
        return IGoalsGrabber.ORIGIN_PLUGIN;
    }
    
    public void refresh() throws Exception {
        super.refresh();
        
        //File pluginsLocal = new File(Environment.getMavenHome(), "plugins");
        File goalsCache = new File(finder.getMavenPluginsDir(), "goals.cache");
        if ( goalsCache.exists() ) {
            log.debug("Grabbing goals from : " + goalsCache.getAbsolutePath());
            
            Properties props = new Properties();
            props.load(new FileInputStream(goalsCache));
            
            Set fullyQualifiedGoalNames = props.keySet();
            Iterator iterator = fullyQualifiedGoalNames.iterator();
            
            while ( iterator.hasNext() ) {
                String goalName = (String) iterator.next();
                registerGoal(goalName, props.getProperty(goalName));
            }
            
        } else {
            log.debug("No goals.cache file found in : " + finder.getMavenPluginsDir());
            loadFromDir(finder.getPluginJarsDir());
            loadFromDir(finder.getUserPluginsDir());
        }
    }
    
    private void loadFromDir(String dirStr) throws Exception {
        if (dirStr != null) {
            File pluginDir = new File(dirStr);
            if (pluginDir.exists()) {
                File[] listFiles = pluginDir.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
                    parsePluginJar(listFiles[i]);
                }
            }
        }
    }
    
   private void parsePluginJar(File pluginJar) throws Exception {

        XmlPullParser parser = factory.newPullParser();
        InputStreamReader reader = null;
        try {
            JarFile fil = new JarFile(pluginJar);
            ZipEntry entry = fil.getEntry("plugin.jelly");
            if (entry != null) {
                reader = new InputStreamReader(fil.getInputStream(entry));
                parser.setInput( reader );
                
                int eventType = parser.getEventType();
                
                while ( eventType != XmlPullParser.END_DOCUMENT ) {
                    if ( eventType == XmlPullParser.START_TAG ) {
                        if ( parser.getName().equals("goal")) {
                            String fullyQualifiedName = parser.getAttributeValue(null, "name");
                            String prereqs = parser.getAttributeValue(null, "prereqs");
                            String description = parser.getAttributeValue(null, "description");
                            registerGoal(fullyQualifiedName, description+">"+prereqs);
                        }
                    }
                    eventType = parser.next();
                }
            }
        } finally {
            if ( reader != null ) {
                reader.close();
            }
        }
    }    
    
}

