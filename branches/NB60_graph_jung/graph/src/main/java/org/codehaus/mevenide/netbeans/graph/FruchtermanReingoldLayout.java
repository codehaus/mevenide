/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.widget.Widget;


/**
 * Layout instance implementing the FruchtermanReingold algorithm 
 * http://mtc.epfl.ch/~beyer/CCVisu/manual/main005.html
 * 
 * Inspired by implementations at JUNG and Prefuse.
 */
public class FruchtermanReingoldLayout extends SceneLayout {

    private double forceConstant;
    private double temp;
    private int iterations = 700;
    private final int magicSizeMultiplier = 8;
    private final int magicSizeConstant = 200;
    private Rectangle bounds;
    protected int m_fidx;
    
    private static final double MIN = 0.000001D;
    private static final double ALPHA = 0.1;
    private DependencyGraphScene scene;
    
    public FruchtermanReingoldLayout(DependencyGraphScene scene) {
        super(scene);
        iterations = 700;
        this.scene = scene;
        init();
    }
    
    public void performLayout() {
        performLayout(true);
    }
    
    private void performLayout(boolean finish) {
        for (int i=0; i < iterations; i++ ) {
                
            for (ArtifactGraphNode n : scene.getNodes()) {
                if (n.isFixed()) continue;
                calcRepulsion(n);
            }
            for (ArtifactGraphEdge e : scene.getEdges()) {
                calcAttraction(e);
            }
            for (ArtifactGraphNode n : scene.getNodes()) {
                if (n.isFixed()) continue;
                calcPositions(n);
            }
            cool(i);
        }
        if (finish) {
            finish();
        }
    }
    
    
    private void init() {
        int nds = scene.getNodes().size();
        bounds = new Rectangle(magicSizeConstant  + (magicSizeMultiplier * nds), 
                               magicSizeConstant  + (magicSizeMultiplier * nds)); //g.getMaximumBounds();
        temp = bounds.getWidth() / 10;
        forceConstant = 0.75 * Math.sqrt(bounds.getHeight() * bounds.getWidth() / nds);
        System.out.println("force constant=" + forceConstant);
        ArtifactGraphNode r = scene.getRootArtifact();
        r.locX = bounds.getCenterX();
        r.locY = bounds.getCenterY();
        r.setFixed(true);
        layoutCirculary(scene.getNodes(), r);
    }
    
    private void finish() {
        for (ArtifactGraphNode n : scene.getNodes()) {
            Widget wid = scene.findWidget(n);
            Point point = new Point();
            point.setLocation(n.locX, n.locY);
            wid.setPreferredLocation(point);
        }
    }
    
    public void calcPositions(ArtifactGraphNode n) {
        double deltaLength = Math.max(MIN,
                Math.sqrt(n.dispX * n.dispX + n.dispY * n.dispY));
        
        double xDisp = n.dispX/deltaLength * Math.min(deltaLength, temp);

        double yDisp = n.dispY/deltaLength * Math.min(deltaLength, temp);
        
        n.locX += xDisp;
        n.locY += yDisp;
    }

    public void calcAttraction(ArtifactGraphEdge e) {
        ArtifactGraphNode n1 = scene.getEdgeSource(e);
        ArtifactGraphNode n2 = scene.getEdgeTarget(e);
        assert (n1 != null && n2 != null) : "wrong edge=" + e;
        double xDelta = n1.locX - n2.locX;
        double yDelta = n1.locX - n2.locY;

        double deltaLength = Math.max(MIN, Math.sqrt(xDelta*xDelta + yDelta*yDelta));
        double force =  (deltaLength * deltaLength) / forceConstant;
        Widget wid1 = scene.findWidget(n1);
        Rectangle rect1 = new Rectangle(new Point((int)n1.locX, (int)n1.locY),  wid1.getPreferredBounds().getSize());
        Widget wid2 = scene.findWidget(n2);
        Rectangle rect2 = new Rectangle(new Point((int)n2.locX, (int)n2.locY),  wid2.getPreferredBounds().getSize());
        if (rect1.intersects(rect2)) {
            force = force / forceConstant;
        }
        Collection col = scene.findNodeEdges(n2, true, false);
        if (col.size() == 0) {
            force = force * deltaLength;
            System.out.println("force for single childs is =" + force);
        }

        double xDisp = (xDelta / deltaLength) * force;
        double yDisp = (yDelta / deltaLength) * force;
        
        n1.dispX -= xDisp; 
        n1.dispY -= yDisp;
        n2.dispX += xDisp; 
        n2.dispY += yDisp;
    }

