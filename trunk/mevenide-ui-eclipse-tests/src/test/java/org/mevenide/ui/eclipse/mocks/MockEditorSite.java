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
package org.mevenide.ui.eclipse.mocks;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.mockobjects.MockObject;

/**
 * 
 * @author Jeffrey Bonevich <jeff@bonevich.com>
 * @version $Id$
 */
public class MockEditorSite 
	extends MockObject
	implements IEditorSite {

	/**
	 * @see org.eclipse.ui.IEditorSite#getActionBarContributor()
	 */
	public IEditorActionBarContributor getActionBarContributor() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IEditorSite#getActionBars()
	 */
	public IActionBars getActionBars() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPartSite#getId()
	 */
	public String getId() {
		return null;
	}
	
	 /* (non-Javadoc)
	    * @see org.eclipse.ui.IWorkbenchPartSite#getPart()
	    */
	public IWorkbenchPart getPart() {
	    return null;
    }


	/**
	 * @see org.eclipse.ui.IWorkbenchPartSite#getPluginId()
	 */
	public String getPluginId() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPartSite#getRegisteredName()
	 */
	public String getRegisteredName() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(java.lang.String, org.eclipse.jface.action.MenuManager, org.eclipse.jface.viewers.ISelectionProvider)
	 */
	public void registerContextMenu(
		String menuId,
		MenuManager menuManager,
		ISelectionProvider selectionProvider) {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPartSite#registerContextMenu(org.eclipse.jface.action.MenuManager, org.eclipse.jface.viewers.ISelectionProvider)
	 */
	public void registerContextMenu(
		MenuManager menuManager,
		ISelectionProvider selectionProvider) {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPartSite#getKeyBindingService()
	 */
	public IKeyBindingService getKeyBindingService() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchSite#getPage()
	 */
	public IWorkbenchPage getPage() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchSite#getSelectionProvider()
	 */
	public ISelectionProvider getSelectionProvider() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchSite#getShell()
	 */
	public Shell getShell() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchSite#getWorkbenchWindow()
	 */
	public IWorkbenchWindow getWorkbenchWindow() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchSite#setSelectionProvider(org.eclipse.jface.viewers.ISelectionProvider)
	 */
	public void setSelectionProvider(ISelectionProvider provider) {
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}
