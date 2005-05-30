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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.repository.RepositoryReaderFactory;

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
        
        if ( element instanceof RepoPathElement ) {
            RepoPathElement currentNodeObject = (RepoPathElement) element;
            if (currentNodeObject.isLeaf()) {
                return null;
            }

            if ( currentNodeObject.isLoaded() ) {
                try {
                    return currentNodeObject.getChildren();
                } catch (Exception e) {
                    // FIXME: Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else {
                String baseUrl = currentNodeObject.getURI().toString();
                RepositoryObjectCollectorJob job = new RepositoryObjectCollectorJob(currentNodeObject);
	            job.setListeners(this.repositoryEventListeners);
	            job.schedule(Job.LONG);
                return new String[]{"Pending..."};
            }
        }
        return null;
    }
    
    public Object getParent(Object element) {
        if ( element instanceof RepoPathElement ) {
            return ((RepoPathElement) element).getParent();
        }
        return null;
    }
    
    public boolean hasChildren(Object element) {
        return element instanceof RepoPathElement && !((RepoPathElement)element).isLeaf();
    }
    
    public Object[] getElements(Object inputElement) {
        if ( inputElement instanceof Collection ) {
            Collection repoUrls = (Collection) inputElement;
            
            RepoPathElement[] repos = new RepoPathElement[repoUrls.size()];
            int i = 0;
            for (Iterator it = repoUrls.iterator(); it.hasNext();) {
                IRepositoryReader reader = RepositoryReaderFactory.createRemoteRepositoryReader(URI.create((String) it.next()));
                repos[i] = new RepoPathElement(reader, null);
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

