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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.mevenide.ui.eclipse.sync.model.ArtifactNode;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RemoveFromClasspathAction extends ArtifactAction {
	public void removeEntry(ArtifactNode selectedNode, IProject project) throws Exception {
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[entries.length - 1];
		int idx = 0;
		while ( true ) {
			if ( ((ArtifactNode) selectedNode).equivalentEntry(entries[idx]) ) {
				break;
			}
			idx++;
		}
		System.arraycopy(entries, 0, newEntries, 0, idx);
		System.arraycopy(entries, idx + 1, newEntries, idx, entries.length - idx - 1);
		javaProject.setRawClasspath(newEntries, null);
		project.refreshLocal(0, null);
		fireArtifactRemovedFromClasspath(selectedNode, project);
	}
	
}
