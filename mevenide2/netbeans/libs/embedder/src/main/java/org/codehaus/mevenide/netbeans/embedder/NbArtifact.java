/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.embedder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 *
 * @author mkleint
 */
public class NbArtifact implements Artifact {
    
    private Artifact original;
    private static Map cache = new HashMap();
    
    public static synchronized File getCachedPom(String id) {
        return (File)cache.get(id);
    }
    
    public static synchronized void putCachedPom(String id, File fil) {
        cache.put(id, fil);
    }
    
    /** Creates a new instance of NbArtifact */
    public NbArtifact(Artifact orig) {
        original = orig;
    }
    
    public String getGroupId() {
        return original.getGroupId();
    }
    
    public String getArtifactId() {
        return original.getArtifactId();
    }
    
    public String getVersion() {
        return original.getVersion();
    }
    
    public void setVersion(String version) {
        original.setVersion(version);
    }
    
    public String getScope() {
        return original.getScope();
    }
    
    public String getType() {
        return original.getType();
    }
    
    public String getClassifier() {
        return original.getClassifier();
    }
    
    public boolean hasClassifier() {
        return original.hasClassifier();
    }
    
    public File getFile() {
        if ("pom".equals(getType()) && isResolved()) {
            if (original.getFile() != null && !original.getFile().exists()) {
                File orig = NbArtifact.getCachedPom(getId());
                if (orig != null) {
                    original.setFile(orig);
                } else {
                    try {
                        File temp = File.createTempFile("mevenide", "pom");
                        temp.deleteOnExit();
                        PrintWriter writer = new PrintWriter(new FileOutputStream(temp));
                        writer.println("<project>");
                        writer.println("<modelVersion>4.0.0</modelVersion>");
                        writer.println("<packaging>pom</packaging>");
                        writer.println("<groupId>" + getGroupId() + "</groupId>");
                        writer.println("<artifactId>" + getArtifactId() + "</artifactId>");
                        writer.println("<version>" + getVersion() + "</version>");
                        writer.println("</project>");
                        writer.close();
                        original.setFile(temp);
                        NbArtifact.putCachedPom(getId(), temp);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
        }
        return original.getFile();
    }
    
    public void setFile(File destination) {
        original.setFile(destination);
    }
    
    public String getBaseVersion() {
        return original.getBaseVersion();
    }
    
    public void setBaseVersion(String baseVersion) {
        original.setBaseVersion(baseVersion);
    }
    
    public String getId() {
        return original.getId();
    }
    
    public String getDependencyConflictId() {
        return original.getDependencyConflictId();
    }
    
    public void addMetadata(ArtifactMetadata metadata) {
        original.addMetadata(metadata);
    }
    
    public Collection getMetadataList() {
        return original.getMetadataList();
    }
    
    public void setRepository(ArtifactRepository remoteRepository) {
        original.setRepository(remoteRepository);
    }
    
    public ArtifactRepository getRepository() {
        return original.getRepository();
    }
    
    public void updateVersion(String version, ArtifactRepository localRepository) {
        original.updateVersion(version, localRepository);
    }
    
    public String getDownloadUrl() {
        return original.getDownloadUrl();
    }
    
    public void setDownloadUrl(String downloadUrl) {
        original.setDownloadUrl(downloadUrl);
    }
    
    public ArtifactFilter getDependencyFilter() {
        return original.getDependencyFilter();
    }
    
    public void setDependencyFilter(ArtifactFilter artifactFilter) {
        original.setDependencyFilter(artifactFilter);
    }
    
    public ArtifactHandler getArtifactHandler() {
        return original.getArtifactHandler();
    }
    
    public List getDependencyTrail() {
        return original.getDependencyTrail();
    }
    
    public void setDependencyTrail(List dependencyTrail) {
        original.setDependencyTrail(dependencyTrail);
    }
    
    public void setScope(String scope) {
        original.setScope(scope);
    }
    
    public VersionRange getVersionRange() {
        return original.getVersionRange();
    }
    
    public void setVersionRange(VersionRange newRange) {
        original.setVersionRange(newRange);
    }
    
    public void selectVersion(String version) {
        original.selectVersion(version);
    }
    
    public void setGroupId(String groupId) {
        original.setGroupId(groupId);
    }
    
    public void setArtifactId(String artifactId) {
        original.setArtifactId(artifactId);
    }
    
    public boolean isSnapshot() {
        return original.isSnapshot();
    }
    
    public void setResolved(boolean resolved) {
        original.setResolved(resolved);
    }
    
    public boolean isResolved() {
        return original.isResolved();
    }
    
    public void setResolvedVersion(String version) {
        original.setResolvedVersion(version);
    }
    
    public void setArtifactHandler(ArtifactHandler handler) {
        original.setArtifactHandler(handler);
    }
    
    public boolean isRelease() {
        return original.isRelease();
    }
    
    public void setRelease(boolean release) {
        original.setRelease(release);
    }
    
    public List getAvailableVersions() {
        return original.getAvailableVersions();
    }
    
    public void setAvailableVersions(List versions) {
        original.setAvailableVersions(versions);
    }
    
    public boolean isOptional() {
        return original.isOptional();
    }
    
    public void setOptional(boolean optional) {
        original.setOptional(optional);
    }
    
    public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
        return original.getSelectedVersion();
    }
    
    public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
        return original.isSelectedVersionKnown();
    }
    
    public int compareTo(Object o) {
        return original.compareTo(o);
    }
    
    public String toString() {
        return original.toString();
    }

    public boolean equals(Object obj) {
        return original.equals(obj);
    }

    public int hashCode() {
        return original.hashCode();
    }
    
    
}
