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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.project.source.SourceDirectoryUtil;
import org.mevenide.properties.resolver.util.ResolverUtils;
import org.mevenide.ui.eclipse.util.JavaProjectUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DirectoryNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class DirectoryNode extends ArtifactNode {
	private static final Log log = LogFactory.getLog(DirectoryNode.class);
	
	private Directory directory;
	
	private MavenProjectNode parentNode;
	
	private ExcludeNode[] excludeNodes;
	//private IncludeNode[] includeNodes;
	
	public DirectoryNode(Directory dir, MavenProjectNode project) {
		directory = dir;
		parentNode = project;
		initialize();
	}
	
	private void initialize() {
		try {
			Map sources = ProjectReader.getReader().readSourceDirectories(((Project) parentNode.getData()).getFile());
			if ( sources.keySet().contains(directory.getCleanPath()) ) {
				return;
			}
			Resource resource = findLessExclusiveEquivalentResource();
			if ( resource != null && resource.getExcludes() != null) {
				excludeNodes = new ExcludeNode[resource.getExcludes().size()];
				for (int i = 0; i < excludeNodes.length; i++) {
					excludeNodes[i] = new ExcludeNode(this, (String) resource.getExcludes().get(i));
				}
			}
		} 
		catch (Exception e) {
			String message = "Unable to read resources"; 
			log.error(message, e);
		}
	}

	private Resource findLessExclusiveEquivalentResource() {
		Resource resource = null;
		Project mavenProject = ((Project) parentNode.getData());
		List allResources = getMavenProjectResources(mavenProject);
		for (int i = 0; i < allResources.size(); i++) {
			Resource nextResource = (Resource) allResources.get(i);
			if ( directory.getCleanPath().equals(SourceDirectoryUtil.stripBasedir(nextResource.getDirectory()).replaceAll("\\\\", "/")) ) {
				if ( resource == null ) {
					resource = nextResource;
				}
				else {
					resource = resource.getExcludes().size() > nextResource.getExcludes().size() ? nextResource : resource;
				}
			}
		}
		return resource;
	}

	private List getMavenProjectResources(Project mavenProject) {
		List resources = new ArrayList();
		Build build = mavenProject.getBuild();
		if ( build != null ) {
			resources = build.getResources();
			if ( build.getUnitTest() != null ) {
				resources.addAll(build.getUnitTest().getResources());
			}
		}
		return resources;
	}

	public boolean equals(Object obj) {
		if ( !(obj instanceof DirectoryNode) ) {
			return false;
		}
		DirectoryNode node = (DirectoryNode) obj;
		return directory.equals(node.directory);
	}
	public ISynchronizationNode[] getChildren() {
		return excludeNodes;
	}
	public Object getData() {
		return directory;
	}
	public ISynchronizationNode getParent() {
		return parentNode;
	}
	public boolean hasChildren() {
		return excludeNodes != null && excludeNodes.length > 0;
	}
	public String toString() {
		return resolve(directory.getCleanPath());
	}
	
	
	public void addTo(IProject project) throws Exception {
	    IClasspathEntry entry = createSourceEntry(project);
	    if ( !project.getFolder(directory.getCleanPath()).exists() ) {
	        project.getFolder(directory.getCleanPath()).create(true, true, null);
	    }
	    JavaProjectUtils.addClasspathEntry(JavaCore.create(project), entry);
	}
	
	private IClasspathEntry createSourceEntry(IProject project) {
        String path = "/" + project.getName() + "/" + directory.getCleanPath();
        IPath[] excludePatterns = null;
        if ( excludeNodes != null ) {
            excludePatterns = new Path[excludeNodes.length];
	        for (int i = 0; i < excludeNodes.length; i++) {
	            excludePatterns[i] = new Path((String) excludeNodes[i].getData());
	        }
        }
        return JavaCore.newSourceEntry(new Path(path), excludePatterns);
    }

    public void addTo(Project project) throws Exception {
		if ( ProjectConstants.MAVEN_RESOURCE.equals(directory.getType()) ) {
			ProjectWriter.getWriter().addResource(directory.getCleanPath(), project.getFile());
		}
		else if ( ProjectConstants.MAVEN_TEST_RESOURCE.equals(directory.getType()) ) {
			ProjectWriter.getWriter().addUnitTestResource(directory.getCleanPath(), project.getFile());
		}
		else {
		    log.debug("adding " + directory.getCleanPath() + " as " + directory.getType());
		    ProjectWriter.getWriter().addSource(directory.getCleanPath(), project.getFile(), directory.getType());
		}
	}
	
	protected String getIgnoreLine() {
		return ResolverUtils.resolve((Project) parentNode.getData(), directory.getCleanPath());
	}
	
	public void removeFrom(Project project) throws Exception {
		String type = directory.getType();
	}
	
	public Object getAdapter(Class adapteeClass) {
		if ( adapteeClass == IPropertySource.class ) {
			DirectoryPropertySource propertySource = new DirectoryPropertySource(directory);
			propertySource.addPropertyChangeListener(this);
			return propertySource;
		}
		return null;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if ( DirectoryPropertySource.DIRECTORY_TYPE.equals(event.getProperty()) ) {
			directory.setType((String) event.getNewValue());
			propagateNodeChangeEvent();
		}
	}
	
	public void setExcludeNodes(ExcludeNode[] excludeNodes) {
		this.excludeNodes = excludeNodes;
	}
	
}
 
