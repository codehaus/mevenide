/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
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