package org.mevenide.idea;

/**
 * This is a simple copy interface of IGoalsGrabber defined in goals-grabber of Mevenide.
 *
 * <p>It is duplicated here due to classloading conflicts in IDEA which prevent the main module from using
 * Mevenide directly. The module goals grabber (which implements IGoalsGrabber) also defines this interface,
 * and therefor provides access to Mevenide through this common interface.</p>
 *
 * <p>It is intended that when the classloading conflicts are resolved, this interface will no longer be
 * needed.</p>
 *
 * @author Arik
 */
public interface GoalsProvider {
    /**
     * return all available plugins
     *
     * @return an array of plugin names
     */
    String[] getPlugins();

    /**
     * return the goals declared by the plugin whose name is passed as parameter
     *
     * @param plugin the plugin to retrieve the goals for
     *
     * @return an array of fully qualified goal names
     */
    String[] getGoals(String plugin);

    /**
     * return the description of plugin:goal
     *
     * @param fullyQualifiedGoalName the fully qualified goal name
     *
     * @return the textual description of the goal, or <code>null</code> if none exists
     */
    String getDescription(String fullyQualifiedGoalName);

    /**
     * Returns the prerequisities for the specified goal. These are fully qualified goal names that will be
     * executed prior to executing this goal.
     *
     * @param fullyQualifiedGoalName the goal name
     *
     * @return an array of fully qualified goal names
     */
    String[] getPrereqs(String fullyQualifiedGoalName);

    /**
     * Refreshes the goals grabber.
     *
     * @throws GoalGrabbingException if errors occur
     */
    void refreshGoals() throws GoalGrabbingException;

    /**
     * Will return the plugin where the goal is defined.
     *
     * @return the plugin name
     */
    String getOrigin(String fullyQualifiedGoalName);

    /**
     * Adds the specified listener to the listener list.
     *
     * @param pListener the listener to add
     */
    void addGoalsProviderListener(GoalsProviderListener pListener);

    /**
     * Removes the specified listener from the listener list.
     *
     * @param pListener the listener the remove
     */
    void removeGoalsProviderListener(GoalsProviderListener pListener);
}
