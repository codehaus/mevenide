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
package org.mevenide.ui.eclipse.wizard;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.Template;
import org.mevenide.ui.eclipse.template.model.Templates;

/**
 * The basic wizard page for a maven project. This include naming the project, selecting
 * project location and/or choosing a template to base the project creation on.  
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated by $Author$
 * @version $Id$
 */
public class MavenProjectWizardBasicSettingsPage extends WizardPage {
	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	private final class NameGroup extends Observable {

		protected final Text fNameField;

		public NameGroup(Composite composite) {
			final Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));
			nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			// label "Project name:"
			final Label nameLabel = new Label(nameComposite, SWT.NONE);
			nameLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.NameGroup.label.text")); //$NON-NLS-1$
			nameLabel.setFont(composite.getFont());

			// text field for project name
			fNameField = new Text(nameComposite, SWT.BORDER);
			fNameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fNameField.setFont(composite.getFont());

			fNameField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					fNameField.setSelection(0, fNameField.getText().length());
				}
			});

			fNameField.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					fireEvent();
				}
			});
			setChanged();
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void setFocus() {
			fNameField.setFocus();
		}
	}

	/**
	 * Request a location. Fires an event whenever the checkbox or the location
	 * field is changed, regardless of whether the change originates from the
	 * user or has been invoked programmatically.
	 */
	private final class LocationGroup extends Observable implements Observer {

		protected final Button fWorkspaceRadio;
		protected final Button fExternalRadio;
		protected final Label fLocationLabel;
		protected final Text fLocationField;
		protected final Button fLocationButton;
		protected String fExternalLocation;

		public LocationGroup(Composite composite) {

			final int numColumns = 3;

			fExternalLocation = ""; //$NON-NLS-1$

			final Group group = new Group(composite, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
			group.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.LocationGroup.title")); //$NON-NLS-1$

			fWorkspaceRadio = new Button(group, SWT.RADIO | SWT.RIGHT);
			fWorkspaceRadio.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.LocationGroup.workspace.desc")); //$NON-NLS-1$
			fWorkspaceRadio.setSelection(true);

			final GridData gd = new GridData();
			gd.horizontalSpan = numColumns;
			fWorkspaceRadio.setLayoutData(gd);

			fExternalRadio = new Button(group, SWT.RADIO | SWT.RIGHT);
			fExternalRadio.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.LocationGroup.external.desc")); //$NON-NLS-1$
			fExternalRadio.setSelection(false);

			final GridData gd2 = new GridData();
			gd2.horizontalSpan = numColumns;
			fExternalRadio.setLayoutData(gd2);

			fLocationLabel = new Label(group, SWT.NONE);
			fLocationLabel.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.LocationGroup.locationLabel.desc")); //$NON-NLS-1$
			fLocationLabel.setEnabled(false);
			fLocationLabel.setLayoutData(new GridData());

			fLocationField = new Text(group, SWT.BORDER);
			fLocationField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fLocationField.setEnabled(false);
			fLocationField.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (fLocationField.getEnabled()) {
						fExternalLocation = fLocationField.getText();
						fireEvent();
					}
				}
			});
			fLocationField.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					fLocationField.setSelection(0, fLocationField.getText().length());
				}
			});

			fLocationButton = new Button(group, SWT.PUSH);
			setButtonLayoutData(fLocationButton);
			fLocationButton.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.LocationGroup.browseButton.desc")); //$NON-NLS-1$
			fLocationButton.setEnabled(false);
			fLocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					final DirectoryDialog dialog = new DirectoryDialog(
							fLocationField.getShell());
					dialog.setMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.directory.message")); //$NON-NLS-1$
					final String directoryName = fLocationField.getText().trim();
					if (directoryName.length() > 0) {
						final File path = new File(directoryName);
						if (path.exists())
							dialog.setFilterPath(new Path(directoryName).toOSString());
					}
					final String selectedDirectory = dialog.open();
					if (selectedDirectory != null) {
						fExternalLocation = selectedDirectory;
						fLocationField.setText(fExternalLocation);
						fireEvent();
					}
				}
			});
			fWorkspaceRadio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					final boolean checked = fWorkspaceRadio.getSelection();
					fLocationLabel.setEnabled(!checked);
					fLocationField.setEnabled(!checked);
					fLocationButton.setEnabled(!checked);
					if (checked) {
						fLocationField.setText(getDefaultPath(fNameGroup.getName()));
					} else {
						fLocationField.setText(fExternalLocation);
					}
					fireEvent();
				}
			});
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		protected String getDefaultPath(String name) {
			final IPath path = Platform.getLocation().append(name);
			return path.toOSString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			if (fWorkspaceRadio.getSelection()) {
				fLocationField.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation() {
			if (isInWorkspace()) {
				return Platform.getLocation();
			}
			return new Path(fLocationField.getText().trim());
		}

		public boolean isInWorkspace() {
			return fWorkspaceRadio.getSelection();
		}
	}

	/**
	 * Request a project template. Fires an event whenever the checkbox or the location
	 * field is changed, regardless of whether the change originates from the
	 * user or has been invoked programmatically.
	 */
	private final class TemplateGroup extends Observable implements SelectionListener {

		protected final Button fStdTemplates;
		protected final Combo fTComboTemplates;
		protected final Group fGroup;
		protected final Templates fTemplates;

		public TemplateGroup(Composite composite) {
			fTemplates = Templates.newTemplates();
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fGroup.setLayout(initGridLayout(new GridLayout(), true));
			fGroup.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.TemplateGroup.title")); //$NON-NLS-1$

			fStdTemplates = new Button(fGroup, SWT.CHECK);
			fStdTemplates.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fStdTemplates.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.TemplateGroup.option.useTemplate")); //$NON-NLS-1$
			fStdTemplates.setSelection(false);
			fStdTemplates.addSelectionListener(this);

			fTComboTemplates = new Combo(fGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			fTComboTemplates.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			for (int i = 0; i < fTemplates.getTemplates().length; i++) {
				fTComboTemplates.add(((Template) fTemplates.getTemplates()[i]).getTemplateName());
			}
			fTComboTemplates.select(0);
			fTComboTemplates.setEnabled(false);
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}
		
		public boolean useTemplates() {
			return fStdTemplates.getSelection();
		}

		public Template getSelectedTemplate(){
			if(fTemplates.getTemplates().length > 0)
				return (Template)fTemplates.getTemplates()[fTComboTemplates.getSelectionIndex()];
			return null;
		}
		
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == fStdTemplates)
			{
				fTComboTemplates.setEnabled(fStdTemplates.getSelection());
				fireEvent();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	/**
	 * Show a warning when the project location contains files.
	 */
	private final class DetectGroup extends Observable implements Observer {

		private final Text fText;
		private boolean fDetect;

		public DetectGroup(Composite composite) {
			fText = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
			final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
			gd.widthHint = 0;
			gd.heightHint = convertHeightInCharsToPixels(6);
			fText.setLayoutData(gd);
			fText.setFont(composite.getFont());
			fText.setText(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.DetectGroup.message")); //$NON-NLS-1$
			fText.setVisible(false);
		}

		public void update(Observable o, Object arg) {
			if (fLocationGroup.isInWorkspace()) {
				String name = getProjectName();
				if (name.length() == 0 || JavaPlugin.getWorkspace().getRoot().findMember(name) != null) {
					fDetect = false;
				} else {
					final File directory = fLocationGroup.getLocation().append(getProjectName()).toFile();
					fDetect = directory.isDirectory();
				}
			} else {
				final File directory = fLocationGroup.getLocation().toFile();
				fDetect = directory.isDirectory();
			}
			fText.setVisible(fDetect);
			setChanged();
			notifyObservers();
		}

		public boolean mustDetect() {
			return fDetect;
		}
	}

	/**
	 * Validate this page and show appropriate warnings and error
	 * NewWizardMessages.
	 */
	private final class Validator implements Observer {

		public void update(Observable o, Object arg) {

			final IWorkspace workspace = JavaPlugin.getWorkspace();

			final String name = fNameGroup.getName();

			// check wether the project name field is empty
			if (name.length() == 0) {
				setErrorMessage(null);
				setMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.Message.enterProjectName")); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}

			// check whether the project name is valid
			final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				setPageComplete(false);
				return;
			}

			// check whether project already exists
			final IProject handle = getProjectHandle();
			if (handle.exists()) {
				setErrorMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.Message.projectAlreadyExists")); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}

			final String location = fLocationGroup.getLocation().toOSString();

			// check whether location is empty
			if (location.length() == 0) {
				setErrorMessage(null);
				setMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.Message.enterLocation")); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}

			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) { //$NON-NLS-1$
				setErrorMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.Message.invalidDirectory")); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}

			// check whether the location has the workspace as prefix
			IPath projectPath = new Path(location);
			if (!fLocationGroup.isInWorkspace() && Platform.getLocation().isPrefixOf(projectPath)) {
				setErrorMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.Message.cannotCreateInWorkspace")); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}

			// If we do not place the contents in the workspace validate the
			// location.
			if (!fLocationGroup.isInWorkspace()) {
				final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
				if (!locationStatus.isOK()) {
					setErrorMessage(locationStatus.getMessage());
					setPageComplete(false);
					return;
				}
			}

			// check whether the usage of templates is enabled and
			// that the selected template is not null
			if (fTemplateGroup.useTemplates() && fTemplateGroup.getSelectedTemplate() == null) {
				setErrorMessage(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.Message.noSelectableTemplates")); //$NON-NLS-1$
				setPageComplete(false); 
				return;
			}

			
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
		}
	}

	protected NameGroup fNameGroup;
	protected LocationGroup fLocationGroup;
	protected TemplateGroup fTemplateGroup;
	protected DetectGroup fDetectGroup;
	protected Validator fValidator;

	private static final String PAGE_NAME = Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.page.pageName"); //$NON-NLS-1$

	/**
	 *  
	 */
	public MavenProjectWizardBasicSettingsPage() {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.page.title")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("MavenProjectWizardBasicSettingsPage.page.description")); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// create UI elements
		fNameGroup = new NameGroup(composite);
		fLocationGroup = new LocationGroup(composite);
		fTemplateGroup = new TemplateGroup(composite);
		fDetectGroup = new DetectGroup(composite);

		// establish connections
		fNameGroup.addObserver(fLocationGroup);
		fLocationGroup.addObserver(fDetectGroup);

		// initialize all elements
		fNameGroup.notifyObservers();

		// create and connect validator
		fValidator = new Validator();
		fNameGroup.addObserver(fValidator);
		fLocationGroup.addObserver(fValidator);
		fTemplateGroup.addObserver(fValidator);
		
		setControl(composite);
	}

	/**
	 * Returns the current project location path as entered by the user, or its
	 * anticipated initial value. Note that if the default has been returned the
	 * path in a project description used to create a project should not be set.
	 * 
	 * @return the project location path or its anticipated initial value.
	 */
	public IPath getLocationPath() {
		return fLocationGroup.getLocation();
	}

	/**
	 * Creates a project resource handle for the current project name field
	 * value.
	 * <p>
	 * This method does not create the project resource; this is the
	 * responsibility of <code>IProject::create</code> invoked by the new
	 * project resource wizard.
	 * </p>
	 * 
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(fNameGroup.getName());
	}
	/**
	 * Return <code>true</code> if the project is created within the workspace else <code>false</code>
	 * @return true if the project is created within the current workspace
	 */
	public boolean isInWorkspace() {
		return fLocationGroup.isInWorkspace();
	}
	/**
	 * Get the name of the project to create
	 * @return
	 */
	public String getProjectName() {
		return fNameGroup.getName();
	}
	/**
	 * Get information about the templates selection
	 * @return true if the user have selected a template
	 */
	public boolean useTemplates() {
		return fTemplateGroup.useTemplates();
	}
	/**
	 * Get the selected template and <code>null</code> if the
	 * user haven't selected any template
	 * @return the selected template 
	 */
	public Template getSelectedTemplate(){
		return fTemplateGroup.getSelectedTemplate();
	}
	
	public boolean getDetect() {
		return fDetectGroup.mustDetect();
	}

	/*
	 * public boolean isSrcBin() { return fLayoutGroup.isSrcBin(); }
	 */

	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			fNameGroup.setFocus();
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

	/**
	 * Set the layout data for a button.
	 */
	protected GridData setButtonLayoutData(Button button) {
		return super.setButtonLayoutData(button);
	}
}