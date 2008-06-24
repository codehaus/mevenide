/*
 *  Copyright 2008 mkleint.
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
package org.netbeans.modules.maven.indexer.api;

/**
 *
 * @author mkleint
 */
public final class RepositoryInfo {

    private String id;
    private String type;
    private String name;
    private String repositoryPath;
    private String repositoryUrl;
    private String indexUpdateUrl;

    public RepositoryInfo(String id, String type, String name, String repositoryPath,
            String repositoryUrl, String indexUpdateUrl) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.repositoryPath = repositoryPath;
        this.repositoryUrl = repositoryUrl;
        this.indexUpdateUrl = indexUpdateUrl;
        assert (isLocal() == true && isRemoteDownloadable() == true) != true : "XXXCannot have both local and remote index fields filled in."; //NOI18N
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
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

    public boolean isRemoteDownloadable() {
        return indexUpdateUrl != null;
    }
    
    public boolean isLocal() {
        return repositoryPath != null;
    }
    
}

