/*
 * ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.preferences;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

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
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;
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
public class TemplatePreferencePage extends PreferencePage
		implements
			IWorkbenchPreferencePage {
	/**
	 * @param arg0
	 * @param arg1
	 */
	public TemplatePreferencePage() {
		super(Mevenide.getResourceString("TemplatePreferencePage.label"));//$NON-NLS-1$
		setPreferenceStore(PreferencesManager.getManager().getPreferenceStore());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench arg0) {
		// TODO Auto-generated method stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		TreeViewer templateViewer = new TreeViewer(composite);
		templateViewer.getControl().setLayoutData(gd);
		templateViewer.setUseHashlookup(true);
		//templateViewer.setContentProvider(new TreeViewContentProvider());
		//templateViewer.setLabelProvider(new TreeViewLabelProvider());
		//templateViewer.setInput();
		//templateViewer.setSorter(new NameSorter());
		ISelectionChangedListener selectionProvicer = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
			}
		};
		templateViewer.addSelectionChangedListener(selectionProvicer);
		
		Composite buttonComp = new Composite(composite, SWT.NONE);
		GridLayout envButtonLayout = new GridLayout();
		envButtonLayout.marginHeight = 0;
		envButtonLayout.marginWidth = 0;
		buttonComp.setLayout(envButtonLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		buttonComp.setLayoutData(gd);
		Button addtemplateButton = new Button(buttonComp, SWT.PUSH);
		addtemplateButton.setText(Mevenide.getResourceString("TemplatePreferencePage.template.add"));//$NON-NLS-1$
		addtemplateButton.addSelectionListener(new SelectionListener() {
			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent e) {
				//do nothing
			}
			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
				dialog.open();
				IPath p = new Path(dialog.getFilterPath() + File.separator + dialog.getFileName());
				
				if(!copyTemplateToPreferences(p))
					MessageDialog.openError(getShell(), Mevenide.getResourceString("TemplatePreferencePage.op_error_title"), Mevenide.getResourceString("TemplatePreferencePage.op_error_create.message"));
			}
		});
		Button removeTemplateButton = new Button(buttonComp, SWT.PUSH);
		removeTemplateButton.setText(Mevenide.getResourceString("TemplatePreferencePage.template.remove"));
		removeTemplateButton.addSelectionListener(new SelectionListener() {
			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			/**
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
			}
		});
		return composite;
	}
	/**
	 * 
	 */
	private boolean copyTemplateToPreferences(IPath source)
	{
		try{
			ProjectReader reader = ProjectReader.getReader();
			Project pom = reader.read(source.toFile());
			
			IPath dest = Mevenide.getInstance().getStateLocation().append("templates");

			//If it's the first template we are going to add then create the
			//template folder
			if(!dest.toFile().exists())
				dest.toFile().mkdir();
			dest = dest.append(pom.toString() + pom.hashCode() +".tmpl");

			copyFile(source.toFile(), dest.toFile());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	  public void copyFile(File in, File out) throws Exception {
	     FileChannel sourceChannel = new FileInputStream(in).getChannel();
	     FileChannel destinationChannel = new FileOutputStream(out).getChannel();
	     sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
	     sourceChannel.close();
	     destinationChannel.close();
	     }

}