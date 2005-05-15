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
package org.mevenide.ui.eclipse.adapters.properties;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class AbstractPomPropertySource 
	implements IPomPropertySource, IAdaptable, IWorkbenchAdapter {

	protected static final String EMPTY_STR = "";
	
	private Vector propertyListeners = new Vector();
	
	public AbstractPomPropertySource() {
	}

	protected String valueOrEmptyString(String value) {
		return value != null ? value : EMPTY_STR;
	}

	protected boolean isEmpty(String value) {
		return value == null || EMPTY_STR.equals(value);
	}

	public Object getAdapter(Class adapter) {
		if (IPropertySource.class.equals(adapter)) {
			return this;
		}
		if (IWorkbenchAdapter.class.equals(adapter)) {
			return this;
		}
		return null;
	}

	public Object[] getChildren(Object o) {
		return null;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public Object getParent(Object o) {
		return null;
	}
	
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		propertyListeners.add(listener);
	}
	
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		propertyListeners.remove(listener);
	}
	
	protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
		Iterator itr = propertyListeners.iterator();
		while (itr.hasNext()) {
			IPropertyChangeListener listener = (IPropertyChangeListener) itr.next();
			listener.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
		}
	}
	
}
