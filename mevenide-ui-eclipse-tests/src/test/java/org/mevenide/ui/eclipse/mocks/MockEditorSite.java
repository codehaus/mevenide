/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
import org.eclipse.ui.IWorkbenchWindow;


/**
 * @author badlap1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MockEditorSite 
//	extends MockObject
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
