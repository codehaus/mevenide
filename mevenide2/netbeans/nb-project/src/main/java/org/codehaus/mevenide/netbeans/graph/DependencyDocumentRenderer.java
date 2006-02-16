/*
 * DependencyDocumentRenderer.java
 *
 * Created on February 14, 2006, 6:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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

/**
 *
 * @author mkleint
 */
public class DependencyDocumentRenderer extends VMDDocumentRenderer {
    
    private final VMDEmptyLinkRouter emptyLinkRouter = new VMDEmptyLinkRouter();
    private final SimpleLinkRenderer linkRenderer;
    private final DependencyGraphNodeLayouter layout = new DependencyGraphNodeLayouter();
    /** Creates a new instance of DependencyDocumentRenderer */
    public DependencyDocumentRenderer(GraphHelper helper) {
        super();
        linkRenderer = new SimpleLinkRenderer(helper);
    }
    
    public IGraphLinkRouter getLinkRouter(IGraphLink link) {
        return emptyLinkRouter;
    }
    
    protected void renderPaperHeader(Graphics2D gr) {
        Font fnt = gr.getFont();
        Color color = gr.getColor();
        gr.setFont(fnt.deriveFont(30f));
        gr.setColor(new Color(109, 226, 255));
        gr.drawString("Maven2 transitive dependencies", 30, 40);
        gr.setColor(color);
        gr.setFont(fnt);
    }
    
    protected int getPaperHeaderHeight(Graphics2D gr) {
        return 50;
    }
    
    public IGraphNodeRenderer getNodeRenderer(IGraphNode node) {
        IGraphNodeRenderer retValue;
        
        retValue = super.getNodeRenderer(node);
        return retValue;
    }
    
    public IGraphLinkRenderer getLinkRenderer(IGraphLink link) {
        return new SimpleLinkRenderer(getHelper());
    }

    public IGraphNodesLayouter getNodesLayouter() {
        return layout;
    }

}