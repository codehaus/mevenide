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

import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = Mevenide.PLUGIN_ID + ".mavenbuilder";
    
    private IProject project;
    
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        if ( getProject() == null ) {
            return new IProject[0];
        }
        IResourceDelta d = getDelta(project);
        ActionDefinitions actionDefinitions = getActionDefinitions();
        ActionActivator activator = new ActionActivator(actionDefinitions);
        d.accept(activator);
        activateActions(activator.getDefinitions());
        return null;
    }

    private void activateActions(ActionDefinitions definitions) {
        // TODO Auto-generated method stub
    }

    private ActionDefinitions getActionDefinitions() {
        return null;
    }
    
}


