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
package org.mevenide.project.io;

import java.io.File;
import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.mevenide.context.IQueryContext;

/**
 *
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class TestQueryContext implements IQueryContext {
    private File projectDir;
    private File userDir;
    private HashMap userProps;
    private HashMap projectProps;
    private HashMap buildProps;
    
    TestQueryContext(File project, File user) {
        projectDir = project;
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
    
    public String getPropertyValue(String str) {
        String toReturn = getValue(userProps, str);
        if (toReturn == null) {
            toReturn = getValue(buildProps, str);
        }
        if (toReturn == null) {
            toReturn = getValue(projectProps, str);
        }
        return toReturn;
    }
    
    public java.io.File getUserDirectory() {
        return userDir;
    }
    
    public String getUserPropertyValue(String str) {
        return getValue(userProps, str);
    }
    
}
