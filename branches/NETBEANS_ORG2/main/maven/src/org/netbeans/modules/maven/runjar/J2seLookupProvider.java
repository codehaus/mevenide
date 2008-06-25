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
package org.netbeans.modules.maven.runjar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.configurations.ConfigurationProviderEnabler;
import org.netbeans.spi.project.LookupProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * extending the default maven project lookup.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class J2seLookupProvider implements LookupProvider {
    
    /** Creates a new instance of J2eeLookupProvider */
    public J2seLookupProvider() {
    }
    
    public Lookup createAdditionalLookup(Lookup baseLookup) {
        NbMavenProjectImpl project = baseLookup.lookup(NbMavenProjectImpl.class);
        ConfigurationProviderEnabler config = baseLookup.lookup(ConfigurationProviderEnabler.class);
        assert project != null;
        assert config != null;
//        // if there's more items later, just do a proxy..
        InstanceContent ic = new InstanceContent();
        //sort of hack.. the base lookup is static list, need instance content
        // from somewhere, why not here?
        config.setInstanceContent(ic);
        Provider prov = new Provider(project, ic);
        return prov;
    }
    
    public static class Provider extends AbstractLookup implements  PropertyChangeListener {
        private NbMavenProjectImpl project;
        private InstanceContent content;
        private RunJarPrereqChecker runJarChecker = new RunJarPrereqChecker();
        public Provider(NbMavenProjectImpl proj, InstanceContent cont) {
            super(cont);
            project = proj;
            content = cont;
            checkJ2se();
            NbMavenProject.addPropertyChangeListener(project, this);
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (NbMavenProjectImpl.PROP_PROJECT.equals(propertyChangeEvent.getPropertyName())) {
                checkJ2se();
            }
        }
        
        private void checkJ2se() {
            NbMavenProject watcher = project.getLookup().lookup(NbMavenProject.class);
            String packaging = watcher.getPackagingType();
            doCheckJ2se(packaging);
        }
        
        
        private void doCheckJ2se(String packaging) {
            content.remove(runJarChecker);
            if (NbMavenProject.TYPE_JAR.equals(packaging)) {
                content.add(runJarChecker);
            } 
        }
    }
}
