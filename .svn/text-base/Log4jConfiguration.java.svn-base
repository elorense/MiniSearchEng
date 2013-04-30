import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

public class Log4jConfiguration {

	/*
	 * Several different log message formats. The simplest format is
	 * PLAIN_FORMAT. The format often used for console output is LEVEL_FORMAT.
	 * The format often used for file output is FILE_FORMAT. To see what each
	 * conversion character produces, use VERBOSE_FORMAT.
	 */
	public static final String PLAIN_FORMAT = "%m%n";
	public static final String LEVEL_FORMAT = "[%-5p] %m%n";
	public static final String FILE_FORMAT = "[%d{yyyy-mm-dd hh:mm:ss SSS} %c@%-4L %-5p] %m%n";

	public static final String VERBOSE_FORMAT = "[Date: %d{yyyy-mm-dd}] " +
			"[Time: %d{HH:mm:ss:SSS}] " +
			"[Priority: %-5p] " +
			"[Line: %4L] " +
			"[File Name: %F] " +
			"[Class Name: %C{1}] " +
			"[Fully Qualified Class: %C] " +
			"[Method Name: %M] " +
			"[Location: %l] " +
			"[Thread Name: %t] " +
			"[Category: %c] " +
			"[Log Message: %m] " +
			"%n";

	// tests whether logging is setup by looking for appenders
	public static boolean isConfigured() {
		return Logger.getRootLogger().getAllAppenders().hasMoreElements();
	}
	
	// attempts to load configuration from file
	public static boolean loadConfiguration() {
		PropertyConfigurator.configure("log4j.properties");		
		return isConfigured();
	}
	
	// a default console appender that only outputs ERROR messages and above
	public static boolean defaultConfiguration() {
		PatternLayout consoleLayout = new PatternLayout(LEVEL_FORMAT);
		ConsoleAppender consoleAppender = new ConsoleAppender(consoleLayout);
		consoleAppender.setThreshold(Level.ERROR);
		Logger.getRootLogger().addAppender(consoleAppender);
		
		return isConfigured();
	}
	
	// example method that loads configuration from file if possible,
	// otherwise attempts to setup a basic log configuration.
	public static void configureLogger() {
		// do nothing if already configured
		if(isConfigured()) {
			return;
		}
		
		// attempt to load configuration
		if(!loadConfiguration()) {
			// if load failed, use basic configuration
			defaultConfiguration();
		}
	}
	
	// sends several test messages of different levels to logger
	public static void testMessages(Logger log) {
		log.trace("This is a trace message.");
		log.debug("This is a debug message.");
		log.info("This is an info message.");
		log.warn("This is a warning message.");
		log.error("This is an error message.", new Exception("This is a generic exception."));
		log.fatal("This is a fatal message.");
	}
	
	// tests local versus root logger
	public static void testLocalLogger() {
		Logger local = Logger.getLogger(Log4jConfiguration.class);
		Logger root = Logger.getRootLogger();

		PatternLayout verboseLayout = new PatternLayout(VERBOSE_FORMAT);
		
		// try to create verbose file log
		try {
			FileAppender fileAppender = new FileAppender(verboseLayout, "verbose.log", false);
			fileAppender.setThreshold(Level.ALL);
			root.addAppender(fileAppender);
			local.addAppender(fileAppender);
		}
		// otherwise setup a console appender
		catch(IOException ex) {
			ConsoleAppender consoleAppender = new ConsoleAppender(verboseLayout);
			consoleAppender.setThreshold(Level.ALL);
			root.addAppender(consoleAppender);
			local.addAppender(consoleAppender);
		}
			
		testMessages(local);
		testMessages(root);
	}
	
	// demos logging features
	public static void main(String[] args) {
		configureLogger();
		testLocalLogger();
	}	
}
