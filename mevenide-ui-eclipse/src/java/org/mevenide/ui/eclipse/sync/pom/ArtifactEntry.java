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
public abstract class ArtifactEntry {
	private IClasspathEntry classpathEntry ;
	
	public ArtifactEntry(IClasspathEntry entry) {
		classpathEntry = entry;
	}

	public abstract void accept(ArtifactVisitor visitor) throws Exception;
	
	/** 
	 * sucky but the way IClasspathEntry is designed is sucky as well.. 
	 * perhaps that will have changed in eclipse 3 ??
	 * 
	 * @param entry
	 * @return
	 */
	public static ArtifactEntry getEntry(IClasspathEntry entry) {
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

class SourceEntry extends ArtifactEntry {
	public SourceEntry(IClasspathEntry entry) {
		super(entry);
	}
	public void accept(ArtifactVisitor visitor) throws Exception {
		visitor.add(this);
	}
}

class DependencyEntry extends ArtifactEntry {
	public DependencyEntry(IClasspathEntry entry) {
		super(entry);
	}
	public void accept(ArtifactVisitor visitor) throws Exception {
		visitor.add(this);
	}
}