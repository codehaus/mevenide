package org.mevenide.idea.project.model;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Arik
 */
public interface PluginInfo {
    String getId();

    String getArtifactId();

    String getGroupId();

    String getName();

    String getVersion();

    GoalInfo getGoal(String pName);

    GoalInfo[] getGoals();

    String getDescription();

    VirtualFile getScriptFile();

    VirtualFile getPomFile();
}
