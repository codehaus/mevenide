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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.project.Dependency;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.MavenSettings;
import org.mevenide.netbeans.project.customizer.DependencyPOMChange;
import org.mevenide.netbeans.project.customizer.ui.LocationComboFactory;
import org.mevenide.netbeans.project.queries.MavenFileOwnerQueryImpl;
import org.mevenide.netbeans.project.writer.NbProjectWriter;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.project.io.JarOverrideReader2;
import org.mevenide.properties.IPropertyResolver;
import org.netbeans.api.project.Project;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.NodeAction;


/**
 * node representing a dependency
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyNode extends AbstractNode {
    
    private Action actions[];
    private DependencyPOMChange change;
    private IContentProvider dependency;
    private MavenProject project;
    private boolean isOverriden;
    private String override;
    private boolean longLiving;
    private PropertyChangeListener listener;
    private ChangeListener listener2;
    
//    public DependencyNode(Dependency dep, MavenProject proj) {
//        this(dep, 0, proj, false);
//    }
//    
//    public DependencyNode(Dependency dep, int location, MavenProject proj, boolean isLongLiving) {
//        this(createContentProvider(dep), location, proj, Lookups.singleton(dep), isLongLiving);
//    }
    
    /**
     *in lookup expect instance of MavenProject, DependencyPOMChange
     */
    public  DependencyNode(Lookup lookup, boolean isLongLiving) {
        super(isLongLiving ? /*new DepChildren()*/ Children.LEAF : Children.LEAF, lookup);
        project = (MavenProject)lookup.lookup(MavenProject.class);
        change = (DependencyPOMChange)lookup.lookup(DependencyPOMChange.class);
        dependency = change.getChangedContent();
        longLiving = isLongLiving;
        if (longLiving) {
            listener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (MavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                        refreshNode();
                    }
                }
            };
            project.addPropertyChangeListener(WeakListeners.propertyChange(listener, this));
            listener2 = new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                    refreshNode();
                }
            };
            MavenFileOwnerQueryImpl.getInstance().addChangeListener(WeakListeners.change(listener2, this));
        }
        setDisplayName(createName());
        setIconBase();
        checkOverride();
    }
    
    private void setIconBase() {
        if (isDependencyProjectOpen()) {
            setIconBase("org/mevenide/netbeans/project/resources/MavenIcon"); //NOI18N
        } else if (isPluginDependency()) {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyPlugin"); //NOI18N
        } else if (isJarDependency()) { //NOI18N
            setIconBase("org/mevenide/netbeans/project/resources/DependencyJar"); //NOI18N
        } else {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        }        
    }
    
    private boolean isJarDependency() {
        return "jar".equalsIgnoreCase(dependency.getValue("type"));
    }
    
    private boolean isPluginDependency() {
        return "plugin".equalsIgnoreCase(dependency.getValue("type"));
    }
    
    private boolean isEjbDependency() {
        return "ejb".equalsIgnoreCase(dependency.getValue("type"));
    }
    
    private boolean isDependencyProjectOpen() {
        URI uri = FileUtilities.getDependencyURI(createDependencySnapshot(), project);
        Project depPrj = MavenFileOwnerQueryImpl.getInstance().getOwner(uri);
        return depPrj != null;
    }
    
    private Dependency createDependencySnapshot() {
        Dependency snap = new Dependency();
        if (dependency.getValue("artifactId") != null) {
            snap.setArtifactId(dependency.getValue("artifactId"));
        }
        if (dependency.getValue("groupId") != null) { 
            snap.setGroupId(dependency.getValue("groupId"));
        }
        if (dependency.getValue("id") != null) {
            snap.setId(dependency.getValue("groupId"));
        }
        if (dependency.getValue("version") != null) {
            snap.setVersion(dependency.getValue("version"));
        }
        if (dependency.getValue("type") != null) {
            snap.setType(dependency.getValue("type"));
        }
        if (dependency.getValue("jar") != null) {
            snap.setJar(dependency.getValue("jar"));
        }
        if (dependency.getValue("url") != null) {
            snap.setUrl(dependency.getValue("url"));
        }
        
        return snap;
    }
    
    public void refreshNode() {
        setDisplayName(createName());
        setIconBase();
        checkOverride();
        fireIconChange();
        fireDisplayNameChange(null, getDisplayName());
    }
    
    private String createName() {
        String title = "";
        IPropertyResolver res = project.getPropertyResolver();
        if (dependency.getValue("jar") != null) {
            title = res.resolveString(dependency.getValue("jar"));
        } else {
            title = res.resolveString(dependency.getValue("artifactId"));
            if (dependency.getValue("version") != null) {
                title = title + "-" + res.resolveString(dependency.getValue("version"));
            }
        }
        return title;
    }
    
    private void checkOverride() {
            // check if dependency is overriden
        isOverriden = false;
        String ov = project.getPropertyResolver().getResolvedValue("maven.jar.override");
        if (ov != null) {
            ov = ov.trim();
        }
        if ("true".equalsIgnoreCase(ov) || "on".equalsIgnoreCase(ov)) {
            override = project.getPropertyResolver().getValue("maven.jar." + dependency.getValue("artifactId"));
            isOverriden = override != null;
        }
    }
    
    public Action[] getActions( boolean context ) {
        if (!checkLocal()) {
            actions = new Action[] {
                DownloadAction.get(DownloadAction.class),
                new EditAction(),
                RemoveDepAction.get(RemoveDepAction.class),
                null,
                PropertiesAction.get(PropertiesAction.class)
            };
        } else {
            actions = new Action[] {
                new EditAction(),
                RemoveDepAction.get(RemoveDepAction.class),
                null,
                PropertiesAction.get(PropertiesAction.class)
            };
        }
        return actions;
    }
    
    public boolean canDestroy() {
        return true;
    }
    
    public boolean canRename() {
        return false;
    }
    
    
    public boolean canCut() {
        return false;
    }
    
    public boolean canCopy() {
        return false;
    }
    
    public boolean hasCustomizer() {
        return false;
    }
    
    private boolean checkLocal() {
        if (!isOverriden) {
            URI uri = FileUtilities.getDependencyURI(createDependencySnapshot(), project);
            if (uri != null) {
                File file = new File(uri);
                return file.exists();
            }
        } else {
            String path = JarOverrideReader2.getInstance().processOverride(createDependencySnapshot(), project.getContext());
            if (path != null) {
                File file = new File(path);
                return file.exists();
            }
        }
        return false;
    }
    
    public boolean hasJavadocInRepository() {
        Dependency depSnap = createDependencySnapshot();
        depSnap.setType("javadoc.jar");
        URI uri = FileUtilities.getDependencyURI(depSnap, project);
        return (uri != null && new File(uri).exists());
    }
    
    public boolean hasSourceInRepository() {
        Dependency depSnap = createDependencySnapshot();
        depSnap.setType("src.jar");
        URI uri = FileUtilities.getDependencyURI(depSnap, project);
        return (uri != null && new File(uri).exists());
    }
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue;
        retValue = super.getIcon(param);
        if (checkLocal()) {
            if ("jar".equalsIgnoreCase(dependency.getValue("type"))) {
                if (hasJavadocInRepository()) {
                    retValue = Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/mevenide/netbeans/project/resources/DependencyJavadocIncluded.png"),
                        12, 12);
                }
                if (hasSourceInRepository()) {
                    retValue = Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/mevenide/netbeans/project/resources/DependencySrcIncluded.png"),
                        12, 8);
                }
                return retValue;
                
            } 
            return retValue;
        } else {
            return Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/mevenide/netbeans/project/resources/ResourceNotIncluded.gif"),
                        0, 0);
        }
    }
    
    public Image getOpenedIcon(int type) {
        java.awt.Image retValue;
        retValue = super.getOpenedIcon(type);
        if (checkLocal()) {
            return retValue;
        } else {
            return Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/mevenide/netbeans/project/resources/ResourceNotIncluded.gif"),
                        0,0);
        }
    }    
    
    public Component getCustomizer() {
        return null;
    }
    
    
    public java.lang.String getHtmlDisplayName() {
        java.lang.String retValue;
        if (isOverriden) {
            retValue = "<S>" + getDisplayName() + "</S>  ( Overriden: " + override + ")";
        } else {
            retValue = super.getHtmlDisplayName();
        }
        return retValue;
    }

    public void destroy() throws java.io.IOException {
        super.destroy();
    }
    
    
    
    private class EditAction extends AbstractAction {
        public EditAction() {
            putValue(Action.NAME, "Edit...");
        }
        
        public void actionPerformed(ActionEvent event) {
            
            DependencyEditor ed = new DependencyEditor(project, change);
            DialogDescriptor dd = new DialogDescriptor(ed, "Edit Dependency");
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (ret == NotifyDescriptor.OK_OPTION) {
                HashMap props = ed.getProperties();
                MavenSettings.getDefault().checkDependencyProperties(props.keySet());
                change.setNewValues(ed.getValues(), props);
                try {
                    NbProjectWriter writer = new NbProjectWriter(project);
                    List changes = (List)getLookup().lookup(List.class);
                    writer.applyChanges(changes);
                } catch (Exception exc) {
                    ErrorManager.getDefault().notify(ErrorManager.USER, exc);
                }
            }
        }
    }
    
    private static class DownloadAction extends NodeAction {

        public DownloadAction() {
        }
        
        protected boolean enable(org.openide.nodes.Node[] node) {
            if (node != null && node.length > 0) {
                for (int i = 0; i < node.length; i++) {
                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
                    if (obj == null) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        public String getName() {
            return "Download artifact";
        }

        protected void performAction(org.openide.nodes.Node[] node) {
            if (node != null && node.length > 0) {
                for (int i = 0; i < node.length; i++) {
                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
                    if (obj != null) {
                    }
                }
            }
        }
        
        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }
    }
    
    private static class RemoveDepAction extends NodeAction {

        public RemoveDepAction() {
        }
        
        protected boolean enable(org.openide.nodes.Node[] node) {
            MavenProject project = null;
            if (node != null && node.length > 0) {
                for (int i = 0; i < node.length; i++) {
                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
                    if (obj == null) {
                        return false;
                    }
                    Object proj = node[i].getLookup().lookup(MavenProject.class);
                    if (project == null) {
                        project = (MavenProject)proj;
                    } else {
                        if (project != proj) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }

        public String getName() {
            return "Delete";
        }

        protected void performAction(org.openide.nodes.Node[] node) {
            List toDelete = new ArrayList();
            MavenProject project = null;
            if (node != null && node.length > 0) {
                for (int i = 0; i < node.length; i++) {
                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
                    if (obj != null) {
                        toDelete.add(obj);
                    }
                    if (project == null) {
                        project = (MavenProject)node[i].getLookup().lookup(MavenProject.class);
                    }
                }
            }
            if (project == null) {
                return;
            }
            if (toDelete.size() > 0) {
                NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
                        "Are you sure you want to remove " + toDelete.size() + " dependencies?",
                        "Remove Dependencies", 
                        NotifyDescriptor.YES_NO_OPTION,
                        NotifyDescriptor.QUESTION_MESSAGE);
                Object ret = DialogDisplayer.getDefault().notify(desc);
                if (ret == NotifyDescriptor.YES_OPTION) {
                    try {
                        NbProjectWriter writer = new NbProjectWriter(project);
                        List changes = (List)node[0].getLookup().lookup(List.class);
                        changes.removeAll(toDelete);
                        writer.applyChanges(changes);
                    } catch (Exception exc) {
                        ErrorManager.getDefault().notify(ErrorManager.USER, exc);
                    }
                    
                }
            }
        }
        
        public HelpCtx getHelpCtx() {
            return HelpCtx.DEFAULT_HELP;
        }

        protected boolean asynchronous() {
            return true;
        }
        
    }    
}

