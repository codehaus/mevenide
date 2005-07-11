package org.mevenide.idea.repository;

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

    public ArtifactNotFoundException(final Artifact pArtifact,
                                     final Throwable... pCauses) {
        this(pArtifact.getRelativePath(false), pCauses);
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
