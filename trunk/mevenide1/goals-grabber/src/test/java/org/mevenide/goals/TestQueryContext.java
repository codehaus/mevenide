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

package org.mevenide.goals;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import org.apache.maven.project.Project;
import org.jdom.Element;
import org.jdom.DefaultJDOMFactory;
import org.jdom.JDOMFactory;
import org.mevenide.context.AbstractQueryContext;
import org.mevenide.context.IProjectContext;

/**
 * interface to externalize the project files content/models to one place, and reuse
 * in all the queries (ILocationFinder, I PropertyResolver, etc)
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public class TestQueryContext extends AbstractQueryContext {
    
    private File projectDir;
    private File userDir;
    private HashMap userMap = new HashMap();
    private HashMap buildMap = new HashMap();
    private HashMap projectMap = new HashMap();
    private HashMap parentProjectMap = new HashMap();
    private HashMap parentBuildMap = new HashMap();
    
    private IProjectContext projectContext = new EmptyProjectContext();
    
    public TestQueryContext() {
        setUserDirectory(new File(System.getProperty("user.home")));
    }
    
    public String getBuildPropertyValue(String key) {
        return (String)buildMap.get(key);
    }
    
    public void addBuildPropertyValue(String key, String value) {
        buildMap.put(key, value);
    }
    
    public File getProjectDirectory() {
       return projectDir; 
    }
    
    public String getProjectPropertyValue(String key) {
        return (String)projectMap.get(key);
    }
    
    public void addProjectPropertyValue(String key, String value) {
        projectMap.put(key, value);
    }
    
    
    public String getUserPropertyValue(String key) {
        return (String)userMap.get(key);
    }
    
    public void addUserPropertyValue(String key, String value) {
        userMap.put(key, value);
    }
    
    
    public File getUserDirectory() {
        return userDir;
    }
    
    public IProjectContext getPOMContext() {
        return projectContext;
    } 
    
    public void setPOMContext(IProjectContext context) {
        projectContext = context;
    }

    public Set getBuildPropertyKeys() {
        return buildMap.keySet();
    }

    public Set getProjectPropertyKeys() {
        return projectMap.keySet();
    }

    public Set getUserPropertyKeys() {
        return userMap.keySet();
    }
 
    public void setProjectDirectory(File projectDir) {
        this.projectDir = projectDir;
    }

    public void setUserDirectory(File userDir) {
        this.userDir = userDir;
    }    
    
    
    // is this necesaary, maybe just return null from getRootProjectElement()
    private static JDOMFactory factory = new DefaultJDOMFactory();   

    public String getParentProjectPropertyValue(String key) {
        return (String)parentProjectMap.get(key);
    }

    public Set getParentProjectPropertyKeys() {
        return parentProjectMap.keySet();
    }

    public String getParentBuildPropertyValue(String key) {
        return (String)parentBuildMap.get(key);
    }

    public Set getParentBuildPropertyKeys() {
        return parentBuildMap.keySet();
    }
    
    public void addParentProjectPropertyValue(String key, String value) {
        parentProjectMap.put(key, value);
    }
    
    public void addParentBuildPropertyValue(String key, String value) {
        parentBuildMap.put(key, value);
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
            case 10 : toRet = projectMap; break;
            case 11 : toRet = buildMap; break;
            case 12 : toRet = userMap; break;
            case 20 : toRet = parentProjectMap; break;
            case 21 : toRet = parentBuildMap; break;
            default : toRet = new HashMap();
        }
        return toRet;
    }
    
    private class EmptyProjectContext implements IProjectContext {
        private Project empty = new Project();
        private Element rootEl = factory.element("project");
        public Project getFinalProject() {
            return empty;
        }
        
        public File[] getProjectFiles() {
            return new File[0];
        }
        
        public Project[] getProjectLayers() {
            return new Project[0];
        }
        
        public org.jdom.Element[] getRootElementLayers() {
            return new Element[0];
        }
        
        public org.jdom.Element getRootProjectElement() {
            return rootEl;
        }

        public int getProjectDepth() {
            return 0;
        }
        
        
    }

 
}
