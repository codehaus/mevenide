/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.editors;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mevenide.project.ProjectComparator;
import org.mevenide.project.io.DefaultProjectMarshaller;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pages.BuildPage;
import org.mevenide.ui.eclipse.editors.pages.DependenciesPage;
import org.mevenide.ui.eclipse.editors.pages.OverviewPage;
import org.mevenide.ui.eclipse.editors.pages.ReportsPage;
import org.mevenide.ui.eclipse.editors.pages.RepositoryPage;
import org.mevenide.ui.eclipse.editors.pages.TeamPage;
import org.mevenide.util.DefaultProjectUnmarshaller;
import org.mevenide.util.MevenideUtils;

/**
 * The Mevenide multi-page POM editor.  This editor presents the user with
 * pages partitioning up the various parts of the POM, plus an XML source editor
 * and content outline, with synchronization between all parts.
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class MevenidePomEditor extends MultiPageEditorPart {

	private static final Log log = LogFactory.getLog(MevenidePomEditor.class);

    public static final String OVERVIEW_PAGE = Mevenide.getResourceString("MevenidePomEditor.tab.label.overview");
    public static final String REPOSITORY_PAGE = Mevenide.getResourceString("MevenidePomEditor.tab.label.repository");
    public static final String TEAM_PAGE = Mevenide.getResourceString("MevenidePomEditor.tab.label.team");
    public static final String DEPENDENCIES_PAGE = Mevenide.getResourceString("MevenidePomEditor.tab.label.dependencies");
    public static final String BUILD_PAGE = Mevenide.getResourceString("MevenidePomEditor.tab.label.build");
    public static final String REPORTS_PAGE = Mevenide.getResourceString("MevenidePomEditor.tab.label.reports");
    public static final String SOURCE_PAGE = Mevenide.getResourceString("MevenidePomEditor.tab.label.source");

    private int overviewPageIndex;
    private int repositoryPageIndex;
    private int teamPageIndex;
    private int dependenciesPageIndex;
    private int buildPageIndex;
    private int reportsPageIndex;
    private int sourcePageIndex;

    private int currentPageIndex;

    private Project pom;
    private Project parentPom;
    private DefaultProjectMarshaller marshaller;
    private DefaultProjectUnmarshaller unmarshaller;
    private ProjectComparator comparator;
    private IDocumentProvider documentProvider;
    private ElementListener elementListener;
    private PomXmlSourcePage sourcePage;
    private boolean modelDirty;

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
        }
        catch (Exception e) {
            log.error("Could not create a POM marshaller", e);
        }
    }

    public Composite getParentContainer() {
        return getContainer();
    }

    public Project getPom() {
        return pom;
    }
    
	public Project getParentPom() {
		return parentPom;
	}

    protected void createPages() {
        createOverviewPage();
        createRepositoryPage();
        createTeamPage();
        createDependenciesPage();
        createBuildPage();
        createReportsPage();
        createSourcePage();

        //TODO: need to track currentPage persistent property between
        // lifespans of editors - store last viewed page on close
        currentPageIndex = overviewPageIndex;
    }

    /**
     * Creates the overview page of the Project Object Model
     * editor, where basic information about the project is defined.
     */
    private void createOverviewPage() {
        OverviewPage overview = new OverviewPage(this);
		comparator.addProjectChangeListener(ProjectComparator.PROJECT, overview);
        overviewPageIndex = addPage(overview);
        setPageText(overviewPageIndex, OVERVIEW_PAGE);
    }

    /**
     * Creates the version control page of the Project Object Model
     * editor, where properties of the project repository are defined.
     */
    private void createRepositoryPage() {
        RepositoryPage repository = new RepositoryPage(this);
		comparator.addProjectChangeListener(ProjectComparator.REPOSITORY, repository);
        repositoryPageIndex = addPage(repository);
        setPageText(repositoryPageIndex, REPOSITORY_PAGE);
    }

    /**
     * Creates the version control page of the Project Object Model
     * editor, where  is defined.
     */
    private void createTeamPage() {
        TeamPage team = new TeamPage(this);
		comparator.addProjectChangeListener(ProjectComparator.CONTRIBUTORS, team);
		comparator.addProjectChangeListener(ProjectComparator.DEVELOPERS, team);
        teamPageIndex = addPage(team);
        setPageText(teamPageIndex, TEAM_PAGE);
    }

    /**
     * Creates the version control page of the Project Object Model
     * editor, where  is defined.
     */
    private void createDependenciesPage() {
        DependenciesPage dependencies = new DependenciesPage(this);
		comparator.addProjectChangeListener(ProjectComparator.DEPENDENCIES, dependencies);
        dependenciesPageIndex = addPage(dependencies);
        setPageText(dependenciesPageIndex, DEPENDENCIES_PAGE);
    }

    /**
     * Creates the version control page of the Project Object Model
     * editor, where  is defined.
     */
    private void createBuildPage() {
        BuildPage build = new BuildPage(this);
		comparator.addProjectChangeListener(ProjectComparator.BUILD, build);
        buildPageIndex = addPage(build);
        setPageText(buildPageIndex, BUILD_PAGE);
    }

    /**
     * Creates the version control page of the Project Object Model
     * editor, where  is defined.
     */
    private void createReportsPage() {
        ReportsPage reports = new ReportsPage(this);
		comparator.addProjectChangeListener(ProjectComparator.REPORTS, reports);
        reportsPageIndex = addPage(reports);
        setPageText(reportsPageIndex, REPORTS_PAGE);
    }

    /**
     * Creates the source view page of the Project Object Model
     * editor, where the raw XML is displayed and edited.
     */
    private void createSourcePage() {
        try {
            sourcePage = new PomXmlSourcePage(this);
            sourcePageIndex = addPage(sourcePage, getEditorInput());
            setPageText(sourcePageIndex, SOURCE_PAGE);
        }
        catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
        }
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
        }
        catch (InterruptedException x) {
        }
        catch (InvocationTargetException x) {
        }
        if (log.isDebugEnabled()) {
            log.debug("saved!");
			log.debug("dirty = " + isDirty());
            log.debug("modeldirty = " + isModelDirty());
        }
    }

    /**
     * Saves the multi-page editor's document as another file.
     * Also updates the text for page 0's tab, and updates this multi-page editor's input
     * to correspond to the nested editor's.
     */
    public void doSaveAs() {
        sourcePage.doSaveAs();
        setPageText(overviewPageIndex, sourcePage.getTitle());
        setInput(sourcePage.getEditorInput());
		updateTitleAndToolTip();
        setModelDirty(false);
    }

    private void updateTitleAndToolTip() {
        if (!MevenideUtils.isNull(pom.getName())) {
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
    	boolean dirtiness = isModelDirty() || 
			(documentProvider != null && 
			 documentProvider.canSaveDocument(getEditorInput()));
        if (log.isDebugEnabled()) {
            log.debug("modelDirty = " + isModelDirty() + " and editor dirty " + dirtiness);
        }
        return dirtiness;
    }

    public boolean isModelDirty() {
        return modelDirty;
    }

    public void setModelDirty(boolean modelDirty) {
    	if (this.modelDirty != modelDirty) {
			this.modelDirty = modelDirty;
    		fireDirtyStateChanged();
    	}
    }

    /**
     * Method declared on IEditorPart
     */
    public void gotoMarker(IMarker marker) {
        setActivePage(0);
        getEditor(0).gotoMarker(marker);
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method
     * checks that the input is an instance of <code>IFileEditorInput</code>.
     */
    public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
        if (!(editorInput instanceof IFileEditorInput)) {
            throw new PartInitException("Invalid Input: Must be IFileEditorInput");
        }
        IFile pomFile = ((IFileEditorInput) editorInput).getFile();

        setInput(editorInput);
        setSite(site);

        try {
            initializeModel(pomFile);
        }
        catch (CoreException e) {
            throw new PartInitException(e.getStatus());
        }

        super.init(site, editorInput);
    }
    
    private void initializeModel(IFile pomFile) throws CoreException {
        documentProvider = new PomXmlDocumentProvider();
        createModel(pomFile);
        comparator = new ProjectComparator(pom);

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
            	String resolvedExtend = MevenideUtils.resolve(pom, pom.getExtend());
            	File extendFile = new File(resolvedExtend);
				if (log.isDebugEnabled()) {
					log.debug("parentPom path = " + resolvedExtend + "; exists = " + extendFile.exists());
				}

				if (!extendFile.exists() ) {
					// not an absolute path; must've been relative
					extendFile = new File(new File(pomFile.getLocation().toOSString()).getParentFile(), resolvedExtend);
					if (log.isDebugEnabled()) {
						log.debug("parentPom path = " + extendFile.getAbsolutePath() + "; exists = " + extendFile.exists());
					}
				}

				if (extendFile.exists()) {
					parentPom = reader.read(extendFile);
				}
            }
            
            updateTitleAndToolTip();
        }
        catch (Exception e) {
            log.error("could not read POM: ", e);
            throw new PartInitException("Could not obtain Project reader");
        }
    }

    protected void pageChange(int newPageIndex) {
		if (log.isDebugEnabled()) {
			log.debug("changing page: " + currentPageIndex + " => " + newPageIndex);
		}
        IPomEditorPage oldPage = getCurrentPage();
        IPomEditorPage newPage = getPage(newPageIndex);
        if (oldPage != null && newPage != null) {
	        oldPage.pageDeactivated(newPage);
	        newPage.pageActivated(oldPage);
        }
        super.pageChange(newPageIndex);

        currentPageIndex = newPageIndex;
    }

    public IPomEditorPage getCurrentPage() {
        return getPage(currentPageIndex);
    }

    public IPomEditorPage getPage(int pageIndex) {
        if (pageIndex == sourcePageIndex) {
            return sourcePage;
        }
        return (IPomEditorPage) getControl(pageIndex);
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
            comparator.setOriginalProject(updatedPom);

            String pomName = pom.getName();
            if (!MevenideUtils.isNull(pomName)) {
                setTitle(pomName);
                firePropertyChange(PROP_TITLE);
            }
			setModelDirty(false);

            clean = true;
        }
        catch (Exception e) {
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
            }
            catch (Exception e) {
                log.error("Marshalling POM failed", e);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("updateDocument exiting");
        }
    }

	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outline == null) {
				outline = new PomContentOutlinePage(getDocumentProvider(), this);
				if (getEditorInput() != null) {
					outline.setInput(getEditorInput());
				}
			}
			return outline;
		}
		return super.getAdapter(required);
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
//        //		MevenidePomEditorContributor contributor = getContributor();
//        //		if (contributor != null) {
//        //			contributor.updateActions();
//        //		}
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

}
