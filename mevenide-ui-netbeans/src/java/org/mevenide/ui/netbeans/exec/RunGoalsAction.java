/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Milos Kleint (ca206216@tiscali.cz).  All rights
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.ui.netbeans.MavenProjectCookie;
import org.mevenide.ui.netbeans.MavenSettings;
import org.openide.ErrorManager;
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

        // removeNotify not useful--might be called before action is invoked

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
            return (String) targets.get (index);
        }

        public HelpCtx getHelpCtx (int index) {
            return new HelpCtx ("org.mevenide.ui.netbeans"); // NOI18N
        }

        public void performActionAt (final int index) {
            // #16720 part 2: don't do this in the event thread...
            final String goal = (String) targets.get (index);
            MavenExecutor templateexec = (MavenExecutor)MavenSettings.getDefault().getExecutor();
            final boolean nobanner = templateexec.isNoBanner();
            final boolean offline = templateexec.isOffline();
            Node[] nds = action.getActivatedNodes();
            if (nds.length == 0) 
            {
                return;
            }
            final DataObject obj = (DataObject)nds[0].getCookie(DataObject.class);
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
                                    targets.add(plugins[i] + ":" + goals[j]);
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
            String str = MavenSettings.getDefault().getTopGoals();
            if (str == null) return;
            StringTokenizer tok = new StringTokenizer(str, " ", false);
            if (!tok.hasMoreTokens()) return;
            while (tok.hasMoreTokens())
            {
                targets.add(tok.nextToken());
            }
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

}
