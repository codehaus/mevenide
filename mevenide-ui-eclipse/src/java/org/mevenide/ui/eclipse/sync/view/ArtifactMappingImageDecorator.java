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
package org.mevenide.ui.eclipse.sync.view;


import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNode;
import org.mevenide.ui.eclipse.sync.model.ProjectContainer;

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
		if ( (flags & ProjectContainer.CONFLICTING) != 0 ) {
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







