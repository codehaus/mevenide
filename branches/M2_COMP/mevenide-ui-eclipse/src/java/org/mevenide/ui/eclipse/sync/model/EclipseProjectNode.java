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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.MavenArtifact;
import org.apache.maven.artifact.factory.DefaultMavenArtifactFactory;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.util.JavaProjectUtils;
import org.mevenide.ui.eclipse.util.FileUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: EclipseProjectNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class EclipseProjectNode implements ISynchronizationNode {
	private static final Log log = LogFactory.getLog(EclipseProjectNode.class);
	
	private IContainer eclipseContainer;
	private IProject eclipseProject;
	private List mavenProjects;
	
	private MavenProjectNode[] mavenProjectNodes;
	
	private List eclipseSourceFolders = new ArrayList();
	private List eclipseClasspathArtifacts = new ArrayList();
	
	public EclipseProjectNode(IContainer container, List mavenProjects) {
		eclipseContainer = container;
		eclipseProject = eclipseContainer.getProject();
		this.mavenProjects = mavenProjects;
		initialize();	
	}
	
	private void initialize() {
		try {
			attachNature();
			if ( eclipseProject.hasNature(JavaCore.NATURE_ID) ) {
				initializeEclipseArtifacts();
				initializeMavenProjects();
			}
		} 
		catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	private void initializeEclipseArtifacts() throws Exception {
		initializeEclipseFolders();
		initializeEclipseLibraries();
	}
	
	private void initializeEclipseFolders() throws Exception {
		IJavaProject javaProject = JavaCore.create(eclipseProject);
		IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
		for (int i = 0; i < classpathEntries.length; i++) {
		    if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
		        IClasspathEntry classpathEntry = classpathEntries[i];
		        String path = new DefaultPathResolver().getRelativeSourceDirectoryPath(classpathEntry, eclipseProject);
		        if ( !FileUtils.isArtifactIgnored(path, javaProject.getProject())) { 
		        	eclipseSourceFolders.add(path);
		        }
		    }
		}
	}
	
	private void initializeEclipseLibraries() throws Exception {
		IClasspathEntry[] classpathEntries = JavaCore.create(eclipseProject).getResolvedClasspath(true);
		
		for (int i = 0; i < classpathEntries.length; i++) {
		    IClasspathEntry entry = classpathEntries[i];
	    	if ( isValidEntry(entry) ) {
		    	MavenArtifact artifact;
		    	artifact = createArtifact(entry);
		    	if ( artifact != null ) {
		    		eclipseClasspathArtifacts.add(artifact);
		    	}
		    }
		}
	}
	
	private MavenArtifact createArtifact(IClasspathEntry entry) throws Exception {
		MavenArtifact artifact = null;
	    if ( entry.getEntryKind() == ClasspathEntry.CPE_LIBRARY ) {
			artifact = createArtifactFromLibraryEntry(entry);
		}
		else {
			artifact = createArtifactFromProjectEntry(entry);
		}
		return artifact;
	}

	private MavenArtifact createArtifactFromLibraryEntry(IClasspathEntry entry) throws Exception {
		String path = entry.getPath().toOSString();
		DefaultMavenArtifactFactory artifactFactory = new DefaultMavenArtifactFactory();
		MavenArtifact artifact = null;
		if ( artifact != null && !new File(path).exists() ) {
			//not the best way to get the absoluteFile ... 
			path = eclipseProject.getProject().getLocation().append(entry.getPath().removeFirstSegments(1)).toOSString();
			artifact = artifactFactory.createArtifact(DependencyFactory.getFactory().getDependency(path));
			artifact.setPath(path);
		}
		
		return artifact;
	}

	private MavenArtifact createArtifactFromProjectEntry(IClasspathEntry entry) throws Exception {
		MavenArtifact artifact = null;
		String path = entry.getPath().toOSString();
	
		DefaultMavenArtifactFactory artifactFactory = new DefaultMavenArtifactFactory();
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String projectName = getProjectName(path);
		IProject referencedProject = root.getProject(projectName);
		if ( !referencedProject.getName().equals(eclipseProject.getProject().getName()) ) {
		    artifact = artifactFactory.createArtifact(createDependencyFromProject(referencedProject));
		}
	
		return artifact;
	}

	private String getProjectName(String projectBasedir) {
		//crap..
		String projectName ;
		if ( projectBasedir.substring(1).indexOf('/') < 0 ) {
			projectName = projectBasedir.substring(1);
		}
		else {
			projectName = projectBasedir.substring(1, projectBasedir.substring(1).indexOf('/') + 1) ; 
		}
		return projectName;
	}

	private boolean isProjectMavenized(String projectBasedir) {
		File pom = new File(projectBasedir, "project.xml");
		log.debug("pom : " + pom.getAbsolutePath() + (pom.exists() ? " " : " not ") + "found");
		return pom.exists();
	}
	
	private Dependency createDependencyFromProject(IProject referencedProject) throws Exception {
		if ( referencedProject.exists() )  {
                
            File referencedPom = FileUtils.getPom(referencedProject);
            //check if referencedPom exists, tho it should since we just have created it

            if ( !referencedPom.exists() ) {
                FileUtils.createPom(referencedProject);
            }
            ProjectReader reader = ProjectReader.getReader();
            return reader.extractDependency(referencedPom);
        }
		return null;
	}

	
	private boolean isValidEntry(IClasspathEntry entry) throws Exception {
		IPathResolver pathResolver = new DefaultPathResolver();
		boolean isLibrary = entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY;
		boolean isProject = entry.getEntryKind() == IClasspathEntry.CPE_PROJECT;
		boolean isVariable = entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE;
		boolean isClassFolder = FileUtils.isClassFolder(entry.getPath().toOSString(), eclipseProject.getProject());
		boolean isJdkLib = JavaProjectUtils.getJreEntryList(eclipseProject.getProject()).contains(pathResolver.getAbsolutePath(entry.getPath())); 
		return ((isLibrary && !isClassFolder) || isVariable || isProject) && !isJdkLib;
	}

	private void initializeMavenProjects() throws Exception {
		mavenProjectNodes = new MavenProjectNode[mavenProjects.size()];
		for (int i = 0; i < mavenProjects.size(); i++) {
			MavenProjectNode mavenProjectNode = new MavenProjectNode((MavenProject) mavenProjects.get(i), this);
			mavenProjectNodes[i] = mavenProjectNode;
		}
	}
	
	private void attachNature() throws Exception {
		if ( !eclipseProject.hasNature(JavaCore.NATURE_ID) ) {
			openAttachNatureDialog(eclipseProject);
		}
	}

	private void openAttachNatureDialog(IProject project) throws CoreException {
	    MessageDialog dialog = 
			new MessageDialog(
			        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Attach Java Nature...",
					null,
					"Current project doesnot have JavaNature. Should we attach it ? ",
					MessageDialog.QUESTION,
					new String[] {"Yes", "No"}, 0);
		int userChoice = dialog.open();
		if ( userChoice == Window.OK ) {
			JavaProjectUtils.attachJavaNature(eclipseProject);
		}
	}
	
	public boolean equals(Object obj) {
		if ( !(obj instanceof EclipseProjectNode )) {
			return false;
		}
		EclipseProjectNode node = (EclipseProjectNode) obj;
		return eclipseContainer.equals(node.eclipseContainer);
	}
	
	public ISynchronizationNode[] getChildren() {
		return mavenProjectNodes;
	}
	
	public Object getData() {
		return eclipseContainer;
	}
	
	public ISynchronizationNode getParent() {
		return null;
	}
	public boolean hasChildren() {
		return mavenProjectNodes != null && mavenProjectNodes.length > 0;
	}
	public String toString() {
		return eclipseProject.getName();
	}

	List getEclipseClasspathArtifacts() {
		return eclipseClasspathArtifacts;
	}

	List getEclipseSourceFolders() {
		return eclipseSourceFolders;
	}
	
	public void setMavenProjects(List mavenProjects) {
		this.mavenProjects = mavenProjects;
	}
}