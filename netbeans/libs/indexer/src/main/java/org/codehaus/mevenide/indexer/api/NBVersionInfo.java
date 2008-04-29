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

/**
 *
 * @author Anuradha G
 */
public class NBVersionInfo {

    private String groupId;
    private String artifactId;
    private String version;
    private String type;
    private String packaging;
    private String projectName;
    private String classifier;
    private String ProjectDescription;
    private String repoId;
    
    //-----
    private boolean sourcesExists;
    private boolean javadocExists;
    private boolean signatureExists;

    public NBVersionInfo(String repoId,String groupId, String artifactId, String version,
            String type, String packaging, String projectName,String ProjectDescription,String classifier) {
        this.repoId = repoId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.packaging = packaging;
        this.projectName = projectName;
        this.ProjectDescription = ProjectDescription;
        this.classifier = classifier;
    }

    public String getRepoId() {
        return repoId;
    }

    public boolean isJavadocExists() {
        return javadocExists;
    }

    public void setJavadocExists(boolean javadocExists) {
        this.javadocExists = javadocExists;
    }

    public boolean isSignatureExists() {
        return signatureExists;
    }

    public void setSignatureExists(boolean signatureExists) {
        this.signatureExists = signatureExists;
    }

    public boolean isSourcesExists() {
        return sourcesExists;
    }

    public void setSourcesExists(boolean sourcesExists) {
        this.sourcesExists = sourcesExists;
    }
    
  
    

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getPackaging() {
        return packaging;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectDescription() {
        return ProjectDescription;
    }

    public String getClassifier() {
        return classifier;
    }
 
    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version + ":" + repoId;
    }
    
}
