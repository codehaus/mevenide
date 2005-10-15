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
package org.mevenide.ui.eclipse.sync.viewer;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.sync.model.source.SourceDirectory;


public class SourceDirectoryGroupLabelProvider implements ITableLabelProvider, IColorProvider {
	
	private static final int INHERIT_IDX = 2;
    private static final int SRC_TYPE_IDX = 1;
    private static final int DIRECTORY_IDX = 0;

    private final String[] sourceTypes;
	
	
	public Color getBackground(Object element) {
        return MevenideColors.WHITE;
    }
    
    public Color getForeground(Object element) {
		if ( ((SourceDirectory) element).isReadOnly() ) {
			return MevenideColors.GREY;
		}
		if ( ((SourceDirectory) element).isInPom() ) {
			return MevenideColors.GREEN;
		}
		if ( ((SourceDirectory) element).getGroup().isDuplicated(element) ) {
			return MevenideColors.ORANGE;
		}
		return MevenideColors.BLACK;
    }
    
	public SourceDirectoryGroupLabelProvider(String[] sourceTypes) {
		this.sourceTypes = sourceTypes;
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		if ( columnIndex == DIRECTORY_IDX ) {
			if ( ((SourceDirectory) element).isReadOnly() ) {
				return Mevenide.getImageDescriptor("source-directory-grayed-16.gif").createImage();
			}
			return Mevenide.getImageDescriptor("source-directory-16.gif").createImage();
		}
		if ( columnIndex == INHERIT_IDX ) {
			if ( ((SourceDirectory) element).isInherited() ) {
				if ( !((SourceDirectory) element).getGroup().isInherited() ) {
					return Mevenide.getImageDescriptor("checked-grayed-16.gif").createImage();
				}
				return Mevenide.getImageDescriptor("checked-16.gif").createImage();
			}
			
			if ( !((SourceDirectory) element).getGroup().isInherited() ) {
				return Mevenide.getImageDescriptor("unchecked-grayed-16.gif").createImage();
			}
			return Mevenide.getImageDescriptor("unchecked-16.gif").createImage();
						
			
		}
		return null;
	}
	
	public String getColumnText(Object element, int columnIndex) {
		Assert.isTrue(element instanceof SourceDirectory);
		if ( columnIndex == DIRECTORY_IDX  ) { 
			return ((SourceDirectory) element).getDisplayPath();
		}
		if ( columnIndex == SRC_TYPE_IDX ) {
			String directoryType = ((SourceDirectory) element).getDirectoryType();
			return directoryType;
		}
		return "";
	}
	
	public void addListener(ILabelProviderListener listener) {
	}
	
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	public void removeListener(ILabelProviderListener listener) {
	}
	
	public void dispose() {
	}
}