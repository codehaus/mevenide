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
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.mevenide.ui.eclipse.repository.RepositoryObjectCollector;
import org.mevenide.ui.eclipse.repository.factory.RepositoryObjectCollectorFactory;
import org.mevenide.ui.eclipse.repository.model.BaseRepositoryObject;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryObjectCollectorJob extends Job {

    private static final Log log = LogFactory.getLog(RepositoryObjectCollectorJob.class);
    
    private BaseRepositoryObject repositoryElement;
    
    private List repositoryEventListeners = new ArrayList();
    
    private String baseUrl;
    
    public RepositoryObjectCollectorJob(BaseRepositoryObject repositoryElement, String baseUrl) {
        super("Repository object collector [" + baseUrl + "]");
        this.repositoryElement = repositoryElement;
        this.baseUrl = baseUrl;
    }

    protected IStatus run(IProgressMonitor monitor) {
        IStatus status = null;
        RepositoryObjectCollector collector = RepositoryObjectCollectorFactory.getRepositoryObjectCollector(repositoryElement);
        try {
            BaseRepositoryObject[] objects = collector.collect();
            repositoryElement.setChildren(objects);
            fireRepositoryDataLoaded();
            status = new Status(IStatus.OK, "org.mevenide.ui", 0, "Repository Data Loaded", null);
        }
        catch ( Exception e ) {
            String message = "Unable to collect repository object " + repositoryElement + "(" + repositoryElement.getClass() + ")";
            log.error(message, e);
            status = new Status(IStatus.ERROR, "org.mevenide.ui", 0, message, e);
        }
        return status;
    }
    
    private void fireRepositoryDataLoaded() {
        RepositoryEvent event = new RepositoryEvent(baseUrl, repositoryElement);
        for (int i = 0; i < repositoryEventListeners.size(); i++) {
            ((RepositoryEventListener) repositoryEventListeners.get(i)).dataLoaded(event);
        }
    }

    public void addListener(RepositoryEventListener listener) {
        this.repositoryEventListeners.add(listener);
    }
    
    public void setListeners(List list) {
        if ( list != null ) {
            this.repositoryEventListeners = list;
        }
    }
    
}
