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
package org.mevenide.ui.eclipse.repository.view;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.mevenide.ui.eclipse.repository.model.Artifact;
import org.mevenide.ui.eclipse.repository.model.BaseRepositoryObject;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryObjectLabelProvider implements ILabelProvider {

    public void addListener(ILabelProviderListener listener) {
    }

    public Image getImage(Object element) {
        return null;
    }
    
    public String getText(Object element) {
        if ( element instanceof Artifact ) {
            Artifact artifact = (Artifact) element;
            return artifact.getName() + " v. " + artifact.getVersion();
        }
        if ( element instanceof BaseRepositoryObject ) {
            return ((BaseRepositoryObject) element).getName();
        }
        return null;
    }
    
    public void dispose() {
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }
}
