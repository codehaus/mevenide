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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class GraphDocumentFactory {
    
    /** Creates a new instance of GraphDocumentFactory */
    private GraphDocumentFactory() {
    }
    
    /**
     * creates a graph document for transitive dependencies
     */
    static DependencyGraphScene createDependencyDocument(Project project) {
        DependencyGraphScene scene = new DependencyGraphScene();
        try {
                MavenExecutionRequest req = new DefaultMavenExecutionRequest();
                req.setPomFile(FileUtil.toFile(project.getProjectDirectory().getFileObject("pom.xml")).getAbsolutePath());
                MavenExecutionResult res = EmbedderFactory.getOnlineEmbedder().readProjectWithDependencies(req);
                if (res.hasExceptions()) {
                    for (Object e : res.getExceptions()) {
                        Exceptions.printStackTrace((Exception)e);
                        ((Exception)e).printStackTrace();
                    }
                }
                generate(res.getArtifactResolutionResult(), scene);
        } finally {
            return scene;
        }
    }
    
    private static void generate(ArtifactResolutionResult res, DependencyGraphScene scene) {
        Map<Artifact, ArtifactGraphNode> cache = new HashMap<Artifact, ArtifactGraphNode>();
        Set<ResolutionNode> nodes = res.getArtifactResolutionNodes();
        Artifact root = res.getOriginatingArtifact();
        ResolutionNode nd1 = new ResolutionNode(root, new ArrayList());
        ArtifactGraphNode rootNode = getNode(nd1, cache, scene);
        rootNode.setRoot(true);
        for (ResolutionNode nd : nodes) {
            ArtifactGraphNode gr = getNode(nd, cache, scene);
            if (nd.isChildOfRootNode()) {
                String edge = nd1.getArtifact().getId() + "--" + nd.getArtifact().getId();
                ArtifactGraphEdge ed = new ArtifactGraphEdge(edge);
                ed.setLevel(0);
                scene.addEdge(ed);
                scene.setEdgeTarget(ed, gr);
                scene.setEdgeSource(ed, rootNode);
            }
//            if (nd.isResolved()) {
                Iterator<ResolutionNode> it = nd.getChildrenIterator();
                while (it.hasNext()) {
                    ResolutionNode child = it.next();
                    ArtifactGraphNode childNode = getNode(child, cache, scene);
                    String edge = nd.getArtifact().getId() + "--" + child.getArtifact().getId();
                    ArtifactGraphEdge ed = new ArtifactGraphEdge(edge);
                    ed.setLevel(nd.getDepth() + 1);
                    scene.addEdge(ed);
                    scene.setEdgeTarget(ed, childNode);
                    scene.setEdgeSource(ed, gr);
                }
//            }
        }
    }
    
        private static ArtifactGraphNode getNode(ResolutionNode art, Map<Artifact, ArtifactGraphNode> cache, DependencyGraphScene scene) {
            ArtifactGraphNode nd = cache.get(art.getArtifact());
            if (nd == null) {
                nd = new ArtifactGraphNode(art);
                cache.put(art.getArtifact(), nd);
                scene.addNode(nd);
            }
            return nd;
        }
    
    
    
}
