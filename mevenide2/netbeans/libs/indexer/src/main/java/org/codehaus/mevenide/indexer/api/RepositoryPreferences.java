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
   public static final List<RepositoryInfo> DEFAULT=new  ArrayList<RepositoryPreferences.RepositoryInfo>(3);
   public static final String LOCAL_REPO_ID="local";
   static {
    DEFAULT.add(new RepositoryInfo(LOCAL_REPO_ID, "Local Repository", null, null, true));
   
   }
    
    
    public static RepositoryInfo getRepositoryInfoById(String id) {
        //first check on default
        for (RepositoryInfo info : DEFAULT) {
            if(info.id.equals(id))return info;
        }

        return null;
    }

    public static class RepositoryInfo {

        private String id;
        private String name;
        private String repositoryUrl;
        private String indexUpdateUrl;
        private boolean system;

        public RepositoryInfo(String id, String name, String repositoryUrl,
                String indexUpdateUrl) {
            this.id = id;
            this.name = name;
            this.repositoryUrl = repositoryUrl;
            this.indexUpdateUrl = indexUpdateUrl;

        }

        private  RepositoryInfo(String id, String name, String repositoryUrl,
                String indexUpdateUrl, boolean system) {
            this.id = id;
            this.name = name;
            this.repositoryUrl = repositoryUrl;
            this.indexUpdateUrl = indexUpdateUrl;
            this.system = system;
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

       
    }
}
