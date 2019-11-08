package com.hotmail.AdrianSR.core.logger.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.hotmail.AdrianSR.core.main.CustomPlugin;

/**
 * Represents the custom plugins
 * loggers that allow developers
 * to make loggers that logs to a file 
 * (logs inside a log file without loggin in console).
 * <p>
 * @author AdrianSR
 */
public class FileLogger {
	
	private final CustomPlugin context;
	private final List<String>    logs;
	private       int      wrote_times;
	
	/**
	 * Construct file logger.
	 * <p>
	 * @param context the {@link CustomPlugin} will
	 * use this logger.
	 */
	public FileLogger(CustomPlugin context) {
		this.context     = context;
		this.logs        = new ArrayList<String>();
		this.wrote_times = 0;
	}

	/**
	 * @return logger context.
	 */
	public final CustomPlugin getContext() {
		return context;
	}
	
	/**
	 * The {@link Logger} of the context.
	 * <p>
	 * @return context logger.
	 */
	protected Logger getContextLogger() {
		return context.getLogger();
	}
	
	/**
	 * The folder that have
	 * the logs files.
	 * <p>
	 * @return logs folder.
	 */
	public File getLogsFolder() {
		return new File(context.getDataFolder(), "logs");
	}
	
	/**
	 * The current log file in which 
	 * this logger are writting.
	 * <p>
	 * @return current log file.
	 */
	public File getLatestLogFile() {
		return new File(getLogsFolder(), "latest.log");
	}
	
	/**
	 * The total times
	 * this logger wrote to the file.
	 * <p>
	 * @return wrote times.
	 */
	public int getWroteTimes() {
		return wrote_times;
	}
	
    /**
     * Logs a message of level {@code Level.SEVERE}; the message is transmitted
     * to all subscribed handlers.
     * <p>
     * @param msg the message to log.
     */
    public void severe(String msg) {
        log(Level.SEVERE, msg);
    }
    
    /**
     * Logs a message of level {@code Level.WARNING}; the message is
     * transmitted to all subscribed handlers.
     * <p>
     * @param msg the message to log.
     */
    public void warning(String msg) {
        log(Level.WARNING, msg);
    }
    
    /**
     * Logs a message of level {@code Level.INFO}; the message is transmitted
     * to all subscribed handlers.
     * <p>
     * @param msg the message to log.
     */
    public void info(String msg) {
        log(Level.INFO, msg);
    }
    
    /**
     * Logs a message of level {@code Level.CONFIG}; the message is transmitted
     * to all subscribed handlers.
     * <p>
     * @param msg the message to log.
     */
    public void config(String msg) {
        log(Level.CONFIG, msg);
    }
    
    /**
     * Logs a message of level {@code Level.FINE}; the message is transmitted
     * to all subscribed handlers.
     * <p>
     * @param msg the message to log.
     */
    public void fine(String msg) {
        log(Level.FINE, msg);
    }
    
    /**
     * Logs a message of level {@code Level.FINER}; the message is transmitted
     * to all subscribed handlers.
     * <p>
     * @param msg the message to log.
     */
    public void finer(String msg) {
        log(Level.FINER, msg);
    }
    
    /**
     * Logs a message of level {@code Level.FINEST}; the message is transmitted
     * to all subscribed handlers.
     * <p>
     * @param msg the message to log.
     */
    public void finest(String msg) {
        log(Level.FINEST, msg);
    }
    
    /**
     * Logs a message using the default level {@code Level.INFO}. 
     * The message is transmitted to all
     * subscribed handlers.
     * <p>
     * @param message the message to log.
     */
    public void log(String message) {
		info(message);
	}
	
    /**
     * Logs a message of the specified level. The message is transmitted to all
     * subscribed handlers.
     * <p>
     * @param level the level of the specified message.
     * @param message the message to log.
     */
	public void log(Level level, String message) {
		log(new LogRecord(level, message));
	}
	
    /**
     * Logs a message of the specified level with the supplied parameter. The
     * message is then transmitted to all subscribed handlers.
     * <p>
     * @param level the level of the given message.
     * @param message the message to log.
     * @param param the parameter associated with the event that is logged.
     */
    public void log(Level level, String message, Object param) {
        log(new FileLogRecord(level, message).setParametersF(new Object[] { param }));
    }
    
	/**
	 * Logs a message of the specified level with the supplied parameter array. The
	 * message is then transmitted to all subscribed handlers.
	 * <p>
	 * @param level the level of the given message
	 * @param message the message to log.
	 * @param params the parameter array associated with the event that is logged.
	 */
	public void log(Level level, String message, Object[] params) {
		log(new FileLogRecord(level, message).setParametersF(params));
	}
	
