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

package org.codehaus.mevenide.indexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Anuradha
 */
public class ArtifactInfo {
 private String name;
 private List<VersionInfo> versionInfos=new  ArrayList<VersionInfo>();
    public ArtifactInfo(String name) {
        this.name = name;
    }

    public boolean removeVersionInfo(Object o) {
        return versionInfos.remove(o);
    }

    public boolean addAlVersionInfos(Collection<? extends VersionInfo> c) {
        return versionInfos.addAll(c);
    }

    public boolean addVersionInfo(VersionInfo e) {
        return versionInfos.add(e);
    }

    public List<VersionInfo> getVersionInfos() {
        return new  ArrayList<VersionInfo>(versionInfos);
    }

    public String getName() {
        return name;
    }
  
}
