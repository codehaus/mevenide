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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.repository.Artifact;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
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
    
    private List repositoryEventListeners = new ArrayList();
    
    public void addRepositoryEventListener(RepositoryEventListener listener) {
        repositoryEventListeners.add(listener);
    }
    
    public void removeRepositoryEventListener(RepositoryEventListener listener) {
        repositoryEventListeners.remove(listener);
    }
    
    public Object[] getChildren(Object element) {
        if ( element instanceof Artifact )  {
            return null;
        }
        
        if ( element instanceof BaseRepositoryObject ) {
            BaseRepositoryObject currentNodeObject = (BaseRepositoryObject) element;
            if ( currentNodeObject.isChildrenLoaded() ) {
                return currentNodeObject.getChildren();
            }
            else {
                String baseUrl = currentNodeObject.getRepositoryUrl();
                RepositoryObjectCollectorJob job = new RepositoryObjectCollectorJob(currentNodeObject, baseUrl);
	            job.setListeners(this.repositoryEventListeners);
	            job.schedule(Job.LONG);
                return new String[]{"Pending..."};
            }
        }
        return null;
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
        if ( inputElement instanceof Collection ) {
            Collection repoUrls = (Collection) inputElement;
            
            Repository[] repos = new Repository[repoUrls.size()];
            int i = 0;
            for (Iterator it = repoUrls.iterator(); it.hasNext();) {
                repos[i] = new Repository((String) it.next());
                i++;
            }
            return repos;
        }
        return null;
    }
    

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}

