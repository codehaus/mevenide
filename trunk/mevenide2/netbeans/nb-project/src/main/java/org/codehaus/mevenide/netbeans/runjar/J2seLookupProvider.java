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
package org.codehaus.mevenide.netbeans.runjar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
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
        NbMavenProject project = baseLookup.lookup(NbMavenProject.class);
        assert project != null;
//        // if there's more items later, just do a proxy..
        InstanceContent ic = new InstanceContent();
        Provider prov = new Provider(project, ic);
        return prov;
    }
    
    public static class Provider extends AbstractLookup implements  PropertyChangeListener {
        private NbMavenProject project;
        private InstanceContent content;
        private RunJarPrereqChecker runJarChecker = new RunJarPrereqChecker();
        public Provider(NbMavenProject proj, InstanceContent cont) {
            super(cont);
            project = proj;
            content = cont;
            checkJ2se();
            ProjectURLWatcher.addPropertyChangeListener(project, this);
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (NbMavenProject.PROP_PROJECT.equals(propertyChangeEvent.getPropertyName())) {
                checkJ2se();
            }
        }
        
        private void checkJ2se() {
            ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
            String packaging = watcher.getPackagingType();
            doCheckJ2se(packaging);
        }
        
        
        private void doCheckJ2se(String packaging) {
            content.remove(runJarChecker);
            if (ProjectURLWatcher.TYPE_JAR.equals(packaging)) {
                content.add(runJarChecker);
            } 
        }
    }
}
