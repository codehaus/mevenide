/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.goals.manager;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.GoalsGrabbersAggregator;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsManager.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public final class GoalsGrabbersManager {
	private static Log log = LogFactory.getLog(GoalsGrabbersManager.class);
		
	private static Map goalsGrabbers = new HashMap();
	
	private static DefaultGoalsGrabber defaultGoalsGrabber;
	
	private GoalsGrabbersManager() { }
	
	public static synchronized IGoalsGrabber getGoalsGrabber(String projectDescriptorPath) throws Exception {
//		if ( defaultGoalsGrabber == null ) {
//			defaultGoalsGrabber = new DefaultGoalsGrabber();
//		}
		if ( goalsGrabbers.get(projectDescriptorPath) == null ) {
			GoalsGrabbersAggregator aggregator = new GoalsGrabbersAggregator();
			aggregator.addGoalsGrabber(getDefaultGoalsGrabber());
			
			String mavenXmlPath = new File(new File(projectDescriptorPath).getParent(), "maven.xml").getAbsolutePath();
			
			if ( new File(mavenXmlPath).exists() ) {
				ProjectGoalsGrabber projectGoalsGrabber = new ProjectGoalsGrabber();
				projectGoalsGrabber.setMavenXmlFile(mavenXmlPath);
				aggregator.addGoalsGrabber(projectGoalsGrabber);
				log.debug("maven.xml not found. aggregator only aggregates defaultGoalsGrabber.");
			}
			
			goalsGrabbers.put(projectDescriptorPath, aggregator);
		}
		
		IGoalsGrabber aggregator = (IGoalsGrabber) goalsGrabbers.get(projectDescriptorPath);
		aggregator.refresh();
		
		return aggregator;
	}
	
	public static synchronized IGoalsGrabber getDefaultGoalsGrabber() throws Exception {
		if ( defaultGoalsGrabber == null ) {
			defaultGoalsGrabber = new DefaultGoalsGrabber();
		}
                return defaultGoalsGrabber;
	}
}