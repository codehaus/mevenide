/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.module;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.module.Module;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.util.components.AbstractModuleComponent;
import org.mevenide.idea.util.goals.GoalsHelper;

/**
 * @author Arik
 */
public class ModuleActionsManager extends AbstractModuleComponent implements PropertyChangeListener {

    private AnAction[] registeredActions = new AnAction[0];

    public ModuleActionsManager(final Module pModule) {
        super(pModule);
    }

    @Override public void moduleAdded() {
        ModuleSettings.getInstance(module).addPropertyChangeListener("queryContext", this);
        unregisterModuleActions();
        refreshActions();
    }

    @Override public void projectClosed() {
        unregisterModuleActions();
    }

    public void propertyChange(final PropertyChangeEvent pEvent) {
        if(pEvent.getPropertyName().equals("queryContext")) {
            unregisterModuleActions();
            refreshActions();
        }
    }

    private void refreshActions() {
        final ModuleSettings settings = ModuleSettings.getInstance(module);
        if(settings == null)
            return;

        final IGoalsGrabber projectGrabber = settings.getProjectGoalsGrabber();
        final AnAction[] projectActions = createActionsFromGrabber(projectGrabber);

        final IGoalsGrabber favoritesGrabber = settings.getFavoriteGoalsGrabber();
        final AnAction[] favoriteActions = createActionsFromGrabber(favoritesGrabber);

        registeredActions = new AnAction[projectActions.length + favoriteActions.length];
        System.arraycopy(projectActions, 0, registeredActions, 0, projectActions.length);
        System.arraycopy(favoriteActions, 0, registeredActions, projectActions.length, favoriteActions.length);
    }

    private void unregisterModuleActions() {
        final ActionManager mgr = ActionManager.getInstance();

        for(AnAction action : registeredActions) {
            final String id = mgr.getId(action);
            if(id != null)
                mgr.unregisterAction(id);
        }
    }

    private AnAction[] createActionsFromGrabber(final IGoalsGrabber pGrabber) {
        if(pGrabber == null)
            return new AnAction[0];

        final ActionManager mgr = ActionManager.getInstance();
        final List<AnAction> actions = new ArrayList<AnAction>(30);

        //
        //iterate over available plugins and register their available actions
        //
        final String[] plugins = pGrabber.getPlugins();
        for(String plugin : plugins) {
            final String[] goals = pGrabber.getGoals(plugin);
            for(String goal : goals) {

                //build a fully-qualified goal name
                final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);

                //create the action and register it
                final GoalAction action = new GoalAction(module,
                                                         fqName,
                                                         pGrabber.getDescription(fqName));
                final String id = module.getProject().getName() + "_" + module.getName() + "_" + fqName;
                mgr.registerAction(id, action);

                //add it to the list of actions that will be returned
                actions.add(action);
            }
        }

        return actions.toArray(new AnAction[actions.size()]);
    }
}
