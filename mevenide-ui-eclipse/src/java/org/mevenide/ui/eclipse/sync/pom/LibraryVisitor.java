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
package org.mevenide.ui.eclipse.sync.pom;

import org.eclipse.jdt.core.IClasspathEntry;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LibraryVisitor {
	public void accept(IClasspathEntry entry) {
		
		
	}
	
	public void visit(SourceEntry entry) {
		
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		
//		String pathToAdd = pathResolver.computePathToAdd(classpathEntry, project);
//
//		ProjectWriter.getWriter().addSource(
//			pathToAdd, 
//			getPom(), 
//			pathResolver.getMavenSourceType(classpathEntry, project)
//		);
	}
	
	public void visit(Entry entry) {
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
//		ProjectWriter.getWriter().addDependency(
//				pathResolver.getAbsolutePath(classpathEntry.getPath()), 
//				getPom()
//			);
//		}
		
	}
	
}

