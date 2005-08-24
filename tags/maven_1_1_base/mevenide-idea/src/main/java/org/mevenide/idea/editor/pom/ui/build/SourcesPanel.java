package org.mevenide.idea.editor.pom.ui.build;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.*;
import static java.awt.GridBagConstraints.BOTH;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.AbstractPomLayerPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * @author Arik
 */
public class SourcesPanel extends AbstractPomLayerPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(SourcesPanel.class);

    /**
     * The source code directory field.
     */
    private final JTextField sourceDirField = new JTextField();

    /**
     * The aspects source code directory field.
     */
    private final JTextField aspectSourceDirField = new JTextField();

    /**
     * The resources panel.
     */
    private final ResourcesPanel resourcesPanel;
    private final BeanAdapter model;

    /**
     * Creates an instance for the given project and document.
     */
    public SourcesPanel(final PsiProject pProject) {
        model = new BeanAdapter(pProject, true);

        resourcesPanel = new ResourcesPanel(pProject.getResources());
        layoutComponents();
        bindComponents();
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
        add(resourcesPanel, c);
        UIUtils.installBorder(resourcesPanel, 10, 0, 0, 0);
    }

    private void bindComponents() {
        Bindings.bind(sourceDirField, model.getValueModel("sourceDirectory"));
        Bindings.bind(aspectSourceDirField, model.getValueModel("aspectSourceDirectory"));
    }

    /**
     * Creates the directories panel.
     *
     * @return panel
     */
    protected JPanel createDirectoriesPanel() {
        final FormLayout layout = new FormLayout("right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        //
        //repository
        //
        builder.appendSeparator(RES.get("src.dirs.title"));
        builder.append(RES.get("java.src.dir"), sourceDirField);
        builder.append(RES.get("aspect.src.dir"), aspectSourceDirField);
        builder.appendSeparator(RES.get("conf.dirs.title"));
        return builder.getPanel();
    }
}
