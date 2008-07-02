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

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

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

    public static RepositoryInfo createRepositoryInfo(FileObject fo) {
        String type = (String) fo.getAttribute(RepositoryPreferences.KEY_TYPE);
        assert type != null;
        String id = fo.getName();
        String name = id;
        String remoteBundleName = (String) fo.getAttribute ("SystemFileSystem.localizingBundle"); // NOI18N
        if (remoteBundleName != null) {
            try {
                ResourceBundle bundle = NbBundle.getBundle (remoteBundleName);
                String nm = bundle.getString(fo.getPath());
                if (nm != null) {
                    name = nm;
                }
            } catch (MissingResourceException e) {
                //just ignore
            }
        }
        String path = (String) fo.getAttribute(RepositoryPreferences.KEY_PATH);
        String repourl =(String) fo.getAttribute(RepositoryPreferences.KEY_REPO_URL);
        String indexurl = (String) fo.getAttribute(RepositoryPreferences.KEY_INDEX_URL);
        return new RepositoryInfo(id, type, name, path, repourl, indexurl);
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

