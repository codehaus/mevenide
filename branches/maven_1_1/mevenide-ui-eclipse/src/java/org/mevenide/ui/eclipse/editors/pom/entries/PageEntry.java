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
package org.mevenide.ui.eclipse.editors.pom.entries;

import java.util.Iterator;
import java.util.Vector;

import org.mevenide.ui.eclipse.Mevenide;

/**
 * Abstract base class for SWT widget wrappers.  Sets up change event notification
 * and tracking dirty state.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public abstract class PageEntry {

	protected static final String INHERITED_TOOLTIP =
		Mevenide.getResourceString("OverridablePageEntry.toggle.tooltip.inherited"); //$NON-NLS-1$
	protected static final String OVERRIDEN_TOOLTIP =
		Mevenide.getResourceString("OverridablePageEntry.toggle.tooltip.overriden"); //$NON-NLS-1$

	protected boolean dirty = false;
	protected boolean disableNotification = false;
	protected Vector listeners = new Vector();

    public PageEntry() {
    }

    public boolean isDirty() {
    	return dirty;
    }

    public void setDirty(boolean dirty) {
    	this.dirty = dirty;
    }
    
    public abstract Object getValue();
    
    public abstract Object getAdaptor(Class clazz);
    
    public abstract boolean setFocus();
    
    public abstract void setEnabled(boolean enable);

    public void addEntryChangeListener(IEntryChangeListener listener) {
    	listeners.add(listener);
    }

    public void removeEntryChangeListener(IEntryChangeListener listener) {
    	listeners.remove(listener);
    }

	/**
	 * Fired when the entry field is altered (keystrokes, selections, etc.)
	 */
	protected void fireEntryDirtyEvent() {
		if (!disableNotification) {
			Iterator itr = listeners.iterator();
			while (itr.hasNext())
			{
				IEntryChangeListener listener = (IEntryChangeListener) itr.next();
				listener.entryDirty(this);
			}
		}
	}
	
	/**
	 * Fired when the change to the entry field is committed (change focus,
	 * switch page, etc.).
	 */
    protected void fireEntryChangeEvent() {
		if (!disableNotification) {
	    	Iterator itr = listeners.iterator();
	    	while (itr.hasNext())
	    	{
	    		IEntryChangeListener listener = (IEntryChangeListener) itr.next();
	    		listener.entryChanged(this);
	    	}
	    	setDirty(false);
		}
    }

}
