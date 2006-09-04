/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.netbeans.j2ee;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.codehaus.mevenide.netbeans.AdditionalM2LookupProvider;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.j2ee.ear.EarModuleProviderImpl;
import org.codehaus.mevenide.netbeans.j2ee.ejb.EjbModuleProviderImpl;
import org.codehaus.mevenide.netbeans.j2ee.web.WebModuleProviderImpl;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;

/**
 * extending the default maven project lookup.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class J2eeLookupProvider implements AdditionalM2LookupProvider {
    
    /** Creates a new instance of J2eeLookupProvider */
    public J2eeLookupProvider() {
    }
    
    public Lookup createMavenLookup(NbMavenProject project) {
//        // if there's more items later, just do a proxy..
        InstanceContent ic = new InstanceContent();
        ic.add(new J2EEPrerequisitesChecker());
        return new Provider(project, ic);
    }
    
    private static class Provider extends AbstractLookup implements  PropertyChangeListener {
        private NbMavenProject project;
        private InstanceContent content;
        private String lastType = "jar";
        private Object lastInstance = null;
        public Provider(NbMavenProject proj, InstanceContent cont) {
            super(cont);
            project = proj;
            content = cont;
            checkJ2ee();
            project.addPropertyChangeListener(this);
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (NbMavenProject.PROP_PROJECT.equals(propertyChangeEvent.getPropertyName())) {
                checkJ2ee();
            }
        }
        
        private void checkJ2ee() {
            String packaging = project.getOriginalMavenProject().getPackaging();
            if (packaging == null) {
                packaging = "jar";
            }
            if ("war".equals(packaging) && !lastType.equals(packaging)) {
                if (lastInstance != null) {
                    content.remove(lastInstance);
                }
                lastInstance = new WebModuleProviderImpl(project);
                content.add(lastInstance);
            } else if ("ear".equals(packaging) && !lastType.equals(packaging)) {
                if (lastInstance != null) {
                    content.remove(lastInstance);
                }
                lastInstance = new EarModuleProviderImpl(project);
                content.add(lastInstance);
            } else if ("ejb".equals(packaging) && !lastType.equals(packaging)) {
                if (lastInstance != null) {
                    content.remove(lastInstance);
                }
                lastInstance = new EjbModuleProviderImpl(project);
                content.add(lastInstance);
            } else if (lastInstance != null && !(
                    "war".equals(packaging) || 
                    "ejb".equals(packaging) || 
                    "ear".equals(packaging)))
            {
                content.remove(lastInstance);
                lastInstance = null;
            }
            lastType = packaging;
        }
        
    }
}
