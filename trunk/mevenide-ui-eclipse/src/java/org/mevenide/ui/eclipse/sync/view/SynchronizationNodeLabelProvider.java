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
import org.mevenide.project.ProjectConstants;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.sync.model.Directory;
import org.mevenide.ui.eclipse.sync.model.DirectoryNode;
import org.mevenide.ui.eclipse.sync.model.EclipseProjectNode;
import org.mevenide.ui.eclipse.sync.model.ExcludeNode;
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
    		return Mevenide.getImageDescriptor("obj16/mdep_obj.gif").createImage();
    	}
    	if ( element instanceof DirectoryNode ) {
    		Directory directory = (Directory) ((DirectoryNode) element).getData();
    		String imageName = getDirectoryImageName(directory);
    		return Mevenide.getImageDescriptor(imageName).createImage();
    	}
    	if ( element instanceof EclipseProjectNode ) {
    		return Mevenide.getImageDescriptor("obj16/mprj_obj.gif").createImage();
    	}
    	if ( element instanceof MavenProjectNode ) {
    		return Mevenide.getImageDescriptor("obj16/mfile_obj.gif").createImage();
    	}
    	if ( element instanceof PropertyNode ) {
    		return Mevenide.getImageDescriptor("obj16/mprop_attr.gif").createImage();
    	}
    	if ( element instanceof ExcludeNode ) {
    		return Mevenide.getImageDescriptor("obj16/mdir_excl_attr.gif").createImage();
    	}
    	return null;
    }
    
    private String getDirectoryImageName(Directory directory) {
		String imageName;
		if ( directory.getType() == null ) {
			imageName = "obj16/mundefined_obj.gif";
		}
		else if ( directory.getType().equals(ProjectConstants.MAVEN_SRC_DIRECTORY) ) {
			imageName = "obj16/msources_obj.gif";
		}
		else if ( directory.getType().equals(ProjectConstants.MAVEN_TEST_DIRECTORY) ) {
			imageName = "obj16/mutests_obj.gif";
		}
		else if ( directory.getType().equals(ProjectConstants.MAVEN_ASPECT_DIRECTORY) ) {
			imageName = "obj16/maspects_obj.gif";
		}
		else if ( directory.getType().equals(ProjectConstants.MAVEN_OUTPUT_DIRECTORY) ) {
			imageName = "obj16/moutput_obj.gif";
		}
		else if ( directory.getType().equals(ProjectConstants.MAVEN_RESOURCE) 
				  || directory.getType().equals(ProjectConstants.MAVEN_TEST_RESOURCE)) {
			imageName = "obj16/mresources_obj.gif";
		}
		else {
			imageName = "obj16/msources_obj.gif";
		}
		return imageName;
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
