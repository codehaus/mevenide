package org.mevenide.idea.editor.pom.ui.scm;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.*;
import static java.awt.GridBagConstraints.BOTH;
import java.lang.reflect.Field;
import javax.swing.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.AbstractPomLayerPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiScmRepository;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * @author Arik
 */
public class ScmPanel extends AbstractPomLayerPanel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ScmPanel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ScmPanel.class);

    private final JTextField anonymousConnection = new JTextField();
    private final JTextField developerConnection = new JTextField();
    private final JTextField browseUrl = new JTextField();
    private final JPanel versionsPanel;
    private final JPanel branchesPanel;

    protected final PsiScmRepository scmRepository;
    protected final BeanAdapter model;

    public ScmPanel(final PsiProject pProject) {
        scmRepository = pProject.getScmRepository();
        model = new BeanAdapter(scmRepository, true);

        bindComponents();

        this.versionsPanel = new LabeledPanel(RES.get("versions.desc"),
                                              new VersionsPanel(pProject));
        branchesPanel = new LabeledPanel(RES.get("branches.desc"),
                                         new BranchesPanel(pProject));

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
        Bindings.bind(anonymousConnection, model.getValueModel("anonymousConnection"));
        Bindings.bind(developerConnection, model.getValueModel("developerConnection"));
        Bindings.bind(browseUrl, model.getValueModel("url"));
    }

    private JPanel createRepositoryPanel() {
        final FormLayout layout = new FormLayout("right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(CustomFormsComponentFactory.getInstance());

        //
        //repository
        //
        builder.appendSeparator(RES.get("repository.title"));
        builder.append(RES.get("scm.anonymous.connection"), anonymousConnection);
        builder.append(RES.get("scm.developer.connection"), developerConnection);
        builder.append(RES.get("scn.browse.url"), browseUrl);
        builder.appendSeparator(RES.get("versions.title"));
        final JPanel panel = builder.getPanel();
        UIUtils.installBorder(panel, 0, 0, 0, 0);

        return panel;
    }
}
