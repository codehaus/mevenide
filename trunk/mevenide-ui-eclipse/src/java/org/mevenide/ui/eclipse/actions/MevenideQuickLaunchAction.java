/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.ui.eclipse.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.mevenide.ui.eclipse.launch.LaunchHistory;
import org.mevenide.ui.eclipse.launch.LaunchedAction;

/**
 * 
 * to be dropped when support for LaunchConfiguration is ready
 * 
 * @author <a href="mailto:rhill@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MevenideQuickLaunchAction implements IWorkbenchWindowPulldownDelegate {
	private IWorkbenchWindow window;
	
	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
		LaunchedAction[] actions = LaunchHistory.getHistory().getLaunchedActions();
		if ( actions != null ){
			for (int i = 0; i < actions.length; i++) {
				ActionContributionItem item= new ActionContributionItem(actions[i]);	
				item.fill(menu, -1);
			}
		}
		
		if ( actions != null && actions.length > 0 ) {
			Separator separator = new Separator();
			separator.fill(menu, menu.getItemCount());
			
			Action manageConfigsAction = 
				new Action("Manage configurations...") {
					public void run() {
						//@todo implement me
					}
				};

			ActionContributionItem manageItem = new ActionContributionItem(manageConfigsAction);
			manageItem.fill(menu, -1);
			
			Action clearMenuAction = 
				new Action("Delete all configurations") {
					public void run() {
						LaunchHistory.getHistory().clear();
					}
				};
			
			ActionContributionItem deleteItem = new ActionContributionItem(clearMenuAction);
			deleteItem.fill(menu, -1);
		}
		return menu;
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

	/**
	 * run last launch
	 */
	public void run(IAction action) {
		LaunchedAction lastLaunched = LaunchHistory.getHistory().getLastlaunched();
		if ( lastLaunched != null ) {
			lastLaunched.run();
		}
	}
	
	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}