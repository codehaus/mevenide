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

package org.mevenide.ui.eclipse.preferences.pages;

import java.io.File;

import org.apache.maven.project.Project;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.Template;
import org.mevenide.ui.eclipse.template.model.TemplateContentProvider;
import org.mevenide.ui.eclipse.template.model.Templates;
import org.mevenide.ui.eclipse.template.view.TemplateViewerFactory;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.util.StringUtils;

/**
 * This preference page contains information about the POM templates which
 * the used has added to his or her .meta folder. From this page it's possible 
 * for add and remove templates. 
 * 
 * @todo In the future it might also be possible to edit the templates.
 *   
 * @author	<a href="mailto:jens@iostream.net">Jens Andersen</a>, Last updated by $Author$
 * @version $Id$
 */
public class TemplatePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private static final String PAGE_NAME = Mevenide.getResourceString("TemplatePreferencePage.label"); //$NON-NLS-1$
//    private static final String PAGE_DESC = Mevenide.getResourceString("TemplatePreferencePage.description"); //$NON-NLS-1$

    private Templates fTemplates;
    private TreeViewer templateViewer;
    private Template fCurrentSelection;
    private ISelectionChangedListener fSelectionProvider;

    /**
     * Initializes a new instance of TemplatePreferencePage.
     */
    public TemplatePreferencePage() {
        super(PAGE_NAME);
//        super.setDescription(PAGE_DESC);
        super.setPreferenceStore(Mevenide.getInstance().getCustomPreferenceStore());
    }

    public void init(IWorkbench arg0) {
        fTemplates = Templates.newTemplates();
    }
    
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 2;
        composite.setLayout(layout);
        
        createTemplateViewer(composite);
        
        createButtonAreaComposite(composite);
        
        return composite;
    }

    private void createTemplateViewer(Composite parent) {
        templateViewer = TemplateViewerFactory.createTemplateViewer(parent);
        fTemplates.addObserver((TemplateContentProvider) templateViewer.getContentProvider());
        templateViewer.setInput(fTemplates);
        
        fSelectionProvider = new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                fCurrentSelection = (Template) selection.getFirstElement();
            }
        };
        templateViewer.addSelectionChangedListener(fSelectionProvider);
    }

    private void createButtonAreaComposite(Composite parent) {
        Composite buttonAreaComposite = new Composite(parent, SWT.NONE);
        GridLayout envButtonLayout = new GridLayout();
        envButtonLayout.marginHeight = 0;
        envButtonLayout.marginWidth = 0;
        buttonAreaComposite.setLayout(envButtonLayout);
        
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
        buttonAreaComposite.setLayoutData(gd);
        
        createAddTemplateButton(buttonAreaComposite);
        createRemoveTemplateButton(buttonAreaComposite);
    }

    private void createRemoveTemplateButton(Composite parent) {
        Button removeTemplateButton = new Button(parent, SWT.PUSH);
        removeTemplateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        removeTemplateButton.setAlignment(SWT.LEFT);
        removeTemplateButton.setText(Mevenide.getResourceString("TemplatePreferencePage.template.remove"));//$NON-NLS-1$
        removeTemplateButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                if (fCurrentSelection != null) {
                    if (MessageDialog.openConfirm(
                                    getShell(),
                                    Mevenide.getResourceString("TemplatePreferencePage.del.confirm.title"),  //$NON-NLS-1$
                                    Mevenide.getResourceString("TemplatePreferencePage.del.confirm.message", fCurrentSelection.getTemplateName()))) //$NON-NLS-1$ 
                    {
                        fCurrentSelection.getProject().getFile().delete();
                        fTemplates.removeTemplate(fCurrentSelection);
                        fCurrentSelection = null;
                    }
                }
            }
        });
    }

    private void createAddTemplateButton(Composite parent) {
        Button addTemplateButton = new Button(parent, SWT.PUSH);
        addTemplateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addTemplateButton.setAlignment(SWT.LEFT);
        addTemplateButton.setText(Mevenide.getResourceString("TemplatePreferencePage.template.add"));//$NON-NLS-1$
        addTemplateButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                //do nothing
            }

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
                String userChoice = dialog.open();
                if ( !StringUtils.isNull(userChoice) ) {
	                IPath p = new Path(dialog.getFilterPath() + File.separator + dialog.getFileName());
		                if (!copyTemplateToPreferences(p))
		                    MessageDialog.openError(
		                                    getShell(),
		                                    Mevenide.getResourceString("TemplatePreferencePage.op_error_title"),  //$NON-NLS-1$
		                                    Mevenide.getResourceString("TemplatePreferencePage.op_error_create.message"));//$NON-NLS-1$
                }
            }
        });
    }

    private boolean copyTemplateToPreferences(IPath source) {
        JDomProjectUnmarshaller unmarshaller = new JDomProjectUnmarshaller();

        try {
            Project pom = unmarshaller.parse(source.toFile());

            IPath dest = Mevenide.getInstance().getStateLocation().append("templates");//$NON-NLS-1$
            // If it's the first template we are going to add then create the template folder
            if (!dest.toFile().exists()) {
                dest.toFile().mkdir();
            }
            dest = dest.append(pom.toString() + pom.hashCode() + ".tmpl");//$NON-NLS-1$

            FileUtils.copyFile(source.toFile(), dest.toFile());

            pom = unmarshaller.parse(dest.toFile());
            fTemplates.addTemplate(new Template(pom));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}