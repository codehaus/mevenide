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

package org.codehaus.mevenide.netbeans.nodes;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.queries.MavenFileOwnerQueryImpl;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 * node representing a dependency
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyNode extends AbstractNode {
    
    private Action actions[];
    private Artifact art;
    private NbMavenProject project;
    private boolean longLiving;
    private PropertyChangeListener listener;
    private ChangeListener listener2;
    

    
    /**
     *@param lookup - expects instance of MavenProject, DependencyPOMChange
     */
    public DependencyNode(Lookup lookup, boolean isLongLiving) {
        super(Children.LEAF, lookup);
//        super(isLongLiving ? new DependencyChildren(lookup) : Children.LEAF, lookup);
        project = (NbMavenProject)lookup.lookup(NbMavenProject.class);
        art = (Artifact)lookup.lookup(Artifact.class);
        longLiving = isLongLiving;
        if (longLiving) {
            listener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
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
            //TODO check if this one is a performance bottleneck.
            MavenFileOwnerQueryImpl.getInstance().addChangeListener(
                        WeakListeners.change(listener2, 
                                             MavenFileOwnerQueryImpl.getInstance()));
        }
        setDisplayName(createName());
        setIconBase();
    }
    
    /**
     * public because of the property sheet
     */
    public boolean isTransitive() {
        List trail = art.getDependencyTrail();
        return (trail != null && trail.size() > 2);
    }
    
    private void setIconBase() {
        if (isDependencyProjectOpen() && isTransitive()) {
            setIconBaseWithExtension("org/codehaus/mevenide/netbeans/TransitiveMaven2Icon.gif"); //NOI18N
        } else if (isDependencyProjectOpen()) {
            setIconBaseWithExtension("org/codehaus/mevenide/netbeans/Maven2Icon.gif"); //NOI18N
        } else if (isTransitive()) {
            setIconBaseWithExtension("org/codehaus/mevenide/netbeans/TransitiveDependencyIcon.gif"); //NOI18N
        } else if (isJarDependency()) { //NOI18N
            setIconBaseWithExtension("org/codehaus/mevenide/netbeans/DependencyJar.gif"); //NOI18N
        } else {
            setIconBaseWithExtension("org/codehaus/mevenide/netbeans/DependencyIcon.gif"); //NOI18N
        }        
    }
    
    private boolean isJarDependency() {
        return "jar".equalsIgnoreCase(art.getType());
    }
    
    boolean isDependencyProjectOpen() {
        URI uri = art.getFile().toURI();
//        URI  rootUri = project.getRepositoryRoot().getURL().toURI(); 
//        URI uri = rootUri.create(rootUri.toString() + "/" + project.getArtifactRelativeRepositoryPath(art));
        Project depPrj = MavenFileOwnerQueryImpl.getInstance().getOwner(uri);
        return depPrj != null;
    }
    
    
    public void refreshNode() {
        setDisplayName(createName());
        setIconBase();
        fireIconChange();
        fireDisplayNameChange(null, getDisplayName());
//        if (longLiving) {
//            ((DependencyChildren)getChildren()).doRefresh();
//        }
    }
    
    public String getHtmlDisplayName() {
        return "<html>" + getDisplayName() + ("compile".equalsIgnoreCase(art.getScope()) ?  "" : "  <i>[" + art.getScope() + "]</i>") + "</html>";
    }
    
    private String createName() {
        return art.getFile().getName();
    }
    
    public Action[] getActions( boolean context ) {
        Collection acts = new ArrayList();
        acts.add(new ViewJavadocAction());
//        if (!checkLocal()) {
//            acts.add(DownloadAction.get(DownloadAction.class));
//        }
//        acts.add(new EditAction());                
//        acts.add(RemoveDepAction.get(RemoveDepAction.class));
        acts.add(new DownloadJavadocAndSourcesAction());
        acts.add(null);
        acts.add(PropertiesAction.get(PropertiesAction.class));
        return (Action[])acts.toArray(new Action[acts.size()]);
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
        return art.getFile().exists();
    }
    
    public boolean hasJavadocInRepository() {
        return getJavadocFile().exists();
    }
    
    private File getJavadocFile() {
        File artifact = art.getFile();
        String version = artifact.getParentFile().getName();
        String artifactId = artifact.getParentFile().getParentFile().getName();
        return new File(artifact.getParentFile(), artifactId + "-" + version + "-javadoc.jar");
    }
    
    public boolean hasSourceInRepository() {
        File artifact = art.getFile();
        String version = artifact.getParentFile().getName();
        String artifactId = artifact.getParentFile().getParentFile().getName();
        File src = new File(artifact.getParentFile(), artifactId + "-" + version + "-sources.jar");
        return src.exists();
    }
    
    void downloadJavadocSources(MavenEmbedder online) {
        Artifact javadoc = project.getEmbedder().createArtifactWithClassifier(
                art.getGroupId(),
                art.getArtifactId(),
                art.getVersion(),
                art.getType(),
                "javadoc");
        Artifact sources = project.getEmbedder().createArtifactWithClassifier(
                art.getGroupId(),
                art.getArtifactId(),
                art.getVersion(),
                art.getType(),
                "sources");
        try {
            StatusDisplayer.getDefault().setStatusText("Checking Javadoc for " + art.getId());
            online.resolve(javadoc, project.getOriginalMavenProject().getRemoteArtifactRepositories(), project.getEmbedder().getLocalRepository());
            StatusDisplayer.getDefault().setStatusText("Checking Sources for " + art.getId());
            online.resolve(sources, project.getOriginalMavenProject().getRemoteArtifactRepositories(), project.getEmbedder().getLocalRepository());
        } catch (ArtifactNotFoundException ex) {
            ex.printStackTrace();
        } catch (ArtifactResolutionException ex) {
            ex.printStackTrace();
        } finally {
            StatusDisplayer.getDefault().setStatusText("");
        }
        refreshNode();
    }
    
    void downloadMainArtifact(MavenEmbedder online) {
        Artifact art2 = project.getEmbedder().createArtifactWithClassifier(
                art.getGroupId(),
                art.getArtifactId(),
                art.getVersion(),
                art.getType(),
                art.getClassifier());
        try {
            StatusDisplayer.getDefault().setStatusText("Checking for " + art.getId());
            online.resolve(art2, project.getOriginalMavenProject().getRemoteArtifactRepositories(), project.getEmbedder().getLocalRepository());
        } catch (ArtifactNotFoundException ex) {
            ex.printStackTrace();
        } catch (ArtifactResolutionException ex) {
            ex.printStackTrace();
        } finally {
            StatusDisplayer.getDefault().setStatusText("");
        }
        refreshNode();
    }
    
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue;
        retValue = super.getIcon(param);
        if (checkLocal()) {
            if (hasJavadocInRepository()) {
                retValue = Utilities.mergeImages(retValue,
                        Utilities.loadImage("org/codehaus/mevenide/netbeans/DependencyJavadocIncluded.png"),
                        12, 12);
            }
            if (hasSourceInRepository()) {
                retValue = Utilities.mergeImages(retValue,
                        Utilities.loadImage("org/codehaus/mevenide/netbeans/DependencySrcIncluded.png"),
                        12, 8);
            }
            return retValue;
        } else {
            return Utilities.mergeImages(retValue,
                    Utilities.loadImage("org/codehaus/mevenide/netbeans/ResourceNotIncluded.gif"),
                    0, 0);
        }
    }
    
    public Image getOpenedIcon(int type) {
        java.awt.Image retValue;
        retValue = super.getOpenedIcon(type);
        if (checkLocal()) {
            if (hasJavadocInRepository()) {
                retValue = Utilities.mergeImages(retValue,
                        Utilities.loadImage("org/codehaus/mevenide/netbeans/DependencyJavadocIncluded.png"),
                        12, 12);
            }
            if (hasSourceInRepository()) {
                retValue = Utilities.mergeImages(retValue,
                        Utilities.loadImage("org/codehaus/mevenide/netbeans/DependencySrcIncluded.png"),
                        12, 8);
            }
            return retValue;
        } else {
            return Utilities.mergeImages(retValue, 
                        Utilities.loadImage("org/codehaus/mevenide/netbeans/ResourceNotIncluded.gif"),
                        0,0);
        }
    }    
    
    public Component getCustomizer() {
        return null;
    }
    
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set basicProps = sheet.get(Sheet.PROPERTIES);
        try {
            PropertySupport.Reflection artifactId = new PropertySupport.Reflection(art, String.class, "getArtifactId", null);
            artifactId.setName("artifactId");
            artifactId.setDisplayName("Artifact Id");
            artifactId.setShortDescription("");
            PropertySupport.Reflection groupId = new PropertySupport.Reflection(art, String.class, "getGroupId", null);
            groupId.setName("groupId");
            groupId.setDisplayName("Group Id");
            groupId.setShortDescription("");
            PropertySupport.Reflection version = new PropertySupport.Reflection(art, String.class, "getVersion", null);
            version.setName("version");
            version.setDisplayName("Version");
            version.setShortDescription("Version of the current artifact");
            PropertySupport.Reflection type = new PropertySupport.Reflection(art, String.class, "getType", null);
            type.setName("type");
            type.setDisplayName("Type");
            PropertySupport.Reflection scope = new PropertySupport.Reflection(art, String.class, "getScope", null);
            scope.setName("scope");
            scope.setDisplayName("Scope");
            PropertySupport.Reflection classifier = new PropertySupport.Reflection(art, String.class, "getClassifier", null);
            classifier.setName("classifier");
            classifier.setDisplayName("Classifier");
            PropertySupport.Reflection hasJavadoc = new PropertySupport.Reflection(this, Boolean.TYPE, "hasJavadocInRepository", null);
            hasJavadoc.setName("javadoc");
            hasJavadoc.setDisplayName("Javadoc Locally");
            PropertySupport.Reflection hasSources = new PropertySupport.Reflection(this, Boolean.TYPE, "hasSourceInRepository", null);
            hasSources.setName("sources");
            hasSources.setDisplayName("Sources Locally");
            PropertySupport.Reflection transitive = new PropertySupport.Reflection(this, Boolean.TYPE, "isTransitive", null);
            transitive.setName("transitive");
            transitive.setDisplayName("Transitive Dependency");
            
            basicProps.put(new Node.Property[] {
                artifactId, groupId, version, type, scope, classifier, transitive, hasJavadoc, hasSources
            });
        } catch (NoSuchMethodException exc) {
            exc.printStackTrace();
        }
        return sheet;
    }
    
    
    private class DownloadJavadocAndSourcesAction extends AbstractAction implements Runnable {
        public DownloadJavadocAndSourcesAction() {
            putValue(Action.NAME, "Download Javadoc & Source");
        }
        
        public void actionPerformed(ActionEvent event) {
            RequestProcessor.getDefault().post(this);
        }
        
        public void run() {
            downloadJavadocSources(EmbedderFactory.getOnlineEmbedder());
        }

    }
    
