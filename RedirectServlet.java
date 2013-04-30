import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that redirects all requests to the {@link LoginServlet}. 
 * Part of the {@link Driver} example.
 */
@SuppressWarnings("serial")
public class RedirectServlet extends BaseServlet {
	/**
	 * Redirects GET requests to the {@link LoginServlet}.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {		
		Map<String, String> cookies = getCookieMap(request);
		
		String login = cookies.get("login");
		String user  = cookies.get("name");
		
		// if login and user cookies appropriately set
		if(login != null && login.equals("true") && user != null) {
			try {
				// redirect to /welcome if properly logged in
				response.sendRedirect("/welcome");
			}
			catch(Exception ex) {
				log.warn("Unable to redirect to /welcome page.", ex);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		else {
			try {
				// redirect to /login if any issues with login cookies
				response.sendRedirect("/login");
			}
			catch(Exception ex) {
				log.warn("Unable to redirect to /login page.", ex);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	/**
	 * Redirects any POST requests to the {@link #doGet(HttpServletRequest, HttpServletResponse)}
	 * method.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
