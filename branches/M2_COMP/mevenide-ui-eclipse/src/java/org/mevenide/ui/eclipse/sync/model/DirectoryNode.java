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

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DirectoryNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class DirectoryNode extends ArtifactNode {
	
	private Directory directory;
	
	private MavenProjectNode parent;
	
	public DirectoryNode(Directory dir, MavenProjectNode project) {
		directory = dir;
		parent = project;
	}
	
	public INode[] getChildren() {
		return null;
	}
	public Object getData() {
		return directory;
	}
	public INode getParent() {
		return parent;
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
	public void addTo(MavenProject project) throws Exception {
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
	public void removeFrom(MavenProject project) throws Exception {
		// TODO Auto-generated method stub
	}
}
