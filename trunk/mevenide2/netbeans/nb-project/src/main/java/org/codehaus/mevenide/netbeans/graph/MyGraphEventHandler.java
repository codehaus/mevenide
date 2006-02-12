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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.undo.UndoableEdit;
import org.netbeans.graph.api.IGraphEventHandler;
import org.netbeans.graph.api.model.GraphEvent;
import org.netbeans.graph.api.model.IGraphLink;
import org.netbeans.graph.api.model.IGraphNode;
import org.netbeans.graph.api.model.IGraphPort;
import org.netbeans.graph.api.model.builtin.GraphDocument;

/**
 * event handler for graphs, custom selection logic.
 * @author Milos Kleint (mkleint@codehaus.org)
 */

class MyGraphEventHandler extends  IGraphEventHandler {
    private GraphDocument document;

    private boolean multiselect = true;
    public MyGraphEventHandler(GraphDocument doc) {
        document = doc;
    }
    
    public void setMultiSelect(boolean bool) {
        multiselect = bool;
    }
    /**
     * Called when a new source port of a link has to be set.
     * Note: IGraphPort.getLinks method of both previous and new source port has to return valid set of links
     * after the model is changed (previous without and new with the link in an array).
     * @param link the link
     * @param sourcePort the source port
     */
    public void setSourcePort(IGraphLink link, IGraphPort sourcePort) {
        
    }
    
    /**
     * Called when a new target port of a link has to be set.
     * Note: IGraphPort.getLinks method of both previous and new target ports has to return valid set of links
     * after the model is changed (previous without and new with the link in an array).
     * @param link the link
     * @param targetPort the target port
     */
    public void setTargetPort(IGraphLink link, IGraphPort targetPort) {
        
    }
    
    /**
     * Called to check whether it is possible to create a link from a source port to a target port.
     * @param sourcePort the source port
     * @param targetPort the possible target port
     * @return true if it is possible
     */
    public boolean isLinkCreateable(IGraphPort sourcePort, IGraphPort targetPort) {
        return true;
    }
    
    /**
     * Called when a link from a source port to a target port has to be created.
     * @param sourcePort the source port
     * @param targetPort the target port
     */
    public void createLink(IGraphPort sourcePort, IGraphPort targetPort) {
        
    }
    
    /**
     * Called when an user changes the components selection.
     * @param event the graph event
     */
    public void componentsSelected(GraphEvent event) {
        IGraphNode[] nds = event.getNodes();
        Collection withChildren = new ArrayList();
        for (int i = 0; i < nds.length; i++) {
            withChildren.add(nds[i]);
            if (multiselect) {
                IGraphPort[] ports = nds[i].getPorts();
                for (int j = 0; j < ports.length; j++) {
                    if (ports[j].isSource()) {
                        IGraphLink[] links = ports[j].getLinks();
                        if (links != null) {
                            for (int k = 0; k < links.length; k++) {
                                IGraphNode nd = links[k].getTargetPort().getNode();
                                withChildren.add(nd);
                            }
                        }
                    }
                }
            }
        }
        GraphEvent newEvent = GraphEvent.create((IGraphNode[])withChildren.toArray(new IGraphNode[withChildren.size()]), new IGraphLink[0]);
        document.selectComponents(newEvent);
        HashSet set = new HashSet();
        final IGraphNode[] nodes = event.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            IGraphNode node = nodes[i];
            final IGraphPort[] ports = node.getPorts();
            if (ports != null) for (int j = 0; j < ports.length; j++) {
                IGraphPort port = ports[j];
                final IGraphLink[] links = port.getLinks();
                if (links != null) {
                    set.addAll(Arrays.asList(links));
                }
            }
        }
        document.highlightComponents(GraphEvent.create(null, (IGraphLink[]) set.toArray(new IGraphLink[set.size()])));
        
    }
    
    /**
     * Called when DND is invoked. Tests if data flavors are acceptable by the document.
     * @param node the node on which the transferable could be dropped, null if on background
     * @param dataFlavors the data flavors
     * @return true if data flavours are acceptable
     */
    public boolean isAcceptable(IGraphNode node, DataFlavor[] dataFlavors) {
        return true;
    }
    
    /**
     * Called when user DND drops a transferable into the document.
     * @param node the node on which the transferable is dropped, null if it is dropped on background
     * @param transferable the transferable
     */
    public void accept(IGraphNode node, Transferable transferable) {
        
    }
    
    /**
     * Called when an undoable edit (usually node location change) is performed and has to be added into UndoRedo queue.
     * @param edit the undoable edit
     */
    public void undoableEditHappened(UndoableEdit edit) {
        
    }
    
    /**
     * Called when the document has to be marked as modified (for example: when node location is changed).
     */
    public void notifyModified() {
        
    }
    
}

