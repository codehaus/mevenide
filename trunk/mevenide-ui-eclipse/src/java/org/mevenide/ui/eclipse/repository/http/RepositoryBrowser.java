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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jface.mavenzilla.Search;
import org.jface.mavenzilla.StringUtils;
import org.mevenide.ui.eclipse.repository.RepositoryBrowsingException;
import org.mevenide.ui.eclipse.repository.RepositoryObjectCollector;
import org.mevenide.ui.eclipse.repository.model.BaseRepositoryObject;
import org.mevenide.ui.eclipse.repository.model.Group;
import org.mevenide.ui.eclipse.repository.model.Repository;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryBrowser implements RepositoryObjectCollector {
    
    
    private Search search;
    
    private Repository repository;
    
    private static final class RepositorySearch extends Search {
        private static final String XPATH = "//a[" +
											"not(starts-with(@href, '?') " + 
											"or starts-with(@href, '/')) " +
											"and ends-with(@href, '/')" +
											"]";

        RepositorySearch(String url) {
            super(url);
        }
        
		protected Collection processDocument(Document doc) throws MalformedURLException, IOException {
			Collection result = new ArrayList();
			for (Iterator it = doc.selectNodes(XPATH).iterator(); it.hasNext(); ) {
				Element a = (Element) it.next();
				String href = a.attributeValue("href");
				String group = StringUtils.stripEnd(a.getTextTrim(), "/");
				result.add(group);
			}
			return result;
		}
    }
    
    public RepositoryBrowser(Repository repository) {
        this.repository = repository;
        String url = repository.getRepositoryUrl(); 
        search = new RepositorySearch(url.endsWith("/") ? url : url + "/");
    }
    
    public BaseRepositoryObject[] collect() throws RepositoryBrowsingException {
        try {
            Collection groupNames = search.search();
            
            List groups = new ArrayList(); 
            
            for ( Iterator it = groupNames.iterator(); it.hasNext(); ) {
                String groupName = (String) it.next();
                if ( !org.mevenide.util.StringUtils.isNull(groupName) ) {
                    Group group = new Group(groupName, repository);
                	groups.add(group);
                }
            }
            
            return (Group[]) groups.toArray(new Group[groups.size()]);
        }
        catch ( IOException e ) {
            throw new RepositoryBrowsingException("Unable to browse repository " + repository.getRepositoryUrl(), e);
        }
    }
}
