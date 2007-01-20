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

package org.codehaus.mevenide.netbeans.j2ee;

import java.io.InputStream;
import org.codehaus.mevenide.netbeans.execute.AbstractActionGoalProvider;

/**
 * j2ee specific defaults for project running and debugging..
 * @author mkleint
 */
public class J2eeActionsProvider extends AbstractActionGoalProvider {
    
    /** Creates a new instance of J2eeActionsProvider */
    public J2eeActionsProvider() {
    }
    
    
    public InputStream getActionDefinitionStream() {
        String path = "/org/codehaus/mevenide/netbeans/j2ee/webActionMappings.xml"; //NOI18N
        InputStream in = getClass().getResourceAsStream(path);
        assert in != null : "no instream for " + path;
        return in;
    }
    
}
