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
import java.util.Observable;
import java.util.Observer;

import org.apache.maven.project.Project;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.Template;
import org.mevenide.ui.eclipse.template.model.Templates;
import org.mevenide.ui.eclipse.util.FileUtils;
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
public class TemplatePreferencePage extends PreferencePage implements IWorkbenchPreferencePage 
{
	private Templates fTemplates;
	private TreeViewer templateViewer;
	private Template fCurrentSelction;
	private ISelectionChangedListener fSelectionProvicer;
	
	class TemplateContentProvider implements ITreeContentProvider, Observer
	{
		private Object[] EMPTY_ARRAY = new Object[0];
		private Viewer fViewer;

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object arg0) {
			return false;
		}	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object obj) {
			if(obj instanceof Templates)
			{
				return ((Templates)obj).getTemplates(); 
			}
			return EMPTY_ARRAY;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object obj) {
			return getChildren(obj);
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			fViewer = viewer;
			
			if(oldInput != null && oldInput instanceof Templates)
				((Templates)oldInput).deleteObserver(this);
			if(newInput != null && newInput instanceof Templates)
				((Templates)newInput).addObserver(this);

		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}
		/* (non-Javadoc)
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			Display.getDefault().syncExec(new Runnable(){
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					fViewer.refresh();
				}	
			});
		}
	}
	
	class TemplateLabelProvider extends LabelProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object obj) {
			if (obj instanceof Template) {
				return ((Template)obj).getTemplateName();
			}
			return super.getText(obj);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj) {
			if (obj instanceof Template) {
				return Mevenide.getImageDescriptor("pom_file.gif").createImage();
			}
			return super.getImage(obj);
		}

	}
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
		fTemplates = new Templates();

		File tmplFolder = Mevenide.getInstance().getStateLocation().append("templates").toFile();//$NON-NLS-1$
		
		if(tmplFolder.exists())
		{
			Template tmp;
			File f;
			File[] tmpls = tmplFolder.listFiles();
			ProjectReader reader; 
			
			for(int i=0; i < tmpls.length; i++)
			{
				try
				{
					f = tmpls[i];
					if(f.getName().endsWith("tmpl"))//$NON-NLS-1$
					{
						reader = ProjectReader.getReader();
						Project pom = reader.read(f);
						fTemplates.addTemplate(new Template(pom));
					}
				}catch(Exception e)
				{
					
				}
			}
		}
		
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
		
		templateViewer = new TreeViewer(composite);
		templateViewer.getControl().setLayoutData(gd);
		templateViewer.setUseHashlookup(true);
		TemplateContentProvider contentProvider = new TemplateContentProvider();
		fTemplates.addObserver(contentProvider);
		templateViewer.setContentProvider(contentProvider);
		templateViewer.setLabelProvider(new TemplateLabelProvider());
		templateViewer.setInput(fTemplates);
		templateViewer.setSorter(new ViewerSorter());
		fSelectionProvicer = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				fCurrentSelction = (Template)selection.getFirstElement();
			}
		};
		templateViewer.addSelectionChangedListener(fSelectionProvicer);

		Composite buttonComp = new Composite(composite, SWT.NONE);
		GridLayout envButtonLayout = new GridLayout();
		envButtonLayout.marginHeight = 0;
		envButtonLayout.marginWidth = 0;
		buttonComp.setLayout(envButtonLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.HORIZONTAL_ALIGN_FILL);
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
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
				dialog.open();
				IPath p = new Path(dialog.getFilterPath() + File.separator + dialog.getFileName());

				if (!copyTemplateToPreferences(p))
					MessageDialog.openError(getShell(),Mevenide.getResourceString("TemplatePreferencePage.op_error_title"),Mevenide.getResourceString("TemplatePreferencePage.op_error_create.message"));//$NON-NLS-1$
			}
		});
		Button removeTemplateButton = new Button(buttonComp, SWT.PUSH);
		removeTemplateButton.setText(Mevenide.getResourceString("TemplatePreferencePage.template.remove"));//$NON-NLS-1$
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
				if(fCurrentSelction != null)
				{
					if(MessageDialog.openConfirm(getShell(),Mevenide.getResourceString("TemplatePreferencePage.del.confirm.title"),Mevenide.getResourceString("TemplatePreferencePage.del.confirm.message") + " " + fCurrentSelction.getTemplateName())) //$NON-NLS-1$
					{
						fCurrentSelction.getProject().getFile().delete();
						fTemplates.removeTemplate(fCurrentSelction);
						fCurrentSelction = null;
					}
				}
			}
		});
		return composite;
	}
	/**
	 *  
	 */
	private boolean copyTemplateToPreferences(IPath source) {
		try {
			ProjectReader reader = ProjectReader.getReader();
			Project pom = reader.read(source.toFile());

			IPath dest = Mevenide.getInstance().getStateLocation().append("templates");//$NON-NLS-1$

			//If it's the first template we are going to add then create the
			//template folder
			if (!dest.toFile().exists())
				dest.toFile().mkdir();
			dest = dest.append(pom.toString() + pom.hashCode() + ".tmpl");//$NON-NLS-1$

			FileUtils.copyFile(source.toFile(), dest.toFile());
			pom = reader.read(dest.toFile());
			fTemplates.addTemplate(new Template(pom));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}