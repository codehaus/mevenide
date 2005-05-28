package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.layer.resources.ResourcesPanel;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import java.awt.GridBagLayout;
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
     * The test source code resources panel.
     */
    private final ResourcesPanel testsResourcesPanel = new ResourcesPanel(project, document, "build/unitTest/resources");

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
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        c = new GridBagConstraints();
        c.fill = BOTH;
        add(createDirectoriesPanel(), c);

        c = new GridBagConstraints();
        c.fill = BOTH;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        add(testsResourcesPanel, c);
        UIUtils.installBorder(testsResourcesPanel, 10, 0, 0, 0);
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

        //
        //repository
        //
        builder.appendSeparator(RES.get("test.dirs.title"));
        builder.append(RES.get("test.src.dir"), testsSourceDirField);
        builder.appendSeparator(RES.get("test.conf.dirs.title"));
        final JPanel panel = builder.getPanel();
        UIUtils.installBorder(panel, 0, 0, 0, 0);

        return panel;
    }
}
