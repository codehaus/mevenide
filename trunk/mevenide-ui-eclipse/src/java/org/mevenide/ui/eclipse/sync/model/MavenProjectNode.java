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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.ArtifactListBuilder;
import org.apache.maven.MavenUtils;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenProjectNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenProjectNode implements ISynchronizationNode, ISelectableNode {
	
	private static final Log log = LogFactory.getLog(MavenProjectNode.class);
	
	private Project mavenProject;
	
	private DirectoryNode[] directoryNodes;
	private MavenArtifactNode[] artifactNodes;
	
	private DirectoryNode[] originalDirectoryNodes;
	private MavenArtifactNode[] originalArtifactNodes;
	
	private EclipseProjectNode parentNode;
	
	public MavenProjectNode(Project project, EclipseProjectNode parentNode) {
		mavenProject = project;
		this.parentNode = parentNode;
		initialize();
	}
	
	private void initialize() {
	    initializeArtifacts();
	    initializeDirectories();
	}
	
	private void initializeArtifacts() {
		initializeArtifacts(mavenProject);
		
//		@todo iterate through parents to build the complete list of inherited artifacts   
//		Project tempProject = mavenProject;
//		
//		
//		try {
//			String extend = MevenideUtils.resolve(tempProject, tempProject.getExtend());
//			while ( extend != null ) {
//				tempProject = ProjectReader.getReader().read(new File(MavenUtils.makeAbsolutePath(tempProject.getFile().getParentFile(), extend)));
//				initializeArtifacts();
//				extend = MevenideUtils.resolve(tempProject, tempProject.getExtend());
//			}
//		} 
//		catch (Exception e) {
//			log.error("Couldnot read parent project", e);
//		}
		
	}
	
	private void initializeArtifacts(Project project) {
		project.setContext(MavenUtils.createContext(project.getFile().getParentFile()));
		List artifacts = ArtifactListBuilder.build(project);
		originalArtifactNodes = new MavenArtifactNode[artifacts.size()];
		List comparisonList = artifactNodes != null ? Arrays.asList(artifactNodes) : new ArrayList();
	    for (int i = 0; i < artifacts.size(); i++) {
	    	Artifact artifact = (Artifact) artifacts.get(i);
	    	MavenArtifactNode artifactNode = new MavenArtifactNode(artifact, this);
	    	if ( !comparisonList.contains(artifactNode) ) {
	    		originalArtifactNodes[i] = artifactNode; 
	    		originalArtifactNodes[i].setDirection(ISelectableNode.INCOMING_DIRECTION);
	    		comparisonList.add(artifactNode);
	    	}
		}
		artifactNodes = originalArtifactNodes;
		joinEclipseProjectArtifacts();
	}

	private void joinEclipseProjectArtifacts() {
	    List tempNodes = new ArrayList(Arrays.asList(artifactNodes));
	    List eclipseArtifacts = createNodes(parentNode.getEclipseClasspathArtifacts());
	    for (int i = 0; i < eclipseArtifacts.size(); i++) {
	    	MavenArtifactNode eclipseArtifactNode = (MavenArtifactNode) eclipseArtifacts.get(i);
	    	if ( !tempNodes.contains(eclipseArtifactNode)  ) {
				eclipseArtifactNode.setDirection(ISelectableNode.OUTGOING_DIRECTION);
				tempNodes.add(eclipseArtifactNode);
			}
	    	else {
				tempNodes.remove(eclipseArtifactNode); 	
	    	}
		}
		artifactNodes = (MavenArtifactNode[]) tempNodes.toArray(new MavenArtifactNode[0]);
	}
	
	private List createNodes(List mavenArtifacts) {
		List nodeList = new ArrayList(mavenArtifacts.size());
		for (int i = 0; i < mavenArtifacts.size(); i++) {
	    	Artifact eclipseArtifact = (Artifact) mavenArtifacts.get(i);
	    	MavenArtifactNode node = new MavenArtifactNode(eclipseArtifact, this);
	    	nodeList.add(node);
	    }
		return nodeList;
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
	
	public boolean equals(Object obj) {
		if ( !(obj instanceof MavenProjectNode) ) {
			return false;
		}
		MavenProjectNode node = ((MavenProjectNode) obj);
		return mavenProject.getName().equals(node.mavenProject.getName())
		       && parentNode.equals(node.parentNode);
	}
}

