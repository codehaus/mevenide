package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.psi.AbstractPsiXmlListener;
import org.mevenide.idea.util.psi.PsiUtils;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Arik
 */
public class PomDependenciesTableModel extends AbstractTableModel {
    /**
     * An enum for specifying what is the source of current event. When the
     * modifies the UI (e.g. not from the text editor), the {@link PomDependenciesTableModel#modificationSource}
     * field will be set to {@link #UI}. If the user modifies using the
     * text editor, the field will be set to {@link #EDITOR}.
     *
     * <p>This is done because the code responding to UI modifications updates
     * the PSI tree, which invokes the code responding to PSI modifications,
     * which updates the UI - this can cause an infinite loop, so we need to
     * know who started the loop to avoid it.</p>
     */
    private enum ModificationSource { UI, EDITOR }

    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(PomDependenciesTableModel.class);

    /**
     * The name of the tag containing all project dependencies in the POM.
     */
    private static final String DEPENDENCIES_TAG_NAME = "dependencies";

    /**
     * The name of a dependency tag in the POM.
     */
    private static final String DEPENDENCY_TAG_NAME = "dependency";

    /**
     * The index of the group ID column.
     */
    protected static final int GROUP_ID_INDEX = 0;

    /**
     * The index of the artifact ID column.
     */
    protected static final int ARTIFACT_ID_INDEX = 1;

    /**
     * The index of the artifact type column.
     */
    protected static final int TYPE_INDEX = 2;

    /**
     * The index of the version column.
     */
    protected static final int VERSION_INDEX = 3;

    /**
     * The number of columns displayed.
     */
    protected static final int COLUMN_COUNT = 4;

    /**
     * Used to synchronize between the PSI and UI listeners.
     */
    private final Object LOCK = new Object();

    /**
     * The source of the current PSI or UI event. Used to prevent infinite
     * loops between the PSI listener and the UI model code.
     */
    private ModificationSource modificationSource = null;

    /**
     * The POM file.
     */
    private final XmlFile xmlFile;

    /**
     * The list of dependencies in the POM. This is sort of a cache which
     * is constantly updated from PSI events or the {@link #setValueAt(Object, int, int)} method
     * (called after UI events).
     */
    private final List<Dependency> dependencies = Collections.synchronizedList(new ArrayList<Dependency>(5));

    /**
     * The project.
     */
    private final Project project;

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject the project.
     * @param pIdeaDocument the document.
     */
    public PomDependenciesTableModel(final Project pProject, final Document pIdeaDocument) {
        project = pProject;
        xmlFile = PsiUtils.findXmlFile(project, pIdeaDocument);

        refreshModel();

        //
        //add this as a PSI listener, so that we can update this instance when the
        //PSI changes
        //
        PsiManager.getInstance(project).addPsiTreeChangeListener(new DependenciesPsiListener());
    }

    /**
     * Disposes this component.
     *
     * @todo currently we unregister this instance as a PSI listener, but this method will never be called until we unregister, so this is a paradox. We must move this to a different place.
     * @throws Throwable
     */
    @Override protected void finalize() throws Throwable {
        super.finalize();
        PsiManager.getInstance(project).removePsiTreeChangeListener(new DependenciesPsiListener());
    }

    @Override public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override public String getColumnName(int column) {
        switch(column) {
            case GROUP_ID_INDEX:
                return "Group";
            case ARTIFACT_ID_INDEX:
                return "Artifact";
            case TYPE_INDEX:
                return "Type";
            case VERSION_INDEX:
                return "Version";
            default:
                throw new IllegalArgumentException(RES.get("illegal.column.index", column));
        }
    }

