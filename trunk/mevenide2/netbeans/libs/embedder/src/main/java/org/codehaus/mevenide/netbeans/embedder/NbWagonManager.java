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
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.manager.DefaultWagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;

/**
 *
 * @author mkleint
 */
public class NbWagonManager extends DefaultWagonManager {

    private List<Artifact> letGoes = new ArrayList<Artifact>();

    /** Creates a new instance of NbWagonManager */
    public NbWagonManager() {
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

    @Override
    public void getArtifact(Artifact artifact, List remoteRepositories) throws TransferFailedException, ResourceDoesNotExistException {
//        System.out.println("getArtifact1 =" + artifact);
        boolean cont;
        synchronized (letGoes) {
            cont = letGoes.contains(artifact);
        }
        if (cont) {
//            System.out.println("downloading=" + artifact);
            try {
                super.getArtifact(artifact, remoteRepositories);
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

    @Override
    public void getArtifact(Artifact artifact, ArtifactRepository repository) throws TransferFailedException, ResourceDoesNotExistException {

//        System.out.println("getArtifact2=" + artifact);
        artifact.setResolved(true);
//        original.getArtifact(artifact, repository);
    }

    @Override
    public void putArtifact(File source, Artifact artifact, ArtifactRepository deploymentRepository) throws TransferFailedException {
//        System.out.println("putArtifact=" + source);
//        original.putArtifact(source, artifact, deploymentRepository);
    }

    @Override
    public void putArtifactMetadata(File source, ArtifactMetadata artifactMetadata, ArtifactRepository repository) throws TransferFailedException {
//        System.out.println("putArtifact metadata=" + source);
//        original.putArtifactMetadata(source, artifactMetadata, repository);
    }

    @Override
    public void getArtifactMetadata(ArtifactMetadata metadata, ArtifactRepository remoteRepository, File destination, String checksumPolicy) throws TransferFailedException, ResourceDoesNotExistException {
//        System.out.println("getartifact metadata=" + metadata);
//        original.getArtifactMetadata(metadata, remoteRepository, destination, checksumPolicy);
    }

    @Override
    public void getArtifact(Artifact artifact,
            ArtifactRepository repository,
            boolean forceUpdateCheck) throws TransferFailedException, ResourceDoesNotExistException 
    {
        this.getArtifact(artifact, repository);
    }

    @Override
    public void getArtifact(Artifact artifact,
            List remoteRepositories,
            boolean forceUpdateCheck)
            throws TransferFailedException, ResourceDoesNotExistException 
    {
        boolean cont;
        synchronized (letGoes) {
            cont = letGoes.contains(artifact);
        }
        if (cont) {
            try {
                super.getArtifact(artifact, remoteRepositories, forceUpdateCheck);
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
}
