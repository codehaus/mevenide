/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.codehaus.mevenide.ui.eclipse.launch;


import java.io.IOException;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * 
 */
public class M2StreamsProxy implements IStreamsProxy {
	
    /**
	 * The monitor for the output stream (connected to standard out of the process)
	 */
	private OutputStreamMonitor fInputMonitor;
	/**
	 * The monitor for the error stream (connected to standard error of the process)
	 */
	private InputStreamMonitor fErrorMonitor;
	/**
	 * The monitor for the input stream (connected to standard in of the process)
	 */
	private InputStreamMonitor fOutputMonitor;
	/**
	 * Records the open/closed state of communications with
	 * the underlying streams.
	 */
	private boolean fClosed= false;
	/**
	 * Creates a <code>StreamsProxy</code> on the streams
	 * of the given system process.
	 */
	public M2StreamsProxy(M2Process process) {
		fInputMonitor= new OutputStreamMonitor(System.in);
		fOutputMonitor= new InputStreamMonitor(System.out);
		fErrorMonitor= new InputStreamMonitor(System.err);
		fErrorMonitor.startMonitoring();
		fInputMonitor.startMonitoring();
	}

	/**
	 * Causes the proxy to close all
	 * communications between it and the
	 * underlying streams after all remaining data
	 * in the streams is read.
	 */
	public void close() {
		if (!fClosed) {
			fClosed= true;
		//	fOutputMonitor.close();
		//	fErrorMonitor.close();
			fInputMonitor.close();
		}
	}

	/**
	 * Causes the proxy to close all
	 * communications between it and the
	 * underlying streams immediately.
	 * Data remaining in the streams is lost.
	 */	
	public void kill() {
		fClosed= true;
	//	fOutputMonitor.cl();
	//	fErrorMonitor.close();
		fInputMonitor.close();
	}

	/**
	 * @see IStreamsProxy#getErrorStreamMonitor()
	 */
	public IStreamMonitor getErrorStreamMonitor() {
		return fErrorMonitor;
	}

	/**
	 * @see IStreamsProxy#getOutputStreamMonitor()
	 */
	public IStreamMonitor getOutputStreamMonitor() {
		return fOutputMonitor;
	}

	/**
	 * @see IStreamsProxy#write(String)
	 */
	public void write(String input) throws IOException {
	}

}
