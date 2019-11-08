package com.hotmail.AdrianSR.core.exceptions;

/**
 * Parent for all custom plugin exceptions.
 */
public class CustomException extends Exception {

	private static final long serialVersionUID = 965352807283652943L;

	/**
     * Create a new exception.
     */
    protected CustomException() {
    	// nothing.
    }

    /**
     * Create a new exception with a message.
     *
     * @param message the message
     */
    protected CustomException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and a cause.
     *
     * @param message the message
     * @param cause the cause
     */
    protected CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new exception with a cause.
     *
     * @param cause the cause
     */
    protected CustomException(Throwable cause) {
        super(cause);
    }
}
