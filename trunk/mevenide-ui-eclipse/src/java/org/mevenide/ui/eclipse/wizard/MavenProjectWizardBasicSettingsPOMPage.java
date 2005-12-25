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

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * The basic wizard page for a maven project. This include currentVersion, organization name
 * inception year, package and  a short description of the project.  
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated by $Author$
 * @version $Id$
 */
public class MavenProjectWizardBasicSettingsPOMPage extends WizardPage {

	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	private final class ProjectGroup extends Observable implements ModifyListener{
		protected final Combo fCurrentVersionCombo;
		protected final Text fShortDescriptionField;
		protected final Text fInceptionYearField;
		protected final Text fPackageField;

		public ProjectGroup(Composite composite) {
			
			final int numColumns = 2;

			final Group projGroup = new Group(composite, SWT.NONE);
			projGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			projGroup.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
			projGroup.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.ProjectObjektModelGroup.title")); //$NON-NLS-1$
			projGroup.setFont(composite.getFont());
	
			// label "Short description:"
			final Label shortDescriptionLabel = new Label(projGroup, SWT.NONE);
			shortDescriptionLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.ProjectObjektModelGroup.shortdesc.label.text")); //$NON-NLS-1$
			shortDescriptionLabel.setFont(composite.getFont());
	
			// text field for a short description
			fShortDescriptionField = new Text(projGroup, SWT.BORDER);
			fShortDescriptionField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fShortDescriptionField.setFont(composite.getFont());
			
			// label "Package:"
			final Label packageLabel = new Label(projGroup, SWT.NONE);
			packageLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.ProjectObjektModelGroup.package.label.text")); //$NON-NLS-1$
			packageLabel.setFont(composite.getFont());

			// text field for pacakge
			fPackageField = new Text(projGroup, SWT.BORDER);
			fPackageField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fPackageField.setFont(composite.getFont());

			fPackageField.addModifyListener(this);			
			
			// label "Version:"
			final Label versionLabel = new Label(projGroup, SWT.NONE);
			versionLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.ProjectObjektModelGroup.version.label.text")); //$NON-NLS-1$
			versionLabel.setFont(composite.getFont());

			// combo for versions
			fCurrentVersionCombo = new Combo(projGroup, SWT.DROP_DOWN);
			fCurrentVersionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fCurrentVersionCombo.select(0);
			
			// label "Package:"
			final Label inceptionYearLabel = new Label(projGroup, SWT.NONE);
			inceptionYearLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.ProjectObjektModelGroup.year.label.text")); //$NON-NLS-1$
			inceptionYearLabel.setFont(composite.getFont());

			// text field for pacakge
			int year = Calendar.getInstance().get(Calendar.YEAR);
			fInceptionYearField = new Text(projGroup, SWT.BORDER);
			fInceptionYearField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fInceptionYearField.setFont(composite.getFont());
			fInceptionYearField.setText("" + year);
			fInceptionYearField.addModifyListener(this);			
			
			setChanged();
		}

		public void modifyText(ModifyEvent e) {
			fireEvent();
		}
	
		public String getShortDescription()
		{
			return fShortDescriptionField.getText();
		}
		
		public String getPackage()
		{
			return fPackageField.getText();
		}
		
		public String getVersion()
		{
			return fCurrentVersionCombo.getItem(fCurrentVersionCombo.getSelectionIndex());
		}
		
		public String getInceptionYear()
		{
			return fInceptionYearField.getText();
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}
		
		public void setFocus() {
			fInceptionYearField.setFocus();
		}
	}
	/**
	 * Request a organization name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	private final class OrganizationGroup extends Observable implements ModifyListener{
		protected final Text fOrganizationNameField;
		protected final Text fOrganizationURLField;
		protected final Text fOrganizationLogoField;
		
		public OrganizationGroup(Composite composite) {
			final int numColumns = 2;

			final Group pomGroup = new Group(composite, SWT.NONE);
			pomGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			pomGroup.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
			pomGroup.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.OrganizationGroup.title")); //$NON-NLS-1$
			pomGroup.setFont(composite.getFont());

			// label "Organization name:"
			final Label organizationNameLabel = new Label(pomGroup, SWT.NONE);
			organizationNameLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.OrganizationGroup.name.label.text")); //$NON-NLS-1$
			organizationNameLabel.setFont(composite.getFont());

			// text field for organization name
			fOrganizationNameField = new Text(pomGroup, SWT.BORDER);
			fOrganizationNameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fOrganizationNameField.setFont(composite.getFont());

			fOrganizationNameField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					fOrganizationNameField.setSelection(0, fOrganizationNameField.getText().length());
					fireEvent();
				}
			});
			fOrganizationNameField.addModifyListener(this);
			
			// label "Organization homepage:"
			final Label organizationURLLabel = new Label(pomGroup, SWT.NONE);
			organizationURLLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.OrganizationGroup.url.label.text")); //$NON-NLS-1$
			organizationURLLabel.setFont(composite.getFont());

			// text field for organization homepage
			fOrganizationURLField = new Text(pomGroup, SWT.BORDER);
			fOrganizationURLField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fOrganizationURLField.setFont(composite.getFont());

			fOrganizationURLField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					fireEvent();
				}
			});
			
			// label "Organization logo"
			final Label organizationLogoLabel = new Label(pomGroup, SWT.NONE);
			organizationLogoLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.OrganizationGroup.logo.label.text")); //$NON-NLS-1$
			organizationLogoLabel.setFont(composite.getFont());

			// text field for organization logo
			fOrganizationLogoField = new Text(pomGroup, SWT.BORDER);
			fOrganizationLogoField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fOrganizationLogoField.setFont(composite.getFont());

			fOrganizationLogoField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					fireEvent();
				}
			});
			
			setChanged();
		}

		public void modifyText(ModifyEvent e) {
			fireEvent();
		}
		
		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}
		/**
		 * Get the name of the organization 
		 * @return the name of the organization
		 */
		public String getOrganizationName() {
			return fOrganizationNameField.getText().trim();
		}
		/**
		 * Get the URL for the organization
		 * @return the URL to the organization homepage
		 */
		public String getOrganizationURL() {
			return fOrganizationURLField.getText().trim();
		}
		/**
		 * Get the organization logo (file location)
		 * @return the path for the organization logo
		 */
		public String getOrganizationLogo() {
			return fOrganizationLogoField.getText().trim();
		}
		/**
		 * 
		 */
		public void setFocus() {
			fOrganizationNameField.setFocus();
		}
	}

	/**
	 * Validate this page and show appropriate warnings and error
	 * NewWizardMessages.
	 */
	private final class Validator implements Observer {

		public void update(Observable o, Object arg) {

			// check wether the organization name field is empty
			if (fOrganizationGroup.getOrganizationName().length() == 0) {
				setMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.Message.enterOrganizationName")); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}
	
			// check wether the package field is valid
			if (fProjectGroup.getPackage().length() != 0) {
				IStatus val= JavaConventions.validatePackageName(fProjectGroup.getPackage());
				if (val.getSeverity() == IStatus.ERROR) {
                    setErrorMessage(MessageFormat.format(JDTWizardMessages.NewPackageWizardPage_error_InvalidPackageName, new String[] { val.getMessage() })); 
					setPageComplete(false);
					return;
				} else if (val.getSeverity() == IStatus.WARNING) {
                    setErrorMessage(MessageFormat.format(JDTWizardMessages.NewPackageWizardPage_warning_DiscouragedPackageName, new String[] { val.getMessage() }));
					setPageComplete(false);
					return;
				} else {
					setMessage(null);
				}
			}

			// check wether the inception year field is valid
			//@TODO Validate the string and test if it's an int e.g. a year and not some string like 'test'
			if (fProjectGroup.getInceptionYear().length() != 4) {
				setErrorMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.Message.enterInceptionYearError")); //$NON-NLS-1$
				setPageComplete(false);
				//fProjectGroup.setFocus();
				return;
			}

			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
			
		}
	}

	protected OrganizationGroup fOrganizationGroup;
	protected ProjectGroup fProjectGroup;
	protected Validator fValidator;

	protected static final String PAGE_NAME = Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.page.pageName"); //$NON-NLS-1$

	protected boolean fUseTemplate = false;
	
	
	/**
	 * @param fBasicSettingsPage 
	 */
	public MavenProjectWizardBasicSettingsPOMPage() {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.page.title")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPOMPage.page.description")); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// create UI elements
		fOrganizationGroup = new OrganizationGroup(composite);
		fProjectGroup = new ProjectGroup(composite);

		// initialize all elements
		fOrganizationGroup.notifyObservers();

		// create and connect validator
		fValidator = new Validator();
		fOrganizationGroup.addObserver(fValidator);
		fProjectGroup.addObserver(fValidator);

		setControl(composite);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		MavenProjectWizard wizard = (MavenProjectWizard)getWizard();

		Organization org = new Organization();
		org.setName(fOrganizationGroup.getOrganizationName());
		org.setUrl(fOrganizationGroup.getOrganizationURL());
		org.setLogo(fOrganizationGroup.getOrganizationLogo());
		
		wizard.getProjectObjectModel().setOrganization(org);
		wizard.getProjectObjectModel().setShortDescription(fProjectGroup.getShortDescription());
		wizard.getProjectObjectModel().setInceptionYear(fProjectGroup.getInceptionYear());
		
		//fBasicSettingsPage.getProjectObjectModel().setVersions();
		
		wizard.getProjectObjectModel().setPackage(fProjectGroup.getPackage());
		return super.getNextPage();
	}
	
	/**
	 * Get the name of the organization
	 * @return the name of the organization
	 */
	public String getOrganizationName() {
		return fOrganizationGroup.getOrganizationName();
	}

	protected void onEnterPage()
	{
		MavenProjectWizard wizard = (MavenProjectWizard)getWizard();
		fUseTemplate = wizard.useTemplate();
		Project fProject = wizard.getProjectObjectModel();
		
		if(fUseTemplate)
		{
			if(fProject.getDescription() != null)
				fProjectGroup.fShortDescriptionField.setText(fProject.getDescription());
			if(fProject.getPackage() != null)
				fProjectGroup.fPackageField.setText(fProject.getPackage());

			if(fProject.getVersions() != null)
			{
				List versions = fProject.getVersions();
				int size = versions.size();
				for (int i = 0; i < size; i++) {
					fProjectGroup.fCurrentVersionCombo.add(versions.get(i).toString());
				}
			}
			if(fProject.getInceptionYear() != null)
				fProjectGroup.fInceptionYearField.setText(fProject.getInceptionYear());
			if(fProject.getOrganization() != null && fProject.getOrganization().getName() != null)
				fOrganizationGroup.fOrganizationNameField.setText(fProject.getOrganization().getName());
			if(fProject.getOrganization() != null && fProject.getOrganization().getLogo() != null)
				fOrganizationGroup.fOrganizationLogoField.setText(fProject.getOrganization().getLogo());
			if(fProject.getOrganization() != null && fProject.getOrganization().getUrl() != null)
				fOrganizationGroup.fOrganizationURLField.setText(fProject.getOrganization().getUrl());
		}
	}
	
	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			fOrganizationGroup.setFocus();
	}

	/**
	 * Initialize a grid layout with the default Dialog settings.
	 */
	protected GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}
}