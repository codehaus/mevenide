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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
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
    
    private FormToolkit toolkit;
    private ScrolledForm form;
    
    public void createPartControl(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        form.setText("Search Repository");
        form.getBody().setLayout(new GridLayout());
        form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Section section = toolkit.createSection(form.getBody(), Section.DESCRIPTION|Section.TWISTIE|Section.EXPANDED|Section.CLIENT_INDENT);
        GridData td = new GridData(GridData.FILL_BOTH);
    	section.setLayoutData(td);
    	section.addExpansionListener(new ExpansionAdapter() {
    		public void expansionStateChanged(ExpansionEvent e) {
    			form.reflow(true);
    		}
    	});
    	section.setText("Simple Search");
    	toolkit.createCompositeSeparator(section);
        
    	Composite sectionClient = toolkit.createComposite(section);
    	GridLayout clientLayout = new GridLayout();
    	clientLayout.numColumns = 2;
    	sectionClient.setLayout(clientLayout);
    	sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	createRepoCombo(sectionClient);
        createTypeCombo(sectionClient);
        createGroupText(sectionClient);
        
        section.setClient(sectionClient);
        
    }

    
    private void createRepoCombo(Composite container) {
        Hyperlink label = toolkit.createHyperlink(container, null, SWT.NULL);
        label.setUnderlined(true);
        label.setBackground(label.getParent().getBackground());
        label.addHyperlinkListener(new IHyperlinkListener(){
            public void linkActivated(HyperlinkEvent e) {
                //AddRepositoryDialog dialog = new AddRepositoryDialog();
            }
            public void linkEntered(HyperlinkEvent e) {
            }
            public void linkExited(HyperlinkEvent e) {
            }
        });
        label.setToolTipText("Add repository");
        label.setText("Repository");
        
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
        Hyperlink label = toolkit.createHyperlink(container, null, SWT.NULL);
        label.setUnderlined(true);
        label.setBackground(label.getParent().getBackground());
        label.addHyperlinkListener(new IHyperlinkListener(){
            public void linkActivated(HyperlinkEvent e) {
                PreferenceDialog d = new WorkbenchPreferenceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                        										   PlatformUI.getWorkbench().getPreferenceManager());
                d.setSelectedNode("org.mevenide.ui.eclipse.preferences.pages.DependencyTypesPreferencePage");
                
                d.setBlockOnOpen(true);
                d.open();
                
                updateTypeCombo();
            }
            public void linkEntered(HyperlinkEvent e) {
            }
            public void linkExited(HyperlinkEvent e) {
            }
        });
        label.setToolTipText("Manage Type");
        label.setText("Type");
        
        typeCombo = new Combo(container, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
        typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        updateTypeCombo();
        
    }

    private void updateTypeCombo() {
        String[] userTypes = DependencyTypeRegistry.getUserRegisteredTypes();
        List items = new ArrayList();
        items.addAll(Arrays.asList(userTypes));
        items.addAll(Arrays.asList(Mevenide.KNOWN_DEPENDENCY_TYPES));
        String[] types = (String[]) items.toArray(new String[items.size()]);
        typeCombo.setItems(types);
    }


    private void createGroupText(Composite container) {
        Text label = toolkit.createText(container, null, SWT.READ_ONLY);
        label.setBackground(label.getParent().getBackground());
        label.setText("Group");

        Composite searchComposite =  toolkit.createComposite(container, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.makeColumnsEqualWidth = false;
        searchComposite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalIndent = 0;
        searchComposite.setLayoutData(gridData);
        
        groupText = new Text(searchComposite, SWT.BORDER | SWT.FLAT); 
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        groupText.setLayoutData(data);
        
        Button searchButton = new Button(searchComposite, SWT.FLAT); 
        searchButton.setSize(10, 10);
        searchButton.setToolTipText("Search");
        searchButton.setImage(Mevenide.getInstance().getImageRegistry().get(IImageRegistry.PATTERN_SEARCH_ICON));
    }
    
    public void setFocus() {
    }
}
