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
import java.util.prefs.Preferences;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.openide.util.NbPreferences;

/**
 *
 * @author Anuradha G
 */
public class RepositoryPreferences {

    public static final RepositoryInfo LOCAL;
    public static final RepositoryInfo NETBEANS;
    public static final RepositoryInfo CENTRAL;
    private static RepositoryPreferences instance;
    /**
     * index of local repository
     */
    public static final String LOCAL_REPO_ID = "local";//NOI18N
    

    static {
        LOCAL = new RepositoryInfo(LOCAL_REPO_ID, "Local Repository",
                EmbedderFactory.getProjectEmbedder().getLocalRepository().getBasedir(),null, null,false);//NOI18N
        NETBEANS = new RepositoryInfo("netbeans", "Netbeans Repository",null,
                "http://deadlock.netbeans.org/maven2/",
                "http://deadlock.netbeans.org/maven2/.index/netbeans/", true);//NOI18N
        CENTRAL = new RepositoryInfo("central", "Central  Repository",null,
                "http://repo1.maven.org/maven2",
                "http://repo1.maven.org/maven2/.index/", true);//NOI18N
    }
    private String KEY_ID = "repository.id";//NOI18N
    private String KEY_NAME = "repository.name";//NOI18N
    private String KEY_PATH = "repository.path";//NOI18N
    private String KEY_INDEX_URL = "repository.index.url";//NOI18N
    private String KEY_REPO_URL = "repository.repo.url";//NOI18N
    private String KEY_REPO_REMOTE = "repository.repo.remote";//NOI18N
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
        toRet.add(LOCAL);
        toRet.add(CENTRAL);
        toRet.add(NETBEANS);
        Preferences pref = getPreferences();
        int count =  toRet.size();
        String id = pref.get(KEY_ID + "." + count, null);
        while (id != null) {
            String name = pref.get(KEY_NAME + "." + count, null);
            String path = pref.get(KEY_PATH + "." + count, null);
            String repourl = pref.get(KEY_REPO_URL + "." + count, null);
            String indexurl = pref.get(KEY_INDEX_URL + "." + count, null);
            boolean remote = pref.getBoolean(KEY_REPO_REMOTE + "." + count, true);
            RepositoryInfo info = new RepositoryInfo(id, name,path, repourl, indexurl, remote);
            toRet.add(info);
            count = count + 1;
            id = pref.get(KEY_ID + "." + count, null);
        }
        return toRet;
    }
    /*prevent concurrunt accsess*/

    public synchronized void addRepositoryInfo(RepositoryInfo info) {
        if (getRepositoryInfoById(info.id) == null) {
            int id = getRepositoryInfos().size();
            Preferences pref = getPreferences();
            pref.put(KEY_ID + "." + id, info.id);
            pref.put(KEY_NAME + "." + id, info.name);
            pref.putBoolean(KEY_REPO_REMOTE + "." + id, info.remote);
            if (info.getRepositoryPath() != null) {
                pref.put(KEY_PATH+ "." + id, info.repositoryPath);
            }
            if (info.getRepositoryUrl() != null) {
                pref.put(KEY_REPO_URL + "." + id, info.repositoryUrl);
            }
            if (info.getIndexUpdateUrl() != null) {
                pref.put(KEY_INDEX_URL + "." + id, info.indexUpdateUrl);
            }
        //todo fire repository added
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

    /*Repository Info */
    public static class RepositoryInfo {

        private String id;
        private String name;
        private String repositoryPath;
        private String repositoryUrl;
        private String indexUpdateUrl;
        private boolean remote;

        public RepositoryInfo(String id, String name, String repositoryPath,
                String repositoryUrl, String indexUpdateUrl, boolean remote) {
            this.id = id;
            this.name = name;
            this.repositoryPath = repositoryPath;
            this.repositoryUrl = repositoryUrl;
            this.indexUpdateUrl = indexUpdateUrl;
            this.remote = remote;
        }

        

        public String getId() {
            return id;
        }


        public String getName() {
            return name;
        }

        public String getRepositoryPath() {
            return repositoryPath;
        }

        
        public String getRepositoryUrl() {
            return repositoryUrl;
        }
        
        public String getIndexUpdateUrl() {
            return indexUpdateUrl;
        }
        public boolean isRemote() {
            return remote;
        }
    }
}
