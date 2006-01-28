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
package org.codehaus.mevenide.ui.eclipse.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.codehaus.mevenide.m2.embedder.M2Embedder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class M2Runner implements IPlatformRunnable {

    private ILaunchConfiguration configuration;
   
    public Object run(Object args) throws Exception {
        M2Embedder embedder = new M2Embedder();
        
        String file = ExternalToolsUtil.getWorkingDirectory(configuration).append("pom.xml").toOSString();
        
        embedder.setFile(file);
        
        List goals = getGoals();
        
        embedder.setGoals(goals);
        
        embedder.run();
        
        return null;
    }
    
    private List getGoals() throws CoreException {
        List goals = new ArrayList();
        String serializedGoals = configuration.getAttribute(M2ArgumentsTab.GOALS_TO_RUN, ""); //$NON-NLS-1$
        StringTokenizer tokenizer = new StringTokenizer(serializedGoals, " ");
		while ( tokenizer.hasMoreTokens() ) {
		    goals.add(tokenizer.nextToken());
		}
        return goals;
    }

    public void setConfiguration(ILaunchConfiguration configuration) {
        this.configuration = configuration;
    }
}

