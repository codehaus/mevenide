package org.mevenide.idea.repository;

import org.apache.maven.project.Dependency;
import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public class ArtifactNotFoundException extends Exception {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ArtifactNotFoundException.class);

    private final Throwable[] repoErrors;

    public ArtifactNotFoundException(final Dependency pDependency) {
        this(pDependency, (Throwable) null);
    }

    public ArtifactNotFoundException(final Dependency pDependency,
                                     final String pRepoUrl) {
        this(pDependency, pRepoUrl, (Throwable[]) null);
    }

    public ArtifactNotFoundException(final Dependency pDependency,
                                     final Throwable... pCauses) {
        this(RepositoryUtils.getDependencyRelativePath(pDependency), pCauses);
    }

    public ArtifactNotFoundException(final Dependency pDependency,
                                     final String pRepoUrl,
                                     final Throwable... pCauses) {
        this(RepositoryUtils.getDependencyRelativePath(pDependency), pRepoUrl, pCauses);
    }

    public ArtifactNotFoundException(final String pGroupId,
                                     final String pType,
                                     final String pArtifactId,
                                     final String pVersion,
                                     final String pExtension,
                                     final Throwable... pCauses) {
        this(RepositoryUtils.getDependencyRelativePath(pGroupId,
                                                       pType,
                                                       pArtifactId,
                                                       pVersion,
                                                       pExtension),
             pCauses);
    }

    public ArtifactNotFoundException(final String pGroupId,
                                     final String pType,
                                     final String pArtifactId,
                                     final String pVersion,
                                     final String pExtension,
                                     final String pRepoUrl,
                                     final Throwable... pCauses) {
        this(RepositoryUtils.getDependencyRelativePath(pGroupId,
                                                       pType,
                                                       pArtifactId,
                                                       pVersion,
                                                       pExtension),
             pRepoUrl,
             pCauses);
    }

    public ArtifactNotFoundException(final String pRelativePath,
                                     final String pRepoUrl,
                                     final Throwable... pCauses) {
        super(RES.get("artifact.not.found.in.repo", pRelativePath, pRepoUrl));
        if (pCauses != null && pCauses.length == 1)
            initCause(pCauses[0]);
        repoErrors = pCauses;
    }

    public ArtifactNotFoundException(final String pRelativePath,
                                     final Throwable... pCauses) {
        super(RES.get("artifact.not.found", pRelativePath));
        if (pCauses != null && pCauses.length == 1)
            initCause(pCauses[0]);
        repoErrors = pCauses;
    }


    public final Throwable[] getRepoErrors() {
        return repoErrors;
    }
}
