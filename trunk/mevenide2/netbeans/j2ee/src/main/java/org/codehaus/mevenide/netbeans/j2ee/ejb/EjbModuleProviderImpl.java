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
package org.codehaus.mevenide.netbeans.j2ee.ejb;

import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.api.ejbjar.Ear;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.ejbjar.EarAccessor;
import org.netbeans.modules.j2ee.spi.ejbjar.EarProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarFactory;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class EjbModuleProviderImpl implements EjbJarProvider  {
    
    private EjbJarImpl ejbimpl;
    private NbMavenProject project;
    /** Creates a new instance of MavenEarProvider */
    public EjbModuleProviderImpl(NbMavenProject proj) {
        project = proj;
        ejbimpl = new EjbJarImpl(project);
    }
    

    public EjbJar findEjbJar(FileObject file) {
        Project proj = FileOwnerQuery.getOwner (file);
        if (proj != null) {
            proj = (Project)proj.getLookup().lookup(NbMavenProject.class);
        }
        if (proj != null && project == proj) {
            if (ejbimpl != null && ejbimpl.isValid()) {
                return EjbJarFactory.createEjbJar(ejbimpl);
            }
        }
        return null;
    }
    
}
