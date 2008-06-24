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
import java.util.Enumeration;
import java.util.List;

import org.netbeans.modules.maven.api.ProjectProfileHandler;
import org.jdom.Element;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.HintContext;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 * for profiles.xml files..
 */
public class MavenProfilesGrammar extends AbstractSchemaBasedGrammar {
    
    
    public MavenProfilesGrammar(GrammarEnvironment env) {
        super(env);
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/netbeans/modules/maven/grammar/profiles-1.0.0.xsd"); //NOI18N
    }
    

    @Override
    protected List getDynamicCompletion(String path, HintContext hintCtx, Element lowestParent) {
        return Collections.EMPTY_LIST;
    }
    
    @Override
    protected Enumeration getDynamicValueCompletion(String path, HintContext virtualTextCtx, Element el) {
        if (path.endsWith("activeProfiles/activeProfile")) { //NOI18N

            Project proj = FileOwnerQuery.getOwner(getEnvironment().getFileObject());
            if (proj != null) {
                ProjectProfileHandler profileHandler = proj.getLookup().lookup(ProjectProfileHandler.class);
                List<String> profiles = profileHandler.getAllProfiles();
                return super.createTextValueList((String[]) profiles.toArray(new String[profiles.size()]), virtualTextCtx);
            }
        }
        return null;
    }
}
