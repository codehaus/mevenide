/* ==========================================================================
 * Copyright (c) 2003-2005 Mevenide Team
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

package org.mevenide.repository;

import java.net.URI;

/**
 * Data placeholder for local and remote repository information.
 * Can contain incomplete values, the means of getting more complete data
 * is calling getChildren()
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class RepoPathElement {
    /**
     * constant returned from getLevel(), 
     * denotes an instance that has nothing filled out.
     * <p>value = {@value}</p>
     */
    public static final int LEVEL_ROOT = 0;
    /**
     * constant returned from getLevel(),
     * denotes an instance that has at least the groupId filled out.
     * <p>value = {@value}</p>
     */
    public static final int LEVEL_GROUP = 1;
    /**
     * constant returned from getLevel()
     * denotes an instance that has at least the groupId and type filled out.
     * <p>value = {@value}</p>
     */
    public static final int LEVEL_TYPE = 2;
    /**
     * constant returned from getLevel()
     * denotes an instance that has at least the groupId, type and artifactId filled out.
     * <p>value = {@value}</p>
     */
    public static final int LEVEL_ARTIFACT = 3;
    /**
     * constant returned from getLevel()
     * denotes an instance that has all fields filled out.
     * <p>value = {@value}</p>
     */
    public static final int LEVEL_VERSION = 4;
    
    private String groupId;
    private String artifactId;
    private String version;
    private String type;
    private String extension;
    private IRepositoryReader reader;
    private RepoPathElement   parent;
    private RepoPathElement[] children;
    
    /** Creates a new instance of RepoPathElement */
    public RepoPathElement(IRepositoryReader reader) {
        this.reader = reader;
    }
    
    /** Creates a new instance of RepoPathElement */
    public RepoPathElement(IRepositoryReader reader, RepoPathElement parent) {
        this.reader = reader;
        this.parent = parent;
    }

    public RepoPathElement(IRepositoryReader read,
                           RepoPathElement parent,
                           String groupId, 
                           String type, 
                           String version, 
                           String artifactId, 
                           String ext) {
        this(read, parent);
        setGroupId(groupId);
        setType(type);
        setVersion(version);
        setArtifactId(artifactId);
        setExtension(ext);
    }

    public RepoPathElement getParent() {
        return this.parent;
    }

    public String getGroupId() {
        return groupId;
    }

    void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }
    
    public String getExtension() {
        return extension;
    }

    void setExtension(String extension) {
        this.extension = extension;
    }    
    
    /**
     * Returns true if all it's fields are filled out, identifying the
     * artifact in an exact manner.
     */
    public boolean isLeaf() {
        return getLevel() == LEVEL_VERSION;
    }
    
    /**
     * Returns true if this node represents the root repository.
     */
    public boolean isRoot() {
        return getLevel() == LEVEL_ROOT;
    }
    
    boolean isRepoDirectory() {
        return getLevel() == LEVEL_ROOT 
            || getLevel() == LEVEL_GROUP 
            || getLevel() == LEVEL_TYPE;
    }
    
    /**
     *
     */
    public int getLevel() {
        if (groupId == null) {
            return LEVEL_ROOT;
        }
        if (type == null) {
            return LEVEL_GROUP;
        }
        if (artifactId == null) {
            return LEVEL_TYPE;
        }
        if (version == null) {
            return LEVEL_ARTIFACT;
        }
        return LEVEL_VERSION;
    }
    
    String getPartialURIPath() {
       StringBuffer buf = new StringBuffer();
       if (groupId != null) {
           buf.append(groupId);
           if (type != null) {
               buf.append("/");
               buf.append(type);
               buf.append("s");
           }
       }
       return buf.toString();
    }
    
    /**
     * Get path to artifact relative to the root of repository.
     * Really only makes sense for leaf elements.
     */
    public String getRelativeURIPath() {
        StringBuffer buf = new StringBuffer();
        if (groupId != null) {
            buf.append(groupId);
            if (type != null) {
                buf.append("/");
                buf.append(type);
                buf.append("s");
                if (artifactId != null) {
                    buf.append("/");
                    buf.append(artifactId);
                    if (version != null) {
                        buf.append("-");
                        buf.append(version);
                    }
                    if ("java-source".equals(type)) {
                       buf.append("-sources.jar"); 
                    } else {
                        if (extension != null) {
                            buf.append(".");
                            buf.append(extension);
                        }
                    }
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * Returns the absolute URI to the root repository of this element.
     * 
     * Makes the most sense for leaf elements.
     */
    public URI getRootURI() {
        return reader.getRootURI();
    }
 
    /**
     * Returns the absolute URI to this element.
     * 
     * Makes the most sense for leaf elements.
     */
    public URI getURI() {
        String root = getRootURI().toString();
        if (!root.endsWith("/")) {
            root = root + "/";
        }
        return URI.create(root + getRelativeURIPath());
    }
 
    /**
     * Get an array of RepoPathElements that share the fields
     * of the current one and have additional differentiating
     * information. Eg. for an instance with specified groupId will return 
     * an array with same groupId and the all existing types. 
     */
    public RepoPathElement[] getChildren() throws Exception {
        // have some refreshing here?
        if (children == null) {
            children = reader.readElements(this);
        } 
        return children;
    }
    
    void setChildren(RepoPathElement[] childs) {
        children = childs;
    } 

    public boolean isRemote() {
        return reader.getRootURI().toString().startsWith("http://");
    }
    
    public IRepositoryReader getReader() {
        return reader;
    }

    public void reset() {
        this.children = null;
    }

    public boolean isLoaded() {
        return this.children != null;
    }
}
