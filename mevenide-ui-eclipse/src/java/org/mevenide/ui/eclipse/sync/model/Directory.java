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
package org.mevenide.ui.eclipse.sync.model;

import org.mevenide.project.ProjectConstants;
import org.mevenide.project.source.SourceDirectoryUtil;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class Directory {
    private String path;
    private String type;
    
    public Directory() {
    }
    
    public String getDisplayPath() {
    	if ( path.equals(ProjectConstants.BASEDIR) || path.equals("") ) {
            return ProjectConstants.BASEDIR;
        }
        return path;
    }
    
    public String getPath() {
        return path;
    }
    
    String getCleanPath() {
        return SourceDirectoryUtil.stripBasedir(path);
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
