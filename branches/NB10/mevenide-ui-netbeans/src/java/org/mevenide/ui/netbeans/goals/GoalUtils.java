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
package org.mevenide.ui.netbeans.goals;

import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.manager.GoalsGrabbersManager;
import org.mevenide.ui.netbeans.GoalsGrabberProvider;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class GoalUtils
{
    
    /** Creates a new instance of Utils */
    private GoalUtils()
    {
    }
    
    public static GoalsGrabberProvider createDefaultGoalsProvider()
    {
        return new GoalsGrabberProvider()
                {
                    public IGoalsGrabber getGoalsGrabber() throws Exception 
                    {
                        return GoalsGrabbersManager.getDefaultGoalsGrabber();
                    }
                };        
    }

    public static GoalsGrabberProvider createProjectGoalsProvider(final String projectXmlPath)
    {
        return new GoalsGrabberProvider()
                {
                    public IGoalsGrabber getGoalsGrabber() throws Exception 
                    {
                        return GoalsGrabbersManager.getGoalsGrabber(projectXmlPath);
                    }
                };        
    }
    
}
