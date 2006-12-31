package org.mevenide.idea.common;

import org.mevenide.idea.common.util.Res;

/**
 * @author Arik
 */
public class PomNotDefinedException extends Exception {
    private static final Res RES = Res.getInstance(PomNotDefinedException.class);
    private static final String MSG_KEY = "pom.not.defined";
    private static final String MSG = RES.get(MSG_KEY);

    public PomNotDefinedException() {
        super(MSG);
    }

    public PomNotDefinedException(final Throwable pCause) {
        super(MSG, pCause);
    }

    public PomNotDefinedException(final String pMsg) {
        super(pMsg);
    }

    public PomNotDefinedException(final String pMsg, final Throwable pCause) {
        super(pMsg, pCause);
    }
}
