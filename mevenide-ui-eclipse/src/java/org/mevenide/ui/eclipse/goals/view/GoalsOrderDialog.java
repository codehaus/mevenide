/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.mevenide.ui.eclipse.goals.view;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * derived work from org.eclipse.ant.ui.internal.launchConfiguration.TargetOrderDialog   
 * 
 * @author IBM Corporation and others
 * @version $Id: GoalsOrderDialog.java,v 1.1 14 sept. 2003 Exp gdodinet 
 *
 */
public class GoalsOrderDialog extends Dialog implements ISelectionChangedListener {
	private static final String BUTTON_MOVE_DOWN = "GoalsOrderDialog.MoveDown"; //$NON-NLS-1$
    private static final String BUTTON_MOVE_UP = "GoalsOrderDialog.MoveUp"; //$NON-NLS-1$
    private static final String DIALOG_TITLE = "GoalsOrderDialog.title"; //$NON-NLS-1$
	
    private static Log log = LogFactory.getLog(GoalsOrderDialog.class);
	
	
	private Button fUp;
	private Button fDown;
	private TableViewer fViewer;
	private GoalsOrderContentProvider fContentProvider;
	private Object[] fTargets;

	/**
	 * Constructs the dialog.
	 * 
	 * @param parentShell
	 */
	public GoalsOrderDialog(Shell parentShell, Object[] targets) {
		super(parentShell);
		fTargets = targets;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Mevenide.getResourceString(DIALOG_TITLE));
		setShellStyle(SWT.RESIZE | SWT.APPLICATION_MODAL);
		//setMessage("Choose POM template from the list below.");
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setFont(parent.getFont());
		GridLayout layout = new GridLayout(2, false);
		comp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		
		createTargetList(comp);
		
		createButtons(comp);
		
		updateButtons();
		
		return comp;
	}

	/**
	 * Create button area & buttons
	 * 
	 * @param comp
	 */
	private void createButtons(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.verticalAlignment = GridData.BEGINNING;
		comp.setLayout(layout);
		comp.setLayoutData(gd);
		
		fUp = new Button(comp, SWT.PUSH);
		fUp.setFont(parent.getFont());
		fUp.setText(Mevenide.getResourceString(BUTTON_MOVE_UP));
		setButtonLayoutData(fUp);
		fUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleUpPressed();
			}
		});
		
		fDown = new Button(comp, SWT.PUSH);
		fDown.setFont(parent.getFont());
		fDown.setText(Mevenide.getResourceString(BUTTON_MOVE_DOWN));
		setButtonLayoutData(fDown);
		fDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleDownPressed();
			}
		});
		
	}

	/**
	 * Down
	 */
	protected void handleDownPressed() {
		int[] selections = fViewer.getTable().getSelectionIndices();
		for (int i = 0; i < selections.length; i++) {
			fContentProvider.moveDownTarget(selections[i]);
		}
		fTargets = fContentProvider.getElements(null);
		fViewer.refresh();
		updateButtons();
	}

	/**
	 * Up
	 */
	protected void handleUpPressed() {
		int[] selections = fViewer.getTable().getSelectionIndices();
		for (int i = 0; i < selections.length; i++) {
			fContentProvider.moveUpTarget(selections[i]);
		}		
		fTargets = fContentProvider.getElements(null);
		fViewer.refresh();
		updateButtons();
	}

	/**
	 * Creates a list viewer for the targets
	 * 
	 * @param comp
	 */
	private void createTargetList(Composite comp) {
		fViewer = new TableViewer(comp, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		fViewer.setLabelProvider(new LabelProvider());
		
		fContentProvider = new GoalsOrderContentProvider();
		fContentProvider.setTargets(fTargets);
		fViewer.setContentProvider(fContentProvider);
		
		fViewer.setInput(fTargets);
		
		fViewer.addSelectionChangedListener(this);
		
		Table table = fViewer.getTable();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 250;		
		table.setLayoutData(gd);
		
	}
	
	/**
	 * Returns the ordered targets
	 */
	public Object[] getTargets() {
		List sortedTargets = fContentProvider.getTargets();
		String[] targets =  new String[sortedTargets.size()];
		for (int i = 0; i < targets.length; i++) {
            targets[i] = (String) sortedTargets.get(i);
        }
        return targets;
	}
	
	/**
	 * Update button enablement
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		updateButtons();
	}
	
	private void updateButtons() {
		int[] selections = fViewer.getTable().getSelectionIndices();
		int last = fTargets.length - 1;
		boolean up = true && selections.length > 0;
		boolean down = true && selections.length > 0;
		for (int i = 0; i < selections.length; i++) {
			if (selections[i] == 0) {
				up = false;
			}
			if (selections[i] == last) {
				down = false;
			}
		}
		fUp.setEnabled(up);
		fDown.setEnabled(down);		
	}
	
}
