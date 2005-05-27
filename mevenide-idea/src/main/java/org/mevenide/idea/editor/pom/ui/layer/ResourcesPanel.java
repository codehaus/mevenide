package org.mevenide.idea.editor.pom.ui.layer;

import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.xml.XmlTag;

/**
 * @author Arik
 */
public class ResourcesPanel extends CRUDTablePanel {
    public ResourcesPanel(final Project pProject, final Document pDocument) {
        super(pProject, pDocument, new ResourcesTableModel(pProject, pDocument));
    }

    private static class ResourcesTableModel extends SimpleTagBasedXmlPsiTableModel {
        public ResourcesTableModel(final Project pProject,
                                   final Document pDocument) {
            super(pProject,
                  pDocument,
                  "build/resources",
                  "resource",
                  new String[]{
                      "Directory",
                      "Target Path",
                      "Includes",
                      "Excludes"
                  },
                  new String[]{
                      "directory",
                      "targetPath",
                      null,
                      null
                  });
        }

        @Override protected Object getValueFromTag(final XmlTag pTag,
                                                   final int pRow,
                                                   final int pColumn) {
            if (pColumn <= 1)
                return super.getValueFromTag(pTag, pRow, pColumn);
            else {
                final String patternContainerTagName;
                final String patternRowTagName;
                if (pColumn == 2) {
                    patternContainerTagName = "includes";
                    patternRowTagName = "include";
                }
                else {
                    patternContainerTagName = "excludes";
                    patternRowTagName = "exclude";
                }

                final XmlTag patternContainerTag = pTag.findFirstSubTag(patternContainerTagName);
                if (patternContainerTag == null)
                    return null;

                final XmlTag[] patternRowTags = patternContainerTag.findSubTags(patternRowTagName);
                final StringBuilder buf = new StringBuilder(patternRowTags.length * 10);
                for (XmlTag patternRowTag : patternRowTags) {
                    if (buf.length() > 0)
                        buf.append(", ");

                    buf.append(patternRowTag.getValue().getTrimmedText());
                }

                return buf.toString();
            }
        }

        @Override public boolean isCellEditable(final int pRow,
                                                final int pColumn) {
            return pColumn <= 1;
        }
    }
}
