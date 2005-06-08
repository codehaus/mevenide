package org.mevenide.idea.editor.pom.ui.layer.scm;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.ui.table.SimpleCRUDTablePanel;

/**
 * @author Arik
 */
public class VersionsPanel extends SimpleCRUDTablePanel {
    private static final String TAG_PATH = "project/versions";
    private static final String ROW_TAG_NAME = "version";
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "id",
        "name",
        "tag"
    };

    private static final String[] COLUMN_TITLES = new String[]{
        "ID",
        "Name",
        "Tag"
    };

    public VersionsPanel(final XmlFile pFile) {
        super(pFile, TAG_PATH, ROW_TAG_NAME, VALUE_TAG_NAMES, COLUMN_TITLES);
    }
}