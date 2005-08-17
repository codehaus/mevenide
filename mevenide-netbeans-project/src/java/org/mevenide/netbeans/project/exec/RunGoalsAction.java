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
package org.mevenide.netbeans.project.exec;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.MavenSettings;
import org.mevenide.netbeans.project.goals.GoalUtils;
import org.mevenide.netbeans.project.goals.GoalsGrabberProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.Presenter;

/** Action to run favourite in a maven project. combines the favourite items from
 * mavensettings and adds the local maven.xml goals.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class RunGoalsAction extends AbstractAction implements Presenter.Popup {
    
    private static Log log = LogFactory.getLog(RunGoalsAction.class);
    
    private static final int MAX_ITEMS_IN_POPUP = 17;
    private static final int MAX_LENGTH_OF_ITEM = 30;
    private MavenProject project;
    
    public RunGoalsAction(MavenProject proj) {
        putValue(Action.NAME, NbBundle.getMessage(RunGoalsAction.class, "LBL_RunGoalsAction"));
        project = proj;
    }
    
    public javax.swing.JMenuItem getPopupPresenter() {
        return new SpecialSubMenu(this, new ActSubMenuModel(project), true);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
    }
    
    /**
     * copied from AntTargetAction. @thanks to Jesse Glick.
     */
    private static final class SpecialSubMenu extends Actions.SubMenu {
        
        private final ActSubMenuModel model;
        
        SpecialSubMenu(Action action, ActSubMenuModel mdl, boolean popup) {
            super(action, mdl, popup);
            model = mdl;
            model.addNotify();
        }
        
        public void addNotify() {
            super.addNotify();
        }
        
    }
    
    /** Model to use for the submenu.
     */
    private static final class ActSubMenuModel implements Actions.SubMenuModel {
        
        private List targets = null; // List<String>
        private MavenProject project = null;
        
        
        ActSubMenuModel(MavenProject proj) {
            project = proj;
        }
        
        public int getCount() {
            if (targets == null) {
                return 0;
            }
            return targets.size();
        }
        
        public String getLabel(int index) {
            ItemWrapper item = (ItemWrapper)targets.get(index);
            return item == null ? null : item.getGoals();
        }
        
        public HelpCtx getHelpCtx(int index) {
            return new HelpCtx("org.mevenide.ui.netbeans"); // NOI18N
        }
        
        public void performActionAt(final int index) {
            final ItemWrapper item = (ItemWrapper) targets.get(index);
            if (item == null) {
                return;
            }
            String mgoal = item.getGoals();
            RunConfig config = null;
            log.debug("item=" + item.getGoals() + " of type " + item.getType()); //NOI18N
            if (item.getType() == ACTION_SHOW_CUSTOM_DIALOG) {
                GoalsGrabberProvider goalProvider = GoalUtils.createProjectGoalsProvider(project.getContext(), 
                        project.getLocFinder());
                GPanel panel = new GPanel(project, goalProvider);
                DialogDescriptor desc = new DialogDescriptor(panel, 
                        NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.dialog.title"));
                Object[] options = new Object[] {
                    new JButton(NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.executeButton")),
                    NotifyDescriptor.CANCEL_OPTION
                };
                desc.setOptions(options);
                desc.setClosingOptions(options);
                desc.setValue(options[0]);
                Object retValue = DialogDisplayer.getDefault().notify(desc);
                if (!retValue.equals(options[0]) || panel.getGoalsToExecute().trim().length() == 0) {
                    return;
                }
                mgoal = panel.getGoalsToExecute();
                if (panel.doAddToFavourites()) {
                    doValidateAndAddToFavs(goalProvider, mgoal);
                }
                config = panel;
            } else {
                config = new DefaultRunConfig();
            }
            final String goal = mgoal;
            final RunConfig fConfig = config;
            // now execute in different thread..
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    ActionProviderImpl impl = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
                    impl.runGoal(goal, project.getLookup(), fConfig);
                }
            });
        }
        
        private void doValidateAndAddToFavs(GoalsGrabberProvider provider, String goals) {
            try {
                IGoalsGrabber grabber = provider.getGoalsGrabber();
                StringTokenizer token = new StringTokenizer(goals, " ");
                while (token.hasMoreTokens()) {
                    String goal = token.nextToken();
                    String origin = grabber.getOrigin(goal);
                    if (origin == null || IGoalsGrabber.ORIGIN_PROJECT.equals(origin)) {
                        NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                              NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.warning1", goal));
                        
                        DialogDisplayer.getDefault().notify(message);
                        return;
                    }
                }
                MavenSettings settings = MavenSettings.getDefault();
                String[] oldGoals = settings.getTopGoals();
                String[] newGoals = new String[oldGoals.length + 1];
                for (int i = 0; i < oldGoals.length; i++) {
                    newGoals[i] = oldGoals[i];
                }
                newGoals[oldGoals.length] = goals;
                settings.setTopGoals(newGoals);
                if (settings.isShowAddFavouriteHint()) {
                    NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                               NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.hint1"));
                    //TODO have a checkbox .. "show hint next time".. for now, just display once.
                    DialogDisplayer.getDefault().notify(message);
                    MavenSettings.getDefault().setShowAddFavouriteHint(false);
                }
            } catch (Exception exc) {
                log.error("Cannot create goals grabber", exc);
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, exc);
            }
        }
        
        void addNotify() {
            targets = Collections.EMPTY_LIST;
            if (project.getOriginalMavenProject() == null) {
                return;
            }
            targets = new ArrayList(15);
            File[] fls = project.getContext().getPOMContext().getProjectFiles();
            for (int x = 0; x < fls.length; x++) {
                File mavenxml = new File(fls[x].getParentFile(), "maven.xml");
                if (mavenxml.exists()) {
                    try {
                        ProjectGoalsGrabber grabber = new ProjectGoalsGrabber();
                        grabber.setMavenXmlFile(mavenxml.getAbsolutePath());
                        grabber.refresh();
                        String[] plugins = grabber.getPlugins();
                        if (plugins != null) {
                            for (int i =0; i < plugins.length; i++) {
                                String[] goals = grabber.getGoals(plugins[i]);
                                if (goals != null) {
                                    for (int j =0; j < goals.length; j++) {
                                        if ("(default)".equals(goals[j])) {
                                            targets.add(new ItemWrapper(plugins[i]));
                                        } else {
                                            targets.add(new ItemWrapper(plugins[i] + ":" + goals[j]));
                                        }
                                    }
                                }
                            }
                            targets.add(null);
                        }
                    } catch (Exception ioe) {
                        log.error("Error loading project-specific goals",ioe);
                    }
                }
            }
            String[] str = MavenSettings.getDefault().getTopGoals();
            if (str != null) {
                for (int i = 0; i < str.length; i++) {
                    if (targets.size() < MAX_ITEMS_IN_POPUP) {
                        targets.add(new ItemWrapper(
                               str[i].length() > MAX_LENGTH_OF_ITEM 
                             ? str[i].substring(0, MAX_LENGTH_OF_ITEM - 3) + "..."
                             : str[i]));
                    }
                }
            }
            targets.add(new ItemWrapper(
                    NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.moreGoals"), 
                    ACTION_SHOW_CUSTOM_DIALOG));
            // In fact we should ensure there are >1 items (workaround for
            // undesired behavior of Actions.SubMenu):
            if (targets.size() == 1) {
                // The extra separator will not actually be displayed:
                targets.add(null);
            }
        }
        
        public synchronized void addChangeListener(ChangeListener l) {
        }
        
        public synchronized void removeChangeListener(ChangeListener l) {
        }
        
    }
    
    private static final int ACTION_RUN = 0;
    private static final int ACTION_SHOW_CUSTOM_DIALOG = 1;
    
    private static class ItemWrapper {
        private String goals;
        private int actionType;
        public ItemWrapper(String gls) {
            goals = gls;
            actionType = ACTION_RUN;
            
        }
        public ItemWrapper(String gls, int actionTp) {
            this(gls);
            actionType = actionTp;
        }
        
        public String getGoals() {
            return goals;
        }
        
        public int getType() {
            return actionType;
        }
        
        
    }
    
    /** a class that puts the CustomGoalsPanel into correct constraints
     */
    private static class GPanel extends JPanel implements RunConfig {
        private RunGoalsPanel panel;
        private JCheckBox cbAdd;
        public GPanel(MavenProject project, GoalsGrabberProvider provider) {
            super();
            setLayout(new GridBagLayout());
            GridBagConstraints con = new GridBagConstraints();
            con.insets = new Insets(12, 12, 6, 12);
            con.anchor = GridBagConstraints.NORTHWEST;
            panel = new RunGoalsPanel(project, provider);
            add(panel, con);
            
            cbAdd = new JCheckBox();
            cbAdd.setText(NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.cbAdd.text"));
            cbAdd.setToolTipText(NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.cbAdd.tooltip"));
            cbAdd.setMnemonic(NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.cbAdd.mnemonic").charAt(0));
            con = new GridBagConstraints();
            con.insets = new Insets(0, 12, 12, 12);
            con.gridx = 0;
            con.gridy = 1;
            con.anchor = GridBagConstraints.NORTHWEST;
            add(cbAdd, con);
        }
        
        public String getGoalsToExecute() {
            return panel.getGoalsToExecute();
        }
        
        public boolean doAddToFavourites() {
            return cbAdd.isSelected();
        }
        
        public boolean isOffline() {
            return panel.isOffline();
        }

        public boolean isDebug() {
            return panel.isDebug();
        }
        public boolean isExceptions() {
            return panel.isExceptions();
        }
        public boolean isNoBanner() {
            return panel.isNoBanner();
        }
        public boolean isNonverbose() {
            return panel.isNonverbose();
        }
        public String getMavenHome() {
            return panel.getMavenHome();
        }
        public String getMavenLocalHome() {
            return panel.getMavenLocalHome();
        }        
    }
    
}
