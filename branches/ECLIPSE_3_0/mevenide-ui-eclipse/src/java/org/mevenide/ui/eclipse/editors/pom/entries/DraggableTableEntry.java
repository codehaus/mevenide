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
					log.debug("Moved an element"); //$NON-NLS-1$
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
			log.debug("field changed to inherited = " + inherited); //$NON-NLS-1$
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
