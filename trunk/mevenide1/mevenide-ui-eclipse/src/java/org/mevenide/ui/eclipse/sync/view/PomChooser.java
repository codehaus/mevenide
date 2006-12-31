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

package org.mevenide.ui.eclipse.sync.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.mevenide.context.IQueryContext;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomChooser {
    
	private IProject eclipseProject;
    private Project  mavenProject;
	
	/**
	 * explicitely set poms [size = 1], so we wont recurse into eclipseProject. 
	 * 
	 * @warn if using this constructor, eclipseProject is not initialized
	 * @open do we want to recurse into pom hierarchy ??  
	 */
	public PomChooser(Project project) {
        this.mavenProject = project;
	}
	
	public PomChooser(IProject container) {
		this.eclipseProject = container;
	}
	
	/**
	 * display a Dialog to allow the user to choose a pom. 
	 * if theres only zero or one available pom, it directly returned  
	 */
	public List openPomChoiceDialog(boolean singleSelection) throws Exception {
        List projects = new ArrayList();
        
        if (this.mavenProject != null) {
            projects.add(this.mavenProject);
            return projects;
        }

        if (this.eclipseProject != null) {
            IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(this.eclipseProject);
            if (context != null) {
                projects.addAll(Arrays.asList(context.getPOMContext().getProjectLayers()));
            }
        }

        if (projects.size() < 2) {
            return projects;
        }

		PomChoiceDialog dialog = new PomChoiceDialog(projects, singleSelection);

        int result = dialog.open();
        if (result == Dialog.CANCEL) {
            return projects;
        }
		
		return dialog.getPoms();
	}
}
