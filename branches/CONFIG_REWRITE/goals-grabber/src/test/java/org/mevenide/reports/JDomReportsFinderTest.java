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
package org.mevenide.reports;

import java.io.File;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.goals.test.util.TestUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class JDomReportsFinderTest extends AbstractReportsFinderTest {
	
    protected void setUp() throws Exception {
        super.setUp();
        File src = new File(JDomReportsFinderTest.class.getResource("/plugins/plugins.cache").getFile());
		File destFile = new File(pluginsLocal, "plugins.cache");
		TestUtils.copy(src.getAbsolutePath(), destFile.getAbsolutePath());            
    }
    
	protected IReportsFinder getReportsFinder() {
        return new JDomReportsFinder(new LocationFinderAggregator(context));
    }

	protected void tearDown() throws Exception {
        super.tearDown();
    }
}
