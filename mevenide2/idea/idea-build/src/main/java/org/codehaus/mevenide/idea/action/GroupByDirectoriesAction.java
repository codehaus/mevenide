package org.codehaus.mevenide.idea.action;

import org.codehaus.mevenide.idea.gui.PomTreeView;

public class GroupByDirectoriesAction extends PomTreeToggleAction {

    public boolean isSelected(PomTreeView view) {
        return view.getSettings().groupByDirectory;
    }

    public void setSelected(PomTreeView view, boolean state) {
        view.getSettings().groupByDirectory = state;
    }
}
