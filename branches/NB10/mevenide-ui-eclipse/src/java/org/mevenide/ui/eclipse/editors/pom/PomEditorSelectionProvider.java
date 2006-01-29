/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
package org.mevenide.ui.eclipse.editors.pom;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.part.MultiPageSelectionProvider;

/**
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomEditorSelectionProvider
	extends MultiPageSelectionProvider 
	implements ISelectionProvider {

	private Vector listeners = new Vector();
	private ISelection selection;

	public PomEditorSelectionProvider(MevenidePomEditor editor) {
		super(editor);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.addElement(listener);
	}

	public ISelection getSelection() {
		return selection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.removeElement(listener);
	}

	public void setSelection(ISelection newSelection) {
		this.selection = newSelection;
		SelectionChangedEvent event = new SelectionChangedEvent(this, selection);

		Iterator itr = ((Vector) listeners.clone()).iterator();
		while (itr.hasNext()) {
			ISelectionChangedListener listener = (ISelectionChangedListener) itr.next();
			listener.selectionChanged(event);
		}
	}

}
