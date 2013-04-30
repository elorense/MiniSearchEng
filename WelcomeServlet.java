import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that welcomes the user, if logged in. Otherwise, redirects
 * to the {@link LoginServlet}. Part of the {@link Driver} example.
 */
@SuppressWarnings("serial")
public class WelcomeServlet extends BaseServlet {	
	/**
	 * Checks for the login cookie. If found, displays a welcome message.
	 * Otherwise, redirects to the {@link LoginServlet}.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> cookies = getCookieMap(request);
		
		String login = cookies.get("login");
		String user  = cookies.get("name");
		
		// if login and user cookies appropriately set
		if(login != null && login.equals("true") && user != null) {
			prepareResponse("Welcome", response);
			
			try {
				// display welcome message
				PrintWriter out = response.getWriter();
				out.println("<p>Hello " + user + "!</p>");
				
				// add link allowing user to logout
				out.println("<p><a href=\"/login?logout\">(logout)</a></p>");
				out.println("<p><a href=\"/setup\">(setup)</a></p>");
				out.println("<p><a href=\"/search\">(go to search)</a></p>");
			}
			catch(IOException ex) {
				log.warn("Unable to write response body.", ex);
			}
			
			finishResponse(response);
		}
		else {
			try {
				// redirect to /login if any issues with login cookies
				response.sendRedirect("/login");
			}
			catch(Exception ex) {
				log.warn("Unable to redirect to /login page.", ex);
			}
		}
	}
}
