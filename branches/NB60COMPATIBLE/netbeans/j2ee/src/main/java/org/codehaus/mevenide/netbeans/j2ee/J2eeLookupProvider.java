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
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.j2ee.ear.EarModuleProviderImpl;
import org.codehaus.mevenide.netbeans.j2ee.ejb.EjbModuleProviderImpl;
import org.codehaus.mevenide.netbeans.j2ee.web.CopyOnSave;
import org.codehaus.mevenide.netbeans.j2ee.POHImpl;
import org.codehaus.mevenide.netbeans.j2ee.web.WebModuleProviderImpl;
import org.netbeans.spi.project.LookupProvider;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * extending the default maven project lookup.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class J2eeLookupProvider implements LookupProvider {
    
    /** Creates a new instance of J2eeLookupProvider */
    public J2eeLookupProvider() {
    }
    
    public Lookup createAdditionalLookup(Lookup baseLookup) {
        NbMavenProject project = baseLookup.lookup(NbMavenProject.class);
        assert project != null;
//        // if there's more items later, just do a proxy..
        InstanceContent ic = new InstanceContent();
        ic.add(new J2EEPrerequisitesChecker());
        ic.add(new J2eeRecoPrivTemplates(project));
        ic.add(new J2eeMavenSourcesImpl(project));
        Provider prov = new Provider(project, ic);
        ic.add(new POHImpl(project, prov));
        return prov;
    }
    
    public static class Provider extends AbstractLookup implements  PropertyChangeListener {
        private NbMavenProject project;
        private InstanceContent content;
        private String lastType = ProjectURLWatcher.TYPE_JAR;
        private Object lastInstance = null;
        private CopyOnSave copyOnSave;
        public Provider(NbMavenProject proj, InstanceContent cont) {
            super(cont);
            project = proj;
            content = cont;
            checkJ2ee();
            ProjectURLWatcher.addPropertyChangeListener(project, this);
        }
        
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (NbMavenProject.PROP_PROJECT.equals(propertyChangeEvent.getPropertyName())) {
                checkJ2ee();
            }
        }
        
        private void checkJ2ee() {
            ProjectURLWatcher watcher = project.getLookup().lookup(ProjectURLWatcher.class);
            String packaging = watcher.getPackagingType();
            doCheckJ2ee(packaging);
        }
        
        public void hackWebModuleServerChange() {
            //#
            doCheckJ2ee(null);
            checkJ2ee();
        }
        
        public void hackEjbModuleServerChange() {
            //#
            doCheckJ2ee(null);
            checkJ2ee();
        }
        
        private void doCheckJ2ee(String packaging) {
            if (packaging == null) {
                packaging = ProjectURLWatcher.TYPE_JAR;
            }
            if (copyOnSave != null && !ProjectURLWatcher.TYPE_WAR.equals(packaging)) {
                try {
                    copyOnSave.cleanup();
                } catch (FileStateInvalidException ex) {
                    ex.printStackTrace();
                }
                copyOnSave = null;
            }
            if (ProjectURLWatcher.TYPE_WAR.equals(packaging) && !lastType.equals(packaging)) {
                if (lastInstance != null) {
                    content.remove(lastInstance);
                }
                WebModuleProviderImpl prov = new WebModuleProviderImpl(project);
                lastInstance = prov;
                content.add(lastInstance);
                copyOnSave = new CopyOnSave(project, prov);
                try {
                    copyOnSave.initialize();
                } catch (FileStateInvalidException ex) {
                    ex.printStackTrace();
                }
            } else if (ProjectURLWatcher.TYPE_EAR.equals(packaging) && !lastType.equals(packaging)) {
                if (lastInstance != null) {
                    content.remove(lastInstance);
                }
                lastInstance = new EarModuleProviderImpl(project);
                content.add(lastInstance);
            } else if (ProjectURLWatcher.TYPE_EJB.equals(packaging) && !lastType.equals(packaging)) {
                if (lastInstance != null) {
                    content.remove(lastInstance);
                }
                lastInstance = new EjbModuleProviderImpl(project);
                content.add(lastInstance);
            } else if (lastInstance != null && !(
                    ProjectURLWatcher.TYPE_WAR.equals(packaging) || 
                    ProjectURLWatcher.TYPE_EJB.equals(packaging) || 
                    ProjectURLWatcher.TYPE_EAR.equals(packaging)))
            {
                content.remove(lastInstance);
                lastInstance = null;
            }
            lastType = packaging;
        }
        
    }

}
