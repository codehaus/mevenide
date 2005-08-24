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
package org.mevenide.project.dependency;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.project.io.ProxyContentProvider;

/**  
 * Utility class to find dependencies matching a certain pattern in a project 
 * and replace it with the new values.
 * 
 *
 * @author Milos Kleint
 * 
 */
public final class DependencyMatcher {

    public static IQueryContext[] matchingContexts(IQueryContext[] input, IDependencyPattern patern) {
        ArrayList toReturn = new ArrayList();
        
        for (int i = 0; i < input.length; i++) {
            List dependencies = input[i].getPOMContext().getFinalProject().getDependencies();
            if (dependencies != null) {
                Iterator it = dependencies.iterator();
                while (it.hasNext()) {
                    Dependency dep = (Dependency)it.next();
                    if (patern.matches(dep, input[i])) {
                        toReturn.add(input[i]);
                    }
                }
            }
        }
        IQueryContext[] contexts = new IQueryContext[toReturn.size()];
        contexts = (IQueryContext[])toReturn.toArray(contexts);
        return contexts;
    }
    
    /** 
     * using the given replacer, the method will find and match the dependency and return a proxy IContentProvider
     * with the changes incorporated.
     * @param original the IContentProvider for the given file (not only for the dependency but for whole pom file)
     * @param context IQueryContent for the project in question (to be written using the original content provider)
     * @param replacer 
     */
    public static IContentProvider replace(IDependencyReplacer replacer, 
                                          IContentProvider originalPom) 
    {
       return new DependencyProxyContentProvider(originalPom, replacer);
                                              
    }
    
    private static class DependencyProxyContentProvider extends ProxyContentProvider {
        private IDependencyReplacer replacer;
        public DependencyProxyContentProvider(IContentProvider provider, IDependencyReplacer repl) {
            super(provider);
            replacer = repl;
        }
        
        public List getSubContentProviderList(String parentKey, String childKey) {
            List lst = super.getSubContentProviderList(parentKey, childKey);
            if (lst != null && "dependencies".equals(parentKey)) { //NOI18N
                List newOne = new ArrayList();
                Iterator it = lst.iterator();
                while (it.hasNext()) {
                    IContentProvider provider = (IContentProvider)it.next();
                    newOne.add(replacer.replace(provider));
                }
                lst = newOne;
            }
            return lst;
        }
    }
}