package com.hotmail.AdrianSR.core.logger.combo;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

/**
 * Represents the Logger
 * that print and write
 * to a file any log.
 * <p>
 * @author AdrianSR
 */
public class ComboLogger extends Logger {

	/**
	 * Logger context.
	 */
	private  CustomPlugin context;
	protected String  plugin_name;
	
	/**
	 * Construct logger.
	 * <p>
	 * @param context
	 */
	public ComboLogger(CustomPlugin context) {
		super(context.getClass().getCanonicalName(), null);
		this.context     = context;
		this.plugin_name = context.getDescription().getPrefix() != null
				? new StringBuilder().append("[").append(context.getDescription().getPrefix()).append("] ").toString()
				: "[" + context.getDescription().getName() + "] ";
		setParent(context.getServer().getLogger());
		setLevel(Level.ALL);
	}
	
	@Override
	public void log(LogRecord record) {
		context.getFileLogger().log(record); // log to file
		
		/* log normally */
		record.setMessage(plugin_name + record.getMessage()); // inserts the plugin name to the message
		super.log(record);                   
	}
}