    public void calcRepulsion(ArtifactGraphNode n1) {
        n1.dispX = 0.0; 
        n1.dispY = 0.0;
//        Widget wid1 = scene.findWidget(n1);
//        Rectangle rect1 = wid1.getBounds();

        for (ArtifactGraphNode n2 : scene.getNodes()) {
            //TODO..
//            if (n2.isFixed()) continue;
            if (n1 != n2) {
                double xDelta = n1.locX - n2.locX;
                double yDelta = n1.locY - n2.locY;
                double deltaLength = Math.max(MIN, Math.sqrt(xDelta*xDelta + yDelta*yDelta));
                double force = (forceConstant * forceConstant) / deltaLength;
                Widget wid1 = scene.findWidget(n1);
                Rectangle rect1 = new Rectangle(new Point((int)n1.locX, (int)n1.locY),  wid1.getPreferredBounds().getSize());
                Widget wid2 = scene.findWidget(n2);
                Rectangle rect2 = new Rectangle(new Point((int)n2.locX, (int)n2.locY),  wid2.getPreferredBounds().getSize());
                if (rect1.intersects(rect2)) {
                    force = force * forceConstant;
                }
                n1.dispX += (xDelta / deltaLength) * force;
                n1.dispY += (yDelta / deltaLength) * force;
            }
        }
    }
    
    /**
     * this "cools" down the forces causing smaller movements..
     */
    private void cool(int iter) {
        temp *= (1.0 - iter / (double) iterations);
    }
    
    
    private void layoutCirculary(Collection<ArtifactGraphNode> nodes, ArtifactGraphNode master) {
        Point masterPoint = new Point();
        masterPoint.setLocation(master.locX, master.locY);
        double r;
        double theta;
        double thetaStep = Math.PI / 5;
        r = 150;
        theta = 0;
        Iterator<ArtifactGraphNode> it = nodes.iterator();
        ArtifactGraphNode nd = it.next();
        while (true) {
            AffineTransform tr = AffineTransform.getRotateInstance(theta);
            Point2D d2point = tr.transform(new Point2D.Double((double)(0), (double)(r)), null);
            Point point = new Point((int)d2point.getX() + masterPoint.x, (int)d2point.getY() + masterPoint.y);
            if (isThereFreeSpace(point, nd)) {
                nd.locX = point.getX();
                nd.locY = point.getY();
                nd.dispX = 0;
                nd.dispY = 0;
                if (it.hasNext()) {
                    nd = it.next();
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
    
    private boolean isThereFreeSpace(Point pnt, ArtifactGraphNode node) {
        Rectangle bnds = scene.findWidget(node).getPreferredBounds();
        bnds = new Rectangle(pnt.x, pnt.y, bnds.width, bnds.height);
        for (ArtifactGraphNode nd : scene.getNodes()) {
            Rectangle bounds = scene.findWidget(nd).getPreferredBounds();
            Point point = new Point();
            point.setLocation(nd.locX, nd.locY);
            bounds = new Rectangle(point, bounds.getSize());
            if (bnds.intersects((bounds))) {
                return false;
            }
        }
        return true;
    }

    
    private boolean isThereFreeSpaceNonFixedSpace(ArtifactGraphNode node) {
        Rectangle bnds = scene.findWidget(node).getPreferredBounds();
        Point pnt = new Point();
        pnt.setLocation(node.locX, node.locY);
        bnds = new Rectangle(pnt, bnds.getSize());
        for (ArtifactGraphNode nd : scene.getNodes()) {
            Rectangle bounds = scene.findWidget(nd).getPreferredBounds();
            Point point = new Point();
            point.setLocation(nd.locX, nd.locY);
            bounds = new Rectangle(point, bounds.getSize());
            if (nd.isFixed() && bnds.intersects((bounds))) {
                return false;
            }
        }
        return true;
    }
    
    
    
    
} 