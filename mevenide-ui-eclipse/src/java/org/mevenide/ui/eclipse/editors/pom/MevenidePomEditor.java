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
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
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
import org.mevenide.project.ProjectComparator;
import org.mevenide.project.ProjectComparatorFactory;
import org.mevenide.project.io.DefaultProjectMarshaller;
import org.mevenide.project.io.IProjectMarshaller;
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
import org.mevenide.util.DefaultProjectUnmarshaller;
import org.mevenide.util.StringUtils;

/**
 * The Mevenide multi-page POM editor. This editor presents the user with pages
 * partitioning up the various parts of the POM, plus an XML source editor and
 * content outline, with synchronization between all parts.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class MevenidePomEditor extends FormEditor {

    private static final Log log = LogFactory.getLog(MevenidePomEditor.class);

    private Project pom;
    private Project parentPom;
    private IProjectMarshaller marshaller;
    private DefaultProjectUnmarshaller unmarshaller;
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
                log.debug("elementContentAboutToBeReplaced: " + element);
            }
        }

        public void elementContentReplaced(Object element) {
            if (log.isDebugEnabled()) {
                log.debug("elementContentReplaced: " + element);
            }
            updateModel();
        }

        public void elementDeleted(Object element) {
            if (log.isDebugEnabled()) {
                log.debug("elementDeleted: " + element);
            }
        }

        public void elementDirtyStateChanged(Object element, boolean isDirty) {
            if (log.isDebugEnabled()) {
                log.debug("elementDirtyStateChanged to " + isDirty);
            }
        }

        public void elementMoved(Object originalElement, Object movedElement) {
            if (log.isDebugEnabled()) {
                log.debug("elementMoved");
            }
            close(true);
        }
    }

    public MevenidePomEditor() {
        super();
        try {
            marshaller = new DefaultProjectMarshaller();
            unmarshaller = new DefaultProjectUnmarshaller();
        } catch (Exception e) {
            log.error("Could not create a POM marshaller", e);
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
            log.error("Unable to create source page", e);
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
    }

    protected void pageChange(int newPageIndex) {
        if (log.isDebugEnabled()) {
            log.debug("changing page: " + getActivePage() + " => " + newPageIndex);
        }
//        IPomEditorPage oldPage = getCurrentPomEditorPage();
//        IPomEditorPage newPage = getPomEditorPage(newPageIndex);
//        if (oldPage != null && newPage != null) {
//            oldPage.pageDeactivated(newPage);
//            newPage.pageActivated(oldPage);
//            if (newPage.isPropertySourceSupplier()) {
//                openPropertiesSheet();
//            }
//        }

        super.pageChange(newPageIndex);
        log.debug("changed page");
    }

    private void openPropertiesSheet() {
        try {
            getSite().getPage().showView("org.eclipse.ui.views.PropertySheet");
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
            log.debug("attempting save...");
        }
        updateDocument();

        final IEditorInput input = getEditorInput();
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

            public void execute(final IProgressMonitor mon) throws CoreException {

                if (log.isDebugEnabled()) {
                    log.debug("saving documentProvider");
                }
                documentProvider.saveDocument(mon, input, documentProvider.getDocument(input), true);
            }
        };

        try {
            updateModel();
            documentProvider.aboutToChange(input);
            op.run(monitor);
            documentProvider.changed(input);
            updateTitleAndToolTip();
            setModelDirty(false);
        } catch (InterruptedException x) {
        } catch (InvocationTargetException x) {
        }
        if (log.isDebugEnabled()) {
            log.debug("saved!");
            log.debug("dirty = " + isDirty());
            log.debug("modeldirty = " + isModelDirty());
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
            setTitle(pom.getName());
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
            log.debug("modelDirty = " + isModelDirty() + " and editor dirty " + dirtiness);
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
        if (!(editorInput instanceof IFileEditorInput)) { throw new PartInitException(
                "Invalid Input: Must be IFileEditorInput"); }
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
    }

    private void createModel(IFile pomFile) throws CoreException {
        try {
            File file = pomFile.getRawLocation().toFile();
            ProjectReader reader = ProjectReader.getReader();
            pom = reader.read(file);

            if (pom.getExtend() != null && !"".equals(pom.getExtend().trim())) {
                String resolvedExtend = new ProjectWalker(pom).resolve(pom.getExtend());
                File extendFile = new File(resolvedExtend);
                if (log.isDebugEnabled()) {
                    log.debug("parentPom path = " + resolvedExtend + "; exists = " + extendFile.exists());
                }

                if (!extendFile.exists()) {
                    // not an absolute path; must've been relative
                    extendFile = new File(new File(pomFile.getLocation().toOSString()).getParentFile(), resolvedExtend);
                    if (log.isDebugEnabled()) {
                        log.debug("parentPom path = " + extendFile.getAbsolutePath() + "; exists = "
                                + extendFile.exists());
                    }
                }

                if (extendFile.exists()) {
                    parentPom = reader.read(extendFile);
                }
            }

            updateTitleAndToolTip();
        } catch (Exception e) {
            log.error("could not read POM: ", e);
            throw new PartInitException("Could not obtain Project reader");
        }
    }

    public boolean updateModel() {
        if (log.isDebugEnabled()) {
            log.debug("updateModel entered");
        }
        boolean clean = false;
        IDocument document = documentProvider.getDocument(getEditorInput());
        StringReader reader = new StringReader(document.get());
        Project updatedPom = null;
        try {
            updatedPom = unmarshaller.parse(reader);

            if (log.isDebugEnabled()) {
                log.debug("old pom name = " + pom.getName() + " and new = " + updatedPom.getName());
            }
            comparator.compare(updatedPom);

            String pomName = pom.getName();
            if (!StringUtils.isNull(pomName)) {
                setTitle(pomName);
                firePropertyChange(PROP_TITLE);
            }
            setModelDirty(false);
            
            clean = true;
        } catch (Exception e) {
            log.error("Unable to update model", e);
            clean = false;
        }
        if (log.isDebugEnabled()) {
            log.debug("updateModel exiting");
        }
        return clean;
    }

    public void updateDocument() {
        if (log.isDebugEnabled()) {
            log.debug("updateDocument entered; modeldirty = " + isModelDirty());
        }
        if (isModelDirty()) {
            IDocument document = documentProvider.getDocument(getEditorInput());
            StringWriter newDocument = new StringWriter();
            PrintWriter writer = new PrintWriter(newDocument);
            try {
                marshaller.marshall(writer, pom);
                writer.flush();
                document.set(newDocument.toString());
                setModelDirty(false);
                if (log.isDebugEnabled()) {
                    log.debug("current project name = " + pom.getName() + " and extends = " + pom.getExtend());
                }
            } catch (Exception e) {
                log.error("Marshalling POM failed", e);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("updateDocument exiting");
        }
    }

    public Object getAdapter(Class adapter) {
        if (log.isDebugEnabled()) {
            log.debug("getting adapter for class: " + adapter);
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

}
