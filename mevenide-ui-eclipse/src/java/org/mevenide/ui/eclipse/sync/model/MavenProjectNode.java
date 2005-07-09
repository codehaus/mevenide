/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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
import java.io.FileFilter;
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
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.properties.IPropertyResolver;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.sync.model.properties.MavenProjectPropertySource;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.ui.eclipse.util.JavaProjectUtils;
import org.mevenide.ui.eclipse.util.SourceDirectoryTypeUtil;
import org.mevenide.util.MevenideUtils;
import org.mevenide.util.StringUtils;

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
	
	private IPropertyResolver environmentLocator;
	
	public MavenProjectNode(Project project, EclipseProjectNode parentNode) {
		mavenProject = project;
		this.parentNode = parentNode;
		eclipseProject = (IProject) parentNode.getData();
		intializeEnvironmentLocator();
		initialize();
	}
	
        private void intializeEnvironmentLocator() {
            File projectDir = new File(eclipseProject.getLocation().toOSString());
            IQueryContext context = new DefaultQueryContext(projectDir);
            environmentLocator = context.getResolver();
        }

	private void initialize() {
	    initializeArtifacts();
	    initializeDirectories();
	    initializeOutputFolders();
	}
	
    private void initializeOutputFolders() {
    	try {
			String defaultEclipseOutputFolder = JavaProjectUtils.getRelativeDefaultOuputFolder(eclipseProject).replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
			String defaultMavenOutputFolder = environmentLocator.getResolvedValue("maven.build.dest").replaceAll("\\\\", "/");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
			
			File baseDir = mavenProject.getFile().getParentFile();
			
			if ( !new File(defaultMavenOutputFolder).isAbsolute() ) {
				defaultMavenOutputFolder = new File(baseDir, defaultMavenOutputFolder).getAbsolutePath().replaceAll("\\\\","/"); //$NON-NLS-1$ //$NON-NLS-2$
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
			String message = "Unable to lookup eclipse default output folder";  //$NON-NLS-1$
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
	    List artifacts = buildArtifactList(project);

		//special junit treatment
		if ( project.getBuild() != null && !StringUtils.isNull(project.getBuild().getUnitTestSourceDirectory()) ) {
			boolean foundJunit = false;
			for ( int u = 0; u < artifacts.size(); u++ ) {
				Artifact currentArtifact = (Artifact) artifacts.get(u);
				Dependency d = currentArtifact.getDependency();
				if ( d != null ) { 
					if ( "junit".equals(d.getGroupId()) &&
						 "junit".equals(d.getArtifactId()) 	) {
						foundJunit = true;
						break;
					}
				}
			}
			if ( !foundJunit ) {
				try {
					File cacheDir = new File(getPreferenceStore().getString(MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY), "cache");
					if ( cacheDir.exists() ) {
						File[] list = cacheDir.listFiles(new FileFilter() {
							public boolean accept(File pathname) { 
								if  ( pathname.isDirectory() && pathname.getName().indexOf("maven-test-plugin") >= 0 ) {
									return true;
								}
								return false;
							}
						});
						//@todo get newest test plugin - or one that is in use
						if ( list.length > 0 ) {
							JDomProjectUnmarshaller unmarshaller = new JDomProjectUnmarshaller();
							File testPluginProjectFile = new File(list[0], "project.xml");
							Project testPluginDescriptor = unmarshaller.parse(testPluginProjectFile);
							testPluginDescriptor.setFile(testPluginProjectFile);
							List testPluginDependencies = buildArtifactList(testPluginDescriptor);
							for ( int u = 0; u < testPluginDependencies.size(); u++ ) {
								Artifact artifact = (Artifact) testPluginDependencies.get(u);
								Dependency d = artifact.getDependency();
								if ( d != null ) { 
									if ( "junit".equals(d.getGroupId()) &&
										 "junit".equals(d.getArtifactId()) 	) {
										artifacts.add(artifact);
										break;
									}
								}
							}
						}
					}
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Artifact artifact = new GenericArtifact(dependency);
				
			}
		}
		
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

	private List buildArtifactList(Project project) {
		//change user.dir to allow to build artifacts correctly
	    String backupUserDir = System.getProperty("user.dir"); //$NON-NLS-1$
		System.setProperty("user.dir", project.getFile().getParentFile().getAbsolutePath()); //$NON-NLS-1$
		//needed for rc3 to correctly setRelativePaths
	    System.setProperty("maven.home", getPreferenceStore().getString(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY)); //$NON-NLS-1$
		project.setContext(MavenUtils.createContext(project.getFile().getParentFile()));
		if ( project.getDependencies() == null ) {
	        project.setDependencies(new ArrayList());
	    }
		List artifacts = ArtifactListBuilder.build(project);
		
		//restore user.dir
		System.setProperty("user.dir", backupUserDir); //$NON-NLS-1$
		return artifacts;
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
			log.error("Cannot read source directories for pom " + project.getFile(), e); //$NON-NLS-1$
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
		    directoryPath = "${basedir}".equals(directoryPath) ? "${basedir}" : directoryPath; //$NON-NLS-1$ //$NON-NLS-2$
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
			return MavenUtils.makeRelativePath(new File(projectPath), mavenProject.getFile().getAbsolutePath()).replaceAll("\\\\", "/");  //$NON-NLS-1$//$NON-NLS-2$
		} 
		catch (IOException e) {
			String message = "Unable to compute pom relative path. returning file.name";  //$NON-NLS-1$
			log.error(message, e);
			return mavenProject.getFile().getName();
		}
	}
	
	
	//quicky.. might be done more properly at initialization stage ?
	public boolean select(int direction) {
	    if ( artifactNodes != null ) {
			for (int i = 0; i < this.artifactNodes.length; i++) {
			 	if ( artifactNodes[i].getDirection() == direction ) {
					return true;
				}
			}
	    }
		if ( directoryNodes != null ) {
			for (int i = 0; i < this.directoryNodes.length; i++) {
				if ( directoryNodes[i].getDirection() == direction ) {
					return true;
				}
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
		if ( obj == null || !(obj instanceof MavenProjectNode) ) {
			return false;
		}

		MavenProjectNode other = ((MavenProjectNode) obj);
		return this.mavenProject.getFile().equals(other.mavenProject.getFile())
		       && parentNode.equals(other.parentNode);
	}
	
	
    public Object getAdapter(Class adapter) {
        if ( IPropertySource.class.equals(adapter) ) {
            return new MavenProjectPropertySource(this.mavenProject);
        }
        return null;
    }

    /**
     * TODO: Describe what getPreferenceStore does.
     * @return
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}

