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
package org.mevenide.ui.eclipse.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.mevenide.project.io.ProjectSkeleton;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.PomSynchronizer;
import org.mevenide.util.MevenideUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class FileUtil {
	
	private static Log log = LogFactory.getLog(FileUtil.class);
	
	private FileUtil() {
	}
	
	

	public static boolean inLocalRepository(String entryPath) {
		File localRepo = new File(Mevenide.getPlugin().getMavenRepository());
		return MevenideUtil.findFile(localRepo, entryPath);
	}

	public static boolean isClassFolder(String entryPath, IProject project) {
		return new File(project.getLocation().append(new Path(entryPath).removeFirstSegments(1)).toOSString()).isDirectory();
	}

	public static void createPom(IProject project) throws Exception, CoreException {
		 log.debug("Creating pom skeleton using template : " + Mevenide.getPlugin().getPomTemplate());
		 String referencedPomSkeleton = ProjectSkeleton.getSkeleton( project.getName(), Mevenide.getPlugin().getPomTemplate() );
		 IFile referencedProjectFile = project.getFile("project.xml"); 
		 referencedProjectFile.create(new ByteArrayInputStream(referencedPomSkeleton.getBytes()), false, null);
	}

	public static File getPom(IProject project) {
		//weird trick to fix a NPE. dont know yet why we got that NPE
		if ( project.exists() ) {
			IPathResolver pathResolver = new DefaultPathResolver();
			IPath referencedProjectLocation = project.getLocation();
			return new File(pathResolver.getAbsolutePath(referencedProjectLocation.append("project.xml")) );
		}
		return null;
	}

	public static void refresh(IProject project) throws Exception {
		IFile projectFile = project.getFile("project.xml");
		projectFile.refreshLocal(IResource.DEPTH_ZERO, null);
		IFile propertiesFile = project.getFile("project.properties");
		if ( propertiesFile.exists() ) {
			propertiesFile.refreshLocal(IResource.DEPTH_ZERO, null);
		}
	}

	public static void assertPomNotEmpty(IFile pom) {
		try {
			if ( pom.exists() ) {
				InputStream inputStream = pom.getContents(true);
			
				if ( inputStream.read() == -1 ) {
					InputStream stream = PomSynchronizer.class.getResourceAsStream("/templates/standard/project.xml"); 
					pom.setContents(stream, true, true, null);
					stream.close();
				}
				inputStream.close();
			}
		} catch (Exception e) {
			log.debug("Unable to check if POM already exists due to : " + e);
		}
	}
	
	
}
