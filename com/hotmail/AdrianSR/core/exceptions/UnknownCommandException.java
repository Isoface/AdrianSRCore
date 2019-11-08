package com.hotmail.AdrianSR.core.exceptions;

public class UnknownCommandException extends CustomException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5483814663170122073L;

	/**
	 * @param msg message to send.
	 */
	public UnknownCommandException(String msg) {
        super(msg);
    }
}
