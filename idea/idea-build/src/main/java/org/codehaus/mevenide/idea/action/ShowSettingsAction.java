package org.codehaus.mevenide.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.codehaus.mevenide.idea.CorePlugin;

public class ShowSettingsAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = (Project) e.getDataContext().getData(DataConstants.PROJECT);
        if ( project != null ) {
            ShowSettingsUtil.getInstance().editConfigurable(project, CorePlugin.getInstance(project));
        }
    }
}
