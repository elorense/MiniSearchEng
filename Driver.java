import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Part of a code example that demonstrates how to use JDBC and mySQL to
 * store user name and passwords, and how to track a user session using
 * cookies. Provides some basic measures for preventing common attacks,
 * like SQL injection, cross-side scripting, etc.
 * 
 * <p>
 * Provided for CS 212 Software Development at the Department of Computer 
 * Science at the University of San Francisco. See 
 * <a href="http://www.cs.usfca.edu">www.cs.usfca.edu</a> for more.
 */
public class Driver {
	/** A log4j logger associated with the {@link Driver} class. */
	protected static Logger log = Logger.getLogger(Driver.class);
	public static final InvertedIndex index = new InvertedIndex();
	
	/** Start server on alternative port 8080 instead of default port 80. */
	private static int PORT = 8080;
	
	public static InvertedIndex getIndexInstance(){
		return index;
	}
	
	/**
	 * Configures the log4j logger. If a log4j.properties file was not
	 * found, then adds a simple console appender for INFO messages and
	 * above.
	 */
	private static void configureLogger() {
		
		PropertyConfigurator.configure("log4j.properties");
		
		if(!log.getAllAppenders().hasMoreElements()) {
			log.addAppender(new ConsoleAppender(new PatternLayout("%m%n")));
			log.setLevel(Level.INFO);
		}
	}
	
	/**
	 * Starts the Jetty server on port 8080 and configures the servlet
	 * handlers.
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		configureLogger();
		
		Server server = new Server(PORT);
		
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		
		ArgumentParser ap = new ArgumentParser(args);
		WorkQueue threadPool = new WorkQueue(10);
		Fetcher f = new Fetcher(ap.getValue("-u"), threadPool, index);
		HashMap<String, ArrayList<Integer>> subIndex = new HashMap<String, ArrayList<Integer>>();
		f.fetch(ap.getValue("-u"), subIndex);
		threadPool.waitPending();
		
		threadPool.shutdown();


		
		handler.addServletWithMapping(SearchServlet.class, "/search");
		handler.addServletWithMapping(LoginServlet.class,    "/login");
		handler.addServletWithMapping(RegisterServlet.class, "/register");
		handler.addServletWithMapping(WelcomeServlet.class,  "/welcome");
		handler.addServletWithMapping(RedirectServlet.class, "/");
		handler.addServletWithMapping(SetupServlet.class, "/setup");
		handler.addServletWithMapping(DeleteServlet.class, "/deleteaccount");

		
		log.info("Starting server on port " + PORT + "...");
		
		try {
			server.start();			
			server.join();
			
			log.info("Exiting...");
		}
		catch(Exception ex) {
			log.fatal("Interrupted while running server.", ex);
			System.exit(-1);
		}
	}
}