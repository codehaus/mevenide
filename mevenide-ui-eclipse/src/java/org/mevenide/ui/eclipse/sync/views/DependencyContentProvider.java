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
package org.mevenide.ui.eclipse.sync.views;

import java.util.List;

import org.apache.maven.project.Dependency;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mevenide.ui.eclipse.sync.dependency.DependencyGroup;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyContentProvider implements ITreeContentProvider {
	
	abstract class DependencyInfo {
		protected Dependency dependency;
		protected String title;
		protected String info;
		DependencyInfo(Dependency d) {
			this.dependency = d;
		}
		
		public Dependency getDependency() {
			return dependency;
		}
		
		public void setInfo(String string) {
			info = string;
		}
		public String getInfo() {
			return info;
		}
		public String getTitle() {
			return title;
		}

	}
	
	class GroupId extends DependencyInfo {
		public GroupId(Dependency d) {
			super(d);
			title = "groupId";
			info = d.getGroupId();
		}
	}
	
	class ArtifactId extends DependencyInfo {
		public ArtifactId(Dependency d) {
			super(d);
			title = "artifactId";
			info = d.getArtifactId();
		}
	}
	
	class Version extends DependencyInfo {
		public Version(Dependency d) {
			super(d);
			title = "version";
			info = d.getVersion();
		}		
	}
	
	class Type extends DependencyInfo {
		public Type(Dependency d) {
			super(d);
			title = "type";
			info = d.getType();
		}	
	}
	
	
	public Object[] getChildren(Object parentElement) {
		if ( parentElement instanceof DependencyGroup ) {
			DependencyGroup group = (DependencyGroup) parentElement;
		
			List list = group.getDependencies();
			if ( list != null ) {
				Object[] elements = new Object[list.size()];
				for (int i = 0; i < list.size(); i++) {
					elements[i] = list.get(i);
				}
				return elements;
			}
		}
		if ( parentElement instanceof Dependency ) {
			Dependency dep = (Dependency) parentElement;
			DependencyInfo[] children = new DependencyInfo[4];
			children[0] = new GroupId(dep);
			children[1] = new ArtifactId(dep);
			children[2] = new Version(dep);
			children[3] = new Type(dep);
			return children;
		}
		return null;
	}

	public Object getParent(Object element) {
		if ( element instanceof DependencyInfo ) {
			return ((DependencyInfo) element).getDependency();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		boolean hasChildren = element instanceof Dependency 
				|| element instanceof DependencyGroup  ;
		return hasChildren;
	}

	public Object[] getElements(Object inputElement) {
		Object[] o = getChildren(inputElement);
		return o == null ? new Object[0] : o;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
