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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.graph.api.model.builtin.GraphNode;
import org.netbeans.graph.vmd.VMDOrderingLogic;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class ArtifactGraphNode extends GraphNode {

    private Artifact artifact;

    /** Creates a new instance of ProjectGraphNode */
    public ArtifactGraphNode(Artifact art) {
        super();
        setPortsOrderingLogic(new VMDOrderingLogic());
        setArtifact(art);
    }
    
    public void setArtifact(Artifact artif) {
        artifact = artif;
        setID(artif.getArtifactId() + ":" + artif.getGroupId());
        setDisplayName(artif.getArtifactId());
        setIcon(createNodeIcon(artif.getScope(), artif.getType()));
        setTooltipText("<html>ArtifactID: <b>" + artif.getArtifactId() + 
                       "</b><p>GroupID: <b>" + artif.getGroupId() + 
                       "</b><p>Version: <b>" + artif.getVersion() + 
                       "</b><p>Scope: <b>" + artif.getScope() + 
                       "</b><p>Type: <b>" + artif.getType() + "</b></html>");
        
    }
    
    
    private Image createNodeIcon(String scope, String type) {
        if ("pom".equals(type)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-pom.png");
        }
        if ("jar".equals(type)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-jar.png");
        }
        if ("nbm".equals(type)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-nbm.png");
        }
        if ("ear".equals(type)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-ear.png");
        }
        if ("war".equals(type)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-war.png");
        }
        if ("ejb".equals(type)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-ejb.png");
        }
        if ("maven-plugin".equals(type)) {
            return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-mvn.png");
        }
        return Utilities.loadImage("org/codehaus/mevenide/netbeans/graph/graph-unknown.png");
        
    }
    
    public Artifact getArtifact() {
        return artifact;
    }
    
    
}
