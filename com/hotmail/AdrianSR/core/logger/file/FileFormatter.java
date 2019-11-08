package com.hotmail.AdrianSR.core.logger.file;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

/**
 * {@code FileFormatter} objects are used to format {@link LogRecord} objects into a
 * string representation. Head and tail strings are sometimes used to wrap a set
 * of records. The {@code getHead} and {@code getTail} methods are used for this
 * purpose.
 * <p>
 * @author AdrianSR
 */
public class FileFormatter extends Formatter {
	
	private static final boolean APPEND_SOURCES = false;
	
	/**
	 * Logger context.
	 */
	private final CustomPlugin context;

	/**
	 * Construct hidden loggers formatter.
	 * <p>
	 * @param context logger context.
	 */
	public FileFormatter(CustomPlugin context) {
		this.context = context;
	}

	@Override
	public String format(LogRecord record) {
		final StringBuilder builder = new StringBuilder();
		
		/* append headers */
		builder.append(new LoggerHeader(
				MessageFormat.format("{0, time}", 
				new Object[] { new Date(record.getMillis()) })).get());      /* time header */
		builder.append(new LoggerHeader(record.getLevel().getName()).get()); /* level header */
		builder.append(new LoggerHeader(
				context.getDescription().getPrefix() != null ? 
				context.getDescription().getPrefix() : 
				context.getDescription().getName(), true).get()).append(": "); /* plugin header */
		
		/* append message, resource bundle and parameters */
		builder.append(formatMessage(record));
		
		/* append sources */
		if (APPEND_SOURCES) {
			if (record.getSourceClassName() != null) { /* source class name, will not be append if null */
				builder.append(record.getSourceClassName()).append(" ");
			} else {
				builder.append(" ");
			}
			
			builder.append(
					record.getSourceMethodName() != null ? 
					record.getSourceMethodName() : "").append(System.lineSeparator()); /* source method name */
		} else {
			builder.append(System.lineSeparator());
		}
		
		/* append throwable */
		if (record.getThrown() != null) {
			Throwable  throwable = record.getThrown();
			PrintWriter  pwriter = null;
			try {
				StringWriter swriter = new StringWriter();
				pwriter              = new PrintWriter(swriter);
				throwable.printStackTrace(pwriter);
				builder.append(swriter.toString());
			} finally {
				closeQuietly(pwriter);
			}
		}
		return builder.toString();
	}
	
	/**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if 'closeable' is null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            	/* ignore */
            }
        }
    }
}