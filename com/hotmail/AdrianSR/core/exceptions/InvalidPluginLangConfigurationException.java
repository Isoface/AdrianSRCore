package com.hotmail.AdrianSR.core.exceptions;

/**
 * Exception thrown when attempting to load an invalid configuration of a
 * language resource of a {@link CustomPlugin}.
 * <p>
 * @author AdrianSR / Date: 14 oct. 2019 / Time: 11:19:08 p. m.
 */
@SuppressWarnings("serial")
public class InvalidPluginLangConfigurationException extends CustomException {

	/**
     * Creates a new instance of {@link InvalidPluginLangConfigurationException} without a
     * message or cause.
     */
    public InvalidPluginLangConfigurationException() {}

	/**
     * Constructs an instance of {@link InvalidPluginLangConfigurationException} with the
     * specified message.
     * <p>
     * @param msg The details of the exception.
     */
    public InvalidPluginLangConfigurationException(String msg) {
        super(msg);
    }

	/**
     * Constructs an instance of {@link InvalidPluginLangConfigurationException} with the
     * specified cause.
     * <p>
     * @param cause The cause of the exception.
     */
    public InvalidPluginLangConfigurationException(Throwable cause) {
        super(cause);
    }

	/**
     * Constructs an instance of {@link InvalidPluginLangConfigurationException} with the
     * specified message and cause.
     * <p>
     * @param cause The cause of the exception.
     * @param msg The details of the exception.
     */
    public InvalidPluginLangConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
