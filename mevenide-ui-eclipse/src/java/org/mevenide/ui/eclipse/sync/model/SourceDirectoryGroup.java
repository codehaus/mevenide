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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.ProjectConstants;
import org.mevenide.ui.eclipse.DefaultPathResolver;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryGroup extends ArtifactGroup {
	
	public SourceDirectoryGroup(IProject project)  {
		super(project);	
	}
	
	protected void initialize() throws Exception {
		IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
		for (int i = 0; i < classpathEntries.length; i++) {
			if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				String path = new DefaultPathResolver().getRelativeSourceDirectoryPath(classpathEntries[i], javaProject.getProject());
				SourceDirectory sourceDirectory = new SourceDirectory(path);
				sourceDirectory.setDirectoryType(ProjectConstants.MAVEN_SRC_DIRECTORY); 
				addSourceDirectory(sourceDirectory);
				
			}
		}
	}
	
	public void addSourceDirectory(SourceDirectory sourceDirectory) {
		for (int j = 0; j < excludedArtifacts.size(); j++) {
			SourceDirectory excluded = (SourceDirectory) excludedArtifacts.get(j);
			if ( sourceDirectory.getDirectoryPath().equals(excluded.getDirectoryPath()) ) {
				excludedArtifacts.remove(excluded);
				sourceDirectory.setDirectoryType(excluded.getDirectoryType());
			}
		}
		artifacts.add(sourceDirectory);
	}
	
	public List getSourceDirectories() {
		return artifacts;
	}
	
	public List getNonInheritedSourceDirectories() {
		List nonInheritedSourceDirectories = new ArrayList();
		for (int i = 0; i < artifacts.size(); i++) {
            SourceDirectory sourceDirectory = (SourceDirectory) artifacts.get(i);
            if ( !sourceDirectory.isInherited() ) {
				nonInheritedSourceDirectories.add(sourceDirectory);
            }
        }
		return nonInheritedSourceDirectories;	
	}
	
	public void excludeSourceDirectory(SourceDirectory directory) {
		artifacts.remove(directory);
		excludedArtifacts.add(directory);
	}
	
	public List getExcludedSourceDirectories() {
		return excludedArtifacts;
	}

	public void setSourceDirectories(List list) {
		artifacts = list;
	}

	public boolean equals(Object obj) {
		return  obj != null 
				&& (obj instanceof SourceDirectoryGroup)
				&& areEquals(((SourceDirectoryGroup)obj).artifacts, artifacts)
				&& areEquals(((SourceDirectoryGroup)obj).javaProject, javaProject);
	}
	
	private boolean areEquals(Object o1, Object o2) {
		boolean allNull = 
				o1 == null && o2 == null;
		boolean firstNotNull = 
				o1 != null && o1.equals(o2);
		return allNull || firstNotNull;
	}
	
	
}
