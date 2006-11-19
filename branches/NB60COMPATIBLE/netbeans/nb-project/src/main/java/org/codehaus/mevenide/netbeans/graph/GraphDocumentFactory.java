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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.embedder.MyResolutionListener;
import org.netbeans.api.visual.graph.GraphScene;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class GraphDocumentFactory {
    
    /** Creates a new instance of GraphDocumentFactory */
    private GraphDocumentFactory() {
    }
    
    /**
     * create a graph document, recursively iterating through the projects' modules
     * declarations..
     */
    static GraphScene createModuleDocument(NbMavenProject project) {
        return null;
    }
    
    /**
     * creates a graph document for transitive dependencies
     */
    static DependencyGraphScene createDependencyDocument(NbMavenProject project) {
        DependencyGraphScene scene = new DependencyGraphScene();
        GraphResolutionListener listener = new GraphResolutionListener(scene);
        try {
            MyResolutionListener.setDelegateResolutionListener(listener);
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
            MyResolutionListener.clearDelegateResolutionListener();
            return scene;
        }
    }
    
//    private static void createSubnodes(final GraphDocument doc, final GraphPort parentport, NbMavenProject prj) {
//        Iterator it = loadModules(prj).iterator();
//        while (it.hasNext()) {
//            NbMavenProject proj = (NbMavenProject)it.next();
//            GraphNode node = new ProjectGraphNode(proj);
//            doc.addComponents(GraphEvent.createSingle(node));
//            GraphPort port = new GraphPort();
//            port.setTarget(true);
//            port.setDirection(IDirectionable.LEFT);
//            // WTF is the order position and how it's calculated???
//            port.setPreferredOrderPosition(new Integer(8));
//            node.addPort(port);
//            GraphLink link = new GraphLink();
//            link.setSourcePort(parentport);
//            link.setTargetPort(port);
//            doc.addComponents(GraphEvent.createSingle(link));
//            if ("pom".equalsIgnoreCase(proj.getOriginalMavenProject().getPackaging())) {
//                GraphPort myparent = new GraphPort();
//                myparent.setSource(true);
//                myparent.setDirection(IDirectionable.RIGHT);
//                myparent.setPreferredOrderPosition(new Integer(0));
//                node.addPort(myparent);
//                node.setDefaultPort(myparent);
//                createSubnodes(doc, myparent, proj);
//            }
//        }
//    }
    
    
    
//    private static Collection loadModules(NbMavenProject prj) {
//        Collection modules = new ArrayList();
//        File base = prj.getOriginalMavenProject().getBasedir();
//        for (Iterator it = prj.getOriginalMavenProject().getModules().iterator(); it.hasNext();) {
//            String elem = (String) it.next();
//            File projDir = FileUtil.normalizeFile(new File(base, elem));
//            FileObject fo = FileUtil.toFileObject(projDir);
//            if (fo != null) {
//                try {
//                    Project childproj = ProjectManager.getDefault().findProject(fo);
//                    if (childproj instanceof NbMavenProject) {
//                        modules.add(childproj);
//                    }
//                } catch (IllegalArgumentException ex) {
//                    ex.printStackTrace();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            } else {
//                //TODO broken module reference.. show as such..
//            }
//        }
//        return modules;
//    }
    
    
    private static class GraphResolutionListener implements ResolutionListener {
        private DependencyGraphScene scene;
        private List<Artifact> parentChain;
        private Map<Artifact, ArtifactGraphNode> cache = new HashMap<Artifact, ArtifactGraphNode>();
        
        public GraphResolutionListener(DependencyGraphScene sc) {
            scene = sc;
            parentChain = new ArrayList<Artifact>();
        }
        
        public void testArtifact(Artifact node) {
        }
        
        public void startProcessChildren(Artifact artifact) {
            parentChain.add(artifact);
        }
        
        public void endProcessChildren(Artifact artifact) {
            parentChain.remove(artifact);;
        }
        
        private ArtifactGraphNode getNode(Artifact art) {
            ArtifactGraphNode nd = cache.get(art);
            if (nd == null) {
                nd = new ArtifactGraphNode(art);
                cache.put(art, nd);
            }
            return nd;
        }
        
        public void includeArtifact(Artifact artifact) {
            if (!scene.isNode(getNode(artifact))) {
                scene.addNode(getNode(artifact));
            }
            if (parentChain.size() > 0) {
                Artifact parent = parentChain.get(parentChain.size() - 1);
                String edge = parent.getId() + "--" + artifact.getId();
                ArtifactGraphEdge ed = new ArtifactGraphEdge(edge);
                scene.addEdge(ed);
                scene.setEdgeTarget(ed, getNode(artifact));
                scene.setEdgeSource(ed, getNode(parent));
            } else {
                getNode(artifact).setRoot(true);
            }
        }
        
        public void omitForNearer(Artifact omitted, Artifact kept) {
            if (!scene.isNode(getNode(kept))) {
                scene.addNode(getNode(kept));
            }
            if (parentChain.size() > 0) {
                Artifact parent = parentChain.get(parentChain.size() - 1);
                String edge = parent.getId() + "--" + omitted.getId() + "++" + omitted.hashCode();
                ArtifactGraphEdge ed = new ArtifactGraphEdge(edge);
                scene.addEdge(ed);
                scene.setEdgeTarget(ed, getNode(kept));
                scene.setEdgeSource(ed, getNode(parent));
                //TODO mark the ommited..
            } 
            if (scene.isNode(getNode(omitted))) {
                scene.removeNode(getNode(omitted));
            }
        }
        
        public void updateScope(Artifact artifact, String scope) {
        }
        
        public void manageArtifact(Artifact artifact, Artifact replacement) {
            System.out.println("manage=" + artifact);
            if (!scene.isNode(getNode(replacement))) {
                scene.addNode(getNode(replacement));
            }
            Collection<ArtifactGraphEdge> edges = scene.getEdges();
            for (ArtifactGraphEdge edge : edges) {
                if (getNode(artifact) == scene.getEdgeSource(edge)) {
                    System.out.println("new source" + edge);
                    scene.setEdgeSource(edge, getNode(replacement));
                }
                if (getNode(artifact) == scene.getEdgeTarget(edge)) {
                    System.out.println("new target" + edge);
                    scene.setEdgeTarget(edge, getNode(replacement));
                }
            }
            if (scene.isNode(getNode(artifact))) {
                scene.removeNode(getNode(artifact));
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
        
    }
    
}
