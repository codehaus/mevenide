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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.IQueryContext;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.netbeans.project.customizer.ui.OriginChangeCellEditor;
import org.mevenide.netbeans.project.customizer.ui.PropertyTableModel;
import org.mevenide.netbeans.project.customizer.ui.TableRowPropertyChange;
import org.mevenide.plugins.IPluginInfo;
import org.mevenide.properties.IPropertyLocator;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class PluginPanel extends JPanel implements ProjectPanel {
    private static Log logger = LogFactory.getLog(PluginPanel.class);
    
    private ProjectValidateObserver valObserver;
    private MavenProject project;
    
    private PropertyTableModel mod;
    
    private HashMap changes;
    private Set propKeys;

    /** Creates new form BuildPanel */
    public PluginPanel(MavenProject proj, Set keys) {
        project = proj;
        propKeys = keys;
        changes = new HashMap();
        initComponents();
        valObserver = null;
        populateChangeInstances();
    }
    
    public PluginPanel(MavenProject proj, Set keys, IPluginInfo info) {
        this(proj, keys);
        lblDescription.setText("<html>These are known properties for plugin <b>" + info.getName() + "</b> in version <b>" + info.getVersion() + "</b></html>");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        spProperties = new javax.swing.JScrollPane();
        tblProperties = new javax.swing.JTable();
        lblDescription = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        spProperties.setViewportView(tblProperties);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(spProperties, gridBagConstraints);

        lblDescription.setText("This is a list of all properties overriden in the project's and user's property files.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(lblDescription, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void populateChangeInstances() {
        Iterator it = propKeys.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            createChangeInstance(key);
        }
        mod = new PropertyTableModel(project.getPropertyResolver(), changes.values());
        tblProperties.setModel(mod);
        TableColumn col = tblProperties.getTableHeader().getColumnModel().getColumn(2);
        col.setMaxWidth(40);
        col.setMinWidth(20);
        col.setPreferredWidth(30);
        col.setCellRenderer(mod);
        col.setCellEditor(new OriginChangeCellEditor(LocationComboFactory.createPropertiesChange(project)));
        col = tblProperties.getTableHeader().getColumnModel().getColumn(1);
        col.setCellRenderer(mod);
        col = tblProperties.getTableHeader().getColumnModel().getColumn(0);
        col.setCellRenderer(mod);
        tblProperties.setSurrendersFocusOnKeystroke(true);
        
    }
    
    private void createChangeInstance(String key) {
        String value = project.getPropertyResolver().getValue(key);
        int location = project.getPropertyLocator().getPropertyLocation(key);
        if (value == null) {
            value = "";
            location = IPropertyLocator.LOCATION_NOT_DEFINED;
        }
        OriginChange chng = LocationComboFactory.createPropertiesChange(project);
        String def = project.getPropertyLocator().getValueAtLocation(key, IPropertyLocator.LOCATION_DEFAULTS);
        def = def == null ? "" : def;
        changes.put(key, new TableRowPropertyChange(key, value, location, chng, def));
    }
    
    
    
    
    public List getChanges() {
        List toReturn = new ArrayList();
        Iterator it = changes.values().iterator();
        while (it.hasNext()) {
            MavenChange change = (MavenChange)it.next();
            if (change.hasChanged()) {
                toReturn.add(change);
            }
        }
        return toReturn;
    }
    
    public void setResolveValues(boolean resolve) {
        mod.setResolve(resolve);
    }
    
    
    
    
    
    public void setValidateObserver(ProjectValidateObserver observer) {
        valObserver = observer;
        
    }
    
    private void doValidate() {
        logger.debug("Listener called");
        ProjectValidateObserver obs = valObserver;
        if (obs != null) {
            obs.resetValidState(isInValidState(), getValidityMessage());
        }
    }
    
    private int doValidateCheck() {
        return 0;
    }
    
    public boolean isInValidState() {
        return doValidateCheck() == 0;
    }
    
    public String getValidityMessage() {
        int retCode = doValidateCheck();
        String message = "";
        
        return message;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDescription;
    private javax.swing.JScrollPane spProperties;
    private javax.swing.JTable tblProperties;
    // End of variables declaration//GEN-END:variables
    
    
    public static PluginPanel createUsedPanel(MavenProject proj) {
        IQueryContext context = proj.getContext();
        Set used = new HashSet();
        used.addAll(context.getParentBuildPropertyKeys());
        used.addAll(context.getParentProjectPropertyKeys());
        used.addAll(context.getBuildPropertyKeys());
        used.addAll(context.getProjectPropertyKeys());
        used.addAll(context.getUserPropertyKeys());
        return new PluginPanel(proj, used);
    }

    public static PluginPanel createPluginPanel(MavenProject proj, IPluginInfo info) {
        IQueryContext context = proj.getContext();
        Set used = new HashSet();
        used.addAll(info.getPropertyKeys());
        return new PluginPanel(proj, used, info);
    }
    
    
}
