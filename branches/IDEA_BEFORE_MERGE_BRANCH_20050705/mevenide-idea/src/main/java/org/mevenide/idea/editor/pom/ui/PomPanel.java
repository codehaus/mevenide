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
import java.util.concurrent.Callable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.editor.pom.PomFileEditorState;
import org.mevenide.idea.editor.pom.PomFileEditorStateHandler;
import org.mevenide.idea.editor.pom.ui.build.SourcesPanel;
import org.mevenide.idea.editor.pom.ui.build.TestingPanel;
import org.mevenide.idea.editor.pom.ui.dependencies.DependenciesPanel;
import org.mevenide.idea.editor.pom.ui.mailingLists.MailingListsPanel;
import org.mevenide.idea.editor.pom.ui.reports.ReportsPanel;
import org.mevenide.idea.editor.pom.ui.scm.ScmPanel;
import org.mevenide.idea.editor.pom.ui.team.ContributorsPanel;
import org.mevenide.idea.editor.pom.ui.team.DevelopersPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.LabeledPanel;

/**
 * This panel displays a single POM layer.
 *
 * @author Arik
 */
public class PomPanel extends AbstractPomLayerPanel implements PomFileEditorStateHandler {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(PomPanel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PomPanel.class);

    /**
     * The PSI project model.
     */
    private final PsiProject project;

    /**
     * Tabbed pane.
     */
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

    /**
     * Creates an instance for the given POM file.
     */
    public PomPanel(final PsiProject pProject) {
        project = pProject;
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        tabs.add("General", new LabeledPanel(RES.get("general.info.desc"),
                                             new GeneralInfoPanel(project)));

        tabs.add("Mailing lists", new TabPlaceHolder(RES.get("mail.lists.desc"),
                                                     new Callable<JComponent>() {
                                                         public JComponent call() throws Exception {
                                                             return new MailingListsPanel(project);
                                                         }
                                                     }));

        tabs.add("Developers", new TabPlaceHolder(RES.get("developers.desc"),
                                                  new Callable<JComponent>() {
                                                      public JComponent call() throws Exception {
                                                          return new DevelopersPanel(project);
                                                      }
                                                  }));

        tabs.add("Contributors", new TabPlaceHolder(RES.get("contributors.desc"),
                                                    new Callable<JComponent>() {
                                                        public JComponent call() throws Exception {
                                                            return new ContributorsPanel(project);
                                                        }
                                                    }));

        tabs.add("SCM", new TabPlaceHolder(RES.get("scm.desc"),
                                           new Callable<JComponent>() {
                                               public JComponent call() throws Exception {
                                                   return new ScmPanel(project);
                                               }
                                           }));

        tabs.add("Dependencies", new TabPlaceHolder(RES.get("dep.list.desc"),
                                                    new Callable<JComponent>() {
                                                        public JComponent call() throws Exception {
                                                            return new DependenciesPanel(project);
                                                        }
                                                    }));

        tabs.add("Source Code", new TabPlaceHolder(RES.get("src.desc"),
                                                   new Callable<JComponent>() {
                                                       public JComponent call() throws Exception {
                                                           return new SourcesPanel(project);
                                                       }
                                                   }));

        tabs.add("Testing", new TabPlaceHolder(RES.get("testing.desc"),
                                               new Callable<JComponent>() {
                                                   public JComponent call() throws Exception {
                                                       return new TestingPanel(project);
                                                   }
                                               }));

        tabs.add("Deployment", new TabPlaceHolder(RES.get("deployment.desc"),
                                                  new Callable<JComponent>() {
                                                      public JComponent call() throws Exception {
                                                          return new DeploymentPanel(project);
                                                      }
                                                  }));

        tabs.add("Reports", new TabPlaceHolder(RES.get("reports.desc"),
                                               new Callable<JComponent>() {
                                                   public JComponent call() throws Exception {
                                                       return new ReportsPanel(project);
                                                   }
                                               }));
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

    private class TabPlaceHolder extends JPanel {
        private final Callable<? extends JComponent> initializer;
        private final String label;
        private JComponent component = null;

        public TabPlaceHolder(final Callable<? extends JComponent> pInitializer) {
            this(null, pInitializer);
        }

        public TabPlaceHolder(final String pLabel,
                              final Callable<? extends JComponent> pInitializer) {
            super(new BorderLayout());
            label = pLabel;
            initializer = pInitializer;
            tabs.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent pEvent) {
                    if (component == null && tabs.getSelectedComponent() == TabPlaceHolder.this)
                        try {
                            component = initializer.call();
                            if (label != null && label.trim().length() > 0)
                                component = new LabeledPanel(label, component);
                            add(component, BorderLayout.CENTER);
                        }
                        catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                }
            });
        }
    }
}
