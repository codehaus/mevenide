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
package org.mevenide.ui.eclipse.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.util.StringInputStream;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.mevenide.project.io.PomSkeletonBuilder;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.MevenideUtils;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class FileUtils {
	
	private static Log log = LogFactory.getLog(FileUtils.class);
	
	private FileUtils() {
	}
	
	

	public static boolean inLocalRepository(String entryPath) {
		File localRepo = new File(Mevenide.getPlugin().getMavenRepository());
		return MevenideUtils.findFile(localRepo, entryPath);
	}

	public static boolean isClassFolder(String entryPath, IProject project) {
		return new File(project.getLocation().append(new Path(entryPath).removeFirstSegments(1)).toOSString()).isDirectory();
	}

	public static void createPom(IProject project) throws Exception, CoreException {
		 log.debug("Creating pom skeleton using template : " + Mevenide.getPlugin().getPomTemplate());
		 PomSkeletonBuilder pomSkeletonBuilder = PomSkeletonBuilder.getSkeletonBuilder( Mevenide.getPlugin().getPomTemplate() ); 
		 String referencedPomSkeleton = pomSkeletonBuilder.getPomSkeleton(project.getName());
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
					InputStream stream = FileUtils.class.getResourceAsStream("/templates/standard/project.xml"); 
					pom.setContents(stream, true, true, null);
					stream.close();
				}
				inputStream.close();
			}
		} catch (Exception e) {
			log.error("Unable to check if POM already exists due to : " + e);
		}
	}
	
	public static IFile assertIgnoreFileExists(IContainer container)  throws Exception {		
		IFile file = container.getFile(new Path(".mvnignore"));
		if ( !file.exists() ) {
			InputStream is = new StringInputStream("");
			file.create(is,true, null);
		}
		return file;	
	}
	
	public static IFile assertIgnoreFileExists(Project project)  throws Exception {		
		File systemFile = new File(project.getFile().getParent(), ".mvnignore");
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		IFile file = workspaceRoot.getFileForLocation(new Path(systemFile.getAbsolutePath()).makeAbsolute());
		if ( !file.exists() ) {
			InputStream is = new StringInputStream("");
			file.create(is,true, null);
		}
		return file;	
	}
	
	
	public static List getIgnoredResources(Project project) {
		try {
			IFile file = assertIgnoreFileExists(project);
			return getIgnoredResources(file);
		} 
		catch (Exception e) {
			log.error("Cannot read ignored resources", e);
			return new ArrayList();
		}
	}
	
	public static List getIgnoredResources(IProject project) {
		try {
			IFile file = assertIgnoreFileExists(project);
			return getIgnoredResources(file);
		} 
		catch (Exception e) {
			log.error("Cannot read ignored resources", e);
			return new ArrayList();
		} 
	}
	
	private static List getIgnoredResources(IFile ignoredResourceFile) throws Exception {
		List ignoredLines = new ArrayList();
	
		RandomAccessFile raf = new RandomAccessFile(ignoredResourceFile.getLocation().toOSString(), "r");
		String line = null;
		while ( (line = raf.readLine()) != null && !line.trim().equals("")) {
			ignoredLines.add(line);
		}
		
		return ignoredLines;
	}
	
	public static  boolean isArtifactIgnored(String ignoreLine, IProject project) {
		List ignoredResources = getIgnoredResources(project);
		for ( int u = 0; u < ignoredResources.size(); u++ ) {
			if ( ((String) ignoredResources.get(u)).equals(ignoreLine) ) {
				return true;
			}
		}
		return false;
	}
	
	
	public static List getPoms(IProject project) throws Exception {

		Project pom = ProjectReader.getReader().read(FileUtils.getPom(project));
		List visitedPoms = new ArrayList();		

		if ( pom != null ) {
			//dirty trick to avoid infinite loops if user has introduced one by mistake
			visitedPoms.add(pom.getFile());
	
			String extend = pom.getExtend();
			
			//recurse poms
			while ( extend != null && !extend.trim().equals("") ) {
				
				//resolve extend
				extend = extend.replaceAll("\\$\\{basedir\\}", pom.getFile().getParent().replaceAll("\\\\", "/"));
				File extendFile = new File(extend);
				if ( !extendFile.exists() ) {
					
					extendFile = new File(pom.getFile().getParent(), extend);
					if ( !extendFile.exists() ) {
						log.debug(extendFile.getAbsolutePath() + " doesnot exist. break.");
						//@ TODO throw new ExtendDoesnotExistException(..)
						break;
					}
				}
				
				//assert pom has not been visited yet
				if ( visitedPoms.contains(extendFile.getAbsolutePath()) ) {
					//@TODO throw new InfinitePomRecursionException(..)
					break;
				}
				visitedPoms.add(new File(extendFile.getAbsolutePath()));
				pom = ProjectReader.getReader().read(extendFile);
			
				extend = pom.getExtend();
			}
		}
		
		return visitedPoms;
	}
	
}
