package org.mevenide.idea.editor.pom.ui.layer.mailingLists;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.ui.table.SimpleCRUDTablePanel;

/**
 * @author Arik
 */
public class MailingListsPanel extends SimpleCRUDTablePanel {
    private static final String TAG_PATH = "project/mailingLists";
    private static final String ROW_TAG_NAME = "mailingList";
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "name",
        "subscribe",
        "unsubscribe",
        "archive"
    };

    private static final String[] COLUMN_TITLES = new String[]{
        "Name",
        "Subscribe",
        "Unsubscribe",
        "Archive"
    };

    public MailingListsPanel(final XmlFile pXmlFile) {
        super(pXmlFile,
              TAG_PATH,
              ROW_TAG_NAME,
              VALUE_TAG_NAMES,
              COLUMN_TITLES);
    }
}
