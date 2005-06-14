package org.mevenide.idea.editor.pom.ui.layer.tests;

import com.intellij.psi.xml.XmlFile;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.lang.reflect.Field;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.editor.pom.ui.layer.build.ResourcesPanel;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

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
    private final ResourcesPanel testsResourcesPanel;

    /**
     * The tests include patterns panel.
     */
    private final TestsPatternsPanel testsIncludesPanel;

    /**
     * The tests exclude patterns panel.
     */
    private final TestsPatternsPanel testsExcludesPanel;

    /**
     * Creates an instance for the given file.
     *
     * @param pXmlFile the file we are editing
     */
    public TestingPanel(final XmlFile pXmlFile) {
        super(pXmlFile);

        testsIncludesPanel = new TestsPatternsPanel(pXmlFile, "include");
        testsExcludesPanel = new TestsPatternsPanel(pXmlFile, "exclude");
        testsResourcesPanel = new ResourcesPanel(pXmlFile, "project/build/unitTest/resources");

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
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

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
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(file);

            binder.bind(testsSourceDirField, "project/build/unitTestSourceDirectory");
        }
    }

    /**
     * Creates the directories panel.
     * @return panel
     */
    protected JPanel createDirectoriesPanel() {
        final FormLayout layout = new FormLayout("right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        builder.append(RES.get("test.src.dir"), testsSourceDirField);
        return builder.getPanel();
    }
}
