/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.eclipse.repository.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.Dependency;
import org.jface.mavenzilla.GroupSearch;
import org.jface.mavenzilla.Search;
import org.jface.mavenzilla.SearchResult;
import org.mevenide.ui.eclipse.repository.RepositoryBrowsingException;
import org.mevenide.ui.eclipse.repository.RepositoryObjectCollector;
import org.mevenide.ui.eclipse.repository.model.Artifact;
import org.mevenide.ui.eclipse.repository.model.BaseRepositoryObject;
import org.mevenide.ui.eclipse.repository.model.Group;
import org.mevenide.ui.eclipse.repository.model.Repository;
import org.mevenide.ui.eclipse.repository.model.Type;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class TypeBrowser implements RepositoryObjectCollector {
    
    private Search search;
    
    private Type type;
    
    public TypeBrowser(Type type) {
        this.type = type;
    }
    
    public BaseRepositoryObject[] collect() throws RepositoryBrowsingException {
        Group group = (Group) type.getParent();
        try {
            
            GroupSearch search = new GroupSearch(((Repository) group.getParent()).getRepositoryUrl(), StringUtils.stripEnd(type.getName(), "s"), group.getName());
            
            Collection searchResults = search.search();
            
            List artifacts = new ArrayList();
            for ( Iterator it = searchResults.iterator(); it.hasNext(); ) {
                SearchResult searchResult = (SearchResult) it.next();
                Artifact artifact = new Artifact(searchResult.getName(), searchResult.getVersion(), type);
                artifacts.add(artifact);
            }
            return (Artifact[]) artifacts.toArray(new Artifact[artifacts.size()]);
        }
        catch ( IOException e ) {
            throw new RepositoryBrowsingException("Unable to browse type " + type.getName() + " for group " + group.getName(), e);
        }
    }
    
    
    public Dependency download(BaseRepositoryObject object) {
        
        return null;
    }
}
