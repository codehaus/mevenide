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
package org.mevenide.ui.eclipse.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * The build wizard page for a maven project. This include source code location (including
 * includes/excludes), unit test location (including includes/excludes), aspects and the 
 * nag email address. 
 *   
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated by $Author$
 * @version $Id$
 */
public class MavenProjectWizardDependencySettingsPage extends WizardPage {
	/**
	 * Label provider for the dependencies 
	 */
	private final class DependencyLabelProvider extends LabelProvider {
		ImageRegistry imageRegistry= JavaPlugin.getDefault().getImageRegistry();
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object obj) {
			if (obj instanceof Dependency) {
				return ((Dependency)obj).getName();
			}
			return obj.toString();
		}
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj) {
			return imageRegistry.get(JavaPluginImages.IMG_OBJS_LIBRARY);
		}
	}
	
	private final class DependencyContentProvider implements ITreeContentProvider, Observer {
	    private Object[] EMPTY_ARRAY = new Object[0];
	    private Viewer fViewer;
	    /*
	     * (non-Javadoc)
	     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	     */
	    public boolean hasChildren(Object arg0) {
	        return false;
	    }
	    /*
	     * (non-Javadoc)
	     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	     */
	    public Object[] getChildren(Object obj) {
	        if (obj instanceof Dependencies) {
	            return ((Dependencies) obj).getDependencies();
	        }
	        return EMPTY_ARRAY;
	    }
	    /*
	     * (non-Javadoc)
	     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	     */
	    public Object[] getElements(Object obj) {
	        return getChildren(obj);
	    }
	    /*
	     * (non-Javadoc)
	     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	     */
	    public Object getParent(Object arg0) {
	        return null;
	    }
	    /*
	     * (non-Javadoc)
	     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	     *      java.lang.Object, java.lang.Object)
	     */
	    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	        fViewer = viewer;
	        if (oldInput != null && oldInput instanceof Dependencies)
	            ((Dependencies) oldInput).deleteObserver(this);
	        if (newInput != null && newInput instanceof Dependencies)
	            ((Dependencies) newInput).addObserver(this);
	    }
	    /*
	     * (non-Javadoc)
	     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	     */
	    public void dispose() {
	    }
	    /*
	     * (non-Javadoc)
	     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	     */
	    public void update(Observable o, Object arg) {
	        Display.getDefault().syncExec(new Runnable() {
	            /*
	             * (non-Javadoc)
	             * @see java.lang.Runnable#run()
	             */
	            public void run() {
	                fViewer.refresh();
	            }
	        });
	    }
	}
	
	public final class Dependencies extends Observable implements IAdaptable{

	    private List fDependencies;

	    public Dependencies(List fDependencies) {
	    	this.fDependencies = fDependencies;
	    }
	    /**
	     * Add a dependency
	     * @param template - template to be added
	     */
	    public void addDependency(Dependency dependency) {
	    	fDependencies.add(dependency);
	        setChanged();
	        notifyObservers();
	    }
	    /**
	     * Remove a dependency
	     * @param dependency - dependency to be removed
	     */
	    public void removeDependency(Dependency dependency) {
	    	fDependencies.remove(dependency);
	        setChanged();
	        notifyObservers();
	    }	
	    public Object[] getDependencies()
	    {
	    	return fDependencies.toArray();
	    }

	    public List getDependenciesList()
	    {
	    	return fDependencies;
	    }

	    /* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			return null;
		}
	}
	
	protected static final String PAGE_NAME = Mevenide.getResourceString("MavenProjectWizardDependencySettingsPage.page.pageName"); //$NON-NLS-1$
    
	private Dependencies fDependencies;
    private TreeViewer dependencyViewer;
    private Dependency fCurrentSelection;
    private ISelectionChangedListener fSelectionProvider;
	
	/**
	 * @param pageName
	 */
	public MavenProjectWizardDependencySettingsPage()
	{
		super(PAGE_NAME);
		setPageComplete(true);
		setTitle(Mevenide.getResourceString("MavenProjectWizardDependencySettingsPage.page.title")); //$NON-NLS-1$
		setDescription(Mevenide.getResourceString("MavenProjectWizardDependencySettingsPage.page.description")); //$NON-NLS-1$
		fDependencies = new Dependencies(new ArrayList());
	}
	
    public void createControl(Composite parent) {
    	initializeDialogUnits(parent);
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 2;
        composite.setLayout(layout);
        
        createDependencyViewer(composite);
        createButtonAreaComposite(composite);

        setControl(composite);
    }

    private void createDependencyViewer(Composite parent) {
        GridData gd = new GridData(GridData.FILL_BOTH);
        dependencyViewer = new TreeViewer(parent, SWT.V_SCROLL | SWT.BORDER);
        dependencyViewer.getControl().setLayoutData(gd);
        dependencyViewer.setUseHashlookup(true);
        DependencyContentProvider contentProvider = new DependencyContentProvider();
        
        dependencyViewer.setContentProvider(contentProvider);
        dependencyViewer.setLabelProvider(new DependencyLabelProvider());
        dependencyViewer.setSorter(new ViewerSorter());
		dependencyViewer.setInput(fDependencies);
        fDependencies.addObserver((DependencyContentProvider) dependencyViewer.getContentProvider());
        
        fSelectionProvider = new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                fCurrentSelection = (Dependency) selection.getFirstElement();
            }
        };
        dependencyViewer.addSelectionChangedListener(fSelectionProvider);
    }

    private void createButtonAreaComposite(Composite parent) {
        Composite buttonAreaComposite = new Composite(parent, SWT.NONE);
        GridLayout envButtonLayout = new GridLayout();
        envButtonLayout.marginHeight = 0;
        envButtonLayout.marginWidth = 0;
        buttonAreaComposite.setLayout(envButtonLayout);
        
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
        buttonAreaComposite.setLayoutData(gd);
        
        createAddDependencyButton(buttonAreaComposite);
        createRemoveDependencyButton(buttonAreaComposite);
    }

    private void createRemoveDependencyButton(Composite parent) {
        Button removeTemplateButton = new Button(parent, SWT.PUSH);
        removeTemplateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        removeTemplateButton.setAlignment(SWT.LEFT);
        removeTemplateButton.setText(Mevenide.getResourceString("MavenProjectWizardDependencySettingsPage.dependency.remove"));//$NON-NLS-1$
        removeTemplateButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            	
            }

            public void widgetSelected(SelectionEvent e) {
                if (fCurrentSelection != null) {
                    if (MessageDialog.openConfirm(
                                    getShell(),
                                    Mevenide.getResourceString("MavenProjectWizardDependencySettingsPage.dependency.remove.confirm.title"),  //$NON-NLS-1$
                                    Mevenide.getResourceString("MavenProjectWizardDependencySettingsPage.dependency.remove.confirm.message", fCurrentSelection.getName()))) //$NON-NLS-1$ 
                    {
                    	fDependencies.removeDependency(fCurrentSelection);
                     fCurrentSelection = null;
                    }
                }
            }
        });
    }

    /**
     * Create the add dependency button
     * @param parent
     */
    private void createAddDependencyButton(Composite parent) {
        Button addTemplateButton = new Button(parent, SWT.PUSH);
        addTemplateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addTemplateButton.setAlignment(SWT.LEFT);
        addTemplateButton.setText(Mevenide.getResourceString("MavenProjectWizardDependencySettingsPage.dependency.add"));//$NON-NLS-1$
        addTemplateButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                //do nothing
            }

            public void widgetSelected(SelectionEvent e) {
            	NewDependencyWizard wizard = new NewDependencyWizard(fDependencies);
            	WizardDialog dialog = new WizardDialog(getShell(), wizard);
            	dialog.create();
            	dialog.open();
            }    	
        });
    }

	protected void onEnterPage()
	{
		MavenProjectWizard wizard = (MavenProjectWizard)getWizard();
		Project fProject = wizard.getProjectObjectModel();
		if(fProject.getDependencies() != null)
			fDependencies = new Dependencies(fProject.getDependencies());
		else
			fDependencies = new Dependencies(new ArrayList());
		fDependencies.addObserver((DependencyContentProvider) dependencyViewer.getContentProvider());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		MavenProjectWizard wizard = (MavenProjectWizard)getWizard();
		wizard.getProjectObjectModel().setDependencies(fDependencies.getDependenciesList());
		return super.getNextPage();
	}
}