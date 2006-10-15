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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
        Set currents = new TreeSet(new ComplexComparator());
        currents.addAll(Arrays.asList(nodes));
        IGraphNode root = findRootNode(helper);
        Point rootPoint;
        if (currents.contains(root)) {
            rootPoint =  new Point(1000, 1000);
            helper.setNodeLocation(root, rootPoint);
        } 
        TreeMap map = groupOneParented(currents);
        currents.remove(root);
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry elem = (Map.Entry) it.next();
            IGraphNode master = (IGraphNode) elem.getKey();
        
            Collection childs = (Collection) elem.getValue();
            layoutCirculary(childs, master, root, helper);
            currents.removeAll(childs);
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
    
    private void layoutCirculary(Collection nodes, IGraphNode master, IGraphNode root, GraphHelper helper) {
        Point masterPoint = helper.getNodeLocation(master);
            double r;
            double theta;
            double thetaStep = Math.PI / 5;
        if (root == master) {
            r = 150;
            theta = 0;
        } else {
            r = 150;
            theta = 0;
        }
        Iterator it = nodes.iterator();
        IGraphNode nd = (IGraphNode)it.next();
        while (true) {
            AffineTransform tr = AffineTransform.getRotateInstance(theta);
            Point2D d2point = tr.transform(new Point2D.Double((double)(0), (double)(r)), null);
            Point point = new Point((int)d2point.getX() + masterPoint.x, (int)d2point.getY() + masterPoint.y);
            if (isThereFreeSpace(point, nd, helper)) {
                helper.setNodeLocation(nd, point);
                if (it.hasNext()) {
                    nd = (IGraphNode)it.next();
                } else {
                    return;
                }
            }
            theta = theta + thetaStep;
            if (theta > (Math.PI * 2 - Math.PI / 10)) {
                r = r + 90;
                theta = theta - Math.PI * 2;
                thetaStep = thetaStep * 3 / 4; 
            }
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

    private static int getDistance(IGraphNode nd) {
        IRootDistance dist = (IRootDistance) nd.getLookup().lookup(IRootDistance.class);
        return dist.getDistanceFromRoot();
    }
    private static IGraphLink[] getOutgoingLinks(IGraphNode nd) {
        IOutLinks out = (IOutLinks) nd.getLookup().lookup(IOutLinks.class);
        return out.getOutgoingLinks();
    }
    
    private static IGraphLink[] getIncomingLinks(IGraphNode nd) {
        IInLinks in = (IInLinks) nd.getLookup().lookup(IInLinks.class);
        return in.getIncomingLinks();
    }
    
    private static boolean isThereFreeSpace(Point pnt, IGraphNode node, GraphHelper helper) {
        Rectangle bnds = new Rectangle(0,0, 100, 150);
        bnds = new Rectangle(pnt.x, pnt.y, bnds.width, bnds.height);
        IGraphNode[] nds = helper.getNodes();
        for (int i = 0; i < nds.length; i++) {
            Rectangle bounds = helper.getBounds(nds[i]);
            if (bounds == null) {
                bounds = new Rectangle(helper.getNodeLocation(nds[i]), new Dimension(150, 200));
            }
            if (bnds.intersects((bounds))) {
                return false;
            }
        }
        return true;
    }

    private TreeMap groupOneParented(Set currents) {
        TreeMap map = new TreeMap(new DistanceComparator());
        Iterator it = currents.iterator();
        while (it.hasNext()) {
            IGraphNode master = (IGraphNode) it.next();
            IGraphLink[] outs = getOutgoingLinks(master);
            if (outs.length > 0) {
                Collection col = new ArrayList();
                for (int i = 0; i < outs.length; i++) {
                   IGraphNode child = outs[i].getTargetPort().getNode();
//                   if (getIncomingLinks(child).length == 1) {
                       col.add(child);
//                   }
                }
                if (col.size() > 0) {
                    map.put(master, col);
                }
            }
        }
        return map;
    }
    
    public class ComplexComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            IGraphNode node1 = (IGraphNode)o1;
            IGraphNode node2 = (IGraphNode)o2;
            int in =  getIncomingLinks(node1).length - getIncomingLinks(node2).length;
            if (in != 0) {
                return in;
            }
            int dist =  getDistance(node1) - getDistance(node2);
            if (dist != 0) {
                return dist;
            }
            int out =  getOutgoingLinks(node1).length - getOutgoingLinks(node2).length;
            if (out != 0) {
                return out;
            }
            return node1.getID().compareTo(node2.getID());
        }
        
    }
    
    public class DistanceComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            IGraphNode node1 = (IGraphNode)o1;
            IGraphNode node2 = (IGraphNode)o2;
            int dist =  getDistance(node1) - getDistance(node2);
            if (dist != 0) {
                return dist;
            }
            return node1.getID().compareTo(node2.getID());
        }
        
    }
    
}
