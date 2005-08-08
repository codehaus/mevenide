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
package org.mevenide.project.io;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import org.mevenide.context.AbstractQueryContext;
import org.mevenide.context.IProjectContext;

/**
 *
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class TestQueryContext extends AbstractQueryContext {
    private File projectDir;
    private File userDir;
    private HashMap userProps;
    private HashMap projectProps;
    private HashMap buildProps;
    private HashMap parentProjectProps;
    private HashMap parentBuildProps;
    private IProjectContext projectContext;
    
    TestQueryContext(File projectDirectory, File user) {
        projectDir = projectDirectory;
        userDir = user;
    }
    
    public void setUserProps(HashMap keyvalues) {
        userProps = keyvalues;
    }
    public void setProjectProps(HashMap keyvalues) {
        projectProps = keyvalues;
    }
    public void setBuildProps(HashMap keyvalues) {
        buildProps = keyvalues;
    }
    
    public void setProjectContext(IProjectContext projs) {
        projectContext = projs;
    }
    
    
    private String getValue(HashMap map, String key) {
        if (map != null) {
            return (String)map.get(key);
        }
        return null;
    }
    
    public String getBuildPropertyValue(String str) {
        return getValue(buildProps, str);
    }
    
    public java.io.File getProjectDirectory() {
        return projectDir;
    }
    
    public String getProjectPropertyValue(String str) {
        return getValue(projectProps, str);
    }
    
    public java.io.File getUserDirectory() {
        return userDir;
    }
    
    public String getUserPropertyValue(String str) {
        return getValue(userProps, str);
    }
    
    public IProjectContext getPOMContext() {
        return projectContext;
    }    
    
    /**
     * all property keys defined in userdir/build.properties
     */
    public Set getUserPropertyKeys() {
        return userProps.keySet();
    }
    /**
     * all property keys defined in build.properties
     */
    public Set getBuildPropertyKeys() {
        return buildProps.keySet();
    }
    /**
     * all property keys defined in project.properties
     */
    public Set getProjectPropertyKeys() {
        return projectProps.keySet();
    }

    public String getParentProjectPropertyValue(String key) {
        return getValue(parentProjectProps, key);
    }

    public Set getParentProjectPropertyKeys() {
        return parentProjectProps.keySet();
    }

    public String getParentBuildPropertyValue(String key) {
        return getValue(parentBuildProps, key);
    }

    public Set getParentBuildPropertyKeys() {
        return parentBuildProps.keySet();
    }

    public Set getPropertyKeysAt(int location) {
        return getMapForLocation(location).keySet();
    }

    public String getPropertyValueAt(String key, int location) {
        return (String)getMapForLocation(location).get(key);
    }
    
    private HashMap getMapForLocation(int loc) {
        HashMap toRet;
        switch (loc) {
            case 10 : toRet = projectProps; break;
            case 11 : toRet = buildProps; break;
            case 12 : toRet = userProps; break;
            case 20 : toRet = parentProjectProps; break;
            case 21 : toRet = parentBuildProps; break;
            default : toRet = new HashMap();
        }
        return toRet;
    }
    
}
