/*
 *  Copyright 2008 Anuradha.
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
import java.util.List;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;


/**
 *
 * @author Anuradha G
 */
public class RepositoryPreferences {

    public static final RepositoryInfo LOCAL;
    public static final RepositoryInfo NETBEANS;
    private static  RepositoryPreferences instance;
    
    /**
     * index of local repository
     */
    public static final String LOCAL_REPO_ID = "local";
    
    static {
        LOCAL = new RepositoryInfo(LOCAL_REPO_ID, "Local Repository", null, null);
        NETBEANS = new RepositoryInfo("netbeans", "Netbeans Repository", "http://deadlock.netbeans.org/maven2/", "http://deadlock.netbeans.org/maven2/.index/netbeans/",true);
    }
    private String KEY_ID = "repository.id";
    private String KEY_NAME = "repository.name";
    private String KEY_INDEX_URL = "repository.index.url";
    private String KEY_REPO_URL = "repository.repo.url";
    

    private  RepositoryPreferences() {
    }
    
    private Preferences getPreferences() {
        return NbPreferences.root().node("org/codehaus/mevenide/nexus/indexing"); //NOI18N
    }

    public synchronized static RepositoryPreferences getInstance() {
        if(instance == null) {
            instance = new RepositoryPreferences();
        }
        return instance;
    }

    
    public  RepositoryInfo getRepositoryInfoById(String id) {
        for (RepositoryInfo ri : getRepositoryInfos()) {
          if (ri.getId().equals(id)) return ri;
        }
        return null;
    }

    public  List<RepositoryInfo> getRepositoryInfos() {
        List<RepositoryInfo> toRet = new ArrayList<RepositoryInfo>();
        toRet.add(LOCAL);
        toRet.add(NETBEANS);
        Preferences pref = getPreferences();
        int count = 0;
        String id = pref.get(KEY_ID + "." + count, null);
        while (id != null) {
            String name = pref.get(KEY_NAME + "." + count, null);
            String repourl = pref.get(KEY_REPO_URL + "." + count, null);
            String indexurl = pref.get(KEY_INDEX_URL + "." + count, null);
            RepositoryInfo info = new RepositoryInfo(id, name, repourl, indexurl, true);
            toRet.add(info);
            count = count + 1;
            id = pref.get(KEY_ID + "." + count, null);
        }
        return toRet;
    }
 
    
    public static class RepositoryInfo {

        private String id;
        private String name;
        private String repositoryUrl;
        private String indexUpdateUrl;
        private boolean remote;

        public RepositoryInfo(String id, String name, String repositoryUrl,
                String indexUpdateUrl) {
            this.id = id;
            this.name = name;
            this.repositoryUrl = repositoryUrl;
            this.indexUpdateUrl = indexUpdateUrl;

        }

        private RepositoryInfo(String id, String name, String repositoryUrl,
                String indexUpdateUrl, boolean remote) {
            this.id = id;
            this.name = name;
            this.repositoryUrl = repositoryUrl;
            this.indexUpdateUrl = indexUpdateUrl;
            this.remote = remote;
        }

        public String getId() {
            return id;
        }

        public String getIndexUpdateUrl() {
            return indexUpdateUrl;
        }

        public String getName() {
            return name;
        }

        public String getRepositoryUrl() {
            return repositoryUrl;
        }

        public boolean isRemote() {
            return remote;
        }
        
    }
}
