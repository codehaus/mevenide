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
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public interface MavenPropertyFiles extends Node.Cookie
{
    public String PROP_PROJECT_DEF     = "project.xml file"; //NOI18N
    public String PROP_PROJECT         = "project.property file"; //NOI18N
    public String PROP_PROJECT_BUILD   = "build.property project file"; //NOI18N
    public String PROP_USER_BUILD      = "build.property user file"; //NOI18N

    
    public void setProjectFile(File projectPropFile, File userHome);
    
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
   
    /** Getter for property projectPropFile.
     * @return Value of property projectPropFile.
     *
     */
    public java.io.File getProjectPropFile();
   /**
     * returns fileobject, if exists, otherwise empty array
     */
    public FileObject[] getProjectPropFO();    
    
    
    /** Getter for property projectBuildFile.
     * @return Value of property projectBuildFile.
     *
     */
    public java.io.File getProjectBuildFile();
    /**
     * returns fileobject, if exists, otherwise empty array
     */
    public FileObject[] getProjectBuildFO();
    
    /** Getter for property userBuildFile.
     * @return Value of property userBuildFile.
     *
     */
    public java.io.File getUserBuildFile();
    /**
     * returns fileobject, if exists, otherwise empty array
     */
    public FileObject[] getUserBuildFO();
    
    
}
