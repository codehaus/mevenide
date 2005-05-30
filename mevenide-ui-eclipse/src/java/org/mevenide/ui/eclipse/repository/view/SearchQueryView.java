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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
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
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.repository.RepositoryReaderFactory;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.preferences.DependencyTypeRegistry;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SearchQueryView extends ViewPart implements RepositoryEventListener {

    private Text groupText;
    private CCombo typeCombo;
    private CCombo repoCombo;
    
    private Button searchButton;
    
    private FormToolkit toolkit;
    private ScrolledForm form;
    private Text errorText;
    
    public void createPartControl(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        toolkit.setBorderStyle(SWT.NULL);
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
    	
    	createErrorMessageArea(sectionClient);
    	createRepoCombo(sectionClient);
        createTypeCombo(sectionClient);
        createGroupText(sectionClient);
        
        section.setClient(sectionClient);

        updateButton();
        
    }

    
    private void createErrorMessageArea(Composite sectionClient) {
        errorText = toolkit.createText(sectionClient, null, SWT.READ_ONLY | SWT.FLAT);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 2;
        errorText.setLayoutData(data);
        
        errorText.setBackground(errorText.getParent().getBackground());
        errorText.setForeground(MevenideColors.RED);
        errorText.setText("All fields are required");
    }


    private void updateButton() {
        boolean hasError = StringUtils.isNull(repoCombo.getText()) ||
        				   StringUtils.isNull(typeCombo.getText()) ||
        				   StringUtils.isNull(groupText.getText()) ;
        errorText.setVisible(hasError);
        searchButton.setEnabled(!hasError);
    }


    private void createRepoCombo(Composite container) {
        Hyperlink label = toolkit.createHyperlink(container, null, SWT.NULL);
        label.setUnderlined(false); 
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
        
        Composite composite = new Composite(container, SWT.NULL);
        composite.setBackground(container.getBackground());
        GridLayout layout = new GridLayout();
        layout.marginWidth = 1;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        repoCombo = new CCombo(composite, SWT.FLAT | SWT.READ_ONLY) ;
        repoCombo.setBackground(composite.getBackground());
        repoCombo.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            public void widgetSelected(SelectionEvent arg0) {
                updateButton();
            }
        });
        repoCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        toolkit.paintBordersFor(composite);
        
        List definedRepositories = RepositoryList.getUserDefinedRepositories();
        List mirrors = RepositoryList.getUserDefinedMirrors();
        
        TreeSet repositories = new TreeSet();
        repositories.addAll(definedRepositories);
        repositories.addAll(mirrors);
        
        repoCombo.setItems((String[]) repositories.toArray(new String[repositories.size()]));
        repoCombo.setText(RepositoryList.MAIN_MAVEN_REPO);
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
        
        Composite composite = new Composite(container, SWT.NULL);
        composite.setBackground(container.getBackground());
        GridLayout layout = new GridLayout();
        layout.marginWidth = 1;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        typeCombo = new CCombo(composite, SWT.FLAT | SWT.READ_ONLY);
        typeCombo.setBackground(composite.getBackground());
        typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        typeCombo.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            public void widgetSelected(SelectionEvent arg0) {
                updateButton();
            }
        });
        toolkit.paintBordersFor(composite);
        
        updateTypeCombo();
        typeCombo.setText(Mevenide.DEPENDENCY_TYPE_JAR);
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
        layout.marginWidth = 1;
        layout.makeColumnsEqualWidth = false;
        searchComposite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalIndent = 0;
        searchComposite.setLayoutData(gridData);
        
        
        groupText = toolkit.createText(searchComposite, null, SWT.FLAT);
        groupText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        toolkit.paintBordersFor(searchComposite);
        
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        groupText.setLayoutData(data);
        groupText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                updateButton();
            }
        });
        groupText.addTraverseListener(new TraverseListener(){
            public void keyTraversed(TraverseEvent e) {
                if ( e.detail == SWT.TRAVERSE_RETURN && searchButton.isEnabled() ) {
                    try {
                        launchSearch();
                    } catch (Exception e1) {
                        // FIXME: Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        
        searchButton = new Button(searchComposite, SWT.FLAT);
        Image image = Mevenide.getInstance().getImageRegistry().get(IImageRegistry.SEARCH_BUTTON_ICON);
        searchButton.setToolTipText("Search");
        searchButton.setImage(image);
        
        
        searchButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            public void widgetSelected(SelectionEvent arg0) {
                try {
                    launchSearch();
                } catch (Exception e) {
                    // FIXME: Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
        });
    }
    
    public void dataLoaded(final RepositoryEvent event) {
        repoCombo.getDisplay().asyncExec(
            new Runnable() {
                public void run() {
                    RepoPathElement element = event.getElement();
                    RepoPathElement[] child = null;
                    try {
                        child = element.getChildren();
                    } catch (Exception e1) {
                        // FIXME: Auto-generated catch block
                        e1.printStackTrace();
                    }
			        if ( child != null && child.length > 0) {
				        SearchResultView view;
				        try {
		                    view = (SearchResultView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.mevenide.repository.search.result");
		                    view.setInput(element);
					    }
				        catch (PartInitException e) {
				            String message = "Unable to open search result page";
				            IStatus status = new Status(Status.ERROR, "org.mevenide.ui", 1, message, e);
				            Mevenide.getInstance().getLog().log(status);
				        }
			        }
			        else {
			            MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Artifact Result", "No Artifact Found");
			        }
                }
            }
    	);
    }
    
    public void setFocus() {
    }


    private void launchSearch() throws Exception {
        String baseUrl = repoCombo.getText();
        IRepositoryReader reader = RepositoryReaderFactory.createRemoteRepositoryReader(URI.create(baseUrl));
        RepoPathElement root = new RepoPathElement(reader, null);
        findGroup(root);
    }

    private void findGroup(RepoPathElement root) throws Exception {
        String gid = groupText.getText();
        RepoPathElement[] child = root.getChildren();
        for (int i = 0; i < child.length; ++i) {
            String groupId = child[i].getGroupId();
            if (groupId != null && groupId.equals(gid)) {
                findType(child[i]);
                break;
            }
        }
    }

    private void findType(RepoPathElement group) throws Exception {
        String tid = typeCombo.getText();
        RepoPathElement[] child = group.getChildren();
        for (int i = 0; i < child.length; ++i) {
            String type = child[i].getType();
            if (type != null && type.equals(tid)) {
                RepositoryObjectCollectorJob job = new RepositoryObjectCollectorJob(child[i]);
                job.addListener(this);
                job.schedule(Job.LONG);
                break;
            }
        }
    }
}
