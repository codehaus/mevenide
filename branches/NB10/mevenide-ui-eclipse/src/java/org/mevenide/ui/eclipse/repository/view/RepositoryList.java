/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.repository.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryList {
    
    private RepositoryList() { }
    
    private static final String MAVEN_REPOSITORIES = "MAVEN_REPOSITORIES";
    private static final String MAVEN_MIRRORS = "MAVEN_MIRRORS";
    
    public static final String MAIN_MAVEN_REPO = "http://www.ibiblio.org/maven/";
    
    public static final List DEFAULT_REPOSITORIES = new ArrayList();
    public static final List MIRRORS = new ArrayList();
    
    static {
        //@todo use repos defined in mavenRepositories.properties
        DEFAULT_REPOSITORIES.add(MAIN_MAVEN_REPO);
        DEFAULT_REPOSITORIES.add("http://cvs.apache.org/repository/");
        DEFAULT_REPOSITORIES.add("http://maven-plugins.sourceforge.net/maven/");
        DEFAULT_REPOSITORIES.add("http://seasar.sourceforge.jp/maven/");
        DEFAULT_REPOSITORIES.add("http://spring-ext.sourceforge.jp/maven/");
        DEFAULT_REPOSITORIES.add("http://ibiblio.org/geotools/");
        DEFAULT_REPOSITORIES.add("http://www.codeczar.com/maven/");
        
        MIRRORS.add("http://mirrors.sunsite.dk/maven/");
        MIRRORS.add("http://ftp.up.ac.za/pub/linux/maven/");
        MIRRORS.add("http://download.au.kde.org/pub/maven/");
        MIRRORS.add("http://public.planetmirror.com/pub/maven/");
        MIRRORS.add("http://public.www.planetmirror.com/pub/maven/");
        MIRRORS.add("http://smokeping.planetmirror.com/pub/maven/");
        MIRRORS.add("http://horde.planetmirror.com/pub/maven/");
        MIRRORS.add("http://curl.planetmirror.com/pub/maven/");
        MIRRORS.add("http://python.planetmirror.com/pub/maven/");
    }
    
    public static List getDefaultRepositoryList() {
        return Collections.unmodifiableList(DEFAULT_REPOSITORIES);
    }
    
    public static List getDefaultMirrorList() {
        return Collections.unmodifiableList(MIRRORS);
    }
    
    public static List getUserDefinedRepositories() {
        return getRepositoryList(MAVEN_REPOSITORIES, new ArrayList(DEFAULT_REPOSITORIES));
    }
    
    public static List getUserDefinedMirrors() {
        return getRepositoryList(MAVEN_MIRRORS, new ArrayList(MIRRORS));
    }
    
    private static List getRepositoryList(String preferenceKey, List defaultList) {
        String repos = getPreferenceStore().getString(preferenceKey);
        
        List repositories = new ArrayList();
        
        if ( !StringUtils.isNull(repos) ) {
            repositories = new ArrayList(Arrays.asList(org.apache.commons.lang.StringUtils.split(repos, ",")));
        }
        else {
            repositories = defaultList;
        }
        return repositories;
    }
    
    public static boolean containsDefaultRepository(String repository) {
        return DEFAULT_REPOSITORIES.contains(repository);
    }
    
    public static void saveUserDefinedRepositories(List repositories) {
        String serializedRepos = "";
        for (Iterator it = repositories.iterator(); it.hasNext();) {
            serializedRepos += it.next() + ",";
        }
        getPreferenceStore().setValue(MAVEN_REPOSITORIES, serializedRepos);
        commitChanges();
    }
    
    public static void resetToDefaultRepositories() {
        getPreferenceStore().setToDefault(MAVEN_REPOSITORIES);
        commitChanges();
    }

    /**
     * Saves the changes made to preferences.
     * @return <tt>true</tt> if the preferences were saved
     */
    private static boolean commitChanges() {
        try {
            getPreferenceStore().save();
            return true;
        } catch (IOException e) {
            Mevenide.displayError("Unable to save preferences.", e);
        }
        return false;
    }

    /**
     * @return the preference store to use in this object
     */
    private static IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
    
}
