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
package org.mevenide.netbeans.project.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.reports.IReportsFinder;
import org.mevenide.reports.JDomReportsFinder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 * showing reports.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class ReportsPanel extends JPanel implements ProjectPanel {
    private static Log logger = LogFactory.getLog(ReportsPanel.class);
    
    private ProjectValidateObserver valObserver;
    private Listener listener;
    private MavenProject project;
    private OriginChange ocReports;
    private DefaultListModel model;
    private ListModelPOMChange change;
    private IReportsFinder finder;

    /** Creates new form BasicsPanel */
    public ReportsPanel(MavenProject proj) {
        project = proj;
        initComponents();
        valObserver = null;
        setName("Reports");

        lstLists.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        populateChangeInstances();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblLists = new javax.swing.JLabel();
        ocReports = LocationComboFactory.createPOMChange(project, true);
        btnReports = (JButton)ocReports.getComponent();
        spLists = new javax.swing.JScrollPane();
        lstLists = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblLists.setLabelFor(lstLists);
        lblLists.setText("Reports:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(lblLists, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(btnReports, gridBagConstraints);

        spLists.setPreferredSize(new java.awt.Dimension(300, 131));
        spLists.setViewportView(lstLists);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(spLists, gridBagConstraints);

        btnAdd.setText("Add");
        btnAdd.setActionCommand("btnAdd");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnAdd, gridBagConstraints);

        btnRemove.setText("Remove");
        btnRemove.setActionCommand("btnRemove");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnRemove, gridBagConstraints);

    }//GEN-END:initComponents
    
    public void addNotify() {
        super.addNotify();
        listener = new Listener();
        btnAdd.addActionListener(listener);
        btnRemove.addActionListener(listener);
        lstLists.addListSelectionListener(listener);
    }
    
    public void removeNotify() {
        super.removeNotify();
        btnAdd.removeActionListener(listener);
        btnRemove.removeActionListener(listener);
        lstLists.removeListSelectionListener(listener);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnReports;
    private javax.swing.JLabel lblLists;
    private javax.swing.JList lstLists;
    private javax.swing.JScrollPane spLists;
    // End of variables declaration//GEN-END:variables
    
    private void populateChangeInstances() {    
        String key = "pom.reports"; //NOI18N
        int location = project.getProjectWalker().getLocation(key);
        List oldValues = new ArrayList();
        List orig = project.getOriginalMavenProject().getReports();
        if (orig != null) {
            oldValues.addAll(orig);
        }
        model = new DefaultListModel();
        lstLists.setModel(model);
        change = new ListModelPOMChange(key, oldValues, location, model, ocReports);
    }
    
    public void setResolveValues(boolean resolve) {
        if (resolve) {
            List list = change.getChangedContent().getValueList("reports", "report");
            if (list != null) {
                List resolved = new ArrayList();
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    String report = (String)it.next();
                    resolved.add(project.getPropertyResolver().resolveString(report));
                }
                change.setResolvedValues(resolved);
            }
        } else {
            change.resetToNonResolvedValue();
        }
        // nothing selected -> disable
        btnRemove.setEnabled(!resolve);
        btnAdd.setEnabled(!resolve);
    }
   
    public List getChanges() {
        List toReturn = new ArrayList();
        if (change.hasChanged()) {
            toReturn.add(change);
        }
        return toReturn;
    }
    
    public void setValidateObserver(ProjectValidateObserver observer) {
        valObserver = observer;
    }
   
    public boolean isInValidState() {
        // is always valid, since we can continue, error messages only happen when the
        // attemp to add to list is done.. if it fails, it's not commited, thus the state is always valid.
        return true;
    }
    
    public String getValidityMessage() {
//        int retCode = doValidateCheck();
        String message = "";
        return message;
    }
    
    private IReportsFinder getReportsFinder() {
        if (finder == null) {
            finder = new JDomReportsFinder(project.getLocFinder());
        }
        return finder;
    }
    
    private JPanel createAddPanel(JList list) {
        JPanel panel = new JPanel();
        java.awt.GridBagConstraints gridBagConstraints;
        JLabel jLabel1 = new javax.swing.JLabel();

        panel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Available Reports:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panel.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        list.setVisibleRowCount(12);
        JScrollPane pane = new JScrollPane();
        pane.setViewportView(list);
        panel.add(pane, gridBagConstraints);
        
        return panel;
    }
    /**
     * action listener for buttons and list selection..
     */
    private class Listener implements ActionListener, ListSelectionListener {
        
        public void actionPerformed(ActionEvent e) {
            if ("btnRemove".equals(e.getActionCommand())) {
                Object[] values = lstLists.getSelectedValues();
                for (int i = 0; i < values.length; i++) {
                    model.removeElement(values[i]);
                }
            }
            if ("btnAdd".equals(e.getActionCommand())) {
                try {
                    String[] reports = getReportsFinder().findReports();
                    Set additional = new TreeSet(Arrays.asList(reports));
                    additional.removeAll(change.getChangedContent().getValueList("reports", "report"));
                    JList list = new JList(additional.toArray());
                    Object[] options = new Object[] {
                        NotifyDescriptor.OK_OPTION,
                        NotifyDescriptor.CANCEL_OPTION
                    };
                    NotifyDescriptor dd = new NotifyDescriptor(createAddPanel(list), "Add Reports",
                                                NotifyDescriptor.OK_CANCEL_OPTION,
                                                NotifyDescriptor.PLAIN_MESSAGE,
                                                options, options[0]);
                    Object val = DialogDisplayer.getDefault().notify(dd);
                    if (val == options[0]) {
                        Object[] values = list.getSelectedValues();
                        for (int i = 0; i < values.length; i++) {
                            model.addElement(values[i]);
                        }
                    }
                } catch (Exception exc) {
                    logger.error("exception while retrieving reports", exc);
                }
            }
        }

        public void valueChanged(ListSelectionEvent e) {
//            if (lstLists.getSelectedIndex() == -1) {
//                currentList = null;
//                btnRemove.setEnabled(false);
//            } else {
//                btnRemove.setEnabled(true);
//            }
        }
    }
    
}