    @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        synchronized (LOCK) {
            if(modificationSource == ModificationSource.EDITOR)
                return;

            modificationSource = ModificationSource.UI;
            try {
                final XmlTag depsTag = findDependenciesTag();
                final XmlTag depTag = depsTag.findSubTags(DEPENDENCY_TAG_NAME)[rowIndex];

                final String stringValue = aValue == null ? null : aValue.toString();
                final Dependency dep = dependencies.get(rowIndex);

                switch(columnIndex) {
                    case GROUP_ID_INDEX:
                        dep.setGroupId(stringValue);
                        PsiUtils.setTagValue(project, depTag, "groupId", stringValue);
                        break;
                    case ARTIFACT_ID_INDEX:
                        dep.setArtifactId(stringValue);
                        PsiUtils.setTagValue(project, depTag, "artifactId", stringValue);
                        break;
                    case TYPE_INDEX:
                        dep.setType(stringValue);
                        PsiUtils.setTagValue(project, depTag, "type", stringValue);
                        break;
                    case VERSION_INDEX:
                        dep.setVersion(stringValue);
                        PsiUtils.setTagValue(project, depTag, "version", stringValue);
                        break;
                    default:
                        throw new IllegalArgumentException(RES.get("illegal.column.index", columnIndex));
                }
            }
            finally {
                modificationSource = null;
            }
        }
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public int getRowCount() {
        return dependencies.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        final Dependency dep = dependencies.get(rowIndex);

        switch(columnIndex) {
            case GROUP_ID_INDEX:
                return dep.getGroupId();
            case ARTIFACT_ID_INDEX:
                return dep.getArtifactId();
            case TYPE_INDEX:
                return dep.getType();
            case VERSION_INDEX:
                return dep.getVersion();
            default:
                throw new IllegalArgumentException(RES.get("illegal.column.index", columnIndex));
        }
    }

    private XmlTag findDependenciesTag() {
        final XmlDocument xmlDocument = xmlFile.getDocument();
        if(xmlDocument == null)
            return null;

        final XmlTag projectTag = xmlDocument.getRootTag();
        if(projectTag == null)
            return null;

        return projectTag.findFirstSubTag(DEPENDENCIES_TAG_NAME);
    }

    private Dependency createDependency(final XmlTag depTag) {
        final XmlTag groupIdTag = depTag.findFirstSubTag("groupId");
        final XmlTag artifactIdTag = depTag.findFirstSubTag("artifactId");
        final XmlTag versionTag = depTag.findFirstSubTag("version");
        final XmlTag typeTag = depTag.findFirstSubTag("type");

        final String groupId = groupIdTag == null ? null : groupIdTag.getValue().getTrimmedText();
        final String artifactId = artifactIdTag == null ? null : artifactIdTag.getValue().getTrimmedText();
        final String version = versionTag == null ? null : versionTag.getValue().getTrimmedText();
        final String type = typeTag == null ? null : typeTag.getValue().getTrimmedText();

        final Dependency dep = new Dependency();
        dep.setGroupId(groupId);
        dep.setArtifactId(artifactId);
        dep.setVersion(version);
        dep.setType(type);
        return dep;
    }

    private void refreshModel() {
        synchronized(LOCK) {
            dependencies.clear();

            final XmlTag depsTag = findDependenciesTag();
            if(depsTag != null)
                for(XmlTag depTag : depsTag.findSubTags(DEPENDENCY_TAG_NAME))
                    dependencies.add(createDependency(depTag));

            fireTableStructureChanged();
        }
    }

    private class DependenciesPsiListener extends AbstractPsiXmlListener {

        public DependenciesPsiListener() {
            super(PomDependenciesTableModel.this.xmlFile);
        }

        private void doRefresh() {
            synchronized (LOCK) {
                if(modificationSource == ModificationSource.UI)
                    return;

                modificationSource = ModificationSource.EDITOR;
                try {
                    refreshModel();
                }
                finally {
                    modificationSource = null;
                }
            }
        }

        @Override public void childAdded(PsiTreeChangeEvent event) {
            doRefresh();
        }

        @Override public void childMoved(PsiTreeChangeEvent event) {
            doRefresh();
        }

        @Override public void childRemoved(PsiTreeChangeEvent event) {
            doRefresh();
        }

        @Override public void childReplaced(PsiTreeChangeEvent event) {
            doRefresh();
        }
    }
}
