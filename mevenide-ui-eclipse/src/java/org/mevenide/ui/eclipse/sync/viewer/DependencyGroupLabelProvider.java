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
package org.mevenide.ui.eclipse.sync.viewer;

import java.io.File;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.sync.model.dependency.DependencyGroup;
import org.mevenide.ui.eclipse.sync.model.dependency.DependencyInfo;
import org.mevenide.ui.eclipse.sync.model.dependency.DependencyWrapper;


public class DependencyGroupLabelProvider implements ITableLabelProvider, IColorProvider {
	
    private static final int DESCRIPTION_IDX = 0;
	private static final int VALUE_IDX = 1;
	private static final int INHERIT_IDX = 2;
	
	public DependencyGroupLabelProvider() {

	}
	
	public Color getBackground(Object element) {
		return MevenideColors.WHITE;
	}

	public Color getForeground(Object element) {
		if ( element instanceof DependencyWrapper 
				&& ((DependencyWrapper) element).isReadOnly() ) {
			return MevenideColors.GREY;
		}
		if ( element instanceof DependencyWrapper 
				&& ((DependencyWrapper) element).isConflictDetected() ) {
			return MevenideColors.RED;
		}
		if ( element instanceof DependencyWrapper
				&& ((DependencyWrapper) element).getDependencyGroup().isDuplicated(element) ) {
			return MevenideColors.ORANGE;
		}
		if ( element instanceof DependencyWrapper 
				&& ((DependencyWrapper) element).isInPom() ) {
			return MevenideColors.GREEN;
		}
		if ( element instanceof DependencyInfo
				&& ((DependencyInfo) element).isReadOnly() ) {
			return MevenideColors.GREY;
		}
		if ( element instanceof DependencyInfo
				&& ((DependencyInfo) element).getDependencyWrapper().isConflictDetected() ) {
			return MevenideColors.RED;
		}
		if ( element instanceof DependencyInfo
				&& ((DependencyInfo) element).getDependencyWrapper().getDependencyGroup().isDuplicated(((DependencyInfo) element).getDependencyWrapper()) ) {
			return MevenideColors.ORANGE;
		}
		if ( element instanceof DependencyInfo
				&& ((DependencyInfo) element).isInPom() ) {
			return MevenideColors.GREEN;
		}
		return MevenideColors.BLACK;
	}
	
	public void addListener(ILabelProviderListener listener) {
	
	}
	public void dispose() {
	
	}
	public Image getColumnImage(Object element, int columnIndex) {
		if ( element instanceof DependencyWrapper && columnIndex == DESCRIPTION_IDX ) {
			return Mevenide.getImageDescriptor("dependency-16.gif").createImage();
		}

		if ( columnIndex == INHERIT_IDX && element instanceof DependencyWrapper ) {
			if ( ((DependencyWrapper) element).isInherited() ) {
				if ( !((DependencyWrapper) element).getDependencyGroup().isInherited() ) {
					return Mevenide.getImageDescriptor("checked-grayed-16.gif").createImage();
				}
				else {
					return Mevenide.getImageDescriptor("checked-16.gif").createImage();
				}
			}
			if ( !((DependencyWrapper) element).getDependencyGroup().isInherited() ) {
				return Mevenide.getImageDescriptor("unchecked-grayed-16.gif").createImage();
			}
			else {
				return Mevenide.getImageDescriptor("unchecked-16.gif").createImage();
			}
		}
		return null;
	}
	
	//@refactor if/else are scary !
	public String getColumnText(Object element, int columnIndex) {
		if ( columnIndex == INHERIT_IDX ) {
			return "";
		}
		if ( columnIndex == DESCRIPTION_IDX ) {
			if ( element instanceof DependencyWrapper ) {
				
				String artifact =  new File((((DependencyWrapper) element).getDependency()).getArtifact()).getName();
			
				//CRAP -- just a workaround.. i dont know yet why it is necessary..
				if ( artifact.endsWith(".") ) {
					if ( ((DependencyWrapper) element).getDependency().getType() == null 
						|| ((DependencyWrapper) element).getDependency().getType().trim().equals("") ) {
						((DependencyWrapper) element).getDependency().setType("jar");
					}
					artifact += ((DependencyWrapper) element).getDependency().getType();
				}

				return artifact;
				//return new File((((DependencyWrapper) element).getDependency()).getJar()).getName();
			}
			if ( element instanceof DependencyGroup ) {
				return Mevenide.getResourceString("DependencyGroupLabelProvider.table.text.dependency");
			}
		}
		if ( element instanceof DependencyInfo ) {
			DependencyInfo info = (DependencyInfo) element;
			String inf = 
				info.getInfo() == null || info.getInfo().trim().equals("") ? 
					Mevenide.getResourceString("DependencyGroupLabelProvider.table.text.unresolved") : info.getInfo();
			return columnIndex == DESCRIPTION_IDX ? info.getTitle() : inf;
		}
		return "";
	}
	public boolean isLabelProperty(Object element, String property) {
		return DependencyMappingViewer.ATTRIBUTE.equals(property);
	}
	public void removeListener(ILabelProviderListener listener) {
	}
	
}