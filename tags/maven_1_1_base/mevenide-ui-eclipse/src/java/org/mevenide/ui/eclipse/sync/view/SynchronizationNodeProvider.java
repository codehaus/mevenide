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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationNodeListener;
import org.mevenide.ui.eclipse.sync.model.EclipseProjectNode;
import org.mevenide.ui.eclipse.sync.model.ISynchronizationNode;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SynchronizationNodeProvider implements ITreeContentProvider {
    
    public Object[] getChildren(Object parentElement) {
    	if ( parentElement instanceof ISynchronizationNode ) {
			return ((ISynchronizationNode) parentElement).getChildren();
		}
        return null;
    }
 
	public Object getParent(Object element) {
		if ( element instanceof ISynchronizationNode )  {
			return ((ISynchronizationNode) element).getParent();
		}
        return null;
    }
    
    public boolean hasChildren(Object element) {
        if ( element instanceof ISynchronizationNode )  {
			return ((ISynchronizationNode) element).hasChildren();
		}
		return false;
    }
    
    public void dispose() {
    }
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	
    }
    
    public Object[] getElements(Object inputElement) {
    	RootNode node = (RootNode) inputElement;
    	EclipseProjectNode root = new EclipseProjectNode(node.getProject(), node.getMavenProjects());
    	root.setSynchronizationNodesListener(node.getSynchronizationNodesListener());
		return new Object[] { root };
    }
    
    public class RootNode {
    	private IProject project;
    	private List mavenProjects;
    
    	private List synchronizationNodesListener = new ArrayList();
    	
    	public RootNode(IProject project, List mavenProjects) {
    		this.project = project;
    		this.mavenProjects = mavenProjects;
    	}
    	
		public List getMavenProjects() {
			return mavenProjects;
		}
		
		public IProject getProject() {
			return project;
		}

		public void addNodeListener(ISynchronizationNodeListener node) {
			synchronizationNodesListener.add(node);
		}
		
		public void removeNodeListener(ISynchronizationNodeListener node) {
			synchronizationNodesListener.remove(node);
		}
		
		
		List getSynchronizationNodesListener() {
			return synchronizationNodesListener;
		}
    }
}

