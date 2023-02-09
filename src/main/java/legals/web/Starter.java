package legals.web;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import legals.model.*;
import legals.utils.*;

@WebServlet(urlPatterns = {"/Starter"})
public class Starter extends TopServlet{

    static final long serialVersionUID = 72L;
    /**
     * Generates the frame set.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	PrintWriter os = null;
	HttpSession session = null;
	String type = "";
	Enumeration<String> values = req.getParameterNames();
	String name, value, id="", unicID="";
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    value = req.getParameter(name).trim();
	    if(name.equals("id")){
		id = value;
	    }
	    else if(name.equals("type")){
		type = value;
	    }	
	}
	User user = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"/Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"/Login?";
	    res.sendRedirect(str);
	    return; 
	}
	if(!user.canEdit()){ // view only user
	    String str = url+"/Browse";
	    res.sendRedirect(str);
	    return; 
	}
	//
	// check for the user
	//
	// Inserts
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url,true));
	if(type.equals("")){
	    out.println(Inserts.sideBar(url, user));
	}
	else{
	    out.println(Inserts.sideBar2(url));
	}

	out.println("<div id=\"mainContent\">");
	if(type.equals("")){
	    out.println("<h2>LegalTrack Introduction</h2>");
	    out.println("<fieldset><legend>Introduction </legend>");
	}
	else{
	    out.println("<h2>LegalTrack Actions</h2>");
	    out.println("<fieldset><legend>Actions </legend>");		
	}
	out.println("Select one of the following options from the left side menu:<br />");
	out.println("<ul>");
	if(type.equals("")){
	    out.println("<li>To add a new Case, select 'New Case'</li>");
	    out.println("<li>To add a new Defendant select 'New Defendant'</li>");
	    out.println("<li>To search for case(s), select 'Search'</li>");
	    out.println("<li>To search for defendants, select 'Search Defendants'</li>");
	    out.println("<li>To generate reports, select 'Reports'</li>");
	    out.println("<li>To create labels, mail letters, select 'Send Out'</li>");

	}
	else{
	    out.println("<li>New actions: list of newly entered records'</li>");
	    out.println("<li>Ongoing actions: list of ongoing records.</li>");
	    out.println("<li>Legal Attention: list of records that were modified by HAND dept. and need Legal dept input.</li>");
	    out.println("<li>HAND Attention: list of records that were modified by Legal Dept and expect HAND interaction.</li>");
	    out.println("<li>To search for any record use 'Search Actions'</li>");
			
	}
	out.println("<li>When done please click on 'Logout' from the menu bar</li>");
	out.println("</ul>");
	out.println("</fieldset>");
	out.println("</div>");
	out.println("</html>");
	out.close();
    }				   
    /**
     * @link #doGet
     */		  
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	doGet(req, res);
    }

}

