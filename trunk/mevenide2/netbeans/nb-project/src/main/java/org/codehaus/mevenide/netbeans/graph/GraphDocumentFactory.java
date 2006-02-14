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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.graph.api.model.GraphEvent;
import org.netbeans.graph.api.model.IGraphPort;
import org.netbeans.graph.api.model.ability.IDirectionable;
import org.netbeans.graph.api.model.builtin.GraphDocument;
import org.netbeans.graph.api.model.builtin.GraphLink;
import org.netbeans.graph.api.model.builtin.GraphNode;
import org.netbeans.graph.api.model.builtin.GraphPort;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkleint
 */
public class GraphDocumentFactory {
    
    /** Creates a new instance of GraphDocumentFactory */
    private GraphDocumentFactory() {
    }
    
    /**
     * create a graph document, recursively iterating through the projects' modules
     * declarations..
     */
    static GraphDocument createModuleDocument(NbMavenProject project) {
        GraphDocument doc = new GraphDocument();
        GraphNode parentnode = new ProjectGraphNode(project);
        doc.addComponents(GraphEvent.createSingle(parentnode));
        GraphPort parentport = new GraphPort();
        parentport.setSource(true);
        parentport.setDirection(IDirectionable.RIGHT);
        parentport.setPreferredOrderPosition(new Integer(IDirectionable.RIGHT));
        parentnode.addPort(parentport);
        parentnode.setDefaultPort(parentport);
        createSubnodes(doc, parentport, project);
        return doc;
    }
    
    /**
     * creates a graph document for transitive dependencies
     */
    static GraphDocument createDependencyDocument(NbMavenProject project) {
        GraphDocument doc = new GraphDocument();
        GraphResolutionListener listener = new GraphResolutionListener();
        try {
            EmbedderFactory.getProjectResolutionListener().setDelegateResolutionListener(listener);
            EmbedderFactory.getProjectEmbedder().readProjectWithDependencies(project.getPOMFile());
        } catch (ArtifactNotFoundException ex) {
            ex.printStackTrace();
        } catch (ArtifactResolutionException ex) {
            ex.printStackTrace();
        } catch (MavenEmbedderException ex) {
            ex.printStackTrace();
        } catch (ProjectBuildingException ex) {
            ex.printStackTrace();
        } catch (Throwable ex) {
            System.out.println("throwable=" + ex.getClass());
            ex.printStackTrace();
        } finally {
            EmbedderFactory.getProjectResolutionListener().clearDelegateResolutionListener();
            doc.addComponents(GraphEvent.create((GraphNode[])listener.getNodes().toArray(new GraphNode[listener.getNodes().size()]),
                    (GraphLink[])listener.getLinks().toArray(new GraphLink[listener.getLinks().size()])));
            return doc;
        }
    }
    
    private static void createSubnodes(final GraphDocument doc, final GraphPort parentport, NbMavenProject prj) {
        Iterator it = loadModules(prj).iterator();
        while (it.hasNext()) {
            NbMavenProject proj = (NbMavenProject)it.next();
            GraphNode node = new ProjectGraphNode(proj);
            doc.addComponents(GraphEvent.createSingle(node));
            GraphPort port = new GraphPort();
            port.setTarget(true);
            port.setDirection(IDirectionable.LEFT);
            // WTF is the order position and how it's calculated???
            port.setPreferredOrderPosition(new Integer(8));
            node.addPort(port);
            GraphLink link = new GraphLink();
            link.setSourcePort(parentport);
            link.setTargetPort(port);
            doc.addComponents(GraphEvent.createSingle(link));
            if ("pom".equalsIgnoreCase(proj.getOriginalMavenProject().getPackaging())) {
                GraphPort myparent = new GraphPort();
                myparent.setSource(true);
                myparent.setDirection(IDirectionable.RIGHT);
                myparent.setPreferredOrderPosition(new Integer(0));
                node.addPort(myparent);
                node.setDefaultPort(myparent);
                createSubnodes(doc, myparent, proj);
            }
        }
    }
    
    
    
