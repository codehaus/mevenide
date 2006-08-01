/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.newproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.record.StandardArtifactIndexRecord;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author mkleint
 */
public class LocalRepoProvider implements ArchetypeProvider {
    
    /** Creates a new instance of LocalRepoProvider */
    public LocalRepoProvider() {
    }

    public List getArchetypes() {
        List lst = new ArrayList();
        try {
            Iterator it = LocalRepositoryIndexer.getInstance().retrievePossibleArchetypes().iterator();
            while (it.hasNext()) {
                StandardArtifactIndexRecord art = (StandardArtifactIndexRecord) it.next();
                Archetype arch = new Archetype();
                arch.setArtifactId(art.getArtifactId());
                arch.setGroupId(art.getGroupId());
                arch.setVersion(art.getVersion());
                arch.setName(art.getProjectName());
                arch.setDescription(art.getProjectDescription());
                lst.add(arch);
            }
        } catch (RepositoryIndexSearchException ex) {
            ex.printStackTrace();
        }
        return lst;
    }
    
}