	/**
	 * Logs a message of the specified level with the supplied {@code Throwable}
	 * object. The message is then transmitted to all subscribed handlers.
	 * <p>
	 * @param level the level of the given message.
	 * @param message the message to log.
	 * @param throwable
	 *            the {@code Throwable} object associated with the event that is
	 *            logged.
	 */
	public void log(Level level, String message, Throwable throwable) {
		log(new FileLogRecord(level, message).setThrownF(throwable));
	}
	
	/**
	 * Logs a message of the given level with the specified source class name,
	 * source method name and parameter.
	 * <p>
	 * @param level the level of the given message
	 * @param source_class_name the source class name
	 * @param source_method_name the source method name
	 * @param message the message to be logged
	 */
	public void logp(Level level, String source_class_name, String source_method_name, String message) {
	    log(new FileLogRecord(level, message)
	    		.setSourceClassNameF(source_class_name)
	    		.setSourceMethodNameF(source_method_name));
	}

	/**
	 * Logs a message of the given level with the specified source class name,
	 * source method name and parameter.
	 * <p>
	 * @param level the level of the given message
	 * @param source_class_name the source class name
	 * @param source_method_name the source method name
	 * @param message the message to be logged
	 * @param params
     *            the parameter array associated with the event that is logged.
	 */
	public void logp(Level level, String source_class_name, String source_method_name, String message, Object[] params) {
	    log(new FileLogRecord(level, message)
	    		.setSourceClassNameF(source_class_name)
	    		.setSourceMethodNameF(source_method_name)
	    		.setParametersF(params));
	}
	
	/**
	 * Logs a message of the given level with the specified source class name,
	 * source method name and {@code Throwable} object.
	 * <p>
	 * @param level the level of the given message.
	 * @param source_class_name the source class name.
	 * @param source_method_name the source method name.
	 * @param message the message to be logged.
	 * @param throwable the {@code Throwable} object.
	 */
	public void logp(Level level, String source_class_name, String source_method_name, String message, Throwable throwable) {
		log(new FileLogRecord(level, message)
				.setSourceClassNameF(source_class_name)
				.setSourceMethodNameF(source_method_name)
				.setThrownF(throwable));
	}
	
	/**
	 * Logs a given log record. Only records with a logging level that is equal or
	 * greater than this logger's level will be submitted to this logger's handlers
	 * for logging. If {@code getUseParentHandlers()} returns {@code
	 * true}, the log record will also be submitted to the handlers of this logger's
	 * parent, potentially recursively up the namespace.
	 * <p>
	 * Since all other log methods call this method to actually perform the logging
	 * action, subclasses of this class can override this method to catch all
	 * logging activities.
	 * </p>
	 * @param record
	 *            the log record to be logged.
	 */
	public void log(LogRecord record) {
		log(record, new FileFormatter(getContext()));
	}
	
    /**
     * Logs a given log record and formatter. Only records with a logging level that is equal
     * or greater than this logger's level will be submitted to this logger's
     * handlers for logging. If {@code getUseParentHandlers()} returns {@code
     * true}, the log record will also be submitted to the handlers of this
     * logger's parent, potentially recursively up the namespace.
     * <p>
     * Since all other log methods call this method to actually perform the
     * logging action, subclasses of this class can override this method to
     * catch all logging activities.
     * </p>
     * @param record the log record to be logged.
     * @param formatter the logging format.
     */
	public void log(LogRecord record, Formatter formatter) {
		record.setLoggerName(getContextLogger().getName()); /* check record logger name */
		
		/* register record format */
		if (!logs.add(formatter.format(record))) {
			return;
		}
		
		/* format and write */
		String out = "";
		for (String log : new ArrayList<String>(logs)) {
			if (log != null) {
				out += log;
			}
		}
		
		/* donnot write empty logs */
		if (out.isEmpty()) {
			return;
		}
		writeToFile(out); /* create and write to log file */
	}
	
//  OLD:
//	public void log(LogRecord record) {
//		String          time = new LoggerHeader(getCurrentTime()).get();
//		String         level = new LoggerHeader(record.getLevel().getName()).get();
//		String  final_header = ( time + level + header + ": " );
//		
//		/* calculate throwable message */
//		Throwable  throwable = record.getThrown();
//		if (throwable != null) {
//			String message = ( record.getMessage() + System.lineSeparator() );
//			message       += ( final_header + throwable.toString() + System.lineSeparator() );
//			StackTraceElement[] trace = throwable.getStackTrace();
//			for (StackTraceElement t : trace) {
//				message += final_header + "\tat " + t.getClassName() 
//						+ "." + t.getMethodName() + "(" + t.getFileName()
//						+ ":" + t.getLineNumber() + ")" + System.lineSeparator();
//			}
//			record.setMessage(message);
//		}
//		
//		/* create and write to log file */
//		writeToFile(final_header + record.getMessage());
//	}
	
