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

import com.intellij.psi.xml.XmlFile;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.PomFileEditorState;
import org.mevenide.idea.editor.pom.PomFileEditorStateHandler;
import org.mevenide.idea.editor.pom.ui.layer.build.SourcesPanel;
import org.mevenide.idea.editor.pom.ui.layer.dependencies.DependenciesPanel;
import org.mevenide.idea.editor.pom.ui.layer.mailingLists.MailingListsPanel;
import org.mevenide.idea.editor.pom.ui.layer.reports.ReportsPanel;
import org.mevenide.idea.editor.pom.ui.layer.scm.ScmPanel;
import org.mevenide.idea.editor.pom.ui.layer.team.TeamPanel;
import org.mevenide.idea.editor.pom.ui.layer.tests.TestingPanel;
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

    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

    private final JPanel generalInfoPanel = new GeneralInfoPanel(file);
    private final JPanel mailingListsPanel = new MailingListsPanel(file);
    private final JPanel depsPanel = new DependenciesPanel(file);
    private final JPanel deploymentPanel = new DeploymentPanel(file);
    private final JPanel teamPanel = new TeamPanel(file);
    private final JPanel scmPanel = new ScmPanel(file);
    private final JPanel sourcesPanel = new SourcesPanel(file);
    private final JPanel testsPanel = new TestingPanel(file);
    private final JPanel reportsPanel = new ReportsPanel(file);

    public PomPanel(final XmlFile pFile) {
        super(pFile);

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
        UIUtils.installBorder(sourcesPanel);
        UIUtils.installBorder(testsPanel);
        UIUtils.installBorder(reportsPanel);

        tabs.add("General", generalInfoPanel);
        tabs.add("Mailing lists", mailingListsLabelPanel);
        tabs.add("Team", teamPanel);
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
