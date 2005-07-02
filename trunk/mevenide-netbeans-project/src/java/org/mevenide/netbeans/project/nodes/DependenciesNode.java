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

package org.mevenide.netbeans.project.nodes;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSettings;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.netbeans.project.dependencies.DependencyEditor;
import org.mevenide.netbeans.project.dependencies.DependencyNode;
import org.mevenide.netbeans.project.writer.NbProjectWriter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * root node for dependencies in project's view.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class DependenciesNode extends AbstractNode {
    private MavenProject project;
    DependenciesNode(MavenProject mavproject) {
        super(new DependenciesChildren(mavproject));
        setName("Dependencies"); //NOI18N
        setDisplayName("Dependencies");
        project = mavproject;
        setIconBase("org/mevenide/netbeans/project/resources/defaultFolder"); //NOI18N
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue = super.getIcon(param);
        retValue = Utilities.mergeImages(retValue,
                Utilities.loadImage("org/mevenide/netbeans/project/resources/libraries-badge.png"),
                8, 8);
        return retValue;
    }
    
    public java.awt.Image getOpenedIcon(int param) {
        java.awt.Image retValue = super.getOpenedIcon(param);
        retValue = Utilities.mergeImages(retValue,
                Utilities.loadImage("org/mevenide/netbeans/project/resources/libraries-badge.png"),
                8, 8);
        return retValue;
    }
    
    public Action[] getActions(boolean context) {
        return new Action[] { new AddDependencyAction() };
    }
    
    private MavenProject getProject() {
        return project;
    }
    
    private static class DependenciesChildren extends Children.Keys implements PropertyChangeListener {
        private MavenProject project;
        List deps;
        public DependenciesChildren(MavenProject proj) {
            super();
            project = proj;
        }
        
        protected Node[] createNodes(Object obj) {
            DependencyPOMChange combo = (DependencyPOMChange)obj;
            Lookup look = Lookups.fixed(new Object[] {
                combo,
                project, 
                deps
            });
            return new Node[] { new DependencyNode(look, true) };
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            if (MavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                regenerateKeys();
                refresh();
            }
        }
        
        protected void addNotify() {
            super.addNotify();
            project.addPropertyChangeListener(this);
            regenerateKeys();
        }
        
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
            project.removePropertyChangeListener(this);
            super.removeNotify();
        }
        
        private void regenerateKeys() {
            Project[] projs = project.getContext().getPOMContext().getProjectLayers();
            deps = new ArrayList();
            for (int i = 0; i < projs.length; i++) {
                List ones = projs[i].getDependencies();
                if (ones != null) {
                    Iterator it = ones.iterator();
                    while (it.hasNext()) {
                        Dependency dep = (Dependency)it.next();
                        deps.add(DependencyPOMChange.createChangeInstance(dep, i, new HashMap(),
                                LocationComboFactory.createPOMChange(project, false), false));
                    }
                }
            }
            setKeys(deps);
        }
    }
    
    private class AddDependencyAction extends AbstractAction {
        public AddDependencyAction() {
            putValue(Action.NAME, "Add Dependency...");
        }
        public void actionPerformed(ActionEvent event) {
            DependencyPOMChange change = DependencyPOMChange.createChangeInstance(null, OriginChange.LOCATION_POM, new HashMap(), LocationComboFactory.createPOMChange(project, false), false);
            DependencyEditor ed = new DependencyEditor(project, change);
            DialogDescriptor dd = new DialogDescriptor(ed, "Add Dependency");
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (ret == NotifyDescriptor.OK_OPTION) {
                HashMap props = ed.getProperties();
                MavenSettings.getDefault().checkDependencyProperties(props.keySet());
                change.setNewValues(ed.getValues(), props);
                try {
                    NbProjectWriter writer = new NbProjectWriter(project);
                    List changes = new ArrayList();//DependencyNode.createChangeInstancesList(project, new HashMap());
                    changes.addAll(((DependenciesChildren)getChildren()).deps);
                    changes.add(change);
                    writer.applyChanges(changes);
                } catch (Exception exc) {
                    ErrorManager.getDefault().notify(ErrorManager.USER, exc);
                }
            }
        }
    }
}

