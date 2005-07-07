package org.mevenide.idea.project.goals;

/**
 * @author Arik
 */
public interface Goal {
    GoalContainer getContainer();

    String getName();

    String getDescription();

    String[] getPrereqs();
}
