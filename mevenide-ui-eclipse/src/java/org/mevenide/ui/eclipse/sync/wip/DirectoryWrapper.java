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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.ProjectConstants;
import org.mevenide.project.io.ProjectWriter;

/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class DirectoryWrapper extends SourceFolder {
	private static Log log = LogFactory.getLog(DirectoryWrapper.class); 
	
	private Directory directory ;
	
	public DirectoryWrapper(Directory directory) {
		this.directory = directory;
	}
	
	public void addTo(IProject project) throws Exception {
		String type = directory.getType();
		String path = directory.getPath();
		log.debug("adding src entry to .classpath : "  + path + "(" + type + ")");
		
		IClasspathEntry srcEntry = newSourceEntry(path, project);
		
		addClasspathEntry(srcEntry, project);
	}

	public void addTo(Project project) throws Exception {
		String type = directory.getType();
		String path = directory.getPath();
		
		ProjectWriter.getWriter().addSource(path, project.getFile(), type);
		
	}
	
	public void removeFrom(Project project) throws Exception {
		if ( project.getBuild() != null ) { 
			String type = directory.getType();
			String path = directory.getPath();
			
			if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(type) ) {
				project.getBuild().setSourceDirectory(null);
			}
			if ( ProjectConstants.MAVEN_TEST_DIRECTORY.equals(type) ) {
				project.getBuild().setUnitTestSourceDirectory(null);
			}
			if ( ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(type) ) {
				project.getBuild().setAspectSourceDirectory(null);
			}
			ProjectWriter.getWriter().write(project);
		}
	}
}
