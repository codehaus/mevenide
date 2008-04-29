package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.codehaus.mevenide.idea.gui.PomTreeStructure;

public class RunSelectedGoalsAction extends PomTreeAction {
    public void update(final AnActionEvent e) {
        boolean enabled = false;
        PomTreeStructure structure = getTreeStructure(e);
        if ( structure != null ) {
            enabled = PomTreeStructure.getCommonParent(structure.getSelectedNodes(PomTreeStructure.GoalNode.class, false)) != null;
        }
        e.getPresentation().setEnabled(enabled);
    }

    public void actionPerformed(AnActionEvent e) {
        PomTreeStructure structure = getTreeStructure(e);
        if (structure != null) {
            structure.runGoals(structure.getSelectedNodes(PomTreeStructure.GoalNode.class, false));
        }
    }
}
