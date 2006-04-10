/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.graph;

import java.awt.Image;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.graph.api.model.builtin.GraphNode;
import org.netbeans.graph.vmd.VMDOrderingLogic;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class ProjectGraphNode extends GraphNode {

    private NbMavenProject project;
    
    /** Creates a new instance of ProjectGraphNode */
    public ProjectGraphNode(NbMavenProject project) {
        super();
        setPortsOrderingLogic(new VMDOrderingLogic());
        this.project = project;
        MavenProject mavenproject = project.getOriginalMavenProject();
        setID(mavenproject.getArtifactId() + ":" + mavenproject.getGroupId());
        setDisplayName(mavenproject.getArtifactId());
        setIcon(createNodeIcon(mavenproject.getPackaging()));
        setTooltipText("<html>ArtifactID: <b>" + mavenproject.getArtifactId() + 
                       "</b><p>GroupID: <b>" + mavenproject.getGroupId() + 
                       "</b><p>Version: <b>" + mavenproject.getVersion() + 
                       "</b><p>Packaging: <b>" + mavenproject.getPackaging() + 
                       "</b><p>Location: <b>" + mavenproject.getBasedir().getAbsolutePath() + "</html>");
        
    }
    
    
    private Image createNodeIcon(String packaging) {
        if ("pom".equals(packaging)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-pom.png");
        }
        if ("jar".equals(packaging)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-jar.png");
        }
        if ("nbm".equals(packaging)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-nbm.png");
        }
        if ("ear".equals(packaging)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-ear.png");
        }
        if ("war".equals(packaging)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-war.png");
        }
        if ("ejb".equals(packaging)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-ejb.png");
        }
        if ("maven-plugin".equals(packaging)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-mvn.png");
        }
        return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-unknown.png");
        
    }

    NbMavenProject getProject() {
        return project;
    }
    
}
