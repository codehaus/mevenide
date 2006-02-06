/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans.embedder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.maven.settings.DefaultMavenSettingsBuilder;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author mkleint
 */
public class MavenSettingsSingleton {
    private static MavenSettingsSingleton instance;
    private SettingsXpp3Reader builder;
    /** Creates a new instance of MavenSettingsSingleton */
    private MavenSettingsSingleton() {
        builder = new SettingsXpp3Reader();
    }
    
    public static synchronized MavenSettingsSingleton getInstance() {
        if (instance == null) {
            instance = new MavenSettingsSingleton();
        }
        return instance;
    }
    /**
     * the location of ${user.home}/.m2
     */
    public File getM2UserDir() {
        return new File(System.getProperty("user.home"), ".m2");
    }
    
    public Settings getSettings() {
        //TODO need probably some kind of caching.. 
        // currently just localrepository is ever accessed.
        Settings sets = null;
        File dir = getM2UserDir();
        try {
            File fil = new File(dir, "settings.xml");
            if (fil.exists()) {
                sets = builder.read(new InputStreamReader(new FileInputStream(fil)));
            } 
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        }
        if (sets == null) {
            sets = new Settings();
        }
        if (sets.getLocalRepository() == null) {
            sets.setLocalRepository(new File(dir, "repository").toString());
        }
        return sets;
    }
    
}
