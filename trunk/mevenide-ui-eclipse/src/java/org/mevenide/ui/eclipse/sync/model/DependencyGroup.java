/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
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
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.util.FileUtil;
import org.mevenide.ui.eclipse.util.ProjectUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyGroup extends ArtifactGroup {
	private static Log log = LogFactory.getLog(DependencyGroup.class);
	
	private Map dependencies = new HashMap();
	
	public DependencyGroup(IProject project) {
		super(project);
		//initializeDependenciesInheritanceMap();
	}


	private void initializeDependenciesInheritanceMap() {
		for (int i = 0; i < artifacts.size(); i++) {
			DependencyWrapper dependency = (DependencyWrapper) artifacts.get(i);
			setDependencyInheritance(dependency.getDependency(), dependency.isInherited());
			log.debug("Updated Inheritance for Dependency : " + dependency + " (" + (dependency.isInherited()) + ")");   
        }	
	}
	

	protected void initialize() throws Exception {
		if ( dependencies == null ) {
			dependencies = new HashMap();
		}
		
		IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
		
		IPathResolver pathResolver = new DefaultPathResolver();
		
		for (int i = 0; i < classpathEntries.length; i++) {
			if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY
					&& !FileUtil.isClassFolder(classpathEntries[i].getPath().toOSString(), javaProject.getProject()) 
					&& !ProjectUtil.getJreEntryList(javaProject.getProject()).contains(pathResolver.getAbsolutePath(classpathEntries[i].getPath())) ) {
				//not the best way to get the absoluteFile ... 
				String path = classpathEntries[i].getPath().toOSString();
				if ( !new File(path).exists() ) {
					path = javaProject.getProject().getLocation().append(classpathEntries[i].getPath().removeFirstSegments(1)).toOSString();
				}
				Dependency dependency = DependencyFactory.getFactory().getDependency(path);
				addDependency(new DependencyWrapper(dependency, false, this));
				
			}
			
		}
		for (int i = 0; i < ProjectUtil.getCrossProjectDependencies().size(); i++) {
        	addDependency(new DependencyWrapper((Dependency) ProjectUtil.getCrossProjectDependencies().get(i), false, this));   
        }
		
		
	}
	
	public List getNonInheritedDependencies() {
		List nonInheritedDependencies = new ArrayList();
		
		for (int i = 0; i < artifacts.size(); i++) {
            Dependency dependency = ((DependencyWrapper) artifacts.get(i)).getDependency();
			if ( !((DependencyWrapper) artifacts.get(i)).isInherited() ) {
				nonInheritedDependencies.add(dependency);
            }
        }
		
		return nonInheritedDependencies;
	}
	
	public List getDependencies() {
		return artifacts;
	}

	public void setDependencies(List list) {
		artifacts = list;
	}
	
	public void addDependency(DependencyWrapper wrapper) {
		Dependency dependency = wrapper.getDependency();
		if ( dependency.getArtifactId() == null ) {
			dependency.setArtifactId("");
		}
		if ( dependency.getGroupId() == null ) {
			dependency.setGroupId("");
			//see if non resolving is due to a past unset mavenrepo property
			DependencyUtil.refreshGroupId(dependency);
		}
		if ( dependency.getVersion() == null ) {
			dependency.setVersion("");
		}
		if ( dependency.getType() == null ) {
			dependency.setType("");
		}
		if ( dependency.getJar() == null ) {
			//if ( dependency.getArtifact() == null ) {
			//dependency.setArtifact("");
			dependency.setJar("");
		}
		
		log.debug("Adding [" + DependencyUtil.toString(dependency));
		artifacts.add(wrapper);
		
	    
		for (int i = 0; i < excludedArtifacts.size(); i++) {
			Dependency excluded = (Dependency) excludedArtifacts.get(i);
			if ( excluded.getArtifact().equals(dependency.getArtifact())) {
			 	 excludedArtifacts.remove(excluded);
			 }
		}
		
		setDependencyInheritance(dependency, wrapper.isInherited());
	}
	
	public void setDependencyInheritance(Dependency dependency, boolean inheritance) {
		dependencies.remove(dependency);
		dependencies.put(dependency, new Boolean(inheritance));
	}
	
	public void excludeDependency(DependencyWrapper wrapper) {
		artifacts.remove(wrapper);
		dependencies.remove(wrapper.getDependency());
		excludedArtifacts.add(wrapper);
	}
	
	public List getExcludedDependencies() {
		return excludedArtifacts;
	}
	
	public boolean containsDependency(Dependency dependency) {
		boolean contains = false;
		for (int i = 0; i < artifacts.size(); i++) {
			log.debug("Testing equality between " + DependencyUtil.toString(((DependencyWrapper) artifacts.get(i)).getDependency()) + " and " + DependencyUtil.toString(dependency));
			if ( DependencyUtil.areEquals(((DependencyWrapper) artifacts.get(i)).getDependency(), dependency)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
}

