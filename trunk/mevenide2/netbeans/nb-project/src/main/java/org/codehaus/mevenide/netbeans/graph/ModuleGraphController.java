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

import com.sun.corba.se.spi.orbutil.fsm.Action;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.graph.api.control.builtin.DefaultViewController;
import org.netbeans.graph.api.model.IGraphNode;

/**
 *
 * @author mkleint
 */
public class ModuleGraphController extends DefaultViewController {
    
    /** Creates a new instance of MyGraphController */
    public ModuleGraphController() {
        super();
    }

    /**
     * Called when popup menu is going to be invoked
     * @return popup menu, if null - no popup is shown
     */
    protected JPopupMenu getPopupMenu () {
        Object obj = getViewPresenter().getMouseOver();
        if (obj != null && obj instanceof IGraphNode) {
            JPopupMenu popup = new JPopupMenu();
            popup.add(new ShowDetailsAction((IGraphNode)obj));
            popup.add(new OpenProjectAction((IGraphNode)obj));
            return popup;
        }
        return null;
    }
    
    /**
     * Called when an user double-clicks on the view.
     * @param component the component (IGraphNode, IGraphLink, or IGraphPort instance), null if user double-clicks on the background
     * @param point the point where an user double-clicked
     */
    protected boolean componentDoubleClicked (Object component, Point point) {
        if (component != null && component instanceof IGraphNode) {
            new ShowDetailsAction((IGraphNode)component).actionPerformed(null);
        }
        return false;
    }
    
    private class ShowDetailsAction extends AbstractAction {

        private IGraphNode node;
        public ShowDetailsAction(IGraphNode  node) {
            this.node = node;
            putValue(javax.swing.Action.NAME, "Show Project Details");
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println("action performed..");
        }
    }
    private class OpenProjectAction extends AbstractAction {

        private IGraphNode node;
        public OpenProjectAction(IGraphNode  node) {
            this.node = node;
            putValue(javax.swing.Action.NAME, "Open Project");
        }

        public void actionPerformed(ActionEvent e) {
            ProjectGraphNode nd = (ProjectGraphNode)node.getLookup().lookup(ProjectGraphNode.class);
            if (nd !=null) {
                OpenProjects.getDefault().open(new Project[] { nd.getProject() }, false);
            }
        }
    }
}
