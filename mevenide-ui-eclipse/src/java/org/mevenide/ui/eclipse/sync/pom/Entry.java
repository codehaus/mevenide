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
public class Entry {
	private IClasspathEntry classpathEntry ;
	
	public Entry(IClasspathEntry entry) {
		classpathEntry = entry;
	}

	public void accept(LibraryVisitor visitor) {
		visitor.visit(this);
	}
	
	/** 
	 * sucky but the way IClasspathEntry is designed is sucky as well.. 
	 * perhaps that will have changed till eclipse 3 ??
	 * 
	 * @param entry
	 * @return
	 */
	public static Entry getEntry(IClasspathEntry entry) {
		switch ( entry.getEntryKind() ) {
			case IClasspathEntry.CPE_SOURCE:
				return new SourceEntry(entry);
			case IClasspathEntry.CPE_LIBRARY:
				return new DependencyEntry(entry);
		}
		return null;
	}
	
	public IClasspathEntry getClasspathEntry() {
		return classpathEntry;
	}

}

class SourceEntry extends Entry {
	public SourceEntry(IClasspathEntry entry) {
		super(entry);
	}
}

class DependencyEntry extends Entry {
	public DependencyEntry(IClasspathEntry entry) {
		super(entry);
	}

}