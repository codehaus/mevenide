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
package org.mevenide.goals.grabber;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;


/**
 * read goals from ${maven.home}/plugins/goals.cache file 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultGoalsGrabber extends AbstractGoalsGrabber {
    private static Log log = LogFactory.getLog(DefaultGoalsGrabber.class);
    
    private ILocationFinder finder;
	
    public DefaultGoalsGrabber() throws Exception {
        this(ConfigUtils.getDefaultLocationFinder());
    }
    
    public DefaultGoalsGrabber(ILocationFinder find) throws Exception {
        finder = find;
        refresh();
    }
        
        public String getName() {
            return IGoalsGrabber.ORIGIN_PLUGIN;
        }
        
 	public void refresh() throws Exception {
		super.refresh();

        //File pluginsLocal = new File(Environment.getMavenHome(), "plugins");
  		File goalsCache = new File(finder.getMavenPluginsDir(), "goals.cache");
		if ( goalsCache.exists() ) {
			log.debug("Grabbing goals from : " + goalsCache.getAbsolutePath());
	  		
			Properties props = new Properties();
	  		props.load(new FileInputStream(goalsCache));
	
	  		Set fullyQualifiedGoalNames = props.keySet();
			Iterator iterator = fullyQualifiedGoalNames.iterator();
	
			while ( iterator.hasNext() ) {
				String goalName = (String) iterator.next();
				registerGoal(goalName, props.getProperty(goalName));
			}  
		
		}	
		else {
			log.debug("No goals.cache file found in : " + ConfigUtils.getDefaultLocationFinder().getMavenPluginsDir());
		}	
    }

}

