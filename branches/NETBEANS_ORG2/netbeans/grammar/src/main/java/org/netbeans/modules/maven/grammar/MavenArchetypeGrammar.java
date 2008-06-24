/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.netbeans.modules.maven.grammar;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.jdom.Element;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.HintContext;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 * for src/main/resources/META-INF/archetype.xml files..
 */
public class MavenArchetypeGrammar extends AbstractSchemaBasedGrammar {
    
    
    public MavenArchetypeGrammar(GrammarEnvironment env) {
        super(env);
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/netbeans/modules/maven/grammar/archetype-1.0.0.xsd"); //NOI18N
    }
    

    protected List getDynamicCompletion(String path, HintContext hintCtx, Element lowestParent) {
        return Collections.EMPTY_LIST;
    }
    
    
}