//    private class EditAction extends AbstractAction {
//        public EditAction() {
//            putValue(Action.NAME, "Edit...");
//        }
//        
//        public void actionPerformed(ActionEvent event) {
//            
//            DependencyEditor ed = new DependencyEditor(project, change);
//            DialogDescriptor dd = new DialogDescriptor(ed, "Edit Dependency");
//            Object ret = DialogDisplayer.getDefault().notify(dd);
//            if (ret == NotifyDescriptor.OK_OPTION) {
//                HashMap props = ed.getProperties();
//                MavenSettings.getDefault().checkDependencyProperties(props.keySet());
//                change.setNewValues(ed.getValues(), props);
//                try {
//                    NbProjectWriter writer = new NbProjectWriter(project);
//                    List changes = (List)getLookup().lookup(List.class);
//                    writer.applyChanges(changes);
//                } catch (Exception exc) {
//                    ErrorManager.getDefault().notify(ErrorManager.USER, exc);
//                }
//            }
//        }
//    }
//    
    private class ViewJavadocAction extends AbstractAction {
        public ViewJavadocAction() {
            putValue(Action.NAME, "View Javadoc");
            setEnabled(hasJavadocInRepository());
        }
        public void actionPerformed(ActionEvent event) {
            File javadoc = getJavadocFile();
            if (javadoc.exists()) {
            try {
                URL url = javadoc.toURI().toURL();
                if (FileUtil.isArchiveFile(url)) {
                    URL archUrl = FileUtil.getArchiveRoot(url);
                    String path = archUrl.toString() + "apidocs/index.html";
                    URL link = new URL(path);
                    HtmlBrowser.URLDisplayer.getDefault().showURL(link);
                }
            } catch (MalformedURLException e) {
                ErrorManager.getDefault().notify(e);
            }
            }
        }
    }
