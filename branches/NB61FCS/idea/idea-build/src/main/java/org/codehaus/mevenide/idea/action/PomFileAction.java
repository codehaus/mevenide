package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;

import java.util.Collection;

abstract public class PomFileAction extends PomTreeAction {
    private final boolean multiple;

    protected PomFileAction(boolean multiple) {
        this.multiple = multiple;
    }

    public void update(final AnActionEvent e) {
        PomTreeStructure structure = getTreeStructure(e);
        e.getPresentation().setEnabled(structure != null && isEnabled(structure.getSelectedNodes(PomTreeStructure.PomNode.class, true)));
    }

    public void actionPerformed(AnActionEvent e) {
        PomTreeStructure structure = getTreeStructure(e);
        if (structure != null) {
            Collection<PomTreeStructure.PomNode> selectedNodes = structure.getSelectedNodes(PomTreeStructure.PomNode.class, true);
            if ( isEnabled(selectedNodes) ) {
                actOnPoms(structure, selectedNodes);
            }
        }
    }

    private boolean isEnabled(Collection<PomTreeStructure.PomNode> selectedNodes) {
        return selectedNodes.size() > ( multiple ? 0 : 1 );
    }

    protected abstract void actOnPoms(PomTreeStructure stucture, Collection<PomTreeStructure.PomNode> selectedNodes);
}
