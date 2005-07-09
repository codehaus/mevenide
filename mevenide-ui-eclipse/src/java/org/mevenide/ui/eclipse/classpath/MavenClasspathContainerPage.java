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

package org.mevenide.ui.eclipse.classpath;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.util.PixelConverter;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.nature.MevenideNature;
import org.mevenide.ui.eclipse.wizard.IHelpContextIds;
import org.mevenide.ui.eclipse.wizard.NewDependencyWizard;

/**
 * Displays the contents of a Maven classpath container in a tablular format.
 */
public class MavenClasspathContainerPage extends WizardPage implements
        IClasspathContainerPage, IClasspathContainerPageExtension {

    private static final String TITLE = "Maven Dependencies";
    private static final String ICON = "";
    private static final String GROUP = "Group";
    private static final String ARTIFACT_NAME = "Artifact Name";
    private static final String VERSION = "Version";
    private static final String TYPE = "Type";

    private String[] columnNames = new String[] { ICON, GROUP, ARTIFACT_NAME, VERSION, TYPE };
    private IClasspathEntry entry;
    private List entries = new ArrayList();
    private boolean dirty = false;
    private TableViewer viewer;
    private Image projectImage;
    private Image libraryImage;
    private Image slibraryImage;
    private IJavaProject javaProject;
    private Button addDependencyButton;
    private Button removeDependencyButton;
    private Button moveDependencyUpButton;
    private Button moveDependencyDownButton;

    /**
     * Initializes a new instance of MavenClasspathContainerPage.
     */
    public MavenClasspathContainerPage() {
        super("org.mevenide.ui.autosync.dependenciesPage"); //$NON-NLS-1$
        setTitle(TITLE);
        setDescription("This container dynamically manages the Maven dependencies.");
        this.projectImage = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
        this.libraryImage = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE);
        this.slibraryImage = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_EXTERNAL_ARCHIVE_WITH_SOURCE);
        setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension#initialize(org.eclipse.jdt.core.IJavaProject, org.eclipse.jdt.core.IClasspathEntry[])
     */
    public void initialize(IJavaProject project, IClasspathEntry[] currentEntries) {
        this.javaProject = project;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#finish()
     */
    public boolean finish() {
        if (this.dirty) {
            getClasspathContainer().setDependencies(this.entries);
            this.dirty = false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#getSelection()
     */
    public IClasspathEntry getSelection() {
        return this.entry;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.IClasspathContainerPage#setSelection(org.eclipse.jdt.core.IClasspathEntry)
     */
    public void setSelection(IClasspathEntry containerEntry) {
        try {
            this.entry = (containerEntry != null) ? containerEntry : createClasspathEntry();
            createRealEntries();
            if (this.viewer != null)
                this.viewer.setInput(this.entry);
        } catch (CoreException e) {
            final String msg = "Unable to add Maven nature to project " + this.javaProject.getProject().getName();
            Mevenide.displayError(TITLE, msg, e);
        }
    }

    private IClasspathEntry createClasspathEntry() throws CoreException {
        // make sure the nature is present
        MevenideNature.addToProject(this.javaProject.getProject());
        IPath path = new Path(MavenClasspathContainer.ID);
        return JavaCore.newContainerEntry(path);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        container.setLayout(layout);
        Label label = new Label(container, SWT.NULL);
        label.setText("Resolved entries:");
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);

        this.viewer = createTableViewer(container);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 400;
        gd.heightHint = 300;
        this.viewer.getTable().setLayoutData(gd);

        Composite buttonPanel = createButtonPanel(container);
        buttonPanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        // Changed for Eclipse 3.1
        Workbench.getInstance().getHelpSystem().setHelp(container, IHelpContextIds.MAVEN_CONTAINER_PAGE);
//      WorkbenchHelp.setHelp(container, IHelpContextIds.MAVEN_CONTAINER_PAGE);
        setControl(container);
        Dialog.applyDialogFont(container);

        if (this.entry != null) {
            this.viewer.setInput(this.entry);
        }
    }

    private TableViewer createTableViewer(Composite parent) {
        Table table = createTable(parent);

        TableViewer tableViewer = new TableViewer(table) {
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.TableViewer#internalRefresh(java.lang.Object)
             */
            protected void internalRefresh(Object element) {
                boolean usingMotif = "motif".equals(SWT.getPlatform()); //$NON-NLS-1$
                try {
                    // This avoids a "graphic is disposed" error on Motif by not letting
                    // it redraw while we remove entries.  Some items in this table are
                    // being removed and may have icons which may have already been
                    // disposed elsewhere.
                    if (usingMotif)
                        getTable().setRedraw(false);
                    super.internalRefresh(element);
                } finally {
                    if (usingMotif)
                        getTable().setRedraw(true);
                }
            }
        };

        tableViewer.setUseHashlookup(true);
        tableViewer.setColumnProperties(columnNames);

        CellEditor[] editors = new CellEditor[columnNames.length];

        for (int i = 0; i < editors.length; i++) {
            editors[i] = new TextCellEditor(table, SWT.READ_ONLY);
        }

        tableViewer.setCellEditors(editors);
        tableViewer.setContentProvider(new EntryContentProvider());
        tableViewer.setLabelProvider(new EntryLabelProvider());

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent e) {
                handleSelectionChanged((IStructuredSelection) e
                        .getSelection());
            }
        });

        return tableViewer;
    }

    private Table createTable(Composite parent) {
        Table table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn col = new TableColumn(table, SWT.CENTER);
        col.setWidth(24);
        col.setText(ICON);
        col.setResizable(false);

        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(160);
        col.setText(GROUP);
        col.setResizable(true);

        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(200);
        col.setText(ARTIFACT_NAME);
        col.setResizable(true);

        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(120);
        col.setText(VERSION);
        col.setResizable(true);

        col = new TableColumn(table, SWT.LEFT);
        col.setWidth(70);
        col.setText(TYPE);
        col.setResizable(true);

        return table;
    }

    private Composite createButtonPanel(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        container.setLayout(layout);

        this.addDependencyButton = new Button(container, SWT.PUSH);
        this.addDependencyButton.setText("Add");
        this.addDependencyButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        setButtonDimensionHint(this.addDependencyButton);
        this.addDependencyButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleAddDependency();
            }
        });
        this.addDependencyButton.setEnabled(true);

        this.removeDependencyButton = new Button(container, SWT.PUSH);
        this.removeDependencyButton.setText("Remove");
        this.removeDependencyButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        setButtonDimensionHint(this.removeDependencyButton);
        this.removeDependencyButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleRemoveDependency();
            }
        });
        this.removeDependencyButton.setEnabled(false);

        this.moveDependencyUpButton = new Button(container, SWT.PUSH);
        this.moveDependencyUpButton.setText("Up");
        this.moveDependencyUpButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        setButtonDimensionHint(this.moveDependencyUpButton);
        this.moveDependencyUpButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleMoveDependencyUp();
            }
        });
        this.moveDependencyUpButton.setEnabled(false);

        this.moveDependencyDownButton = new Button(container, SWT.PUSH);
        this.moveDependencyDownButton.setText("Down");
        this.moveDependencyDownButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        setButtonDimensionHint(this.moveDependencyDownButton);
        this.moveDependencyDownButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleMoveDependencyDown();
            }
        });
        this.moveDependencyDownButton.setEnabled(false);

        return container;
    }

    private void createRealEntries() {
        MavenClasspathContainer container = getClasspathContainer();
        if (container != null) {
            this.entries.addAll(container.getDependencies());
        }
    }

    private void handleSelectionChanged(IStructuredSelection selection) {
        List elements = selection.toList();
        this.removeDependencyButton.setEnabled(!elements.isEmpty());
        this.moveDependencyUpButton.setEnabled(!elements.isEmpty() && elements.get(0) != this.viewer.getElementAt(0));
        this.moveDependencyDownButton.setEnabled(!elements.isEmpty() && elements.get(elements.size() - 1) != this.viewer.getElementAt(this.viewer.getTable().getItemCount() - 1));
    }

    private void handleAddDependency() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        Shell shell = window.getShell();

        NewDependencyWizard wizard = new NewDependencyWizard();

        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        int result = dialog.open();
        Dependency dependency = wizard.getDependency();
        if (result == Window.OK && dependency != null) {
            this.entries.add(dependency);
            this.viewer.refresh();
            this.dirty = true;
        }
    }

    private void handleRemoveDependency() {
        IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
        this.entries.removeAll(selection.toList());
        this.viewer.remove(selection.toArray());
        this.dirty = true;
    }

    private void handleMoveDependencyUp() {
        IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();

        int pos = this.entries.indexOf(selection.getFirstElement());
        if (pos > 0) {
            Object old = this.entries.remove(pos - 1);
            this.entries.add(pos + selection.size() - 1, old);
            this.viewer.refresh();
            this.dirty = true;
        }
    }

    private void handleMoveDependencyDown() {
        IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();

        int pos = this.entries.indexOf(selection.getFirstElement());
        if (pos >= 0) {
            Object old = this.entries.remove(pos + selection.size());
            this.entries.add(pos, old);
            this.viewer.refresh();
            this.dirty = true;
        }
    }

    /**
     * @return the Maven classpath container
     */
    private MavenClasspathContainer getClasspathContainer() {
        MavenClasspathContainer container = null;

        if (this.javaProject != null) {
            try {
                container = (MavenClasspathContainer) JavaCore.getClasspathContainer(entry.getPath(), this.javaProject);
            } catch (JavaModelException e) {
                final String message = "An error occurred while retreaving Maven classpath container for " + this.javaProject.getProject().getName();
                Mevenide.displayError("Internal MevenIDE Error", message, e);
            }
        }

        return container;
    }

    /**
     * Sets width and height hint for the button control.
     * <b>Note:</b> This is a NOP if the button's layout data is not
     * an instance of <code>GridData</code>.
     * 
     * @param   the button for which to set the dimension hint
     */
    private static void setButtonDimensionHint(Button button) {
        Dialog.applyDialogFont(button);
        Assert.isNotNull(button);
        Object gd = button.getLayoutData();
        if (gd instanceof GridData) {
            ((GridData) gd).widthHint = getButtonWidthHint(button);
        }
    }

    /**
     * Returns a width hint for a button control.
     */
    private static int getButtonWidthHint(Button button) {
        if (button.getFont().equals(JFaceResources.getDefaultFont()))
            button.setFont(JFaceResources.getDialogFont());
        PixelConverter converter = new PixelConverter(button);
        int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
    }

    class EntryContentProvider implements IContentProvider, IStructuredContentProvider {
        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public Object[] getElements(Object parent) {
            return entries.toArray();
        }
    }

    class EntryLabelProvider implements ITableLabelProvider {
        public String getColumnText(Object obj, int col) {
            Dependency dependency = (Dependency) obj;
            if (dependency != null) {
                switch (col) {
                case 1:
                    return dependency.getGroupId();
                case 2:
                    return dependency.getArtifactId();
                case 3:
                    return dependency.getVersion();
                case 4:
                    return dependency.getType();
                default:
                    return null;
                }
            }
            return null;
        }

        public Image getColumnImage(Object obj, int col) {
            Image result = null;

            if (col == 0) {
                Dependency dependency = (Dependency) obj;
                IClasspathEntry entry = getClasspathContainer().getClasspathEntry(dependency);
                if (entry == null) {
                    entry = getClasspathContainer().buildClasspath(dependency);
                }
                int kind = entry.getEntryKind();
                if (kind == IClasspathEntry.CPE_PROJECT) {
                    result = projectImage;
                } else if (kind == IClasspathEntry.CPE_LIBRARY) {
                    IPath sourceAtt = entry.getSourceAttachmentPath();
                    result = sourceAtt != null ? slibraryImage : libraryImage;
                }
            }

            return result;
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }
    }

}
