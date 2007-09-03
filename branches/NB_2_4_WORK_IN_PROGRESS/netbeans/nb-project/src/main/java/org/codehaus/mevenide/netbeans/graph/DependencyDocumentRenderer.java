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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import org.apache.maven.artifact.Artifact;
import org.netbeans.graph.api.control.GraphHelper;
import org.netbeans.graph.api.control.IGraphLinkRenderer;
import org.netbeans.graph.api.control.IGraphLinkRouter;
import org.netbeans.graph.api.control.IGraphNodeRenderer;
import org.netbeans.graph.api.control.IGraphNodesLayouter;
import org.netbeans.graph.api.control.editor.IGraphEditor;
import org.netbeans.graph.api.model.IGraphLink;
import org.netbeans.graph.api.model.IGraphNode;
import org.netbeans.graph.api.model.IGraphPort;
import org.netbeans.graph.vmd.VMDDocumentRenderer;
import org.netbeans.graph.vmd.VMDEmptyLinkRouter;
import org.netbeans.graph.vmd.VMDNodeRenderer;

/**
 *
 * @author mkleint
 */
public class DependencyDocumentRenderer extends VMDDocumentRenderer {
    
    public interface IArtifactGetter {
        Artifact getArtifact();
    }
    
    //cyan
    private static Color RUNTIME = new Color(203, 255, 252);
    // green
    private static Color TEST = new Color(203, 255, 209);
    // blue
    private static Color PROVIDED = new Color(221, 203, 255);
    // yellow
    private static Color COMPILE = new Color(252, 255, 205);
    //red
    private static Color SYSTEM = new Color(255, 203, 214);
        
    private final VMDEmptyLinkRouter emptyLinkRouter = new VMDEmptyLinkRouter();
    private final SimpleLinkRenderer linkRenderer;
    private final DependencyGraphNodeLayouter layout = new DependencyGraphNodeLayouter();

    private String title;
    /** Creates a new instance of DependencyDocumentRenderer */
    public DependencyDocumentRenderer(String title, GraphHelper helper) {
        super();
        linkRenderer = new SimpleLinkRenderer(helper);
        this.title = title;
    }
    
    public IGraphLinkRouter getLinkRouter(IGraphLink link) {
        return emptyLinkRouter;
    }
    
    protected void renderPaperHeader(Graphics2D gr) {
        Font fnt = gr.getFont();
        Color color = gr.getColor();
        gr.setFont(fnt.deriveFont(30f));
        gr.setColor(new Color(109, 226, 255));
        gr.drawString(title, 30, 40);
        gr.setColor(color);
        gr.setFont(fnt);
    }
    
    protected int getPaperHeaderHeight(Graphics2D gr) {
        return 50;
    }
    
    public IGraphNodeRenderer getNodeRenderer(IGraphNode node) {
        return new DelegateNodeRenderer(getHelper(), node);
    }
    
    public IGraphLinkRenderer getLinkRenderer(IGraphLink link) {
        return new SimpleLinkRenderer(getHelper());
    }

    public IGraphNodesLayouter getNodesLayouter() {
        return layout;
    }
    
    private class DelegateNodeRenderer implements IGraphNodeRenderer {
        private IGraphNodeRenderer vmd;

        private GraphHelper helper;
        
        public DelegateNodeRenderer(GraphHelper helper, IGraphNode node) {
            vmd = new VMDNodeRenderer(helper, node);
            this.helper = helper;
        }

        public void layoutNode(IGraphNode node, Graphics2D gr) {
            vmd.layoutNode(node, gr);
        }

        public int[] getLayers(IGraphNode node) {
            return vmd.getLayers(node);
        }

                
        public void renderNode(IGraphNode node, Graphics2D gr, int layer) {
            vmd.renderNode(node, gr, layer);
                    //LAYER_NODE_BACKGROUND = 100;
            if (layer == 100) {
                if (! helper.isComponentSelected(node)) {
                    Rectangle rect = helper.getBounds(node);
                    IArtifactGetter get = (IArtifactGetter)node.getLookup().lookup(IArtifactGetter.class);
                    Artifact art = get.getArtifact();
                    Color col = null;
                    if ("runtime".equals(art.getScope())) {
                        col = RUNTIME; // cyan
                    }
                    if ("test".equals(art.getScope())) {
                        col = TEST; // green
                    }
                    if ("provided".equals(art.getScope())) {
                        col = PROVIDED; // blue
                    }
                    // no scope == compile?
                    if ("compile".equals(art.getScope()) || art.getScope() == null) {
                        col = COMPILE; // yellow
                    }
                    if ("system".equals(art.getScope())) {
                        col = SYSTEM;  //red
                    }
                    if (col != null) {
                        Color old = gr.getColor();
                        gr.setColor (col);
                        gr.fillRect (rect.x, rect.y, rect.width, rect.height);
                        gr.setColor(old);
                    }
                    DependencyGraphNodeLayouter.IRootDistance dist = (DependencyGraphNodeLayouter.IRootDistance)node.getLookup().lookup(DependencyGraphNodeLayouter.IRootDistance.class);
                    if (dist.getDistanceFromRoot() == 0) {
                        Color old = gr.getColor();
                        gr.setColor(new Color(0xCDCDCD));
                        Stroke oldStroke = gr.getStroke();
                        gr.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        gr.draw (new Rectangle2D.Float (rect.x + 0.5f, rect.y + 0.5f, rect.width - 1, rect.height - 1));
                        gr.setColor(old);
                        gr.setStroke(oldStroke);
                    }
                }
            }
        }

        public IGraphEditor getEditor(IGraphNode node, Point position) {
            return vmd.getEditor(node, position);
        }

        public String getToolTipText(IGraphNode node, Point position) {
            return vmd.getToolTipText(node, position);
        }

        public Point locationSuggested(IGraphNode node, Point suggestedLocation) {
            return vmd.locationSuggested(node, suggestedLocation);
        }

        public void portLocationSuggested(IGraphPort port, Point suggestedLocation) {
            vmd.portLocationSuggested(port, suggestedLocation);
        }
        
    }

}