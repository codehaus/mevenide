package org.mevenide.idea.editor.pom.ui.layer.scm;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.ui.table.SimpleCRUDTablePanel;

/**
 * @author Arik
 */
public class BranchesPanel extends SimpleCRUDTablePanel {
    private static final String TAG_PATH = "project/branches";
    private static final String ROW_TAG_NAME = "branch";
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "tag"
    };

    private static final String[] COLUMN_TITLES = new String[]{
        "Tag"
    };

    public BranchesPanel(final XmlFile pFile) {
        super(pFile, TAG_PATH, ROW_TAG_NAME, VALUE_TAG_NAMES, COLUMN_TITLES);
    }
}
