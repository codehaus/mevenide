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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyEditor;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.ui.netbeans.GoalsGrabberProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class GoalCustomEditor extends JPanel
{
    private PropertyEditor editor;
    private Listener listener;
    private CustomGoalsPanel pnlAvailableGoals;
    /** Creates new form GoalCustomEditor */
    protected GoalCustomEditor()
    {
        initComponents();
        GoalsGrabberProvider provider = new GoalsGrabberProvider()
        {
            public IGoalsGrabber getGoalsGrabber() throws Exception
            {
                return GoalsGrabbersManager.getDefaultGoalsGrabber();
            }
        };
        pnlAvailableGoals = new CustomGoalsPanel(provider);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new Insets(12, 12, 12, 0);
        // add as the first one..
        add(pnlAvailableGoals, gridBagConstraints, 0);        
        
        lstGoals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enableRemoveButtons(-1);
        btnAdd.setMnemonic(NbBundle.getMessage(GoalCustomEditor.class, "GoalCustomEditor.btnAdd.mnemonic").charAt(0));
        btnEdit.setMnemonic(NbBundle.getMessage(GoalCustomEditor.class, "GoalCustomEditor.btnEdit.mnemonic").charAt(0));
        btnRemove.setMnemonic(NbBundle.getMessage(GoalCustomEditor.class, "GoalCustomEditor.btnRemove.mnemonic").charAt(0));
        btnRemoveAll.setMnemonic(NbBundle.getMessage(GoalCustomEditor.class, "GoalCustomEditor.btnRemoveAll.mnemonic").charAt(0));
        btnMoveUp.setMnemonic(NbBundle.getMessage(GoalCustomEditor.class, "GoalCustomEditor.btnMoveUp.mnemonic").charAt(0));
        btnMoveDown.setMnemonic(NbBundle.getMessage(GoalCustomEditor.class, "GoalCustomEditor.btnMoveDown.mnemonic").charAt(0));
    }
    
    public GoalCustomEditor(PropertyEditor editor)
    {
        this();
        this.editor = editor;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnRemoveAll = new javax.swing.JButton();
        btnMoveUp = new javax.swing.JButton();
        btnMoveDown = new javax.swing.JButton();
        lblGoals = new javax.swing.JLabel();
        spGoals = new javax.swing.JScrollPane();
        lstGoals = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        btnAdd.setText(org.openide.util.NbBundle.getBundle(GoalCustomEditor.class).getString("GoalCustomEditor.btnAdd.text"));
        btnAdd.setActionCommand("Add");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnAdd, gridBagConstraints);

        btnEdit.setText(org.openide.util.NbBundle.getMessage(GoalCustomEditor.class, "GoalCustomEditor.btnEdit.text"));
        btnEdit.setActionCommand("Edit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnEdit, gridBagConstraints);

        btnRemove.setText(org.openide.util.NbBundle.getBundle(GoalCustomEditor.class).getString("GoalCustomEditor.btnRemove.text"));
        btnRemove.setActionCommand("Remove");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnRemove, gridBagConstraints);

        btnRemoveAll.setText(org.openide.util.NbBundle.getBundle(GoalCustomEditor.class).getString("GoalCustomEditor.btnRemoveAll.text"));
        btnRemoveAll.setActionCommand("RemoveAll");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        add(btnRemoveAll, gridBagConstraints);

        btnMoveUp.setText(org.openide.util.NbBundle.getBundle(GoalCustomEditor.class).getString("GoalCustomEditor.btnMoveUp.text"));
        btnMoveUp.setActionCommand("MoveUp");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnMoveUp, gridBagConstraints);

        btnMoveDown.setText(org.openide.util.NbBundle.getBundle(GoalCustomEditor.class).getString("GoalCustomEditor.btnMoveDown.text"));
        btnMoveDown.setActionCommand("MoveDown");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(btnMoveDown, gridBagConstraints);

        lblGoals.setLabelFor(lstGoals);
        lblGoals.setText(org.openide.util.NbBundle.getBundle(GoalCustomEditor.class).getString("GoalCustomEditor.lblGoals.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lblGoals, gridBagConstraints);

        spGoals.setMinimumSize(new java.awt.Dimension(100, 150));
        spGoals.setPreferredSize(new java.awt.Dimension(200, 200));
        spGoals.setViewportView(lstGoals);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        add(spGoals, gridBagConstraints);

    }//GEN-END:initComponents
    
    public void addNotify()
    {
        super.addNotify();
        if (editor != null)
        {
            DefaultListModel model = new DefaultListModel();
            
            String[] vals = (String[])editor.getValue();
            for (int i = 0; i < vals.length; i++)
            {
                model.addElement(vals[i]);
            }
            lstGoals.setModel(model);
            model.addListDataListener(new DataListener());
        }
        listener = new Listener();
        btnAdd.addActionListener(listener);
        btnEdit.addActionListener(listener);
        btnRemove.addActionListener(listener);
        btnRemoveAll.addActionListener(listener);
        btnMoveUp.addActionListener(listener);
        btnMoveDown.addActionListener(listener);
        lstGoals.addListSelectionListener(listener);
        lstGoals.addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent e) {
                enableRemoveButtons(lstGoals.getSelectedIndex());
//                btnAdd.setEnabled(false);
            }
            public void focusLost(FocusEvent e) {
//                enableRemoveButtons(-1);
            }
        });
