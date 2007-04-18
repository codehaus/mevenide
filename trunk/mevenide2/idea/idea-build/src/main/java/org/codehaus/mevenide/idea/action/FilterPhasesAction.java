package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.PomTreeView;

public class FilterPhasesAction extends PomTreeToggleAction {

    public boolean isSelected(PomTreeView view) {
        return view.getSettings().filterStandardPhases;
    }

    public void setSelected(PomTreeView view, boolean state) {
        view.getSettings().filterStandardPhases = state;
    }
}
