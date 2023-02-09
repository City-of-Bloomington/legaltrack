package legals.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.list.*;
import legals.utils.*;
/**
 *
 *
 */
@WebServlet(urlPatterns = {"/LegalNActionsServ"})
public class LegalNActionsServ extends TopServlet{

    static final long serialVersionUID = 47L;
    Logger logger = LogManager.getLogger(LegalNActionsServ.class);
    /**
     * Generates the search defendants form and then list the matching records.
     *
     * The user can check a selection of these and clicks the add defendents
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    
    /**
     * @link LegalServ#doGet
     * @see #doGet
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);

    }

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	boolean success = true;
	String [] titles = {"Legal ID",
	    "Case ID",
	    "Rental ID",
	    "Date",
	    "Status",
							
	    "By",
	    "Current Attention",
	    "Address",
	    "Reason"

	};
	String action=""; 
	String  dateFrom="", dateTo="", whichDate="", attention="",
	    reason="", startBy="";
	String id="",rental_id="", case_id="", status="";

	String message = "";
	Enumeration<String> values = req.getParameterNames();
	String [] idArr = null;
	String [] vals;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
	    }
	    else if (name.equals("rental_id")) {
		rental_id =value;
	    }
	    else if (name.equals("case_id")) {
		case_id =value;
	    }
	    else if (name.equals("reason")) {
		reason =value;
	    }
	    else if (name.equals("startBy")) {
		startBy =value;
	    }
	    else if (name.equals("status")) {
		status =value;
	    }
	    else if (name.equals("attention")) {
		attention = value;
	    }
	    else if (name.equals("whichDate")) {
		whichDate = value;
	    }
	    else if (name.equals("dateFrom")) {
		dateFrom = value;
	    }
	    else if (name.equals("dateTo")) {
		dateTo = value;
	    }

	}
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	//
	// Inserts
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar2(url));
	out.println("<div id=\"mainContent\">");
	out.println("<h1>Search Results</h1>");

	LegalNActions cl = new LegalNActions(debug);
	if(!id.equals("")){
	    cl.setId(id);
	}
	if(!status.equals("")){
	    cl.setStatus(status);
	}
	if(!attention.equals("")){
	    cl.setAttention(attention);
	}
	if(!rental_id.equals("")){
	    cl.setRental_id(rental_id);
	}
	if(!case_id.equals("")){
	    cl.setCase_id(case_id);
	}
	if(!reason.equals("")){
	    cl.setReason(reason);
	}
	if(!startBy.equals("")){
	    cl.setStartBy(startBy);
	}
	if(!dateFrom.equals("")){
	    cl.setDateFrom(dateFrom);
	}
	if(!dateTo.equals("")){
	    cl.setDateTo(dateTo);
	}
	if(!whichDate.equals("")){
	    cl.setWhichDate(whichDate);
	}
	String msg = cl.lookFor();
	List<LegalNAction> lactions = null;
	if(msg.equals("")){
	    lactions = cl.getLegActions();
	}
	else{
	    message += msg;
	    success = false;
	}
	int ncnt = 0;
	if(lactions != null){
	    if(lactions.size() == 1){
		LegalNAction one = lactions.get(0);
		res.sendRedirect(url+"LegalServ?id="+one.getId());
		return;
	    }
	    else{
		ncnt = lactions.size();
	    }
	}
				

	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");
	}
	else{
	    if(!message.equals(""))
		out.println("<h3 class=\"errorMessages\">"+message+"</h3>");
	}
	out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
	if(ncnt > 0){
	    String str = "", str2="", back="", rid="";
	    out.println("<table class='box'>");
	    out.println("<tr>");
	    for (int c = 0; c < titles.length; c++){
		if(c == 6){
		    if(attention.isEmpty()){
			out.println("<th>"+titles[c]+"</th>");
		    } // skip
		}
		else{
		    out.println("<th>"+titles[c]+"</th>");
		}
	    }	   
	    out.println("</tr>");
	    for(LegalNAction legal: lactions){
		str = legal.getId();
		out.println("<tr>");
		out.println("<td><a href=\""+url+
			    "LegalServ?"+
			    "id="+str+"\">"+
			    str+"</a></td>"); 
		str = legal.getCase_id();
		out.println("<td><a href=\""+url+
			    "CaseServ?"+
			    "action=zoom&id="+str+"\">"+
			    str+"</a></td>"); 
		rid = legal.getRental_id();
		out.println("<td>"+str+"</td>");
		str = legal.getDate();
		if(str.equals("")) str = "&nbsp;";
		out.println("<td>"+str+"</td>");
		str = legal.getStatus();
		if(str.equals("")) str = "&nbsp;";
		out.println("<td>"+str+"</td>");
		str = legal.getFullName();
		if(str.equals("")) str = "&nbsp;";
		out.println("<td>"+str+"</td>");
		if(attention.isEmpty()){
		    str = legal.getAttention();
		    if(str.equals("")) str = "&nbsp;";
		    out.println("<td>"+str+"</td>");
		}
		String address = "";
		Rental rental = new Rental(debug, rid);
		back = rental.findAddresses();
		if(back.equals("")){
		    List<String> addresses = rental.getAddresses();
		    for(String addr: addresses){
			if(!address.equals("")) address += "<br />";
			address += addr;
		    }
		}
		out.println("<td>"+address+"</td>");
		str = legal.getReason();
		if(str.equals("")) str = "&nbsp;";
		out.println("<td>"+str+"</td>");
		out.println("</tr>");
	    }
	    out.println("</table>");
	}
	out.println("</div>");
	out.println("</body></html>");
	out.close();
    }

}






















































