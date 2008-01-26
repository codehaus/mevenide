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

/**
 *
 * @author Anuradha G
 */
public class RepositoryPreferences {

    public static final RepositoryInfo LOCAL;
    private static  RepositoryPreferences instance;
    
    /**
     * index of local repository
     */
    public static final String LOCAL_REPO_ID = "local";
    private List<RepositoryPreferences.RepositoryInfo> repositoryInfos=
            new  ArrayList<RepositoryPreferences.RepositoryInfo>();
    
    static {
        LOCAL = new RepositoryInfo(LOCAL_REPO_ID, "Local Repository", null, null);
    }

    private  RepositoryPreferences() {
        //todo add central
        //repositoryInfos.add(new RepositoryInfo("Central", "Central", "http://repo1.maven.org/maven2/", "http://repo1.maven.org/maven2/"));
    }

    public synchronized static RepositoryPreferences getInstance() {
        if(instance==null)
        {
         instance=new RepositoryPreferences();
        }
        return instance;
    }

    
    public  RepositoryInfo getRepositoryInfoById(String id) {
        //first check on default

        if (LOCAL.id.equals(id)) {
            return LOCAL;
        }
        for (RepositoryInfo ri : repositoryInfos) {
          if(ri.getId().equals(id))return ri;
        }



        return null;
    }

    public  List<RepositoryInfo> getRepositoryInfos() {
               return repositoryInfos;
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
