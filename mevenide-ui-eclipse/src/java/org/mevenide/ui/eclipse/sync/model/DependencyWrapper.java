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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;

/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class DependencyWrapper extends ArtifactWrapper {
	private static Log log = LogFactory.getLog(DependencyWrapper.class);
	
	
	private Dependency dependency;
	
	public DependencyWrapper(File declaringPom, Dependency dependency) {
		super(declaringPom);
		this.dependency = dependency;
	}
	
	public void addTo(IProject project) throws Exception {
		IClasspathEntry newEntry = newClasspathEntry(dependency);

		if ( newEntry != null ) {
			
			addClasspathEntry(newEntry, project);
		}
	}
	
	private IClasspathEntry newClasspathEntry(Dependency dependency) throws Exception {
		IClasspathEntry newEntry = null;
		String eclipseDependency = (String) dependency.resolvedProperties().get("eclipse.dependency"); 
		log.debug("eclipse.dependency = " + eclipseDependency);
		if ( "true".equals(eclipseDependency) ) {
			newEntry = newProjectEntry(dependency);
		}
		else { 
			newEntry = newLibraryEntry(dependency);
		}
		return newEntry;
	}
	
	private IClasspathEntry newLibraryEntry(Dependency dependency) {
		IClasspathEntry newEntry = null;
		//crap ! we should use project.artifacts. for that we have to instantiate a jelly context and create a project instance with MavenUtils
		//if possible refactor *all* code that way (MavenUtils) 
		if ( dependency.getType() == null || dependency.isAddedToClasspath() ) { 
			
			String path = null;
			
			if ( isMavenOverrideOn() ) {
				path = getPathOverride().replaceAll("\\\\", "/");
			}
			
			if ( path == null ) {
				path = buildArtifactPath(dependency.getVersion());
			}
			
			newEntry = JavaCore.newLibraryEntry(new Path(path), null, null);
		}

		return newEntry;
	}

	private String buildArtifactPath(String version) {
		String type = dependency.getType() == null ? "jar" : dependency.getType();
		File group = new File(ConfigUtils.getDefaultLocationFinder().getMavenLocalRepository(), dependency.getGroupId());
		return new File(group, type + "s/" + dependency.getArtifactId() + "-" + version + "." + type).getAbsolutePath();
	}

	private IClasspathEntry newProjectEntry(Dependency dependency) throws CoreException {
		IClasspathEntry newEntry;

		//respecting maven-eclipse-plugin
		newEntry = JavaCore.newProjectEntry(new Path("/" + dependency.getArtifactId()));

		//attachJavaNature - there should a lower-level way to do that
		IProject project = (IProject) ResourcesPlugin.getWorkspace().getRoot().getProject("/" + dependency.getArtifactId());
		if ( !project.hasNature(JavaCore.NATURE_ID) ) {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = JavaCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
			project.getNature(JavaCore.NATURE_ID).configure();
			IJavaProject javaProject = JavaCore.create(project);
			javaProject.setRawClasspath(new IClasspathEntry[0], null);
		}

		return newEntry;
	}
	
	public void addTo(Project project) throws Exception {
		List dependencies = project.getDependencies();
		dependencies.add(dependency);
		ProjectWriter.getWriter().setDependencies(dependencies, project.getFile());
	}
	
	public void removeFrom(Project project) throws Exception {
		List newDeps = project.getDependencies();
		newDeps.remove(dependency);
		project.setDependencies(newDeps);
		ProjectWriter.getWriter().setDependencies(newDeps, project.getFile());
	}
	
	protected String getIgnoreLine() {
		return dependency.getGroupId() + ":" + dependency.getArtifactId();
	}
	
	private boolean isMavenOverrideOn() {
		PropertyModel model = getPropertyModel();
		if ( model == null ) {
			return false;
		}
		KeyValuePair pair = model.findByKey("maven.jar.override");
		return pair != null 
				&& pair.getValue() != null 
				&& (pair.getValue().equals("on") || pair.getValue().equals("1") || pair.getValue().equals("true")); 
	}
	
	private PropertyModel getPropertyModel() {
		PropertyModel model = null;
		
		File projectProperties = new File(getDeclaringPom().getParentFile(), "project.properties");
		
		if ( projectProperties.exists() ) {
		
			PropertyModelFactory factory = PropertyModelFactory.getFactory();
			
			try {
				model = factory.newPropertyModel(projectProperties);
			} 
			catch (IOException e) {
				log.error("Unable to mount project PropertyModel", e);
			}
		}
		
		return model;
	}

	private String getPathOverride() {
		String result = null;
		
		PropertyModel model = getPropertyModel();
		KeyValuePair kvp = model.findByKey("maven.jar." + dependency.getArtifactId());
		
		if ( kvp != null ) {
			String value = kvp.getValue();
			if ( new File(value).exists() ) {
			    result = value;
			}
			else {
			    //assume value is a version
			    String path = buildArtifactPath(value);
			    System.err.println(path);
			    if ( new File(path).exists() ) {
			    	result = path;
			    }
			    else {
			    	//if no valid artifact file can be constructed then it is problematic.. 
			        //@TODO warn user about that problem..
			        result = value;
			    }
			}
		}
		
		return resolvePathOverride(result);
	}
	
	private String resolvePathOverride(String result) {
		if ( result != null && !new File(result).isAbsolute() ) {
			if ( result.startsWith("{basedir}") ) {
			    result = result.replaceAll("{basedir}", this.getDeclaringPom().getParent());
			}
			else {
			    result = new File(this.getDeclaringPom().getParent(), result).getAbsolutePath();
			}
		}
		return result;
	}
}
