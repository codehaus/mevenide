/*
 * JungDagLayout.java
 *
 * Created on February 16, 2007, 10:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.graph;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserDataContainer.CopyAction;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.SpringLayout.LengthFunction;
import edu.uci.ics.jung.visualization.VertexLocationFunction;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author mkleint
 */
public class JungDagLayout extends SceneLayout {
    private DirectedSparseGraph graph;
    private final int magicSizeMultiplier = 20;
    private final int magicSizeConstant = 400;
    Rectangle bounds;
    private DependencyGraphScene scene;
    private MyDAG layout;
    
    double repulsionRange = 200;
    double forceMultiplier = 0.9; //0r 0.1
    double stretch = 0.8;
    
    /** Creates a new instance of JungDagLayout */
    public JungDagLayout(DependencyGraphScene scene) {
        super(scene);
        this.scene = scene;
    }
    
    /**
     *
     */
    protected void performLayout() {
        graph = new DirectedSparseGraph();
        List<DirectedSparseVertex> vertices = new ArrayList<DirectedSparseVertex>();
        for (ArtifactGraphNode nd : scene.getNodes()) {
            DirectedSparseVertex vertex = new DirectedSparseVertex();
            vertex.setUserDatum("MK", nd, new CopyAction.Shared());
            graph.addVertex(vertex);
            vertices.add(vertex);
            nd.putUserData("MK", vertex);
        }
        for (ArtifactGraphEdge edge : scene.getEdges()) {
            ArtifactGraphNode source = scene.getEdgeSource(edge);
            ArtifactGraphNode target = scene.getEdgeTarget(edge);
            Vertex v1 = (Vertex)source.getuserData("MK");
            Vertex v2 = (Vertex)target.getuserData("MK");
            DirectedSparseEdge ed = new DirectedSparseEdge(v1, v2);
            try {
                graph.addEdge(ed);
            } catch (Throwable x) {
                System.out.println("duplicate edge s=" + source.getArtifact()  + " t=" + target.getArtifact());
            }
        }
        int nds = scene.getNodes().size();
        bounds = new Rectangle(magicSizeConstant  + (magicSizeMultiplier * nds),
                magicSizeConstant  + (magicSizeMultiplier * nds)); //g.getMaximumBounds();
        
        
        layout = new MyDAG(graph, new Len());
        layout.setStretch(stretch);
        layout.setRepulsionRange((int)repulsionRange);
        layout.setForceMultiplier(forceMultiplier);
        layout.initialize(new Dimension(bounds.width, bounds.height), new VertexLocFunction(vertices, new Dimension(bounds.width, bounds.height)));
//        for (int i = 0; i < 500; i++) {
//            layout.advancePositions();
//        }
        for (ArtifactGraphNode n : scene.getNodes()) {
            Widget wid = scene.findWidget(n);
            Point point = new Point();
            Vertex v = (Vertex)n.getuserData("MK");
            point.setLocation(layout.getX(v), layout.getY(v));
            wid.setPreferredLocation(point);
        }
    }
    
    private class MyDAG extends SpringLayout  {
        MyDAG(Graph gr, LengthFunction len) {
            super(gr);
            lengthFunction = len;
        }
    }
    
    private class Len implements LengthFunction {
        public double getLength(Edge e) {
            Vertex ver = (Vertex)e.getEndpoints().getSecond();
            ArtifactGraphNode nd = (ArtifactGraphNode)ver.getUserDatum("MK");
            if (scene.findNodeEdges(nd, true, false).size() == 0) {
                return 200;
            };
            return 300;
        }
    }
    
    private class VertexLocFunction implements VertexLocationFunction {
        private List<DirectedSparseVertex> list;
        double r;
        double theta;
        double thetaStep = Math.PI / 5;
        private Point masterPoint;
        private Map<ArtifactGraphNode, Point> locations;
        VertexLocFunction(List<DirectedSparseVertex> lst, Dimension dim) {
            list = lst;
            r = 150;
            theta = 0;
            masterPoint = new Point(dim.width /2 , dim.height /2 );
            locations = new HashMap<ArtifactGraphNode, Point>();
        }
        
        public Point2D getLocation(ArchetypeVertex nd) {
            while (true) {
                AffineTransform tr = AffineTransform.getRotateInstance(theta);
                Point2D d2point = tr.transform(new Point2D.Double((double)(0), (double)(r)), null);
                Point point = new Point((int)d2point.getX() + masterPoint.x, (int)d2point.getY() + masterPoint.y);
                ArtifactGraphNode node = (ArtifactGraphNode)nd.getUserDatum("MK");
                if (isThereFreeSpace(point, node)) {
                    locations.put(node, point);
                    return point;
                }
                theta = theta + thetaStep;
                if (theta > (Math.PI * 2 - Math.PI / 10)) {
                    r = r + 90;
                    theta = theta - Math.PI * 2;
                    thetaStep = thetaStep * 3 / 4;
                }
            }
        }
        
        public Iterator getVertexIterator() {
            return list.iterator();
        }
        
        private boolean isThereFreeSpace(Point pnt, ArtifactGraphNode node) {
            Rectangle bnds = scene.findWidget(node).getPreferredBounds();
            bnds = new Rectangle(pnt.x, pnt.y, bnds.width, bnds.height);
            for (ArtifactGraphNode nd : locations.keySet()) {
                Rectangle bounds = scene.findWidget(nd).getPreferredBounds();
                Point point = locations.get(nd);
                bounds = new Rectangle(point, bounds.getSize());
                if (bnds.intersects((bounds))) {
                    return false;
                }
            }
            return true;
        }
        
    }
}
