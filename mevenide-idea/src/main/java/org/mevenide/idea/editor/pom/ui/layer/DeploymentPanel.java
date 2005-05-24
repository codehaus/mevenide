package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

import javax.swing.JTextField;

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

    public DeploymentPanel(final Project pProject, final Document pPomDocument) {
        super(pProject, pPomDocument);

        initComponents();
        layoutComponents();
        bindComponents();
    }

    private void initComponents() {
        nameComponents();
    }

    private void layoutComponents() {
        final FormLayout layout = new FormLayout(
                "right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, RES.getBundle(), this);
        builder.setComponentFactory(new CustomFormsComponentFactory());

        builder.appendSeparator(RES.get("site.deployment.title"));
        builder.append(RES.get("site.address"), siteAddressField);
        builder.append(RES.get("site.directory"), siteDirectoryField);

        builder.appendSeparator(RES.get("dist.deployment.title"));
        builder.append(RES.get("dist.address"), distributionAddressField);
        builder.append(RES.get("dist.directory"), distributionDirectoryField);
    }

    private void bindComponents() {
        synchronized (this) {
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(project, editorDocument);

            binder.bind(siteAddressField, "siteAddress");
            binder.bind(siteDirectoryField, "siteDirectory");
            binder.bind(distributionAddressField, "distributionAddress");
            binder.bind(distributionDirectoryField, "distributionDirectory");
        }
    }
}
