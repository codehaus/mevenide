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
package org.mevenide.ui.netbeans.loader;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mevenide.ui.netbeans.MavenPropertyFiles;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class PropFilesCookieImpl implements MavenPropertyFiles
{

    private static final String BUILD_PROP_FILENAME = "build.properties";
    private static final String PROJECT_PROP_FILENAME = "project.properties";
    private static final String USER_PROP_FILENAME = "build.properties";

    /** key = FileObject
     *  value listener
     */
    private HashMap map;
    
    private File projectFile;
    /** Holds value of property projectPropFile. */
    private File projectPropFile;    
    
    /** Holds value of property projectBuildFile. */
    private File projectBuildFile;
    
    /** Holds value of property userBuildFile. */
    private File userBuildFile;
    
    private PropertyChangeSupport support;
    
    private FileObjectListener listener;
    private FileObjectListener listenerUser;
    private FileObjectListener listenerBuild;
    
    /** Creates a new instance of PropFilesCookieImpl */
    public PropFilesCookieImpl()
    {
        support = new PropertyChangeSupport(this);
        map = new HashMap(5);
    }
    
    /** Getter for property projectPropFile.
     * @return Value of property projectPropFile.
     *
     */
    public File getProjectPropFile()
    {
        return this.projectPropFile;
    }
    
    /** Setter for property projectPropFile.
     * @param projectPropFile New value of property projectPropFile.
     *
     */
    public void setProjectFile(File projectFile, File userHome)
    {
        this.projectFile = projectFile;
        File parent = projectFile.getParentFile();
        projectPropFile = new File(parent, PROJECT_PROP_FILENAME);
        projectBuildFile = new File(parent, BUILD_PROP_FILENAME);
        userBuildFile = new File(userHome, USER_PROP_FILENAME);
        listener = new FileObjectListener(PROP_PROJECT);
        listenerBuild = new FileObjectListener(PROP_PROJECT_BUILD);
        listenerUser = new FileObjectListener(PROP_USER_BUILD);
        //TODO remove listeners form current set.
        if (map.size() > 0)
        {
            map.clear();
        }
        if (projectPropFile.exists())
        {
            FileObject[] projectPropFO = FileUtil.fromFile(projectPropFile);
            for (int i = 0; i < projectPropFO.length; i++)
            {
                projectPropFO[i].addFileChangeListener(listener);
                map.put(projectPropFO[i], listener);
            }
        } else {
            projectPropFile = null;
        }
        if (projectBuildFile.exists())
        {
            FileObject[] projectBuildFO = FileUtil.fromFile(projectBuildFile);
            for (int i = 0; i < projectBuildFO.length; i++)
            {
                projectBuildFO[i].addFileChangeListener(listenerBuild);
                map.put(projectBuildFO[i], listenerBuild);
            }
        } else {
            projectBuildFile = null;
        }
        if (userBuildFile.exists())
        {
            FileObject[] userBuildFO = FileUtil.fromFile(userBuildFile);
            for (int i = 0; i < userBuildFO.length; i++)
            {
                userBuildFO[i].addFileChangeListener(listenerUser);
                map.put(userBuildFO[i], listenerUser);
            }
        } else {
            userBuildFile = null;
        }
        firePropertyChange(PROP_PROJECT_DEF, null, projectFile);
    }
    
    /** Getter for property projectBuildFile.
     * @return Value of property projectBuildFile.
     *
     */
    public File getProjectBuildFile()
    {
        return this.projectBuildFile;
    }
    
    
    /** Getter for property userBuildFile.
     * @return Value of property userBuildFile.
     *
     */
    public File getUserBuildFile()
    {
        return this.userBuildFile;
    }
    
    /** Setter for property userBuildFile.
     * @param userBuildFile New value of property userBuildFile.
     *
     */
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }    
    
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }
    
    protected void firePropertyChange(String property, File oldValue, File newValue)
    {
        support.firePropertyChange(property, oldValue, newValue);
    }
    
    public FileObject[] getProjectPropFO()
    {
        return getFileObjects(listener);
    }
    
    public FileObject[] getProjectBuildFO()
    {
        return getFileObjects(listenerBuild);
    }
    
    public FileObject[] getUserBuildFO()
    {
        return getFileObjects(listenerUser);
    }

    private FileObject[] getFileObjects(FileObjectListener value)
    {
        Set entrySet = map.entrySet();
        Collection toReturn = new ArrayList(entrySet.size());
        Iterator it = entrySet.iterator();
        while (it.hasNext())
        {
            Map.Entry ent = (Map.Entry)it.next();
            if (ent.getValue().equals(value)) {
                toReturn.add(ent.getKey());
            }
        }
        FileObject[] fos = new FileObject[toReturn.size()];
        if (fos.length > 0)
        {
            fos = (FileObject[])toReturn.toArray(fos);
        }
        return fos;
    }
    
    private class FileObjectListener extends  FileChangeAdapter
    {
        private String prop;
        
        public FileObjectListener(String property)
        {
            prop = property;
        }
        
        public String getProperty()
        {
            return prop;
        }
        
        public void fileDeleted(FileEvent fileEvent)
        {
            super.fileDeleted(fileEvent);
        }
        
        public void fileRenamed(FileRenameEvent fileRenameEvent)
        {
            super.fileRenamed(fileRenameEvent);
        }
        
        public void fileChanged(FileEvent fileEvent)
        {
            super.fileChanged(fileEvent);
        }
        
    }
}
