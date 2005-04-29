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

import java.io.File;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.repository.Repository;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.ui.eclipse.repository.DownloadException;
import org.mevenide.ui.eclipse.repository.RepositoryObjectDownloader;
import org.mevenide.ui.eclipse.repository.model.Artifact;
import org.mevenide.ui.eclipse.repository.model.Group;
import org.mevenide.ui.eclipse.repository.model.Type;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class HttpRepositoryObjectDownloader implements RepositoryObjectDownloader {

    private static final Log log = LogFactory.getLog(HttpRepositoryObjectDownloader.class);
    
    private Wagon wagon;
    
    private String localRepositoryPath;
    
    public HttpRepositoryObjectDownloader() {
        this(null);
    }
    
    public HttpRepositoryObjectDownloader(String localRepositoryPath) {
        this.localRepositoryPath = org.mevenide.util.StringUtils.isNull(localRepositoryPath) ?
            ConfigUtils.getDefaultLocationFinder().getMavenLocalRepository() :
            localRepositoryPath;
        this.localRepositoryPath = StringUtils.stripEnd(this.localRepositoryPath, "/");
        wagon = new LightweightHttpWagon();
    }
    
    public Dependency download(Artifact repositoryObject) throws DownloadException {
        try {
	        return get(repositoryObject);
        }
        catch (TransferFailedException e) {
            String message = "A problem occured during file transfer."; 
            throw new DownloadException(message, e);
        }
        catch (ResourceDoesNotExistException e) {
            String message = "Resource not found on the repository. This seems to be a serious in Mevenide. Please a bug report at http://jira.codehaus.org/browse/MEVENIDE"; 
            throw new DownloadException(message, e);
        }
        catch (ConnectionException e) {
            String message = "Unable to connect to repository " + repositoryObject.getRepositoryUrl(); 
            throw new DownloadException(message, e);
        }
        catch (AuthorizationException e) {
            String message = "Secured repositories not managed yet. It appears that " + repositoryObject.getRepositoryUrl() + " is secured"; 
            throw new DownloadException(message, e);
        }
        catch (AuthenticationException e) {
            String message = "Secured repositories not managed yet. It appears that " + repositoryObject.getRepositoryUrl() + " is secured"; 
            throw new DownloadException(message, e);
        }
        
        finally {
            try {
                if ( wagon != null ) {
                    wagon.disconnect();
                }
            }
            catch (ConnectionException e) {
                String message = "Unable to disconnect wagon"; 
                log.error(message, e);
            }
        }
    }

    private Dependency get(Artifact repositoryObject) throws ConnectionException, 
    												   AuthenticationException, 
    												   TransferFailedException, 
    												   ResourceDoesNotExistException, 
    												   AuthorizationException {
        Repository repository = new Repository();
        repository.setUrl(repositoryObject.getRepositoryUrl());
        
        Type repositoryObjectType = (Type) repositoryObject.getParent();
        Group repositoryObjectGroup = (Group) repositoryObjectType.getParent();
        
        String resource = repositoryObjectGroup + "/" + 
        				  repositoryObjectType + "/" + 
        				  repositoryObject.getName() + "-" + 
        				  repositoryObject.getVersion() + "." +
        				  StringUtils.stripEnd(repositoryObjectType.getName(), "s");

        File destination = new File(localRepositoryPath + "/" + resource);

        Dependency dependency = new Dependency();
        dependency.setArtifactId(repositoryObject.getName());
        dependency.setGroupId(repositoryObjectGroup.getName());
        dependency.setVersion(repositoryObject.getVersion());
        dependency.setType(StringUtils.stripEnd(repositoryObjectType.getName(), "s"));

        if ( !destination.exists() ) {
	        wagon.connect( repository );
	        wagon.get( resource, destination );
    	}
    	
        return dependency;
    }


}
