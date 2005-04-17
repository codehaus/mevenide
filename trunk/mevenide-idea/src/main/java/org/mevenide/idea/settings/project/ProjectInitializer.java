package org.mevenide.idea.settings.project;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.mevenide.idea.goalstoolwindow.GoalsToolWindow;
import org.mevenide.idea.runner.console.ExecutionToolWindow;
import org.mevenide.idea.util.images.Images;

import javax.swing.*;

/**
 * @author Arik
 */
public class ProjectInitializer implements ProjectComponent {

    public static final String NAME = ProjectInitializer.class.getName();

    private final Project project;

    public ProjectInitializer(final Project pProject) {
        project = pProject;
    }

    public String getComponentName() {
        return NAME;
    }

    public void projectOpened() {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);

        final GoalsToolWindow toolWin = new GoalsToolWindow(project);
        toolMgr.registerToolWindow(GoalsToolWindow.NAME, toolWin, ToolWindowAnchor.RIGHT);
        final ToolWindow goalsTw = toolMgr.getToolWindow(GoalsToolWindow.NAME);
        goalsTw.setIcon(new ImageIcon(Images.MAVEN_ICON));

        final ExecutionToolWindow execWin = new ExecutionToolWindow(project);
        toolMgr.registerToolWindow(ExecutionToolWindow.NAME, execWin, ToolWindowAnchor.BOTTOM);
        final ToolWindow execTw = toolMgr.getToolWindow(ExecutionToolWindow.NAME);
        execTw.setIcon(new ImageIcon(Images.MAVEN_ICON));
        execTw.setAvailable(false, null);
    }

    public void initComponent() {
    }

    public void projectClosed() {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        toolMgr.unregisterToolWindow(GoalsToolWindow.NAME);
    }

    public void disposeComponent() {
    }
}
