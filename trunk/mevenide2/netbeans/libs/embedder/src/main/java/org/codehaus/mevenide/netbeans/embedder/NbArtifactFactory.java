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
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 *
 * @author mkleint
 */
public class NbArtifactFactory implements ArtifactFactory, Contextualizable {

    // TODO: remove, it doesn't know the ones from the plugins
    private ArtifactHandlerManager artifactHandlerManager;
    
    private ArtifactFactory original;
    
    /** Creates a new instance of NbArtifactFactory */
    public NbArtifactFactory() {
        original = new DefaultArtifactFactory();
    }

    public Artifact createArtifact(String groupId, String artifactId, String version, String scope, String type) {
        return createFromOriginal(original.createArtifact(groupId, artifactId, version, scope, type));
    }

    public Artifact createArtifactWithClassifier(String groupId, String artifactId, String version, String type, String classifier) {
        return createFromOriginal(original.createArtifactWithClassifier(groupId, artifactId, version, type, classifier));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope) {
        return createFromOriginal(original.createDependencyArtifact(groupId, artifactId, versionRange, type, classifier, scope));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope, boolean optional) {
        return createFromOriginal(original.createDependencyArtifact(groupId, artifactId, versionRange, type, classifier, scope, optional));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope, String inheritedScope) {
        return createFromOriginal(original.createDependencyArtifact(groupId, artifactId, versionRange, type, classifier, scope, inheritedScope));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope, String inheritedScope, boolean optional) {
        return createFromOriginal(original.createDependencyArtifact(groupId, artifactId, versionRange, type, classifier, scope, inheritedScope, optional));
    }

    public Artifact createBuildArtifact(String groupId, String artifactId, String version, String packaging) {
        return createFromOriginal(original.createBuildArtifact(groupId, artifactId, version, packaging));
    }

    public Artifact createProjectArtifact(String groupId, String artifactId, String version) {
        return createFromOriginal(original.createProjectArtifact(groupId, artifactId, version));
    }

    public Artifact createParentArtifact(String groupId, String artifactId, String version) {
        return createFromOriginal(original.createParentArtifact(groupId, artifactId, version));
    }

    public Artifact createPluginArtifact(String groupId, String artifactId, VersionRange versionRange) {
        return createFromOriginal(original.createPluginArtifact(groupId, artifactId, versionRange));
    }

    public Artifact createProjectArtifact(String groupId, String artifactId, String version, String scope) {
        return createFromOriginal(original.createProjectArtifact(groupId, artifactId, version, scope));
    }

    public Artifact createExtensionArtifact(String groupId, String artifactId, VersionRange versionRange) {
        return createFromOriginal(original.createExtensionArtifact(groupId, artifactId, versionRange));
    }

    public void contextualize(Context context) throws ContextException {
        setField("artifactHandlerManager", artifactHandlerManager);
    }
    
    private Artifact createFromOriginal(Artifact orig) {
        if (orig != null) {
            return new NbArtifact(orig);
        }
        return null;
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

    
}