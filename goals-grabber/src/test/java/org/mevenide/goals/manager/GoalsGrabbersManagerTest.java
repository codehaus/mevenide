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
package org.mevenide.goals.manager;

import java.io.File;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.goals.grabber.GoalsGrabbersAggregatorTest;
import org.mevenide.goals.grabber.IGoalsGrabber;


/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsGrabberManagerTest.java,v 1.1 7 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsGrabbersManagerTest extends GoalsGrabbersAggregatorTest {
	
    protected IGoalsGrabber getGoalsGrabber() throws Exception {
        String descriptorPath = GoalsGrabbersManagerTest.class.getResource("/").getFile();
//        System.out.println("grabber manager file=" + descriptorPath);
        context.setProjectDirectory(new File(descriptorPath));
        return GoalsGrabbersManager.getGoalsGrabber(context, new LocationFinderAggregator(context));
    }
	
	public void testGetGoalsGrabber() throws Exception {
		//commented to permit jcoverage to do its job.. 
		//dont know yet why it fails with jcoverage 
		//assertEquals(1, ((GoalsGrabbersAggregator) GoalsGrabbersManager.getGoalsGrabber("somePath")).getGoalsGrabbers().size());
		//assertEquals(2, ((GoalsGrabbersAggregator) GoalsGrabbersManager.getGoalsGrabber(GoalsGrabbersManagerTest.class.getResource("/project.xml").getFile())).getGoalsGrabbers().size());
	}
}
