/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.embedder;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.manager.DefaultWagonManager;
import org.apache.maven.artifact.manager.WagonConfigurationException;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.repository.RepositoryPermissions;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 *
 * @author mkleint
 */
public class NbWagonManager extends AbstractLogEnabled implements WagonManager, Contextualizable {
    
    private ArtifactRepositoryFactory repositoryFactory;
    
    private DefaultWagonManager original;
    
    private List letGoes = new ArrayList();
    
    /** Creates a new instance of NbWagonManager */
    public NbWagonManager() {
        original = new DefaultWagonManager();
    }

    public Wagon getWagon(String protocol) throws UnsupportedProtocolException {
        return original.getWagon(protocol);
    }
    //MEVENIDE-422
    public void letGoThrough(Artifact artifact) {
        synchronized (letGoes) {
            letGoes.add(artifact);
        }
    }
    
    //MEVENIDE-422
    public void cleanLetGone(Artifact artifact) {
        synchronized (letGoes) {
            letGoes.remove(artifact);
        }
    }

    public void getArtifact(Artifact artifact, List remoteRepositories) throws TransferFailedException, ResourceDoesNotExistException {
//        System.out.println("getArtifact1 =" + artifact);
        boolean cont;
        synchronized (letGoes) {
            cont = letGoes.contains(artifact);
        }
        if (cont) {
//            System.out.println("downloading=" + artifact);
            try {
                original.getArtifact(artifact, remoteRepositories);
            } catch (TransferFailedException exc) {
                //ignore, we will just pretend it didn't happen.
                artifact.setResolved(true);
            } catch (ResourceDoesNotExistException exc) {
                //ignore, we will just pretend it didn't happen.
                artifact.setResolved(true);
            }
            synchronized (letGoes) {
                letGoes.remove(artifact);
            }
        } else {
            artifact.setResolved(true);
        }
    }

    public void getArtifact(Artifact artifact, ArtifactRepository repository) throws TransferFailedException, ResourceDoesNotExistException {
  
//        System.out.println("getArtifact2=" + artifact);
        artifact.setResolved(true);
//        original.getArtifact(artifact, repository);
    }

    public void putArtifact(File source, Artifact artifact, ArtifactRepository deploymentRepository) throws TransferFailedException {
//        System.out.println("putArtifact=" + source);
//        original.putArtifact(source, artifact, deploymentRepository);
    }

    public void putArtifactMetadata(File source, ArtifactMetadata artifactMetadata, ArtifactRepository repository) throws TransferFailedException {
//        System.out.println("putArtifact metadata=" + source);
//        original.putArtifactMetadata(source, artifactMetadata, repository);
    }

    public void getArtifactMetadata(ArtifactMetadata metadata, ArtifactRepository remoteRepository, File destination, String checksumPolicy) throws TransferFailedException, ResourceDoesNotExistException {
//        System.out.println("getartifact metadata=" + metadata);
//        original.getArtifactMetadata(metadata, remoteRepository, destination, checksumPolicy);
    }

    public void setOnline(boolean online) {
        original.setOnline(online);
    }

    public boolean isOnline() {
        return original.isOnline();
    }

    public void addProxy(String protocol, String host, int port, String username, String password, String nonProxyHosts) {
        original.addProxy(protocol, host, port, username, password, nonProxyHosts);
    }

    public void addAuthenticationInfo(String repositoryId, String username, String password, String privateKey, String passphrase) {
        original.addAuthenticationInfo(repositoryId, username, password, privateKey, passphrase);
    }

    public void addMirror(String id, String mirrorOf, String url) {
        original.addMirror(id, mirrorOf, url);
    }

    public void setDownloadMonitor(TransferListener downloadMonitor) {
        original.setDownloadMonitor(downloadMonitor);
    }

    public void addPermissionInfo(String repositoryId, String filePermissions, String directoryPermissions) {
        original.addPermissionInfo(repositoryId, filePermissions, directoryPermissions);
    }

    public ProxyInfo getProxy(String protocol) {
        return original.getProxy(protocol);
    }

    public AuthenticationInfo getAuthenticationInfo(String id) {
        return original.getAuthenticationInfo(id);
    }

    public void addConfiguration(String repositoryId, Xpp3Dom configuration) {
        original.addConfiguration(repositoryId, configuration);
    }

    public void setInteractive(boolean interactive) {
        original.setInteractive(interactive);
    }

    public void contextualize(Context context) throws ContextException {
        try {
            Field fld = original.getClass().getDeclaredField("repositoryFactory");
            fld.setAccessible(true);
            fld.set(original, repositoryFactory);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        original.contextualize(context);
    }
    
    public void enableLogging(Logger logger ) {
        super.enableLogging(logger);
        original.enableLogging(logger);
    }

    public Wagon getWagon(Repository repository) throws UnsupportedProtocolException, WagonConfigurationException {
        return original.getWagon(repository);
    }

    public void registerWagons(Collection arg0, PlexusContainer arg1) {
        original.registerWagons(arg0, arg1);
    }

    public void findAndRegisterWagons(PlexusContainer arg0) {
        original.findAndRegisterWagons(arg0);
    }

    public void setDefaultRepositoryPermissions(RepositoryPermissions arg0) {
        original.setDefaultRepositoryPermissions(arg0);
    }
    
}
