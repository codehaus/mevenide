/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.editors.entries;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DraggableTableEntry extends PageEntry {
	private static final Log log = LogFactory.getLog(DraggableTableEntry.class);

	private TableViewer sourceViewer;
	private TableViewer targetViewer;
	private Button overrideToggle;
	private boolean inherited;
	
	private final class OverridableSelectionAdapter extends SelectionAdapter {
		private IOverrideAdaptor adaptor;
    	
		private OverridableSelectionAdapter(IOverrideAdaptor adaptor) {
			this.adaptor = adaptor;
		}
    	
		public void widgetSelected(SelectionEvent e) {
			toggleOverride();
			removeAll();
			if (isInherited()) {
				addEntries((List) adaptor.acceptParent());
			}
			adaptor.overrideParent(new ArrayList());
			adaptor.refreshUI();
			setDirty(true);
			fireEntryDirtyEvent();
		}
	}

	private final class TableViewerDragAdapter implements DragSourceListener {
		
		private TableViewer viewer;
		
		public TableViewerDragAdapter(TableViewer viewer) {
			this.viewer = viewer;
		}
		public void dragStart(DragSourceEvent event) {
			event.doit = ! viewer.getSelection().isEmpty();
		}
		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			event.data = selection.getFirstElement().toString();
		}
		public void dragFinished(DragSourceEvent event) {
			if (! event.doit) return;
			if (event.detail == DND.DROP_MOVE) {
				if (log.isDebugEnabled()) {
					log.debug("Moved an element");
				}
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				if (selection != null && ! selection.isEmpty()) {
					viewer.remove(selection.getFirstElement().toString());
				}
				setDirty(true);
				fireEntryChangeEvent();
			}
		}
	}
	
	public DraggableTableEntry(
		TableViewer sourceViewer, 
		TableViewer targetViewer, 
		Button overrideToggle) {
			
		this.sourceViewer = sourceViewer;
		this.targetViewer = targetViewer;
		this.overrideToggle = overrideToggle;
		init();
	}
	
	private void init() {
		// Drag-n-Drop support
		int operations = DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };
		sourceViewer.addDragSupport(
			operations,
			transfers,
			new TableViewerDragAdapter(sourceViewer)
		);
		
		transfers = new Transfer[] { TextTransfer.getInstance() };
		sourceViewer.addDropSupport(
			operations,
			transfers,
			new ViewerDropAdapter(sourceViewer) {
				public boolean performDrop(Object source) {
					String reportToAdd = (String) source;
					// no copies!
					sourceViewer.remove(reportToAdd);
					sourceViewer.add(reportToAdd);
					return true;
				}
				public boolean validateDrop(Object target, int operation, TransferData transferType) {
					return operation == DND.DROP_MOVE && TextTransfer.getInstance().isSupportedType(transferType);
				}
			}
		);
		
		transfers = new Transfer[] { TextTransfer.getInstance() };
		targetViewer.addDragSupport(
			operations,
			transfers,
			new TableViewerDragAdapter(targetViewer)
		);

		transfers = new Transfer[] { TextTransfer.getInstance() };
		targetViewer.addDropSupport(
			operations,
			transfers,
			new ViewerDropAdapter(targetViewer) {
				public boolean performDrop(Object source) {
					String reportToAdd = (String) source;
					String targetReport = (String) getCurrentTarget();
					// no copies!
					targetViewer.remove(reportToAdd);
					if (targetReport == null) {
						targetViewer.add(reportToAdd);
					} else {
						int index = 0;
						TableItem[] items = targetViewer.getTable().getItems();
						for (int i = 0; i < items.length; i++) {
							TableItem item = items[i];
							Object report = item.getData();
							if (report != null && report.equals(targetReport)) {
								index = i;
								break;
							}
						}
						targetViewer.insert(reportToAdd, index);
					}
					
					return true;
				}
				public boolean validateDrop(Object target, int operation, TransferData transferType) {
					return operation == DND.DROP_MOVE && TextTransfer.getInstance().isSupportedType(transferType);
				}
			}
		);
		
	}

	public void addOverrideAdaptor(IOverrideAdaptor adaptor) {
		if (overrideToggle != null) {
			overrideToggle.addSelectionListener(new OverridableSelectionAdapter(adaptor));
		}
	}
	
	public boolean isInherited() {
		return inherited;
	}

	public void setInherited(boolean inherited) {
		if (log.isDebugEnabled()) {
			log.debug("field changed to inherited = " + inherited);
		}
		this.inherited = inherited;
		setEnabled(!inherited);
		setFocus();
		if (overrideToggle != null) {
			overrideToggle.setToolTipText(inherited ? INHERITED_TOOLTIP : OVERRIDEN_TOOLTIP);
		}
	}

	public void setEnabled(boolean enable) {
		if (sourceViewer != null && targetViewer != null) {
			sourceViewer.getTable().setEnabled(enable);
			targetViewer.getTable().setEnabled(enable);
		}
		if (overrideToggle != null) {
			overrideToggle.setSelection(!enable);
		}
	}

	protected void toggleOverride() {
		setInherited(!inherited);
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.PageEntry#getValue()
	 */
	public Object getValue() {
		List values = new ArrayList(targetViewer.getTable().getItemCount());
		for (int i = 0; i < targetViewer.getTable().getItemCount(); i++) {
			values.add(targetViewer.getElementAt(i));
		}
		return values;
	}

	public void addEntries(List entries, boolean shouldDisableNotification) {
		this.disableNotification = shouldDisableNotification;
		addEntries(entries);
		this.disableNotification = false;
	}

	public void addEntries(List entries) {
		if (targetViewer != null) {
			Object[] entryArray = entries.toArray();
			targetViewer.add(entryArray);
			sourceViewer.remove(entryArray);
		}
	}
	
	public void removeAll() {
		if (targetViewer != null) {
			while (targetViewer.getTable().getItemCount() > 0) {
				targetViewer.remove(targetViewer.getElementAt(0));
			}
		}
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.PageEntry#getAdaptor(java.lang.Class)
	 */
	public Object getAdaptor(Class clazz) {
		if (clazz == Table.class) {
			return targetViewer.getTable();
		}
		if (clazz == TableViewer.class) {
			return targetViewer;
		}
		return null;
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.PageEntry#setFocus()
	 */
	public boolean setFocus() {
		if (targetViewer != null) {
			return targetViewer.getTable().setFocus();
		}
		return false;
	}

}
