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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.sync.views.SourceDirectoryTypePart;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class MavenNature implements IProjectNature {
	
	private IProject project;

	public void configure() throws CoreException {
		try {
			configureProject(project);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, "mevenide", 1, e.getMessage(), e));
		}

	}

	public void deconfigure() throws CoreException {
		deconfigureProject(project);
		
	}
	
	public static void configureProject(IProject project) throws Exception {

		addPomNature(project);
		MavenPlugin.getPlugin().createPom();
		SourceDirectoryTypePart.showView();
		synchronizeProject(project);
	}
	
	private static void addPomNature(IProject project) {
		try {
			IProjectDescription projectDescription = project.getDescription();
			String[] natures = projectDescription.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = MavenPlugin.NATURE_ID; 
			projectDescription.setNatureIds(newNatures);
			project.setDescription(projectDescription, null);
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void deconfigureProject(IProject project) throws CoreException {
		if ( project != null ) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			List newNatures = new ArrayList(Arrays.asList(prevNatures));
			if ( newNatures.contains(MavenPlugin.NATURE_ID) ) {
				newNatures.remove(MavenPlugin.NATURE_ID);
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
		try {
			//@todo FUNCTIONAL update .classpath : add correct src type (src, test, aspect) to each entry whose kind is CPE_SRC
			//AbstractSynchronizer cpSync = AbstractSynchronizer.getSynchronizer(AbstractSynchronizer.POM_TO_IDE);
			//cpSync.synchronize(getSrcTypes());
		
			//AbstractSynchronizer pomSync = AbstractSynchronizer.getSynchronizer(AbstractSynchronizer.IDE_TO_POM);
			//pomSync.createPomFile();
			//pomSync.synchronize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
