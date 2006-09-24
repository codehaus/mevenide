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
import java.util.Collections;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
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
        return createFromOriginal(original.createArtifact(checkValue(groupId), checkValue(artifactId), checkVersion(version), scope, type));
    }

    public Artifact createArtifactWithClassifier(String groupId, String artifactId, String version, String type, String classifier) {
        return createFromOriginal(original.createArtifactWithClassifier(checkValue(groupId), checkValue(artifactId), checkVersion(version), type, classifier));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope) {
        return createFromOriginal(original.createDependencyArtifact(checkValue(groupId), checkValue(artifactId), checkVersionRange(versionRange), type, classifier, scope));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope, boolean optional) {
        return createFromOriginal(original.createDependencyArtifact(checkValue(groupId), checkValue(artifactId), checkVersionRange(versionRange), type, classifier, scope, optional));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope, String inheritedScope) {
        return createFromOriginal(original.createDependencyArtifact(checkValue(groupId), checkValue(artifactId), checkVersionRange(versionRange), type, classifier, scope, inheritedScope));
    }

    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type, String classifier, String scope, String inheritedScope, boolean optional) {
        return createFromOriginal(original.createDependencyArtifact(checkValue(groupId), checkValue(artifactId), checkVersionRange(versionRange), type, classifier, scope, inheritedScope, optional));
    }

    public Artifact createBuildArtifact(String groupId, String artifactId, String version, String packaging) {
        return createFromOriginal(original.createBuildArtifact(checkValue(groupId), checkValue(artifactId), checkVersion(version), packaging));
    }

    public Artifact createProjectArtifact(String groupId, String artifactId, String version) {
        return createFromOriginal(original.createProjectArtifact(checkValue(groupId), checkValue(artifactId), checkVersion(version)));
    }

    public Artifact createParentArtifact(String groupId, String artifactId, String version) {
        return createFromOriginal(original.createParentArtifact(checkValue(groupId), checkValue(artifactId), checkVersion(version)));
    }

    public Artifact createPluginArtifact(String groupId, String artifactId, VersionRange versionRange) {
        return createFromOriginal(original.createPluginArtifact(checkValue(groupId), checkValue(artifactId), checkVersionRange(versionRange)));
    }

    public Artifact createProjectArtifact(String groupId, String artifactId, String version, String scope) {
        return createFromOriginal(original.createProjectArtifact(checkValue(groupId), checkValue(artifactId), checkVersion(version), scope));
    }

    public Artifact createExtensionArtifact(String groupId, String artifactId, VersionRange versionRange) {
        return createFromOriginal(original.createExtensionArtifact(checkValue(groupId), checkValue(artifactId), checkVersionRange(versionRange)));
    }

    public void contextualize(Context context) throws ContextException {
        setField("artifactHandlerManager", artifactHandlerManager);
    }
    
    // this method bypasses DefaultArtifact's validateIdentity() method
    private String checkValue(String in) {
        if (in == null || in.trim().length() == 0) {
            return "error";
        }
        return in;
    }
    
    // this method bypasses DefaultArtifact's validateIdentity() method
    private String checkVersion(String value) {
        if (value == null) {
            return "unknown";
        }
        return value;
    }
    
    // this method bypasses DefaultArtifact's validateIdentity() method
    private VersionRange checkVersionRange(VersionRange range) {
        if (range == null) {
            return VersionRange.createFromVersion("unknown");
        }
        return range;
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
