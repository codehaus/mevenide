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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSettings;
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
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class RunGoalsPanel extends JPanel {
    private static final Log logger = LogFactory.getLog(RunGoalsPanel.class);
    /**
     * Action name, fired when the text in the goals textfield changes.
     */
    public static final String ACTION_GOALS_CHANGED = "GoalsChanged"; //NOI18N
    
    private GoalsGrabberProvider provider;
    private MavenProject project;
    private List actionListeners = new ArrayList();
    private static String lastGoal = null;
    
    /** Creates new form CustomGoalsPanel */
    public RunGoalsPanel(MavenProject proj, GoalsGrabberProvider goalsProvider) {
        initComponents();
        provider = goalsProvider;
        project = proj;
        tvGoals.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tvGoals.setRootVisible(false);
        if (lastGoal != null) {
            // do a favour and show the last goal selected.
            txtEnter.setText(lastGoal);
            txtEnter.setCaretPosition(lastGoal.length());
            txtEnter.setSelectionStart(0);
            txtEnter.setSelectionEnd(lastGoal.length());
        } else {
            txtEnter.setText("");
        }
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
        lastGoal = txtEnter.getText();
        return txtEnter.getText();
    }
    
    public void setGoalsToExecute(String goals) {
        txtEnter.setText(goals);
        txtEnter.setSelectionStart(0);
        txtEnter.setSelectionEnd(goals.length());
    }
    
    public boolean isOffline() {
        return cbOffline.isSelected();
    }
    
    public boolean isDebug() {
        return cbDebugMessages.isSelected();
    }
    public boolean isExceptions() {
        return cbExceptions.isSelected();
    }
    public boolean isNoBanner() {
        return cbNoBanner.isSelected();
    }
    public boolean isNonverbose() {
        return cbReduceOutput.isSelected();
    }
    public String getMavenHome() {
        return txtMavenHome.getText();
    }
    public String getMavenLocalHome() {
        return txtMavenLocalHome.getText();
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlBrowser = new javax.swing.JPanel();
        lblGoals = new javax.swing.JLabel();
        epGoals = new org.openide.explorer.ExplorerPanel();
        tvGoals = new org.openide.explorer.view.BeanTreeView();
        lblDescription = new javax.swing.JLabel();
        taDescription = new javax.swing.JTextArea();
        pnlOptions = new javax.swing.JPanel();
        cbOffline = new javax.swing.JCheckBox();
        cbNoBanner = new javax.swing.JCheckBox();
        cbReduceOutput = new javax.swing.JCheckBox();
        cbDebugMessages = new javax.swing.JCheckBox();
        cbExceptions = new javax.swing.JCheckBox();
        lblMavenHome = new javax.swing.JLabel();
        txtMavenHome = new javax.swing.JTextField();
        lblMavenLocalHome = new javax.swing.JLabel();
        txtMavenLocalHome = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

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

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        pnlBrowser.setLayout(new java.awt.GridBagLayout());

        lblGoals.setLabelFor(epGoals);
        lblGoals.setText("Available Goals:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        pnlBrowser.add(lblGoals, gridBagConstraints);

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
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 6);
        pnlBrowser.add(epGoals, gridBagConstraints);

        lblDescription.setText("Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 0, 0);
        pnlBrowser.add(lblDescription, gridBagConstraints);

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
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        pnlBrowser.add(taDescription, gridBagConstraints);

        jTabbedPane1.addTab("Browser", pnlBrowser);

        pnlOptions.setLayout(new java.awt.GridBagLayout());

        cbOffline.setText("Build Offline");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        pnlOptions.add(cbOffline, gridBagConstraints);

        cbNoBanner.setText("Hide Maven Banner");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlOptions.add(cbNoBanner, gridBagConstraints);

        cbReduceOutput.setText("Reduce execution output");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlOptions.add(cbReduceOutput, gridBagConstraints);

        cbDebugMessages.setText("Show debug  messages");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlOptions.add(cbDebugMessages, gridBagConstraints);

        cbExceptions.setText("Print exception traces");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlOptions.add(cbExceptions, gridBagConstraints);

        lblMavenHome.setText("Maven Home :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        pnlOptions.add(lblMavenHome, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        pnlOptions.add(txtMavenHome, gridBagConstraints);

        lblMavenLocalHome.setText("Maven Local Home :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        pnlOptions.add(lblMavenLocalHome, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        pnlOptions.add(txtMavenLocalHome, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 6, 0);
        pnlOptions.add(jSeparator1, gridBagConstraints);

        jTabbedPane1.addTab("Options", pnlOptions);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(jTabbedPane1, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    public void addNotify() {
        super.addNotify();
        epGoals.getExplorerManager().setRootContext(createLoadingNode());
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    final IGoalsGrabber grabber = provider.getGoalsGrabber();
                    if (grabber == null) {
                        logger.error("No grabber");
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
                    logger.error("Error while retrieving goals", exc);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            epGoals.getExplorerManager().setRootContext(createNoGoalsNode());
                        }
                    });
                }
            }
        });
        epGoals.getExplorerManager().addPropertyChangeListener(new PropListener());
        cbOffline.setSelected(MavenSettings.getDefault().isOffline());
        cbDebugMessages.setSelected(MavenSettings.getDefault().isDebug());
        cbExceptions.setSelected(MavenSettings.getDefault().isExceptions());
        cbNoBanner.setSelected(MavenSettings.getDefault().isNoBanner());
        cbReduceOutput.setSelected(MavenSettings.getDefault().isNonverbose());
        txtMavenHome.setText(project.getLocFinder().getMavenHome());
        txtMavenLocalHome.setText(project.getLocFinder().getMavenLocalHome());
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
    private javax.swing.JCheckBox cbDebugMessages;
    private javax.swing.JCheckBox cbExceptions;
    private javax.swing.JCheckBox cbNoBanner;
    private javax.swing.JCheckBox cbOffline;
    private javax.swing.JCheckBox cbReduceOutput;
    private org.openide.explorer.ExplorerPanel epGoals;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblEnter;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JLabel lblMavenHome;
    private javax.swing.JLabel lblMavenLocalHome;
    private javax.swing.JPanel pnlBrowser;
    private javax.swing.JPanel pnlOptions;
    private javax.swing.JTextArea taDescription;
    private org.openide.explorer.view.BeanTreeView tvGoals;
    private javax.swing.JTextField txtEnter;
    private javax.swing.JTextField txtMavenHome;
    private javax.swing.JTextField txtMavenLocalHome;
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
                logger.debug("selstart=" + selStart + "  selEnd=" + selEnd);
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
            ActionEvent newEvent = new ActionEvent(RunGoalsPanel.this, ActionEvent.ACTION_PERFORMED, ACTION_GOALS_CHANGED);
            fireActionEvent(newEvent);
        }
    }
}
