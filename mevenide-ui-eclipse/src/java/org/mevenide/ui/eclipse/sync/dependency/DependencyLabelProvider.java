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
package org.mevenide.ui.eclipse.sync.dependency;

import org.apache.maven.project.Dependency;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.Mevenide;


public class DependencyLabelProvider implements ITableLabelProvider {
	
	public DependencyLabelProvider() {
		
	}
	public void addListener(ILabelProviderListener listener) {
	
	}
	public void dispose() {
	
	}
	public Image getColumnImage(Object element, int columnIndex) {
		if ( element instanceof Dependency && columnIndex == 0 ) {
			return Mevenide.getImageDescriptor("dependency-16.gif").createImage();
		}
		return null;
	}
	
	//@refactor if/else are scary !
	public String getColumnText(Object element, int columnIndex) {
		if ( columnIndex == 0 ) {
			if ( element instanceof Dependency ) {
				return ((Dependency) element).getArtifact();
			}
			if ( element instanceof DependencyGroup && columnIndex == 0 ) {
				return "Dependencies";
			}
		}
		if ( element instanceof DependencyContentProvider.DependencyInfo ) {
			DependencyContentProvider.DependencyInfo info = 
					(DependencyContentProvider.DependencyInfo) element;
			String inf = 
				info.getInfo() == null || info.getInfo().trim().equals("") ? 
					"<unresolved>" : info.getInfo();
			return columnIndex == 0 ? info.getTitle() : inf;
		}
		return "";
	}
	public boolean isLabelProperty(Object element, String property) {
		return "attribute".equals(property);
	}
	public void removeListener(ILabelProviderListener listener) {
	}
}