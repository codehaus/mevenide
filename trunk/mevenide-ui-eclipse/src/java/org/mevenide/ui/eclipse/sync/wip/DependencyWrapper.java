/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.sync.wip;

import java.io.File;
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
import org.mevenide.Environment;
import org.mevenide.project.io.ProjectWriter;

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
	
	public DependencyWrapper(Dependency dependency) {
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
		if ( dependency.getType() == null || dependency.isAddedToClasspath() ) { //should check maven.jar.override property also..
			String path = null;
			if ( dependency.getJar() == null ) {
				String type = dependency.getType() == null ? "jar" : dependency.getType();
				File group = new File(Environment.getMavenLocalRepository(), dependency.getGroupId());
				path = new File(group, type + "s/" + dependency.getArtifactId() + "-" + dependency.getVersion() + "." + type).getAbsolutePath();
			}
			else {
				path = new File(dependency.getJar()).getAbsolutePath();
			}
			newEntry = JavaCore.newLibraryEntry(new Path(path), null, null);
		}

		return newEntry;
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
}
