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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.ArtifactListBuilder;
import org.apache.maven.MavenUtils;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.properties.resolver.DefaultsResolver;
import org.mevenide.properties.resolver.PropertyFilesAggregator;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.properties.MavenProjectPropertySource;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.ui.eclipse.util.JavaProjectUtils;
import org.mevenide.ui.eclipse.util.SourceDirectoryTypeUtil;
import org.mevenide.util.MevenideUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: MavenProjectNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenProjectNode extends AbstractSynchronizationNode implements ISelectableNode, IAdaptable {
	
	private static final Log log = LogFactory.getLog(MavenProjectNode.class);
	
	private Project mavenProject;
	
	private DirectoryNode[] directoryNodes;
	private MavenArtifactNode[] artifactNodes;
	
	private DirectoryNode[] originalDirectoryNodes;
	private MavenArtifactNode[] originalArtifactNodes;
	
	private EclipseProjectNode parentNode;
	private IProject eclipseProject;
	
	private PropertyFilesAggregator environmentLocator;
	
	public MavenProjectNode(Project project, EclipseProjectNode parentNode) {
		mavenProject = project;
		this.parentNode = parentNode;
		eclipseProject = (IProject) parentNode.getData();
		intializeEnvironmentLocator();
		initialize();
	}
	
	private void intializeEnvironmentLocator() {
    	File projectDir = new File(eclipseProject.getLocation().toOSString()); 
    	File userHomeDir = new File(System.getProperty("user.home"));
    	LocationFinderAggregator finder = new LocationFinderAggregator();
        finder.setEffectiveWorkingDirectory(projectDir.getAbsolutePath());
    	environmentLocator = new PropertyFilesAggregator(
    			                     projectDir, 
    			                     userHomeDir, 
									 new DefaultsResolver(projectDir, userHomeDir, finder));
	}

	private void initialize() {
	    initializeArtifacts();
	    initializeDirectories();
	    initializeOutputFolders();
	}
	
    private void initializeOutputFolders() {
    	try {
			String defaultEclipseOutputFolder = JavaProjectUtils.getRelativeDefaultOuputFolder(eclipseProject).replaceAll("\\\\", "/");
			String defaultMavenOutputFolder = environmentLocator.getResolvedValue("maven.build.dest").replaceAll("\\\\", "/");
			
			File baseDir = mavenProject.getFile().getParentFile();
			
			if ( !new File(defaultMavenOutputFolder).isAbsolute() ) {
				defaultMavenOutputFolder = new File(baseDir, defaultMavenOutputFolder).getAbsolutePath().replaceAll("\\\\","/");
			}
			
			if ( !defaultEclipseOutputFolder.equals(defaultMavenOutputFolder) ) {
				DirectoryNode[] newNodes = new DirectoryNode[directoryNodes.length + 2];
				System.arraycopy(directoryNodes, 0, newNodes, 0, directoryNodes.length);
				
				DirectoryNode eclipseOutputFolderNode = createOutputFolderDirectoryNode(defaultEclipseOutputFolder);
				eclipseOutputFolderNode.setDirection(ISelectableNode.OUTGOING_DIRECTION);
				DirectoryNode mavenOutputFolderNode = createOutputFolderDirectoryNode(defaultMavenOutputFolder);
				mavenOutputFolderNode.setDirection(ISelectableNode.INCOMING_DIRECTION);
				
				newNodes[directoryNodes.length] = eclipseOutputFolderNode;
				newNodes[directoryNodes.length + 1] = mavenOutputFolderNode;
				
				directoryNodes = newNodes;
			}
		} 
    	catch (Exception e) {
			String message = "Unable to lookup eclipse default output folder"; 
			log.error(message, e);
		}
	}

	private DirectoryNode createOutputFolderDirectoryNode(String defaultEclipseOutputFolder) throws IOException {
		Directory eclipseOutputDirectory = new Directory(mavenProject);
		eclipseOutputDirectory.setPath(MevenideUtils.makeRelativePath(eclipseProject.getLocation().toFile(), defaultEclipseOutputFolder));
		eclipseOutputDirectory.setType(ProjectConstants.MAVEN_OUTPUT_DIRECTORY);
		DirectoryNode eclipseOutputFolderNode = new DirectoryNode(eclipseOutputDirectory, this);
		return eclipseOutputFolderNode;
	}

	/**
     * @todo iterate through parents to build the complete list of inherited artifacts
     * actually i thought again about it and im not too sure about that. if user wants to synchronize
     * projects with parent pom too shouldnot he as well pick up the parent in the dialog ? 
     * i believe it would make things more intuitive. 
     */
	private void initializeArtifacts() {
		initializeArtifacts(mavenProject);
	}
	
	private void initializeArtifacts(Project project) {
	    //change user.dir to allow to build artifacts correctly
	    String backupUserDir = System.getProperty("user.dir");
	    System.setProperty("user.dir", project.getFile().getParentFile().getAbsolutePath());
	    
	    //needed for rc3 to correctly setRelativePaths
	    System.setProperty("maven.home", Mevenide.getInstance().getMavenHome());
	    
	    project.setContext(MavenUtils.createContext(project.getFile().getParentFile()));
		List artifacts = ArtifactListBuilder.build(project);
		
		//restore user.dir
		System.setProperty("user.dir", backupUserDir);
		
		originalArtifactNodes = new MavenArtifactNode[artifacts.size()];
		List comparisonList = artifactNodes != null ? Arrays.asList(artifactNodes) : new ArrayList();
		for (int i = 0; i < artifacts.size(); i++) {
	    	Artifact artifact = (Artifact) artifacts.get(i);
	    	MavenArtifactNode artifactNode = new MavenArtifactNode(artifact, this);
	    	if ( !comparisonList.contains(artifactNode) && !FileUtils.isArtifactIgnored(artifactNode.toString(), eclipseProject) ) {
	    		originalArtifactNodes[i] = artifactNode; 
	    		originalArtifactNodes[i].setDirection(ISelectableNode.INCOMING_DIRECTION);
	    		comparisonList.add(artifactNode);
	    	}
		}
		artifactNodes = (MavenArtifactNode[]) comparisonList.toArray(new MavenArtifactNode[0]);
		joinEclipseProjectArtifacts();
	}

	private void joinEclipseProjectArtifacts() {
	    List tempNodes = new ArrayList(Arrays.asList(artifactNodes));
	    List eclipseArtifacts = createArtifactNodes(parentNode.getEclipseClasspathArtifacts());
	    for (int i = 0; i < eclipseArtifacts.size(); i++) {
	    	MavenArtifactNode eclipseArtifactNode = (MavenArtifactNode) eclipseArtifacts.get(i);
	    	if ( !tempNodes.contains(eclipseArtifactNode) && !FileUtils.isArtifactIgnored(eclipseArtifactNode.toString(), eclipseProject) ) {
				eclipseArtifactNode.setDirection(ISelectableNode.OUTGOING_DIRECTION);
				tempNodes.add(eclipseArtifactNode);
			}
	    	else {
				tempNodes.remove(eclipseArtifactNode); 	
	    	}
		}
		artifactNodes = (MavenArtifactNode[]) tempNodes.toArray(new MavenArtifactNode[0]);
	}
	
	private List createArtifactNodes(List mavenArtifacts) {
		List nodeList = new ArrayList(mavenArtifacts.size());
		for (int i = 0; i < mavenArtifacts.size(); i++) {
	    	Artifact eclipseArtifact = (Artifact) mavenArtifacts.get(i);
	    	MavenArtifactNode node = new MavenArtifactNode(eclipseArtifact, this);
	    	nodeList.add(node);
	    }
		return nodeList;
	}
	
	/**
	 * @see #initializeArtifacts()
	 */
	private void initializeDirectories() {
		initializeDirectories(mavenProject);
	}
	
	private void initializeDirectories(Project project) {
		try {
			ProjectReader projectReader = ProjectReader.getReader();
			Map sourceDirectoryMap = projectReader.readSourceDirectories(project.getFile());
			Map map = reverseMap(sourceDirectoryMap);
			Map resourceDirectoryMap = projectReader.readAllResources(project.getFile());
			map.putAll(resourceDirectoryMap);
			createDirectoryNodes(map);
			if ( map == null || map.size() == 0 ) {
				directoryNodes = new DirectoryNode[0];
			}
			joinEclipseSourceFolders();
		} 
		catch (Exception e) {
			log.error("Cannot read source directories for pom " + project.getFile(), e);
		}
	}
	
	private Map reverseMap(Map map) {
        Map result = new HashMap();
	    if ( map != null && map.size() > 0) {
		    Iterator iterator = map.keySet().iterator();
		    while ( iterator.hasNext() ) {
		        Object nextKey = iterator.next();
	        	Object nextValue = map.get(nextKey);
	        	result.put(nextValue, nextKey);
		    }
	    }
        return result;
    }

    private void joinEclipseSourceFolders() {
		List tempNodes = new ArrayList(Arrays.asList(directoryNodes));
	    List eclipseSourceFolders = createSourceFolderNodes(parentNode.getEclipseSourceFolders());
	    for (int i = 0; i < eclipseSourceFolders.size(); i++) {
	    	DirectoryNode eclipseSourceFolderNode = (DirectoryNode) eclipseSourceFolders.get(i);
	    	if ( !tempNodes.contains(eclipseSourceFolderNode)  ) {
				eclipseSourceFolderNode.setDirection(ISelectableNode.OUTGOING_DIRECTION);
				Directory directory = (Directory) eclipseSourceFolderNode.getData();
				directory.setType(SourceDirectoryTypeUtil.guessSourceType(directory.getPath()));
				tempNodes.add(eclipseSourceFolderNode);
			}
	    	else {
				tempNodes.remove(eclipseSourceFolderNode); 	
	    	}
		}
		directoryNodes = (DirectoryNode[]) tempNodes.toArray(new DirectoryNode[0]);
	}
	
	private List createSourceFolderNodes(List eclipseSourceDirectories) {
		List nodeList = new ArrayList();
		for (int i = 0; i < eclipseSourceDirectories.size(); i++) {
		    String directoryPath = (String) eclipseSourceDirectories.get(i);
		    directoryPath = "${basedir}".equals(directoryPath) ? "${basedir}" : directoryPath;
		    Directory eclipseDirectory = new Directory(mavenProject);
	    	eclipseDirectory.setPath(directoryPath);
	    	DirectoryNode node = new DirectoryNode(eclipseDirectory, this);
	    	node.setExcludeNodes(getEclipseExclusionFilterNodes(directoryPath, node));
	    	nodeList.add(node);
	    }
	    return nodeList;
	}

	private ExcludeNode[] getEclipseExclusionFilterNodes(String eclipseSourceFolder, DirectoryNode directoryNode) {
		String[] exclusionPatterns = JavaProjectUtils.findExclusionPatterns(eclipseSourceFolder, eclipseProject);
		if ( exclusionPatterns != null ) {
			ExcludeNode[] nodes = new ExcludeNode[exclusionPatterns.length];
			for (int i = 0; i < exclusionPatterns.length; i++) {
				nodes[i] = new ExcludeNode(directoryNode, exclusionPatterns[i]);
			}
			return nodes;
		}
		return null;
	}
	
	private void createDirectoryNodes(Map sourceDirectoryMap) {
	    List tempNodes = new ArrayList();
		originalDirectoryNodes = new DirectoryNode[sourceDirectoryMap.size()];
		Iterator iterator = sourceDirectoryMap.keySet().iterator();
		int u = 0;
		while ( iterator.hasNext() ) {
			String nextPath = (String) iterator.next();
			String nextType = (String) sourceDirectoryMap.get(nextPath);
			DirectoryNode node = createDirectoryNode(nextType, nextPath);
			node.setDirection(ISelectableNode.INCOMING_DIRECTION);
			if ( !FileUtils.isArtifactIgnored(node.toString(), eclipseProject) ) {
			    tempNodes.add(node);
			}
			originalDirectoryNodes[u] = node;
			u++;
		}
		directoryNodes = (DirectoryNode[]) tempNodes.toArray(new DirectoryNode[0]);
	}

	private DirectoryNode createDirectoryNode(String type, String path) {
		Directory directory = new Directory(mavenProject);
		directory.setPath(path);
		directory.setType(type);
		DirectoryNode node = new DirectoryNode(directory, this);
		return node;
	}

	
	public ISynchronizationNode[] getChildren() {
	    final int directoryNodesNumber = directoryNodes != null ? directoryNodes.length : 0;
	    final int artifactNodesNumber = artifactNodes != null ? artifactNodes.length : 0;
	    ISynchronizationNode[] children = new ISynchronizationNode[directoryNodesNumber + artifactNodesNumber];
	    System.arraycopy(directoryNodes == null ? new DirectoryNode[0] : directoryNodes, 0, children, 0, directoryNodesNumber);
	    System.arraycopy(artifactNodes == null ? new MavenArtifactNode[0] : artifactNodes, 0, children, directoryNodesNumber, artifactNodesNumber);
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
		String projectPath = eclipseProject.getLocation().toOSString();
		try {
			return MavenUtils.makeRelativePath(new File(projectPath), mavenProject.getFile().getAbsolutePath()).replaceAll("\\\\", "/");
		} 
		catch (IOException e) {
			String message = "Unable to compute pom relative path. returning file.name"; 
			log.error(message, e);
			return mavenProject.getFile().getName();
		}
	}
	
	
	//quicky.. might be done more properly at initialization stage ?
	public boolean select(int direction) {
		for (int i = 0; i < this.artifactNodes.length; i++) {
		 	if ( artifactNodes[i].getDirection() == direction ) {
				return true;
			}
		}
		for (int i = 0; i < this.directoryNodes.length; i++) {
			if ( directoryNodes[i].getDirection() == direction ) {
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
	
	
    public Object getAdapter(Class adapter) {
        if ( IPropertySource.class.equals(adapter) ) {
            return new MavenProjectPropertySource(this.mavenProject);
        }
        return null;
    }
}

