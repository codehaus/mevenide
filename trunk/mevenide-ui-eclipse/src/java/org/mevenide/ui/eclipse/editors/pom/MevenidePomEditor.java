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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.apache.maven.util.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.mevenide.project.IProjectChangeListener;
import org.mevenide.project.ProjectChangeEvent;
import org.mevenide.project.ProjectComparator;
import org.mevenide.project.ProjectComparatorFactory;
import org.mevenide.project.io.CarefulProjectMarshaller;
import org.mevenide.project.io.IProjectMarshaller;
import org.mevenide.context.JDomProjectUnmarshaller;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.properties.resolver.ProjectWalker;
import org.mevenide.ui.eclipse.editors.pom.pages.BuildPage;
import org.mevenide.ui.eclipse.editors.pom.pages.DependenciesPage;
import org.mevenide.ui.eclipse.editors.pom.pages.OrganizationPage;
import org.mevenide.ui.eclipse.editors.pom.pages.OverviewPage;
import org.mevenide.ui.eclipse.editors.pom.pages.ReportsPage;
import org.mevenide.ui.eclipse.editors.pom.pages.RepositoryPage;
import org.mevenide.ui.eclipse.editors.pom.pages.TeamPage;
import org.mevenide.ui.eclipse.editors.pom.pages.UnitTestsPage;
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

    private static final Log log = LogFactory.getLog(MevenidePomEditor.class);
	
    private static final String PROPERTY_SHEET_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

    private Project pom;
    private Project parentPom;
    private IProjectMarshaller marshaller;
    private JDomProjectUnmarshaller unmarshaller;
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
            if (log.isDebugEnabled()) {
                log.debug("elementContentAboutToBeReplaced: " + element); //$NON-NLS-1$
            }
        }

        public void elementContentReplaced(Object element) {
            if (log.isDebugEnabled()) {
                log.debug("elementContentReplaced: " + element); //$NON-NLS-1$
            }
            updateModel();
        }

        public void elementDeleted(Object element) {
            if (log.isDebugEnabled()) {
                log.debug("elementDeleted: " + element); //$NON-NLS-1$
            }
        }

        public void elementDirtyStateChanged(Object element, boolean isDirty) {
            if (log.isDebugEnabled()) {
                log.debug("elementDirtyStateChanged to " + isDirty); //$NON-NLS-1$
            }
        }

        public void elementMoved(Object originalElement, Object movedElement) {
            if (log.isDebugEnabled()) {
                log.debug("elementMoved"); //$NON-NLS-1$
            }
            close(true);
        }
    }

    public MevenidePomEditor() {
        super();
        try {
            marshaller = new CarefulProjectMarshaller();
            unmarshaller = new JDomProjectUnmarshaller();
        } catch (Exception e) {
            log.error("Could not create a POM marshaller", e); //$NON-NLS-1$
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
            createOverviewPage();
            createOrganizationPage();
            createRepositoryPage();
            createTeamPage();
            createDependenciesPage();
            createBuildPage();
            createUnitTestsPage();
            createReportsPage();
            createSourcePage();
        } catch (PartInitException e) {
            log.error("Unable to create source page", e); //$NON-NLS-1$
        }
    }

    /**
     * Creates the overview page of the Project Object Model editor, where
     * basic information about the project is defined.
     */
    private void createOverviewPage() throws PartInitException {
        OverviewPage overviewPage = new OverviewPage(this);
        comparator.addProjectChangeListener(ProjectComparator.PROJECT, overviewPage);
        overviewPageIndex = addPage(overviewPage);
        addPropertyListener(overviewPage);
    }

    /**
     * Creates the organization-specific, licensing, and site generation
     * information in the project..
     */
    private void createOrganizationPage() throws PartInitException {
        OrganizationPage orgPage = new OrganizationPage(this);
        comparator.addProjectChangeListener(ProjectComparator.PROJECT, orgPage);
        comparator.addProjectChangeListener(ProjectComparator.ORGANIZATION, orgPage);
        comparator.addProjectChangeListener(ProjectComparator.LICENSES, orgPage);
        addPage(orgPage);
    }

    /**
     * Creates the version control page of the Project Object Model editor,
     * where properties of the project repository are defined.
     */
    private void createRepositoryPage() throws PartInitException {
        RepositoryPage repoPage = new RepositoryPage(this);
        comparator.addProjectChangeListener(ProjectComparator.REPOSITORY, repoPage);
        comparator.addProjectChangeListener(ProjectComparator.VERSIONS, repoPage);
        comparator.addProjectChangeListener(ProjectComparator.BRANCHES, repoPage);
        addPage(repoPage);
    }

    /**
     * Creates the version control page of the Project Object Model editor,
     * where is defined.
     */
    private void createTeamPage() throws PartInitException {
        TeamPage teamPage = new TeamPage(this);
        comparator.addProjectChangeListener(ProjectComparator.CONTRIBUTORS, teamPage);
        comparator.addProjectChangeListener(ProjectComparator.DEVELOPERS, teamPage);
        comparator.addProjectChangeListener(ProjectComparator.MAILINGLISTS, teamPage);
        addPage(teamPage);
    }

    /**
     * Creates the version control page of the Project Object Model editor,
     * where is defined.
     */
    private void createDependenciesPage() throws PartInitException {
        DependenciesPage depsPage = new DependenciesPage(this);
        comparator.addProjectChangeListener(ProjectComparator.DEPENDENCIES, depsPage);
        addPage(depsPage);
    }

    /**
     * Creates the version control page of the Project Object Model editor,
     * where is defined.
     */
    private void createBuildPage() throws PartInitException {
        BuildPage buildPage = new BuildPage(this);
        comparator.addProjectChangeListener(ProjectComparator.BUILD, buildPage);
        comparator.addProjectChangeListener(ProjectComparator.RESOURCES, buildPage);
        addPage(buildPage);
    }

    /**
     * Creates the version control page of the Project Object Model editor,
     * where is defined.
     */
    private void createUnitTestsPage() throws PartInitException {
        UnitTestsPage testsPage = new UnitTestsPage(this);
        comparator.addProjectChangeListener(ProjectComparator.UNIT_TESTS, testsPage);
        addPage(testsPage);
    }

    /**
     * Creates the version control page of the Project Object Model editor,
     * where is defined.
     */
    private void createReportsPage() throws PartInitException {
        ReportsPage reportsPage = new ReportsPage(this);
        comparator.addProjectChangeListener(ProjectComparator.REPORTS, reportsPage);
        addPage(reportsPage);
    }

    /**
     * Creates the source view page of the Project Object Model editor, where
     * the raw XML is displayed and edited.
     */
    private void createSourcePage() throws PartInitException {
        sourcePage = new PomXmlSourcePage(this);
        addPage(sourcePage, sourcePage.getEditorInput());
        comparator.addProjectChangeListener(sourcePage);
    }

    protected void pageChange(int newPageIndex) {
        if (log.isDebugEnabled()) {
            log.debug("changing page: " + getActivePage() + " => " + newPageIndex); //$NON-NLS-1$ //$NON-NLS-2$
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
        log.debug("changed page"); //$NON-NLS-1$
    }

    private void openPropertiesSheet() {
        try {
            getSite().getPage().showView(PROPERTY_SHEET_ID); 
        } catch (PartInitException e) {
            log.error(e);
        }
    }

    public IPomEditorPage getCurrentPomEditorPage() {
        return getPomEditorPage(getCurrentPage());
    }

    public IPomEditorPage getPomEditorPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < getPageCount()) {
            return (IPomEditorPage) getEditor(pageIndex);
        }
        return null;
    }

    /**
     * Saves the multi-page editor's document.
     */
    public void doSave(IProgressMonitor monitor) {

        if (log.isDebugEnabled()) {
            log.debug("attempting save..."); //$NON-NLS-1$
        }
        
        updateDocument();
        
        final IEditorInput input = getEditorInput();
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

            public void execute(final IProgressMonitor mon) throws CoreException {

                if (log.isDebugEnabled()) {
                    log.debug("saving documentProvider"); //$NON-NLS-1$
                }
                documentProvider.saveDocument(mon, input, documentProvider.getDocument(input), true);
            }
        };

        try {
            documentProvider.aboutToChange(input);
            op.run(monitor);
            documentProvider.changed(input);
            updateModel();
            updateTitleAndToolTip();
            setModelDirty(false);
            
        } catch (InterruptedException x) {
        } catch (InvocationTargetException x) {
        }
        if (log.isDebugEnabled()) {
            log.debug("saved!"); //$NON-NLS-1$
            log.debug("dirty = " + isDirty()); //$NON-NLS-1$
            log.debug("modeldirty = " + isModelDirty()); //$NON-NLS-1$
        }
    }

    /**
     * Saves the multi-page editor's document as another file. Also updates the
     * text for page 0's tab, and updates this multi-page editor's input to
     * correspond to the nested editor's.
     */
    public void doSaveAs() {
        sourcePage.doSaveAs();
        setPageText(overviewPageIndex, sourcePage.getTitle());
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
        if (log.isDebugEnabled()) {
            log.debug("modelDirty = " + isModelDirty() + " and editor dirty " + dirtiness); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return dirtiness;
    }

    public boolean isModelDirty() {
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
        } 
        catch (CoreException e) {
            throw new PartInitException(e.getStatus());
        }
    }

    private void initializeModel(IFile pomFile) throws CoreException {
        documentProvider = new PomXmlDocumentProvider();
        createModel(pomFile);
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
    
    
    private void createModel(IFile pomFile) throws CoreException {
        try {
            File file = pomFile.getRawLocation().toFile();
            ProjectReader reader = ProjectReader.getReader();
            pom = reader.read(file);

            if (pom.getExtend() != null && !"".equals(pom.getExtend().trim())) { //$NON-NLS-1$
                String resolvedExtend = new ProjectWalker(pom).resolve(pom.getExtend());
                File extendFile = new File(resolvedExtend);
                if (log.isDebugEnabled()) {
                    log.debug("parentPom path = " + resolvedExtend + "; exists = " + extendFile.exists()); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (!extendFile.exists()) {
                    // not an absolute path; must've been relative
                    extendFile = new File(new File(pomFile.getLocation().toOSString()).getParentFile(), resolvedExtend);
                    if (log.isDebugEnabled()) {
                        log.debug("parentPom path = " + extendFile.getAbsolutePath() + "; exists = " //$NON-NLS-1$ //$NON-NLS-2$
                                + extendFile.exists());
                    }
                }

                if (extendFile.exists()) {
                    parentPom = reader.read(extendFile);
                }
            }

            updateTitleAndToolTip();
        } catch (Exception e) {
            log.error("could not read POM: ", e); //$NON-NLS-1$
            throw new PartInitException("Could not obtain Project reader"); //$NON-NLS-1$
        }
    }

    public boolean updateModel() {
        if (log.isDebugEnabled()) {
            log.debug("updateModel entered"); //$NON-NLS-1$
        }
        
        boolean clean = false;
	    
        IDocument document = documentProvider.getDocument(getEditorInput());
        InputStream is = new StringInputStream(document.get());
        Project updatedPom = null;
        try {
            updatedPom = unmarshaller.parse(((IFileEditorInput) getEditorInput()).getFile().getRawLocation().toFile());

            
            if (log.isDebugEnabled()) {
                log.debug("old pom name = " + pom.getName() + " and new = " + updatedPom.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            comparator.compare(updatedPom);
            
            updateTitleAndToolTip();
            
            setModelDirty(false);
            
            clean = true;
        } catch (Exception e) {
            log.error("Unable to update model", e); //$NON-NLS-1$
            clean = false;
        }
        if (log.isDebugEnabled()) {
            log.debug("updateModel exiting"); //$NON-NLS-1$
        }
        return clean;
    }
    

	public void updateDocument() {
        if (log.isDebugEnabled()) {
            log.debug("updateDocument entered; modeldirty = " + isModelDirty()); //$NON-NLS-1$
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
                if (log.isDebugEnabled()) {
                    log.debug("current project name = " + pom.getName() + " and extends = " + pom.getExtend()); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } catch (Exception e) {
                log.error("Marshalling POM failed", e); //$NON-NLS-1$
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("updateDocument exiting"); //$NON-NLS-1$
        }
    }

    public Object getAdapter(Class adapter) {
        if (log.isDebugEnabled()) {
            log.debug("getting adapter for class: " + adapter); //$NON-NLS-1$
        }

        if (IContentOutlinePage.class.equals(adapter)) { return getContentOutline(); }
        return super.getAdapter(adapter);
    }

    private Object getContentOutline() {
        if (outline == null) {
            outline = new PomContentOutlinePage(getDocumentProvider(), this);
            if (getEditorInput() != null) {
                outline.setInput(getEditorInput());
            }
        }
        return outline;
    }

    public IDocumentProvider getDocumentProvider() {
        return documentProvider;
    }

    public MevenidePomEditorContributor getContributor() {
        return (MevenidePomEditorContributor) getEditorSite().getActionBarContributor();
    }

    //    public void fireSaveNeeded() {
    //        firePropertyChange(PROP_DIRTY);
    //        if (log.isDebugEnabled()) {
    //            log.debug("fireSaveNeeded");
    //        }
    //        // MevenidePomEditorContributor contributor = getContributor();
    //        // if (contributor != null) {
    //        // contributor.updateActions();
    //        // }
    //    }

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
        pom.setGroupId(ProjectUtils.getGroupId(p));
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
