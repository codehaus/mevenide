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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.codehaus.mevenide.grammar.AbstractSchemaBasedGrammar.MyTextElement;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.jdom.Element;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.HintContext;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 */
public class MavenSettingsGrammar extends AbstractSchemaBasedGrammar {
    
    public static final String[] UPDATE_POLICIES = new String[] {
        "always",
        "daily",
        "never",
        "interval:10",
        "interval:60"
    };
    public static final String[] CHECKSUM_POLICIES = new String[] {
        "fail",
        "warn"
    };
    public static final String[] LAYOUTS = new String[] {
        "default",
        "legacy"
    };
    
    public MavenSettingsGrammar(GrammarEnvironment env) {
        super(env);
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/codehaus/mevenide/grammar/settings-1.0.0.xsd");
    }
    

    protected List getDynamicCompletion(String path, HintContext hintCtx, Element lowestParent) {
        if ("/settings/proxies".equals(path)) {
            // doesn't work!!!'
//            if ("proxy".startsWith(hintCtx.getCurrentPrefix())) {
//                ArrayList lst = new ArrayList();
//                lst.add(new MyElement("host"));
//                lst.add(new MyElement("port"));
//                GrammarResult rootRes = new ComplexElement("proxy2", "Insert Proxy", new NodeListImpl(lst));
//                return Collections.singletonList(rootRes);
//            }
        }
        return Collections.EMPTY_LIST;
    }
    

    protected Enumeration getDynamicValueCompletion(String path, HintContext virtualTextCtx, Element el) {
        if ("/settings/profiles/profile/repositories/repository/releases/updatePolicy".equals(path) ||
            "/settings/profiles/profile/repositories/repository/snapshots/updatePolicy".equals(path) ||
            "/settings/profiles/profile/pluginRepositories/pluginRepository/releases/updatePolicy".equals(path) ||
            "/settings/profiles/profile/pluginRepositories/pluginRepository/snapshots/updatePolicy".equals(path)) {
            return super.createTextValueList(UPDATE_POLICIES, virtualTextCtx);
        }
        if ("/settings/profiles/profile/repositories/repository/releases/checksumPolicy".equals(path) ||
            "/settings/profiles/profile/repositories/repository/snapshots/checksumPolicy".equals(path) ||
            "/settings/profiles/profile/pluginRepositories/pluginRepository/releases/checksumPolicy".equals(path) ||
            "/settings/profiles/profile/pluginRepositories/pluginRepository/snapshots/checksumPolicy".equals(path)) {
            return super.createTextValueList(CHECKSUM_POLICIES, virtualTextCtx);
        }
        if ("/settings/profiles/profile/repositories/repository/layout".equals(path) ||
            "/settings/profiles/profile/pluginRepositories/pluginRepository/layout".equals(path)) {
            return super.createTextValueList(LAYOUTS, virtualTextCtx);
        }
        if (path.endsWith("pluginGroups/pluginGroup")) {
            try {
                Set elems = CustomQueries.retrievePluginGroupIds(virtualTextCtx.getCurrentPrefix());
                Iterator it = elems.iterator();
                ArrayList texts = new ArrayList();
                while (it.hasNext()) {
                    String elem = (String) it.next();
                    texts.add(new MyTextElement(elem, virtualTextCtx.getCurrentPrefix()));
                }
                return Collections.enumeration(texts);
            } catch (RepositoryIndexSearchException ex) {
                ex.printStackTrace();
            }
        }
        
        return null;
    }

    
    
}
