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

package org.mevenide.netbeans.grammar;

import java.beans.FeatureDescriptor;
import java.util.Enumeration;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.openide.util.enum.SingletonEnumeration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MavenQueryProvider extends GrammarQueryManager {

    public MavenQueryProvider()
    {
    }
    
    public Enumeration enabled(GrammarEnvironment ctx) {
        Enumeration en = ctx.getDocumentChildren();
        while (en.hasMoreElements()) {
            Node next = (Node) en.nextElement();
            if (next.getNodeType() == next.ELEMENT_NODE) {
                Element root = (Element) next;                
                if ("project".equals(root.getNodeName())) { // NOI18N
                    // add check for pomversion..
                    return new SingletonEnumeration(next);
                }
            }
        }
        return null;
    }
    
    public FeatureDescriptor getDescriptor() {
        return new FeatureDescriptor();
    }
    
    public GrammarQuery getGrammar(GrammarEnvironment env) {
        if (env.getFileObject().getNameExt().equals("project.xml")) {
            return new MavenProjectGrammar();
        }
        if (env.getFileObject().getNameExt().equals("plugin.jelly")) {
            return new MavenJellyGrammar();
        }
        if (env.getFileObject().getNameExt().equals("template.jelly")) {
            return new MavenJellyGrammar();
        }
        if (env.getFileObject().getNameExt().equals("maven.xml")) {
            return new MavenJellyGrammar();
        }
        return null;
    }
    
}
