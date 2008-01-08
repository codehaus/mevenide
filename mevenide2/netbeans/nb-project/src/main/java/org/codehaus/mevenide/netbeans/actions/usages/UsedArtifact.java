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

package org.codehaus.mevenide.netbeans.actions.usages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Anuradha
 */
public class UsedArtifact {
 private String name;
 private List<UsedVersion> usedVersions=new  ArrayList<UsedVersion>();
    public UsedArtifact(String name) {
        this.name = name;
    }

    public boolean removeUsedVersion(Object o) {
        return usedVersions.remove(o);
    }

    public boolean addAllUsedVersions(Collection<? extends UsedVersion> c) {
        return usedVersions.addAll(c);
    }

    public boolean addUsedVersion(UsedVersion e) {
        return usedVersions.add(e);
    }

    public List<UsedVersion> getUsedVersions() {
        return new  ArrayList<UsedVersion>(usedVersions);
    }

    public String getName() {
        return name;
    }
  
}
