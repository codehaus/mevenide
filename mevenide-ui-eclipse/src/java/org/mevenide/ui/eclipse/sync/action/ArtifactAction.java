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
package org.mevenide.ui.eclipse.sync.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.mevenide.ui.eclipse.sync.event.IActionListener;
import org.mevenide.ui.eclipse.sync.event.IdeArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.PomArtifactEvent;
import org.mevenide.ui.eclipse.sync.model.ArtifactWrapper;
import org.mevenide.ui.eclipse.sync.model.DependencyWrapper;
import org.mevenide.ui.eclipse.sync.model.Directory;
import org.mevenide.ui.eclipse.sync.model.DirectoryWrapper;
import org.mevenide.ui.eclipse.sync.model.ResourceWrapper;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactAction {
	private static Log log = LogFactory.getLog(ArtifactAction.class);
	
	private List listeners = new ArrayList();
	
	public void addActionListener(IActionListener listener) {
		listeners.add(listener);	
	}
	
	public void removeActionListener(IActionListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireArtifactAddedToClasspath(Object item, IProject project) {
		for (int i = 0; i < listeners.size(); i++) {
			IdeArtifactEvent event = new IdeArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactAddedToClasspath(event);
		}
	}
	
	protected void fireArtifactRemovedFromPom(Object item, Project project) {
		for (int i = 0; i < listeners.size(); i++) {
			PomArtifactEvent event = new PomArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactRemovedFromPom(event);
		}
	}
	
	protected void fireArtifactRemovedFromClasspath(Object item, IProject project) {
		for (int i = 0; i < listeners.size(); i++) {
			IdeArtifactEvent event = new IdeArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactRemovedFromClasspath(event);
		}
	}
	
	protected void fireArtifactAddedToPom(Object item, Project project) {
		log.debug("Artifact (" + item + ") added to POM : " + project.getFile());
		for (int i = 0; i < listeners.size(); i++) {
			PomArtifactEvent event = new PomArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactAddedToPom(event);
		}
	}
	
	protected void fireArtifactIgnored(Object item, IContainer container) {
		for (int i = 0; i < listeners.size(); i++) {
			IdeArtifactEvent event = new IdeArtifactEvent(item, container);
			((IActionListener)listeners.get(i)).artifactIgnored(event);
		}
	}
	
	protected void fireArtifactIgnored(Object item, Project project) {
		for (int i = 0; i < listeners.size(); i++) {
			PomArtifactEvent event = new PomArtifactEvent(item, project);
			((IActionListener)listeners.get(i)).artifactIgnored(event);
		}
	}
	
	//crap..
	protected ArtifactWrapper getArtifactWrapper(File declaringPom, Object item) {
		if ( item instanceof Dependency ) {
			return new DependencyWrapper(declaringPom, (Dependency) item);
		}
		if ( item instanceof Directory ) {
			return new DirectoryWrapper(declaringPom, (Directory) item);
		}
		if ( item instanceof Resource ) {
			return new ResourceWrapper(declaringPom, (Resource) item);
		}
		return null;
	}
}