    private static Collection loadModules(NbMavenProject prj) {
        Collection modules = new ArrayList();
        File base = prj.getOriginalMavenProject().getBasedir();
        for (Iterator it = prj.getOriginalMavenProject().getModules().iterator(); it.hasNext();) {
            String elem = (String) it.next();
            File projDir = FileUtil.normalizeFile(new File(base, elem));
            FileObject fo = FileUtil.toFileObject(projDir);
            if (fo != null) {
                try {
                    Project childproj = ProjectManager.getDefault().findProject(fo);
                    if (childproj instanceof NbMavenProject) {
                        modules.add(childproj);
                    }
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                //TODO broken module reference.. show as such..
            }
        }
        return modules;
    }
    
    
    private static class GraphResolutionListener implements ResolutionListener {
        private ArrayList nodes;
        private ArrayList links;
        private ArrayList parentChain;
        public GraphResolutionListener() {
            nodes = new ArrayList();
            links = new ArrayList();
            parentChain = new ArrayList();
        }
        
        public List getNodes() {
            return nodes;
        }
        
        public List getLinks() {
            return links;
        }
        
        private ArtifactGraphNode findNode(Artifact art) {
            Iterator it = nodes.iterator();
            while (it.hasNext()) {
                ArtifactGraphNode nd = (ArtifactGraphNode) it.next();
                if (nd.getArtifact().equals(art)) {
                    return nd;
                }
            }
            return null;
        }
        
        public void testArtifact(Artifact node) {
        }
        
        public void startProcessChildren(Artifact artifact) {
            GraphNode node = findNode(artifact);
            parentChain.add(node);
            GraphPort port = getParentPort(node);
            if (port == null) {
                port = new GraphPort();
                port.setID("Parent");
                port.setPreferredOrderPosition(new Integer(0));
                node.addPort(port);
            }
//            node.setDefaultPort(port);
        }
        
        public void endProcessChildren(Artifact artifact) {
            GraphNode node = findNode(artifact);
            parentChain.remove(node);
            GraphPort port = getParentPort(node);
            if (port.getLinks() == null || port.getLinks().length == 0) {
                node.removePort(port);
            }
            
        }
        
        public void includeArtifact(Artifact artifact) {
            ArtifactGraphNode node = (ArtifactGraphNode)findNode(artifact);
            if (node == null) {
                node = new ArtifactGraphNode(artifact);
                nodes.add(node);
            }
            if (parentChain.size() != 0) {
                GraphPort port = getChildPort(node);
                if (port == null) {
                    port = new GraphPort();
                    port.setTarget(true);
                    port.setPreferredOrderPosition(new Integer(8));
                    port.setID("Child");
                    node.addPort(port);
                }
                GraphNode parent = (GraphNode)parentChain.get(parentChain.size() - 1);
                GraphPort parentPort = getParentPort(parent);
                GraphLink link = new GraphLink();
                link.setSourcePort(parentPort);
                link.setTargetPort(port);
                link.setID(((ArtifactGraphNode)parent).getArtifact().getId() + ":" + artifact.getId());
                link.setTooltipText(link.getID());
                link.setDisplayName(link.getID());
                links.add(link);
            }
        }
        
        public void omitForNearer(Artifact omitted, Artifact kept) {
            GraphNode node = findNode(kept);
            if (node == null) {
                node = findNode(omitted);
                ((ArtifactGraphNode)node).setArtifact(kept);
            }
            assert node != null : "non existant kept= " + kept + " omitted=" + omitted;
            GraphPort port = getChildPort(node);
            GraphNode parent = (GraphNode)parentChain.get(parentChain.size() - 1);
            GraphPort parentPort = getParentPort(parent);
            GraphLink link = new GraphLink();
//            link.setDisplayName("Omitted was " + omitted);
            link.setID(((ArtifactGraphNode)parent).getArtifact().getId() + ":" + kept.getId());
            link.setDisplayName(link.getID());
            link.setTooltipText(link.getID());
            link.setSourcePort(parentPort);
            link.setTargetPort(port);
            links.add(link);
        }
        
        public void updateScope(Artifact artifact, String scope) {
        }
        
        public void manageArtifact(Artifact artifact, Artifact replacement) {
            System.out.println("MANAGING=" + replacement);
            ArtifactGraphNode node = (ArtifactGraphNode)findNode(artifact);
            System.out.println("artifact = " + artifact);
            if (node == null) {
                includeArtifact(replacement);
            } else {
                node.setArtifact(replacement);
            }
        }
        
        public void omitForCycle(Artifact artifact) {
            System.out.println("OMIT for CYCLE "+ artifact);
        }
        
        public void updateScopeCurrentPom(Artifact artifact, String scope) {
        }
        
        public void selectVersionFromRange(Artifact artifact) {
        }
        
        public void restrictRange(Artifact artifact, Artifact replacement, VersionRange newRange) {
            System.out.println("RESTRICT RANGE " + artifact + " repl=" + replacement + " range=" + newRange);
        }
        
        private GraphPort getParentPort(GraphNode parent) {
            IGraphPort[] ports = parent.getPorts();
            if (ports != null) {
                for (int i = 0; i < ports.length; i++) {
                    if ("Parent".equals(ports[i].getID())) {
                        return (GraphPort)ports[i];
                    }
                }
            }
            return null;
        }
        
        private GraphPort getChildPort(GraphNode node) {
            IGraphPort[] ports = node.getPorts();
            if (ports != null) {
                for (int i = 0; i < ports.length; i++) {
                    if ("Child".equals(ports[i].getID())) {
                        return (GraphPort)ports[i];
                    }
                }
            }
            return null;
        }
        
    }
    
}
