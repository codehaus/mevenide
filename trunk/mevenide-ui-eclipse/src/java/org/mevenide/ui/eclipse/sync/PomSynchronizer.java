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
package org.mevenide.ui.eclipse.sync;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.mevenide.project.source.SourceDirectoryUtil;
import org.mevenide.sync.AbstractPomSynchronizer;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.dependency.DependencyGroup;
import org.mevenide.ui.eclipse.sync.dependency.DependencyMarshaller;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryGroup;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryMarshaller;
import org.mevenide.ui.eclipse.util.ProjectUtil;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class PomSynchronizer extends AbstractPomSynchronizer implements ISynchronizer {
	/** the project under control */
	private IProject project;
    
    /** the POM under control */
	private IFile pom;

    /** helper instance */
    private IPathResolverDelegate pathResolver;
    

    
    public void initialize() {
		this.project = Mevenide.getPlugin().getProject();
		this.pom = project.getFile("project.xml");
		assertPomNotEmpty();
		pathResolver = new DefaultPathResolverDelegate(); 
	}

	private void assertPomNotEmpty() {
		try {
			if ( pom.exists() ) {
				InputStream inputStream = pom.getContents(true);
			
				if ( inputStream.read() == -1 ) {
					InputStream stream = PomSynchronizer.class.getResourceAsStream("/templates/standard/project.xml"); 
					pom.setContents(stream, true, true, null);
					stream.close();
				}
				inputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer
     */
	protected void mavenize() {
		try {
			assertPomNotEmpty();
			
			SourceDirectoryUtil.resetSourceDirectories(Mevenide.getPlugin().getPom());
			
			DependencyGroup dependencyGroup = DependencyMarshaller.getDependencyGroup(project, Mevenide.getPlugin().getFile("statedDependencies.xml"));
			SourceDirectoryGroup sourceGroup = SourceDirectoryMarshaller.getSourceDirectoryGroup(project, Mevenide.getPlugin().getFile("sourceTypes.xml"));
			
			ProjectUtil.updatePom(sourceGroup, dependencyGroup, Mevenide.getPlugin().getPom());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#preSynchronization()
     */
	public void preSynchronization() {
		try {
			Mevenide.getPlugin().createPom();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#postSynchronization()
     */
	public void postSynchronization() {
		
	}
 

	
}