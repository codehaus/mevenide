package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.PomTreeStructure;

public class FilterPhasesAction extends PomTreeToggleAction {

    public boolean isSelected(PomTreeStructure structure) {
        return structure.getSettings().filterStandardPhases;
    }

    public void setSelected(PomTreeStructure structure, boolean state) {
        structure.getSettings().filterStandardPhases = state;
    }
}
