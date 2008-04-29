/*
 * Copyright 2008 Mevenide Team
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.classpath;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkleint
 */
class PackagedClassPathImpl extends AbstractProjectClassPathImpl {

    public PackagedClassPathImpl(NbMavenProject project) {
        super(project);
    }

    public URI[] createPath() {
        List lst = new ArrayList();
        List arts = getMavenProject().getOriginalMavenProject().getCompileArtifacts();
        List<Dependency> deps = getMavenProject().getOriginalMavenProject().getCompileDependencies();
        List<String> packagedIds = new ArrayList<String>();
        for (Dependency dep : deps) {
            if (!Artifact.SCOPE_PROVIDED.equals(dep.getScope()))  {
                packagedIds.add(dep.getManagementKey());
            }
        }
        
        List assemblies = new ArrayList();
        Iterator it = arts.iterator();
        while (it.hasNext()) {
            Artifact art = (Artifact)it.next();
            String key = art.getGroupId() + ":" + art.getArtifactId() + ":" + art.getType(); //NOI18N
            if (art.getFile() != null && packagedIds.contains(key)) {
                File fil = FileUtil.normalizeFile(art.getFile());
                // the assemblied jars go as last ones, otherwise source for binaries don't really work.
                // unless one has the assembled source jar s well?? is it possible?
                if (art.getClassifier() != null) {
                    assemblies.add(0, fil);
                } else {
                    lst.add(fil.toURI());
                }
            } else {
              //NOPMD   //null means dependencies were not resolved..
            } 
        }
        it = assemblies.iterator();
        while (it.hasNext()) {
            File ass = (File)it.next();
            lst.add(ass.toURI());
        }
        URI[] uris = new URI[lst.size()];
        uris = (URI[])lst.toArray(uris);
        return uris;
    }

}
