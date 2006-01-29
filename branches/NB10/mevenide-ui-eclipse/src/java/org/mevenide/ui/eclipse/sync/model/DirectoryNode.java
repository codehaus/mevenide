/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
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
import org.mevenide.properties.writer.CarefulPropertiesWriter;
import org.mevenide.ui.eclipse.sync.model.properties.DirectoryPropertySource;
import org.mevenide.ui.eclipse.sync.model.properties.ReadOnlyDirectoryPropertySource;
import org.mevenide.ui.eclipse.util.FileUtils;
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
			List exclusionPatterns = findExclusionPatterns();
			excludeNodes = new ExcludeNode[exclusionPatterns.size()];
			for (int i = 0; i < excludeNodes.length; i++) {
				excludeNodes[i] = new ExcludeNode(this, (String) exclusionPatterns.get(i));
			}
		} 
		catch (Exception e) {
			String message = "Unable to read resources";  //$NON-NLS-1$
			log.error(message, e);
		}
	}

	private List findExclusionPatterns() {
		Set excludes = new TreeSet();
		Project mavenProject = ((Project) parentNode.getData());
		List allResources = getMavenProjectResources(mavenProject);
		for (int i = 0; i < allResources.size(); i++) {
			Resource nextResource = (Resource) allResources.get(i);
			if ( directory.getCleanPath().equals(SourceDirectoryUtil.stripBasedir(nextResource.getDirectory()).replaceAll("\\\\", "/")) ) {  //$NON-NLS-1$//$NON-NLS-2$
				if ( nextResource.getExcludes() != null && nextResource.getExcludes().size() > 0 ) {
				    excludes.addAll(nextResource.getExcludes());
				}
			}
		}
		return new ArrayList(excludes);
	}

	private List getMavenProjectResources(Project mavenProject) {
		List resources = new ArrayList();
		Build build = mavenProject.getBuild();
		if ( build != null ) {
		    if ( build.getResources() != null ) {
		        resources = build.getResources();
		    }
			if ( build.getUnitTest() != null && build.getUnitTest().getResources() != null ) {
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
		boolean isRoot = "${basedir}".equals(directory.getCleanPath()) || ".".equals(directory.getCleanPath()); //$NON-NLS-1$ //$NON-NLS-2$
		return  isRoot ? "${basedir}" : resolve(directory.getCleanPath()); //$NON-NLS-1$
	}
	
	
	public void addTo(IProject project) throws Exception {
	    if ( ProjectConstants.MAVEN_OUTPUT_DIRECTORY.equals(directory.getType() )) {
	        JavaProjectUtils.setDefaultOuputLocation(project, directory.getCleanPath());
	    }
	    else {
		    IFolder folder = project.getFolder(directory.getCleanPath());
		    createFolder(folder, true, true);
		    
		    IClasspathEntry entry = createSourceEntry(project);
		    
		    JavaProjectUtils.addClasspathEntry(JavaCore.create(project), entry);
	    }
	}
	
	private void createFolder(IFolder folder, boolean force, boolean local) throws Exception {
		
	    if ( !folder.exists() ) {
	    	IContainer parent= folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder)parent, force, local);
			}
			folder.create(force, local, null);
	    }
	}
	
	private IClasspathEntry createSourceEntry(IProject project) {
        String path = "/" + project.getName() + "/" + directory.getCleanPath(); //$NON-NLS-1$ //$NON-NLS-2$
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
        String[] exclusionPatterns = new String[excludeNodes.length];
        for (int i = 0; i < exclusionPatterns.length; i++) {
           	exclusionPatterns[i] = (String) excludeNodes[i].getData();
        }
		if ( ProjectConstants.MAVEN_RESOURCE.equals(directory.getType()) ) {
		    ProjectWriter.getWriter().addResource(directory.getCleanPath(), project.getFile(), exclusionPatterns);
		}
		else if ( ProjectConstants.MAVEN_TEST_RESOURCE.equals(directory.getType()) ) {
			ProjectWriter.getWriter().addUnitTestResource(directory.getCleanPath(), project.getFile(), exclusionPatterns);
		}
		else if ( ProjectConstants.MAVEN_OUTPUT_DIRECTORY.equals(directory.getType()) ) {
		    InputStream fis = null;
		    OutputStream fos = null;
		    try {
			    File propFile = FileUtils.getProjectPropertiesFile(((Project) parentNode.getData()).getFile().getParentFile());
			    Properties props = new Properties();
			    fis = new FileInputStream(propFile);
			    props.load(fis);
			    props.setProperty("maven.build.dest", directory.getCleanPath()); //$NON-NLS-1$
			    fos = new FileOutputStream(propFile);
				new CarefulPropertiesWriter().marshall(fos, props, fis);
		    }
		    finally {
		        if ( fis != null ) {
		            fis.close();
		        }
		        if ( fos != null ) {
		            fos.close();
		        }
		    }
		}
		else {
		    log.debug("adding " + directory.getCleanPath() + " failed - reason : unrecognized source type = " + directory.getType() ); //$NON-NLS-1$ //$NON-NLS-2$
		    ProjectWriter.getWriter().addSource(directory.getCleanPath(), project.getFile(), directory.getType());
		}
	}
	
	protected String getIgnoreLine() {
		return resolve(directory.getCleanPath());
	}
	
	public void removeFrom(Project project) throws Exception {
		String type = directory.getType();
		String path = directory.getPath();
		ProjectWriter.getWriter().removeDirectory((Project) parentNode.getData(), path, type);
	}
	
	public boolean equivalentEntry(IClasspathEntry entry) {
	    boolean sameType = entry.getEntryKind() == IClasspathEntry.CPE_SOURCE;
	    
	    String relativeEntryPath = entry.getPath().removeFirstSegments(1).toOSString().replaceAll("\\\\", "/");  //$NON-NLS-1$//$NON-NLS-2$
	    String artifactPath = resolve(directory.getCleanPath());
	    boolean equivalentPath = relativeEntryPath.equals(artifactPath);
	    
	    return sameType && equivalentPath;
	}
	
	public Object getAdapter(Class adapteeClass) {
		if ( adapteeClass == IPropertySource.class ) {
		    if ( getDirection() == ISelectableNode.OUTGOING_DIRECTION ) {
		        DirectoryPropertySource propertySource = new DirectoryPropertySource(directory);
		        propertySource.addPropertyChangeListener(this);
		        return propertySource;
		    }
	        return new ReadOnlyDirectoryPropertySource(this.directory);
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
