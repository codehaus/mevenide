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
package org.mevenide.ui.eclipse.editors.pom;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.context.IQueryContext;
import org.mevenide.project.IProjectChangeListener;
import org.mevenide.project.ProjectChangeEvent;
import org.mevenide.project.ProjectComparator;
import org.mevenide.project.ProjectComparatorFactory;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.project.io.IProjectMarshaller;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.pages.BuildPage;
import org.mevenide.ui.eclipse.editors.pom.pages.DependenciesPage;
import org.mevenide.ui.eclipse.editors.pom.pages.DescriptionPage;
import org.mevenide.ui.eclipse.editors.pom.pages.OrganizationPage;
import org.mevenide.ui.eclipse.editors.pom.pages.OverviewPage;
import org.mevenide.ui.eclipse.editors.pom.pages.ReportsPage;
import org.mevenide.ui.eclipse.editors.pom.pages.RepositoryPage;
import org.mevenide.ui.eclipse.editors.pom.pages.TeamPage;
import org.mevenide.ui.eclipse.editors.pom.pages.UnitTestsPage;
import org.mevenide.ui.eclipse.util.Tracer;
import org.mevenide.util.ProjectUtils;
import org.mevenide.util.StringUtils;

/**
 * The Mevenide multi-page POM editor. This editor presents the user with pages
 * partitioning up the various parts of the POM, plus an XML source editor and
 * content outline, with synchronization between all parts.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class MevenidePomEditor extends FormEditor implements IProjectChangeListener {

    private static final String PROPERTY_SHEET_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

    private Project pom;
    private Project parentPom;
    private IProjectMarshaller marshaller;
    private ProjectComparator comparator;
    private PomEditorSelectionProvider selectionProvider = new PomEditorSelectionProvider(this);
    private IDocumentProvider documentProvider;
    private ElementListener elementListener;
    private PomXmlSourcePage sourcePage;

    private boolean modelDirty;
    private int overviewPageIndex;

    private PomContentOutlinePage outline;

    class ElementListener implements IElementStateListener {
        public void elementContentAboutToBeReplaced(Object element) {
            if (Tracer.isDebugging()) {
                Tracer.trace("elementContentAboutToBeReplaced: " + element); //$NON-NLS-1$
            }
        }

        public void elementContentReplaced(Object element) {
            if (Tracer.isDebugging()) {
                Tracer.trace("elementContentReplaced: " + element); //$NON-NLS-1$
            }
            updateModel();
        }

        public void elementDeleted(Object element) {
            if (Tracer.isDebugging()) {
                Tracer.trace("elementDeleted: " + element); //$NON-NLS-1$
            }
        }

        public void elementDirtyStateChanged(Object element, boolean isDirty) {
            if (Tracer.isDebugging()) {
                Tracer.trace("elementDirtyStateChanged to " + isDirty); //$NON-NLS-1$
            }
        }

        public void elementMoved(Object originalElement, Object movedElement) {
            if (Tracer.isDebugging()) {
                Tracer.trace("elementMoved"); //$NON-NLS-1$
            }
            close(true);
        }
    }

    public MevenidePomEditor() {
        super();
        try {
            marshaller = new CarefulProjectMarshaller();
        } catch (Exception e) {
            final String msg = "Could not create a POM marshaller"; //$NON-NLS-1$
            Mevenide.displayError(msg, e);
        }
    }

    public Project getPom() {
        return pom;
    }

    public Project getParentPom() {
        return parentPom;
    }

    public IProject getProject() {
        return ((IResource) getEditorInput().getAdapter(IResource.class)).getProject();
    }

    /**
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
            this.overviewPageIndex = addPage(new OverviewPage(this));
            addPage(new DescriptionPage(this));
            addPage(new OrganizationPage(this));
            addPage(new RepositoryPage(this));
            addPage(new TeamPage(this));
            addPage(new DependenciesPage(this));
            addPage(new BuildPage(this));
            addPage(new UnitTestsPage(this));
            addPage(new ReportsPage(this));
            this.sourcePage = new PomXmlSourcePage(this);
            addPage(this.sourcePage, sourcePage.getEditorInput());
        } catch (PartInitException e) {
            final String msg = "Unable to create source page"; //$NON-NLS-1$
            Mevenide.displayError(msg, e);
        }
    }

    protected void pageChange(int newPageIndex) {
        if (Tracer.isDebugging()) {
            Tracer.trace("changing page: " + getActivePage() + " => " + newPageIndex); //$NON-NLS-1$ //$NON-NLS-2$
        }
        IPomEditorPage oldPage = getCurrentPomEditorPage();
        IPomEditorPage newPage = getPomEditorPage(newPageIndex);
        if (oldPage != null && newPage != null) {
            oldPage.pageDeactivated(newPage);
            newPage.pageActivated(oldPage);
            if (newPage.isPropertySourceSupplier()) {
                openPropertiesSheet();
            }
        }

        super.pageChange(newPageIndex);
        Tracer.trace("changed page"); //$NON-NLS-1$
    }

    private void openPropertiesSheet() {
        try {
            getSite().getPage().showView(PROPERTY_SHEET_ID); 
        } catch (PartInitException e) {
            Mevenide.displayError(e.getLocalizedMessage(), e);
        }
    }

    private IPomEditorPage getCurrentPomEditorPage() {
        return getPomEditorPage(getCurrentPage());
    }

    private IPomEditorPage getPomEditorPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < getPageCount()) {
            return (IPomEditorPage) getEditor(pageIndex);
        }
        return null;
    }

    /**
     * Saves the multi-page editor's document.
     */
    public void doSave(IProgressMonitor monitor) {

        if (Tracer.isDebugging()) {
            Tracer.trace("attempting save..."); //$NON-NLS-1$
        }
        
        updateDocument();
        
        final IEditorInput input = getEditorInput();
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

            public void execute(final IProgressMonitor mon) throws CoreException {

                if (Tracer.isDebugging()) {
                    Tracer.trace("saving documentProvider"); //$NON-NLS-1$
                }
                documentProvider.saveDocument(mon, input, documentProvider.getDocument(input), true);
            }
        };

        try {
            documentProvider.aboutToChange(input);
            op.run(monitor);
            documentProvider.changed(input);
            Mevenide.getInstance().getPOMManager().forceUpdate(((IFileEditorInput) getEditorInput()).getFile().getProject());
            updateModel();
            updateTitleAndToolTip();
            setModelDirty(false);
            
        } catch (InterruptedException x) {
        } catch (InvocationTargetException x) {
        }
        if (Tracer.isDebugging()) {
            Tracer.trace("saved!"); //$NON-NLS-1$
            Tracer.trace("dirty = " + isDirty()); //$NON-NLS-1$
            Tracer.trace("modeldirty = " + isModelDirty()); //$NON-NLS-1$
        }
    }

    /**
     * Saves the multi-page editor's document as another file. Also updates the
     * text for page 0's tab, and updates this multi-page editor's input to
     * correspond to the nested editor's.
     */
    public void doSaveAs() {
        sourcePage.doSaveAs();
        setPageText(this.overviewPageIndex, sourcePage.getTitle());
        setInput(sourcePage.getEditorInput());
        updateTitleAndToolTip();
        setModelDirty(false);
    }

    private void updateTitleAndToolTip() {
        if (!StringUtils.isNull(pom.getName())) {
            setPartName(pom.getName());
        }
        IFile pomFile = ((IFileEditorInput) getEditorInput()).getFile();
        setTitleToolTip(pomFile.getProject().getName() + pomFile.getProjectRelativePath());
        fireTitleChanged();
    }

    public boolean isSaveAsAllowed() {
        return true;
    }

    public boolean isDirty() {
        boolean dirtiness = isModelDirty() 
        					|| (documentProvider != null && documentProvider.canSaveDocument(getEditorInput()));
        if (Tracer.isDebugging()) {
            Tracer.trace("modelDirty = " + isModelDirty() + " and editor dirty " + dirtiness); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return dirtiness;
    }

    private boolean isModelDirty() {
        return modelDirty;
    }

    public void setModelDirty(boolean dirty) {
        if (this.modelDirty != dirty) {
            this.modelDirty = dirty;
            fireDirtyStateChanged();
        }
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>.
     */
    public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
        if (!(editorInput instanceof IFileEditorInput)) { 
            throw new PartInitException("Invalid Input: Must be IFileEditorInput");  //$NON-NLS-1$
        }
        IFile pomFile = ((IFileEditorInput) editorInput).getFile();

        setInput(editorInput);
        setSite(site);

        site.setSelectionProvider(selectionProvider);

        try {
            initializeModel(pomFile);
        } catch (CoreException e) {
            throw new PartInitException(e.getStatus());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        super.dispose();

        this.comparator.removeProjectChangeListener(this);

        this.documentProvider.removeElementStateListener(this.elementListener);

        IEditorInput editorInput = getEditorInput();
        IAnnotationModel annotModel = this.documentProvider.getAnnotationModel(editorInput);
        if (annotModel != null) {
            annotModel.disconnect(this.documentProvider.getDocument(editorInput));
        }

        this.documentProvider.disconnect(editorInput);
    }

    private void initializeModel(IFile pomFile) throws CoreException {
        IQueryContext queryContext = Mevenide.getInstance().getPOMManager().getQueryContext(pomFile.getProject());
        if (queryContext != null) {
            Project[] project = queryContext.getPOMContext().getProjectLayers();
            pom = project[0];
            if (project.length > 1) {
                parentPom = project[1];
            }
        } else {
            IStatus status = new Status(IStatus.ERROR, Mevenide.PLUGIN_ID, 0, "Unable to locate the Maven POM.", null);
            throw new CoreException(status);
        }

        updateTitleAndToolTip();
        documentProvider = new PomXmlDocumentProvider();
        comparator = ProjectComparatorFactory.getComparator(pom);
        
        IEditorInput editorInput = getEditorInput();
        documentProvider.connect(editorInput);
        IAnnotationModel annotModel = documentProvider.getAnnotationModel(editorInput);
        if (annotModel != null) {
            annotModel.connect(documentProvider.getDocument(editorInput));
        }
        elementListener = new ElementListener();
        documentProvider.addElementStateListener(elementListener);
        
        comparator.addProjectChangeListener(this);
    }
    
    
    protected boolean updateModel() {
        if (Tracer.isDebugging()) {
            Tracer.trace("updateModel entered"); //$NON-NLS-1$
        }
        
        boolean clean = false;
	    
        Project updatedPom = null;
        try {
            IEditorInput editorInput = getEditorInput();
            IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
            IFile eclipseFile = fileEditorInput.getFile();
            IQueryContext queryContext = Mevenide.getInstance().getPOMManager().getQueryContext(eclipseFile.getProject());
            if (queryContext != null) {
                Project[] project = queryContext.getPOMContext().getProjectLayers();
                updatedPom = project[0];
            }
            
            if (Tracer.isDebugging()) {
                Tracer.trace("old pom name = " + pom.getName() + " and new = " + updatedPom.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            comparator.compare(updatedPom);
            
            updateTitleAndToolTip();
            
            setModelDirty(false);
            
            clean = true;
        } catch (Exception e) {
            final String msg = "Unable to update model"; //$NON-NLS-1$
            Mevenide.displayError(msg, e);
            clean = false;
        }
        if (Tracer.isDebugging()) {
            Tracer.trace("updateModel exiting"); //$NON-NLS-1$
        }
        return clean;
    }
    

	public void updateDocument() {
        if (Tracer.isDebugging()) {
            Tracer.trace("updateDocument entered; modeldirty = " + isModelDirty()); //$NON-NLS-1$
        }
        if (isModelDirty()) {
            IDocument document = documentProvider.getDocument(getEditorInput());
            StringWriter newDocument = new StringWriter();
            PrintWriter writer = new PrintWriter(newDocument);
            try {
                FileInputStream stream = new FileInputStream(((IFileEditorInput) getEditorInput()).getFile().getRawLocation().toFile());
                ((CarefulProjectMarshaller) marshaller).marshall(writer, pom, stream);
                writer.flush();
                document.set(newDocument.toString());
                
                setModelDirty(false);
                if (Tracer.isDebugging()) {
                    Tracer.trace("current project name = " + pom.getName() + " and extends = " + pom.getExtend()); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } catch (Exception e) {
                final String msg = "Marshalling POM failed"; //$NON-NLS-1$
                Mevenide.displayError(msg, e);
            }
        }
        if (Tracer.isDebugging()) {
            Tracer.trace("updateDocument exiting"); //$NON-NLS-1$
        }
    }

    public Object getAdapter(Class adapter) {
        if (Tracer.isDebugging()) {
            Tracer.trace("getting adapter for class: " + adapter); //$NON-NLS-1$
        }

        if (ProjectComparator.class.equals(adapter)) { return this.comparator; }
        if (IContentOutlinePage.class.equals(adapter)) { return getContentOutline(); }
        return super.getAdapter(adapter);
    }

    private Object getContentOutline() {
        if (outline == null) {
            outline = new PomContentOutlinePage(this); //getDocumentProvider(), this);
            if (getEditorInput() != null) {
                outline.setModel(getPom());
            }
        }
        return outline;
    }

    protected IDocumentProvider getDocumentProvider() {
        return documentProvider;
    }

    private void fireDirtyStateChanged() {
        firePropertyChange(PROP_DIRTY);
    }

    private void fireTitleChanged() {
        firePropertyChange(PROP_TITLE);
    }

    public void close(final boolean save) {
        Display display = getSite().getShell().getDisplay();

        display.asyncExec(new Runnable() {

            public void run() {
                getSite().getPage().closeEditor(MevenidePomEditor.this, save);
            }
        });
    }

    public void setPropertySourceSelection(ISelection selection) {
        selectionProvider.setSelection(selection);
    }

    
    //this is crap but it will allow us to move forward (fix editor pages synchro)
    public void projectChanged(ProjectChangeEvent e) {
        updatePom(e.getPom());
    }
    
    private void updatePom(Project p) {
        pom.setVersions(p.getVersions());
        pom.setUrl(p.getUrl());
        pom.setSiteDirectory(p.getSiteDirectory());
        pom.setSiteAddress(p.getSiteAddress());
        pom.setShortDescription(p.getShortDescription());
        pom.setRepository(p.getRepository());
        pom.setReports(p.getReports());
        pom.setPomVersion(p.getPomVersion());
        pom.setPackage(p.getPackage());
        pom.setParent(p.getParent());
        pom.setOrganization(p.getOrganization());
        pom.setName(p.getName());
        pom.setMailingLists(p.getMailingLists());
        pom.setLogo(p.getLogo());
        pom.setLicenses(p.getLicenses());
        pom.setInceptionYear(p.getInceptionYear());
        pom.setId(p.getId());
        if ( p.getId().indexOf(':') == -1 ) {
            pom.setArtifactId(p.getId());
        }
        else {
            pom.setArtifactId(p.getId().substring(p.getId().indexOf(':') + 1, p.getId().length()));
        }
        pom.setGumpRepositoryId(p.getGumpRepositoryId());
        //pom.setGroupId(p.getGroupId());
        pom.setGroupId(ProjectUtils.parseGroupId(new File(((FileEditorInput) getEditorInput()).getFile().getLocation().toOSString())));
        pom.setExtend(p.getExtend());
        pom.setDistributionSite(p.getDistributionSite());
        pom.setDistributionDirectory(p.getDistributionDirectory());
        pom.setCurrentVersion(p.getCurrentVersion());
        pom.setDevelopers(p.getDevelopers());
        pom.setDescription(p.getDescription());
        pom.setDependencies(p.getDependencies());
        pom.setContributors(p.getContributors());
        pom.setBuild(p.getBuild());
        pom.setBranches(p.getBranches());
        pom.setArtifacts(p.getArtifacts());
    }

	
    
}
