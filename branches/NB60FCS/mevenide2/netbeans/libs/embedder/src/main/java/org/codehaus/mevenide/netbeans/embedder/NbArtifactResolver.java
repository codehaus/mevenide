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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.DefaultArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

/**
 *
 * @author mkleint
 */
public class NbArtifactResolver extends DefaultArtifactResolver {    
    
    private ResolutionListener listener;
    protected Field wagonMan;
    
    /** Creates a new instance of NbWagonManager */
    public NbArtifactResolver() {
        super();
        try {
            wagonMan = DefaultArtifactResolver.class.getDeclaredField("wagonManager");
            wagonMan.setAccessible(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public void resolve(Artifact artifact, List list, ArtifactRepository artifactRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
//        artifact.setResolved(true);
        //MEVENIDE-422 
        if (artifact.getScope() == null && "pom".equals(artifact.getType())) {
            //the condition is meant to mean.. "if we look for parent pom", not sure it's close enough..
            try {
                letArtifactGo(artifact);
                super.resolve(artifact, list, artifactRepository);
            } catch (ArtifactResolutionException exc) {
                if (exc.getCause() instanceof IOException) {
                    // DefaultArtifactResolver:193 when having snapshots something gets copied and fails
                    // when the wagon manager just pretends to download something..
                    System.out.println("exc=" + exc.getCause().getMessage());
                    return;
                }
                throw exc;
            }
            finally {
                cleanLetGone(artifact);
            }
        } else {
            super.resolve(artifact, list, artifactRepository);
        }
    }
    
    @Override
    public void resolveAlways(Artifact artifact, List list, ArtifactRepository artifactRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
        if (artifact.getScope() == null && "pom".equals(artifact.getType())) {
            //the condition is meant to mean.. "if we look for parent pom", not sure it's close enough..
            try {
                letArtifactGo(artifact);
                super.resolveAlways(artifact, list, artifactRepository);
            } catch (ArtifactResolutionException exc) {
                if (exc.getCause() instanceof IOException) {
                    // DefaultArtifactResolver:193 when having snapshots something gets copied and fails
                    // when the wagon manager just pretends to download something..
                    System.out.println("exc=" + exc.getCause().getMessage());
                    return;
                }
                throw exc;
            } finally {
                cleanLetGone(artifact);
            }
        } else {
            super.resolveAlways(artifact, list, artifactRepository);
            
        }
    }

    @Override
    public ArtifactResolutionResult resolveTransitively(
            Set set, Artifact artifact, 
            List list, ArtifactRepository artifactRepository, ArtifactMetadataSource artifactMetadataSource) throws ArtifactResolutionException, ArtifactNotFoundException {
//        System.out.println("resolve trans1=" + artifact);
        return resolveTransitively(set, artifact, list, artifactRepository, artifactMetadataSource, null);
    }

    @Override
    public ArtifactResolutionResult resolveTransitively(
            Set set, Artifact artifact, 
            List list, ArtifactRepository artifactRepository, 
            ArtifactMetadataSource artifactMetadataSource, List list0) throws ArtifactResolutionException, ArtifactNotFoundException {
//        System.out.println("resolve trans2=" + artifact);
        return resolveTransitively(set, artifact, Collections.EMPTY_MAP, artifactRepository, list, artifactMetadataSource, null, list0);
    }

    @Override
    public ArtifactResolutionResult resolveTransitively( Set artifacts, Artifact originatingArtifact,
                                                         ArtifactRepository localRepository, List remoteRepositories,
                                                         ArtifactMetadataSource source, ArtifactFilter filter )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        return resolveTransitively( artifacts, originatingArtifact, Collections.EMPTY_MAP, localRepository,
                                    remoteRepositories, source, filter );

    }

    @Override
    public ArtifactResolutionResult resolveTransitively( Set artifacts, Artifact originatingArtifact,
                                                         Map managedVersions, ArtifactRepository localRepository,
                                                         List remoteRepositories, ArtifactMetadataSource source )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        return resolveTransitively( artifacts, originatingArtifact, managedVersions, localRepository,
                                    remoteRepositories, source, null );
    }

    @Override
    public ArtifactResolutionResult resolveTransitively(
            Set set, Artifact artifact, 
            Map map, ArtifactRepository artifactRepository, 
            List list, ArtifactMetadataSource artifactMetadataSource, 
            ArtifactFilter artifactFilter) throws ArtifactResolutionException, ArtifactNotFoundException 
    {
        return resolveTransitively(set, artifact, map, artifactRepository, list, artifactMetadataSource, artifactFilter, null);
    }

    @Override
    public ArtifactResolutionResult resolveTransitively(
                   Set set, Artifact artifact, 
                   Map map, ArtifactRepository artifactRepository, 
                   List list, ArtifactMetadataSource artifactMetadataSource, 
                   ArtifactFilter artifactFilter, List listeners) throws ArtifactResolutionException, ArtifactNotFoundException {
//        System.out.println("resolve trans6=" + artifact);
        ArrayList newListeners = new ArrayList();
        if (listeners != null) {
            newListeners.addAll(listeners);
        }
        if (listener != null) {
            newListeners.add(listener);
        }
        return super.resolveTransitively(set, artifact, map, artifactRepository, list, artifactMetadataSource, artifactFilter, newListeners);
    }

    private void cleanLetGone(Artifact artifact) {
        if (wagonMan != null) {
            try {
                Object manObj = wagonMan.get(this);
                if (manObj instanceof NbWagonManager) {
                    NbWagonManager manager = (NbWagonManager)manObj;
                    manager.cleanLetGone(artifact);
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void letArtifactGo(Artifact artifact) {
        if (wagonMan != null) {
            try {
                Object manObj = wagonMan.get(this);
                if (manObj instanceof NbWagonManager) {
                    NbWagonManager manager = (NbWagonManager)manObj;
                    manager.letGoThrough(artifact);
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
