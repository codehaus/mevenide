package org.mevenide.idea;

/**
 * @author Arik
 */
public class JdkNotDefinedException extends Exception {
    private static final Res RES = Res.getInstance(JdkNotDefinedException.class);
    private static final String MSG_KEY = "jdk.not.defined";
    private static final String MSG = RES.get(MSG_KEY);

    public JdkNotDefinedException() {
        super(MSG);
    }

    public JdkNotDefinedException(final Throwable pCause) {
        super(MSG, pCause);
    }

    public JdkNotDefinedException(final String pMsg) {
        super(pMsg);
    }

    public JdkNotDefinedException(final String pMsg, final Throwable pCause) {
        super(pMsg, pCause);
    }
}
