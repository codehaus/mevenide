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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.ui.eclipse.Mevenide;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SearchResultView extends ViewPart {
    
    private static final String GROUP = "Group";
    private static final String ARTIFACT_NAME = "Artifact Name";
    private static final String VERSION = "Version";
    private static final String TYPE = "Type";
    
    private TableViewer searchResults;
    private String[] columnNames = new String[] { GROUP, ARTIFACT_NAME, VERSION, TYPE }; 
    
    public SearchResultView() {
    }

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());
        
        createSearchResultTable(parent);
    }
    
    public void setInput(RepoPathElement element) {
        searchResults.setInput(element);
        searchResults.refresh();
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
        searchResults.setContentProvider(new IStructuredContentProvider(){
            public void dispose() {
            }
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
            public Object[] getElements(Object inputElement) {
                if ( inputElement instanceof RepoPathElement ) {
                    try {
                        return ((RepoPathElement) inputElement).getChildren();
                    } catch (Exception e) {
                        final String msg = "Unable to fetch children.";
                        Mevenide.displayError(msg, e);
                    } 
                }
                return null;
            }
        });
        searchResults.setLabelProvider(new SearchResultLabelProvider());
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

    

    public void setFocus() {
    }
}
