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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.artifact.MavenArtifact;
import org.apache.maven.artifact.factory.DefaultMavenArtifactFactory;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.mevenide.project.dependency.DependencyUtil;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenProjectNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenProjectNode implements ISynchronizationNode, ISelectableNode {
	
	private MavenProject mavenProject;
	
	private DirectoryNode[] directoryNodes;
	private MavenArtifactNode[] artifactNodes;
	
	private DirectoryNode[] originalDirectoryNodes;
	private MavenArtifactNode[] originalArtifactNodes;
	
	private EclipseProjectNode parentNode;
	
	public MavenProjectNode(MavenProject project, EclipseProjectNode parentNode) {
		mavenProject = project;
		this.parentNode = parentNode;
		initialize();
	}
	
	private void initialize() {
	    initializeArtifacts();
	    initializeDirectories();
	}
	
	private void initializeArtifacts() {
		if ( mavenProject.getProperties() == null ) {
			mavenProject.setProperties(new HashMap());
		}
		List artifacts = new DefaultMavenArtifactFactory().createArtifacts(mavenProject);
		originalArtifactNodes = new MavenArtifactNode[artifacts.size()];
	    for (int i = 0; i < artifacts.size(); i++) {
	    	originalArtifactNodes[i] = new MavenArtifactNode((MavenArtifact) artifacts.get(i), this);
			originalArtifactNodes[i].setDirection(ISelectableNode.INCOMING_DIRECTION);
		}
		artifactNodes = originalArtifactNodes;
		joinEclipseProjectArtifacts();
	}
	
	private void joinEclipseProjectArtifacts() {
	    List tempNodes = new ArrayList(Arrays.asList(artifactNodes));
	    List eclipseArtifacts = parentNode.getEclipseClasspathArtifacts();
	    for (int i = 0; i < eclipseArtifacts.size(); i++) {
	    	MavenArtifact eclipseArtifact = (MavenArtifact) eclipseArtifacts.get(i);
	    	if ( !isPresent(eclipseArtifact)  ) {
	    		MavenArtifactNode node = new MavenArtifactNode(eclipseArtifact, this);
	    		node.setDirection(ISelectableNode.OUTGOING_DIRECTION);
			    tempNodes.add(node);
			}
//	    	else {
//	    	    //remove artifact from tempNodes 	
//	    	}
		}
		artifactNodes = (MavenArtifactNode[]) tempNodes.toArray(new MavenArtifactNode[0]);
	}
	
	private boolean isPresent(MavenArtifact artifact) {
	    for (int i = 0; i < artifactNodes.length; i++) {
	    	MavenArtifactNode node = (MavenArtifactNode) artifactNodes[i];
	        Dependency dependency = ((MavenArtifact) node.getData()).getDependency(); 
			if ( DependencyUtil.areEquals(dependency, artifact.getDependency()) ) {
			    return true;
			}
		}
	    return false;
	} 
	
	private void initializeDirectories() {
		directoryNodes = new DirectoryNode[0];
	}
	
	public ISynchronizationNode[] getChildren() {
	    ISynchronizationNode[] children = new ISynchronizationNode[directoryNodes.length + artifactNodes.length];
	    System.arraycopy(directoryNodes, 0, children, 0, directoryNodes.length);
	    System.arraycopy(artifactNodes, 0, children, 0, artifactNodes.length);
		return children;
	}
	
	public Object getData() {
		return mavenProject;
	}
	
	public ISynchronizationNode getParent() {
		return parentNode;
	}
	
	public boolean hasChildren() {
		return ( directoryNodes != null && directoryNodes.length > 0) || 
		       ( artifactNodes != null && artifactNodes.length > 0) ;
	}
	
	public String toString() {
		return mavenProject.getFile().getName();
	}
	
	
	//quicky.. might be done more properly at initialization stage ?
	public boolean select(int direction) {
		for (int i = 0; i < this.artifactNodes.length; i++) {
			if ( artifactNodes[i].getDirection() == direction ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean selectArtifacts(ArtifactNode[] nodes, int direction) {
		for (int i = 0; i < nodes.length; i++) {
			if ( artifactNodes[i].getDirection() == direction ) {
				return true;
			}
		}
		return false;
	}
}

