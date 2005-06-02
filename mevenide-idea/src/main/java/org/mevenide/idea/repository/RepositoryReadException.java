package org.mevenide.idea.repository;

import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public class RepositoryReadException extends RuntimeException {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RepositoryReadException.class);

    public RepositoryReadException() {
        super(RES.get("repo.read.error"));
    }

    public RepositoryReadException(final Throwable pCause) {
        super(RES.get("repo.read.error"), pCause);
    }

    public RepositoryReadException(final String pMessage) {
        super(pMessage);
    }

    public RepositoryReadException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
