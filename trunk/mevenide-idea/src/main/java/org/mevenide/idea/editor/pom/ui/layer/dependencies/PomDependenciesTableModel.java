package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.psi.PsiUtils;
import org.mevenide.idea.util.ui.table.AbstractXmlPsiTableModel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Arik
 */
public class PomDependenciesTableModel extends AbstractXmlPsiTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(PomDependenciesTableModel.class);

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
     * The column titles.
     */
    private static final String[] COLUMN_TITLES = new String[]{
        "Group ID",
        "Artifact ID",
        "Version",
        "Type"
    };

    /**
     * The JavaBeans property names respective of each column index.
     */
    private static final String[] COLUMN_PROPERTY_NAMES = new String[]{
        "groupId",
        "artifactId",
        "version",
        "type"
    };

    /**
     * Number of columns this model provides.
     */
    private static final int COLUMN_COUNT = COLUMN_TITLES.length;

    /**
     * The list of dependencies in the POM. This is sort of a cache which is constantly updated from
     * PSI events or the {@link #setValueAt(Object, int, int)} method (called after UI events).
     */
    private final List<Dependency> dependencies = Collections.synchronizedList(new ArrayList<Dependency>(5));

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public PomDependenciesTableModel(final Project pProject, final Document pIdeaDocument) {
        super(pProject, pIdeaDocument);

        //
        //start off with the current contents of the file reflected in this model
        //
        refreshModel();
    }

    @Override public Class<?> getColumnClass(final int pColumn) {
        return String.class;
    }

    @Override public String getColumnName(final int pColumn) {
        if (pColumn < 0 || pColumn > COLUMN_COUNT)
            throw new IllegalArgumentException(RES.get("illegal.column.index", pColumn));

        return COLUMN_TITLES[pColumn];
    }

    @Override public boolean isCellEditable(final int pRow, final int pColumn) {
        return true;
    }

    protected void setValueAtInternal(final Object pValue,
                                      final int pRow,
                                      final int pColumn) {
        final XmlTag depsTag = findDependenciesTag();
        final XmlTag depTag = depsTag.findSubTags(DEPENDENCY_TAG_NAME)[pRow];

        final String stringValue = pValue == null ? null : pValue.toString();
        final Dependency dep = dependencies.get(pRow);
        final String propertyName = COLUMN_PROPERTY_NAMES[pColumn];

        try {
            PsiUtils.setTagValue(project, depTag, propertyName, stringValue);
            PropertyUtils.setProperty(dep, propertyName, stringValue);
        }
        catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        catch (NoSuchMethodException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public int getRowCount() {
        return dependencies.size();
    }

    public Object getValueAt(final int pRow, final int pColumn) {
        final Dependency dep = dependencies.get(pRow);
        final String propertyName = COLUMN_PROPERTY_NAMES[pColumn];
        try {
            return PropertyUtils.getProperty(dep, propertyName);
        }
        catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        catch (InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        catch (NoSuchMethodException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private XmlTag findDependenciesTag() {
        final XmlDocument xmlDocument = xmlFile.getDocument();
        if (xmlDocument == null)
            return null;

        final XmlTag projectTag = xmlDocument.getRootTag();
        if (projectTag == null)
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

    @Override protected void refreshModel(final PsiEventType pEventType,
                                          final PsiTreeChangeEvent pEvent) {
        dependencies.clear();

        final XmlTag depsTag = findDependenciesTag();
        if (depsTag != null)
            for (XmlTag depTag : depsTag.findSubTags(DEPENDENCY_TAG_NAME))
                dependencies.add(createDependency(depTag));

        fireTableStructureChanged();
    }
}
