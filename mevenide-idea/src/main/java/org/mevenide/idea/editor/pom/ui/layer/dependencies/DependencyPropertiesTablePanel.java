package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class DependencyPropertiesTablePanel extends CRUDTablePanel {
    public DependencyPropertiesTablePanel(final XmlFile pXmlFile) {
        super(pXmlFile, new DependencyPropertiesTableModel(pXmlFile));
    }
}
