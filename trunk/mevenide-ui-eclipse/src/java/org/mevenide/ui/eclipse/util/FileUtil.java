/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
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
		
		 String referencedPomSkeleton = ProjectSkeleton.getSkeleton( project.getName());
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
