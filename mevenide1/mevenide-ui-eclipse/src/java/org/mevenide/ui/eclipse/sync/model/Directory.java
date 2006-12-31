/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
package org.mevenide.ui.eclipse.sync.model;

import org.apache.maven.project.Project;
import org.mevenide.project.source.SourceDirectoryUtil;
import org.mevenide.util.ResolverUtils;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class Directory {
    private String path;
    private String type;
    
    private Project project;
    
    
    public Directory(Project project) {
        this.project = project;
    }
    
    /** 
     * we dont want here to compare the type of Directory since this is method 
     * is heavily used in the synchronizer. 
     * If you want the type to be taken into account use equalsStrict instead   
     */
    public boolean equals(Object obj) {
    	if ( !(obj instanceof Directory) ) {
    		return false;
    	}
    	Directory dir = (Directory) obj;
    	return ResolverUtils.getInstance().resolve(project, getCleanPath()).equals(ResolverUtils.getInstance().resolve(project, dir.getCleanPath())); 
	}
    
    public boolean equalsStrict(Object obj) {
    	if ( !(obj instanceof Directory) ) {
    		return false;
    	}
    	Directory dir = (Directory) obj;
    	return equals(obj) && type.equals(dir.type); 
	}
    
    public String getPath() {
        return path;
    }
    
    String getCleanPath() {
        return SourceDirectoryUtil.stripBasedir(path).replaceAll("\\\\", "/");  //$NON-NLS-1$//$NON-NLS-2$
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    
    
}

