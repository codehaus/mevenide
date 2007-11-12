package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.PomTreeStructure;

public class GroupByModulesAction extends PomTreeToggleAction {

    public boolean isSelected(PomTreeStructure structure) {
        return structure.getSettings().groupByModule;
    }

    public void setSelected(PomTreeStructure structure, boolean state) {
        structure.getSettings().groupByModule = state;
    }
}