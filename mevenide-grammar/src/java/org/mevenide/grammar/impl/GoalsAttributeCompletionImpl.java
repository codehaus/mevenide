/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * =========================================================================
 */
package org.mevenide.grammar.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.grammar.AttributeCompletion;

/**
 * Implementation of a attribute completion that keeps track of available maven
 * goals. 
 * 
 * if basedir is set then custom goals will also be retrieved 
 * 
 * @author Milos Kleint (ca206216@tiscali.cz)
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 */
public class GoalsAttributeCompletionImpl implements AttributeCompletion {

    private static Log logger = LogFactory.getLog(GoalsAttributeCompletionImpl.class);
    
    /** primary grabber that handles both global and custom goals */
    private IGoalsGrabber grabber;
    /** fallback grabber - used if grabber isnot available */
    private IGoalsGrabber defaultGrabber;

    private String basedir;
    
    /** Creates a new instance of GoalsAttributeCompletionImpl */
    public GoalsAttributeCompletionImpl() throws Exception {
        ILocationFinder finder = new LocationFinderAggregator(DefaultQueryContext.getNonProjectContextInstance());
        grabber = GoalsGrabbersManager.getDefaultGoalsGrabber(finder);
        defaultGrabber = grabber;
    }

    public String getName() {
        return "goal";
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
        try {
            IQueryContext context = new DefaultQueryContext(new File(basedir));
            grabber = GoalsGrabbersManager.getGoalsGrabber(context, new LocationFinderAggregator(context));
        }
        catch (Exception e) {
            grabber = defaultGrabber;
            logger.error("Unable to set basedir. It is highly probable that maven.xml is badly formed.");
        }
    }
    
    public Collection getValueHints(String start) {
        Collection toReturn = new TreeSet();
        String[] plugins = grabber.getPlugins();
        if (plugins != null) {
            int colon = (start == null ? -1 : start.indexOf(':'));
            String pluginMatch = (colon > -1 ? start.substring(0, colon) : start);
            String goalMatch = null;
            if (colon > -1 && colon < start.length() - 1) {
                goalMatch = start.substring(colon + 1);
            }
            Collection selectedPlugins = new ArrayList(plugins.length + 5);
            for (int i = 0; i < plugins.length; i++) {
                if (pluginMatch == null || plugins[i].startsWith(pluginMatch)) {
                    selectedPlugins.add(plugins[i]);
                }
            }
            Iterator it = selectedPlugins.iterator();
            while (it.hasNext()) {
                String plugin = (String) it.next();
                String[] goals = grabber.getGoals(plugin);
                boolean hasDefault = false;
                for (int i = 0; i < goals.length; i++) {
                    if (goalMatch == null || goals[i].startsWith(goalMatch)) {
                        if (!"(default)".equals(goals[i])) {
                            toReturn.add(plugin + ":" + goals[i]);
                        }
                    }
                    if ("(default)".equals(goals[i])) {
                        hasDefault = true;
                    }
                }
                if (hasDefault && colon == -1) {
                    toReturn.add(plugin);
                }
            }
        }
        return toReturn;
    }
}