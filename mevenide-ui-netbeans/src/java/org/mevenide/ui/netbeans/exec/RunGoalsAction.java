/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.netbeans.exec;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;
import org.mevenide.ui.netbeans.GoalsGrabberProvider;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.mevenide.ui.netbeans.MavenSettings;
import org.mevenide.ui.netbeans.goals.GoalUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions.SubMenu;
import org.openide.awt.Actions.SubMenuModel;
import org.openide.loaders.DataObject;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.Presenter.Popup;

/** Action to run favourite in a maven project. combines the favourite items from 
 * mavensettings and adds the local maven.xml goals.
 */
public class RunGoalsAction extends CookieAction implements Popup {

    private static Log log = LogFactory.getLog(RunGoalsAction.class);

    private static final int MAX_ITEMS_IN_POPUP = 17;
    private static final int MAX_LENGTH_OF_ITEM = 30;
    
    public JMenuItem getPopupPresenter() {
        return new SpecialSubMenu (this, new ActSubMenuModel (this), true);
    }

    protected int mode () {
        return MODE_EXACTLY_ONE;
    }
    
    protected Class[] cookieClasses () {
        return new Class[] { MavenProjectCookie.class };
    }
    
    protected void performAction (Node[] activatedNodes) {
        // do nothing, should not happen
    }

    public String getName() {
        return NbBundle.getMessage(RunGoalsAction.class, "LBL_RunGoalsAction");
    }

    public HelpCtx getHelpCtx () {
        return new HelpCtx ("org.mevenide.ui.netbeans"); // NOI18N
    }
    
/**
 * copied from AntTargetAction. @thanks to Jesse Glick.
 */ 
    private static final class SpecialSubMenu extends SubMenu {

        private final ActSubMenuModel model;

        SpecialSubMenu (SystemAction action, ActSubMenuModel model, boolean popup) {
            super (action, model, popup);
            this.model = model;
        }

        public void addNotify () {
            model.addNotify ();
            super.addNotify ();
        }

    }

    /** Model to use for the submenu.
    */
    private static final class ActSubMenuModel implements SubMenuModel {

        private List targets = null; // List<String>
        private MavenProjectCookie project = null;

        private final NodeAction action;
        
        ActSubMenuModel (NodeAction action) {
            this.action = action;
        }

        public int getCount () {
            // Apparently when >1 Ant script is selected and you right-click,
            // it gets here though targets==null (as it should since the action
            // should not be enabled!). Not clear why this happens.
            if (targets == null) return 0;
            return targets.size ();
        }

        public String getLabel (int index) {
            ItemWrapper item = (ItemWrapper)targets.get (index);
            return item == null ? null : item.getGoals();
        }

        public HelpCtx getHelpCtx (int index) {
            return new HelpCtx ("org.mevenide.ui.netbeans"); // NOI18N
        }

