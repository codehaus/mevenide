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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.plexus.util.DirectoryScanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ActionActivator implements IResourceDeltaVisitor {

    private static final Log log = LogFactory.getLog(ActionActivator.class);
    
    private List definitionCandidates;
    
    private IProject project;

    private boolean[] shouldSkipDefinitions;
    
    public ActionActivator(List actionDefinitions, IProject project) {
        this.definitionCandidates = actionDefinitions;
        this.shouldSkipDefinitions = new boolean[definitionCandidates.size()];
        this.project = project;
    }
    
    public boolean visit(IResourceDelta delta) throws CoreException {
        for (int i = 0; i < definitionCandidates.size(); i++) {
            if ( !shouldSkipDefinitions[i] ) {
                ActionDefinitions definition = (ActionDefinitions) definitionCandidates.get(i);
                if ( definition.isEnabled(project) ) {
                    shouldSkipDefinitions[i] = true;
                }
                else {
		            if ( !project.hasNature(Mevenide.NATURE_ID) ) {
		                definition.setEnabled(project, false);
		                shouldSkipDefinitions[i] = true;
		            }
		            else {
				        IPath path = delta.getFullPath();
				        List patterns = definition.getPatterns();
				        DirectoryScanner scanner;
			            String[] files = scan(patterns);
			            definition.setEnabled(project, match(path, files));
				        if ( match(path, files) ) {
				            shouldSkipDefinitions[i] = true;
				            if ( definition.isAutoBuild() ) {
				                IAction action = new PatternBasedMavenLaunchAction(project, definition);
				                action.run();
				                definition.setEnabled(project, false);
				            }
				            return false;
				        }
		            }
                }
            }
        }
        return true;
    }

    private String[] scan(List patterns) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(project.getLocation().toOSString());
        scanner.setIncludes((String[]) patterns.toArray(new String[patterns.size()]));
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    private boolean match(IPath path, String[] files) {
	    try {
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                String relativePath = path.makeRelative().removeFirstSegments(1).toOSString().replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
                if ( relativePath.equals(file.replaceAll("\\\\", "/")) ) { //$NON-NLS-1$ //$NON-NLS-2$
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            String message = "Unable to match files/path";  //$NON-NLS-1$
            log.error(message, e);
        }
        return false; 
    }

}
