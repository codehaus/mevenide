/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.template.model;

import org.apache.maven.project.Project;
import org.eclipse.core.runtime.IAdaptable;

/**
 * A template is just a wrapper for a Project object
 * 
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated
 *         by $Author$
 * @version $Id$
 */
public class Template implements IAdaptable {

    private Project fProject;

    public Template(Project project) {
        fProject = project;
    }

    public String getTemplateName() {
        return fProject.getName();
    }

    public Project getProject() {
        return fProject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}