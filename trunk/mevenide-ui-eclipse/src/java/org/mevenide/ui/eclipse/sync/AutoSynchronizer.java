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
 */
package org.mevenide.ui.eclipse.sync;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;


/**
 * only listens to POM modification. classpath modification are tracked thanks IElementChangedListener. 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class AutoSynchronizer implements IResourceChangeListener {
	private static AutoSynchronizer listener = new AutoSynchronizer();
	private static Object lock = new Object();
	private IResourceVisitor synchronizationVisitor = new SynchronizationVisitor();
	
	private AutoSynchronizer() {
	}
	
	public void resourceChanged(IResourceChangeEvent event) {
		IResource resource = event.getResource();
		try {
			if (resource != null) {
				resource.accept(
					synchronizationVisitor,
					IResource.DEPTH_ZERO,
					false);
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static IResourceChangeListener getSynchronizer() {
		if (listener != null) {
			return listener;
		}
		else {
			synchronized (lock) {
				listener = new AutoSynchronizer();
				return listener;
			}
		}
	}
	
	
	private class SynchronizationVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			try {
				if (resource.getProject().hasNature(JavaCore.NATURE_ID)) {
					SynchronizerFactory
						.getSynchronizer(ISynchronizer.POM_TO_IDE)
						.synchronize();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
}
