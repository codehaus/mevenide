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

package org.codehaus.mevenide.netbeans.j2ee.ejb;

import java.io.IOException;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * implementation of ejb netbeans functionality
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class EjbJarImpl implements EjbJarImplementation {
    
    private NbMavenProject project;
    /** Creates a new instance of EjbJarImpl */
    EjbJarImpl(NbMavenProject proj) {
        project = proj;
    }

    boolean isValid() {
        //TODO any checks necessary??.
        return true;
    }
    
    /** J2EE platform version - one of the constants 
     * defined in {@link org.netbeans.modules.j2ee.api.common.EjbProjectConstants}.
     * @return J2EE platform version
     */

    public String getJ2eePlatformVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                EjbJar ejb = prov.getDDRoot(dd);
                String ejbVersion = ejb.getVersion().toString();
                return ejbVersion;
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
        // hardwire?
        return EjbJar.VERSION_2_0;
    }
    
    /** META-INF folder for the web module.
     */

    public FileObject getMetaInf() {
        Sources srcs = ProjectUtils.getSources(project);
        if (srcs != null) {
            SourceGroup[] grp = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
            for (int i = 0; i < grp.length; i++) {
                FileObject fo = grp[i].getRootFolder().getFileObject("META-INF");
                if (fo != null) {
                    return fo;
                }
            }
        }
        return null;
    }

    /** Deployment descriptor (ejb-jar.xml file) of the ejb module.
     */
    public FileObject getDeploymentDescriptor() {
        FileObject metaInf = getMetaInf();
        if (metaInf != null) {
            return metaInf.getFileObject("ejb-jar.xml");
        }
        return null;
    }

    /** Source roots associated with the EJB module.
     * <div class="nonnormative">
     * Note that not all the java source roots in the project (e.g. in a freeform project)
     * belong to the EJB module.
     * </div>
     */
    public FileObject[] getJavaSources() {
        return null;
    }
    
}
