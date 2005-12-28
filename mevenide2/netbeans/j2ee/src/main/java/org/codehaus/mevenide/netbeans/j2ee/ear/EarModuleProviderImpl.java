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
package org.codehaus.mevenide.netbeans.j2ee.ear;

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
 * provider for ear specific functionality
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class EarModuleProviderImpl implements EarProvider  {
    
    private EarImpl earimpl;
    private NbMavenProject project;
    /** Creates a new instance of MavenEarProvider */
    public EarModuleProviderImpl(NbMavenProject proj) {
        project = proj;
        earimpl = new EarImpl(project);
    }
    
    public Ear findEar(FileObject file) {
        Project proj = FileOwnerQuery.getOwner(file);
        if (proj != null && proj instanceof NbMavenProject && project == proj) {
            if (earimpl != null && earimpl.isValid()) {
                return EjbJarFactory.createEar(earimpl);
            }
        }
        return null;
    }
}
