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
package org.mevenide.ui.eclipse.repository.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.repository.Artifact;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mevenide.ui.eclipse.repository.RepositoryObjectCollector;
import org.mevenide.ui.eclipse.repository.factory.RepositoryObjectCollectorFactory;
import org.mevenide.ui.eclipse.repository.model.BaseRepositoryObject;
import org.mevenide.ui.eclipse.repository.model.Repository;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryContentProvider implements ITreeContentProvider {

    private static final Log log = LogFactory.getLog(RepositoryContentProvider.class);
    
    public RepositoryContentProvider() {
        super();
    }
    
    public Object[] getChildren(Object element) {
        RepositoryObjectCollector collector = RepositoryObjectCollectorFactory.getRepositoryObjectCollector(element);
        try {
            return collector.collect();
        }
        catch ( Exception e ) {
            String message = "Unable to collect repository object " + element + "(" + element.getClass() + ")";
            log.error(message, e);
	        return null;
        }
    }
    
    public Object getParent(Object element) {
        if ( element instanceof BaseRepositoryObject ) {
            return ((BaseRepositoryObject) element).getParent();
        }
        return null;
    }
    
    public boolean hasChildren(Object element) {
        return !(element instanceof Artifact);
    }
    
    public Object[] getElements(Object inputElement) {
        if ( inputElement instanceof String ) {
            return new Object[] {new Repository((String) inputElement)};
        }
        return null;
    }
    

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}

