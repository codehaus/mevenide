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

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MavenQueryProvider extends GrammarQueryManager {

    private List grammars;
    public MavenQueryProvider() {
        grammars = new ArrayList();
        // TODO make regitrable/pluggable somehow
        grammars.add(new MavenProjectGrammar());
        grammars.add(new MavenSettingsGrammar());
    }
    
    public Enumeration enabled(GrammarEnvironment ctx) {
        Enumeration en = ctx.getDocumentChildren();
        while (en.hasMoreElements()) {
            Node next = (Node) en.nextElement();
            if (next.getNodeType() == next.ELEMENT_NODE) {
                return Collections.enumeration(Collections.singletonList(next));
            }
        }
        return null;
    }
    
    public FeatureDescriptor getDescriptor() {
        return new FeatureDescriptor();
    }
    
    public GrammarQuery getGrammar(GrammarEnvironment env) {
        Iterator it = grammars.iterator();
        while (it.hasNext()) {
            AbstractSchemaBasedGrammar gr = (AbstractSchemaBasedGrammar)it.next();
            if (gr.isSupported(env)) {
                return gr;
            }
        }
        return null;
    }

}
