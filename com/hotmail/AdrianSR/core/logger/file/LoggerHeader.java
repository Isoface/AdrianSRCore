package com.hotmail.AdrianSR.core.logger.file;

/**
 * Represents the maker
 * class that can make
 * the logger header.
 * <p>
 * @author AdrianSR
 */
public class LoggerHeader {

	private static final String  LOGGER_VARIABLE = "$";
	private static final String   LOGGER_HEADERS = "[" + LOGGER_VARIABLE + "] ";
	private static final String LOGGER_HEADERS_F = "[" + LOGGER_VARIABLE + "]";
	
	private String      entry;
	private boolean finishing;
	
	public LoggerHeader(String entry, boolean finishing) {
		this.entry     = entry;
		this.finishing = finishing;
	}
	
	public LoggerHeader(String entry) {
		this.entry = entry;
	}
	
	public boolean isFinishing() {
		return finishing;
	}
	
	public String get() {
		return ( isFinishing() ? LOGGER_HEADERS_F : LOGGER_HEADERS ).replace(LOGGER_VARIABLE, entry);
	}
}