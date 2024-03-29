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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreeSelectionModel;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.netbeans.project.goals.GoalsGrabberProvider;
import org.mevenide.netbeans.project.goals.GoalNameCookie;
import org.mevenide.netbeans.project.goals.GoalsRootNode;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;


/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class CustomGoalsPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(CustomGoalsPanel.class.getName());
    /**
     * Action name, fired when the text in the goals textfield changes.
     */
    public static final String ACTION_GOALS_CHANGED = "GoalsChanged"; //NOI18N
    
    private GoalsGrabberProvider provider;
    private List actionListeners = new ArrayList();
    
    /** Creates new form CustomGoalsPanel */
    public CustomGoalsPanel(GoalsGrabberProvider goalsProvider) {
        initComponents();
        provider = goalsProvider;
        tvGoals.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tvGoals.setRootVisible(false);
        txtEnter.setText("");
        txtEnter.getDocument().addDocumentListener(new DocListener());
    }
    
    public void addActionListener(ActionListener listener) {
        synchronized (actionListeners) {
            actionListeners.add(listener);
        }
    }
    public void removeActionListener(ActionListener listener) {
        synchronized (actionListeners) {
            actionListeners.remove(listener);
        }
    }
    
    protected void fireActionEvent(ActionEvent event) {
        if (actionListeners.size() > 0) {
            List listeners = new ArrayList();
            synchronized (actionListeners) {
                listeners.addAll(actionListeners);
            }
            if (listeners.size() > 0) {
                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    ActionListener list = (ActionListener)it.next();
                    list.actionPerformed(event);
                }
            }
        }
    }
    
    public String getGoalsToExecute() {
        return txtEnter.getText();
    }
    
    public void setGoalsToExecute(String goals) {
        txtEnter.setText(goals);
        txtEnter.setSelectionStart(0);
        txtEnter.setSelectionEnd(goals.length());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblEnter = new javax.swing.JLabel();
        txtEnter = new javax.swing.JTextField();
        lblGoals = new javax.swing.JLabel();
        epGoals = new org.openide.explorer.ExplorerPanel();
        tvGoals = new org.openide.explorer.view.BeanTreeView();
        lblDescription = new javax.swing.JLabel();
        taDescription = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        lblEnter.setLabelFor(txtEnter);
        lblEnter.setText("Enter goal(s) :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblEnter, gridBagConstraints);

        txtEnter.setMinimumSize(new java.awt.Dimension(60, 28));
        txtEnter.setPreferredSize(new java.awt.Dimension(100, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(txtEnter, gridBagConstraints);

        lblGoals.setLabelFor(epGoals);
        lblGoals.setText("Available Goals:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblGoals, gridBagConstraints);

        epGoals.setMinimumSize(new java.awt.Dimension(150, 100));
        tvGoals.setPreferredSize(new java.awt.Dimension(400, 323));
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

        lblDescription.setText("Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(lblDescription, gridBagConstraints);

        taDescription.setEditable(false);
        taDescription.setLineWrap(true);
        taDescription.setRows(2);
        taDescription.setTabSize(4);
        taDescription.setWrapStyleWord(true);
        taDescription.setMinimumSize(new java.awt.Dimension(50, 24));
        taDescription.setPreferredSize(new java.awt.Dimension(100, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(taDescription, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    public void addNotify() {
        super.addNotify();
        epGoals.getExplorerManager().setRootContext(createLoadingNode());
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    final IGoalsGrabber grabber = provider.getGoalsGrabber();
                    if (grabber == null) {
                        logger.severe("No grabber");
                        throw new Exception("no grabber");
                    }
                    grabber.refresh();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            epGoals.getExplorerManager().setRootContext(new GoalsRootNode(grabber));
                        }
                    });
                } catch (Exception exc) {
                    ErrorManager.getDefault().notify(exc);
                    logger.log(Level.SEVERE, "Error while retrieving goals", exc);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            epGoals.getExplorerManager().setRootContext(createNoGoalsNode());
                        }
                    });
                }
            }
        });
        epGoals.getExplorerManager().addPropertyChangeListener(new PropListener());
    }
    
    private Node createLoadingNode() {
        Children childs = new Children.Array();
        Node loading = new AbstractNode(Children.LEAF);
        loading.setName("Loading"); //NOI18N
        loading.setDisplayName(NbBundle.getBundle(CustomGoalsPanel.class).getString("Loading"));
        childs.add(new Node[] {loading});
        return new AbstractNode(childs);
    }
    
    private Node createNoGoalsNode() {
        Children childs = new Children.Array();
        Node node = new AbstractNode(Children.LEAF);
        node.setName("NoGoals"); //NOI18N
        node.setDisplayName(NbBundle.getBundle(CustomGoalsPanel.class).getString("NoGoals"));
        childs.add(new Node[] { node });
        return new AbstractNode(childs);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.ExplorerPanel epGoals;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblEnter;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JTextArea taDescription;
    private org.openide.explorer.view.BeanTreeView tvGoals;
    private javax.swing.JTextField txtEnter;
    // End of variables declaration//GEN-END:variables
    
    
    private class PropListener implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nds = epGoals.getExplorerManager().getSelectedNodes();
                StringBuffer goals = new StringBuffer();
                for (int i = 0; i < nds.length; i++) {
                    GoalNameCookie cook = (GoalNameCookie)nds[i].getCookie(GoalNameCookie.class);
                    if (cook != null) {
                        goals.append(cook.getGoalName());
                        goals.append(" ");
                        if (i == nds.length - 1) {
                            taDescription.setText(nds[i].getShortDescription());
                        }
                    }
                }
                if (nds.length == 0) {
                    taDescription.setText("");
                }
                int currPos = txtEnter.getCaretPosition();
                int length = goals.length();
                String currSelected = txtEnter.getSelectedText();
                int selStart = currSelected == null ? currPos : txtEnter.getSelectionStart();
                int selEnd = currSelected == null ? currPos : txtEnter.getSelectionEnd();
                logger.fine("selstart=" + selStart + "  selEnd=" + selEnd);
                String text = txtEnter.getText();
                if (selStart > 0) {
                    goals.insert(0, text.substring(0, selStart));
                }
                if (selEnd < text.length()) {
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
    private class DocListener implements DocumentListener {
        
        public void changedUpdate(DocumentEvent e) {
            generateActionEvent();
        }
        
        public void insertUpdate(DocumentEvent e) {
            generateActionEvent();
        }
        
        public void removeUpdate(DocumentEvent e) {
            generateActionEvent();
        }
        
        private void generateActionEvent() {
            ActionEvent newEvent = new ActionEvent(CustomGoalsPanel.this, ActionEvent.ACTION_PERFORMED, ACTION_GOALS_CHANGED);
            fireActionEvent(newEvent);
        }
    }
}
