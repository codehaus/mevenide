package org.mevenide.idea.editor.pom.ui.layer.scm;

import com.intellij.psi.xml.XmlFile;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.text.XmlPsiDocumentBinder;

/**
 * @author Arik
 */
public class ScmPanel extends AbstractPomLayerPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ScmPanel.class);

    private final JTextField anonynousConnection = new JTextField();
    private final JTextField developerConnection = new JTextField();
    private final JTextField browseUrl = new JTextField();
    private final JPanel versionsPanel;
    private final JPanel branchesPanel;

    public ScmPanel(final XmlFile pXmlFile) {
        super(pXmlFile);
        bindComponents();

        this.versionsPanel = new LabeledPanel(RES.get("versions.desc"),
                                              new VersionsPanel(file));
        branchesPanel = new LabeledPanel(RES.get("branches.desc"),
                                         new BranchesPanel(file));

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        UIUtils.installBorder(versionsPanel, 5, 0, 10, 0);
        UIUtils.installBorder(branchesPanel, 10, 0, 0, 0);

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
        add(createRepositoryPanel(), c);

        c = new GridBagConstraints();
        c.fill = BOTH;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        add(new SplitPanel<JPanel, JPanel>(versionsPanel, branchesPanel), c);
    }

    private void bindComponents() {
        synchronized (this) {
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(file);

            binder.bind(anonynousConnection, "project/repository/connection");
            binder.bind(developerConnection, "project/repository/developerConnection");
            binder.bind(browseUrl, "project/repository/url");
        }
    }

    private JPanel createRepositoryPanel() {
        final FormLayout layout = new FormLayout("right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        //
        //repository
        //
        builder.appendSeparator(RES.get("repository.title"));
        builder.append(RES.get("scm.anonymous.connection"), anonynousConnection);
        builder.append(RES.get("scm.developer.connection"), developerConnection);
        builder.append(RES.get("scn.browse.url"), browseUrl);
        builder.appendSeparator(RES.get("versions.title"));
        final JPanel panel = builder.getPanel();
        UIUtils.installBorder(panel, 0, 0, 0, 0);

        return panel;
    }
}
