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

import org.apache.maven.project.Project;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.sync.model.DependencyMappingNode;
import org.mevenide.ui.eclipse.sync.model.DependencyMappingNodeContainer;
import org.mevenide.ui.eclipse.sync.model.DirectoryMappingNode;
import org.mevenide.ui.eclipse.sync.model.DirectoryMappingNodeContainer;
import org.mevenide.ui.eclipse.sync.model.EclipseContainerContainer;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNode;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNodeContainer;
import org.mevenide.ui.eclipse.sync.model.PomContainer;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactMappingLabelProvider implements ILabelProvider, IColorProvider, ILabelDecorator {
    
    public Image decorateImage(Image image, Object element) {
        if ( !(element instanceof IArtifactMappingNode) ) {
            return image;
        }
        IArtifactMappingNode node = (IArtifactMappingNode) element;
        int flags = 0x0;
        if ( !DecoratorManager.getManager().isMappingComplete(node) ) {
            flags |= IArtifactMappingNode.INCOMPLETE;
        }
//        if ( DecoratorManager.getManager().isConflicting(node) ) {
//            flags |= ProjectContainer.CONFLICTING;
//        }
        if ( DecoratorManager.getManager().isInherited(node) ) {
            flags |= IArtifactMappingNode.INHERITED;
        }
        ArtifactMappingImageDecorator decorator = new ArtifactMappingImageDecorator(image, flags);
        return decorator.createImage(); 
    }
    
    public Image getImage(Object element) {
        Image baseImage = null;
        if ( element instanceof PomContainer ) {
            return Mevenide.getImageDescriptor("pom_file.gif").createImage();
        }
        if ( element instanceof EclipseContainerContainer ) {
            baseImage = Mevenide.getImageDescriptor("maven_project.gif").createImage();
        }
        if ( element instanceof DirectoryMappingNodeContainer ) {
            return Mevenide.getImageDescriptor("maven_source_tree.gif").createImage();
        }
		if ( element instanceof DependencyMappingNodeContainer ) {
			return Mevenide.getImageDescriptor("maven_dep_tree.gif").createImage();
		}
        if ( element instanceof DependencyMappingNode ) {
            baseImage = Mevenide.getImageDescriptor("maven_dep_sync.gif").createImage();
        }
        if ( element instanceof DirectoryMappingNode ) {
           baseImage = Mevenide.getImageDescriptor("sourcefolder_obj.gif").createImage();
        }
        //return baseImage;
        return decorateImage(baseImage, element);
    }
    
    public String decorateText(String text, Object element) {
        if ( element instanceof IArtifactMappingNode ) {
            IArtifactMappingNode node = (IArtifactMappingNode) element;
            if ( (node.getChangeDirection() & EclipseContainerContainer.INCOMING) != 0 ) {
                text = "< " + text;
            }
            if ( (node.getChangeDirection() & EclipseContainerContainer.OUTGOING) != 0 ) {
                text = "> " + text;
            }
        }
        return text;
    }
    
    public String getText(Object element) {
        if ( element instanceof PomContainer ) {
            return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(((PomContainer) element).getPomFile().getAbsolutePath())).getProjectRelativePath().removeFirstSegments(1).toOSString();
        }
        if ( element instanceof EclipseContainerContainer ) {
            return ((EclipseContainerContainer) element).getProject().getName();
        }
        if ( element instanceof DependencyMappingNodeContainer ) {
            Project pom = ((IArtifactMappingNodeContainer) element).getPrimaryPom();
//            return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pom.getFile().getAbsolutePath())).getProjectRelativePath().removeFirstSegments(1).toOSString();
            return "/Dependencies";
        }
        if ( element instanceof DirectoryMappingNodeContainer ) {   
            Project pom = ((IArtifactMappingNodeContainer) element).getPrimaryPom();
            return "/Directories";
        }
        if ( element instanceof IArtifactMappingNode ) {
            return decorateText(((IArtifactMappingNode) element).getLabel(), element);
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
//        if ( ( element instanceof PomContainer && (((PomContainer) element).getNodes() == null || ((PomContainer) element).getNodes().length == 0) )
//        		|| ( element instanceof EclipseContainerContainer && (((EclipseContainerContainer) element).getPomContainers() == null || ((EclipseContainerContainer) element).getPomContainers().length == 0) )
//        		|| ( element instanceof IArtifactMappingNodeContainer && (((IArtifactMappingNodeContainer) element).getNodes() == null || ((IArtifactMappingNodeContainer) element).getNodes().length == 0) )) {
//            return MevenideColors.GREY;
//        }
    	return MevenideColors.BLACK;	
	}
}
