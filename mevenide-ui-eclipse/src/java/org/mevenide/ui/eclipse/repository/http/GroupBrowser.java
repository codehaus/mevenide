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
import org.mevenide.ui.eclipse.repository.model.Type;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class GroupBrowser implements RepositoryObjectCollector {
    
    private Search search;
    
    private Group group;
    
    
    private static final class GroupSearch extends Search {
	    private static final String XPATH = "//a[" +
											"not(starts-with(@href, '?') " + 
											"or starts-with(@href, '/')) " +
	    									"and ends-with(@href, '/')" +
	    									"]";
	    
        GroupSearch(String url, String groupName) {
            super(url.endsWith("/") ? url + groupName : url + "/" + groupName);
        }
	    
	    protected Collection processDocument(Document doc) throws MalformedURLException, IOException {
	        Collection result = new ArrayList();
	        for (Iterator it = doc.selectNodes(XPATH).iterator(); it.hasNext();)
	        {
	            Element a = (Element) it.next();
	            String href = a.attributeValue("href");
	            String type = StringUtils.stripEnd(a.getTextTrim(), "/");
	            type = type != null ? StringUtils.stripEnd(type.trim(), "s") : type;
	            result.add(type);
	        }
	        return result;
	    }
        
    }
    
    public GroupBrowser(Group group) {
        this.group = group;
        this.search = new GroupSearch(((Repository) group.getParent()).getUrl(), group.getName());
    }
    
    
    public BaseRepositoryObject[] collect() throws RepositoryBrowsingException {
        try {
            Collection typeNames = search.search();
           
            List types = new ArrayList(); 
            
            for ( Iterator it = typeNames.iterator(); it.hasNext(); ) {
                String typeName = (String) it.next();
                Type type = new Type(typeName, group);
                types.add(type);
            }
            return (Type[]) types.toArray(new Type[types.size()]);
        }
        catch ( IOException e ) {
            throw new RepositoryBrowsingException("Unable to browse group " + group.getName(), e);
        }
    }
}
