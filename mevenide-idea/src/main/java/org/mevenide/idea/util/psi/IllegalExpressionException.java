package org.mevenide.idea.util.psi;

/**
 * @author Arik
 */
public class IllegalExpressionException extends RuntimeException {

    public IllegalExpressionException(String message) {
        super(message);
    }

    public IllegalExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
