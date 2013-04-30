import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class SetupServlet extends BaseServlet{

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse("Setup", response);
		Map<String, String> cookies = getCookieMap(request);
		
		String login = cookies.get("login");
		
		if(login == null || login.equals("false")){
			try {
				response.sendRedirect("/login");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
		
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
			

		

			// print login form
			printForm(out, cookies.get("name"));
		
		}
		catch(IOException ex) {
			log.debug("Unable to prepare response body.", ex);
		}
		}
		finishResponse(response);
	}
	
	/**
	 * Processes the login form, and passes any errors back to the login
	 * servlet GET response.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		// get username and password from form
		Map<String, String> cookies = getCookieMap(request);
		ResultSet results = db.getComments(cookies.get("name"));
		Status status = null;
		String newpass = request.getParameter("pass");
		String name = request.getParameter("fullName");
		String email = request.getParameter("email");
		String delete = request.getParameter("delete");
		if(delete!= null){
			try {
				response.sendRedirect(response.encodeRedirectURL("/deleteaccount"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			// get status from database handler registration attempt
//			if(newpass.equals("")){
//				try {
//					results.next();
//					System.out.println(results.getString("password"));
//					newpass = results.getString("password");
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			status = db.changeUser(cookies.get("name"), newpass, name, email);
		
		try {
			if(status == Status.OK) {
				// if everything went okay, let the new user login
				response.sendRedirect(response.encodeRedirectURL("/setup?error=CHANGE_SUCCESSFUL"));
			}

		}
		catch(IOException ex) {
			log.warn("Unable to redirect user. " + status, ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	}
	
	/**
	 * Prints login form using supplied PrintWriter.
	 * @param out PrintWriter from HTTP response
	 */
	private void printForm(PrintWriter out, String username) {
		assert out != null;
		ResultSet results = db.getComments(username);
		try {
			results.next();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		out.println("<h2>" + username + " </h2>");
		out.println("<form action=\"/setup\" method=\"post\">");
		
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Full Name: </td>");
		
		try {
			out.println("\t\t<td><input type=\"text\" value = \"" + results.getString("name") + "\" name=\"fullName\" size=\"30\"></td>");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.println("</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Email: </td>");
		try {
			out.println("\t\t<td><input type=\"text\" value = \"" + results.getString("email") + "\" name=\"email\"size=\"30\"></td>");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<input type = \"checkbox\" name = \"delete\" value = \"delete \"> Delete account ");
		out.println("<p><input type=\"submit\" value=\"Submit\"></p>");
		out.println("</form>");
	}

	
}