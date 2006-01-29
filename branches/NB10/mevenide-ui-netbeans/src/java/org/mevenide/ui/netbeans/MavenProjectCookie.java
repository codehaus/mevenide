/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.netbeans;

import java.beans.PropertyChangeListener;
import java.io.File;
import org.apache.maven.project.Project;
import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface MavenProjectCookie extends  Node.Cookie
{
    public String FILENAME_BUILD = "build.properties"; //NOI18N
    public String FILENAME_PROJECT = "project.properties"; //NOI18N
    public String FILENAME_MAVEN = "maven.xml"; //NOI18N
  
    public static final String PROP_PROJECT = "Project"; //NOI18N    
    /**
     * ${project.home}/project.xml
     */
    File getProjectFile();
    /**
     * ${project.home}/project.properties
     */
    File getProjectPropFile();
    /**
     * ${project.home}/build.properties
     */
    File getProjectBuildPropFile();
    /**
     * ${user.home}/build.properties
     */
    File getUserBuildPropFile();
    /**
     * ${project.home}/maven.xml
     */
    File getMavenCustomFile();
    
//    /**
//     * project version
//     */
//    String getVersion();
    
    /**
     * an ID:VERSION pair string for display of MavenProjectNode.
     * will not load org.apache.maven.Project.
     */
    String getProjectName();
    
    /**
     * gets properties of project, will load org.apache.maven.Project
     */
    
    Node.Property[] getProperties();
    
    /**
     returns org.apache.maven.Project
     */
    Project getMavenProject();
    
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
    
    /**
     * TODO: throws a specialized exception when the project file was changed and data would be lost.
     */
    void reloadProject() throws Exception;

}
 