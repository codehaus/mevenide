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
import java.io.File;
import java.net.URI;
import javax.swing.Action;
import org.apache.maven.project.Dependency;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.mevenide.netbeans.project.queries.MavenFileOwnerQueryImpl;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.project.io.JarOverrideReader2;
import org.mevenide.properties.IPropertyResolver;
import org.netbeans.api.project.Project;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * node representing a dependency
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyNode extends AbstractNode {
    
    private Action actions[];
    private IContentProvider dependency;
    private MavenProject project;
    private boolean isOverriden;
    private String override;
    
    public DependencyNode(IContentProvider dep, MavenProject proj) {
        this(dep, proj, Lookups.singleton(dep));
    }
    
    public DependencyNode(IContentProvider dep, MavenProject proj, Lookup lookup) {
        super(Children.LEAF, lookup);
        dependency = dep;
        project = proj;

        setDisplayName(createName());
        setIconBase();
        checkOverride();
    }
    
    private void setIconBase() {
        URI uri = FileUtilities.getDependencyURI(createDependencySnapshot(), project);
        Project depPrj = MavenFileOwnerQueryImpl.getInstance().getOwner(uri);
        if (depPrj != null) {
            setIconBase("org/mevenide/netbeans/project/resources/MavenIcon"); //NOI18N
        } else if ("plugin".equalsIgnoreCase(dependency.getValue("type"))) {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyPlugin"); //NOI18N
        } else if (dependency.getValue("type") != null && "pom".equalsIgnoreCase(dependency.getValue("type"))) { //NOI18N
            setIconBase("org/mevenide/netbeans/project/resources/DependencyPom"); //NOI18N
        } else if ("jar".equalsIgnoreCase(dependency.getValue("type"))) { //NOI18N
            setIconBase("org/mevenide/netbeans/project/resources/DependencyJar"); //NOI18N
        } else {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        }        
    }
    
    public DependencyNode(Dependency dep, MavenProject proj) {
        this(createContentProvider(dep), proj);
    }
    
    private static IContentProvider createContentProvider(Dependency dep) {
        return new DepContentProvider(dep);
    }
    
    private Dependency createDependencySnapshot() {
        Dependency snap = new Dependency();
        if (dependency.getValue("artifactId") != null) {
            snap.setArtifactId(dependency.getValue("artifactId"));
        }
        if (dependency.getValue("groupId") != null) { 
            snap.setGroupId(dependency.getValue("groupId"));
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
        if ( actions == null ) {
            actions = new Action[0];
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
    
    public boolean hasCustomizer() {
        return true;
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
    
    public boolean canCopy() {
        return false;
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
    
    private static class DepContentProvider implements IContentProvider {
        private Dependency dependency;
        public DepContentProvider(Dependency dep) {
            dependency = dep;
        }
        public java.util.List getProperties() {
            return dependency.getProperties();
        }

        public IContentProvider getSubContentProvider(String key) {
            return null;
        }

        public java.util.List getSubContentProviderList(String parentKey, String childKey) {
            return null;
        }

        public String getValue(String key) {
            if ("artifactId".equals(key)) {
                return dependency.getArtifactId();
            }
            if ("groupId".equals(key)) {
                return dependency.getGroupId();
            }
            if ("version".equals(key)) {
                return dependency.getVersion();
            }
            if ("type".equals(key)) {
                return dependency.getType();
            }
            if ("jar".equals(key)) {
                return dependency.getJar();
            }
            if ("url".equals(key)) {
                return dependency.getUrl();
            }
            return null;
        }

        public java.util.List getValueList(String parentKey, String childKey) {
            return null;
        }
        
    }
}

