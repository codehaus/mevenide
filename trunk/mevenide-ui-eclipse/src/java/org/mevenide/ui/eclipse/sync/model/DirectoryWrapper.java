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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.ui.eclipse.util.SourceDirectoryTypeUtil;

/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class DirectoryWrapper extends SourceFolder {
	private static Log log = LogFactory.getLog(DirectoryWrapper.class); 
	
	private Directory directory ;
	
	public DirectoryWrapper(File declaringPom, Directory directory) {
		super(declaringPom);
		this.directory = directory;
	}
	
	public void addTo(IProject project) throws Exception {
		String type = directory.getType();
		String path = directory.getPath();
		log.debug("adding src entry to .classpath : "  + path + "(" + type + ")");
		
		IClasspathEntry srcEntry = newSourceEntry(path, project);
		
		addClasspathEntry(srcEntry, project);
	}

	public void addTo(Project project) throws Exception {
		String type = directory.getType();
		String path = directory.getPath();
		
		if ( SourceDirectoryTypeUtil.isSource(type) ) {	
			ProjectWriter.getWriter().addSource(path, project.getFile(), type);
		}
		
		if ( SourceDirectoryTypeUtil.isResource(type) ) {
			ProjectWriter.getWriter().addResource(path, project.getFile());
		}
		
		if ( SourceDirectoryTypeUtil.isUnitTestResource(type) ) {
			ProjectWriter.getWriter().addUnitTestResource(path, project.getFile());
		}
	}
	
	public void removeFrom(Project project) throws Exception {
		if ( project.getBuild() != null ) { 
			String type = directory.getType();
			String path = directory.getPath();
			
			if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(type) ) {
				project.getBuild().setSourceDirectory(null);
			}
			if ( ProjectConstants.MAVEN_TEST_DIRECTORY.equals(type) ) {
				project.getBuild().setUnitTestSourceDirectory(null);
			}
			if ( ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(type) ) {
				project.getBuild().setAspectSourceDirectory(null);
			}
			ProjectWriter.getWriter().write(project);
		}
	}
	
	protected String getIgnoreLine() {
		return directory.getPath();
	}
}
