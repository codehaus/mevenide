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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.project.Branch;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.Project;
import org.apache.maven.project.Version;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.mevenide.project.Report;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideResources;
import org.mevenide.ui.eclipse.editors.pom.pages.DependenciesPage;
import org.mevenide.ui.eclipse.editors.pom.pages.ReportsPage;
import org.mevenide.ui.eclipse.editors.pom.pages.RepositoryPage;
import org.mevenide.ui.eclipse.editors.pom.pages.TeamPage;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomContentOutlinePage extends ContentOutlinePage {

    private static final String[] PAGES = {
            MevenideResources.OVERVIEW_PAGE_TAB,
            MevenideResources.DESCRIPTION_PAGE_TAB,
            MevenideResources.ORGANIZATION_PAGE_TAB,
            MevenideResources.REPOSITORY_PAGE_TAB,
            MevenideResources.TEAM_PAGE_TAB,
            MevenideResources.DEPENDENCIES_PAGE_TAB,
            MevenideResources.BUILD_PAGE_TAB,
            MevenideResources.UNIT_TESTS_PAGE_TAB,
            MevenideResources.REPORTS_PAGE_TAB
        };
    
    class PomTreeContentProvider extends LabelProvider implements ITreeContentProvider {
        
        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren(Object parentElement) {
            System.out.println("PomTreeContentProvider.getChildren(): " + parentElement);
            if (parentElement instanceof String) {
                String string = (String) parentElement;
                if (MevenideResources.REPOSITORY_PAGE_TAB.equals(string)) {
                    List versions = new ArrayList();
                    if (pom.getVersions() != null) {
                        versions.addAll( pom.getVersions() );
                    }
                    if (pom.getBranches() != null) {
                        versions.addAll( pom.getBranches() );
                    }
                    return versions.toArray();
                }
                if (MevenideResources.TEAM_PAGE_TAB.equals(string)) {
                    List teamMembers = new ArrayList();
                    if (pom.getDevelopers() != null) {
                        teamMembers.addAll( pom.getDevelopers() );
                    }
                    if (pom.getContributors() != null) {
                        teamMembers.addAll( pom.getContributors() );
                    }
                    return teamMembers.toArray();
                }
                if (MevenideResources.DEPENDENCIES_PAGE_TAB.equals(string)) {
                    List dependencies = new ArrayList();
                    if (pom.getDependencies() != null) {
                        dependencies.addAll( pom.getDependencies() );
                    }
                    return dependencies.toArray();
                }
                if (MevenideResources.REPORTS_PAGE_TAB.equals(string)) {
                    List reports = new ArrayList();
                    if (pom.getReports() != null) {
                        reports.addAll( wrapReportStrings( pom.getReports() ) );
                    }
                    return reports.toArray();
                }
            }
            return new Object[0];
        }

        private List wrapReportStrings(List reports) {
            List wrappedReports = new ArrayList();
            for (Iterator i = reports.iterator(); i.hasNext(); ) {
                wrappedReports.add( new Report((String) i.next()) );
            }
            return wrappedReports;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent(Object element) {
            System.out.println("PomTreeContentProvider.getParent(): " + element);
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren(Object element) {
            System.out.println("PomTreeContentProvider.hasChildren(): " + element);
            if (element instanceof String) {
                String string = (String) element;
                if (MevenideResources.OVERVIEW_PAGE_TAB.equals(string) ||
                    MevenideResources.DESCRIPTION_PAGE_TAB.equals(string) ||
                    MevenideResources.ORGANIZATION_PAGE_TAB.equals(string) ||
                    MevenideResources.BUILD_PAGE_TAB.equals(string) ||
                    MevenideResources.UNIT_TESTS_PAGE_TAB.equals(string))
                {
                    return false;
                }
                if (MevenideResources.TEAM_PAGE_TAB.equals(string)  ||
                    MevenideResources.REPOSITORY_PAGE_TAB.equals(string) ||
                    MevenideResources.DEPENDENCIES_PAGE_TAB.equals(string) ||
                    MevenideResources.REPORTS_PAGE_TAB.equals(string) )
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object element) {
            if (element instanceof String) {
                return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.MAVEN_POM_OBJ);
            }
            if (element instanceof Dependency) {
                return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.DEPENDENCY_OBJ);
            }
            if (element instanceof Developer) {
                return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.DEVELOPER_OBJ);
            }
            if (element instanceof Contributor) {
                return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.CONTRIBUTOR_OBJ);
            }
            if (element instanceof Branch) {
                return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.BRANCH_OBJ);
            }
            if (element instanceof Version) {
                return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.VERSION_OBJ);
            }
            if (element instanceof Report) {
                return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.REPORT_OBJ);
            }
            return null;
        }
        
        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         */
        public String getText(Object element) {
            if (element instanceof Dependency) {
                Dependency dep = (Dependency) element;
                return dep.getArtifact();
            }
            if (element instanceof Branch) {
                Branch branch = (Branch) element;
                return branch.getTag();
            }
            return super.getText(element);
        }
        
        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object inputElement) {
            return PAGES;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            System.out.println("PomTreeContentProvider.inputChanged()");
        }
    	
    }
    
    private final MevenidePomEditor editor;
    private final PomTreeContentProvider provider;
    private Project pom;

    public PomContentOutlinePage(MevenidePomEditor pomEditor) {
        this.editor = pomEditor;
        this.provider = new PomTreeContentProvider();
    }
    
    public void setModel(Project pom) {
        System.out.println("PomContentOutlinePage.setModel(): " + pom);
    	this.pom = pom;
        if (getTreeViewer() != null) {
            System.out.println("PomContentOutlinePage.setModel(): tree will get pom as input");
            getTreeViewer().setInput(pom);
        }
    }

    /**
     * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
        
		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(provider);
		viewer.setLabelProvider(provider);
		viewer.addSelectionChangedListener(this);
        if (pom != null) {
            System.out.println("PomContentOutlinePage.createControl(): setting pom as input for tree");
            viewer.setInput(pom);
        }
    }
    
    public void selectionChanged(SelectionChangedEvent event) {
        super.selectionChanged(event);
        
        ISelection sel = event.getSelection();
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) sel;
            Object selectedElement = selection.getFirstElement();
            if (selectedElement != null) {
                System.out.println("PomContentOutlinePage.selectionChanged(): " + selectedElement.getClass().getName());
                if (selectedElement instanceof String) {
                    String string = (String) selectedElement;
                    if (MevenideResources.OVERVIEW_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.OVERVIEW_PAGE_ID);
                    }
                    if (MevenideResources.DESCRIPTION_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.DESCRIPTION_PAGE_ID);
                    }
                    if (MevenideResources.ORGANIZATION_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.ORGANIZATION_PAGE_ID);
                    }
                    if (MevenideResources.REPOSITORY_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.REPOSITORY_PAGE_ID);
                    }
                    if (MevenideResources.TEAM_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.TEAM_PAGE_ID);
                    }
                    if (MevenideResources.DEPENDENCIES_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.DEPENDENCIES_PAGE_ID);
                    }
                    if (MevenideResources.BUILD_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.BUILD_PAGE_ID);
                    }
                    if (MevenideResources.UNIT_TESTS_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.UNIT_TESTS_PAGE_ID);
                    }
                    if (MevenideResources.REPORTS_PAGE_TAB.equals(string)) {
                        editor.setActivePage(MevenideResources.REPORTS_PAGE_ID);
                    }
                }
                if (selectedElement instanceof Version) {
                    Version version = (Version) selectedElement;
                    RepositoryPage page = (RepositoryPage) editor.setActivePage(MevenideResources.REPOSITORY_PAGE_ID);
                    page.selectReveal(version);
                }
                if (selectedElement instanceof Branch) {
                    Branch branch = (Branch) selectedElement;
                    RepositoryPage page = (RepositoryPage) editor.setActivePage(MevenideResources.REPOSITORY_PAGE_ID);
                    page.selectReveal(branch);
                }
                if (selectedElement instanceof Dependency) {
                    Dependency dependency = (Dependency) selectedElement;
                    DependenciesPage page = (DependenciesPage) editor.setActivePage(MevenideResources.DEPENDENCIES_PAGE_ID);
                    page.selectReveal(dependency);
                }
                if (selectedElement instanceof Developer) {
                    Developer developer = (Developer) selectedElement;
                    TeamPage page = (TeamPage) editor.setActivePage(MevenideResources.TEAM_PAGE_ID);
                    page.selectReveal(developer);
                }
                if (selectedElement instanceof Contributor) {
                    Contributor contributor = (Contributor) selectedElement;
                    TeamPage page = (TeamPage) editor.setActivePage(MevenideResources.TEAM_PAGE_ID);
                    page.selectReveal(contributor);
                }
                if (selectedElement instanceof Report) {
                    Report report = (Report) selectedElement;
                    ReportsPage page = (ReportsPage) editor.setActivePage(MevenideResources.REPORTS_PAGE_ID);
                    page.selectReveal(report);
                }
            }
        }
    }

}
