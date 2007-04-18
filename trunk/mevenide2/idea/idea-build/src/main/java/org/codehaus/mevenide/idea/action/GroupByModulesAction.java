package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.PomTreeView;

public class GroupByModulesAction extends PomTreeToggleAction {

    public boolean isSelected(PomTreeView view) {
        return view.getSettings().groupByModule;
    }

    public void setSelected(PomTreeView view, boolean state) {
        view.getSettings().groupByModule = state;
    }
}