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

import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DirectoryNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class DirectoryNode extends ArtifactNode implements IAdaptable {
	
	private Directory directory;
	
	private MavenProjectNode parentNode;
	
	public DirectoryNode(Directory dir, MavenProjectNode project) {
		directory = dir;
		parentNode = project;
	}
	
	public boolean equals(Object obj) {
		if ( !(obj instanceof DirectoryNode) ) {
			return false;
		}
		DirectoryNode node = (DirectoryNode) obj;
		return directory.equals(node.directory) && parentNode.equals(node.parentNode);
	}
	public ISynchronizationNode[] getChildren() {
		return null;
	}
	public Object getData() {
		return directory;
	}
	public ISynchronizationNode getParent() {
		return parentNode;
	}
	public boolean hasChildren() {
		return false;
	}
	public String toString() {
		return directory.getCleanPath();
	}
	
	
	/* (non-Javadoc)
	 * @see org.mevenide.ui.eclipse.sync.model.ArtifactNode#addTo(org.eclipse.core.resources.IProject)
	 */
	public void addTo(IProject project) throws Exception {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.mevenide.ui.eclipse.sync.model.ArtifactNode#addTo(org.apache.maven.project.MavenProject)
	 */
	public void addTo(Project project) throws Exception {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.mevenide.ui.eclipse.sync.model.ArtifactNode#getIgnoreLine()
	 */
	protected String getIgnoreLine() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.mevenide.ui.eclipse.sync.model.ArtifactNode#removeFrom(org.apache.maven.project.MavenProject)
	 */
	public void removeFrom(Project project) throws Exception {
		// TODO Auto-generated method stub
	}
	
	public Object getAdapter(Class adapteeClass) {
		if ( adapteeClass == IPropertySource.class ) {
			return new DirectoryPropertySource(directory);
		}
		return null;
	}
}
