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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.ui.netbeans.GoalsGrabberProvider;
import org.mevenide.ui.netbeans.goals.GoalNameCookie;
import org.mevenide.ui.netbeans.goals.GoalsRootNode;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Children.Array;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class CustomGoalsPanel extends JPanel
{
    private static final Log logger = LogFactory.getLog(CustomGoalsPanel.class);
    /**
     * Action name, fired when the text in the goals textfield changes.
     */
    public static final String ACTION_GOALS_CHANGED = "GoalsChanged"; //NOI18N
    
    private GoalsGrabberProvider provider;
    private List actionListeners = new ArrayList();
    
    /** Creates new form CustomGoalsPanel */
    public CustomGoalsPanel(GoalsGrabberProvider goalsProvider)
    {
        initComponents();
        provider = goalsProvider;
        tvGoals.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tvGoals.setRootVisible(false);
        txtEnter.getDocument().addDocumentListener(new DocListener());
    }
    
    public void addActionListener(ActionListener listener)
    {
        synchronized (actionListeners) {
            actionListeners.add(listener);
        }
    }
    public void removeActionListener(ActionListener listener)
    {
        synchronized (actionListeners) {
            actionListeners.remove(listener);
        }
    }
    
    protected void fireActionEvent(ActionEvent event)
    {
        if (actionListeners.size() > 0) {
            List listeners = new ArrayList();
            synchronized (actionListeners) {
                listeners.addAll(actionListeners);
            }
            if (listeners.size() > 0)
            {
                Iterator it = listeners.iterator();
                while (it.hasNext())
                {
                    ActionListener list = (ActionListener)it.next();
                    list.actionPerformed(event);
                }
            }
        }
    }
    
    public String getGoalsToExecute()
    {
        return txtEnter.getText();
    }
    
    public void setGoalsToExecute(String goals)
    {
        txtEnter.setText(goals);
        txtEnter.setSelectionStart(0);
        txtEnter.setSelectionEnd(goals.length());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        lblEnter = new javax.swing.JLabel();
        txtEnter = new javax.swing.JTextField();
        lblGoals = new javax.swing.JLabel();
        epGoals = new org.openide.explorer.ExplorerPanel();
        tvGoals = new org.openide.explorer.view.BeanTreeView();

        setLayout(new java.awt.GridBagLayout());

        lblEnter.setLabelFor(txtEnter);
        lblEnter.setText(org.openide.util.NbBundle.getBundle(CustomGoalsPanel.class).getString("CustomGoalsPanel.lblEnter.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblEnter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(txtEnter, gridBagConstraints);

        lblGoals.setLabelFor(epGoals);
        lblGoals.setText(org.openide.util.NbBundle.getBundle(CustomGoalsPanel.class).getString("CustomGoalsPanel.lblGoals.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblGoals, gridBagConstraints);

        epGoals.add(tvGoals, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(epGoals, gridBagConstraints);

    }//GEN-END:initComponents
  
    
    public void addNotify()
    {
        super.addNotify();
        epGoals.getExplorerManager().setRootContext(createLoadingNode());
        RequestProcessor.getDefault().post(new Runnable()
        {
            public void run()
            {
                try
                {
                    final IGoalsGrabber grabber = provider.getGoalsGrabber();
                    if (grabber == null)
                    {
                        logger.error("No grabber");
                        throw new Exception("no grabber");
                    }
                    if (grabber.getPlugins() == null)
                    {
                        grabber.refresh();
                    }
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            epGoals.getExplorerManager().setRootContext(new GoalsRootNode(grabber));
                        }
                    });
                } catch (Exception exc)
                {
                    ErrorManager.getDefault().notify(exc);
                    logger.error("Error while retrieving goals", exc);
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            epGoals.getExplorerManager().setRootContext(createNoGoalsNode());
                        }
                    });
                }
            }
        });  
        epGoals.getExplorerManager().addPropertyChangeListener(new PropListener());
    }
    
    private Node createLoadingNode()
    {
        Children childs = new Array();
        Node loading = new AbstractNode(Children.LEAF);
        loading.setName("Loading"); //NOI18N
        loading.setDisplayName(NbBundle.getBundle(CustomGoalsPanel.class).getString("Loading"));
        childs.add(new Node[] {loading});
        return new AbstractNode(childs);
    }    

    private Node createNoGoalsNode()
    {
        Children childs = new Array();
        Node node = new AbstractNode(Children.LEAF);
        node.setName("NoGoals"); //NOI18N
        node.setDisplayName(NbBundle.getBundle(CustomGoalsPanel.class).getString("NoGoals"));
        childs.add(new Node[] { node });
        return new AbstractNode(childs);
    }    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.ExplorerPanel epGoals;
    private javax.swing.JLabel lblEnter;
    private javax.swing.JLabel lblGoals;
    private org.openide.explorer.view.BeanTreeView tvGoals;
    private javax.swing.JTextField txtEnter;
    // End of variables declaration//GEN-END:variables
    
    
    private class PropListener implements PropertyChangeListener
    {
        
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName()))
            {
                Node[] nds = epGoals.getExplorerManager().getSelectedNodes();
                StringBuffer goals = new StringBuffer();
                for (int i = 0; i < nds.length; i++)
                {
                    GoalNameCookie cook = (GoalNameCookie)nds[i].getCookie(GoalNameCookie.class);
                    if (cook != null)
                    {
                        goals.append(cook.getGoalName());
                        goals.append(" ");
                    }
                }
                int currPos = txtEnter.getCaretPosition();
                int length = goals.length();
                String currSelected = txtEnter.getSelectedText();
                int selStart = currSelected == null ? currPos : txtEnter.getSelectionStart();
                int selEnd = currSelected == null ? currPos : txtEnter.getSelectionEnd();
                logger.debug("selstart=" + selStart + "  selEnd=" + selEnd);
                String text = txtEnter.getText();
                if (selStart > 0)
                {
                    goals.insert(0, text.substring(0, selStart));
                }
                if (selEnd < text.length())
                {
                    goals.append(text.substring(selEnd + 1));
                }
                txtEnter.setText(goals.toString());
                txtEnter.setCaretPosition(selStart + length);
                txtEnter.setSelectionStart(selStart);
                txtEnter.setSelectionEnd(selStart + length);
            }
        }
        
    }

    /**
     * listener for changes in the text field, will trigger action events for the whole panel.
     */
    private class DocListener implements DocumentListener
    {
        
        public void changedUpdate(javax.swing.event.DocumentEvent e)
        {
            generateActionEvent();
        }
        
        public void insertUpdate(javax.swing.event.DocumentEvent e)
        {
            generateActionEvent();
        }
        
        public void removeUpdate(javax.swing.event.DocumentEvent e)
        {
            generateActionEvent();
        }
        
        private void generateActionEvent()
        {
                ActionEvent newEvent = new ActionEvent(CustomGoalsPanel.this, ActionEvent.ACTION_PERFORMED, ACTION_GOALS_CHANGED);
                fireActionEvent(newEvent);
        }
    }
}
