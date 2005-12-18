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

package org.codehaus.mevenide.grammar;

import java.io.InputStream;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 */
public class MavenSettingsGrammar extends AbstractSchemaBasedGrammar {
    
    
    public MavenSettingsGrammar() {
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/codehaus/mevenide/grammar/settings-1.0.0.xsd");
    }
    
    public boolean isSupported(GrammarEnvironment env) {
        if (env.getFileObject().getNameExt().equals("settings.xml")) {
            return true;
        }
        return false;
    }
    
}
