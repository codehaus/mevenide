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

package org.mevenide.netbeans.project.dependencies;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSettings;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.repository.RepoPathElement;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyEditor extends javax.swing.JPanel {
    
    private MavenProject project;
    private Set currentProps;
    private DefaultComboBoxModel comboModel;
    /** Creates new form DependencyEditor */
    public DependencyEditor(MavenProject proj, DependencyPOMChange change) {
        initComponents();
        project = proj;
        IContentProvider content = change.getChangedContent();
        assignValue(content, "groupId", txtGroupId);
        assignValue(content, "artifactId", txtArtifactId);
        assignValue(content, "version", txtVersion);
        assignValue(content, "type", txtType);
        assignValue(content, "jar", txtJar);
        assignValue(content, "url", txtURL);
        List props = content.getProperties();
        currentProps = new TreeSet();
        comboModel = new DefaultComboBoxModel();
        List tableRows = new ArrayList();
        if (props != null) {
            Iterator it = props.iterator();
            while (it.hasNext()) {
                String ent = (String)it.next();
                int index  = ent.indexOf(':');
                if (index > 0) {
                    String key = ent.substring(0, index);
                    currentProps.add(key);
                    comboModel.addElement(key);
                    tableRows.add(new WrapperRow(key, ent.substring(index + 1)));
                }
            }
        }
        String[] defaults = MavenSettings.getDefault().getDependencyProperties();
        for (int i = 0; i < defaults.length; i++) {
            if (! currentProps.contains(defaults[i])) {
                comboModel.addElement(defaults[i]);
            }
        }
        JComboBox comProperty = new JComboBox();
        comProperty.setModel(comboModel);
        comProperty.setRenderer(new MyListRenderer());
        comProperty.setEditable(true);
        tblProperties.setModel(new Model(tableRows));
        tblProperties.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comProperty));
        tblProperties.setSurrendersFocusOnKeystroke(true);
        
        //TEMPORARY - hide override related stuff.
        txtOverride.setVisible(false);
        lblOverride.setVisible(false);
        btnOverride.setVisible(false);
        
    }
    
    private void assignValue(IContentProvider provider, String key, JTextField field) {
        String value = provider.getValue(key);
        field.setText(value == null ? "" : value);
    }
    
    public HashMap getValues() {
        HashMap toRet = new HashMap();
        toRet.put("artifactId", getValue(txtArtifactId));
        toRet.put("groupId", getValue(txtGroupId));
        toRet.put("version", getValue(txtVersion));
        toRet.put("jar", getValue(txtJar));
        toRet.put("url", getValue(txtURL));
        toRet.put("type", getValue(txtType));
        return toRet;
    }
    
    private String getValue(JTextField field) {
        String ret = field.getText();
        if (ret != null && ret.length() == 0) {
            ret = null;
        }
        return ret;
    }
    
    public HashMap getProperties() {
        HashMap toRet = new HashMap();
        Collection rows = ((Model)tblProperties.getModel()).getRows();
        Iterator it = rows.iterator();
        while (it.hasNext()) {
            WrapperRow row = (WrapperRow)it.next();
            if (row.getKey() != null && row.getKey().length() > 0 && row.getValue() != null && row.getValue().length() > 0) {
                toRet.put(row.getKey(), row.getValue());
            }
        }
        return toRet;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblGroupId = new javax.swing.JLabel();
        txtGroupId = new javax.swing.JTextField();
        lblArtifactId = new javax.swing.JLabel();
        txtArtifactId = new javax.swing.JTextField();
        lblVersion = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        lblType = new javax.swing.JLabel();
        txtType = new javax.swing.JTextField();
        lbljar = new javax.swing.JLabel();
        txtJar = new javax.swing.JTextField();
        lblURL = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        btnRepo = new javax.swing.JButton();
        btnOverride = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        lblOverride = new javax.swing.JLabel();
        txtOverride = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProperties = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        lblGroupId.setText("Group ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblGroupId, gridBagConstraints);

        txtGroupId.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtGroupId, gridBagConstraints);

        lblArtifactId.setText("Artifact ID :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblArtifactId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtArtifactId, gridBagConstraints);

        lblVersion.setText("Version :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblVersion, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtVersion, gridBagConstraints);

        lblType.setText("Type :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtType, gridBagConstraints);

        lbljar.setText("Jar :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lbljar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtJar, gridBagConstraints);

        lblURL.setText("URL :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblURL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtURL, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(jSeparator1, gridBagConstraints);

        btnRepo.setText("Repository...");
        btnRepo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepoActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnRepo, gridBagConstraints);

        btnOverride.setText("Anywhere...");
        btnOverride.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOverrideActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(btnOverride, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(jSeparator2, gridBagConstraints);

        lblOverride.setText("Override :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(lblOverride, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(txtOverride, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        add(jSeparator3, gridBagConstraints);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(200, 200));
        tblProperties.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblProperties);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(jScrollPane2, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void btnOverrideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOverrideActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_btnOverrideActionPerformed
    
    private void btnRepoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepoActionPerformed
// TODO add your handling code here:
        RepositoryExplorerPanel panel = new RepositoryExplorerPanel(project.getPropertyResolver(), project.getLocFinder());
        DialogDescriptor dd = new DialogDescriptor(panel, "Select repository artifact:");
        Object retVal = DialogDisplayer.getDefault().notify(dd);
        if (retVal == DialogDescriptor.OK_OPTION) {
            RepoPathElement el = panel.getSelectedRepoPathElement();
            if (el != null) {
                txtArtifactId.setText(el.getArtifactId());
                txtGroupId.setText(el.getGroupId());
                String vers = el.getVersion();
                if (vers != null) {
                    txtVersion.setText(vers);
                } else {
                    if (el.isLeaf()) {
                        txtJar.setText(el.getArtifactId());
                    }
                    txtVersion.setText("");
                }
                txtType.setText(el.getType());
            }
        }
        
    }//GEN-LAST:event_btnRepoActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOverride;
    private javax.swing.JButton btnRepo;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblArtifactId;
    private javax.swing.JLabel lblGroupId;
    private javax.swing.JLabel lblOverride;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblURL;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JLabel lbljar;
    private javax.swing.JTable tblProperties;
    private javax.swing.JTextField txtArtifactId;
    private javax.swing.JTextField txtGroupId;
    private javax.swing.JTextField txtJar;
    private javax.swing.JTextField txtOverride;
    private javax.swing.JTextField txtType;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextField txtVersion;
    // End of variables declaration//GEN-END:variables
    
    private class MyListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
            Component supers = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (supers instanceof JLabel && currentProps.contains(value)) {
                JLabel lbl = (JLabel)supers;
                lbl.setText("<html><b>" + value.toString() + "</b></html>");
            }
            return supers;
        }
    }
    
    
    private class Model implements TableModel {
        
        private List rows;
        private Collection listeners = new ArrayList();
        
        public Model(List rowlist) {
            rows = rowlist;
        }
        
        public void setValueAt(Object aValue, int row, int column) {
            WrapperRow rw;
            if (rows.size() == row) {
                rw = new WrapperRow();
            } else {
                rw = (WrapperRow)rows.get(row);
            }
                
            if (column == 1) {
                rw.setValue(aValue.toString());
            } else {
                String old = (String)getValueAt(row, column);
                rw.setKey(aValue.toString());
                if (aValue.toString().length() > 0) {
                    currentProps.remove(old);
                    currentProps.add(aValue.toString());
                }
            }
            if (rows.size() == row) {
                rows.add(rw);
                System.out.println("firing table change");
                fireTableChanged();
            }
            
        }
        
        private void fireTableChanged() {
            TableModelEvent event = new TableModelEvent(this);
            Iterator it = listeners.iterator();
            while (it.hasNext()) {
                TableModelListener list = (TableModelListener)it.next();
                list.tableChanged(event);
            }
        }

        public String getColumnName(int column) {
            if (column == 0) {
                return "Key";
            }
            if (column == 1) {
                return "Value";
            }
            return "XXX";
        }

        public Class getColumnClass(int columnIndex) {
            return String.class;
        }

        public Object getValueAt(int row, int column) {
            if (row != rows.size()) {
                WrapperRow rowObj = (WrapperRow)rows.get(row);
                if (column == 0) {
                    return rowObj.getKey();
                }
                if (column == 1) {
                    return rowObj.getValue();
                }
            }
            return "";
        }

        public int getRowCount() {
            System.out.println("get size=" + (rows.size() + 1));
            return rows.size() + 1;
        }

        public int getColumnCount() {
            return 2;
        }

        public void addTableModelListener(TableModelListener tableModelListener) {
            listeners.add(tableModelListener);
        }

        public boolean isCellEditable(int row, int column) {
            return true;
        }

        public void removeTableModelListener(TableModelListener tableModelListener) {
            listeners.remove(tableModelListener);
        }
        
        public Collection getRows() {
            return rows;
        }
        
    }   
    
    private class WrapperRow {
        private String key;
        private String value;

        public WrapperRow() {
            
        }
        
        public WrapperRow(String k, String v) {
            key = k;
            value = v;
        }
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        
    }
}
