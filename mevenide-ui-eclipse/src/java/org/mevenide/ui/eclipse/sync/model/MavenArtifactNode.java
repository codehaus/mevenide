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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.views.properties.IPropertySource;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.DependencyPropertySource;
import org.mevenide.ui.eclipse.sync.model.properties.ReadOnlyDependencyPropertySource;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.ui.eclipse.util.JavaProjectUtils;
import org.mevenide.util.MevenideUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: DependencyNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public class MavenArtifactNode extends ArtifactNode {
    
    private static final Log log = LogFactory.getLog(MavenArtifactNode.class);
    
    private static final String MAVEN_REPO = "MAVEN_REPO"; //$NON-NLS-1$
    private static final String SEPARATOR = " : "; //$NON-NLS-1$
	
	private Artifact artifact;
	private MavenProjectNode parent;
	
	private PropertyNode[] properties;
	private ILocationFinder locationFinder;
	
	public MavenArtifactNode(Artifact artifact, MavenProjectNode project) {
		this.artifact = artifact;
		parent = project;
		locationFinder = new LocationFinderAggregator(new DefaultQueryContext(((Project) project.getData()).getFile().getParentFile()));
        ((LocationFinderAggregator)locationFinder).setCustomLocationFinder(Mevenide.getInstance().getPOMManager().getCustomLocationFinder());
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
		return new File(artifact.getPath()).getName() !=  null && new File(artifact.getPath()).getName().equals(new File(node.artifact.getPath()).getName()) ;
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

	    String groupId = artifact.getDependency().getGroupId(); 
	    groupId = resolve(groupId) + SEPARATOR;
	    
	    String artifactId = artifact.getDependency().getArtifactId();
		artifactId = resolve(artifactId);
	    
	    String version = artifact.getDependency().getVersion();
	    version = version != null ? resolve(version) : null;
	    
		return groupId + artifactId + (version != null ? SEPARATOR + version : "") ; //$NON-NLS-1$
	}
	
	
	/**
	 * @todo manage potential causes of failure : 
	 * o JavaModelException (CoreException) if project already added
	 * o ResourceException if .project is out of sync
	 * 
	 */
	public void addTo(IProject project) throws Exception {
		boolean treatAsEclipseDependency = false;
		String eclipseDependencyProperty = artifact.getDependency().getProperty("eclipse.dependency"); //$NON-NLS-1$
		treatAsEclipseDependency = "true".equals(eclipseDependencyProperty); //$NON-NLS-1$
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
		String mavenRepo = locationFinder.getMavenLocalRepository();
		Dependency dep = artifact.getDependency();
		String artifactPath = artifact.getPath();
		
		if ( "version".equals(artifact.getOverrideType()) && 
				artifactPath.replaceAll("\\\\","/").startsWith(mavenRepo.replaceAll("\\\\","/")) ) {
			//not sure if this is correct but i experiment strange behaviour with version override
			//jar is set to MAVEN_REPO/MAVEN_REPO/<repo relative path>
			//so extract name if jar startsWith MAVEN_REPO
			//if no repeating path will be corrected afterwards
			dep.setJar(new File(artifactPath).getName());
		}
		if ( dep.getJar() != null && !dep.getJar().replaceAll("\\\\","/").startsWith(mavenRepo.replaceAll("\\\\","/")) ) { //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
			artifactPath = new File(mavenRepo, dep.getGroupId() + "/" + dep.getType() + "s/" + dep.getJar()).getAbsolutePath();
		}
		
		artifactPath = resolve(artifactPath);
		
		if ( artifactPath.replaceAll("\\\\","/").startsWith(mavenRepo.replaceAll("\\\\","/")) ) {  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
		    try {
                artifactPath = MevenideUtils.makeRelativePath(new File(mavenRepo), artifactPath);
                //create MAVEN_REPO variable if not exists
                IPath mavenRepoVar = JavaCore.getClasspathVariable(MAVEN_REPO);
                if ( mavenRepoVar == null ) {
                    try {
                        JavaCore.setClasspathVariable(MAVEN_REPO, new Path(mavenRepo), null);
                    }
                    catch( Exception e ) {
                        String message = "Unable to set MAVEN_REPO variable";  //$NON-NLS-1$
                        log.error(message, e);     
                    }
                }
                return JavaCore.newVariableEntry(new Path(MAVEN_REPO + "/" + artifactPath.replaceAll("\\\\","/")), null, null);   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            }
            catch (IOException e) {
                String message = "Unable to get mavenRepo relative Path for " + artifactPath;  //$NON-NLS-1$
                log.error(message, e);
            }
		}
		return JavaCore.newLibraryEntry(new Path(artifactPath), null, null);
		
	}
	
	private IClasspathEntry createNewProjectEntry() throws Exception {
		assertJavaNature();
		//maven-eclipse-plugin assumes project name = artifactId when eclipse.dependency is set to true
		//follow same pattern here tho i dont think this is very accurate. need to think of another solution
		String referencedProjectName = artifact.getDependency().getArtifactId();
		referencedProjectName = resolve(referencedProjectName);
		return JavaCore.newProjectEntry(new Path("/" + referencedProjectName)); //$NON-NLS-1$
	}
	
	private void assertJavaNature() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("/" + artifact.getDependency().getArtifactId()); //$NON-NLS-1$
		if ( !project.hasNature(JavaCore.NATURE_ID) ) {
			JavaProjectUtils.attachJavaNature(project);
		}
	}
	
	public void addTo(Project project, boolean shouldWriteProperties) throws Exception {
	    if ( project.getArtifacts() == null ) {
	        project.setArtifacts(new ArrayList());
	    }
	    if ( project.getDependencies() == null ) {
	        project.setDependencies(new ArrayList());
	    }
	    
	    project.getArtifacts().add(artifact);
	    List artifacts = new ArrayList(project.getArtifacts());
	    
	    ProjectWriter.getWriter().setArtifacts(artifacts, project, shouldWriteProperties);
	    
	    if ( shouldWriteProperties ) {
	        IContainer eclipseContainer = ((IContainer) parent.getParent().getData());
	        FileUtils.refreshProperties(eclipseContainer);
	    }
	}
	
	

    public void addTo(Project project) throws Exception {
	    addTo(project, false);
	}
	
	protected String getIgnoreLine() {
		return toString();
	}
	
	public void removeFrom(Project project) throws Exception {
	    try {
            ProjectWriter.getWriter().removeArtifact((Project) parent.getData(), artifact);
        }
        catch (Exception e) {
            String message = "Unable to remove artifact " + artifact.getDependency() + " from pom " + project.getName();   //$NON-NLS-1$//$NON-NLS-2$
            log.error(message, e);
        }
		
	}
	
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
		    if ( getDirection() == ISelectableNode.OUTGOING_DIRECTION ) {
				DependencyPropertySource propertySource = new DependencyPropertySource(artifact.getDependency());
				propertySource.addPropertyChangeListener(this);
				return propertySource;
		    }
	        return new ReadOnlyDependencyPropertySource(artifact.getDependency());
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
	
	public boolean equivalentEntry(IClasspathEntry entry) {
	    //@todo manage CPE_VARIABLE
	    if ( entry.getEntryKind() != IClasspathEntry.CPE_PROJECT && entry.getEntryKind() != IClasspathEntry.CPE_LIBRARY && entry.getEntryKind() != IClasspathEntry.CPE_VARIABLE ) { 	
		    return false;
		}
	    
	    Artifact a = getEntryArtifact(entry);
        
	    boolean equivalentDependencies = DependencyUtil.areEquals((Project) parent.getData(), artifact.getDependency(), a.getDependency());
    
        boolean sameKind = hasSameKind(entry);
	   
        return sameKind && equivalentDependencies;
	}

    private Artifact getEntryArtifact(IClasspathEntry entry) {
	    IProject project = (IProject) parent.getParent().getData();
        Artifact artifactEntry = null;
	    try {
	    	if ( entry.getEntryKind() == IClasspathEntry.CPE_PROJECT ) {
	            artifactEntry = JavaProjectUtils.createArtifactFromProjectEntry(project, entry);
	        }
	    	if ( entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY || 
	    	        entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE ) {
	        	artifactEntry = JavaProjectUtils.createArtifactFromLibraryEntry(project, entry);
	        }
        }
        catch (Exception e) {
            String message = "Unable to retrieve artifact from entry " + entry.getPath() + " in the context of project " + project.getName(); //$NON-NLS-1$ //$NON-NLS-2$
            log.error(message, e);
        }
        return artifactEntry;
    }

    private boolean hasSameKind(IClasspathEntry entry) {
        boolean sameKind = false;
        String eclipseDependency = artifact.getDependency().getProperty("eclipse.dependency"); //$NON-NLS-1$
        if ( eclipseDependency != null && "true".equals(eclipseDependency) ) { //$NON-NLS-1$
            sameKind = entry.getEntryKind() == IClasspathEntry.CPE_PROJECT;
        }
        else {
           sameKind = entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY || 
           				entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE;
        }
        return sameKind;
    }
	
    public void addProperty(String key, String value) {
        Dependency dependency = ((Artifact) this.getData()).getDependency();
		dependency.addProperty(key + SEPARATOR + value);
		dependency.resolvedProperties().put(key, value);
		
		PropertyNode propertyNode = new PropertyNode(this, key, value);
		PropertyNode[] newNodes = new PropertyNode[this.getChildren().length + 1];
		System.arraycopy(this.getChildren(), 0, newNodes, 0, this.getChildren().length);
		newNodes[this.getChildren().length] = propertyNode;
		
		this.properties = newNodes;
    }
    
    
}
