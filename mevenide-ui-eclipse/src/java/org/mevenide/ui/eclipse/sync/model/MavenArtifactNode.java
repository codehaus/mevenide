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
import java.util.List;

import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.project.dependency.DefaultDependencyPathFinder;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.ui.eclipse.editors.properties.DependencyPropertySource;
import org.mevenide.ui.eclipse.util.JavaProjectUtils;
import org.mevenide.util.MevenideUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DependencyNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenArtifactNode extends ArtifactNode {
	
	private Artifact artifact;
	private MavenProjectNode parent;
	
	private PropertyNode[] properties;
	
	public MavenArtifactNode(Artifact artifact, MavenProjectNode project) {
		this.artifact = artifact;
		parent = project;
		initialize();
	}
	
	private void initialize() {
		List list = artifact.getDependency().getProperties();
		properties = new PropertyNode[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String[] resolvedProperty = MevenideUtils.resolveProperty((String) list.get(i));
			properties[i] = new PropertyNode(this, resolvedProperty[0], resolvedProperty[1]);
		}
	}
	
	public boolean equals(Object obj) {
		if ( !(obj instanceof MavenArtifactNode) ) {
			return false;
		}
		MavenArtifactNode node = (MavenArtifactNode) obj;
		return DependencyUtil.areEquals(this.artifact.getDependency(), node.artifact.getDependency());
	}
	
	public ISynchronizationNode[] getChildren() {
		return properties;
	}
	
	public Object getData() {
		return artifact;
	}
	public ISynchronizationNode getParent() {
		return parent;
	}
	public boolean hasChildren() {
		return properties != null && properties.length > 0;
	}
	public String toString() {
		String groupId = artifact.getDependency().getGroupId() + " : ";
		String artifactId = artifact.getDependency().getArtifactId();
		String version = artifact.getDependency().getVersion();
		return groupId + artifactId + (version != null ? " : " + version : "") ;
	}
	
	/**
	 * @todo manage potential causes of failure : 
	 * o JavaModelException (CoreException) if project already added
	 * o ResourceException if .project is out of sync
	 * 
	 */
	public void addTo(IProject project) throws Exception {
		boolean treatAsEclipseDependency = false;
		String eclipseDependencyProperty = (String) artifact.getDependency().getProperty("eclipse.dependency");
		treatAsEclipseDependency = "true".equals(eclipseDependencyProperty);
		IClasspathEntry entry = null;
		if ( treatAsEclipseDependency ) {
			entry = createNewProjectEntry();
		}
		else {
			entry = createNewLibraryEntry();
		}
		JavaProjectUtils.addClasspathEntry(JavaCore.create(project), entry);
	}
	
	private IClasspathEntry createNewLibraryEntry() {
		String artifactPath = new DefaultDependencyPathFinder(artifact.getDependency()).resolve();
		return JavaCore.newLibraryEntry(new Path(artifactPath), null, null);
	}
	
	private IClasspathEntry createNewProjectEntry() throws Exception {
		assertJavaNature();
		//maven-eclipse-plugin assumes project name = artifactId when eclipse.dependency is set to true
		//follow same pattern here tho i dont think this is very accurate. need to think of another solution
		return JavaCore.newProjectEntry(new Path("/" + artifact.getDependency().getArtifactId()));
	}
	
	private void assertJavaNature() throws Exception {
		IProject project = (IProject) ResourcesPlugin.getWorkspace().getRoot().getProject("/" + artifact.getDependency().getArtifactId());
		if ( !project.hasNature(JavaCore.NATURE_ID) ) {
			JavaProjectUtils.attachJavaNature(project);
		}
	}
	
	public void addTo(Project project) throws Exception {
		project.getArtifacts().add(artifact);
		ProjectWriter.getWriter().write(project);
	}
	
	protected String getIgnoreLine() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void removeFrom(Project project) throws Exception {
		List projectArtifacts = project.getArtifacts();
		List iteratedList = new ArrayList(projectArtifacts);
		int idx = 0;
		for (int i = 0; i < iteratedList.size(); i++) {
			Artifact iteratedArtifact = (Artifact) iteratedList.get(i);
			if ( artifact.getPath().equals(iteratedArtifact.getPath()) ) {
				projectArtifacts.remove(idx);
			}
			else {
				idx++;
			}
		}
		ProjectWriter.getWriter().write(project);
	}
	
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
			DependencyPropertySource propertySource = new DependencyPropertySource(artifact.getDependency());
			propertySource.addPropertyChangeListener(this);
			return propertySource;
		}
		return null;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if ( DependencyPropertySource.DEPENDENCY_ARTIFACTID.equals(event.getProperty()) ) {
			artifact.getDependency().setArtifactId((String) event.getNewValue());
		}
		if ( DependencyPropertySource.DEPENDENCY_GROUPID.equals(event.getProperty()) ) {
			artifact.getDependency().setGroupId((String) event.getNewValue());
		}
		if ( DependencyPropertySource.DEPENDENCY_JAR.equals(event.getProperty()) ) {
			artifact.getDependency().setJar((String) event.getNewValue());
		}
		if ( DependencyPropertySource.DEPENDENCY_TYPE.equals(event.getProperty()) ) {
			artifact.getDependency().setType((String) event.getNewValue());
		}
		if ( DependencyPropertySource.DEPENDENCY_URL.equals(event.getProperty()) ) {
			artifact.getDependency().setUrl((String) event.getNewValue());
		}
		if ( DependencyPropertySource.DEPENDENCY_VERSION.equals(event.getProperty()) ) {
			artifact.getDependency().setVersion((String) event.getNewValue());
		}
		propagateNodeChangeEvent();
	}
}
