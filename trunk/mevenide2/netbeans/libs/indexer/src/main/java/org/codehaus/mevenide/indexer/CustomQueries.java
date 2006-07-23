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

package org.codehaus.mevenide.indexer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Model;
import org.apache.maven.repository.indexing.RepositoryIndex;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.RepositoryIndexSearchHit;
import org.apache.maven.repository.indexing.query.Query;
import org.apache.maven.repository.indexing.query.SinglePhraseQuery;

/**
 *
 * @author mkleint
 */
public class CustomQueries {
    
    /** Creates a new instance of CustomQueries */
    private CustomQueries() {
    }
    
    public static Set retrieveGroupIds(String prefix) throws IOException {
        List lst = LocalRepositoryIndexer.getInstance().getDefaultIndex().enumerateGroupIds();
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            String id = (String)it.next();
            if (id.startsWith(prefix)) {
                elems.add(id);
            }
        }
        return elems;
    }
    
    public static Set retrieveArtifactIdForGroupId(String groupId, String prefix) throws IOException {
        List lst = LocalRepositoryIndexer.getInstance().getDefaultIndex().getArtifacts(groupId);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            String art = (String)it.next();
            if (art.startsWith(prefix)) {
                elems.add(art);
            }
        }
        return elems;
    }
    
    public static Set retrievePluginGroupIds(String prefix) throws RepositoryIndexSearchException {
        Query q = new SinglePhraseQuery(RepositoryIndex.FLD_PACKAGING, "maven-plugin");
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultPomIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isModel()) {
                Model art = (Model)elem.getObject();
                if (art.getGroupId() != null && art.getGroupId().startsWith(prefix)) {
                    elems.add(art.getGroupId());
                }
            }
        }
        return elems;
    }
    
    public static Set retrievePluginArtifactIds(String groupId, String prefix) throws RepositoryIndexSearchException {
        Query q = new SinglePhraseQuery(RepositoryIndex.FLD_PACKAGING, "maven-plugin");
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultPomIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isModel()) {
                Model art = (Model)elem.getObject();
                if (art.getGroupId() != null && art.getGroupId().equals(groupId) &&
                        art.getArtifactId() != null && art.getArtifactId().startsWith(prefix)) {
                    elems.add(art.getArtifactId());
                }
            }
        }
        return elems;
    }
    
    /**
     * returns a list of Artifacts that are archetypes.
     */
    public static Set retrievePossibleArchetypes() throws RepositoryIndexSearchException {
        Query q = new SinglePhraseQuery(RepositoryIndex.FLD_FILES, "archetype.xml");
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        System.out.println("set size = " + lst.size());
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isHashMap()) {
                HashMap map = (HashMap)elem.getObject();
                Artifact art = (Artifact)map.get(RepositoryIndex.ARTIFACT);
                JarFile jf = null;
                System.out.println("art=" + art.getId());
                try {
                    if (!art.getFile().exists()) {
                        System.out.println("artifact doesn't exist" + art.getFile());
                        continue;
                    }
                    jf = new JarFile(art.getFile());
                    if (jf.getJarEntry("META-INF/archetype.xml") != null) {
                        elems.add(art);
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    if (jf != null) {
                        try {
                            jf.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return elems;
    }
    
}
