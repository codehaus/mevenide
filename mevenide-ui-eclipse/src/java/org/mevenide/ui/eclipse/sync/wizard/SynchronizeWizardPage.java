/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
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
package org.mevenide.ui.eclipse.sync.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mevenide.Environment;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.DependencyGroup;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupContentProvider;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupMarshaller;
import org.mevenide.ui.eclipse.sync.model.DependencyWrapper;
import org.mevenide.ui.eclipse.sync.model.SourceDirectory;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroup;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroupMarshaller;
import org.mevenide.ui.eclipse.sync.control.DependencyMappingViewControl;
import org.mevenide.ui.eclipse.sync.control.SourceDirectoryMappingViewControl;
import org.mevenide.ui.eclipse.util.ResourceSorter;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: SynchronizeWizardPage.java 27 août 2003 Exp gdodinet 
 * 
 */
public class SynchronizeWizardPage extends WizardPage {
	private static Log log = LogFactory.getLog(SynchronizeWizardPage.class);
	
	
	private TabItem sourceDirectoriesSynchronizationTab;
    private TabItem dependenciesSynchronizationTab;
    private TabFolder tabFolder;
	
	private IProject project;
	
	private TableViewer sourceDirectoriesViewer;
	
	private TableTreeViewer dependenciesViewer;
	private Button addDependencyButton;
	private Button removeDependencyButton;
	private Button dependencyPropertiesButton;
	private Button refreshDependenciesButton;


    private PreferenceStore inheritancePropertiesStore;


    private BooleanFieldEditor isInheritedEditor;


    private FileFieldEditor parentPomEditor;


    private Composite bottomControls;

	

    public SynchronizeWizardPage() {
		super("Artifacts Synchronization");
		setTitle("Artifacts Synchronization");
		setDescription("This page let you control which artifact should be synchronized and allow you to specify which ones are inherited.");
		setImageDescriptor(Mevenide.getImageDescriptor("dep-synch-64.gif"));
	
	}
	
    public void createControl(Composite parent) {
    	Composite topLevelContainer = new Composite(parent, SWT.NULL);
    	GridLayout layout = new GridLayout();
    	topLevelContainer.setLayout(layout);
    	
        tabFolder = new TabFolder(topLevelContainer, SWT.NULL);
        
        sourceDirectoriesSynchronizationTab = new TabItem(tabFolder, SWT.NORMAL);
		sourceDirectoriesSynchronizationTab.setText("Source Directories");
		//sourceDirectoriesSynchronizationTab.setImage(Mevenide.getImageDescriptor("source-directory-16.gif").createImage());
		
		dependenciesSynchronizationTab = new TabItem(tabFolder, SWT.NORMAL);
		dependenciesSynchronizationTab.setText("Dependencies");
		//sourceDirectoriesSynchronizationTab.setImage(Mevenide.getImageDescriptor("dependencies-16.gif").createImage());
        
        initSourceSynchronizationTabControl();
        initDependenciesSynchronizationTab();
		
		tabFolder.setSelection(0);
		GridData tabFolderData = new GridData(GridData.FILL_BOTH);
		tabFolderData.grabExcessHorizontalSpace = true;
		tabFolder.setLayoutData(tabFolderData);
		
		createBottomControls(topLevelContainer);
		
        setControl(topLevelContainer);
        
    }

	private void createBottomControls(Composite parent) {
		bottomControls = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		bottomControls.setLayout(layout);
		GridData bottomControlsData = new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL);
		bottomControls.setLayoutData(bottomControlsData);

		inheritancePropertiesStore = new PreferenceStore(Mevenide.getPlugin().getPreferencesFilename());
		try {
            inheritancePropertiesStore.load();
        }
        catch (IOException e) {
           log.error("Cannot load Preference Store due to : " + e);
        }

        isInheritedEditor = new BooleanFieldEditor("pom." + project.getName() + ".isInherited",  "Is Inherited", bottomControls) ;
        isInheritedEditor.fillIntoGrid(bottomControls, 3);		
		isInheritedEditor.setPreferenceStore(inheritancePropertiesStore);
		isInheritedEditor.load();

