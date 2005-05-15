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
package org.mevenide.ui.eclipse.sync.view;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.mevenide.project.io.ProjectReader;


class SynchronizationResourceDeltaVisitor implements IResourceDeltaVisitor {
	
	private static final Log log = LogFactory.getLog(SynchronizationResourceDeltaVisitor.class);
	
	private SynchronizationView synchronizationView;
	
	private IProject project;
	private IFile dotClasspath;
	
	SynchronizationResourceDeltaVisitor(SynchronizationView view, IProject project) {
		this.project = project;
		this.synchronizationView = view;
		dotClasspath = project != null ? project.getFile(".classpath") : null;
	}
	
	public boolean visit(IResourceDelta delta) {
		if (delta != null) {
			IResource r = delta.getResource();									
			if (r instanceof IFile) {
				IFile file = (IFile) r;
				if ( file.equals(dotClasspath) || ".mvnignore".equals(file.getName()) ) {
					this.synchronizationView.asyncRefresh(false);
				}
				if ( this.synchronizationView.getPoms() != null ) {
					for (int i = 0; i < this.synchronizationView.getPoms().size(); i++) {
						File f = ((Project) this.synchronizationView.getPoms().get(i)).getFile();
						if ( new File(file.getLocation().toOSString()).equals(f) ) {

							try {
	                            this.synchronizationView.updatePoms(ProjectReader.getReader().read(f));
	                        } 
							catch (Exception e) {
	                            log.error("Unable to update pom list", e);
	                        }
						}
					}
				}
				this.synchronizationView.asyncRefresh(false);
			}
			if ( r instanceof IProject ) {
				IProject prj = (IProject) r;
				if ( project != null && prj.getName().equals(project.getName()) ) {
					this.synchronizationView.asyncRefresh(false);
				}
			}
		}
		return true;
	}
}