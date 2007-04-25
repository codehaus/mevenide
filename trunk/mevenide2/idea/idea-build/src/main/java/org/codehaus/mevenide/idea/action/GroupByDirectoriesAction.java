package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.PomTreeStructure;

public class GroupByDirectoriesAction extends PomTreeToggleAction {

    public boolean isSelected(PomTreeStructure structure) {
        return structure.getSettings().groupByDirectory;
    }

    public void setSelected(PomTreeStructure structure, boolean state) {
        structure.getSettings().groupByDirectory = state;
    }
}
