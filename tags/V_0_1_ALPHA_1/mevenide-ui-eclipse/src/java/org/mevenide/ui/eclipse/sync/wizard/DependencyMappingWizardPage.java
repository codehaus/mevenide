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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableTreeViewer;
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
import org.eclipse.ui.PlatformUI;
import org.mevenide.Environment;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.DependencyGroup;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupContentProvider;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupMarshaller;
import org.mevenide.ui.eclipse.sync.view.DependencyMappingViewControl;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingWizardPage extends WizardPage {
	private static Log log = LogFactory.getLog(DependencyMappingWizardPage.class);
	
	private TableTreeViewer viewer;
	private Button addButton;
	private Button removeButton;
	private Button propertiesButton;
	private Button refreshButton;
	
	private IProject project;
	
	public DependencyMappingWizardPage() {
		super("Dependencies Synchronization");
		setTitle("Dependency Synchronization");
		setDescription("Please check the dependencies' groupId, artifactId and version");
		setImageDescriptor(Mevenide.getImageDescriptor("dep-synch-64.gif"));
		
		
	}

	

	public void createControl(Composite arg0) {
		Composite composite = new Composite(arg0, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		createViewer(composite);
		createButtons(composite);
		
		setControl(composite);
	}

	private void createViewer(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		
		viewer = DependencyMappingViewControl.getViewer(composite, SWT.BORDER);
		setInput(((SynchronizeWizard)getWizard()).getProject());
		
		viewer.getTableTree().addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if ( viewer.getTableTree().getSelection().length == 0 ) {
						removeButton.setEnabled(false);
						propertiesButton.setEnabled(false);
					}
					else {
						removeButton.setEnabled(true);
						propertiesButton.setEnabled(true);
					}
				}
			}
		);
		 
	}

	private void createButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
	
		addButton = new Button(composite, SWT.PUSH);
		addButton.setText("Add...");
		addButton.setToolTipText("Add a dependency");
		GridData addButtonData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonData.grabExcessHorizontalSpace = true;
		addButton.setLayoutData(addButtonData);
		addButton.setEnabled(true);	
	
		removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setToolTipText("Remove dependency");
		GridData removeButtonData = new GridData(GridData.FILL_HORIZONTAL);
		removeButtonData.grabExcessHorizontalSpace = true;
		removeButton.setLayoutData(removeButtonData);
		removeButton.setEnabled(false);
		
		propertiesButton = new Button(composite, SWT.PUSH);
		propertiesButton.setText("Properties");
		propertiesButton.setToolTipText("Set depedencency properties");
		GridData propertiesButtonData = new GridData(GridData.FILL_HORIZONTAL);
		propertiesButtonData.grabExcessHorizontalSpace = true;
		propertiesButton.setLayoutData(propertiesButtonData);
		propertiesButton.setEnabled(false);
		
		refreshButton = new Button(composite, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.setToolTipText("Refresh from .classpath");
		GridData refreshButtonData = new GridData(GridData.FILL_HORIZONTAL);
		refreshButtonData.grabExcessHorizontalSpace = true;
		refreshButton.setLayoutData(refreshButtonData);
		
		addButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						try {
							FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
							dialog.setFilterPath(Mevenide.getPlugin().getMavenRepository());
							
							String path = dialog.open();
							if ( path != null ) {
								Dependency dependencyToAdd = DependencyFactory.getFactory().getDependency(path);
								if ( !((DependencyGroup)viewer.getInput()).containsDependency(dependencyToAdd) ) {
									((DependencyGroup)viewer.getInput()).addDependency(dependencyToAdd);
									log.debug("Added Dependency : " + path);
									viewer.refresh();
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
		
		removeButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						TableTreeItem[] items = viewer.getTableTree().getSelection();
						for (int i = 0; i < items.length; i++) {
							TableTreeItem item = items[i];
							while ( item.getParentItem() != null ) {
								item = item.getParentItem();
							}
							((DependencyGroup) viewer.getInput()).getDependencies().remove((Dependency) item.getData());
							((DependencyGroup) viewer.getInput()).excludeDependency((Dependency) item.getData());
							viewer.refresh();
						}
						removeButton.setEnabled(false);
						propertiesButton.setEnabled(false);
					}
				}
		);
		
		propertiesButton.addSelectionListener(
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
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
						Dependency affectedDependency = null;
						Object sel = selection.getFirstElement();
						log.debug("selected : " + sel.getClass() + " item");
						if ( sel instanceof Dependency ) {
							affectedDependency = (Dependency) sel;
						}
						else {
							affectedDependency = ((DependencyGroupContentProvider.DependencyInfo) sel).getDependency();
						}
						return affectedDependency;
					}
				}
		);
		
		refreshButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						initInput(project);
					}
				}
		);
		
	}
	
	private void initInput(IProject project) {
		viewer.setInput(new DependencyGroup(project));
	}
	
	public void setInput(IProject project) {
		this.project = project;
		if ( viewer.getContentProvider() != null ) {
			DependencyGroup newInput = null ;
			try {
			
				newInput = getSavedInput(project);
			}
			catch (Exception e) {
				log.debug("Error occured while restoring previously saved DependencyGroup for project '" + project.getName() + "'. Reason : " + e);
	
			}
			if ( newInput == null ) {
				newInput = new DependencyGroup(project);
			}
		
			viewer.setInput(newInput);
		}
	}
	
	public DependencyGroup getInput() {
		return (DependencyGroup) viewer.getInput();
	}
	
	
	private DependencyGroup getSavedInput(IProject project) throws Exception {
		
		String savedStates = Mevenide.getPlugin().getFile("statedDependencies.xml");
			
		return DependencyGroupMarshaller.getDependencyGroup(project, savedStates);
			
	}
	
	
	public void saveState() throws Exception {
		DependencyGroupMarshaller.saveDependencyGroup((DependencyGroup)viewer.getInput(), Mevenide.getPlugin().getFile("statedDependencies.xml"));
	}
	


	


}
