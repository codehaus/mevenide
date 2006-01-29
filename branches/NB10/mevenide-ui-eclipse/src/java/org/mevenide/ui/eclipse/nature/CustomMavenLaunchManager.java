/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CustomMavenLaunchManager implements IWorkbenchWindowPulldownDelegate2 {

    private Menu menu;
    
    private ManageActionDefinitionsAction manageActionDefinitionsAction;
    
    private IProject lastSelectedProject;
    
    public CustomMavenLaunchManager() {
        manageActionDefinitionsAction = new ManageActionDefinitionsAction();
    }
	
    private void setMenu(Menu menu) {
		if (this.menu != null) {
			this.menu.dispose();
		}
		this.menu = menu;
	}
    
    public Menu getMenu(Menu parent) {
		setMenu(new Menu(parent));
		fillMenu(menu);
		initMenu();
		return menu;
	}
    
    public Menu getMenu(Control parent) {
		setMenu(new Menu(parent));
		fillMenu(menu);
		initMenu();
		return menu;
	}
    
    private void initMenu() {
		// Add listener to repopulate the menu each time
		// it is shown because of dynamic nature
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu m = (Menu)e.widget;
				MenuItem[] items = m.getItems();
				for (int i=0; i < items.length; i++) {
				    items[i].dispose();
				}
				fillMenu(m);
			}            
		});
	}
    
    protected void fillMenu(Menu menu) {	
		ActionDefinitionsManager actionDefinitionsManager = Mevenide.getInstance().getActionDefinitionsManager();
		List definitions = actionDefinitionsManager.getDefinitions();
		boolean definitionsAdded = false;
		if ( definitions.size() > 0 ) {
		    for (int i = 0; i < definitions.size(); i++) {
		        ActionDefinitions definition = (ActionDefinitions) definitions.get(i);
		        if ( !definition.isAutoBuild() ) {
			        PatternBasedMavenLaunchAction action = new PatternBasedMavenLaunchAction(lastSelectedProject, definition);
			        addToMenu(menu, action);
			        definitionsAdded = true;
		        }
            }
		}
		if ( definitionsAdded ) {
		    addSeparator(menu);
		}
		addToMenu(menu, manageActionDefinitionsAction);
	}
    
	protected void addSeparator(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
	}
    
    protected void addToMenu(Menu menu, IAction action) {
		action.setText(action.getText());
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(menu, -1);
	}
    
	public void run(IAction action) {
	    //do nothing
	}
	
	public void init(IWorkbenchWindow window) {
	}
	
	public void dispose() {
	    setMenu(null);
    }
    
    public void selectionChanged(IAction action, ISelection selection) {
        IProject project = null;
        if ( selection instanceof StructuredSelection) {
	        Object firstElement = ((StructuredSelection) selection).getFirstElement();
	        if ( firstElement instanceof IResource ) {
	            project = ((IResource) firstElement).getProject();
	    	}
	        if ( firstElement instanceof IJavaElement )  {
	            project = ((IJavaElement) firstElement).getJavaProject().getProject();
	        }
        }
        if ( project != null ) {
            lastSelectedProject = project;
        }
    }
}
