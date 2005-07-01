package org.mevenide.idea.editor.pom.ui.tests;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.AbstractPomLayerPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.UIUtils;

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
    private final TestResourcesPanel resources;

    /**
     * Creates an instance for the given file.
     */
    public TestingPanel(final PsiProject pProject) {
        final BeanAdapter model = new BeanAdapter(pProject, true);

        resources = new TestResourcesPanel(pProject);
        Bindings.bind(testsSourceDirField,
                      model.getValueModel("unitTestSourceDirectory"));

        layoutComponents();
    }

    private void layoutComponents() {
        final String cols = "fill:min:grow";
        final String rows = "top:min, top:min, top:min, fill:min:grow";
        final FormLayout layout = new FormLayout(cols, rows);
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        UIUtils.installBorder(dirsPanel);

        builder.appendSeparator(RES.get("test.dirs.title"));
        builder.append(dirsPanel);
        builder.appendSeparator(RES.get("test.patterns.title"));
        builder.append(resources);
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
