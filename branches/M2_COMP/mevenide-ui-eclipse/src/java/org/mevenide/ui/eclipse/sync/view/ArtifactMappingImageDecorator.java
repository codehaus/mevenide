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


import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNode;
import org.mevenide.ui.eclipse.sync.model.EclipseContainerContainer;

/**
 * 
 * @author <a href="rhill2@free.fr">gdodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactMappingImageDecorator extends CompositeImageDescriptor
{
    /**
     * Base image of the object
     */ 
    private Image baseImage;
    
    /**
     * Size of the base image 
     */ 
    private Point sizeOfImage;
    
    private int flags; 
    
    public ArtifactMappingImageDecorator(Image baseImage, int flags) {
        this.baseImage = baseImage;
        this.sizeOfImage = new Point(baseImage.getBounds().width, baseImage.getBounds().height);
        this.flags = flags; 
    }

    /**
     * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
     */
    protected void drawCompositeImage(int arg0, int arg1) {
        drawImage(baseImage.getImageData(), 0, 0);
		if ( (flags & IArtifactMappingNode.INHERITED) != 0 ) {
			ImageData inheritedImageData = Mevenide.getImageDescriptor("override.gif").getImageData();
		    //top left
			drawImage(inheritedImageData, 0, 0);
        }
		if ( (flags & EclipseContainerContainer.CONFLICTING) != 0 ) {
			ImageData conflictImageData = Mevenide.getImageDescriptor("conflicting.gif").getImageData(); 
			//bottom right
			drawImage(conflictImageData, sizeOfImage.x - conflictImageData.width, sizeOfImage.y - conflictImageData.height);
		}
		if ( (flags & IArtifactMappingNode.INCOMPLETE) != 0 ) {
			ImageData warnImageData = PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ISharedImages.IMG_OBJS_ERROR_TSK).getImageData().scaledTo(8, 8);
			//bottom left 
			drawImage(warnImageData, sizeOfImage.x - warnImageData.width, 0);
		}
    }
    
    
    protected Point getSize() {
        return sizeOfImage;
    }
    
    public Image getImage() {
        return createImage();
    }
}







