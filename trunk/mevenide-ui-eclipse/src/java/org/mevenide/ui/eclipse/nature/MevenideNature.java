/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
		} catch (Exception e) {
			log.debug("Unable to add MevenideNature to project '" + project.getName() + "' due to : " + e);
			throw new CoreException(new Status(IStatus.ERROR, "mevenide", 1, e.getMessage(), e));
		}
	}

	public void deconfigure() throws CoreException {
		deconfigureProject(project);
		
	}
	
	public static void configureProject(IProject project) throws Exception {

		addPomNature(project);
		Mevenide.getPlugin().createPom();
		
			
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
		
	}

	
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
