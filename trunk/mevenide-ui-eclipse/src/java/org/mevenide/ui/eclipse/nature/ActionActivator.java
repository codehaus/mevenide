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
package org.mevenide.ui.eclipse.nature;

import java.util.List;
import org.apache.plexus.util.DirectoryScanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ActionActivator implements IResourceDeltaVisitor {

    private List definitionCandidates;
    
    private String basedir;
    
    private IProject project;
    
    public ActionActivator(List actionDefinitions, IProject project) {
        this.definitionCandidates = actionDefinitions;
        this.basedir = project.getLocation().toOSString();
        this.project = project;
    }
    
    public boolean visit(IResourceDelta delta) throws CoreException {
        for (int i = 0; i < definitionCandidates.size(); i++) {
            ActionDefinitions definition = (ActionDefinitions) definitionCandidates.get(i);
            if ( !project.hasNature(Mevenide.NATURE_ID) ) {
                definition.setEnabled(false);
            }
            else {
		        IPath path = delta.getFullPath();
		        List patterns = definition.getPatterns();
		        DirectoryScanner scanner;
	            String[] files = scan(patterns);
		        definition.setEnabled(match(path, files));
            }
        }
        return true;
    }

    private String[] scan(List patterns) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(basedir);
        scanner.setIncludes((String[]) patterns.toArray(new String[patterns.size()]));
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    private boolean match(IPath path, String[] files) {
	    for (int i = 0; i < files.length; i++) {
	        String file = files[i];
            if ( files[i].equals(path.toOSString()) ) {
                return true;
            }
        }
        return false; 
    }

}
