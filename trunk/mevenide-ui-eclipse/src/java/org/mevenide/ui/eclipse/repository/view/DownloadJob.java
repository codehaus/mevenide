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
import org.mevenide.repository.RepoPathElement;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.repository.DownloadException;
import org.mevenide.ui.eclipse.repository.RepositoryObjectDownloader;
import org.mevenide.ui.eclipse.repository.factory.RepositoryObjectDownloaderFactory;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DownloadJob extends Job {
    
    private static final Log log = LogFactory.getLog(DownloadJob.class);
    
    private List downloadList ;
    
    private RepositoryObjectDownloader downloader;

    
    public DownloadJob(List downloadList) {
        super("Downloading artifact");
        this.downloadList = downloadList;
        this.downloader = RepositoryObjectDownloaderFactory.getDownloader(RepositoryObjectDownloader.HTTP, 
                                                                          Mevenide.getInstance().getMavenRepository());
    }

    
    protected IStatus run(IProgressMonitor monitor) {
        IStatus status = null;
        List failedDownloads = new ArrayList();
        for (int i = 0; i < downloadList.size(); i++) {
            RepoPathElement artifact = (RepoPathElement) downloadList.get(i);
            String artifactName = artifact.getArtifactId() + "-" + artifact.getVersion(); 
            try {
                downloader.download(artifact);
            }
            catch (DownloadException e) {
                String message = "Unable to download " + artifactName;
                log.error(message, e);
                failedDownloads.add(artifactName);
            }
        }
        
        if ( failedDownloads.size() == 0 ) {
            status = new Status(IStatus.OK, "org.mevenide.ui", 0, "download completed", null);
        }
        else {
            String statusMessage = "The following artifacts couldnot be downloaded : ";
            for (int i = 0; i < failedDownloads.size(); i++) {
                String failedDownload = (String) failedDownloads.get(i);
                statusMessage += "\n" + failedDownload;
            }
            status = new Status(IStatus.ERROR, "org.mevenide.ui", 1, statusMessage, null);
        }
        return status;
    }
}
