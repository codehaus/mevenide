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
package org.mevenide.grammar.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 * 
 */
public class GoalsAttributeCompletionImplTest extends TestCase {

    private GoalsAttributeCompletionImpl impl;
    private List siteGoals;
    private List maven11Goals;
    protected void setUp() throws Exception {
        impl = new GoalsAttributeCompletionImpl();
        siteGoals = new ArrayList();
        siteGoals.add("site");
        siteGoals.add("site:deploy");
        siteGoals.add("site:ear");
        siteGoals.add("site:fsdeploy");
        siteGoals.add("site:generate");
        siteGoals.add("site:init");
        siteGoals.add("site:run-reports");
        siteGoals.add("site:sshdeploy");
        siteGoals.add("site:war");
        //Maven RC2 added..
        siteGoals.add("site:ftpdeploy");
        //Maven 1.1 added...
        maven11Goals = new ArrayList();
        maven11Goals.add("site:site");
        maven11Goals.add("site:local-deploy-init");
        maven11Goals.add("site:publish");
        maven11Goals.add("site:remote-deploy-init");
        maven11Goals.add("site:rsyncdeploy");
    }

    protected void tearDown() throws Exception {
    }

    public void testCompleteList() throws Exception {
        Collection col = impl.getValueHints(null);
        assertNotNull("returned null. Why?", col);
        assertTrue("Didn't return any items. Why?", col.size() > 0);
    }
    
    public void testPartialPlugin() throws Exception {
        // get goals for site plugin
        Collection col = impl.getValueHints("sit");
        assertNotNull("returned null. Why?", col);
        Iterator it = col.iterator();
        while (it.hasNext()) {
           String goal = (String)it.next();
           assertTrue("goal:" + goal + " should not have been returned.", siteGoals.contains(goal) || maven11Goals.contains(goal)); 
        }
        assertTrue("wrong number of items", col.size() == siteGoals.size() || col.size() == siteGoals.size() + maven11Goals.size());
    }
    
    public void testPartialPlugin2() throws Exception {
        // get goals for site plugin
        Collection col = impl.getValueHints("site:");
        assertNotNull("returned null. Why?", col);
        Iterator it = col.iterator();
        String list = "";
        while (it.hasNext()) {
           String goal = (String)it.next();
           assertTrue("goal:" + goal + " should not have been returned.", siteGoals.contains(goal) || maven11Goals.contains(goal)); 
           list = list + " " + goal;
        }
        // all except the default "site" goal.
        assertTrue("wrong number of items (" + list + ")", col.size() == siteGoals.size() - 1 || col.size() == siteGoals.size() + maven11Goals.size() - 1);
    }

    public void testPartialPlugin3() throws Exception {
        // get goals for site plugin
        Collection col = impl.getValueHints("site:s");
        assertNotNull("returned null. Why?", col);
        String list = "";
        Iterator it = col.iterator();
        while (it.hasNext()) {
           String goal = (String)it.next();
           assertTrue("goal:" + goal + " should not have been returned.", siteGoals.contains(goal) || maven11Goals.contains(goal)); 
           list = list + " " + goal;
        }
        // only site:sshdeploy should be returned.
        // on maven 1.1 also site:site
        assertTrue("wrong number of items (" + list + ")", col.size() == 2 || col.size() == 1);
    }
    
}
