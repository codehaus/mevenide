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
package org.mevenide.ui.eclipse.sync.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.maven.project.Project;
import org.apache.maven.util.StringInputStream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class ArtifactWrapper {
	public abstract void addTo(IProject project) throws Exception;
	
	public abstract void addTo(Project project) throws Exception;
	
	public abstract void removeFrom(Project project) throws Exception;
	
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
		
		mvnIgnoreFile.appendContents(new StringInputStream("\r\n" + ignoreLine), true, true, null);
	}
	
	public void addToMvnIgnore(IContainer container) throws Exception {
		String ignoreLine = getIgnoreLine();
		
		IFile mvnIgnoreFile = FileUtils.assertIgnoreFileExists(container);
		
		addIgnoreLine(ignoreLine, mvnIgnoreFile);
	}
	
	public void addToMvnIgnore(Project project) throws Exception {
		String ignoreLine = getIgnoreLine();
		
		IFile mvnIgnoreFile = FileUtils.assertIgnoreFileExists(project);
		
		addIgnoreLine(ignoreLine, mvnIgnoreFile);
	}

	
	protected abstract String getIgnoreLine();
	
}
