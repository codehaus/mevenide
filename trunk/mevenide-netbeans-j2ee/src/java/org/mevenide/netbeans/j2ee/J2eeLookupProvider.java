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
package org.mevenide.netbeans.j2ee;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mevenide.netbeans.api.project.AdditionalMavenLookupProvider;
import org.mevenide.netbeans.j2ee.web.WebModuleImpl;
import org.mevenide.netbeans.j2ee.web.WebModuleProviderImpl;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class J2eeLookupProvider implements AdditionalMavenLookupProvider {
    
    /** Creates a new instance of J2eeLookupProvider */
    public J2eeLookupProvider() {
    }

     public Lookup createMavenLookup(MavenProject project) {
//         return Lookups.fixed(new Object[] {
//            new WebModuleImpl(project),
//            new WebModuleProviderImpl(project),
//            new MavenEarEjbProvider(),
//            new MavenEarImpl(project),
//            new MavenEjbJarImpl(project),
//            new J2eeModuleProviderImpl(project)
//         });
//        // if there's more items later, just do a proxy..
        InstanceContent ic = new InstanceContent ();
        return new Provider(project, ic);
    }
    
    private static class Provider extends AbstractLookup implements  PropertyChangeListener {
        private MavenProject project;
        private InstanceContent content;
        private MavenEarImpl impl;
        private MavenEjbJarImpl impl2;
        private WebModuleImpl impl3;
        private boolean isAdded;
        private boolean isAdded2;
        private boolean isAdded3;
        public Provider(MavenProject proj, InstanceContent cont) {
            super(cont);
            project = proj;
            content = cont;
            impl = new MavenEarImpl(project);
            isAdded = false;
            impl2 = new MavenEjbJarImpl(project);
            isAdded2 = false;
            content.add(new MavenEarEjbProvider(proj, impl2, impl));
            impl3 = new WebModuleImpl(project);
            isAdded3 = false;
            content.add(new J2eeModuleProviderImpl(project));
            content.add(new WebModuleProviderImpl(project));
            checkJ2ee();
        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (MavenProject.PROP_PROJECT.equals(propertyChangeEvent.getPropertyName())) {
                checkJ2ee();
            }
        }
        
        private void checkJ2ee() {
            if (impl.isValid()) {
                if (!isAdded) {
                    content.add(impl);
                    isAdded = true;
                }
            } else {
                if (isAdded) {
                    content.remove(impl);
                    isAdded = false;
                }
            }
            if (impl2.isValid()) {
                if (!isAdded2) {
                    content.add(impl2);
                    isAdded2 = true;
                }
            } else {
                if (isAdded2) {
                    content.remove(impl2);
                    isAdded2 = false;
                }
            }
            if (impl3.isValid()) {
                if (!isAdded3) {
                    content.add(impl3);
                    isAdded3 = true;
                }
            } else {
                if (isAdded3) {
                    content.remove(impl3);
                    isAdded3 = false;
                }
            }
        }
        
    }
}
