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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.io.ProjectWriter;

/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class ResourceWrapper extends SourceFolder {

	private static Log log = LogFactory.getLog(ResourceWrapper.class); 
	
	private Resource resource;
	
	public ResourceWrapper(Resource resource) {
		this.resource = resource;
	}

	public void addTo(IProject project) throws Exception {
		String path = resource.getDirectory();
		
		log.debug("adding src entry to .classpath : "  + path + "(resource)");

		IClasspathEntry srcEntry = newSourceEntry(path, project);		

		addClasspathEntry(srcEntry, project);
	}
	
	public void addTo(Project project) throws Exception {
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		
		//add it to resources list for now..
		//@TODO manage case where it is a unitTestResourceDirectory
		//project.getBuild().addResource(resource);
		ProjectWriter.getWriter().addResource(resource.getDirectory(), project.getFile());
	}
	
	public void removeFrom(Project project) throws Exception {
		if ( project.getBuild() != null ) {
			//remove from pom.build.resources
			List resources = project.getBuild().getResources();
			removeFromList(resources);
			project.getBuild().setResources(resources);
			
			//remove from pom.build.unitTest.resources
			if ( project.getBuild().getUnitTest() != null ) {
				List unitTestResources = new ArrayList(project.getBuild().getResources());
				removeFromList(unitTestResources);
				project.getBuild().getUnitTest().setResources(unitTestResources);
			}
		}
		ProjectWriter.getWriter().write(project);
	}

	private void removeFromList(List resources) {
		List iteratorResources = new ArrayList(resources); 
		for ( int i = 0; i < iteratorResources.size(); i++ ) {
			Resource res = (Resource) iteratorResources.get(i);
			String directory = res.getDirectory();
			if ( directory != null && directory.equals(resource.getDirectory()) ) {
				resources.remove(res);
			}
		}
	}
	
	protected String getIgnoreLine() {
		return resource.getDirectory();
	}
}
