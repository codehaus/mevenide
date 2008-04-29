package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;

import java.util.Collection;

public class DetachPluginAction extends PomTreeAction {

    protected boolean isEnabled(AnActionEvent e) {
        PomTreeStructure structure = getTreeStructure(e);
        return structure != null && !structure.getSelectedNodes(PomTreeStructure.ExtraPluginNode.class, true).isEmpty();
    }

    public void actionPerformed(AnActionEvent e) {
        PomTreeStructure structure = getTreeStructure(e);
        if ( structure != null) {
            Collection<PomTreeStructure.ExtraPluginNode> selectedNodes = structure.getSelectedNodes(PomTreeStructure.ExtraPluginNode.class, true);
            for (PomTreeStructure.ExtraPluginNode node : selectedNodes) {
                node.detach();
            }
        }
    }
}