		isInheritedEditor.setPropertyChangeListener(
			new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					((DependencyGroup)dependenciesViewer.getInput()).setInherited(((Boolean) event.getNewValue()).booleanValue());
					((SourceDirectoryGroup)sourceDirectoriesViewer.getInput()).setInherited(((Boolean) event.getNewValue()).booleanValue());
					
					parentPomEditor.setEnabled(((Boolean) event.getNewValue()).booleanValue(), bottomControls);
					
					dependenciesViewer.refresh(true);
					sourceDirectoriesViewer.refresh(true);
                }
			}
		);		

		parentPomEditor = new FileFieldEditor("pom." + project.getName() + ".parent", "Parent POM", bottomControls);
        parentPomEditor.setFileExtensions(new String[] { "*.xml" });
		parentPomEditor.fillIntoGrid(bottomControls, 3);		
		parentPomEditor.setPreferenceStore(inheritancePropertiesStore);
		parentPomEditor.load();
	}
	
	private void initSourceSynchronizationTabControl() {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		composite.setLayout(layout);

		createSourceDirectoriesViewer(composite);
		createSourceDirectoriesButtons(composite);
		
		sourceDirectoriesSynchronizationTab.setControl(composite);
	}
	
	
	private void createSourceDirectoriesViewer(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
	
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
	
		sourceDirectoriesViewer = SourceDirectoryMappingViewControl.getViewer(composite, SWT.BORDER);
		setSourceDirectoriesInput(((SynchronizeWizard)getWizard()).getProject());
	}

	private void createSourceDirectoriesButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
	
		Button addButton = new Button(composite, SWT.PUSH);
		addButton.setText("Add...");
		addButton.setToolTipText("Add a dependency");
		GridData addButtonData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonData.grabExcessHorizontalSpace = true;
		addButton.setLayoutData(addButtonData);
		addButton.setEnabled(true);
	
		Button removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setToolTipText("Remove dependency");
		GridData removeButtonData = new GridData(GridData.FILL_HORIZONTAL);
		removeButtonData.grabExcessHorizontalSpace = true;
		removeButton.setLayoutData(removeButtonData);
		removeButton.setEnabled(true);
	
		Button refreshButton = new Button(composite, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.setToolTipText("Refresh project dependencies");
		GridData refreshButtonData = new GridData(GridData.FILL_HORIZONTAL);
		refreshButtonData.grabExcessHorizontalSpace = true;
		refreshButton.setLayoutData(refreshButtonData);
	
		addButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						IContainer container = openSourceDirectoryDialog();
						if ( container != null ) {
							SourceDirectory directory = new SourceDirectory(container.getFullPath().removeFirstSegments(1).toOSString(), ((SourceDirectoryGroup)sourceDirectoriesViewer.getInput()));
							((SourceDirectoryGroup) sourceDirectoriesViewer.getInput()).addSourceDirectory(directory);
							sourceDirectoriesViewer.refresh();
						}
					
					}
				}
		);
	
		removeButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						TableItem[] items = sourceDirectoriesViewer.getTable().getSelection();
						for (int i = 0; i < items.length; i++) {
							TableItem item = items[i];
							((SourceDirectoryGroup) sourceDirectoriesViewer.getInput()).getSourceDirectories().remove(item.getData());
							((SourceDirectoryGroup) sourceDirectoriesViewer.getInput()).excludeSourceDirectory((SourceDirectory) item.getData());
							sourceDirectoriesViewer.refresh();
						}
					}
				}
		);
	
		refreshButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						initSourceDirectoriesInput(project);
					}
				}
		);
	}

	private void initSourceDirectoriesInput(IProject project) {
		sourceDirectoriesViewer.setInput(new SourceDirectoryGroup(project));
	}

	private IContainer openSourceDirectoryDialog() {
		IWorkspaceRoot root= project.getWorkspace().getRoot();
		Class[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
		ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
		IProject[] allProjects= root.getProjects();
	
		//modified ArrayList rejectedElements= new ArrayList(allProjects.length);
		ArrayList rejectedElements= new ArrayList();
		for (int i= 0; i < allProjects.length; i++) {
			if (!allProjects[i].equals(project)) {
				rejectedElements.add(allProjects[i]);
			}
		}
	
		//added
		List list = ((SourceDirectoryGroup)sourceDirectoriesViewer.getInput()).getSourceDirectories();
		for (int i = 0; i < list.size(); i++) {
			SourceDirectory directory = (SourceDirectory) list.get(i);
			rejectedElements.add(project.getFolder(directory.getDirectoryPath()));
		} 

		ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

		ILabelProvider lp= new WorkbenchLabelProvider();
		ITreeContentProvider cp= new WorkbenchContentProvider();

		IResource initSelection= null;
	
		FolderSelectionDialog dialog= new FolderSelectionDialog(getShell(), lp, cp);
		dialog.setTitle(NewWizardMessages.getString("OutputLocationDialog.ChooseOutputFolder.title")); //$NON-NLS-1$
		dialog.setValidator(validator);
		dialog.setMessage(NewWizardMessages.getString("OutputLocationDialog.ChooseOutputFolder.description")); //$NON-NLS-1$
		dialog.addFilter(filter);
		dialog.setInput(root);
		dialog.setInitialSelection(initSelection);
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));

		if (dialog.open() == ElementTreeSelectionDialog.OK) {
			return (IContainer)dialog.getFirstResult();
		}
		return null;
	}

	private void setSourceDirectoriesInput(IProject project) {
		this.project = project;
		if ( sourceDirectoriesViewer.getContentProvider() != null ) {
			SourceDirectoryGroup newInput = null ;
			try {
		
				newInput = getSavedSourceDirectoriesInput(project);
				log.debug("Found " + newInput.getSourceDirectories().size() + " previously stored SourceDirectories");
			}
			catch (Exception e) {
				log.debug("Error occured while restoring previously saved SourceDirectoryGroup for project '" + project.getName() + "'. Reason : " + e); 

			}
			if ( newInput == null ) {
			
				newInput = new SourceDirectoryGroup(project);
			}
	
			sourceDirectoriesViewer.setInput(newInput);
		}
	}
		
	private SourceDirectoryGroup getSavedSourceDirectoriesInput(IProject project) throws Exception {
		
		String savedStates = Mevenide.getPlugin().getFile("sourceTypes.xml");
	
		return SourceDirectoryGroupMarshaller.getSourceDirectoryGroup(project, savedStates);
	
	
	}
		
	private void initDependenciesSynchronizationTab() {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		composite.setLayout(layout);

		createDependenciesViewer(composite);
		createDependenciesViewerButtons(composite);
		
		dependenciesSynchronizationTab.setControl(composite);
		
		
	}
	
	private void createDependenciesViewer(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
	
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
	
		dependenciesViewer = DependencyMappingViewControl.getViewer(composite, SWT.BORDER);
		setDependenciesViewerInput(((SynchronizeWizard)getWizard()).getProject());
	
		dependenciesViewer.getTableTree().addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if ( dependenciesViewer.getTableTree().getSelection().length == 0 ) {
						removeDependencyButton.setEnabled(false);
						dependencyPropertiesButton.setEnabled(false);
					}
					else {
						removeDependencyButton.setEnabled(true);
						dependencyPropertiesButton.setEnabled(true);
					}
				}
			}
		);
	 
	}

	private void createDependenciesViewerButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addDependencyButton = new Button(composite, SWT.PUSH);
		addDependencyButton.setText("Add...");
		addDependencyButton.setToolTipText("Add a dependency");
		GridData addButtonData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonData.grabExcessHorizontalSpace = true;
		addDependencyButton.setLayoutData(addButtonData);
		addDependencyButton.setEnabled(true);	

		removeDependencyButton = new Button(composite, SWT.PUSH);
		removeDependencyButton.setText("Remove");
		removeDependencyButton.setToolTipText("Remove dependency");
		GridData removeButtonData = new GridData(GridData.FILL_HORIZONTAL);
		removeButtonData.grabExcessHorizontalSpace = true;
		removeDependencyButton.setLayoutData(removeButtonData);
		removeDependencyButton.setEnabled(false);
	
		dependencyPropertiesButton = new Button(composite, SWT.PUSH);
		dependencyPropertiesButton.setText("Properties");
		dependencyPropertiesButton.setToolTipText("Set depedencency properties");
		GridData propertiesButtonData = new GridData(GridData.FILL_HORIZONTAL);
		propertiesButtonData.grabExcessHorizontalSpace = true;
		dependencyPropertiesButton.setLayoutData(propertiesButtonData);
		dependencyPropertiesButton.setEnabled(false);
	
		refreshDependenciesButton = new Button(composite, SWT.PUSH);
		refreshDependenciesButton.setText("Refresh");
		refreshDependenciesButton.setToolTipText("Refresh from .classpath");
		GridData refreshButtonData = new GridData(GridData.FILL_HORIZONTAL);
		refreshButtonData.grabExcessHorizontalSpace = true;
		refreshDependenciesButton.setLayoutData(refreshButtonData);
	
		addDependencyButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						try {
							FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
							dialog.setFilterPath(Mevenide.getPlugin().getMavenRepository());
						
							String path = dialog.open();
							if ( path != null ) {
								Dependency dependencyToAdd = DependencyFactory.getFactory().getDependency(path);
								if ( !((DependencyGroup)dependenciesViewer.getInput()).containsDependency(dependencyToAdd) ) {
									((DependencyGroup)dependenciesViewer.getInput()).addDependency(new DependencyWrapper(dependencyToAdd, false, (DependencyGroup)dependenciesViewer.getInput()));
									log.debug("Added Dependency : " + path);
									dependenciesViewer.refresh();
									if ( Environment.getMavenRepository() == null || Environment.getMavenRepository().trim().equals("") ) {
										MessageBox messageBox = new MessageBox (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK);
										messageBox.setText ("Unset Property : Meven Local Repository");
										messageBox.setMessage ("Maven Repository has not been, thus groupId wont be resolved. Please see Windows > Preferences > Maven");
										messageBox.open ();
									}
								}
							}
						}
						catch ( Exception ex ) {
							log.debug("Problem occured while trying to add a Dependency due to : " + e);
							ex.printStackTrace();
						}
					}
				}
		);
	
		removeDependencyButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						TableTreeItem[] items = dependenciesViewer.getTableTree().getSelection();
						for (int i = 0; i < items.length; i++) {
							TableTreeItem item = items[i];
							while ( item.getParentItem() != null ) {
								item = item.getParentItem();
							}
							((DependencyGroup) dependenciesViewer.getInput()).excludeDependency((DependencyWrapper) item.getData());
							dependenciesViewer.refresh();
						}
						removeDependencyButton.setEnabled(false);
						dependencyPropertiesButton.setEnabled(false);
					}
				}
		);
	
		dependencyPropertiesButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						DependencyPropertiesDialog dialog = new DependencyPropertiesDialog();
						dialog.setInput(getSelectedDependency());
						dialog.open();
						Map props = dialog.getProperties();
						if ( props != null ) {
							Dependency affectedDependency = getSelectedDependency();
						
							//crap... still that _not really_ deprecated stuff (dependency.properties is a list !) 
							Iterator it = props.keySet().iterator();
							while ( it.hasNext() ) {
								String propName = (String) it.next();
								String propValue = (String) props.get(propName);
								affectedDependency.addProperty(propName + ":" + propValue);
								affectedDependency.resolvedProperties().put(propName, propValue);
							
							}
						
						
							//affectedDependency.setProperties(props);
						}
					}
					private Dependency getSelectedDependency() {
						IStructuredSelection selection = (IStructuredSelection) dependenciesViewer.getSelection();
						Dependency affectedDependency = null;
						Object sel = selection.getFirstElement();
						log.debug("selected : " + sel.getClass() + " item");
						if ( sel instanceof DependencyWrapper ) {
							affectedDependency = ((DependencyWrapper) sel).getDependency();
						}
						else {
							affectedDependency = ((DependencyGroupContentProvider.DependencyInfo) sel).getDependency();
						}
						return affectedDependency;
					}
				}
		);
	
		refreshDependenciesButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						initDependenciesViewerInput(project);
					}
				}
		);
	
	}
	
	private void initDependenciesViewerInput(IProject project) {
		dependenciesViewer.setInput(new DependencyGroup(project));
	}

	private void setDependenciesViewerInput(IProject project) {
		this.project = project;
		if ( dependenciesViewer.getContentProvider() != null ) {
			DependencyGroup newInput = null ;
			try {
		
				newInput = getSavedDependenciesViewerInput(project);
			}
			catch (Exception e) {
				log.debug("Error occured while restoring previously saved DependencyGroup for project '" + project.getName() + "'. Reason : " + e);

			}
			if ( newInput == null ) {
				newInput = new DependencyGroup(project);
			}
	
			dependenciesViewer.setInput(newInput);
		}
	}
	
	private DependencyGroup getSavedDependenciesViewerInput(IProject project) throws Exception {
		
		String savedStates = Mevenide.getPlugin().getFile("statedDependencies.xml");
		
		return DependencyGroupMarshaller.getDependencyGroup(project, savedStates);
		
	}
	
	
	public void saveState() throws Exception {
		SourceDirectoryGroupMarshaller.saveSourceDirectoryGroup((SourceDirectoryGroup)sourceDirectoriesViewer.getInput(), Mevenide.getPlugin().getFile("sourceTypes.xml"));
		DependencyGroupMarshaller.saveDependencyGroup((DependencyGroup)dependenciesViewer.getInput(), Mevenide.getPlugin().getFile("statedDependencies.xml"));
		inheritancePropertiesStore.setValue("pom." + project.getName() + ".isInherited", isInheritedEditor.getBooleanValue());
		inheritancePropertiesStore.setValue("pom." + project.getName() + ".parent", parentPomEditor.getTextControl(bottomControls).getText());
		inheritancePropertiesStore.save();
	}
}
