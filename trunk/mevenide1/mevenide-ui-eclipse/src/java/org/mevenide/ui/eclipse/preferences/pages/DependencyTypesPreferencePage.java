/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

package org.mevenide.ui.eclipse.preferences.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.DependencyTypeRegistry;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyTypesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private static final String PAGE_NAME = Mevenide.getResourceString("DependencyTypesPreferencePage.title"); //$NON-NLS-1$
//    private static final String PAGE_DESC = Mevenide.getResourceString("DependencyTypesPreferencePage.description"); //$NON-NLS-1$

    private static final String ROOT = "ROOT"; //$NON-NLS-1$
    private List types = new ArrayList();   
    
	private Composite topLevelContainer;
    private TableViewer typesViewer;
    private Button addTypeButton;
    private Button removeTypeButton;
    
	public DependencyTypesPreferencePage() {
        super(PAGE_NAME);
//        super.setDescription(PAGE_DESC);
        super.setPreferenceStore(Mevenide.getInstance().getCustomPreferenceStore());
//        super.setImageDescriptor(MavenPlugin.getImageDescriptor("sample.gif"));
    }
	
    protected Control createContents(Composite parent) {
		topLevelContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		topLevelContainer.setLayout(layout);

		createDependencyTypesEditor();
		createButtons();
		
		return topLevelContainer;
	}
		
	private void createButtons() {
	    Composite parent = new Composite(topLevelContainer, SWT.NULL);
	    parent.setLayout(new GridLayout());
	    parent.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        createAddTypeButton(parent);
		createRemoveTypeButton(parent);
    }

    private void createDependencyTypesEditor() {
	    unserializeTypes();
	    
        typesViewer = new TableViewer(topLevelContainer, SWT.BORDER);
        typesViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        
        typesViewer.setContentProvider( new IStructuredContentProvider() {
            public Object[] getElements(Object inputElement) {
                //unconditionally set elements to types - no matter what the input is 
                return types.toArray();
            }
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
            public void dispose() { }
        });
        
        typesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                if ( typesViewer.getSelection() != null ) {
                    IStructuredSelection selection = (IStructuredSelection) typesViewer.getSelection();
                    String currentType = (String) selection.getFirstElement();
                    boolean enableRemove = !Arrays.asList(DependencyTypeRegistry.KNOWN_TYPES).contains(currentType);
                    removeTypeButton.setEnabled(enableRemove);
                }
            }	 
        });
        
        typesViewer.setSorter(new ViewerSorter() {
            public int compare(Viewer viewer, Object e1, Object e2) {
                return ((String) e1).compareTo((String) e2);
            }
        });
        
        typesViewer.setInput(ROOT);
    }

    public boolean performOk() {
        return finish();
    }
    
	private void unserializeTypes() {
        String[] registeredTypes = DependencyTypeRegistry.getUserRegisteredTypes();
        
        for (int i = 0; i < registeredTypes.length; i++) {
            types.add(registeredTypes[i]);
        }
        
        for (int i = 0; i < DependencyTypeRegistry.KNOWN_TYPES.length; i++) {
            types.add(DependencyTypeRegistry.KNOWN_TYPES[i]);
        }
    }

	public void init(IWorkbench workbench) { }
	
	private boolean finish() {
	    
		return DependencyTypeRegistry.storeTypes(types);
	    
	}
	
	private void createAddTypeButton(Composite parent) {
        addTypeButton = new Button(parent, SWT.PUSH);
        addTypeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addTypeButton.setAlignment(SWT.LEFT);
        addTypeButton.setText(Mevenide.getResourceString("DependencyTypesPreferencePage.type.add"));//$NON-NLS-1$
        addTypeButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                //do nothing
            }
            public void widgetSelected(SelectionEvent e) {
                String newType = openNewTypeDialog();
                if ( !StringUtils.isNull(newType) ) { //&& !CANCEL
                    addType(newType);
                }
            }
        });
    }
    
    private String openNewTypeDialog() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String dialogTitle = Mevenide.getResourceString("DependencyTypesPreferencePage.NewType.Dialog.Title"); //$NON-NLS-1$
        String dialogMessage = Mevenide.getResourceString("DependencyTypesPreferencePage.NewType.Dialog.Message"); //$NON-NLS-1$
        IInputValidator validator = new IInputValidator() {
            public String isValid(String newText) {
                if ( types.contains(newText) ) {
                    return Mevenide.getResourceString("DependencyTypesPreferencePage.Validator.Type.AlreadyExists"); //$NON-NLS-1$
                }
                if ( StringUtils.isNull(newText) ) {
                    return Mevenide.getResourceString("DependencyTypesPreferencePage.Validator.Type.NullNotAllowed"); //$NON-NLS-1$
                }
                return null;
            }  
        };
        InputDialog dialog = new InputDialog(shell, dialogTitle, dialogMessage, null, validator);
        int result = dialog.open();
        return result == Window.OK ? dialog.getValue() : null;
    }
	
    private void createRemoveTypeButton(Composite parent) {
        removeTypeButton = new Button(parent, SWT.PUSH);
        removeTypeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        removeTypeButton.setAlignment(SWT.LEFT);
        removeTypeButton.setText(Mevenide.getResourceString("DependencyTypesPreferencePage.type.remove"));//$NON-NLS-1$
        removeTypeButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                //do nothing
            }
            public void widgetSelected(SelectionEvent e) {
                if ( typesViewer.getSelection() != null ) {
                    IStructuredSelection selection = (IStructuredSelection) typesViewer.getSelection();
                    String currentType = (String) selection.getFirstElement();
                    removeType(currentType);
                }
            }
        });
    }
    
    private void removeType(String currentType) {
        types.remove(currentType);
        typesViewer.refresh();
    }
    
    private void addType(String newType) {
        types.add(newType);
        typesViewer.refresh();
    }
}
