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

import java.io.File;

import org.apache.maven.project.Project;
import org.mevenide.project.io.ProjectReader;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomContainer {
    private Object[] nodes;
    
    private Project project;
    private EclipseContainerContainer parent;
    private File pomFile;
    
    public PomContainer(File projectFile) throws Exception {
        this.pomFile = projectFile;
        this.project = ProjectReader.getReader().read(projectFile);
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }

    
    public Object[] getNodes() {
        return nodes;
    }

    public void setNodes(Object[] nodes) {
        this.nodes = nodes;
    }

	public EclipseContainerContainer getParent() {
	    return parent;
	}
	
	public void setParent(EclipseContainerContainer parent) {
	    this.parent = parent;
	}

    public File getPomFile() {
        return pomFile;
    }

    public void setPomFile(File pomFile) {
        this.pomFile = pomFile;
    }

}
