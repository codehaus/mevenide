/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.ui.netbeans.GoalsGrabberProvider;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.mevenide.ui.netbeans.MavenSettings;
import org.mevenide.ui.netbeans.goals.GoalUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.loaders.DataObject;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.SystemAction;

/** Action to run favourite in a maven project. combines the favourite items from 
 * mavensettings and adds the local maven.xml goals.
 */
public class RunGoalsAction extends CookieAction implements Presenter.Popup {

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
    private static final class SpecialSubMenu extends Actions.SubMenu {

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
    private static final class ActSubMenuModel implements Actions.SubMenuModel {

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
                GPanel panel = new GPanel(GoalUtils.createProjectGoalsProvider(cook.getProjectFile().getAbsolutePath()));
                DialogDescriptor desc = new DialogDescriptor(panel, NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.dialog.title"));
                Object[] options = new Object[] {
                    new JButton(NbBundle.getMessage(RunGoalsAction.class, "RunGoalsAction.executeButton")),
                    NotifyDescriptor.CANCEL_OPTION
                };
                desc.setOptions(options);
                desc.setClosingOptions(options);
                Object retValue = DialogDisplayer.getDefault().notify(desc);
                if (!retValue.equals(options[0]))
                {
                    return;
                }
                mgoal = panel.getGoalsToExecute();
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
        public GPanel(GoalsGrabberProvider provider)
        {
            super();
            setLayout(new GridBagLayout());
            GridBagConstraints con = new GridBagConstraints();
            con.insets = new Insets(12, 12, 12, 12);
            panel = new CustomGoalsPanel(provider);
            add(panel, con);
        }
        
        public String getGoalsToExecute()
        {
            return panel.getGoalsToExecute();
        }
    }
    
}
