/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.newproject;

/**
 *
 * @author mkleint
 */
public class Archetype {
    
    private String artifactId;
    private String groupId;
    private String version;
    private String name;
    private String description;
    /** Creates a new instance of Archetype */
    public Archetype() {
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int hashCode() {
        return getGroupId().hashCode() + 10 * getArtifactId().hashCode() + 22 * getVersion().hashCode();
    }
    
    public boolean equals(Object obj) {
        Archetype ar1 = (Archetype)obj;
        int gr = ar1.getGroupId().trim().compareTo(getGroupId().trim());
        if (gr != 0) {
            return false;
        }
        int ar = ar1.getArtifactId().trim().compareTo(getArtifactId().trim());
        if (ar != 0) {
            return false;
        }
        return ar1.getVersion().trim().equals(getVersion().trim());
    }
    
}
