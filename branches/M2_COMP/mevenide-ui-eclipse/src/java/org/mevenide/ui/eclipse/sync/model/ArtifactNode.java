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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.util.StringInputStream;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: ArtifactNode.java,v 1.1 12 avr. 2004 Exp gdodinet 
 * 
 */
public abstract class ArtifactNode implements ISynchronizationNode, ISelectableNode {

	private int direction;
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public abstract void addTo(IProject project) throws Exception;
	
	public abstract void addTo(MavenProject project) throws Exception;
	
	/**
	 * default implementation just delegates to addTo(Project) 
	 * subclasses that need to know if project.properties should be overriden 
	 * must override this method
	 */
	public void addTo(MavenProject project, boolean shouldWriteProperties) throws Exception {
		addTo(project);
	}
	
	public abstract void removeFrom(MavenProject project) throws Exception;
	
	protected void addClasspathEntry(IClasspathEntry newEntry, IProject project) throws JavaModelException {
		IJavaProject javaProject = (IJavaProject) JavaCore.create(project);
		IClasspathEntry[] cpEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newCpEntries = new IClasspathEntry[cpEntries.length + 1];

		System.arraycopy(cpEntries, 0, newCpEntries, 0, cpEntries.length);
		newCpEntries[cpEntries.length] = newEntry;

		javaProject.setRawClasspath(newCpEntries, null);
	}

	protected void addIgnoreLine(String ignoreLine, IFile mvnIgnoreFile) throws CoreException, IOException {
		InputStream is = mvnIgnoreFile.getContents();
		Reader reader = new InputStreamReader(is);
		
		mvnIgnoreFile.appendContents(new StringInputStream(Character.LINE_SEPARATOR + ignoreLine), true, true, null);
	}
	
	public void addToMvnIgnore(IContainer container) throws Exception {
		String ignoreLine = getIgnoreLine();
		
		IFile mvnIgnoreFile = FileUtils.assertIgnoreFileExists(container);
		
		addIgnoreLine(ignoreLine, mvnIgnoreFile);
	}
	
	public void addToMvnIgnore(MavenProject project) throws Exception {
		String ignoreLine = getIgnoreLine();
		
		IFile mvnIgnoreFile = FileUtils.assertIgnoreFileExists(project);
		
		addIgnoreLine(ignoreLine, mvnIgnoreFile);
	}

	protected abstract String getIgnoreLine();
	
	public boolean select(int direction) {
		System.err.println("selecting node [" + getData() + "-" + getDirection() + "]" + direction);
		return this.direction == direction;
	}
}
