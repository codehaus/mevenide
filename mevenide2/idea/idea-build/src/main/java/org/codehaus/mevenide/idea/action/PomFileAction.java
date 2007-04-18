package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.mevenide.idea.gui.PomTreeView;
import org.codehaus.mevenide.idea.component.PomTreeStructure;

import java.util.Collection;

abstract public class PomFileAction extends PomTreeAction {
    private final boolean multiple;

    protected PomFileAction(boolean multiple) {
        this.multiple = multiple;
    }

    public void update(final AnActionEvent e) {
        PomTreeView pomTreeView = getView(e);
        e.getPresentation().setEnabled(pomTreeView != null && isEnabled(pomTreeView.getSelectedNodes(PomTreeStructure.PomNode.class, true)));
    }

    public void actionPerformed(AnActionEvent e) {
        PomTreeView pomTreeView = getView(e);
        if (pomTreeView != null) {
            Collection<PomTreeStructure.PomNode> selectedNodes = pomTreeView.getSelectedNodes(PomTreeStructure.PomNode.class, true);
            if ( isEnabled(selectedNodes) ) {
                actOnPoms(pomTreeView, selectedNodes);
            }
        }
    }

    private boolean isEnabled(Collection<PomTreeStructure.PomNode> selectedNodes) {
        return selectedNodes.size() > ( multiple ? 0 : 1 );
    }

    protected abstract void actOnPoms(PomTreeView pomTreeView, Collection<PomTreeStructure.PomNode> selectedNodes);
}
