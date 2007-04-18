package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.component.PomTreeStructure;

import java.util.Collection;

public class DetachPluginAction extends PomTreeAction {

    protected boolean isEnabled(AnActionEvent e) {
        PomTreeView view = getView(e);
        return view != null && !view.getSelectedNodes(PomTreeStructure.ExtraPluginNode.class, true).isEmpty();
    }

    public void actionPerformed(AnActionEvent e) {
        PomTreeView view = getView(e);
        if ( view != null) {
            Collection<PomTreeStructure.ExtraPluginNode> selectedNodes = view.getSelectedNodes(PomTreeStructure.ExtraPluginNode.class, true);
            for (PomTreeStructure.ExtraPluginNode node : selectedNodes) {
                node.detach();
            }
        }
    }
}
