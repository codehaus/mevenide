/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated by $Author$
 * @version $Id$
 */
public class SimpleDependencyWizardPage extends DependencyWizardPage {
	private Text fGroupIdText;
	private Text fTypeText;
	private Text fArtifactIdText;
	private Text fVersionText;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		GridData gd;
		composite.setLayout(layout);

      Label label = new Label(composite, SWT.NULL);
		label.setText(Mevenide.getResourceString("NewDependencyWizardPage.page.artifactid.label"));//$NON-NLS-1$
		fArtifactIdText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fArtifactIdText.setLayoutData(gd);
		fArtifactIdText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText(Mevenide.getResourceString("NewDependencyWizardPage.page.groupid.label"));//$NON-NLS-1$
		fGroupIdText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fGroupIdText.setLayoutData(gd);
		fGroupIdText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText(Mevenide.getResourceString("NewDependencyWizardPage.page.type.label"));//$NON-NLS-1$
		fTypeText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fTypeText.setLayoutData(gd);

		label = new Label(composite, SWT.NULL);
		label.setText(Mevenide.getResourceString("NewDependencyWizardPage.page.version.label"));//$NON-NLS-1$
		fVersionText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fVersionText.setLayoutData(gd);
		
		dialogChanged();
		setControl(composite);
	}

	/**
	 * Ensures that the fields are validated.
	 */
	private void dialogChanged() {
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * @return the groupid
	 */
	public String getGroupId() {
		return fGroupIdText.getText();
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return fTypeText.getText();
	}

	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return fArtifactIdText.getText();
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return fVersionText.getText();
	}
	
}