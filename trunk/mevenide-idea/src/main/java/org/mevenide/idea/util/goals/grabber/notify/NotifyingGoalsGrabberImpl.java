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
package org.mevenide.idea.util.goals.grabber.notify;

import java.util.EventListener;
import javax.swing.event.EventListenerList;
import org.mevenide.goals.grabber.IGoalsGrabber;

/**
 * @author Arik
 */
public class NotifyingGoalsGrabberImpl implements NotifyingGoalsGrabber {
    private final IGoalsGrabber goalsGrabber;

    private final EventListenerList listenerList = new EventListenerList();

    public NotifyingGoalsGrabberImpl(final IGoalsGrabber pGoalsGrabber) {
        goalsGrabber = pGoalsGrabber;
    }

    public void addGoalsGrabberListener(final GoalsGrabberListener pListener) {
        listenerList.add(GoalsGrabberListener.class, pListener);
    }

    public void removeGoalsGrabberListener(final GoalsGrabberListener pListener) {
        listenerList.remove(GoalsGrabberListener.class, pListener);
    }

    public String getDescription(String fullyQualifiedGoalName) {
        return goalsGrabber.getDescription(fullyQualifiedGoalName);
    }

    public String[] getGoals(String plugin) {
        return goalsGrabber.getGoals(plugin);
    }

    public String getName() {
        return goalsGrabber.getName();
    }

    public String getOrigin(String fullyQualifiedGoalName) {
        return goalsGrabber.getOrigin(fullyQualifiedGoalName);
    }

    public String[] getPlugins() {
        return goalsGrabber.getPlugins();
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        return goalsGrabber.getPrereqs(fullyQualifiedGoalName);
    }

    public void refresh() throws Exception {
        goalsGrabber.refresh();

        final GoalsGrabberRefreshedEvent event = new GoalsGrabberRefreshedEvent(this);
        final EventListener[] listeners = listenerList.getListeners(GoalsGrabberListener.class);
        for (int i = 0; i < listeners.length; i++) {
            final GoalsGrabberListener listener = (GoalsGrabberListener) listeners[i];
            listener.goalsGrabberRefreshed(event);
        }
    }
}
