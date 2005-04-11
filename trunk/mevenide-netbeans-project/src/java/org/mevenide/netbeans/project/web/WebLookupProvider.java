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

package org.mevenide.netbeans.project.web;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mevenide.netbeans.api.project.AdditionalMavenLookupProvider;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebLookupProvider implements AdditionalMavenLookupProvider {
    
    /** Creates a new instance of WebLookupProvider */
    public WebLookupProvider() {
    }

    public Lookup createMavenLookup(MavenProject context) {
        // if there's more items later, just do a proxy..
        InstanceContent ic = new InstanceContent ();
        return new Provider(context, ic);
    }
    
    private static class Provider extends AbstractLookup implements  PropertyChangeListener {
        private MavenProject project;
        private InstanceContent content;
        private WebModuleImpl impl;
        private boolean isAdded;
        public Provider(MavenProject proj, InstanceContent cont) {
            super(cont);
            project = proj;
            content = cont;
            impl = new WebModuleImpl(project);
            isAdded = false;
            checkWebApp();
        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (MavenProject.PROP_PROJECT.equals(propertyChangeEvent.getPropertyName())) {
                checkWebApp();
            }
        }
        
        private void checkWebApp() {
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
        }
        
    }
}
