package org.mevenide.idea.util.goals.grabber;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.mevenide.goals.grabber.AbstractGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.util.goals.GoalsHelper;

import java.util.*;

/**
 * TODO: create a GoalsGrabberJDOMExternalizer
 * This is a generic goals grabber with equals and hashcode, and copy-ctor
 *
 * @author Arik
 */
public class CustomGoalsGrabber extends AbstractGoalsGrabber implements JDOMExternalizable
{
    private static final String DEFAULT_NAME = "Goals";

    private String name;
    private String externalName;

    public CustomGoalsGrabber() {
        this(DEFAULT_NAME);
    }

    public CustomGoalsGrabber(final String pName) {
        this(pName, null);
    }

    public CustomGoalsGrabber(final IGoalsGrabber pGoalsGrabber) {
        this(DEFAULT_NAME, pGoalsGrabber);
    }

    public CustomGoalsGrabber(final String pName, final IGoalsGrabber pGoalsGrabber) {
        if(pName == null)
            throw new IllegalArgumentException("Name cannot be null.");
        
        name = pName;

        if(pGoalsGrabber == null)
            return;

        final String[] plugins = pGoalsGrabber.getPlugins();
        for(final String plugin : plugins) {
            final String[] goals = pGoalsGrabber.getGoals(plugin);
            for (final String goal : goals) {
                final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                final String desc = pGoalsGrabber.getDescription(fqName);
                final String prereqs = StringUtils.join(pGoalsGrabber.getPrereqs(fqName), ',');
                registerGoal(fqName,
                             StringUtils.defaultString(desc) + ">" + StringUtils.defaultString(prereqs));
            }
        }
    }

    public String getExternalName() {
        return externalName == null ? getName() : externalName;
    }

    public void setExternalName(final String pExternalName) {
        externalName = pExternalName;
    }

    public String getName() {
        return name;
    }

    public void readExternal(Element element) throws InvalidDataException {
        final Element elt = element.getChild(getExternalName());
        if(elt == null)
            return;

        final List<Element> pluginElts = elt.getChildren("plugin");
        for(final Element pluginElt : pluginElts) {
            final String plugin = pluginElt.getAttributeValue("name");
            if(plugin == null || plugin.trim().length() == 0)
                continue;

            final List<Element> goalElts = pluginElt.getChildren("goal");
            for(final Element goalElt : goalElts) {
                final String goal = goalElt.getAttributeValue("name");
                if(goal == null || goal.trim().length() == 0)
                    continue;

                final String desc = goalElt.getAttributeValue("description");
                final String prereqs = goalElt.getAttributeValue("prereqs");
                final String props =
                        StringUtils.defaultString(desc) + ">" +
                                StringUtils.defaultString(prereqs);
                final String fqName = GoalsHelper.buildFullyQualifiedName(plugin,
                                                                          goal);
                registerGoal(fqName, props);
            }
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {
        final String[] plugins = getPlugins();
        if(plugins == null || plugins.length == 0)
            return;

        final Element pluginsElt = new Element(getExternalName());

        for(final String plugin : plugins) {
            final String[] goals = getGoals(plugin);
            if(goals == null || goals.length == 0)
                continue;

            final Element pluginElt = new Element("plugin");
            pluginElt.setAttribute("name", plugin);

            for(final String goal : goals) {
                final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                final String description = getDescription(fqName);
                final String prereqs = StringUtils.join(getPrereqs(fqName));

                final Element goalElt = new Element("goal");
                goalElt.setAttribute("name", goal);
                if(description != null && description.trim().length() > 0)
                    goalElt.setAttribute("description", description);

                if(prereqs != null && prereqs.trim().length() > 0)
                    goalElt.setAttribute("prereqs", prereqs);

                pluginElt.addContent(goalElt);
            }

            pluginsElt.addContent(pluginElt);
        }

        element.addContent(pluginsElt);
    }

    public boolean equals(Object obj) {
        if(obj == this)
            return true;

        final IGoalsGrabber other = (IGoalsGrabber) obj;

        final Map thisMap = GoalsHelper.asMap(this);
        final Map otherMap = GoalsHelper.asMap(other);

        return thisMap.equals(otherMap);
    }

    public int hashCode() {
        return GoalsHelper.asMap(this).hashCode();
    }

    @Override public void registerGoal(String fullyQualifiedGoalName, String properties) {
        super.registerGoal(fullyQualifiedGoalName, properties);
    }

    @Override public void registerGoalName(String fullyQualifiedGoalName) {
        super.registerGoalName(fullyQualifiedGoalName);
    }

    @Override public void registerGoalProperties(String fullyQualifiedGoalName, String properties) {
        super.registerGoalProperties(fullyQualifiedGoalName, properties);
    }

    public void unregisterGoal(final String fullyQualifiedGoalName) {
        final String plugin = GoalsHelper.getPluginName(fullyQualifiedGoalName);
        final Set goals = (Set) plugins.get(plugin);
        if(goals != null) {
            final String goal = GoalsHelper.getGoalSimpleName(fullyQualifiedGoalName);
            goals.remove(goal);
        }

        super.descriptions.remove(fullyQualifiedGoalName);
        super.prereqs.remove(fullyQualifiedGoalName);
    }
}
