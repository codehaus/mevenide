package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.ui.layer.model.BranchesTableModel;
import org.mevenide.idea.editor.pom.ui.layer.model.VersionsTableModel;
import org.mevenide.idea.util.ui.CustomFormsComponentFactory;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;
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
class ScmPanel extends AbstractPomLayerPanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ScmPanel.class);

    private final JTextField anonynousConnection = new JTextField();
    private final JTextField developerConnection = new JTextField();
    private final JTextField browseUrl = new JTextField();
    private final JPanel versionsPanel;
    private final JPanel branchesPanel;

    public ScmPanel(final Project pProject, final Document pIdeaDocument) {
        super(pProject, pIdeaDocument);
        bindComponents();

        final VersionsTableModel versionsModel = new VersionsTableModel(project, editorDocument);
        final BranchesTableModel branchesModel = new BranchesTableModel(project, editorDocument);
        final JPanel versionsCRUDPanel = new CRUDTablePanel(project, editorDocument, versionsModel);
        final JPanel branchesCRUDPanel = new CRUDTablePanel(project, editorDocument, branchesModel);
        versionsPanel = new LabeledPanel(RES.get("versions.desc"), versionsCRUDPanel);
        branchesPanel = new LabeledPanel(RES.get("branches.desc"), branchesCRUDPanel);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        UIUtils.installBorder(versionsPanel, 5, 0, 10, 0);
        UIUtils.installBorder(branchesPanel, 10, 0, 0, 0);

        final Field[] fields = this.getClass().getDeclaredFields();
        for(final Field field : fields) {
            try {
                final Object value = field.get(this);
                if(value != null && value instanceof Component) {
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
        add(new SplitPanel<JPanel,JPanel>(versionsPanel, branchesPanel), c);
    }

    private void bindComponents() {
        synchronized (this) {
            final XmlPsiDocumentBinder binder = new XmlPsiDocumentBinder(project, editorDocument);

            binder.bind(anonynousConnection, "repository/connection");
            binder.bind(developerConnection, "repository/developerConnection");
            binder.bind(browseUrl, "repository/url");
        }
    }

    private JPanel createRepositoryPanel() {
        final FormLayout layout = new FormLayout("right:min, 2dlu, fill:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setComponentFactory(new CustomFormsComponentFactory());

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
