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
public final class Archetype {
    
    private String artifactId;
    private String groupId;
    private String version;
    private String name;
    private String description;
    private String repository;
    boolean deletable;
    
    /** Creates a new instance of Archetype */
    public Archetype(boolean deletable) {
        this.deletable = deletable;
    }
    
    public Archetype() {
        this(true);
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
    
    /**
     * optional property.
     */ 
    public void setRepository(String repo) {
        repository = repo;
    }
    
    /**
     * optional property.
     */ 
    public String getRepository() {
        return repository;
    }
    
    public int hashCode() {
        return getGroupId().trim().hashCode() + 13 * getArtifactId().trim().hashCode() + 23 * getVersion().trim().hashCode();
    }
    
    public boolean equals(Object obj) {
        Archetype ar1 = (Archetype)obj;
        boolean gr = ar1.getGroupId().trim().equals(getGroupId().trim());
        if (!gr) {
            return false;
        }
        boolean ar = ar1.getArtifactId().trim().equals(getArtifactId().trim());
        if (!ar) {
            return false;
        }
        boolean ver =  ar1.getVersion().trim().equals(getVersion().trim());
        return ver;
    }

}
