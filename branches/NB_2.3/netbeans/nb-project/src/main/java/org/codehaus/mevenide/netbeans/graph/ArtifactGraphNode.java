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
import org.apache.maven.artifact.Artifact;
import org.netbeans.graph.api.model.IGraphLink;
import org.netbeans.graph.api.model.IGraphPort;
import org.netbeans.graph.api.model.builtin.GraphNode;
import org.netbeans.graph.api.model.builtin.GraphPort;
import org.netbeans.graph.vmd.VMDOrderingLogic;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 */
public class ArtifactGraphNode extends GraphNode implements DependencyGraphNodeLayouter.IRootDistance, 
                                                            DependencyGraphNodeLayouter.IInLinks,
                                                            DependencyGraphNodeLayouter.IOutLinks, 
                                                            DependencyDocumentRenderer.IArtifactGetter {

    private Artifact artifact;
    private GraphPort parentPort;
    private GraphPort childPort;

    private int distance = Integer.MAX_VALUE;
    
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
    
    public GraphPort getChildPort() {
        return childPort;
    }
    
    public GraphPort getParentPort() {
        return parentPort;
    }
    
    public GraphPort createChildPort() {
        if (childPort == null) {
            childPort = new GraphPort();
            childPort.setTarget(true);
            childPort.setPreferredOrderPosition(new Integer(8));
            childPort.setID("Child");
            addPort(childPort);
        }
        return childPort;
    }
    
    public GraphPort createParentPort() {
        if (parentPort == null) {
            parentPort = new GraphPort();
            parentPort.setSource(true);
            parentPort.setTarget(false);
            parentPort.setPreferredOrderPosition(new Integer(0));
            parentPort.setID("Parent");
            addPort(parentPort);
        }
        return parentPort;
    }
    
    public void removeParentPort() {
        if (parentPort != null) {
            removePort(parentPort);
        }
        parentPort = null;
    }
    
    public int getDistanceFromRoot() {
        return distance;
    }
    
    public void setDistanceFromRoot(int inte) {
        distance = inte;
    }

    public IGraphLink[] getOutgoingLinks() {
        IGraphPort par = getParentPort();
        if (par != null) {
            return par.getLinks() != null ? par.getLinks() : new IGraphLink[0];
        }
        return new IGraphLink[0];
    }

    public IGraphLink[] getIncomingLinks() {
        IGraphPort par = getChildPort();
        if (par != null) {
            return par.getLinks() != null ? par.getLinks() : new IGraphLink[0];
        }
        return new IGraphLink[0];
    }

    
}
