/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.sync.view;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.sync.model.DirectoryNode;
import org.mevenide.ui.eclipse.sync.model.EclipseProjectNode;
import org.mevenide.ui.eclipse.sync.model.ISynchronizationNode;
import org.mevenide.ui.eclipse.sync.model.MavenArtifactNode;
import org.mevenide.ui.eclipse.sync.model.MavenProjectNode;
import org.mevenide.ui.eclipse.sync.model.PropertyNode;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SynchronizationNodeLabelProvider implements ILabelProvider, IColorProvider, ILabelDecorator {
    
    public Image getImage(Object element) {
    	if ( element instanceof MavenArtifactNode ) {
    		return Mevenide.getImageDescriptor("maven_dep_tree.gif").createImage();
    	}
    	if ( element instanceof DirectoryNode ) {
    		return Mevenide.getImageDescriptor("sourcefolder_obj.gif").createImage();
    	}
    	if ( element instanceof EclipseProjectNode ) {
    		return Mevenide.getImageDescriptor("maven_project.gif").createImage();
    	}
    	if ( element instanceof MavenProjectNode ) {
    		return Mevenide.getImageDescriptor("pom_file.gif").createImage();
    	}
    	if ( element instanceof PropertyNode ) {
    		return Mevenide.getImageDescriptor("property.gif").createImage();
    	}
    	return null;
    }
    
    public String getText(Object element) {
        if ( element instanceof ISynchronizationNode ) {
        	return ((ISynchronizationNode) element).toString();
        }
        return null;
    }
    
    public void addListener(ILabelProviderListener listener) { }
    
    public void dispose() { }
    
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
    
    public void removeListener(ILabelProviderListener listener) { }
    
    public Color getBackground(Object element) {
		return MevenideColors.WHITE;
	}
    
    public Color getForeground(Object element) {
    	return MevenideColors.BLACK;	
	}
	
    public Image decorateImage(Image image, Object element) {
		return null;
	}
	
    public String decorateText(String text, Object element) {
		return null;
	}
}
