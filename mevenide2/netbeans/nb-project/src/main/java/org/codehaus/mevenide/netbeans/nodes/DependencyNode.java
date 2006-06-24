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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.NbArtifact;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.mevenide.netbeans.execute.MavenJavaExecutor;
import org.codehaus.mevenide.netbeans.execute.RunConfig;
import org.codehaus.mevenide.netbeans.queries.MavenFileOwnerQueryImpl;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.actions.PropertiesAction;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 * node representing a dependency
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyNode extends AbstractNode {
    
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
        if ( Artifact.SCOPE_SYSTEM.equals(art.getScope())) {
            return false;
        }
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
        if (art instanceof NbArtifact) {
            NbArtifact nb = (NbArtifact)art;
            if (nb.isFakedSystemDependency()) {
                return nb.getNonFakedFile().getName();
            }
        }
        return art.getFile().getName();
    }
    
    public Action[] getActions( boolean context ) {
        Collection acts = new ArrayList();
        acts.add(new ViewJavadocAction());
        InstallLocalArtifactAction act = new InstallLocalArtifactAction();
        acts.add(act);
        if (!isLocal()) {
            act.setEnabled(true);
        }
        
//        acts.add(new EditAction());
//        acts.add(RemoveDepAction.get(RemoveDepAction.class));
        acts.add(new DownloadJavadocAndSourcesAction());
        if (!hasJavadocInRepository()) {
            acts.add(new InstallLocalJavadocAction());
        }
        if (!hasSourceInRepository()) {
            acts.add(new InstallLocalSourcesAction());
        }
        if (isTransitive()) {
            acts.add(new ExcludeTransitiveAction());
        } else {
            acts.add(new RemoveDependencyAction());
        }
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
    
    private boolean isLocal() {
        if (art instanceof NbArtifact) {
            NbArtifact nb = (NbArtifact)art;
            if (nb.isFakedSystemDependency()) {
                return false;
            }
        }
        return art.getFile().exists();
    }
    
    public boolean hasJavadocInRepository() {
        return (! Artifact.SCOPE_SYSTEM.equals(art.getScope())) &&  getJavadocFile().exists();
    }
    
    private File getJavadocFile() {
        File artifact = art.getFile();
        String version = artifact.getParentFile().getName();
        String artifactId = artifact.getParentFile().getParentFile().getName();
        return new File(artifact.getParentFile(), artifactId + "-" + version + "-javadoc.jar");
    }
    
    private File getSourceFile() {
        File artifact = art.getFile();
        String version = artifact.getParentFile().getName();
        String artifactId = artifact.getParentFile().getParentFile().getName();
        return  new File(artifact.getParentFile(), artifactId + "-" + version + "-sources.jar");
    }
    
    public boolean hasSourceInRepository() {
        if (Artifact.SCOPE_SYSTEM.equals(art.getScope())) {
            return false;
        }
        return getSourceFile().exists();
    }
    
    void downloadJavadocSources(MavenEmbedder online) {
        if ( Artifact.SCOPE_SYSTEM.equals(art.getScope())) {
            return;
        }
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
        if (isLocal()) {
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
        if (isLocal()) {
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
    
    private class RemoveDependencyAction extends AbstractAction {
        public RemoveDependencyAction() {
            putValue(Action.NAME, "Remove Dependency");
        }
        
        public void actionPerformed(ActionEvent event) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation("Are you sure you want to remove the dependency " + art.getGroupId() + ":" + art.getArtifactId() + "?", "Confirm");
            nd.setOptionType(NotifyDescriptor.YES_NO_OPTION);
            Object ret = DialogDisplayer.getDefault().notify(nd);
            if (ret != NotifyDescriptor.YES_OPTION) {
                return;
            }
            
            MavenProject mproject = project.getOriginalMavenProject();
            boolean found = false;
            while (mproject != null) {
                if (mproject.getDependencies() != null) {
                    Iterator it = mproject.getDependencies().iterator();
                    while (it.hasNext()) {
                        Dependency dep = (Dependency) it.next();
                        if (   art.getArtifactId().equals(dep.getArtifactId())
                        && art.getGroupId().equals(dep.getGroupId())) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    break;
                }
                mproject = mproject.getParent();
            }
            if (mproject == null) {
                //how come..
                StatusDisplayer.getDefault().setStatusText("Was not able to locate the dependency in POM file(s). Ignoring...");
                return;
            }
            if (mproject != project.getOriginalMavenProject()) {
                //TODO warn that we are to modify the parent pom.
                nd = new NotifyDescriptor.Confirmation("The dependency is specified in parent POM. A change there can infuence other projects as well. Proceed?", "Dependency in Parent POM");
                nd.setOptionType(NotifyDescriptor.YES_NO_OPTION);
                ret = DialogDisplayer.getDefault().notify(nd);
                if (ret != NotifyDescriptor.YES_OPTION) {
                    return;
                }
            }
            try {
                File fil = mproject.getFile();
                Model model = EmbedderFactory.getProjectEmbedder().readModel(fil);
                Iterator it = model.getDependencies().iterator();
                while (it.hasNext()) {
                    Dependency dep = (Dependency) it.next();
                    if (   art.getArtifactId().equals(dep.getArtifactId())
                    && art.getGroupId().equals(dep.getGroupId())) {
                        model.removeDependency(dep);
                        break;
                    }
                }
                WriterUtils.writePomModel(FileUtil.toFileObject(project.getPOMFile()), model);
                project.firePropertyChange(NbMavenProject.PROP_PROJECT);
                
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (MavenEmbedderException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (XmlPullParserException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class ExcludeTransitiveAction extends AbstractAction  {
        public ExcludeTransitiveAction() {
            putValue(Action.NAME, "Exclude Dependency");
        }
        
        public void actionPerformed(ActionEvent event) {
            try {
                List trail = art.getDependencyTrail();
                String str = (String)trail.get(1);
                StringTokenizer tok = new StringTokenizer(str, ":");
                String groupId = tok.nextToken();
                String artifactId = tok.nextToken();
                File fil = DependencyNode.this.project.getPOMFile();
                Model model = EmbedderFactory.getProjectEmbedder().readModel(fil);
                Dependency dep = null;
                if (model.getDependencies() != null) {
                    Iterator it = model.getDependencies().iterator();
                    while (it.hasNext()) {
                        Dependency dependency = (Dependency) it.next();
                        if (groupId.equals(dependency.getGroupId()) && artifactId.equals(dependency.getArtifactId())) {
                            dep = dependency;
                            break;
                        }
                    }
                }
                if (dep == null) {
                    // now check the active profiles for the dependency..
                    List profileNames = new ArrayList();
                    Iterator it = project.getOriginalMavenProject().getActiveProfiles().iterator();
                    while (it.hasNext()) {
                        Profile prof = (Profile) it.next();
                        profileNames.add(prof.getId());
                    }
                    it = model.getProfiles().iterator();
                    while (it.hasNext()) {
                        Profile profile = (Profile) it.next();
                        if (profileNames.contains(profile.getId())) {
                            List lst = profile.getDependencies();
                            if (lst != null) {
                                Iterator it2 = lst.iterator();
                                while (it2.hasNext()) {
                                    Dependency dependency = (Dependency) it2.next();
                                    if (groupId.equals(dependency.getGroupId()) && artifactId.equals(dependency.getArtifactId())) {
                                        dep = dependency;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                }
                if (dep == null) {
                    dep = new Dependency();
                    dep.setArtifactId(artifactId);
                    dep.setGroupId(groupId);
                    dep.setType(tok.nextToken());
                    dep.setVersion(tok.nextToken());
                    model.addDependency(dep);
                }
                Exclusion exclude = new Exclusion();
                exclude.setArtifactId(art.getArtifactId());
                exclude.setGroupId(art.getGroupId());
                dep.addExclusion(exclude);
                WriterUtils.writePomModel(FileUtil.toFileObject(project.getPOMFile()), model);
                project.firePropertyChange(NbMavenProject.PROP_PROJECT);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (XmlPullParserException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (MavenEmbedderException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    private class InstallLocalArtifactAction extends AbstractAction implements RunConfig {
        public InstallLocalArtifactAction() {
            putValue(Action.NAME, "Manually install artifact");
        }
        
        public void actionPerformed(ActionEvent event) {
            File fil = InstallPanel.showInstallDialog(DependencyNode.this.art);
            if (fil != null) {
                putValue("FileToInstall", fil);
                MavenJavaExecutor exec = new MavenJavaExecutor(this);
                ExecutorTask task = ExecutionEngine.getDefault().execute("Install", exec, exec.getInputOutput());
                
                task.addTaskListener(new TaskListener() {
                    public void taskFinished(Task task2) {
//                        project.firePropertyChange(NbMavenProject.PROP_PROJECT);
                    }
                });
            }
        }
        
        public File getExecutionDirectory() {
            return project.getPOMFile().getParentFile();
        }
        
        public NbMavenProject getProject() {
            return project;
        }
        
        public List getGoals() {
            return Collections.singletonList("install:install-file");
        }
        
        public String getExecutionName() {
            return "install-artifact";
        }
        
        public Properties getProperties() {
            Properties props = new Properties();
            props.put("artifactId", art.getArtifactId());
            props.put("groupId", art.getGroupId());
            props.put("version", art.getVersion());
            props.put("packaging", art.getType());
            File file = (File)getValue("FileToInstall");
            props.put("file", file.getAbsolutePath());
            props.put("generatePom", "false");
//            props.put("pomFile", );
            
            return props;
        }
        
        public Boolean isShowDebug() {
            return null;
        }
        
        public Boolean isShowError() {
            return null;
        }
        
        public Boolean isOffline() {
            return null;
        }
        
        public List getActiveteProfiles() {
            return Collections.emptyList();
        }
        
    }
    
    private class InstallLocalJavadocAction extends AbstractAction implements Runnable {
        private File source;
        public InstallLocalJavadocAction() {
            putValue(Action.NAME, "Add local javadoc");
        }
        
        public void actionPerformed(ActionEvent event) {
            source = InstallDocSourcePanel.showInstallDialog(true);
            if (source != null) {
                RequestProcessor.getDefault().post(this);
            }
        }
        
        public void run() {
            File target = getJavadocFile();
            try {
                FileUtils.copyFile(source, target);
            } catch (IOException ex) {
                ex.printStackTrace();
                target.delete();
            }
            refreshNode();
        }
        
    }
    
    private class InstallLocalSourcesAction extends AbstractAction implements Runnable {
        
        private File source;
        public InstallLocalSourcesAction() {
            putValue(Action.NAME, "Add local sources");
        }
        
        public void actionPerformed(ActionEvent event) {
            source = InstallDocSourcePanel.showInstallDialog(false);
            if (source != null) {
                RequestProcessor.getDefault().post(this);
            }
        }
        
        public void run() {
            File target = getSourceFile();
            try {
                FileUtils.copyFile(source, target);
            } catch (IOException ex) {
                ex.printStackTrace();
                target.delete();
            }
            refreshNode();
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
}

