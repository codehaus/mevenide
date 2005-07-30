package org.mevenide.idea.global;

/**
 * @author Arik
 */
public class IllegalMavenHomeException extends Exception {
    public IllegalMavenHomeException(final String pMsg) {
        super(pMsg);
    }

    public IllegalMavenHomeException(final String pMsg, final Throwable pCause) {
        super(pMsg, pCause);
    }

    public IllegalMavenHomeException(final Throwable pCause) {
        super(pCause.getMessage(), pCause);
    }
}