//    
//    
//    private static class DownloadAction extends NodeAction {
//
//        public DownloadAction() {
//        }
//        
//        protected boolean enable(org.openide.nodes.Node[] node) {
//            if (node != null && node.length > 0) {
//                for (int i = 0; i < node.length; i++) {
//                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
//                    if (obj == null) {
//                        return false;
//                    }
//                }
//                return true;
//            }
//            return false;
//        }
//
//        public String getName() {
//            return "Download artifact";
//        }
//
//        protected void performAction(org.openide.nodes.Node[] node) {
//            if (node != null && node.length > 0) {
//                Collection projectsToFire = new HashSet();
//                for (int i = 0; i < node.length; i++) {
//                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
//                    if (obj != null) {
//                        DependencyPOMChange chng = (DependencyPOMChange)obj;
//                        MavenProject prj = (MavenProject)node[i].getLookup().lookup(MavenProject.class);
//                        IRepositoryReader[] readers = RepositoryUtilities.createRemoteReaders(prj.getPropertyResolver());
//                        Dependency dep = createDependencySnapshot(chng.getChangedContent(), prj.getPropertyResolver());
//                        try {
//                            boolean downloaded = RepositoryUtilities.downloadArtifact(readers, prj, dep);
//                            if (downloaded) {
//                                projectsToFire.add(prj);
//                            }
//                        } catch (FileNotFoundException e) {
//                           StatusDisplayer.getDefault().setStatusText(dep.getArtifact() 
//                                   + " is not available in repote repositories.");
//                        } catch (Exception exc) {
//                           StatusDisplayer.getDefault().setStatusText("Error downloading " 
//                                   + dep.getArtifact() + " : " + exc.getLocalizedMessage());
//                        }
//                    }
//                }
//                Iterator it = projectsToFire.iterator();
//                while (it.hasNext()) {
//                    ((MavenProject)it.next()).firePropertyChange(MavenProject.PROP_PROJECT);
//                }
//                
//            }
//        }
//        
//        public HelpCtx getHelpCtx() {
//            return HelpCtx.DEFAULT_HELP;
//        }
//
//        protected boolean asynchronous() {
//            return true;
//        }
//    }
//    
//    private static class RemoveDepAction extends NodeAction {
//
//        public RemoveDepAction() {
//        }
//        
//        protected boolean enable(org.openide.nodes.Node[] node) {
//            MavenProject project = null;
//            if (node != null && node.length > 0) {
//                for (int i = 0; i < node.length; i++) {
//                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
//                    if (obj == null) {
//                        return false;
//                    }
//                    Object proj = node[i].getLookup().lookup(MavenProject.class);
//                    if (project == null) {
//                        project = (MavenProject)proj;
//                    } else {
//                        if (project != proj) {
//                            return false;
//                        }
//                    }
//                }
//                return true;
//            }
//            return false;
//        }
//
//        public String getName() {
//            return "Delete";
//        }
//
//        protected void performAction(org.openide.nodes.Node[] node) {
//            List toDelete = new ArrayList();
//            MavenProject project = null;
//            if (node != null && node.length > 0) {
//                for (int i = 0; i < node.length; i++) {
//                    Object obj = node[i].getLookup().lookup(DependencyPOMChange.class);
//                    if (obj != null) {
//                        toDelete.add(obj);
//                    }
//                    if (project == null) {
//                        project = (MavenProject)node[i].getLookup().lookup(MavenProject.class);
//                    }
//                }
//            }
//            if (project == null) {
//                return;
//            }
//            if (toDelete.size() > 0) {
//                NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
//                        "Are you sure you want to remove " + toDelete.size() + " dependencies?",
//                        "Remove Dependencies", 
//                        NotifyDescriptor.YES_NO_OPTION,
//                        NotifyDescriptor.QUESTION_MESSAGE);
//                Object ret = DialogDisplayer.getDefault().notify(desc);
//                if (ret == NotifyDescriptor.YES_OPTION) {
//                    try {
//                        NbProjectWriter writer = new NbProjectWriter(project);
//                        List changes = (List)node[0].getLookup().lookup(List.class);
//                        changes.removeAll(toDelete);
//                        writer.applyChanges(changes);
//                    } catch (Exception exc) {
//                        ErrorManager.getDefault().notify(ErrorManager.USER, exc);
//                    }
//                    
//                }
//            }
//        }
//        
//        public HelpCtx getHelpCtx() {
//            return HelpCtx.DEFAULT_HELP;
//        }
//
//        protected boolean asynchronous() {
//            return true;
//        }
//        
//    }    
}

