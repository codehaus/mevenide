/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.codehaus.mevenide.ui.eclipse.launch;


import java.util.HashMap;
import java.util.Map;
import org.codehaus.mevenide.ui.eclipse.IM2UIConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.console.IConsole;

/**
 * 
 */
public class M2Process implements IProcess, IProgressMonitor {
	
	/**
	 * Process attribute with process identifier - links the Ant process build
	 * logger to a process.
	 */
	public static final String ATTR_ANT_PROCESS_ID = IM2UIConstants.PLUGIN_ID + ".ATTR_M2_PROCESS_ID"; //$NON-NLS-1$
	
	private String fLabel = null;
	private ILaunch fLaunch = null;
	private Map fAttributes = null;
	private boolean fTerminated = false;
	private boolean fCancelled = false;
	private IConsole fConsole = null;
	
	public M2Process(String label, ILaunch launch, Map attributes) {
	    fLabel = label;
		fLaunch = launch;
		if (attributes == null) {
			fAttributes = new HashMap();
		} else {
			fAttributes = attributes;
		}
		
		launch.addProcess(this);
		
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getLabel()
	 */
	public String getLabel() {
		return fLabel;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getLaunch()
	 */
	public ILaunch getLaunch() {
		return fLaunch;
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getStreamsProxy()
	 */
	public IStreamsProxy getStreamsProxy() {
		return new M2StreamsProxy(this);
	}

	
	
	/**
	 * @see org.eclipse.debug.core.model.IProcess#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String key, String value) {
		fAttributes.put(key, value);
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key) {
		return (String)fAttributes.get(key);
	}

	/**
	 * @see org.eclipse.debug.core.model.IProcess#getExitValue()
	 */
	public int getExitValue() {
		return 0;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isCanceled() && !isTerminated();
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return fTerminated;
	}
	
	protected void terminated() {
		if (!fTerminated) {
			fTerminated = true;
			if (DebugPlugin.getDefault() != null) {
				DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] {new DebugEvent(this, DebugEvent.TERMINATE)});
			}
		}
	}

	/**
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() {
		setCanceled(true);
	}

	
	
	/**
	 * Sets the console associated with this process.
	 * 
	 * @param console
	 */
	public void setConsole(IConsole console) {
		fConsole = console;
	}
	
	// IProgressMontior implemented to support termination.
	
	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
	}

	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	public void done() {
	}

	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(double work) {
	}

	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		return fCancelled;
	}

	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(boolean value) {
		fCancelled = value;
	}

	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
	}

	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
	}

	/**
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(int work) {
	}
}
