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
package org.mevenide.ui.eclipse.sync.view;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PomChoiceDialog extends Dialog {
	private PomChooser pomChooser;
	
	private CheckboxTableViewer tableViewer;
	
	private File chosenPom;
	
	public PomChoiceDialog(PomChooser chooser) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		super.setBlockOnOpen(true);
		this.pomChooser = chooser;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		
		composite.setLayoutData(gridData);
		
		Table table = new Table(composite, SWT.CHECK);
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
					if ( element instanceof File ) {
						return ((File) element).getAbsolutePath();
					}
					return "unexpected child... please fill a bug report.";
				}
			}
		);
		
		tableViewer.addCheckStateListener(
			new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent event) {
					File checkedElement = (File) event.getElement();
					System.err.println(checkedElement);
					tableViewer.setCheckedElements(new Object[0]);
					tableViewer.setChecked(checkedElement, true);
					chosenPom = checkedElement;
				}
			}
		);
		
		List allPoms = pomChooser.getPoms();
		setInput(allPoms);	

		return composite;
	}
	
	private void setInput(List pomFiles) {
		tableViewer.setInput(pomFiles);
	}
	
	public File getPom() {
		return this.chosenPom;
	}
	
}