//        System.out.println("is focusable=" + epAllGoals.isFocusable());
//        System.out.println("is focusable2=" + tvAllGoals.isFocusable());
//        epAllGoals.addFocusListener(new FocusListener()
//        {
//            public void focusGained(FocusEvent e) {
//                System.out.println("focus gained1");
//                enableRemoveButtons(-1);
//                btnAdd.setEnabled(true);
//            }
//            public void focusLost(FocusEvent e) {
//                System.out.println("focus lost1");
////                enableRemoveButtons(-1);
//            }
//        });
//        tvAllGoals.addFocusListener(new FocusListener()
//        {
//            public void focusGained(FocusEvent e) {
//                System.out.println("focus gained2");
//                enableRemoveButtons(-1);
//                btnAdd.setEnabled(true);
//            }
//            public void focusLost(FocusEvent e) {
//                System.out.println("focus lost2");
////                enableRemoveButtons(-1);
//            }
//        });
        
    }
    
    public void removeNotify()
    {
        super.removeNotify();
        if (listener != null)
        {
            btnAdd.removeActionListener(listener);
            btnRemove.removeActionListener(listener);
            btnRemoveAll.removeActionListener(listener);
            lstGoals.removeListSelectionListener(listener);
            btnMoveUp.removeActionListener(listener);
            btnMoveDown.removeActionListener(listener);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnRemoveAll;
    private javax.swing.JLabel lblGoals;
    private javax.swing.JList lstGoals;
    private javax.swing.JScrollPane spGoals;
    // End of variables declaration//GEN-END:variables

    private void enableRemoveButtons(int selectedIndex)
    {
        if (selectedIndex == -1) {
            btnRemove.setEnabled(false);
            btnMoveUp.setEnabled(false);
            btnMoveDown.setEnabled(false);
            btnEdit.setEnabled(false);
        } else {
            if (selectedIndex == 0)
            {
                btnMoveUp.setEnabled(false);
            } else {
                btnMoveUp.setEnabled(true);
            }
            if (selectedIndex == lstGoals.getModel().getSize() - 1)
            {
                btnMoveDown.setEnabled(false);
            } else {
                btnMoveDown.setEnabled(true);
            }
            btnRemove.setEnabled(true);
            btnEdit.setEnabled(true);
        }
    }
    
    
    private class Listener implements ActionListener, ListSelectionListener
    {
        
        public void actionPerformed(ActionEvent e)
        {
            if ("Add".equals(e.getActionCommand())) //NOI18N
            {
                String goals = pnlAvailableGoals.getGoalsToExecute();
                if (goals.trim().length() != 0) {
                    DefaultListModel model = (DefaultListModel)lstGoals.getModel();
                    model.addElement(goals);
                }
            } else if ("Edit".equals(e.getActionCommand())) //NOI18N
            {
                String goals = pnlAvailableGoals.getGoalsToExecute();
                DefaultListModel model = (DefaultListModel)lstGoals.getModel();
                int index = lstGoals.getSelectedIndex();
                if (index >= 0)
                {
                    model.set(index, goals);
                }
            } else if ("Remove".equals(e.getActionCommand())) //NOI18N
            {
                DefaultListModel model = (DefaultListModel)lstGoals.getModel();
                int selected = lstGoals.getSelectedIndex();
                Object[] sel = lstGoals.getSelectedValues();
                lstGoals.clearSelection();
                for (int i = 0; i < sel.length; i++)
                {
                    model.removeElement(sel[i]);
                }
                // only select the next one If there is a next one..
                if (lstGoals.getModel().getSize() > selected) {
                    lstGoals.setSelectedIndex(selected);
                    lstGoals.grabFocus();
                }
            } else if ("RemoveAll".equals(e.getActionCommand())) //NOI18N
            {
                DefaultListModel model = (DefaultListModel)lstGoals.getModel();
                lstGoals.clearSelection();
                model.removeAllElements();
            } else if ("MoveUp".equals(e.getActionCommand())) //NOI18N
            {
                DefaultListModel model = (DefaultListModel)lstGoals.getModel();
                int selected = lstGoals.getSelectedIndex();
                Object obj = model.remove(selected);
                model.insertElementAt(obj, selected - 1);
                lstGoals.setSelectedIndex(selected - 1);
            } else if ("MoveDown".equals(e.getActionCommand())) //NOI18N
            {
                DefaultListModel model = (DefaultListModel)lstGoals.getModel();
                int selected = lstGoals.getSelectedIndex();
                Object obj = model.remove(selected);
                model.insertElementAt(obj, selected + 1);
                lstGoals.setSelectedIndex(selected + 1);
            }
        }
        
        public void valueChanged(ListSelectionEvent e)
        {
            enableRemoveButtons(lstGoals.getSelectedIndex());
            if (lstGoals.getSelectedIndex() != -1) {
                String goals = (String)lstGoals.getSelectedValue();
                pnlAvailableGoals.setGoalsToExecute(goals);
            } else {
                pnlAvailableGoals.setGoalsToExecute("");
            }
        }
        
    }
    
    private class DataListener implements ListDataListener
    {
        
        public void contentsChanged(ListDataEvent e)
        {
            setValue();
        }
        
        public void intervalAdded(ListDataEvent e)
        {
            setValue();
        }
        
        public void intervalRemoved(ListDataEvent e)
        {
            setValue();
        }
        
        private void setValue()
        {
            DefaultListModel model = (DefaultListModel)lstGoals.getModel();
            String[] arr = new String[model.size()];
            model.copyInto(arr);
            editor.setValue(arr);
        }
        
    }
}
