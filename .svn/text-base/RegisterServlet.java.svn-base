import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for handling registration requests. Part of the 
 * {@link Driver} example.
 */
@SuppressWarnings("serial")
public class RegisterServlet extends BaseServlet {
	/**
	 * Output the registration form, and any error messages from the
	 * previous registration attempt.
	 */
	String nullmessage;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			prepareResponse("Register New User", response);
			
			PrintWriter out = response.getWriter();
			String error = request.getParameter("error");
			String missing = request.getParameter("missing");
			/* 
			 * Avoid using any user input directly in the HTML output,
			 * to avoid cross-side scripting attacks.
			 */
			if(missing != null){
//			if(missing.equals("null_field")){
				out.println("<p style=\"color: red;\">" + nullmessage + "</p>");

			}
			if(error != null) {
				
				// gets error message from the status enum name
				String errorMessage = getStatusMessage(error);
				
				// safe to output, since we provide the error messages
				out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
			}
			
			printForm(out);
			finishResponse(response);
		}
		catch(IOException ex) {
			log.debug("Unable to prepare response properly.", ex);
		}
	}

	/**
	 * Processes the registration form, and passes any errors back
	 * to the registration servlet GET response.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse("Register New User", response);
		Status status = null;
		// get username and password from form
		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		String name = request.getParameter("fullName");
		String email = request.getParameter("email");
		// get status from database handler registration attempt
		if(newuser.equals("") || newpass.equals("") || name.equals("") || email.equals("")){
			nullmessage = "Must fill in the following fields: \n";
			if(newuser.equals("")){
				nullmessage = nullmessage.concat("\"Username \" ");
			}
			if(newpass.equals("")){
				nullmessage = nullmessage.concat("\"Password \" ");
			}
			if(name.equals("")){
				nullmessage = nullmessage.concat("\"Name \" ");
			}
			if(email.equals("")){
				nullmessage = nullmessage.concat("\"Email \" ");
			}
			try {
				response.sendRedirect(response.encodeRedirectURL("/register?missing=null_field"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}else{
			status = db.registerUser(newuser, newpass, name, email);
			try {
				if(status == Status.OK) {
					// if everything went okay, let the new user login
					response.sendRedirect(response.encodeRedirectURL("/login?newuser=true"));
				}
				else {				
					// include status name in url to provide user-friendly
					// error message later
					String url = "/register?error=" + status.name();
					
					// encode url properly (see http://www.w3schools.com/tags/ref_urlencode.asp)
					url = response.encodeRedirectURL(url);
					
					// make user try to register again by redirecting back
					// to registration servlet
					response.sendRedirect(url);
				}
			}
			catch(IOException ex) {
				log.warn("Unable to redirect user. " + status, ex);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		
		}

		}
	
	/**
	 * Prints registration form using supplied PrintWriter.
	 * @param out PrintWriter from HTTP response
	 */
	private void printForm(PrintWriter out) {
		assert out != null;
		
		out.println("<form action=\"/register\" method=\"post\">");
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Username:</td>");
		out.println("\t\t<td><input type=\"text\" name=\"user\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Full Name: </td>");
		out.println("\t\t<td><input type=\"text\" name=\"fullName\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Email: </td>");
		out.println("\t\t<td><input type=\"text\" name=\"email\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p><input type=\"submit\" value=\"Register\"></p>");
		out.println("</form>");
	}
}
