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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.preferences.DependencyTypeRegistry;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SearchQueryView extends ViewPart {

    private Text groupText;
    
    private Combo typeCombo;
    
    private Combo repoCombo;
    
    private Button searchButton;
    
    
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        container.setBackground(MevenideColors.WHITE);
        
        createRepoCombo(container);

        createGroupText(container);
        
        createTypeCombo(container);
        
        Button searchButton = new Button(container, SWT.NULL);
        searchButton.setText("Search");
        
    }

    
    private void createRepoCombo(Composite container) {

        Hyperlink label = new Hyperlink(container, SWT.NULL);
        label.setUnderlined(true);
        label.addHyperlinkListener(new IHyperlinkListener(){
            public void linkActivated(HyperlinkEvent e) {
            }
            public void linkEntered(HyperlinkEvent e) {
            }
            public void linkExited(HyperlinkEvent e) {
            }
        });
        label.setToolTipText("Add repository");
        label.setText("Repository");
        label.setForeground(MevenideColors.BLUE);
        label.setBackground(MevenideColors.WHITE);
        
        repoCombo = new Combo(container, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
        
        List definedRepositories = RepositoryList.getUserDefinedRepositories();
        List mirrors = RepositoryList.getUserDefinedMirrors();
        
        TreeSet repositories = new TreeSet();
        repositories.addAll(definedRepositories);
        repositories.addAll(mirrors);
        
        repoCombo.setItems((String[]) repositories.toArray(new String[repositories.size()]));
        repoCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
    }
    
    private void createTypeCombo(Composite container) {
        Hyperlink label = new Hyperlink(container, SWT.NULL);
        label.setUnderlined(true);
        label.addHyperlinkListener(new IHyperlinkListener(){
            public void linkActivated(HyperlinkEvent e) {
            }
            public void linkEntered(HyperlinkEvent e) {
            }
            public void linkExited(HyperlinkEvent e) {
            }
        });
        label.setToolTipText("Manage Type");
        label.setText("Type");
        label.setForeground(MevenideColors.BLUE);
        label.setBackground(MevenideColors.WHITE);
        
        typeCombo = new Combo(container, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
        typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        String[] userTypes = DependencyTypeRegistry.getUserRegisteredTypes();
        String[] types = new String[Mevenide.KNOWN_DEPENDENCY_TYPES.length + userTypes.length];
        System.arraycopy(userTypes, 0, types, 0, userTypes.length);
        System.arraycopy(Mevenide.KNOWN_DEPENDENCY_TYPES, 
                         userTypes.length, 
                         types, 
                         userTypes.length, 
                         Mevenide.KNOWN_DEPENDENCY_TYPES.length);
        
        typeCombo.setItems(types);
        
    }

    private void createGroupText(Composite container) {
        Text label = new Text(container, SWT.READ_ONLY);
        label.setText("Group");
        label.setBackground(MevenideColors.WHITE);
        
        groupText = new Text(container, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        groupText.setLayoutData(data);
    }
    
    public void setFocus() {
    }
}
