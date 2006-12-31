package org.mevenide.idea.project.goals;

import com.intellij.openapi.vfs.pointers.VirtualFilePointer;

/**
 * @author Arik
 */
public interface GoalContainer {
    String getId();

    Goal getGoal(String pName);

    Goal[] getGoals();

    VirtualFilePointer getScriptFile();

    VirtualFilePointer getPomFile();
}
