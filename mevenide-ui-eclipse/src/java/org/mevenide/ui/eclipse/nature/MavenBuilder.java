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
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MavenBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = Mevenide.PLUGIN_ID + ".mavenbuilder"; //$NON-NLS-1$
    
    
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        if ( getProject() == null ) {
            return new IProject[0];
        }
        IResourceDelta d = getDelta(getProject());
        if ( d != null ) {
	        List actionDefinitions = getAllActionDefinitions();
	        ActionActivator activator = new ActionActivator(actionDefinitions);
	        d.accept(activator);
	        activateActions(activator.getDefinitions());
        }
        return new IProject[0];
    }

    private void activateActions(final List definitions) {
        for (int i = 0; i < definitions.size(); i++) {
            ActionDefinitions definition = (ActionDefinitions) definitions.get(i); 
            activateAction(definition);
        }
    }

    private void activateAction(final ActionDefinitions definition) {
        Action action = new Action() {
            public String getText() {
                String actionText = ""; //$NON-NLS-1$
                List goals = definition.getGoals();
                for (int i = 0; i < goals.size() - 1; i++) {
                    actionText += goals.get(i) + " "; //$NON-NLS-1$
                }
                return actionText + goals.get(goals.size() - 1);
            }
            public void run() {
                //TODO
                MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Faking Maven run", "Faking Maven run");
            }
        };

        final IMenuManager menu = new MenuManager("Maven"); //TODO
        menu.add(new GroupMarker("Maven")); //TODO
        menu.add(action);
        
        //retrieval of the window instance is delegated to Mevenide instance which is registered
        //as a window listener because the builder runs in a non-ui thread and thus has 
        //no way to retrieve this reference itself through the default IWorkbench instance
        WorkbenchWindow window = Mevenide.getInstance().getWorkbenchWindow(); 

        window.getMenuBarManager().add(menu);
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
	            Mevenide.getInstance().getWorkbenchWindow().getMenuBarManager().update(true);
	            menu.setVisible(true);
            }
            
        });
    }

    private List getAllActionDefinitions() {
        return null;
    }
}


