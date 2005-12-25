/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.codehaus.mevenide.grammar.AbstractSchemaBasedGrammar.MyTextElement;
import org.jdom.Element;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.HintContext;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenNbmGrammar extends AbstractSchemaBasedGrammar {
    
    
    public MavenNbmGrammar(GrammarEnvironment env) {
        super(env);
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/codehaus/mevenide/grammar/nbm-1.0.0.xsd");
    }
    

    protected Enumeration getDynamicValueCompletion(String path, HintContext hintCtx, Element lowestParent) {
        if ("/nbm/dependencies/dependency/type".equals(path)) {
            return createTextValueList(new String[] {
                "spec",
                "impl",
                "loose"
                }, hintCtx);
        }
        if ("/nbm/moduleType".equals(path)) {
            return createTextValueList(new String[] {
                "normal",
                "autoload",
                "eager"
                }, hintCtx);
            
        }
        return null;
    }
    
    
    
    
}