        public void performActionAt (final int index) {
            final ItemWrapper item = (ItemWrapper) targets.get (index);
            if (item == null) return;
            String mgoal = item.getGoals();
            MavenExecutor templateexec = (MavenExecutor)MavenSettings.getDefault().getExecutor();
            final boolean nobanner = templateexec.isNoBanner();
            final boolean offline = templateexec.isOffline();
            Node[] nds = action.getActivatedNodes();
            if (nds.length == 0) 
            {
                return;
            }
            log.debug("item=" + item.getGoals() + " of type " + item.getType()); //NOI18N
            final DataObject obj = (DataObject)nds[0].getCookie(DataObject.class);
            if (item.getType() == ACTION_SHOW_CUSTOM_DIALOG)
            {
                final MavenProjectCookie cook = (MavenProjectCookie)obj.getCookie(MavenProjectCookie.class);
                GoalsGrabberProvider goalProvider = GoalUtils.createProjectGoalsProvider(cook.getProjectFile().getAbsolutePath());
                GPanel panel = new GPanel(goalProvider);
                DialogDescriptor desc = new DialogDescriptor(panel, NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.dialog.title"));
                Object[] options = new Object[] {
                    new JButton(NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.executeButton")),
                    NotifyDescriptor.CANCEL_OPTION
                };
                desc.setOptions(options);
                desc.setClosingOptions(options);
                desc.setValue(options[0]);
                Object retValue = DialogDisplayer.getDefault().notify(desc);
                if (!retValue.equals(options[0]) || panel.getGoalsToExecute().trim().length() == 0)
                {
                    return;
                }
                mgoal = panel.getGoalsToExecute();
                if (panel.doAddToFavourites())
                {
                    doValidateAndAddToFavs(goalProvider, mgoal);
                }
            }
            final String goal = mgoal;
            // now execute in different thread..
            RequestProcessor.postRequest(new Runnable() {
                public void run() {
                    try {
                        MavenExecutor exec = new MavenExecutor();
                        exec.setGoal(goal);
                        exec.setNoBanner(nobanner);
                        exec.setOffline(offline);
                        exec.execute(obj);
                    } catch (IOException ioe) {
                        //TODO
                        ErrorManager.getDefault().notify(ioe);
                    }
                }
            });
        }
        
        private void doValidateAndAddToFavs(GoalsGrabberProvider provider, String goals)
        {
            try {
                IGoalsGrabber grabber = provider.getGoalsGrabber();
                StringTokenizer token = new StringTokenizer(goals, " ");
                while (token.hasMoreTokens())
                {
                    String goal = token.nextToken();
                    String origin = grabber.getOrigin(goal);
                    if (origin == null || IGoalsGrabber.ORIGIN_PROJECT.equals(origin))
                    {
                        NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                            NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.warning1", goal));
                        
                        DialogDisplayer.getDefault().notify(message);
                        return;
                    }
                }
                MavenSettings settings = MavenSettings.getDefault();
                String[] oldGoals = settings.getTopGoals();
                String[] newGoals = new String[oldGoals.length + 1];
                for (int i = 0; i < oldGoals.length; i++)
                {
                    newGoals[i] = oldGoals[i];
                }
                newGoals[oldGoals.length] = goals;
                settings.setTopGoals(newGoals);
                if (settings.isShowAddFavouriteHint())
                {
                        NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                            NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.hint1"));
                        //TODO have a checkbox .. "show hint next time".. for now, just display once.
                        DialogDisplayer.getDefault().notify(message);
                        MavenSettings.getDefault().setShowAddFavouriteHint(false);
                }
            } catch (Exception exc)
            {
                log.error("Cannot create goals grabber", exc);
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, exc);
            }
        }

        void addNotify () {
            project = null;
            targets = null;
            Node[] nodes = action.getActivatedNodes ();
            if (nodes.length != 1) return;
            project = (MavenProjectCookie) nodes[0].getCookie (MavenProjectCookie.class);
            if (project == null) return;
            targets = Collections.EMPTY_LIST;
            if (project.getMavenProject() == null) return;
            targets = new ArrayList(15);
            if (project.getMavenCustomFile() != null &&
                project.getMavenCustomFile().exists())
            {
                try
                {
                    ProjectGoalsGrabber grabber = new ProjectGoalsGrabber();
                    grabber.setMavenXmlFile(project.getMavenCustomFile().getAbsolutePath());
                    grabber.refresh();
                    String[] plugins = grabber.getPlugins();
                    if (plugins != null)
                    {
                        for (int i =0; i < plugins.length; i++)
                        {
                            String[] goals = grabber.getGoals(plugins[i]);
                            if (goals != null)
                            {
                                for (int j =0; j < goals.length; j++)
                                {
                                    targets.add(new ItemWrapper(plugins[i] + ":" + goals[j]));
                                }
                            }
                        }
                        targets.add(null);
                    }
                } catch (Exception ioe)
                {
                    log.error("Error loading project-specific goals",ioe);
                }
            }
            String[] str = MavenSettings.getDefault().getTopGoals();
            if (str != null)
            {
                for (int i = 0; i < str.length; i++)
                {
                        if (targets.size() < MAX_ITEMS_IN_POPUP) {
                            targets.add(new ItemWrapper(
                                    str[i].length() > MAX_LENGTH_OF_ITEM ? str[i].substring(0, MAX_LENGTH_OF_ITEM - 3) + "..." : str[i]));
                        }
                }
            }
            targets.add(new ItemWrapper(NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.moreGoals"), ACTION_SHOW_CUSTOM_DIALOG));
            // In fact we should ensure there are >1 items (workaround for
            // undesired behavior of Actions.SubMenu):
            if (targets.size () == 1) {
                // The extra separator will not actually be displayed:
                targets.add (null);
            }
        }

        public synchronized void addChangeListener (ChangeListener l) {
        }

        public synchronized void removeChangeListener (ChangeListener l) {
        }

    }
    
    private static final int ACTION_RUN = 0;
    private static final int ACTION_SHOW_CUSTOM_DIALOG = 1;
    
    private static class ItemWrapper
    {
        private String goals;
        private int actionType;
        public ItemWrapper(String goals)
        {
            this.goals = goals;
            actionType = ACTION_RUN;
            
        }
        public ItemWrapper(String goals, int actionType)
        {
            this(goals);
            this.actionType = actionType;
        }
        
        public String getGoals()
        {
            return goals;
        }
        
        public int getType()
        {
            return actionType;
        }
        
        
    }

    /** a class that puts the CustomGoalsPanel into correct constraints
     */
    private static class GPanel extends JPanel
    {
        private CustomGoalsPanel panel;
        private JCheckBox cbAdd;
        public GPanel(GoalsGrabberProvider provider)
        {
            super();
            setLayout(new GridBagLayout());
            GridBagConstraints con = new GridBagConstraints();
            con.insets = new Insets(12, 12, 6, 12);
            con.anchor = GridBagConstraints.NORTHWEST;
            panel = new CustomGoalsPanel(provider);
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
        
        public String getGoalsToExecute()
        {
            return panel.getGoalsToExecute();
        }
        
        public boolean doAddToFavourites()
        {
            return cbAdd.isSelected();
        }
    }
    
}
