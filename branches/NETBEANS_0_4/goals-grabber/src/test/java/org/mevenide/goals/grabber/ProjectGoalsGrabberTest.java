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



/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ProjectGoalsGrabberTest.java 5 sept. 2003 Exp gdodinet 
 * 
 */
public class ProjectGoalsGrabberTest extends AbstractGoalsGrabberTestCase {
	
	protected IGoalsGrabber getGoalsGrabber() throws Exception {
		IGoalsGrabber goalsGrabber = new ProjectGoalsGrabber();
		((ProjectGoalsGrabber)goalsGrabber).setMavenXmlFile(this.getClass().getResource("/maven.xml").getFile());
		return goalsGrabber;	
	}

	protected String[] getGetPluginsResults() {
		return new String[] { "build", "build-all", "build-site" };
	}
    
	protected String[] getGetGoalsParameters() {
		return new String[] { "build-all" };
	}
	
	protected String[][] getGetGoalsResults() {
		return new String[][] { {"(default)"} }; 
	} 
	
	protected String[] getGetDescriptionParameters() {
		return new String[] { "build", "build-all", "build-site" } ; 
	}
    
    protected String[] getGetDescriptionResults() {
    	return new String[] { "Build each Mevenide module", "Build all Mevenide modules", "Build Mevenide Site" };
    }

	protected String[] getGetPrereqsParameters() {
		return new String[] { "build-all" };
	}

	protected String[][] getGetPrereqsResults() {
		return new String[][] { 
			{ "prereqs:prereq1", "prereqs:prereq2","prereq3" }
		};
	}
     
   protected String[] getGetOriginParameters() {
       return new String[] { "build", "build-all"};
   }
    protected String[] getGetOriginResults() {
       return new String[] { IGoalsGrabber.ORIGIN_PROJECT, IGoalsGrabber.ORIGIN_PROJECT };
    }
}
