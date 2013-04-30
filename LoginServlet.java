import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for handling registration requests. Part of the 
 * {@link Driver} example.
 */
@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {
	/**
	 * Output the registration form, and any error messages from the
	 * previous login attempt.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse("Login", response);
		
		try {
			PrintWriter out = response.getWriter();
			String error = request.getParameter("error");
			
			/* 
			 * Avoid using any user input directly in the HTML output,
			 * to avoid cross-side scripting attacks.
			 */
			if(error != null) {
				// gets error message from the status enum name
				String errorMessage = getStatusMessage(error);
				
				// safe to output, since we provide the error messages
				out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
			}
			
			// output success message if redirected from successful registration
			if(request.getParameter("newuser") != null) {
				out.println("<p>Registration was successful!");
				out.println("Login with your new username and password below.</p>");
			}

			// erase cookies if logout was requested
			if(request.getParameter("logout") != null) {
				eraseCookies(request, response);
				out.println("<p>Successfully logged out.</p>");
			}

			// print login form
			printForm(out);
		}
		catch(IOException ex) {
			log.debug("Unable to prepare response body.", ex);
		}

		finishResponse(response);
	}
	
	/**
	 * Processes the login form, and passes any errors back to the login
	 * servlet GET response.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String user = request.getParameter("user");
		String pass = request.getParameter("pass");
		
		Status status = db.verifyLogin(user, pass);
		
		try {
			if(status == Status.OK) {
				// add cookies to indicate user successfully logged in
				response.addCookie(new Cookie("login", "true"));
				response.addCookie(new Cookie("name", user));
				
				// redirect to welcome page
				response.sendRedirect(response.encodeRedirectURL("/welcome"));
			}
			else {
				// make sure any old login cookies are cleared
				response.addCookie(new Cookie("login", "false"));
				response.addCookie(new Cookie("name", ""));

				// let user try again
				response.sendRedirect(response.encodeRedirectURL("/login?error=" + status.name()));
			}
		}
		catch(Exception ex) {
			log.error("Unable to process login form.", ex);
		}
	}
	
	/**
	 * Prints login form using supplied PrintWriter.
	 * @param out PrintWriter from HTTP response
	 */
	private void printForm(PrintWriter out) {
		assert out != null;
		
		out.println("<form action=\"/login\" method=\"post\">");
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Usename:</td>");
		out.println("\t\t<td><input type=\"text\" name=\"user\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p><input type=\"submit\" value=\"Login\"></p>");
		out.println("</form>");
		
		out.println("<p>(<a href=\"/register\">new user? register here.</a>)</p>");
	}
}
