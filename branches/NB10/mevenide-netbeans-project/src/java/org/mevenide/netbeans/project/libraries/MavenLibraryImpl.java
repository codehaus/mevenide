/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.project.libraries;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import org.netbeans.spi.project.libraries.LibraryImplementation;

/**
 * Implementation of LibraryImplementation that maps one artifact from maven repository.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenLibraryImpl implements LibraryImplementation {
    
    private String description;
    private String name;
    private String locBundle;
    private PropertyChangeSupport support;
    private List content = Collections.EMPTY_LIST;
    private List javadocContent = Collections.EMPTY_LIST;
    private List srcContent = Collections.EMPTY_LIST;
    private String artifactID;
    private String groupID;
    private String type;
    private String version;
    
    /** Creates a new instance of MavenLibraryImpl */
    MavenLibraryImpl(String art, String gr, String ver, String tp) {
        support = new PropertyChangeSupport(this);
        type = tp;
        version = ver;
        artifactID = art;
        groupID = gr;
    }
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.removePropertyChangeListener(propertyChangeListener);
    }
    
    public List getContent(String str) {
        if ("classpath".equals(str)) {
            return content;
        }
        if ("src".equals(str)) {
            return srcContent;
        }
        if ("javadoc".equals(str)) {
            return javadocContent;
        }
        return Collections.EMPTY_LIST;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getLocalizingBundle() {
        return locBundle;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return "j2se";
//        return MavenRepoLibraryProvider.TYPE;
    }
    
    
    public void setContent(String str, List list) {
        if ("classpath".equals(str)) {
            content = list;
        } else if ("src".equals(str)) {
            srcContent = list;
        } else if ("javadoc".equals(str)) {
            javadocContent = list;
        } else {
            throw new IllegalArgumentException("what to do with type=" + str);
        }
    }
    
    public void setDescription(String str) {
        description = str;
    }
    public void setLocalizingBundle(String str) {
        locBundle = str;
    }
    
    public void setName(String str) {
        name = str;
    }
    
    
    public String getArtifactID() {
        return artifactID;
    }
    
    public String getGroupID() {
        return groupID;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getMavenArtifactType() {
        return type;
    }
}

