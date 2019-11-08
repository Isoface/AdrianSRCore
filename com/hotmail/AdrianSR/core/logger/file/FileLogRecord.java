package com.hotmail.AdrianSR.core.logger.file;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Represents a {@link LogRecord} easy
 * to use.
 * <p>
 * @author AdrianSR
 */
public class FileLogRecord extends LogRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7031566919360734959L;

	/**
	 * Store the current value for the sequence number
	 */
    private static long currentSequenceNumber = 0;
    
 	/**
 	 * Store the id for each thread
 	 */
    private static ThreadLocal<Integer> currentThreadId = new ThreadLocal<Integer>();
    
    /**
     * The base id as the starting point for thread ID allocation.
     */
    private static int initThreadId = 0;
    
	/**
	 * Values.
	 */
	private Level                   level; /* The major byte used in serialization */
	private String             loggerName;
	private long                   millis;
	private String                message;
	private transient Object[] parameters;
	private ResourceBundle resourceBundle;
	private String     resourceBundleName;
	private long           sequenceNumber;
	private String        sourceClassName;
	private transient boolean sourceInitialized;
	private String       sourceMethodName;
	private int                  threadID;
	private Throwable              thrown;

	/**
	 * Constructs a {@code EasyLogRecord} object using the supplied the logging level
	 * and message. The millis property is set to the current time. The sequence
	 * property is set to a new unique value, allocated in increasing order within
	 * the VM. The thread ID is set to a unique value for the current thread. All
	 * other properties are set to {@code null}.
	 * <p>
	 * @param level the logging level, may not be {@code null}.
	 * @param msg the raw message.
	 * @throws NullPointerException
	 *             if {@code level} is {@code null}.
	 */
	public FileLogRecord(Level level, String msg) {
		super(level, msg);
        if (level == null) {
            throw new NullPointerException("The level cannot be null!");
        }
        
        this.level   = level;
        this.message = msg;
        this.millis  = System.currentTimeMillis();
        
        synchronized (LogRecord.class) {
            this.sequenceNumber = currentSequenceNumber++;
            Integer          id = currentThreadId.get();
            if (id == null) {
                this.threadID = initThreadId;
                currentThreadId.set(Integer.valueOf(initThreadId++));
            } else {
                this.threadID = id.intValue();
            }
        }
        
        this.sourceClassName    = null;
        this.sourceMethodName   = null;
        this.loggerName         = null;
        this.parameters         = null;
        this.resourceBundle     = null;
        this.resourceBundleName = null;
        this.thrown             = null;
	}
	
	@Override
    public Level getLevel() {
        return level;
    }
	
	/**
     * Sets the logging level.
     * <p>
     * @param level the level to set.
     * @throws NullPointerException if {@code level} is {@code null}.
     */
    public FileLogRecord setLevelF(Level level) {
        if (level == null) {
            throw new NullPointerException("level == null");
        }
        this.level = level;
        return this;
    }
    
    @Override
    public String getLoggerName() {
        return loggerName;
    }
    
    /**
     * Sets the name of the logger.
     * <p>
     * @param loggerName the logger name to set.
     */
    public FileLogRecord setLoggerNameF(String loggerName) {
        this.loggerName = loggerName;
        return this;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the raw message. When this record is formatted by a logger that has
     * a localization resource bundle that contains an entry for {@code message},
     * then the raw message is replaced with its localized version.
     * <p>
     * @param message the raw message to set, may be {@code null}.
     */
    public FileLogRecord setMessageF(String message) {
        this.message = message;
		return this;
    }
    
    @Override
    public long getMillis() {
        return millis;
    }
    
    /**
     * Sets the time when this event occurred, in milliseconds since 1970.
     * <p>
     * @param millis the time when this event occurred, in milliseconds since 1970.
     */
    public FileLogRecord setMillisF(long millis) {
        this.millis = millis;
        return this;
    }
    
    @Override
    public Object[] getParameters() {
        return parameters;
    }
    
    /**
     * Sets the parameters.
     * <p>
     * @param parameters the array of parameters to set, may be {@code null}.
     */
    public FileLogRecord setParametersF(Object[] parameters) {
        this.parameters = parameters;
        return this;
    }
    
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    /**
     * Sets the resource bundle used to localize the raw message during
     * formatting.
     * <p>
     * @param resourceBundle the resource bundle to set, may be {@code null}.
     */
    public FileLogRecord setResourceBundleF(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        return this;
    }
    
    @Override
    public String getResourceBundleName() {
        return resourceBundleName;
    }
    
    /**
     * Sets the name of the resource bundle.
     * <p>
     * @param resourceBundleName the name of the resource bundle to set.
     */
    public FileLogRecord setResourceBundleNameF(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
        return this;
    }
    
    @Override
    public long getSequenceNumber() {
        return sequenceNumber;
    }
    
    /**
     * Sets the sequence number. It is usually not necessary to call this method
     * to change the sequence number because the number is allocated when this
     * instance is constructed.
     * <p>
     * @param sequenceNumber the sequence number to set.
     */
    public FileLogRecord setSequenceNumberF(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }
    
    @Override
    public String getSourceClassName() {
        initSource();
        return sourceClassName;
    }
    
    /**
     * Initialize the sourceClass and sourceMethod fields.
     */
    private void initSource() {
        if (sourceInitialized) {
            return;
        }
        
        boolean sawLogger = false;
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            String current = element.getClassName();
            if (current.startsWith(Logger.class.getName())) {
                sawLogger = true;
            } else if (sawLogger) {
                this.sourceClassName  = element.getClassName();
                this.sourceMethodName = element.getMethodName();
                break;
            }
        }
        sourceInitialized = true;
    }
    
	/**
	 * Sets the name of the class that is the source of this log record.
	 * <p>
	 * @param sourceClassName
	 *            the name of the source class of this log record, may be
	 *            {@code null}.
	 */
    public FileLogRecord setSourceClassNameF(String sourceClassName) {
        this.sourceInitialized = true;
        this.sourceClassName   = sourceClassName;
        return this;
    }
    
    @Override
    public String getSourceMethodName() {
        initSource();
        return sourceMethodName;
    }
    
    /**
     * Sets the name of the method that is the source of this log record.
     * <p>
     * @param sourceMethodName
     *            the name of the source method of this log record, may be
     *            {@code null}.
     */
    public FileLogRecord setSourceMethodNameF(String sourceMethodName) {
        this.sourceInitialized = true;
        this.sourceMethodName  = sourceMethodName;
        return this;
    }
    
    @Override
    public int getThreadID() {
        return threadID;
    }
    
	/**
	 * Sets the ID of the thread originating this log record.
	 * <p>
	 * @param threadID the new ID of the thread originating this log record.
	 */
    public FileLogRecord setThreadIDE(int threadID) {
        this.threadID = threadID;
        return this;
    }
    
    @Override
    public Throwable getThrown() {
        return thrown;
    }
    
    /**
     * Sets the {@code Throwable} object associated with this log record.
     * <p>
     * @param thrown
     *            the new {@code Throwable} object to associate with this log
     *            record.
     */
    public FileLogRecord setThrownF(Throwable thrown) {
        this.thrown = thrown;
        return this;
    }
}