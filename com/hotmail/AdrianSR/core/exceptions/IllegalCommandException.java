package com.hotmail.AdrianSR.core.exceptions;

public class IllegalCommandException extends CustomException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3304206723197924977L;

	/**
	 * @param msg message to send.
	 */
	public IllegalCommandException(String msg) {
        super(msg);
    }
}
