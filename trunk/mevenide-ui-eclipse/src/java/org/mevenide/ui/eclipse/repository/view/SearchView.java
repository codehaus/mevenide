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
package org.mevenide.ui.eclipse.repository.view;

import java.util.List;
import java.util.TreeSet;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.preferences.DependencyTypeRegistry;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SearchView extends ViewPart {

    
    private static final String GROUP = "Group";
    private static final String ARTIFACT_NAME = "Artifact Name";
    private static final String VERSION = "Version";
    private static final String TYPE = "Type";
    
    private Text groupText;
    
    private Combo typeCombo;
    
    private Combo repoCombo;
    
    private Button searchButton;
    
    private TableViewer searchResults;
    private String[] columnNames = new String[] { GROUP, ARTIFACT_NAME, VERSION, TYPE }; 
    
    private PreferencesManager preferencesManager = PreferencesManager.getManager();
    
    public SearchView() {
    }

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());
        
        
        //ScrolledComposite scrolledContainer = new ScrolledComposite(parent, SWT.V_SCROLL);
        //Composite container = new Composite(scrolledContainer, SWT.NULL);
        //scrolledContainer.setContent(container);
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        layout.makeColumnsEqualWidth = false;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        container.setBackground(MevenideColors.WHITE);
        
        createGroupText(container);
        
        createTypeCombo(container);
        
        createRepoCombo(container);
        
        Button searchButton = new Button(container, SWT.NULL);
        searchButton.setText("Search");
        
        container.setSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        createSearchResultTable(parent);
    }

    private void createRepoCombo(Composite container) {
        Composite composite = new Composite(container, SWT.NULL);
        composite.setBackground(MevenideColors.WHITE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.makeColumnsEqualWidth = false;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Text label = new Text(composite, SWT.READ_ONLY);
        label.setText("Repository");
        label.setBackground(MevenideColors.WHITE);
        
        repoCombo = new Combo(composite, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
        
        List definedRepositories = RepositoryList.getUserDefinedRepositories();
        List mirrors = RepositoryList.getUserDefinedMirrors();
        
        TreeSet repositories = new TreeSet();
        repositories.addAll(definedRepositories);
        repositories.addAll(mirrors);
        
        repoCombo.setItems((String[]) repositories.toArray(new String[repositories.size()]));
        
        Button addRepoButton = new Button(composite, SWT.FLAT);
        addRepoButton.setText(">>"); //todo : replace text by an image
        addRepoButton.setToolTipText("Add repository");
        addRepoButton.setEnabled(false);
        //addRepoButton.addSelectionListener()
    }

    private void createSearchResultTable(Composite resultContainer) {
        Table table = createTable(resultContainer);

        searchResults = new TableViewer(table);
        searchResults.setUseHashlookup(true);
        searchResults.setColumnProperties(columnNames);
        
        CellEditor[] editors = new CellEditor[columnNames.length];

        for (int i = 0; i < editors.length; i++) {
            editors[i] = new TextCellEditor(table, SWT.READ_ONLY);
        }
        
        searchResults.setCellEditors(editors);
        
    }

    private Table createTable(Composite resultContainer) {
        Table table = new Table(resultContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        table.setLayoutData(data);
        
        TableColumn groupColumn = new TableColumn(table, SWT.LEFT);
        groupColumn.setWidth(160);
        groupColumn.setText(GROUP);
        groupColumn.setResizable(true);
        
        TableColumn artifactNameColumn = new TableColumn(table, SWT.LEFT);
        artifactNameColumn.setWidth(200);
        artifactNameColumn.setText(ARTIFACT_NAME);
        artifactNameColumn.setResizable(true);
        
        TableColumn versionColumn = new TableColumn(table, SWT.LEFT);
        versionColumn.setWidth(120);
        versionColumn.setText(VERSION);
        versionColumn.setResizable(true);
        
        TableColumn typeColumn = new TableColumn(table, SWT.LEFT);
        typeColumn.setWidth(70);
        typeColumn.setText(TYPE);
        typeColumn.setResizable(true);
        
        return table;
    }

    private void createTypeCombo(Composite container) {
        Composite composite = new Composite(container, SWT.NULL);
        composite.setBackground(MevenideColors.WHITE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.makeColumnsEqualWidth = false;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Text label = new Text(composite, SWT.READ_ONLY);
        label.setText("Type");
        label.setBackground(MevenideColors.WHITE);
        
        typeCombo = new Combo(composite, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
        
        String[] userTypes = DependencyTypeRegistry.getUserRegisteredTypes();
        String[] types = new String[Mevenide.KNOWN_DEPENDENCY_TYPES.length + userTypes.length];
        System.arraycopy(userTypes, 0, types, 0, userTypes.length);
        System.arraycopy(Mevenide.KNOWN_DEPENDENCY_TYPES, 
                         userTypes.length, 
                         types, 
                         userTypes.length, 
                         Mevenide.KNOWN_DEPENDENCY_TYPES.length);
        
        typeCombo.setItems(types);
        
        Button manageTypesButton = new Button(composite, SWT.FLAT);
        manageTypesButton.setText(">>"); //todo : replace text by an image
        manageTypesButton.setToolTipText("Manage types");
        manageTypesButton.setEnabled(false);
        //manageTypesButton.addSelectionListener()
        
    }

    private void createGroupText(Composite container) {
        Composite composite = new Composite(container, SWT.NULL);
        composite.setBackground(MevenideColors.WHITE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        composite.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayoutData(gd);
        
        Text label = new Text(composite, SWT.READ_ONLY);
        label.setText("Group");
        label.setBackground(MevenideColors.WHITE);
        
        groupText = new Text(composite, SWT.BORDER);
        
    }

    public void setFocus() {
    }
}
