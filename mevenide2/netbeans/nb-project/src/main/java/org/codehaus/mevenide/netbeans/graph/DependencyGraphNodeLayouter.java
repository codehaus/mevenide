/*
 * DependencyGraphNodeLayouter.java
 *
 * Created on February 14, 2006, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.graph;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.graph.api.control.GraphHelper;
import org.netbeans.graph.api.control.IGraphNodesLayouter;
import org.netbeans.graph.api.model.IGraphLink;
import org.netbeans.graph.api.model.IGraphNode;
import org.netbeans.graph.api.model.IGraphPort;

/**
 *
 * @author mkleint
 */
public class DependencyGraphNodeLayouter implements IGraphNodesLayouter {
    
    public interface IRootDistance {
        int getDistanceFromRoot();
    }
    
    public interface IOutLinks {
        IGraphLink[] getOutgoingLinks();
    }
    
    public interface IInLinks {
        IGraphLink[] getIncomingLinks();
    }
    
    /** Creates a new instance of DependencyGraphNodeLayouter */
    public DependencyGraphNodeLayouter() {
    }
    
    public void layoutNodesLocations(Graphics2D graphics, GraphHelper helper, IGraphNode[] nodes) {
        Set currents = new TreeSet(new DistanceComparator());
        currents.addAll(Arrays.asList(nodes));
        IGraphNode root = findRootNode(helper);
        Point rootPoint;
        if (currents.contains(root)) {
            rootPoint =  new Point(700, 700);
            helper.setNodeLocation(root, rootPoint);
        } else {
            rootPoint = helper.getNodeLocation(root);
        }
        currents.remove(root);
        double r = 150;
        double theta = 0;
        double thetaStep = Math.PI / 5;
        while (currents.size() != 0) {
            AffineTransform tr = AffineTransform.getRotateInstance(theta);
            Point2D d2point = tr.transform(new Point2D.Double((double)(r), (double)(r)), null);
            Point point = new Point((int)d2point.getX() + rootPoint.x, (int)d2point.getY() + rootPoint.y);
            IGraphNode nd = findBestFitNode(currents, root);
            currents.remove(nd);
            helper.setNodeLocation(nd, point);
            theta = theta + thetaStep;
            if (theta > (Math.PI * 2 - Math.PI / 10)) {
                r = r + 90;
                theta = theta - Math.PI * 2;
                thetaStep = thetaStep * 3 / 4; 
            }
        }
        
        // at the end align the nodes to the edge..
        int minx = Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        IGraphNode[] nds = helper.getNodes();
        for (int i = 0; i < nds.length; i++) {
            Point pnt = helper.getNodeLocation(nds[i]);
            minx = Math.min(minx, pnt.x);
            miny = Math.min(miny, pnt.y);
        }
        for (int i = 0; i < nds.length; i++) {
            Point pnt = helper.getNodeLocation(nds[i]);
            Point newPoint = new Point(pnt.x - minx + 100, pnt.y - miny + 100);
            helper.setNodeLocation(nds[i], newPoint);
        }
    }
    
    private IGraphNode findRootNode(GraphHelper helper) {
        IGraphNode[] nodes = helper.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            IGraphPort[] ports = nodes[i].getPorts();
            if (ports.length == 1 && ports[0].getID().equals("Parent")) {
                return nodes[i];
            }
        }
        throw new IllegalStateException();
    }

    private IGraphNode findBestFitNode(Set currents, IGraphNode root) {
        IGraphNode nd = (IGraphNode)currents.iterator().next();
        return nd;
    }

    private int getHopsToRoot(IGraphNode nd, IGraphNode root) {
        if (nd == root) {
            return 0;
        }
        IRootDistance dist = (IRootDistance) nd.getLookup().lookup(IRootDistance.class);
        return dist.getDistanceFromRoot();
//        IGraphLink[] links = getChildPort(nd);
//        int min = Integer.MAX_VALUE;
//        for (int i = 0; i < links.length; i++) {
//            int curr = getHopsToRoot(links[i].getSourcePort().getNode(), root);
//            min = Math.min(min, curr);
//        }
//        return min == Integer.MAX_VALUE ? 0 : min + 1;
    }
    
    private IGraphLink[] getChildPort(IGraphNode node) {
        IGraphPort[] ports = node.getPorts();
        if (ports != null) {
            for (int i = 0; i < ports.length; i++) {
                if ("Child".equals(ports[i].getID())) {
                    return ports[i].getLinks() != null ? ports[i].getLinks() : new IGraphLink[0];
                }
            }
        }
        return new IGraphLink[0];
    }
    
    private IGraphLink[] getParentPort(IGraphNode node) {
        IGraphPort[] ports = node.getPorts();
        if (ports != null) {
            for (int i = 0; i < ports.length; i++) {
                if ("Parent".equals(ports[i].getID())) {
                    return ports[i].getLinks() != null ? ports[i].getLinks() : new IGraphLink[0];
                }
            }
        }
        return new IGraphLink[0];
    }
    
    public class DistanceComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            IGraphNode node1 = (IGraphNode)o1;
            IGraphNode node2 = (IGraphNode)o2;
            IInLinks in1 = (IInLinks) node1.getLookup().lookup(IInLinks.class);
            IInLinks in2 = (IInLinks) node2.getLookup().lookup(IInLinks.class);
            int in =  in1.getIncomingLinks().length - in2.getIncomingLinks().length;
            if (in != 0) {
                return in;
            }
            IRootDistance dist1 = (IRootDistance) node1.getLookup().lookup(IRootDistance.class);
            IRootDistance dist2 = (IRootDistance) node2.getLookup().lookup(IRootDistance.class);
            int dist =  dist1.getDistanceFromRoot() - dist2.getDistanceFromRoot();
            if (dist != 0) {
                return dist;
            }
            IOutLinks out1 = (IOutLinks) node1.getLookup().lookup(IOutLinks.class);
            IOutLinks out2 = (IOutLinks) node2.getLookup().lookup(IOutLinks.class);
            int out =  out1.getOutgoingLinks().length - out2.getOutgoingLinks().length;
            if (out != 0) {
                return out;
            }
            return node1.getID().compareTo(node2.getID());
        }
        
    }
    
}
