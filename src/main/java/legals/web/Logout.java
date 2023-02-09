package legals.web;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import legals.model.*;
import legals.utils.*;
/**
 * Logout page.
 *
 */
@WebServlet(urlPatterns = {"/Logout"})
public class Logout extends TopServlet{

    static final long serialVersionUID = 52L;
    /**
     * Deletes the sesion info.
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	Enumeration<String> values = req.getParameterNames();
	String name= "";
	String value = "";
	String switchTo = "";
	String id = "";
	while (values.hasMoreElements()) {
	    name = values.nextElement().trim();
	    value = req.getParameter(name).trim();
	    if (name.equals("switchTo"))
		switchTo = value;
	    else if (name.equals("id"))
		id = value;	
	}	
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    session.invalidate();
	}
	if(switchTo.equals("")){
	    res.sendRedirect("url="+url);	
	}
	else{
	    //
	    // to rental
	    //
	    String str = rentalUrl+"?id="+id;
	    res.sendRedirect(str);
	}
	return;
    }
}






















































