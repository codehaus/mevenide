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
 */
package org.mevenide.ui.eclipse.sync.views;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.sync.source.*;


public class SourceDirectoryLabelProvider implements ITableLabelProvider {
	
	private final String[] sourceTypes;
	
	public SourceDirectoryLabelProvider(String[] sourceTypes) {
		this.sourceTypes = sourceTypes;
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}
	
	public String getColumnText(Object element, int columnIndex) {
		Assert.isTrue(element instanceof SourceDirectory);
		if ( columnIndex == 0  ) { 
			return ((SourceDirectory) element).getDisplayPath();
		}
		else {
			String directoryType = ((SourceDirectory) element).getDirectoryType();
			//Assert.isTrue(Arrays.asList(sourceTypes).contains(directoryType));
			return directoryType;
		} 
	}
	
	public void addListener(ILabelProviderListener listener) {
	}
	
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}
	
	public void removeListener(ILabelProviderListener listener) {
	}
	
	public void dispose() {
	}
}