	/**
	 * Write logs to the log file.
	 * <p>
	 * @param out to write.
	 */
	private final void writeToFile(String out) {
		File log_folder = getLogsFolder(); /* log folder and its parents */
		if (!log_folder.getParentFile().exists() || !log_folder.exists()) {
			if (!log_folder.mkdirs()) {
				return;
			}
		}
		
		/* zip last */
		File log_file = getLatestLogFile();
		File   to_zip = new File(log_file.getParentFile(), ( getCurrentDate() + "-" + nextZipCount() + ".log" ) );
		if (log_file.exists()) {
			if (getWroteTimes() == 0) { /* zip last before overwritting */
				if (renameFile(log_file, to_zip, false)) {
					zipLast(to_zip);
				}
			}
			
			log_file.delete();
			to_zip.delete();
		}
		
		/* count wrote times */
		wrote_times ++;
		
		/* create and write */
		try {
			log_file.createNewFile();
		} catch (IOException e) {
			return;
		}
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(log_file));
			writer.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				/* ignore */
			}
		}
	}
	
	/**
     * Rename file.
     * <p>
     * @param source current file name.
     * @param destination new file name.
     * @param renameEmptyFiles if true, rename file even if empty, otherwise delete empty files.
     * @return true if successfully renamed.
     */
	private final boolean renameFile(final File source, final File destination, final boolean renameEmptyFiles) {
		if (renameEmptyFiles || (source.length() > 0)) {
			final File parent = destination.getParentFile();
			if ((parent != null) && !parent.exists()) {
				/**
				 * LOG4J2-679: ignore mkdirs() result: in multithreaded scenarios,
				 * if one thread succeeds the other thread returns false
				 * even though directories have been created. Check if dir exists instead.
				 */
				parent.mkdirs();
				if (!parent.exists()) {
					return false;
				}
			}
			try {
				try {
					Files.move(Paths.get(source.getAbsolutePath()), Paths.get(destination.getAbsolutePath()),
							StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
					return true;
				} catch (final IOException exMove) {
					boolean result = source.renameTo(destination);
					if (!result) {
						try {
							Files.copy(Paths.get(source.getAbsolutePath()), Paths.get(destination.getAbsolutePath()),
									StandardCopyOption.REPLACE_EXISTING);
							try {
								Files.delete(Paths.get(source.getAbsolutePath()));
								result = true;
							} catch (final IOException exDelete) {
								try {
									new PrintWriter(source.getAbsolutePath()).close();
								} catch (final IOException exOwerwrite) {
									exOwerwrite.printStackTrace();
								}
							}
						} catch (final IOException exCopy) {
							exCopy.printStackTrace();
						}
					}
					return result;
				}
			} catch (final RuntimeException ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				source.delete();
			} catch (final Exception exDelete) {
				exDelete.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Returns the number
	 * of compressed log files.
	 * <p>
	 * @return number of zips.
	 */
	private final int nextZipCount() {
		return getLogsFolder().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				if (name.toLowerCase().endsWith(".zip")) {
					if (name.startsWith(getCurrentDate())) {
						return true;
					}
				}
				return false;
			}
		}).length + 1;
	}
	
	/**
	 * Compress latest log file.
	 * <p>
	 * @param to_zip the file 'latest.log'.
	 */
	private final void zipLast(File to_zip) {
		if (to_zip.exists()) {
			try {
				byte[]           buffer = new byte[1024];
				File           zip_file = new File(getLogsFolder(), ( getCurrentDate() + "-" + nextZipCount() + ".log.zip" ) );
				FileOutputStream output = new FileOutputStream(zip_file);
				ZipOutputStream zip_out = new ZipOutputStream(output);
		        FileInputStream   input = new FileInputStream(to_zip);
		        
				// begin writing a new ZIP entry, positions the stream to the start of the entry data
		        zip_out.putNextEntry(new ZipEntry(to_zip.getName()));
		        
	            int length;
	            while ((length = input.read(buffer)) > 0) {
	            	zip_out.write(buffer, 0, length);
	            }
		        
	            zip_out.closeEntry();
	            zip_out.close();
	            input.close();
		        output.close();
			} catch(Throwable t) {
				/* ignore */
			}
		}
	}
    
	/**
	 * Returns current date.
	 * <p>
	 * @return current date.
	 */
	private final String getCurrentDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate              date = LocalDate.now();
		return formatter.format(date);
	}
}