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
import org.codehaus.mevenide.grammar.AbstractSchemaBasedGrammar.MyTextElement;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.jdom.Element;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.HintContext;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 */
public class MavenSettingsGrammar extends AbstractSchemaBasedGrammar {

    public static final String[] UPDATE_POLICIES = new String[]{
        "always", //NOI18N
        "daily", //NOI18N
        "never", //NOI18N
        "interval:10", //NOI18N
        "interval:60" //NOI18N
    };
    public static final String[] CHECKSUM_POLICIES = new String[]{
        "fail", //NOI18N
        "warn" //NOI18N
    };
    public static final String[] LAYOUTS = new String[]{
        "default", //NOI18N
        "legacy" //NOI18N
    };

    public MavenSettingsGrammar(GrammarEnvironment env) {
        super(env);
    }

    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/codehaus/mevenide/grammar/settings-1.0.0.xsd"); //NOI18N
    }

    protected List getDynamicCompletion(String path, HintContext hintCtx, Element lowestParent) {
        if ("/settings/proxies".equals(path)) { //NOI18N
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
        if (path.endsWith("releases/updatePolicy") || //NOI18N
                path.endsWith("snapshots/updatePolicy")) { //NOI18N
            return super.createTextValueList(UPDATE_POLICIES, virtualTextCtx);
        }
        if (path.endsWith("releases/checksumPolicy") || //NOI18N
                path.endsWith("snapshots/checksumPolicy")) { //NOI18N
            return super.createTextValueList(CHECKSUM_POLICIES, virtualTextCtx);
        }
        if (path.endsWith("repository/layout") || //NOI18N
                path.endsWith("pluginRepository/layout")) { //NOI18N
            return super.createTextValueList(LAYOUTS, virtualTextCtx);
        }
        if (path.endsWith("repositories/repository/url") || //NOI18N
                path.endsWith("pluginRepositories/pluginRepository/url")) { //NOI18N
            List<String> repoIds = getRepoUrls();
            return super.createTextValueList(repoIds.toArray(new String[0]), virtualTextCtx);
        }

        if (path.endsWith("pluginGroups/pluginGroup")) { //NOI18N

            Set elems = RepositoryUtil.getDefaultRepositoryIndexer().filterPluginGroupIds(RepositoryPreferences.LOCAL_REPO_ID, virtualTextCtx.getCurrentPrefix());
            Iterator it = elems.iterator();
            ArrayList texts = new ArrayList();
            while (it.hasNext()) {
                String elem = (String) it.next();
                texts.add(new MyTextElement(elem, virtualTextCtx.getCurrentPrefix()));
            }
            return Collections.enumeration(texts);

        }

        return null;
    }

    /*Return repo url's*/
    private List<String> getRepoUrls() {
        List<String> repos = new ArrayList<String>();

        List<RepositoryInfo> ris = RepositoryPreferences.getInstance().getRepositoryInfos();
        for (RepositoryInfo ri : ris) {
            if(ri.getRepositoryUrl()!=null){
             repos.add(ri.getRepositoryUrl());
            }
        }

        return repos;

    }
}
