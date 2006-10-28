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

import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;

/**
 * an abstraction for creating grammarQuery instances based on context, used by QueryProvider.
 * can be made part of api to register additional grammars. 
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public abstract class GrammarFactory {
    
    /** Creates a new instance of GrammarFactory */
    protected GrammarFactory() {
    }
    
    public abstract GrammarQuery isSupported(GrammarEnvironment env);
}
