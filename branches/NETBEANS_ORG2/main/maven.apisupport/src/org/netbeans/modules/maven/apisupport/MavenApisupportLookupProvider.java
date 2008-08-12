/* ==========================================================================
 * Copyright 2007 Mevenide Team
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
package org.netbeans.modules.maven.apisupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.LookupProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * extending the default maven project lookup.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class MavenApisupportLookupProvider implements LookupProvider {
    
    /** Creates a new instance of MavenApisupportLookupProvider */
    public MavenApisupportLookupProvider() {
    }
    
    public Lookup createAdditionalLookup(Lookup baseLookup) {
        Project project = baseLookup.lookup(Project.class);
        assert project != null;
//        // if there's more items later, just do a proxy..
        InstanceContent ic = new InstanceContent();
        ic.add(new ApisupportRecoPrivTemplates(project));
        ic.add(new ExecutionChecker(project));
        return new Provider(project, ic);
    }
    
    private static class Provider extends AbstractLookup implements  PropertyChangeListener {
        private Project project;
        private InstanceContent content;
        private String lastType = NbMavenProject.TYPE_JAR;
        private MavenNbModuleImpl lastInstance = null;
        private AccessQueryImpl lastAccess = null;
        public Provider(Project proj, InstanceContent cont) {
            super(cont);
            project = proj;
            content = cont;
            checkNbm();
            NbMavenProject.addPropertyChangeListener(project, this);
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (NbMavenProject.PROP_PROJECT.equals(propertyChangeEvent.getPropertyName())) {
                checkNbm();
            }
        }
        
        private void checkNbm() {
            NbMavenProject watcher = project.getLookup().lookup(NbMavenProject.class);
            String packaging = watcher.getPackagingType();
            if (packaging == null) {
                packaging = NbMavenProject.TYPE_JAR;
            }
            if (NbMavenProject.TYPE_NBM.equals(packaging) && !lastType.equals(packaging)) {
                if (lastInstance == null) {
                    lastInstance = new MavenNbModuleImpl(project);
                }
                content.add(lastInstance);
                if (lastAccess == null) {
                    lastAccess = new AccessQueryImpl(project);
                }
                content.add(lastAccess);
            } else if (lastInstance != null && !(
                    NbMavenProject.TYPE_NBM.equals(packaging)))
            {
                content.remove(lastInstance);
                content.remove(lastAccess);
            }
            lastType = packaging;
        }
        
    }

}
