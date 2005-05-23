/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.PomFileEditorState;
import org.mevenide.idea.editor.pom.PomFileEditorStateHandler;
import org.mevenide.idea.editor.pom.ui.layer.model.ContributorsTableModel;
import org.mevenide.idea.editor.pom.ui.layer.model.DependenciesTableModel;
import org.mevenide.idea.editor.pom.ui.layer.model.DevelopersTableModel;
import org.mevenide.idea.editor.pom.ui.layer.model.MailingListsTableModel;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * This panel displays a single POM layer.
 * 
 * @author Arik
 */
public class PomPanel extends AbstractPomLayerPanel implements PomFileEditorStateHandler {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PomPanel.class);

    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    private final JPanel generalInfoPanel = new GeneralInfoPanel(project, editorDocument);
    private final JPanel mailingListsPanel = new CRUDTablePanel(project, editorDocument, new MailingListsTableModel(project, editorDocument));
    private final JPanel depsPanel = new CRUDTablePanel(project, editorDocument, new DependenciesTableModel(project, editorDocument));
    private final JPanel deploymentPanel = new DeploymentPanel(project, editorDocument);
    private final JPanel teamPanel = createTeamPanel(project, editorDocument);
    private final JPanel scmPanel = new ScmPanel(project, editorDocument);

    public PomPanel(final com.intellij.openapi.project.Project pProject,
                         final Document pPomDocument) {
        super(pProject, pPomDocument);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        final String depsLabel = RES.get("dep.list.desc");
        final String mailLabel = RES.get("mail.lists.desc");

        final LabeledPanel mailingListsLabelPanel = new LabeledPanel(mailLabel, mailingListsPanel);
        final LabeledPanel depsLabelPanel = new LabeledPanel(depsLabel, depsPanel);
        UIUtils.installBorder(generalInfoPanel);
        UIUtils.installBorder(mailingListsLabelPanel);
        UIUtils.installBorder(depsLabelPanel);
        UIUtils.installBorder(deploymentPanel);
        UIUtils.installBorder(scmPanel);

        tabs.add("General", generalInfoPanel);
        tabs.add("Mailing lists", mailingListsLabelPanel);
        tabs.add("Team", teamPanel);
        tabs.add("SCM", scmPanel);
        tabs.add("Dependencies", depsLabelPanel);
        tabs.add("Deployment", deploymentPanel);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    public void getState(final PomFileEditorState pState) {
        pState.setSelectedTabIndex(tabs.getSelectedIndex());

        final Component component = tabs.getSelectedComponent();
        if(component instanceof PomFileEditorStateHandler) {
            PomFileEditorStateHandler handler = (PomFileEditorStateHandler) component;
            handler.getState(pState);
        }
    }

    public void setState(final PomFileEditorState pState) {
        tabs.setSelectedIndex(pState.getSelectedTabIndex());

        final Component component = tabs.getSelectedComponent();
        if(component instanceof PomFileEditorStateHandler) {
            PomFileEditorStateHandler handler = (PomFileEditorStateHandler) component;
            handler.setState(pState);
        }
    }

    private static JPanel createTeamPanel(final Project pProject, final Document pDocument) {
        final DevelopersTableModel developersModel = new DevelopersTableModel(pProject, pDocument);
        final DevelopersTableModel contributorsModel = new ContributorsTableModel(pProject, pDocument);
        final JPanel developersPanel = new CRUDTablePanel(pProject, pDocument, developersModel);
        final JPanel contributorsPanel = new CRUDTablePanel(pProject, pDocument, contributorsModel);
        final LabeledPanel developersLabelPanel = new LabeledPanel(RES.get("developers.desc"), developersPanel);
        final LabeledPanel contributorsLabelPanel = new LabeledPanel(RES.get("contributors.desc"), contributorsPanel);
        UIUtils.installBorder(developersLabelPanel);
        UIUtils.installBorder(contributorsLabelPanel);
        return new SplitPanel<JPanel,JPanel>(developersLabelPanel, contributorsLabelPanel);
    }

}
