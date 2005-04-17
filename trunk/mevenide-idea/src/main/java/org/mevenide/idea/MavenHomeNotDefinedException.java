package org.mevenide.idea;

import org.mevenide.idea.util.Res;

/**
 * @author Arik
 */
public class MavenHomeNotDefinedException extends Exception {
    private static final Res RES = Res.getInstance(MavenHomeNotDefinedException.class);
    private static final String KEY = "maven.home.not.defined";
    private static final String MSG = RES.get(KEY);

    public MavenHomeNotDefinedException() {
        super(MSG);
    }

    public MavenHomeNotDefinedException(final Throwable pCause) {
        super(MSG, pCause);
    }

    public MavenHomeNotDefinedException(final String pMsg) {
        super(pMsg);
    }

    public MavenHomeNotDefinedException(final String pMsg, final Throwable pCause) {
        super(pMsg, pCause);
    }
}
