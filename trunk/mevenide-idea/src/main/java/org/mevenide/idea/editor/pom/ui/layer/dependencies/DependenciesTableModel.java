package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import org.mevenide.idea.util.psi.MultiValuedXmlTagRowsTableModel;
import org.mevenide.idea.util.psi.PsiEventType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.PsiTreeChangeEvent;

/**
 * @author Arik
 */
public class DependenciesTableModel extends MultiValuedXmlTagRowsTableModel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(DependenciesTableModel.class);

    private static final String TAG_PATH = "project/dependencies";
    private static final String ROW_TAG_NAME = "dependency";
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "groupId",
        "artifactId",
        "version",
        "type"
    };

    private static final String[] COLUMN_TITLES = new String[]{
        "Group ID",
        "Artifact ID",
        "Version",
        "Type"
    };

    public DependenciesTableModel(final XmlFile pPsiFile) {
        super(pPsiFile, TAG_PATH, ROW_TAG_NAME, VALUE_TAG_NAMES);
    }

    public String getColumnName(int column) {
        return COLUMN_TITLES[column];
    }

    public void refreshModel(final PsiEventType pType, final PsiTreeChangeEvent pEvent) {
        final XmlTag depsTag = getTag();
        if(depsTag == null || pType == null || pEvent == null) {
            super.refreshModel(pType, pEvent);
            return;
        }

        boolean propertyChange = false;
        boolean dependencyChange = false;

        XmlElement xmlElt = (XmlElement) pEvent.getElement();
        while(xmlElt != null) {
            if(depsTag.equals(xmlElt))
                dependencyChange = true;
            else if(xmlElt instanceof XmlTag) {
                final XmlTag tag = (XmlTag) xmlElt;
                propertyChange = tag.getName().equals("properties");
            }

            xmlElt = (XmlElement) xmlElt.getParent();
        }

        final String statusStr = "depChange=" + dependencyChange + "; " +
                                 "propChange=" + propertyChange;
        final String eltStr = "Element=" + pEvent.getElement();
        LOG.trace(pType + "; " + statusStr + "; " + eltStr);

        if(dependencyChange && !propertyChange)
            super.refreshModel(pType, pEvent);
    }
}
