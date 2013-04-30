import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class SearchServlet extends BaseServlet{
	private	Driver ls = new Driver();
	private PrintWriter out;
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		prepareResponse("Search", response);
		Map<String, String> cookies = getCookieMap(request);
		
		String login = cookies.get("login");
		
		if(login == null || login.equals("false")){
			try {
				response.sendRedirect("/login");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		
		try {
			out = response.getWriter();
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
//		}
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
		String search = request.getParameter("search");
		if(!search.equals("")){
			WorkQueue threadPool = new WorkQueue(10);

			SearchParser sp = new SearchParser(ls.getIndexInstance(), threadPool);
			sp.readSearches(search);
			try {
				response.sendRedirect("/search");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			threadPool.waitPending();

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
		
		out.println("<form action=\"/search\" method=\"post\">");
		
		out.println("<input type=\"text\" name=\"search\" size=\"30\">");
		ls.getIndexInstance().printSearches(out);

		out.println("<p><input type=\"submit\" value=\"Submit\"></p>");
		out.println("</form>");
	}

}
