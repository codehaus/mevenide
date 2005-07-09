package org.mevenide.idea.synchronize;

import com.intellij.openapi.project.Project;

/**
 * @author Arik Kfir
 */
public abstract class AbstractArtifactProblemInfo extends AbstractProblemInfo implements ArtifactProblemInfo {
    protected final String groupId;
    protected final String artifactId;
    protected final String type;
    protected final String version;
    protected final String extension;

    protected AbstractArtifactProblemInfo(final ProblemInspector pInspector,
                                          final Project pProject,
                                          final String pPomUrl,
                                          final String pGroupId,
                                          final String pArtifactId,
                                          final String pType,
                                          final String pVersion,
                                          final String pExtension) {
        super(pInspector, pProject, pPomUrl);
        groupId = pGroupId;
        artifactId = pArtifactId;
        type = pType;
        version = pVersion;
        extension = pExtension;
    }

    protected AbstractArtifactProblemInfo(final ProblemInspector pInspector,
                                          final Project pProject,
                                          final String pPomUrl,
                                          final String pDescription,
                                          final String pGroupId,
                                          final String pArtifactId,
                                          final String pType,
                                          final String pVersion,
                                          final String pExtension) {
        super(pInspector, pProject, pPomUrl, pDescription);
        groupId = pGroupId;
        artifactId = pArtifactId;
        type = pType;
        version = pVersion;
        extension = pExtension;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getExtension() {
        return extension;
    }
}
