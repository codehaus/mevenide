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
package org.mevenide.ui.eclipse.nature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class MevenideNature implements IProjectNature {
	
	private static Log log = LogFactory.getLog(MevenideNature.class);
	
	private IProject project;

	public void configure() throws CoreException {
		try {
			configureProject(project);
		} 
		catch (Exception e) {
			log.debug("Unable to add MevenideNature to project '" + project.getName() + "' due to : " + e);
			throw new CoreException(new Status(IStatus.ERROR, "mevenide", 1, e.getMessage(), e));
		}
	}

	public void deconfigure() throws CoreException {
		deconfigureProject(project);
		
	}
	
	public static void configureProject(IProject project) throws Exception {
		addPomNature(project);
		synchronizeProject(project);		
	}
	
	private static void addPomNature(IProject project) {
		try {
			IProjectDescription projectDescription = project.getDescription();
			String[] natures = projectDescription.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = Mevenide.NATURE_ID; 
			projectDescription.setNatureIds(newNatures);
			project.setDescription(projectDescription, null);
		} 
		catch (Throwable e) {
			log.debug("Unable to set project description due to : " + e);
		}
	}
	
	public static void deconfigureProject(IProject project) throws CoreException {
		if ( project != null ) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			List newNatures = new ArrayList(Arrays.asList(prevNatures));
			if ( newNatures.contains(Mevenide.NATURE_ID) ) {
				newNatures.remove(Mevenide.NATURE_ID);
				String[] setNatures = new String[newNatures.size()];
				for (int i = 0; i < setNatures.length; i++) {
					setNatures[i] = (String) newNatures.get(i);
				}
				description.setNatureIds(setNatures);
				project.setDescription(description, null);
			}
		}
	}

	
	private static void synchronizeProject(IProject project) {
		//@todo
	    //synchronize only if pref is set to true
	}

	
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
