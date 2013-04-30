import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class DeleteServlet extends BaseServlet{
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
			prepareResponse("Delete Account", response);
			
			try {
				// display welcome message
				PrintWriter out = response.getWriter();
				out.println("<p>Are you sure you want to delete your account, <strong>" + user + "</strong>");
				out.println("<form action=\"/deleteaccount\" method=\"post\">");
				
				out.println("<input type = \"radio\" name = \"delete\" value = yes> Yes, I want to delete " +
						"my account. ");
				out.println("<input type = \"radio\" name = \"delete\" value = no> No, I change my mind.");

				out.println("<p><input type=\"submit\" value=\"Submit\"></p>");
				out.println("</form>");
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
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		// get username and password from form
		Map<String, String> cookies = getCookieMap(request);
		Status status = null;
		
		String delete = request.getParameter("delete");
		if(delete.equals("yes")){
			status = db.deleteUser(cookies.get("name"));
			eraseCookies(request, response);
			try {
				response.sendRedirect(response.encodeRedirectURL("/login?error=DELETE_ACCOUNT"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			// get status from database handler registration attempt
		
			try {
					// if everything went okay, let the new user login
				response.sendRedirect(response.encodeRedirectURL("/setup"));
				
	
			}
			catch(IOException ex) {
				log.warn("Unable to redirect user. " + status, ex);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
	}
	}


}
