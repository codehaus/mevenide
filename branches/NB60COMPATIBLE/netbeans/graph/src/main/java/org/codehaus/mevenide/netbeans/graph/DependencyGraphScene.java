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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPopupMenu;
import org.apache.maven.artifact.Artifact;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;
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
    private WidgetAction popupMenuAction = ActionFactory.createPopupMenuAction(new MyPopupMenuProvider());
    private WidgetAction zoomAction = ActionFactory.createCenteredZoomAction(1.1);
    private WidgetAction panAction = ActionFactory.createPanAction();
    private WidgetAction selectAction = ActionFactory.createSelectAction(new MySelectProvider());
    private WidgetAction hoverAction; 
    private FruchtermanReingoldLayout layout;
    
    /** Creates a new instance ofla DependencyGraphScene */
    public DependencyGraphScene() {
        mainLayer = new LayerWidget(this);
        connectionLayer = new LayerWidget(this);
        addChild(mainLayer);
        addChild(connectionLayer);
        hoverAction = createObjectHoverAction();
        
        getActions().addAction(hoverAction);
        getActions().addAction(selectAction);
        getActions().addAction(zoomAction);
        getActions().addAction(panAction);
        addObjectSceneListener(new SceneListener(), ObjectSceneEventType.OBJECT_HOVER_CHANGED, ObjectSceneEventType.OBJECT_SELECTION_CHANGED);
    }

    void cleanLayout() {
        layout =  new FruchtermanReingoldLayout(this);
        layout.invokeLayout();
    }
    
    ArtifactGraphNode getRootArtifact() {
        return rootNode;
    }
    
    protected Widget attachNodeWidget(ArtifactGraphNode node) {
        Widget root = new ArtifactWidget(this, node);
        mainLayer.addChild(root);
        node.setWidget(root);
        if (rootNode == null) {
            rootNode = node;
        }
        root.setOpaque(true);
        
        root.getActions().addAction(hoverAction);
        root.getActions().addAction(moveAction);
        root.getActions().addAction(selectAction);
        root.getActions().addAction(popupMenuAction);
        
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
    
    
    void findNodeByText(String text) {
        clearFind();
        List<ArtifactGraphNode> found = new ArrayList<ArtifactGraphNode>();
        for (ArtifactGraphNode node : getNodes()) {
            Artifact art = node.getArtifact();
            if (art.getId().contains(text)) {
                found.add(node);
            }
        }
        Set<ArtifactGraphNode> toShow = new HashSet<ArtifactGraphNode>();
        toShow.addAll(found);
        for (ArtifactGraphNode nd : found) {
            Widget widget = findWidget(nd);
            widget.setBackground(hovering);
            markParent(nd, found, toShow);
        }
        if (toShow.size() > 0) {
            for (ArtifactGraphNode node : getNodes()) {
                if (!toShow.contains(node)) {
                    findWidget(node).setVisible(false);
                }
            }
            for (ArtifactGraphEdge edge : getEdges()) {
                if (!toShow.contains(getEdgeSource(edge)) || !toShow.contains(getEdgeTarget(edge))) {
                    findWidget(edge).setVisible(false);
                }
            }
        }
    }
    
    void clearFind() {
        for (ArtifactGraphNode nd: getNodes()) {
            Widget wid = DependencyGraphScene.this.findWidget(nd);
            wid.setBackground(Color.WHITE);
            wid.setVisible(true);
        }
        for (ArtifactGraphEdge ed : getEdges()) {
            Widget wid = DependencyGraphScene.this.findWidget(ed);
            wid.setForeground(null);
            wid.repaint();
            wid.setVisible(true);
        }
    }
        Color hovering = new Color(71, 215, 217);
        Color deps = new Color(154, 215, 217);
        Color parents = new Color(219, 197, 191);
        Color parentsLink = new Color(219, 46, 0);
    
        private void markParent(ArtifactGraphNode target, List<ArtifactGraphNode> excludes, Set<ArtifactGraphNode> collect) {
            Collection<ArtifactGraphEdge> col = DependencyGraphScene.this.findNodeEdges(target, false, true);
            for (ArtifactGraphEdge edge : col) {
                ArtifactGraphNode source = DependencyGraphScene.this.getEdgeSource(edge);
                Widget wid2 = DependencyGraphScene.this.findWidget(edge);
                wid2.setForeground(parentsLink);
//                DependencyGraphScene.this.getSceneAnimator().animateBackgroundColor(wid2, parentsLink);
                getSceneAnimator().animateForegroundColor(wid2, parentsLink);
                if (!excludes.contains(source)) {
                    Widget wid = DependencyGraphScene.this.findWidget(source);
                    getSceneAnimator().animateBackgroundColor(wid, parents);
                }
                if (collect != null) {
                    collect.add(source);
                }
                markParent(source, excludes, collect);
            }
        }
    
    
    
    private class MySelectProvider implements SelectProvider {
        
    
        public boolean isAimingAllowed(Widget arg0, Point arg1, boolean arg2) {
            return true;
        }

        public boolean isSelectionAllowed(Widget arg0, Point arg1, boolean arg2) {
            return true;
        }

        public void select(Widget wid, Point arg1, boolean arg2) {
            ArtifactGraphNode node = (ArtifactGraphNode)findObject(wid);
            if (node != null) {
                setSelectedObjects(Collections.singleton(node));
            }
        }
    }
    
    private static class MyPopupMenuProvider implements PopupMenuProvider {
        
        public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
            JPopupMenu popupMenu = new JPopupMenu();
//            popupMenu.add(new JMenuItem("Open "));
            return popupMenu;
        }
        
    }
    
    private static class ArtifactWidget extends Widget {
        private Widget detailsWidget;
        
        private ArtifactWidget(DependencyGraphScene scene, ArtifactGraphNode node) {
            super(scene);
            Artifact artifact = node.getArtifact();
            setLayout (LayoutFactory.createVerticalFlowLayout());
            setOpaque (true);
            setBackground (Color.WHITE);
            setBorder (BorderFactory.createLineBorder (10));
            setToolTipText("<html><i>GroupId:</i><b> " + artifact.getGroupId() + 
                         "</b><br/><i>ArtifactId:</i><b> "+ artifact.getArtifactId() + 
                         "</b><br/><i>Version:</i><b> " + artifact.getVersion() + 
                         "</b><br/><i>Scope:</i><b> " + artifact.getScope() + 
                         "</b><br/><i>Type:</i><b> " + artifact.getType() + 
                    "</b></html>");
            
            Widget root = new LevelOfDetailsWidget(scene, 0.05, 0.1, Double.MAX_VALUE, Double.MAX_VALUE);
            addChild(root);
            root.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 1));
            LabelWidget lbl = new LabelWidget(scene);
            lbl.setLabel(artifact.getArtifactId() + "  ");
