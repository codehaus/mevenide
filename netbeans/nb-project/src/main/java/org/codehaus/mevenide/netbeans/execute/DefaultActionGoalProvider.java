/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.execute;

import java.io.InputStream;

/**
 * a default implementation of AdditionalM2ActionsProvider, a fallback when nothing is
 * user configured or overriden by a more specialized provider.
 * @author mkleint
 */
public class DefaultActionGoalProvider extends AbstractActionGoalProvider {
    /** Creates a new instance of DefaultActionProvider */
    public DefaultActionGoalProvider() {
    }
    
    public InputStream getActionDefinitionStream() {
       String path = "/org/codehaus/mevenide/netbeans/execute/defaultActionMappings.xml";
       InputStream in = getClass().getResourceAsStream(path);
        if (in == null) {
            System.out.println("no instream for=" + path);
            return null;
        }
       return in;
    }

    
}
