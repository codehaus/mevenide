/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Mevenide @ Sourceforge.net.  All rights
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
package org.mevenide.ui.eclipse.sync.wip;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.mevenide.ui.eclipse.Mevenide;


/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactMappingLabelProvider implements ILabelProvider {
    public Image decorateImage(Image image, Object element) {
        if ( !(element instanceof IArtifactMappingNode) ) {
            return image;
        }
        IArtifactMappingNode node = (IArtifactMappingNode) element;
        int flags = 0x0;
        if ( !SyncManager.getManager().isMappingComplete(node) ) {
            flags |= IArtifactMappingNode.INCOMPLETE;
        }
        if ( SyncManager.getManager().isConflicting(node) ) {
            flags |= ProjectContainer.CONFLICTING;
        }
        if ( SyncManager.getManager().isInherited(node) ) {
            flags |= IArtifactMappingNode.INHERITED;
        }
        ArtifactMappingImageDecorator decoratedImage = new ArtifactMappingImageDecorator(image, flags);
        return decoratedImage.createImage(); 
    }
    
    public Image getImage(Object element) {
        Image baseImage = null;
        if ( element instanceof ProjectContainer ) {
            baseImage = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
        }
        if ( element instanceof IArtifactMappingNodeContainer ) {
            return Mevenide.getImageDescriptor("maven.gif").createImage();
        }
        if ( element instanceof DependencyMappingNode ) {
            baseImage = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_JAR);
        }
        if ( element instanceof DirectoryMappingNode ) {
           baseImage = PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
        }
        return decorateImage(baseImage, element);
    }
    
    public String getText(Object element) {
        if ( element instanceof ProjectContainer ) {
            return ((ProjectContainer) element).getProject().getName();
        }
        if ( element instanceof IArtifactMappingNodeContainer ) {
            return ((IArtifactMappingNodeContainer) element).getLabel();
        }
        if ( element instanceof IArtifactMappingNode ) {
            return ((IArtifactMappingNode) element).getLabel();
        }
        return null;
    }
    
    public void addListener(ILabelProviderListener listener) {
    }
    
    public void dispose() {
    }
    
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
    
    public void removeListener(ILabelProviderListener listener) {
    }
}
