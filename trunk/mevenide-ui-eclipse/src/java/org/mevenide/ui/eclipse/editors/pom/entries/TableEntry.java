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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.adapters.properties.IPomPropertySource;
import org.mevenide.ui.eclipse.adapters.properties.PomPropertySourceProvider;
import org.mevenide.ui.eclipse.adapters.properties.ResourcePatternProxy;
import org.mevenide.ui.eclipse.editors.pom.pages.PageSection;

/**
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class TableEntry extends PageEntry {
	private static final Log log = LogFactory.getLog(TableEntry.class);

	private static final String ADD_BUTTON_LABEL = Mevenide.getResourceString("TableEntry.addButton.label"); //$NON-NLS-1$
	private static final String ADD_BUTTON_TOOLTIP_KEY = "TableEntry.addButton.tooltip"; //$NON-NLS-1$
	private static final String REMOVE_BUTTON_LABEL = Mevenide.getResourceString("TableEntry.removeButton.label"); //$NON-NLS-1$
	private static final String REMOVE_BUTTON_TOOLTIP_KEY = "TableEntry.removeButton.tooltip"; //$NON-NLS-1$
	private static final String UP_BUTTON_LABEL = Mevenide.getResourceString("TableEntry.upButton.label"); //$NON-NLS-1$
	private static final String UP_BUTTON_TOOLTIP_KEY = "TableEntry.upButton.tooltip"; //$NON-NLS-1$
	private static final String DOWN_BUTTON_LABEL = Mevenide.getResourceString("TableEntry.downButton.label"); //$NON-NLS-1$
	private static final String DOWN_BUTTON_TOOLTIP_KEY = "TableEntry.downButton.tooltip"; //$NON-NLS-1$

	private TableViewer viewer;
	private Button overrideToggle;
	private Button addButton, removeButton, upButton, downButton;
	private boolean inherited;
	private PomPropertySourceProvider propertyProvider = new PomPropertySourceProvider();
	private IPomCollectionAdaptor collectionAdaptor;
	
	private Vector dependentEntries = new Vector(10); // elements: TableEntry
	private Object parentPomObject;

	private final class OverridableSelectionAdapter extends SelectionAdapter {
		private IOverrideAdaptor adaptor;
    	
		private OverridableSelectionAdapter(IOverrideAdaptor overrideAdaptor) {
			this.adaptor = overrideAdaptor;
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

	public TableEntry(
		TableViewer tableViewer, 
		Button toggle, 
		String tooltipInfo, 
		Composite parent, 
		FormToolkit factory,
		PageSection section) {
			
		this.viewer = tableViewer;
		this.overrideToggle = toggle;
		init(parent, factory, section, tooltipInfo);
		addEntries(new ArrayList());
	}
	
	private void init(Composite parent, FormToolkit factory, final PageSection section, String tooltipInfo) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		Composite buttonContainer = factory.createComposite(parent);
		buttonContainer.setLayoutData(data);
		buttonContainer.setLayout(layout);
//		buttonContainer.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		
		addButton = factory.createButton(buttonContainer, ADD_BUTTON_LABEL, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		addButton.setLayoutData(data);
		addButton.setToolTipText(Mevenide.getResourceString(ADD_BUTTON_TOOLTIP_KEY, tooltipInfo));
		addButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Object entry = collectionAdaptor.addNewObject(parentPomObject);
					if (entry != null) {
					    addEntry(entry);
					    setDirty(true);
					    fireEntryDirtyEvent();
					} 
				}
			}
		);

		removeButton = factory.createButton(buttonContainer, REMOVE_BUTTON_LABEL, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		removeButton.setLayoutData(data);
		removeButton.setToolTipText(Mevenide.getResourceString(REMOVE_BUTTON_TOOLTIP_KEY, tooltipInfo));
		removeButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IStructuredSelection selected = (IStructuredSelection) viewer.getSelection();
					
					Object[] itemsToRemove = selected.toArray();
					int index = viewer.getTable().getSelectionIndices()[0];
					viewer.remove(itemsToRemove);
					if (index < 0) {
						index = 0;
					} else if (index >= viewer.getTable().getItemCount()) {
						index = viewer.getTable().getItemCount() - 1;
					}
					for (int i = 0; i < itemsToRemove.length; i++) {
						IPomPropertySource property = (IPomPropertySource) itemsToRemove[i];
						collectionAdaptor.removeObject(property.getSource(), parentPomObject);
					}
					if ( viewer.getElementAt(index) != null ) {
						viewer.setSelection(new StructuredSelection(viewer.getElementAt(index)));
					}
					setDirty(true);
					fireEntryDirtyEvent();
				}
			}
		);

		upButton = factory.createButton(buttonContainer, UP_BUTTON_LABEL, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		upButton.setLayoutData(data);
		upButton.setToolTipText(Mevenide.getResourceString(UP_BUTTON_TOOLTIP_KEY, tooltipInfo));
		upButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int index = viewer.getTable().getSelectionIndex();
					if (index > 0) {
						Object item = viewer.getElementAt(index);
						viewer.remove(item);
						viewer.insert(item,--index);
						viewer.setSelection(new StructuredSelection(viewer.getElementAt(index)));
						collectionAdaptor.moveObjectTo(index, ((IPomPropertySource) item).getSource(), parentPomObject);
						setDirty(true);
						fireEntryDirtyEvent();
					}
				}
			}
		);

		downButton = factory.createButton(buttonContainer, DOWN_BUTTON_LABEL, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		downButton.setLayoutData(data);
		downButton.setToolTipText(Mevenide.getResourceString(DOWN_BUTTON_TOOLTIP_KEY, tooltipInfo));
		downButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int index = viewer.getTable().getSelectionIndex();
					if (index >= 0 && index < viewer.getTable().getItemCount() - 1) {
						Object item = viewer.getElementAt(index);
						viewer.remove(item);
						viewer.insert(item, ++index);
						viewer.setSelection(new StructuredSelection(viewer.getElementAt(index)));
						collectionAdaptor.moveObjectTo(index, ((IPomPropertySource) item).getSource(), parentPomObject);
						setDirty(true);
						fireEntryDirtyEvent();
					}
				}
			}
		);

		viewer.addSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					IStructuredSelection selection = (IStructuredSelection) e.getSelection();
					if (log.isDebugEnabled()) {
						log.debug("selection changed; empty = " + selection.isEmpty()); //$NON-NLS-1$
					}
					if (section.getPage().isActive()) {
						section.getPage().getPomEditor().setPropertySourceSelection(selection);
					}
					IPomPropertySource source = (IPomPropertySource) selection.getFirstElement();
					if (source != null && dependentEntries != null && !dependentEntries.isEmpty()) {
						Iterator itr = dependentEntries.iterator();
						while (itr.hasNext()) {
							TableEntry dependentEntry = (TableEntry) itr.next();
							dependentEntry.setParentPomObject(source.getSource());
						}
					}
				}
			}
		);
		
		viewer.addPostSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					if (log.isDebugEnabled()) {
						log.debug("selection updated; empty = " + e.getSelection().isEmpty()); //$NON-NLS-1$
					}
					boolean isSelected = ! e.getSelection().isEmpty();
					removeButton.setEnabled(isSelected);
					upButton.setEnabled(isSelected);
					downButton.setEnabled(isSelected);
					if (dependentEntries != null && !dependentEntries.isEmpty()) {
						Iterator itr = dependentEntries.iterator();
						while (itr.hasNext()) {
							TableEntry dependentEntry = (TableEntry) itr.next();
							dependentEntry.addButton.setEnabled(isSelected);
						}
					}
				}
			}
		);

		// change of focus, if the text box was previously changed,
		// constitutes a changed entry event
		viewer.getTable().addFocusListener(
			new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if (isDirty()) {
						fireEntryChangeEvent();
					}
				}
			}
		);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
		    public void doubleClick(DoubleClickEvent event) {
		        try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
				}
				catch ( PartInitException e ) {
					log.debug(e, e);
				}
		    } 
		});
		
		addButton.setEnabled(false);
		removeButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
	}

	void setParentPomObject(Object object) {
		this.parentPomObject = object;
		removeAll();
		addEntries(collectionAdaptor.getDependents(parentPomObject), true);
	}
	
	public Object getParentPomObject() {
		return parentPomObject;
	}
	
	public void addDependentTableEntry(TableEntry entry ) {
		this.dependentEntries.add(entry);
	}

	public void addOverrideAdaptor(IOverrideAdaptor adaptor) {
		if (overrideToggle != null) {
			overrideToggle.addSelectionListener(new OverridableSelectionAdapter(adaptor));
		}
	}
	
	public void addPomCollectionAdaptor(IPomCollectionAdaptor factory)
	{
		this.collectionAdaptor = factory;
	}

	public boolean isInherited() {
		return inherited;
	}

	public void setInherited(boolean inherits) {
		if (log.isDebugEnabled()) {
			log.debug("field changed to inherited = " + inherited); //$NON-NLS-1$
		}
		this.inherited = inherits;
		setEnabled(!inherited);
		setFocus();
		if (overrideToggle != null) {
			overrideToggle.setToolTipText(inherited ? INHERITED_TOOLTIP : OVERRIDEN_TOOLTIP);
		}
	}

	public void setEnabled(boolean enable) {
		if (viewer != null && !viewer.getTable().isDisposed()) {
			viewer.getTable().setEnabled(enable);
			addButton.setEnabled(enable);
			removeButton.setEnabled(enable && viewer.getTable().getItemCount() > 0);
			upButton.setEnabled(enable && viewer.getTable().getItemCount() > 0);
			downButton.setEnabled(enable && viewer.getTable().getItemCount() > 0);
			if (dependentEntries != null && !dependentEntries.isEmpty()) {
				Iterator itr = dependentEntries.iterator();
				while (itr.hasNext()) {
					TableEntry dependentEntry = (TableEntry) itr.next();
					dependentEntry.removeAll();
					dependentEntry.setEnabled(enable);
					dependentEntry.addButton.setEnabled(false);
				}
			}
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
		List values = new ArrayList(viewer.getTable().getItemCount());
		for (int i = 0; i < viewer.getTable().getItemCount(); i++) {
			IPomPropertySource source = (IPomPropertySource) viewer.getElementAt(i);
			values.add(source.getSource());
		}
		return values;
	}

	public void addEntries(List entries, boolean shouldDisableNotification) {
		this.disableNotification = shouldDisableNotification;
		addEntries(entries);
		this.disableNotification = false;
	}

	public void addEntries(List entries) {
		if (viewer != null && entries != null) {
			Iterator itr = entries.iterator();
			while (itr.hasNext()) {
				addEntry(itr.next());
			}
		}
	}
	
	private void addEntry(Object entry) {
		IPomPropertySource source = propertyProvider.getPomPropertySource(entry);
		source.addPropertyChangeListener(
			new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					if (log.isDebugEnabled()) {
						log.debug("property source value changed! " + event.getSource()); //$NON-NLS-1$
					}
					viewer.update(event.getSource(), null);
					setDirty(true);
					fireEntryDirtyEvent();
					if (parentPomObject != null || event.getSource() instanceof ResourcePatternProxy) {
						fireEntryChangeEvent();
					}
				}
			}
		);
		if ( !viewer.getTable().isDisposed() ) {
		    viewer.add(source);
		}
	}
	
	public void removeAll() {
		if (viewer != null && !viewer.getTable().isDisposed()) {
			while (viewer.getTable().getItemCount() > 0) {
				viewer.remove(viewer.getElementAt(0));
			}
		}
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.PageEntry#getAdaptor(java.lang.Class)
	 */
	public Object getAdaptor(Class clazz) {
		if (clazz == Table.class) {
			return viewer.getTable();
		}
		if (clazz == TableViewer.class) {
			return viewer;
		}
		return null;
	}

	/**
	 * @see org.mevenide.ui.eclipse.editors.pages.PageEntry#setFocus()
	 */
	public boolean setFocus() {
		if (viewer != null && !viewer.getTable().isDisposed()) {
			return viewer.getTable().setFocus();
		}
		return false;
	}

}
