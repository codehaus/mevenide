package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.psi.xml.XmlFile;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.lang.reflect.Field;
import javax.swing.JTextField;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

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

    public DeploymentPanel(final XmlFile pFile) {
        super(pFile);

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
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(file);

            binder.bind(siteAddressField, "project/siteAddress");
            binder.bind(siteDirectoryField, "project/siteDirectory");
            binder.bind(distributionAddressField, "project/distributionAddress");
            binder.bind(distributionDirectoryField, "project/distributionDirectory");
        }
    }
}
