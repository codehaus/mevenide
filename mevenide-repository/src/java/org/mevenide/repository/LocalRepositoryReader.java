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

package org.mevenide.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.mevenide.project.dependency.URIDependencyResolver;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class LocalRepositoryReader extends AbstractRepositoryReader {
    private File rootRepository;
    /** Creates a new instance of LocalRepositoryReader */
    public LocalRepositoryReader(File rootFile) {
        super(rootFile.toURI());
        rootRepository = rootFile;
    }
    
    
    public RepoPathElement[] readElements(RepoPathElement element) throws Exception {
        if (element.isLeaf()) {
            return new RepoPathElement[0];
        }
        String part = element.getPartialURIPath();
        File fil = new File(rootRepository, part);
        if (fil.exists()) {
            File[] fls = fil.listFiles();
            return getChildren(fls, element);
        }
        return new RepoPathElement[0];
    }
    
    private RepoPathElement[] getChildren(File[] files, RepoPathElement element) {
        Collection col = new ArrayList();
        URIDependencyResolver resolver = new URIDependencyResolver();
        Collection knownArtifacts = new HashSet();
        for (int i = 0; i < files.length; i++) {
            RepoPathElement elem = null;
            if (element.getLevel() == RepoPathElement.LEVEL_ROOT) {
                // nothing known
                if (files[i].isDirectory() && ! "Global Project".equals(files[i].getName())) {
                    elem = newChild(element);
                    elem.setGroupId(files[i].getName());
                }
            }
            else if (element.getLevel() == RepoPathElement.LEVEL_GROUP) {
                // groupid known already
                if (files[i].isDirectory() /*&& files[i].getName().endsWith("s")*/) {
                    elem = newChild(element);
                    String type = files[i].getName();
                    elem.setType(type.substring(0, type.length() - 1));
                }
            }
            else if (element.getLevel() == RepoPathElement.LEVEL_TYPE) {
                if (files[i].isFile()) {
                    resolver.setURI(files[i].toURI());
                    elem = levelTypeCheck(element, resolver, knownArtifacts);
                }
            }
            else if (element.getLevel() == RepoPathElement.LEVEL_ARTIFACT) {
                if (files[i].isFile() 
                          && files[i].getName().startsWith(element.getArtifactId())) {
                    
                    resolver.setURI(files[i].toURI());
                    elem = levelArtifactCheck(element, resolver);
                }
            }
            if (elem != null) {
                col.add(elem);
            }
        }
        if (element.getLevel() == RepoPathElement.LEVEL_TYPE) {
            // now we are in one dir, organize versions as well, without contacting server
            // again
            Iterator it = col.iterator();
            while (it.hasNext()) {
                RepoPathElement chil = (RepoPathElement)it.next();
                RepoPathElement[] ch = getChildren(files, chil);
                chil.setChildren(ch);
            }
        }
        RepoPathElement[] elems = new RepoPathElement[col.size()];
        return (RepoPathElement[])col.toArray(elems);
        
    }
}
