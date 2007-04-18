package org.codehaus.mevenide.idea.gui;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.psi.PsiFile;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.treeStructure.SimpleNode;
import org.codehaus.mevenide.idea.action.RunSelectedGoalsAction;
import org.codehaus.mevenide.idea.component.PomTreeStructure;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class PomTreeMouseAdapter extends PopupHandler {
    private final PomTreeView pomTreeView;

    public PomTreeMouseAdapter(PomTreeView treeView) {
        this.pomTreeView = treeView;
    }

    public void invokePopup(Component comp, int x, int y) {
        ActionPopupMenu popupMenu = createPopup();
        if ( popupMenu != null ) {
            popupMenu.getComponent().show(comp, x, y);
        }
    }

    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
            doubleClick();
        }
    }

    protected void doubleClick() {
        Collection<SimpleNode> selectedNodes = pomTreeView.getSelectedNodes();

        Collection<PomTreeStructure.PomNode> pomNodes = pomTreeView.filterNodes(selectedNodes, PomTreeStructure.PomNode.class, false);
        if (!pomNodes.isEmpty()) {
            navigate(pomNodes);
            return;
        }

        Collection<PomTreeStructure.GoalNode> goalNodes = pomTreeView.filterNodes(selectedNodes, PomTreeStructure.GoalNode.class, false);
        if (!goalNodes.isEmpty()) {
            RunSelectedGoalsAction.runSelectedGoals(null, goalNodes);
        }
    }

    private ActionPopupMenu createPopup() {
        Collection<SimpleNode> selectedNodes = pomTreeView.getSelectedNodes();

        ActionPopupMenu pomMenu = createPopupMenu(selectedNodes, PomTreeStructure.PomNode.class, false, "org.codehaus.mevenide.idea.action.PomMenu");
        if ( pomMenu != null ) return pomMenu;

        ActionPopupMenu goalMenu = createPopupMenu(selectedNodes, PomTreeStructure.GoalNode.class, false, "org.codehaus.mevenide.idea.action.GoalMenu");
        if (goalMenu != null ) return goalMenu;

        ActionPopupMenu pluginMenu = createPopupMenu(selectedNodes, PomTreeStructure.ExtraPluginNode.class, false, "org.codehaus.mevenide.idea.action.PluginMenu");
        return pluginMenu;
    }

    protected ActionPopupMenu createPopupMenu(Collection<SimpleNode> selectedNodes, Class<? extends SimpleNode> aClass, boolean strict, String menuId){
        Collection<? extends SimpleNode> nodes = pomTreeView.filterNodes(selectedNodes, aClass, strict);
        if (nodes.isEmpty()) {
            return null;
        } else {
            return ActionManager.getInstance().createActionPopupMenu("",(ActionGroup) ActionManager.getInstance().getAction(menuId));
        }
    }

    public static void navigate(Collection<PomTreeStructure.PomNode> selectedNodes) {
        for (PomTreeStructure.PomNode selectedNode : selectedNodes) {
            PsiFile psiFile = selectedNode.getDocument().getPsiFile();
            if ( psiFile != null ) {
                psiFile.navigate(selectedNodes.size() == 1);
            }
        }
    }
}
