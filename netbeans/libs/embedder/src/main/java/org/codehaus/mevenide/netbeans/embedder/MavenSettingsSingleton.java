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
import org.apache.maven.embedder.ConfigurationValidationResult;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.profiles.ProfilesRoot;
import org.apache.maven.profiles.io.xpp3.ProfilesXpp3Reader;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * a workaround for the fact that one cannot access the settings values the embedder is using.
 * nice thing to do would be to have access to 1. the merged global/user settings for retrieval of used values
 * 2. the model of user settings for reading/writing in UI.
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
        return FileUtil.normalizeFile(new File(System.getProperty("user.home"), ".m2"));
    }
    
    /**
     * this method  should rather use the embedder's settings, however there's no clear
     * way of retrieving/using them.
     * @deprecated rather not use, doesn't contain the global setting values
     */
    @Deprecated
    public Settings getSettings() {
        //TODO need probably some kind of caching..
        Settings sets = createUserSettingsModel();
        if (sets.getLocalRepository() == null) {
            sets.setLocalRepository(new File(getM2UserDir(), "repository").toString());
        }
        return sets;
    }
    
    public Settings createUserSettingsModel() {
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
        return sets;
    }
    
    public static ProfilesRoot createProfilesModel(FileObject projectDir) {
        FileObject profiles = projectDir.getFileObject("profiles.xml");
        ProfilesRoot prof = null;
        if (profiles != null) {
            InputStreamReader read = null;
            try {
                read = new InputStreamReader(profiles.getInputStream());
                prof = new ProfilesXpp3Reader().read(read);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (XmlPullParserException ex) {
                ex.printStackTrace();
            } finally {
                IOUtil.close(read);
            }
        } 
        if (prof == null) {
            prof = new ProfilesRoot();
        }
        return prof;
    }
    
}
