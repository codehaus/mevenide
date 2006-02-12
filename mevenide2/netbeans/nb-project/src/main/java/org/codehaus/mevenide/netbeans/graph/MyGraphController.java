/*
 * MyGraphController.java
 *
 * Created on February 12, 2006, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
public class MyGraphController extends DefaultViewController {
    
    /** Creates a new instance of MyGraphController */
    public MyGraphController() {
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
