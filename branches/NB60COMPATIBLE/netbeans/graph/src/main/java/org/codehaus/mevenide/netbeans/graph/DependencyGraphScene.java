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

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.maven.artifact.Artifact;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.LevelOfDetailsWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyGraphScene extends GraphScene<ArtifactGraphNode, ArtifactGraphEdge> {
    
    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;
    private ArtifactGraphNode rootNode;
    
    private WidgetAction moveAction = ActionFactory.createMoveAction();
    private WidgetAction mouseHoverAction = ActionFactory.createHoverAction(new MyHoverProvider());
    private WidgetAction popupMenuAction = ActionFactory.createPopupMenuAction(new MyPopupMenuProvider());
    private WidgetAction zoomAction = ActionFactory.createZoomAction();
    private WidgetAction panAction = ActionFactory.createPanAction();
    private WidgetAction movetStrategyAction = ActionFactory.createMoveAction(null, new MyMoveStrategy());
    private FruchtermanReingoldLayout layout;
    /** Creates a new instance ofla DependencyGraphScene */
    public DependencyGraphScene() {
        mainLayer = new LayerWidget(this);
        connectionLayer = new LayerWidget(this);
        addChild(mainLayer);
        addChild(connectionLayer);
        
        getActions().addAction(mouseHoverAction);
        getActions().addAction(zoomAction);
        getActions().addAction(panAction);
        getActions().addAction(movetStrategyAction);
//        getActions ().addAction (ActionFactory.createEditAction (new EditProvider() {
//            public void edit (Widget widget) {
//                new TreeGraphLayout<Artifact, String> (DependencyGraphScene.this, 100, 100, 50, 50, true).layout(getRootArtifact());
//            }
//        }));
        
    }
    
    void cleanLayout() {
        layout =  new FruchtermanReingoldLayout(this);
        layout.invokeLayout();
    }
    
    ArtifactGraphNode getRootArtifact() {
        return rootNode;
    }
    
    protected Widget attachNodeWidget(ArtifactGraphNode node) {
        Artifact artifact = node.getArtifact();
        Widget root = new LevelOfDetailsWidget(this, 0.05, 0.1, Double.MAX_VALUE, Double.MAX_VALUE);
        root.setBorder(BorderFactory.createLineBorder(5));
        root.setLayout(LayoutFactory.createVerticalLayout(LayoutFactory.SerialAlignment.JUSTIFY, 1));
        mainLayer.addChild(root);
        LabelWidget lbl = new LabelWidget(this);
        lbl.setLabel(artifact.getArtifactId() + "  ");
//        Font fnt = getDefaultFont();
//        lbl.setFont(fnt.deriveFont((float)(fnt.getSize() * 2)));
        root.addChild(lbl);
        
        root.getActions().addAction(moveAction);
        root.getActions().addAction(mouseHoverAction);
        root.getActions().addAction(popupMenuAction);
        
        Widget details1 = new LevelOfDetailsWidget(this, 0.5, 0.7, Double.MAX_VALUE, Double.MAX_VALUE);
        details1.setLayout(LayoutFactory.createVerticalLayout(LayoutFactory.SerialAlignment.JUSTIFY, 1));
        root.addChild(details1);
        LabelWidget lbl2 = new LabelWidget(this);
        lbl2.setLabel(artifact.getVersion() + "  ");
        details1.addChild(lbl2);
//        LabelWidget lbl3 = new LabelWidget(this);
//        lbl3.setLabel(artifact.getScope());
//        details1.addChild (lbl3);
        if (rootNode == null) {
            rootNode = node;
        }
//        details1.getActions().addAction(movetStrategyAction);
        root.getActions().addAction(movetStrategyAction);
//        lbl.getActions().addAction(movetStrategyAction);
//        lbl2.getActions().addAction(movetStrategyAction);
        
        return root;
    }
    
    protected Widget attachEdgeWidget(ArtifactGraphEdge edge) {
        ConnectionWidget connectionWidget = new ConnectionWidget(this);
        connectionLayer.addChild(connectionWidget);
        connectionWidget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        return connectionWidget;
    }
    
    protected void attachEdgeSourceAnchor(ArtifactGraphEdge edge,
            ArtifactGraphNode oldsource,
            ArtifactGraphNode source) {
        ((ConnectionWidget) findWidget(edge)).setSourceAnchor(AnchorFactory.createRectangularAnchor(findWidget(source)));
        
    }
    
    protected void attachEdgeTargetAnchor(ArtifactGraphEdge edge,
            ArtifactGraphNode oldtarget,
            ArtifactGraphNode target) {
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(AnchorFactory.createRectangularAnchor(findWidget(target)));
    }
    
    private class MyHoverProvider implements TwoStateHoverProvider {
        
        Color hovering = new Color(71, 215, 217);
        Color deps = new Color(154, 215, 217);
        Color parents = new Color(219, 197, 191);
        Color parentsLink = new Color(219, 46, 0);
        
        public void unsetHovering(Widget widget) {
            for (ArtifactGraphNode nd: DependencyGraphScene.this.getNodes()) {
                Widget wid = DependencyGraphScene.this.findWidget(nd);
                wid.setOpaque(false);
                wid.setBackground(Color.WHITE);
            }
            for (ArtifactGraphEdge ed : DependencyGraphScene.this.getEdges()) {
                Widget wid = DependencyGraphScene.this.findWidget(ed);
                wid.setForeground(null);
                wid.repaint();
//                wid.setBackground(null);
            }
        }
        
        public void setHovering(Widget widget) {
            widget.setOpaque(true);
            widget.setBackground(hovering);
            Object sel = DependencyGraphScene.this.findObject(widget);
            if (sel instanceof ArtifactGraphNode) {
                ArtifactGraphNode art = (ArtifactGraphNode)sel;
                for (ArtifactGraphEdge edge : DependencyGraphScene.this.findNodeEdges(art, true, false)) {
                    Widget wid = DependencyGraphScene.this.findWidget(DependencyGraphScene.this.getEdgeTarget(edge));
                    wid.setOpaque(true);
                    DependencyGraphScene.this.getSceneAnimator().animateBackgroundColor(wid, deps);
                }
                markParent(art);
            }
        }
        private void markParent(ArtifactGraphNode target) {
            Collection<ArtifactGraphEdge> col = DependencyGraphScene.this.findNodeEdges(target, false, true);
            for (ArtifactGraphEdge edge : col) {
                ArtifactGraphNode source = DependencyGraphScene.this.getEdgeSource(edge);
                Widget wid = DependencyGraphScene.this.findWidget(source);
                wid.setOpaque(true);
                DependencyGraphScene.this.getSceneAnimator().animateBackgroundColor(wid, parents);
                Widget wid2 = DependencyGraphScene.this.findWidget(edge);
                wid2.setForeground(parentsLink);
//                DependencyGraphScene.this.getSceneAnimator().animateBackgroundColor(wid2, parentsLink);
                DependencyGraphScene.this.getSceneAnimator().animateForegroundColor(wid2, parentsLink);
                markParent(source);
            }
        }
        
    }
    
    private static class MyPopupMenuProvider implements PopupMenuProvider {
        
        public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add(new JMenuItem("Open "));
            return popupMenu;
        }
        
    }
    
    private class MyMoveStrategy implements MoveProvider {
        
        public void movementStarted(Widget arg0) {
        }
        
        public void movementFinished(Widget wid) {
//            Object obj = DependencyGraphScene.this.findObject(wid);
//            if (obj instanceof ArtifactGraphNode) {
//                ArtifactGraphNode art = (ArtifactGraphNode)obj;
//                System.out.println("art=" + art.getArtifact().getId());
//                for (ArtifactGraphNode nd : DependencyGraphScene.this.getNodes()) {
//                    nd.setFixed(false);
//                    nd.dispX = 0;
//                    nd.dispY = 0;
//                    Widget artWid = DependencyGraphScene.this.findWidget(nd);
//                    nd.locX = artWid.getLocation().x;
//                    nd.locY = artWid.getLocation().y;
//                }
//                art.setFixed(true);
////                RequestProcessor.getDefault().post(new Runnable() {
////                    public void run() {
//                        layout.rePerformLayout(10);
//                        for (ArtifactGraphNode nd : DependencyGraphScene.this.getNodes()) {
//                            if (!nd.isFixed()) {
//                                Widget artWid = DependencyGraphScene.this.findWidget(nd);
//                                Point point = new Point();
//                                point.setLocation(nd.locX, nd.locY);
//                                DependencyGraphScene.this.getSceneAnimator().animatePreferredLocation(artWid, point);
//                            } else {
//                                System.out.println("fixed node=" + nd);
//                            }
//                        }
////                    }
////                }, 1000);
//            }
        }
        
        public Point getOriginalLocation(Widget widget) {
            return widget.getPreferredLocation();
        }
        public void setNewLocation(Widget widget, Point location) {
            widget.setPreferredLocation(location);
        }
    }
    
}
