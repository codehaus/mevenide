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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.DefaultArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.transform.ArtifactTransformationManager;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 *
 * @author mkleint
 */
public class NbArtifactResolver extends AbstractLogEnabled implements ArtifactResolver, Contextualizable {    
    
    private WagonManager wagonManager;

    private ArtifactTransformationManager transformationManager;

    protected ArtifactFactory artifactFactory;

    private ArtifactCollector artifactCollector;

    private DefaultArtifactResolver original;
    
    private ResolutionListener listener;
    
    /** Creates a new instance of NbWagonManager */
    public NbArtifactResolver() {
        original = new DefaultArtifactResolver();
    }

    public void enableLogging(Logger logger ) {
        super.enableLogging(logger);
        original.enableLogging(logger);
    }

    private void setField(String name, Object value) {
        try {
            Field fld = original.getClass().getDeclaredField(name);
            fld.setAccessible(true);
            fld.set(original, value);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public void resolve(Artifact artifact, List list, ArtifactRepository artifactRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
        original.resolve(artifact, list, artifactRepository);
    }

    public ArtifactResolutionResult resolveTransitively(
            Set set, Artifact artifact, 
            List list, ArtifactRepository artifactRepository, ArtifactMetadataSource artifactMetadataSource) throws ArtifactResolutionException, ArtifactNotFoundException {
//        System.out.println("resolve trans1=" + artifact);
        return resolveTransitively(set, artifact, list, artifactRepository, artifactMetadataSource, null);
    }

    public ArtifactResolutionResult resolveTransitively(
            Set set, Artifact artifact, 
            List list, ArtifactRepository artifactRepository, 
            ArtifactMetadataSource artifactMetadataSource, List list0) throws ArtifactResolutionException, ArtifactNotFoundException {
//        System.out.println("resolve trans2=" + artifact);
        return resolveTransitively(set, artifact, Collections.EMPTY_MAP, artifactRepository, list, artifactMetadataSource, null, list0);
    }

    public ArtifactResolutionResult resolveTransitively( Set artifacts, Artifact originatingArtifact,
                                                         ArtifactRepository localRepository, List remoteRepositories,
                                                         ArtifactMetadataSource source, ArtifactFilter filter )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        return resolveTransitively( artifacts, originatingArtifact, Collections.EMPTY_MAP, localRepository,
                                    remoteRepositories, source, filter );

    }

    public ArtifactResolutionResult resolveTransitively( Set artifacts, Artifact originatingArtifact,
                                                         Map managedVersions, ArtifactRepository localRepository,
                                                         List remoteRepositories, ArtifactMetadataSource source )
        throws ArtifactResolutionException, ArtifactNotFoundException
    {
        return resolveTransitively( artifacts, originatingArtifact, managedVersions, localRepository,
                                    remoteRepositories, source, null );
    }

    public ArtifactResolutionResult resolveTransitively(
            Set set, Artifact artifact, 
            Map map, ArtifactRepository artifactRepository, 
            List list, ArtifactMetadataSource artifactMetadataSource, 
            ArtifactFilter artifactFilter) throws ArtifactResolutionException, ArtifactNotFoundException 
    {
        return resolveTransitively(set, artifact, map, artifactRepository, list, artifactMetadataSource, artifactFilter, null);
    }

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
        return original.resolveTransitively(set, artifact, map, artifactRepository, list, artifactMetadataSource, artifactFilter, newListeners);
    }

    public void resolveAlways(Artifact artifact, List list, ArtifactRepository artifactRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
//        System.out.println("resolve always=" + artifact);
        original.resolveAlways(artifact, list, artifactRepository);
    }

    public void contextualize(Context context) throws ContextException {
        setField("wagonManager", wagonManager);
        setField("artifactFactory", artifactFactory);
        setField("artifactCollector", artifactCollector);
        setField("transformationManager", transformationManager);
    }

    
}