//            lbl.setFont(scene.getDefaultFont().deriveFont(Font.BOLD));
            root.addChild(lbl);
        
            Widget details1 = new LevelOfDetailsWidget(scene, 0.5, 0.7, Double.MAX_VALUE, Double.MAX_VALUE);
            details1.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 1));
            root.addChild(details1);
            LabelWidget lbl2 = new LabelWidget(scene);
            lbl2.setLabel(artifact.getVersion() + "  ");
            details1.addChild(lbl2);
        }
    }
    
    public class SceneListener implements ObjectSceneListener {
        
        public void selectionChanged(ObjectSceneEvent state,
                                     Set<Object> oldSet,
                                     Set<Object> newSet) {
            clearFind();
            for (Object obj : newSet) {
                Widget widget = findWidget(obj);
                widget.setBackground(hovering);
                
                if (obj instanceof ArtifactGraphNode) {
                    ArtifactGraphNode art = (ArtifactGraphNode)obj;
                    for (ArtifactGraphEdge edge : findNodeEdges(art, true, false)) {
                        Widget wid = findWidget(DependencyGraphScene.this.getEdgeTarget(edge));
                        wid.setOpaque(true);
                        getSceneAnimator().animateBackgroundColor(wid, deps);
                    }
                    markParent(art, new ArrayList<ArtifactGraphNode>(), new HashSet<ArtifactGraphNode>());
                }
            }
            
        }

        public void objectAdded(ObjectSceneEvent event, Object addedObject) {
        }

        public void objectRemoved(ObjectSceneEvent event, Object removedObject) {
        }

        public void objectStateChanged(ObjectSceneEvent event, Object changedObject, ObjectState previousState, ObjectState newState) {
        }

        public void highlightingChanged(ObjectSceneEvent event, Set<Object> previousHighlighting, Set<Object> newHighlighting) {
        }

        public void hoverChanged(ObjectSceneEvent event, Object previousHoveredObject, Object newHoveredObject) {
            ArtifactGraphNode nd = (ArtifactGraphNode)newHoveredObject;
            if (nd == null) {
                //hide detail component
            } else {
                //show detail component
            }
        }

        public void focusChanged(ObjectSceneEvent event, Object previousFocusedObject, Object newFocusedObject) {
        }

    }
}
