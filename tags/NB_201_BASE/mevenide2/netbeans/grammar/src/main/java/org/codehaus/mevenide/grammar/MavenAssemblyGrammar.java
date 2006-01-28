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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.jdom.Element;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarResult;
import org.netbeans.modules.xml.api.model.HintContext;
import org.netbeans.modules.xml.spi.dom.NodeListImpl;
import org.openide.ErrorManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * xml completion grammar based on xsd, additionally allowing more to be added.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenAssemblyGrammar extends AbstractSchemaBasedGrammar {
    
    
    public MavenAssemblyGrammar(GrammarEnvironment env) {
        super(env);
    }
    
    protected InputStream getSchemaStream() {
        return getClass().getResourceAsStream("/org/codehaus/mevenide/grammar/assembly-1.0.0.xsd");
    }
    
    protected Enumeration getDynamicValueCompletion(String path, HintContext virtualTextCtx, Element element) {
        if ("/assembly/dependencySets/dependencySet/includes/include".equals(path) ||
            "/assembly/dependencySets/dependencySet/excludes/exclude".equals(path)) {
            //TODO could be nice to filter out the dependencies that are already being used..
            List toRet = new ArrayList();
            NbMavenProject project = getOwnerProject();
            if (project != null) {
                Node previous;
                // HACK.. if currentPrefix is zero length, the context is th element, otherwise it's the content inside
                if (virtualTextCtx.getCurrentPrefix().length() == 0) {
                     previous = virtualTextCtx.getParentNode().getParentNode(); 
                } else {
                    previous = virtualTextCtx.getParentNode().getParentNode().getParentNode();
                }
                previous = previous.getPreviousSibling();
                String scope = null;
                while (previous != null) {
                    if (previous instanceof org.w3c.dom.Element) {
                        org.w3c.dom.Element el = (org.w3c.dom.Element)previous;
                        NodeList lst = el.getChildNodes();
                        if (lst.getLength() > 0) {
                            if ("scope".equals(el.getNodeName())) {
                                scope = lst.item(0).getNodeValue();
                                break;
                            }
                        }
                    }
                    previous = previous.getPreviousSibling();
                }
                if (scope == null) {
                    scope = "runtime";
                }
                scope = scope.trim();
                Iterator it;
                if ("runtime".equals(scope)) {
                    it = project.getOriginalMavenProject().getRuntimeDependencies().iterator();
                } else if ("test".equals(scope)) {
                    it = project.getOriginalMavenProject().getTestDependencies().iterator();
                } else if ("compile".equals(scope)) {
                    it = project.getOriginalMavenProject().getCompileDependencies().iterator();
                } else {
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "How to process includes/excludes for scope '" + scope + "'? Fallback to 'runtime'.");
                    it = project.getOriginalMavenProject().getRuntimeDependencies().iterator();
                }
                while (it.hasNext()) {
                    Dependency elem = (Dependency) it.next();
                    String str = elem.getGroupId() + ":" + elem.getArtifactId();
                    if (str.startsWith(virtualTextCtx.getCurrentPrefix())) {
                        toRet.add(new MyTextElement(str, virtualTextCtx.getCurrentPrefix()));
                    }
                }
            }
            return Collections.enumeration(toRet);
        }
        return null;
    }
    
    
}
