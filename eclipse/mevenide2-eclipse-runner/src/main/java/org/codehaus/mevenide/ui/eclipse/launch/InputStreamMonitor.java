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
package org.codehaus.mevenide.ui.eclipse.launch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.internal.core.DebugCoreMessages;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class InputStreamMonitor implements IStreamMonitor {
    private String fContents;
    /**
	 * The stream which is being written to (connected to system in).
	 */
	private OutputStream fStream;
	/**
	 * The queue of output.
	 */
	private Vector fQueue;
	/**
	 * The thread which writes to the stream.
	 */
	private Thread fThread;
	/**
	 * A lock for ensuring that writes to the queue are contiguous
	 */
	private Object fLock;
	
	/**
	 * Creates an input stream monitor which writes
	 * to system in via the given output stream.
	 */
	public InputStreamMonitor(OutputStream stream) {
		fStream= stream;
		fQueue= new Vector();
		fLock= new Object();
	}
	
	/**
	 * Appends the given text to the stream, or
	 * queues the text to be written at a later time
	 * if the stream is blocked.
	 */
	public void write(String text) {
		synchronized(fLock) {
			fQueue.add(text);
			fLock.notifyAll();
		}
	}

	/**
	 * Starts a thread which writes the stream.
	 */
	public void startMonitoring() {
		if (fThread == null) {
			fThread= new Thread(new Runnable() {
				public void run() {
					write();
				}
			}, DebugCoreMessages.getString("InputStreamMonitor.label")); //$NON-NLS-1$
			fThread.start();
		}
	}
	
	/**
	 * Close all communications between this
	 * monitor and the underlying stream.
	 */
	public void close() {
		if (fThread != null) {
			Thread thread= fThread;
			fThread= null;
			thread.interrupt(); 
		}
	}
	
	/**
	 * Continuously writes to the stream.
	 */
	protected void write() {
		while (fThread != null) {
			writeNext();
		}
		try {
			fStream.close();
		} catch (IOException e) {
			DebugPlugin.log(e);
		}
	}
	
	/**
	 * Write the text in the queue to the stream.
	 */
	protected void writeNext() {
		while (!fQueue.isEmpty()) {
			String text = (String)fQueue.firstElement();
			fQueue.removeElementAt(0);
			try {
			    fContents = text;
				fStream.write(text.getBytes());
				fStream.flush();
			} catch (IOException e) {
				DebugPlugin.log(e);
			}
		}
		try {
			synchronized(fLock) {
				fLock.wait();
			}
		} catch (InterruptedException e) {
		}
	}
	
	
    public void addListener(IStreamListener listener) {
        // TODO Auto-generated method stub
    }
    public String getContents() {
        return fContents != null ? fContents : "HERE";
    }
    public void removeListener(IStreamListener listener) {
        // TODO Auto-generated method stub
    }
}
