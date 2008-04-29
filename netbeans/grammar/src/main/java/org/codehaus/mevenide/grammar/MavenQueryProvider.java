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
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Result;
import org.w3c.dom.Node;

public final class MavenQueryProvider extends GrammarQueryManager {

    private List<GrammarFactory> grammars;
    public MavenQueryProvider() {
        grammars = new ArrayList<GrammarFactory>();
        
        
        Result<GrammarFactory> result = Lookup.getDefault().lookupResult(GrammarFactory.class);
        Collection<? extends Item<GrammarFactory>> items = result.allItems();
        for (Item<GrammarFactory> item : items) {
            grammars.add(item.getInstance());
        }

    }
    
    public Enumeration enabled(GrammarEnvironment ctx) {
        // check if is supported environment..
        if (getGrammar(ctx) != null) {
            Enumeration en = ctx.getDocumentChildren();
            while (en.hasMoreElements()) {
                Node next = (Node)en.nextElement();
                if (next.getNodeType() == next.ELEMENT_NODE) {
                    return Collections.enumeration(Collections.singletonList(next));
                }
            }
        }
        return null;
    }
    
    public FeatureDescriptor getDescriptor() {
        return new FeatureDescriptor();
    }
    
    public GrammarQuery getGrammar(GrammarEnvironment env) {
        for (GrammarFactory gr : grammars) {
            GrammarQuery query = gr.isSupported(env);
            if (query != null) {
                return query;
            }
        }
        return null;
    }
    
}
