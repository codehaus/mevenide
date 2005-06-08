package org.mevenide.idea.editor.pom.ui.layer.tests;

import com.intellij.psi.xml.XmlFile;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.util.psi.SingleValuedXmlTagRowsTableModel;
import org.mevenide.idea.util.ui.table.CRUDTableModel;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class TestsPatternsPanel extends CRUDTablePanel {
    public TestsPatternsPanel(final XmlFile pPsiFile,
                              final String pTagName) {
        super(pPsiFile, createModel(pPsiFile, pTagName));
    }

    private static CRUDTableModel createModel(final XmlFile pPsiFile,
                                              final String pTagName) {
        return new SingleValuedXmlTagRowsTableModel(
            pPsiFile,
            "project/build/unitTest/" + pTagName + "s",
            pTagName,
            StringUtils.capitalize(pTagName));
    }
}
