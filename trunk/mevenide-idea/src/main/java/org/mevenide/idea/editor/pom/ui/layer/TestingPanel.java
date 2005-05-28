package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.layer.resources.ResourcesPanel;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.lang.reflect.Field;

/**
 * @author Arik
 */
public class TestingPanel extends AbstractPomLayerPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(TestingPanel.class);

    /**
     * The unit tests source code directory field.
     */
    private final JTextField testsSourceDirField = new JTextField();

    /**
     * The directories locations panel.
     */
    private final JPanel dirsPanel = createDirectoriesPanel();

    /**
     * The test source code resources panel.
     */
    private final ResourcesPanel testsResourcesPanel = new ResourcesPanel(project, document, "build/unitTest/resources");

    /**
     * The tests include patterns panel.
     */
    private final CRUDTablePanel testsIncludesPanel = new CRUDTablePanel(project, document, TableModelConstants.TESTS_INCLUDES);

    /**
     * The tests exclude patterns panel.
     */
    private final CRUDTablePanel testsExcludesPanel = new CRUDTablePanel(project, document, TableModelConstants.TESTS_EXCLUDES);

    /**
     * Creates an instance for the given project and document.
     *
     * @param pProject the project this editor belongs to
     * @param pPomDocument the document backing up this panel
     */
    public TestingPanel(final Project pProject, final Document pPomDocument) {
        super(pProject, pPomDocument);

        initComponents();
        layoutComponents();
        bindComponents();
    }

    private void initComponents() {
        final Field[] fields = this.getClass().getDeclaredFields();
        for (final Field field : fields) {
            try {
                final Object value = field.get(this);
                if (value != null && value instanceof Component) {
                    final Component comp = (Component) value;
                    comp.setName(field.getName());
                }
            }
            catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void layoutComponents() {
        final String cols = "fill:min:grow,10dlu,fill:min:grow";
        final String rows = "top:min, top:min, top:min, fill:min:grow, top:min, fill:min:grow";
        final FormLayout layout = new FormLayout(cols, rows);
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setComponentFactory(new CustomFormsComponentFactory());

        UIUtils.installBorder(dirsPanel);
        UIUtils.installBorder(testsIncludesPanel);
        UIUtils.installBorder(testsExcludesPanel);
        UIUtils.installBorder(testsResourcesPanel);

        builder.appendSeparator(RES.get("test.dirs.title"));
        builder.append(dirsPanel, 3);
        builder.appendSeparator(RES.get("test.patterns.title"));
        builder.append(testsIncludesPanel, testsExcludesPanel);
        builder.appendSeparator(RES.get("test.conf.dirs.title"));
        builder.append(testsResourcesPanel, 3);
    }

    private void bindComponents() {
        synchronized (this) {
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(project, document);

            binder.bind(testsSourceDirField, "build/unitTestSourceDirectory");
        }
    }

    /**
     * Creates the directories panel.
     * @return panel
     */
    protected JPanel createDirectoriesPanel() {
        final FormLayout layout = new FormLayout("right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(new CustomFormsComponentFactory());

        builder.append(RES.get("test.src.dir"), testsSourceDirField);
        return builder.getPanel();
    }
}
