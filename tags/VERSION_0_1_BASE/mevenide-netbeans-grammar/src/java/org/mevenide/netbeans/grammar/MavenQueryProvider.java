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

package org.mevenide.netbeans.grammar;

import java.beans.FeatureDescriptor;
import java.util.Enumeration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.openide.util.enum.SingletonEnumeration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class MavenQueryProvider extends GrammarQueryManager {

   private static Log logger = LogFactory.getLog(MavenQueryProvider.class); 
    public MavenQueryProvider()
    {
        logger.debug("creating..");
    }
    
    public Enumeration enabled(GrammarEnvironment ctx) {
        logger.debug("enabled...");
        Enumeration en = ctx.getDocumentChildren();
        while (en.hasMoreElements()) {
            Node next = (Node) en.nextElement();
            if (next.getNodeType() == next.ELEMENT_NODE) {
                Element root = (Element) next;                
                if ("project".equals(root.getNodeName())) { // NOI18N
                    // add check for pomversion..
                    logger.debug("do enable..");
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
        logger.debug("creating grammer");
        if (env.getFileObject().getNameExt().equals("project.xml")) {
            logger.debug("creating project grammer");
            return new MavenProjectGrammar();
        }
        if (env.getFileObject().getNameExt().equals("plugin.jelly")) {
            logger.debug("creating jelly grammar");
            return new MavenJellyGrammar();
        }
        if (env.getFileObject().getNameExt().equals("maven.xml")) {
            logger.debug("creating maven.xml jelly grammar");
            return new MavenJellyGrammar();
        }
        return null;
    }
    
}
