package org.mevenide.idea.editor.pom.ui;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;

/**
 * @author Arik
 */
public class DeploymentPanel extends AbstractPomLayerPanel {
    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(DeploymentPanel.class);

    private final JTextField siteAddressField = new JTextField();
    private final JTextField siteDirectoryField = new JTextField();
    private final JTextField distributionAddressField = new JTextField();
    private final JTextField distributionDirectoryField = new JTextField();

    protected final PsiProject project;
    protected final BeanAdapter model;

    public DeploymentPanel(final PsiProject psiProject) {
        project = psiProject;
        model = new BeanAdapter(project, true);

        layoutComponents();
        bindComponents();
    }

    private void layoutComponents() {
        final FormLayout layout = new FormLayout(
                "right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout,
                                                            RES.getBundle(),
                                                            this);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        builder.appendSeparator(RES.get("site.deployment.title"));
        builder.append(RES.get("site.address"), siteAddressField);
        builder.append(RES.get("site.directory"), siteDirectoryField);

        builder.appendSeparator(RES.get("dist.deployment.title"));
        builder.append(RES.get("dist.address"), distributionAddressField);
        builder.append(RES.get("dist.directory"), distributionDirectoryField);
    }

    private void bindComponents() {
        Bindings.bind(siteAddressField, model.getValueModel("siteAddress"));
        Bindings.bind(siteDirectoryField, model.getValueModel("siteDirectory"));
        Bindings.bind(distributionAddressField,
                      model.getValueModel("distributionAddress"));
        Bindings.bind(distributionDirectoryField,
                      model.getValueModel("distributionDirectory"));
    }
}
