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
import org.mevenide.project.io.JarOverrideReader2;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;

/**
 * node representing a dependency
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class DependencyNode extends AbstractNode {
    
    private Action actions[];
    private Dependency dependency;
    private MavenProject project;
    private boolean isOverriden;
    private String override;
    public DependencyNode(Dependency dep, MavenProject proj) {
        super(Children.LEAF);
        dependency = dep;
        project = proj;
        if (dep.isPlugin()) {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyPlugin"); //NOI18N
        } else if (dep.getType() != null && "pom".equalsIgnoreCase(dep.getType())) { //NOI18N
            setIconBase("org/mevenide/netbeans/project/resources/DependencyPom"); //NOI18N
        } else if (dep.getType() != null && "jar".equalsIgnoreCase(dep.getType())) { //NOI18N
            setIconBase("org/mevenide/netbeans/project/resources/DependencyJar"); //NOI18N
        } else {
            setIconBase("org/mevenide/netbeans/project/resources/DependencyIcon"); //NOI18N
        }
        setDisplayName(createName());
        checkOverride();
    }
    
    private String createName() {
        String title = "";
        if (dependency.getJar() != null) {
            title = dependency.getJar();
        } else {
            title = dependency.getArtifactId();
            if (dependency.getVersion() != null) {
                title = title + "-" + dependency.getVersion();
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
            override = project.getPropertyResolver().getValue("maven.jar." + dependency.getArtifactId());
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
            URI uri = FileUtilities.getDependencyURI(dependency, project);
            if (uri != null) {
                File file = new File(uri);
                return file.exists();
            }
        } else {
            String path = JarOverrideReader2.getInstance().processOverride(dependency, project.getContext());
            if (path != null) {
                File file = new File(path);
                return file.exists();
            }
        }
        return false;
    }
    
    
    public java.awt.Image getIcon(int param) {
        java.awt.Image retValue;
        retValue = super.getIcon(param);
        if (checkLocal()) {
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
        DependencyPanel panel = new DependencyPanel();
        panel.setDependency(dependency, project);
        panel.setFieldsEditable(false);
        return panel;
    }
    
    public boolean canCopy() {
        return false;
    }
    
    public Dependency getDependency() {
        return dependency;
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
}

