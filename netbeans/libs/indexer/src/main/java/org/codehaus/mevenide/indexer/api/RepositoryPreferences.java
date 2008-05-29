/*
 *  Copyright 2005-2008 Mevenide Team.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.indexer.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Anuradha G
 */
public final class RepositoryPreferences {

    private static final RepositoryInfo LOCAL;
    private static final RepositoryInfo CENTRAL;
    private static final RepositoryInfo JAVANET;
    private static RepositoryPreferences instance;
    /**
     * index of local repository
     */
    public static final String LOCAL_REPO_ID = "local";//NOI18N
    
    //TODO - move elsewhere, implementation detail??
    public static final String TYPE_NEXUS = "nexus"; //NOI18N
    

    static {
        LOCAL = new RepositoryInfo(LOCAL_REPO_ID, TYPE_NEXUS, "Local Repository",
                EmbedderFactory.getProjectEmbedder().getLocalRepository().getBasedir(),null, null);//NOI18N
        CENTRAL = new RepositoryInfo("central", TYPE_NEXUS, "Central  Repository",null,
                "http://repo1.maven.org/maven2",
                "http://repo1.maven.org/maven2/.index/");//NOI18N
        JAVANET = new RepositoryInfo("java.net2", TYPE_NEXUS, "Java.net Repository",null,
                "http://download.java.net/maven/2/",
                "http://download.java.net/maven/2/.index/");//NOI18N
    }
    private static final String KEY_ID = "repository.id";//NOI18N
    private static final String KEY_TYPE = "repository.type";//NOI18N
    private static final String KEY_NAME = "repository.name";//NOI18N
    private static final String KEY_PATH = "repository.path";//NOI18N
    private static final String KEY_INDEX_URL = "repository.index.url";//NOI18N
    private static final String KEY_REPO_URL = "repository.repo.url";//NOI18N
    private static final String KEY_REMOVED = "repository.removed"; //NOI18N
    /*index settings */
    public static final String PROP_INDEX_FREQ = "indexUpdateFrequency"; //NOI18N
    public static final String PROP_LAST_INDEX_UPDATE = "lastIndexUpdate"; //NOI18N
    public static final String PROP_SNAPSHOTS = "includeSnapshots"; //NOI18N
    public static final int FREQ_ONCE_WEEK = 0;
    public static final int FREQ_ONCE_DAY = 1;
    public static final int FREQ_STARTUP = 2;
    public static final int FREQ_NEVER = 3;
    //---------------------------------------------------------------------------
    private RepositoryPreferences() {
    }

    private Preferences getPreferences() {
        return NbPreferences.root().node("org/codehaus/mevenide/nexus/indexing"); //NOI18N
    }

    public synchronized static RepositoryPreferences getInstance() {
        if (instance == null) {
            instance = new RepositoryPreferences();
            //not very nice but need the repos to be inserted when not present
            // and to to overwrite potencial edits.
            // still not clear how to allow people to delete central or netbeans
            instance.addDefaultRepositoryInfo(LOCAL);
            instance.addDefaultRepositoryInfo(CENTRAL);
            instance.addDefaultRepositoryInfo(JAVANET);
        }
        return instance;
    }

    public RepositoryInfo getRepositoryInfoById(String id) {
        for (RepositoryInfo ri : getRepositoryInfos()) {
            if (ri.getId().equals(id)) {
                return ri;
            }
        }
        return null;
    }

    public List<RepositoryInfo> getRepositoryInfos() {
        List<RepositoryInfo> toRet = new ArrayList<RepositoryInfo>();
        Preferences pref = getPreferences();
        try {
            String[] keys = pref.keys();
            for (String key : keys) {
                if (!key.startsWith(KEY_ID)) {
                    continue;
                }
                String id = pref.get(key, null);
                String name = pref.get(KEY_NAME + "." + id, null);
                String type = pref.get(KEY_TYPE + "." + id, null);
                String path = pref.get(KEY_PATH + "." + id, null);
                String repourl = pref.get(KEY_REPO_URL + "." + id, null);
                String indexurl = pref.get(KEY_INDEX_URL + "." + id, null);
                RepositoryInfo info = new RepositoryInfo(id, type, name, path, repourl, indexurl);
                toRet.add(info);
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return toRet;
    }

    /**
     * 
     * @param info
     */
    public synchronized void addOrModifyRepositoryInfo(RepositoryInfo info) {
        Preferences pref = getPreferences();
        pref.put(KEY_ID + "." + info.getId(), info.getId());
        pref.put(KEY_TYPE + "." + info.getId(), info.getType());
        pref.put(KEY_NAME + "." + info.getId(), info.getName());
        if (info.getRepositoryPath() != null) {
            pref.put(KEY_PATH + "." + info.getId(), info.getRepositoryPath());
        } else {
            pref.remove(KEY_PATH + "." + info.getId());
        }
        if (info.getRepositoryUrl() != null) {
            pref.put(KEY_REPO_URL + "." + info.getId(), info.getRepositoryUrl());
        } else {
            pref.remove(KEY_REPO_URL + "." + info.getId());
        }
        if (info.getIndexUpdateUrl() != null) {
            pref.put(KEY_INDEX_URL + "." + info.getId(), info.getIndexUpdateUrl());
        } else {
            pref.remove(KEY_INDEX_URL + "." + info.getId());
        }
        pref.remove(KEY_REMOVED + "." + info.getId());
        //todo fire repository added
    }
    
    /**
     * To be used from modules adding default instances of repositories.
     * Such repository will only be really added if not present yet and not removed by user.
     * @param info
     */
    public synchronized void addDefaultRepositoryInfo(RepositoryInfo info) {
        Preferences pref = getPreferences();
        if (pref.getBoolean(KEY_REMOVED + "." + info.getId(), false)) {
            //user removed the setting.
            return;
        }
        if (getRepositoryInfoById(info.getId()) != null) {
            //user possibly changed the setting..
            return;
        }
        addOrModifyRepositoryInfo(info);
    }
    
    public void removeRepositoryInfo(RepositoryInfo info) {
        if (getRepositoryInfoById(info.getId()) != null) {
            Preferences pref = getPreferences();
            pref.remove(KEY_ID + "." + info.getId());
            pref.remove(KEY_TYPE + "." + info.getId());
            pref.remove(KEY_NAME + "." + info.getId());
            pref.remove(KEY_PATH + "." + info.getId());
            pref.remove(KEY_REPO_URL + "." + info.getId());
            pref.remove(KEY_INDEX_URL + "." + info.getId());
            pref.putBoolean(KEY_REMOVED + "." + info.getId(), true);
        }
    }

    

    public void setIndexUpdateFrequency(int fr) {
        getPreferences().putInt(PROP_INDEX_FREQ, fr);
    }

    public int getIndexUpdateFrequency() {
        return getPreferences().getInt(PROP_INDEX_FREQ, FREQ_ONCE_WEEK);
    }

    public Date getLastIndexUpdate(String repoId) {
        return new Date(getPreferences().getLong(PROP_LAST_INDEX_UPDATE+"."+repoId, 0));
    }

    public void setLastIndexUpdate(String repoId,Date date) {
        getPreferences().putLong(PROP_LAST_INDEX_UPDATE+"."+repoId, date.getTime());
    }

    public boolean isIncludeSnapshots() {
        return getPreferences().getBoolean(PROP_SNAPSHOTS, true);
    }

    public void setIncludeSnapshots(boolean includeSnapshots) {
        getPreferences().putBoolean(PROP_SNAPSHOTS, includeSnapshots);
    }

}
