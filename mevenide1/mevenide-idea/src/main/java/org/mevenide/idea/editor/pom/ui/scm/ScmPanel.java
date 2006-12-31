package org.mevenide.idea.editor.pom.ui.scm;

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
import org.mevenide.idea.psi.project.PsiScmRepository;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;

/**
 * @author Arik
 */
public class ScmPanel extends AbstractPomLayerPanel {
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

        versionsPanel = new VersionsPanel(pProject);
        branchesPanel = new BranchesPanel(pProject);

        layoutComponents();
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
        final SplitPanel<JPanel, JPanel> splitPanel = new SplitPanel<JPanel, JPanel>(
                new LabeledPanel(RES.get("versions.desc"), versionsPanel),
                new LabeledPanel(RES.get("branches.desc"), branchesPanel),
                false);
        add(splitPanel, c);
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
        builder.append(RES.get("scm.browse.url"), browseUrl);
        builder.appendSeparator(RES.get("versions.title"));
        return builder.getPanel();
    }
}
