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
package org.codehaus.mevenide.ui.eclipse.console;

import org.codehaus.mevenide.ui.eclipse.M2Plugin;
import org.codehaus.mevenide.ui.eclipse.launch.M2Process;
import org.codehaus.mevenide.ui.eclipse.launch.M2StreamsProxy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;


public class M2ConsoleColorProvider extends ConsoleColorProvider implements IPropertyChangeListener {

	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#getColor(java.lang.String)
	 */
	public Color getColor(String streamIdentifer) {
		if (streamIdentifer.equals(IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM)) {
			return M2Plugin.getPreferenceColor(IConsoleColorConstants.CONSOLE_WARNING_RGB);
		}
		if (streamIdentifer.equals(IDebugUIConstants.ID_STANDARD_ERROR_STREAM)) {
			return M2Plugin.getPreferenceColor(IConsoleColorConstants.CONSOLE_ERROR_RGB);
		}
		return super.getColor(streamIdentifer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#connect(org.eclipse.debug.core.model.IProcess, org.eclipse.debug.ui.console.IConsole)
	 */
	public void connect(IProcess process, IConsole console) {
		//Both remote and local Ant builds are guaranteed to have
		//an AntStreamsProxy. The remote Ant builds make use of the
		// org.eclipse.debug.core.processFactories extension point
		M2StreamsProxy proxy = (M2StreamsProxy)process.getStreamsProxy();
		if (process instanceof M2Process) {
			((M2Process)process).setConsole(console);
		}
		
		M2Plugin.getInstance().getPreferenceStore().addPropertyChangeListener(this);
		super.connect(process, console);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#isReadOnly()
	 */
	public boolean isReadOnly() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.IConsoleColorProvider#disconnect()
	 */
	public void disconnect() {
	    M2Plugin.getInstance().getPreferenceStore().removePropertyChangeListener(this);
		super.disconnect();
	}
}