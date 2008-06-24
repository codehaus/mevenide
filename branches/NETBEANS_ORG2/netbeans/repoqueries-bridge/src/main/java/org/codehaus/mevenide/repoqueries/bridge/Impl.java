/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.repoqueries.bridge;

import java.io.File;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.UnknownRepositoryLayoutException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.CredentialsDataSourceException;
import org.apache.maven.artifact.manager.WagonConfigurationException;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryQueries;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.netbeans.modules.repoqueries.spi.RepositoryQueryImplementation;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public class Impl implements RepositoryQueryImplementation {

    public Result findInRepository(String shaChecksum) {
        List<NBVersionInfo> infos = RepositoryQueries.findBySHA1(shaChecksum);
        if (infos != null && infos.size() > 0) {
            return new Res(infos.get(0));
        }
        return null;
    }

    private class Res implements RepositoryQueryImplementation.Result {

        private NBVersionInfo info;

        private Res(NBVersionInfo info) {
            this.info = info;
        }

        public boolean hasJavadoc() {
            return info.isJavadocExists();
        }

        public boolean hasSource() {
            return info.isSourcesExists();
        }

        public boolean downloadArtifact(File newLocation) {
            try {
                MavenEmbedder emb = EmbedderFactory.createOnlineEmbedder();
                ArtifactFactory af = (ArtifactFactory) emb.getPlexusContainer().lookup(ArtifactFactory.ROLE);
                Artifact art = af.createArtifactWithClassifier(info.getGroupId(), info.getArtifactId(), info.getVersion(), info.getType(), null);
                return downloadArtifact(art, emb, newLocation);
            } catch (ComponentLookupException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }

        public boolean downloadJavadoc(File newLocation) {
            try {
                MavenEmbedder emb = EmbedderFactory.createOnlineEmbedder();
                ArtifactFactory af = (ArtifactFactory) emb.getPlexusContainer().lookup(ArtifactFactory.ROLE);
                Artifact art = af.createArtifactWithClassifier(info.getGroupId(), info.getArtifactId(), info.getVersion(), info.getType(), "javadoc");
                return downloadArtifact(art, emb, newLocation);
            } catch (ComponentLookupException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }
        public boolean downloadSources(File newLocation) {
            try {
                MavenEmbedder emb = EmbedderFactory.createOnlineEmbedder();
                ArtifactFactory af = (ArtifactFactory) emb.getPlexusContainer().lookup(ArtifactFactory.ROLE);
                Artifact art = af.createArtifactWithClassifier(info.getGroupId(), info.getArtifactId(), info.getVersion(), info.getType(), "sources");
                return downloadArtifact(art, emb, newLocation);
            } catch (ComponentLookupException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }

        public boolean downloadArtifact(Artifact art, MavenEmbedder emb, File newLocation) throws ComponentLookupException {
            try {
                WagonManager wagon = (WagonManager) emb.getPlexusContainer().lookup(WagonManager.ROLE);
                System.out.println("art=" + art.getId());
                RepositoryInfo repoinfo = RepositoryPreferences.getInstance().getRepositoryInfoById(info.getRepoId());
                ArtifactRepositoryFactory arf = (ArtifactRepositoryFactory) emb.getPlexusContainer().lookup(ArtifactRepositoryFactory.ROLE);
                ArtifactRepository artRepo = arf.createArtifactRepository(repoinfo.getId(), repoinfo.getRepositoryUrl(), arf.getLayout(arf.DEFAULT_LAYOUT_ID), null, null);
                System.out.println("path=" + artRepo.pathOf(art));
                Repository repository = new Repository(repoinfo.getId(), repoinfo.getRepositoryUrl());
                Wagon wag = wagon.getWagon(repository);
                if (wag != null) {
                    try {
                        wag.connect(repository, wagon.getAuthenticationInfo(repoinfo.getId()));
                        wag.get(artRepo.pathOf(art), newLocation);
                        return true;
                    } catch (ConnectionException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (AuthenticationException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (CredentialsDataSourceException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (TransferFailedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ResourceDoesNotExistException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (AuthorizationException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        try {
                            wag.disconnect();
                        } catch (ConnectionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            } catch (UnsupportedProtocolException ex) {
                Exceptions.printStackTrace(ex);
            } catch (WagonConfigurationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (UnknownRepositoryLayoutException ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }

        public String getMavenId() {
            return info.getGroupId() + ":" + info.getArtifactId() + ":" + info.getVersion() + ":" + info.getClassifier() + ":" + info.getType();
        }
    }
}
