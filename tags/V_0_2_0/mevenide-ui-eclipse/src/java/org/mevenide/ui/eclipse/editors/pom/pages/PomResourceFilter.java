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
package org.mevenide.ui.eclipse.editors.pom.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Show only XML files (extension == xml) in POM resource dialog.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomResourceFilter extends ViewerFilter {
	
    private static final Log log = LogFactory.getLog(PomResourceFilter.class);
	private static final String POM_EXTENSION = "xml";
	
	private IFile currentPomFile;
    
    public PomResourceFilter(IFile pomFile) {
        super();
        this.currentPomFile = pomFile;
    }

    public boolean select(Viewer viewer, Object parent, Object element) {
    	if (element instanceof IFile) {
    		IFile file = (IFile) element;
    		if (file.getFullPath().toOSString().equals(currentPomFile.getFullPath().toOSString())) {
    			return false;
    		}
    		String extension = file.getFileExtension();
    		return POM_EXTENSION.equals(extension);
    	}
    	if (element instanceof IContainer) {
    		IContainer container = (IContainer) element;
    		try {
                IResource[] resources = container.members();
                for (int i = 0; i < resources.length; i++) {
                	if (select(viewer, parent, resources[i])) {
                		return true;
                	}
                }
            }
            catch (CoreException e) {
                log.error(e);
            }
    	}
        return false;
    }

}
