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
package org.mevenide.idea.editor.pom.ui;

import java.awt.*;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.PomFileEditorState;
import org.mevenide.idea.editor.pom.PomFileEditorStateHandler;
import org.mevenide.idea.editor.pom.ui.build.SourcesPanel;
import org.mevenide.idea.editor.pom.ui.dependencies.DependenciesPanel;
import org.mevenide.idea.editor.pom.ui.mailingLists.MailingListsPanel;
import org.mevenide.idea.editor.pom.ui.reports.ReportsPanel;
import org.mevenide.idea.editor.pom.ui.scm.ScmPanel;
import org.mevenide.idea.editor.pom.ui.team.ContributorsPanel;
import org.mevenide.idea.editor.pom.ui.team.DevelopersPanel;
import org.mevenide.idea.editor.pom.ui.tests.TestingPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.UIUtils;

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

    /**
     * Tabbed pane.
     */
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

    /**
     * * Panels ***********************************************************
     */

    private final JPanel generalInfoPanel;
    private final JPanel mailingListsPanel;
    private final JPanel depsPanel;
    private final JPanel deploymentPanel;
    private final JPanel developersPanel;
    private final JPanel contributorsPanel;
    private final JPanel scmPanel;
    private final JPanel sourcesPanel;
    private final JPanel testsPanel;
    private final JPanel reportsPanel;

    /**
     * Creates an instance for the given POM file.
     */
    public PomPanel(final PsiProject pProject) {
        generalInfoPanel = new GeneralInfoPanel(pProject);
        mailingListsPanel = new MailingListsPanel(pProject);
        depsPanel = new DependenciesPanel(pProject);
        deploymentPanel = new DeploymentPanel(pProject);
        developersPanel = new DevelopersPanel(pProject);
        contributorsPanel = new ContributorsPanel(pProject);
        scmPanel = new ScmPanel(pProject);
        sourcesPanel = new SourcesPanel(pProject);
        testsPanel = new TestingPanel(pProject);
        reportsPanel = new ReportsPanel(pProject.getReports());

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        final String depsLabel = RES.get("dep.list.desc");
        final String mailLabel = RES.get("mail.lists.desc");

        final LabeledPanel mailingListsLabelPanel = new LabeledPanel(mailLabel,
                                                                     mailingListsPanel);
        final LabeledPanel depsLabelPanel = new LabeledPanel(depsLabel, depsPanel);
        UIUtils.installBorder(generalInfoPanel);
        UIUtils.installBorder(mailingListsLabelPanel);
        UIUtils.installBorder(depsLabelPanel);
        UIUtils.installBorder(deploymentPanel);
        UIUtils.installBorder(developersPanel);
        UIUtils.installBorder(contributorsPanel);
        UIUtils.installBorder(scmPanel);
        UIUtils.installBorder(sourcesPanel);
        UIUtils.installBorder(testsPanel);
        UIUtils.installBorder(reportsPanel);

        tabs.add("General", generalInfoPanel);
        tabs.add("Mailing lists", mailingListsLabelPanel);
        tabs.add("Developers", developersPanel);
        tabs.add("Contributors", contributorsPanel);
        tabs.add("SCM", scmPanel);
        tabs.add("Dependencies", depsLabelPanel);
        tabs.add("Source Code", sourcesPanel);
        tabs.add("Testing", testsPanel);
        tabs.add("Deployment", deploymentPanel);
        tabs.add("Reports", reportsPanel);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    public void getState(final PomFileEditorState pState) {
        pState.setSelectedTabIndex(tabs.getSelectedIndex());

        final Component component = tabs.getSelectedComponent();
        if (component instanceof PomFileEditorStateHandler) {
            PomFileEditorStateHandler handler = (PomFileEditorStateHandler) component;
            handler.getState(pState);
        }
    }

    public void setState(final PomFileEditorState pState) {
        tabs.setSelectedIndex(pState.getSelectedTabIndex());

        final Component component = tabs.getSelectedComponent();
        if (component instanceof PomFileEditorStateHandler) {
            PomFileEditorStateHandler handler = (PomFileEditorStateHandler) component;
            handler.setState(pState);
        }
    }
}
