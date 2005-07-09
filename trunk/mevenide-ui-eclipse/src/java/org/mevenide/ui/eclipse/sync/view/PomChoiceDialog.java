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
package org.mevenide.ui.eclipse.sync.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Project;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.util.StatusConstants;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomChoiceDialog extends TitleAreaDialog {
    private static final String POM_CHOICE_MESSAGE = Mevenide.getResourceString("PomChoiceDialog.Message"); //$NON-NLS-1$
    private static final String DIALOG_NAME = Mevenide.getResourceString("PomChoiceDialog.Name"); //$NON-NLS-1$
    private static final String POM_CHOICE_TITLE = Mevenide.getResourceString("PomChoiceDialog.Title"); //$NON-NLS-1$
    
	private CheckboxTableViewer tableViewer;
	
    private List initialContents;
	private List chosenPoms = new ArrayList();

    private Button okButton;
	
    private boolean singleSelection;
    
	public PomChoiceDialog(List initialContents, boolean singleSelection) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		super.setBlockOnOpen(true);
		this.initialContents = initialContents;
		this.singleSelection = singleSelection;
	}
	
	protected Control createContents(Composite parent) {
	    
	    Control contents = super.createContents(parent);
	    
	    getShell().setText(DIALOG_NAME);
	    setTitle(POM_CHOICE_TITLE); 
	    setTitleImage(Mevenide.getInstance().getImageRegistry().get(IImageRegistry.POM_CHOICE_WIZ));
	    setMessage(POM_CHOICE_MESSAGE); 
	    
	    okButton = getButton(IDialogConstants.OK_ID);
	    okButton.setEnabled(false);
	    
        return contents;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		
		composite.setLayoutData(gridData);
		
		createCheckboxTableViewer(composite);
		
		createSelectionButtonsArea(composite);
		
		setInput(this.initialContents);	

		return composite;
	}
	
	private void createSelectionButtonsArea(Composite parent) {
		Composite buttonsArea = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		buttonsArea.setLayout(layout);
		buttonsArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		String buttonLabel = Mevenide.getResourceString("PomChoiceDialog.SelectAll"); //$NON-NLS-1$
		Button selectAllButton = createSelectionButton(buttonsArea, buttonLabel);
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				setAllChecked(true);
			}
		});
		
		buttonLabel = Mevenide.getResourceString("PomChoiceDialog.DeselectAll"); //$NON-NLS-1$
		Button deselectAllButton = createSelectionButton(buttonsArea, buttonLabel);
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				setAllChecked(false);
			}
		});
	}
	
	private void setAllChecked(boolean state) {
		//setAllChecked(state);
	    TableItem[] children = tableViewer.getTable().getItems();
		for (int i = 0; i < children.length; i++) {
			TableItem item = children[i];
			item.setChecked(state);
			//manually propagate the event.. crap !
			itemStateChaged(new CheckStateChangedEvent(tableViewer, item.getData(), state));
		}
	}

	private Button createSelectionButton(Composite buttonsArea, String buttonLabel) {
		Button button = new Button(buttonsArea, SWT.NULL);
		button.setText(buttonLabel);
		button.setLayoutData(new GridData());
		return button;
	}

	private void createCheckboxTableViewer(Composite parent) {
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER);
		table.setLayout(new GridLayout());
		GridData orderTextGridData = new GridData(GridData.FILL_BOTH);
		orderTextGridData.grabExcessVerticalSpace = true;
		orderTextGridData.grabExcessHorizontalSpace = true;
		table.setLayoutData(orderTextGridData);
		
		tableViewer = new CheckboxTableViewer(table);

		tableViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				Assert.isTrue(inputElement instanceof List);
				return ((List) inputElement).toArray();
			}
			public void dispose() { }
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
		});

		tableViewer.setLabelProvider(
			new LabelProvider() {
				public String getText(Object element) {
                    return getProjectLabel(element);
				}
			}
		);
		
		tableViewer.addCheckStateListener(
			new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					itemStateChaged(event);
				}

			}
		);
	}

    private boolean userAlreadyNotified = false;
    private String getProjectLabel(Object element) {
        if (element != null && element instanceof Project) {
            final File file = ((Project) element).getFile();
            if (file != null) {
                return file.getAbsolutePath();
            }

            if (!userAlreadyNotified) {
                userAlreadyNotified = true;
                final String msg = "Unable to get the name of the POM.";
                IStatus status = new Status(IStatus.ERROR, Mevenide.PLUGIN_ID, StatusConstants.INTERNAL_ERROR, msg, null);
                Mevenide.displayError(msg, status);
            }

            return "UNKNOWN";
        }

        return Mevenide.getResourceString("PomChoiceDialog.UnexpectedChild"); //$NON-NLS-1$
    }

    private void itemStateChaged(CheckStateChangedEvent event) {
		// dirty trick to enable single selection since SWT.SINGLE
        // does not do what i want, nor does CheckboxTableViewer.newCheckList(SWT.SINGLE)
	    if ( singleSelection ) {
	        tableViewer.setCheckedElements(new Object[0]);
	        chosenPoms.clear();
	    }
	    Object checkedElement = event.getElement();
	    if ( event.getChecked() ) {
		    chosenPoms.add(checkedElement);
			//dirty trick bis repetita
		    if ( singleSelection ) {
		        tableViewer.setChecked(checkedElement, true);
		    }
		}
		else {
		    chosenPoms.remove(checkedElement);
		}
		okButton.setEnabled(chosenPoms.size() > 0);
	}

	private void setInput(List pomFiles) {
		tableViewer.setInput(pomFiles);
	}
	
	public List getPoms() {
		return this.chosenPoms;
	}
	
}
