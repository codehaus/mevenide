/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
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
package org.mevenide.ui.eclipse.editors.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.properties.IPomPropertySource;
import org.mevenide.ui.eclipse.editors.properties.PomPropertySourceProvider;

/**
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class TableEntry extends PageEntry {
	private static final Log log = LogFactory.getLog(TableEntry.class);

	private static final String INHERITED_TOOLTIP =
		Mevenide.getResourceString("OverridableTextEntry.toggle.tooltip.inherited");
	private static final String OVERRIDEN_TOOLTIP =
		Mevenide.getResourceString("OverridableTextEntry.toggle.tooltip.overriden");

	private TableViewer viewer;
	private Button overrideToggle;
	private Button addButton, removeButton, upButton, downButton;
	private boolean inherited;
	private PomPropertySourceProvider propertyProvider = new PomPropertySourceProvider();
	private IPomCollectionAdaptor objectFactory;

	private class OverridableSelectionAdapter extends SelectionAdapter {
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

	public TableEntry(
		TableViewer viewer, 
		Button overrideToggle, 
		Composite parent, 
		PageWidgetFactory factory,
		PageSection section) {
			
		this.viewer = viewer;
		this.overrideToggle = overrideToggle;
		init(parent, factory, section);
	}
	
	private void init(Composite parent, PageWidgetFactory factory, final PageSection section) {
		Composite buttonContainer = factory.createComposite(parent);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		buttonContainer.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);
		
		addButton = factory.createButton(buttonContainer, "Add", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		addButton.setLayoutData(data);
		addButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					addEntry(objectFactory.addNewObject());
					setDirty(true);
					fireEntryDirtyEvent();
				}
			}
		);

		removeButton = factory.createButton(buttonContainer, "Remove", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		removeButton.setLayoutData(data);
		removeButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IStructuredSelection selected = (IStructuredSelection) viewer.getSelection();
					Object[] itemsToRemove = selected.toArray();
					viewer.remove(itemsToRemove);
					for (int i = 0; i < itemsToRemove.length; i++) {
						IPomPropertySource property = (IPomPropertySource) itemsToRemove[i];
						objectFactory.removeObject(property.getSource());
						objectFactory.removeObject(property.getSource());
					}
					setDirty(true);
					fireEntryDirtyEvent();
				}
			}
		);

		upButton = factory.createButton(buttonContainer, "Up", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		upButton.setLayoutData(data);
		upButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int index = viewer.getTable().getSelectionIndex();
					if (index > 0) {
						Object item = viewer.getElementAt(index);
						viewer.remove(item);
						viewer.insert(item,--index);
						viewer.getTable().select(index);
						objectFactory.moveObjectTo(index, ((IPomPropertySource) item).getSource());
						setDirty(true);
						fireEntryDirtyEvent();
					}
				}
			}
		);

		downButton = factory.createButton(buttonContainer, "Down", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		downButton.setLayoutData(data);
		downButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int index = viewer.getTable().getSelectionIndex();
					if (index >= 0 && index < viewer.getTable().getItemCount() - 1) {
						Object item = viewer.getElementAt(index);
						viewer.remove(item);
						viewer.insert(item, ++index);
						viewer.getTable().select(index);
						objectFactory.moveObjectTo(index, ((IPomPropertySource) item).getSource());
						setDirty(true);
						fireEntryDirtyEvent();
					}
				}
			}
		);

		viewer.addSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					section.getPage().getEditor().setPropertySourceSelection(e.getSelection());
				}
			}
		);
		
		viewer.addPostSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					if (e.getSelection().isEmpty()) {
						removeButton.setEnabled(false);
						upButton.setEnabled(false);
						downButton.setEnabled(false);
					} else {
						removeButton.setEnabled(true);
						upButton.setEnabled(true);
						downButton.setEnabled(true);
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

		removeButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
	}

	public void addOverrideAdaptor(IOverrideAdaptor adaptor) {
		if (overrideToggle != null) {
			overrideToggle.addSelectionListener(new OverridableSelectionAdapter(adaptor));
		}
	}
	
	public void addPomCollectionAdaptor(IPomCollectionAdaptor factory)
	{
		this.objectFactory = factory;
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
		if (viewer != null) {
			viewer.getTable().setEnabled(enable);
			addButton.setEnabled(enable);
			removeButton.setEnabled(enable && viewer.getTable().getItemCount() > 0);
			upButton.setEnabled(enable && viewer.getTable().getItemCount() > 0);
			downButton.setEnabled(enable && viewer.getTable().getItemCount() > 0);
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

	public void addEntries(List entries) {
		if (viewer != null) {
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
					viewer.update(event.getSource(), null);
					setDirty(true);
					fireEntryDirtyEvent();
				}
			}
		);
		viewer.add(source);
	}
	
	public void addEntries(List entries, boolean shouldDisableNotification) {
		this.disableNotification = shouldDisableNotification;
		addEntries(entries);
		this.disableNotification = false;
	}

	public void removeAll() {
		if (viewer != null) {
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
		if (viewer != null) {
			return viewer.getTable().setFocus();
		}
		return false;
	}

}
