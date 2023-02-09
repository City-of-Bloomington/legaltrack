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
 */
@WebServlet(urlPatterns = {"/DefendantView"})
public class DefendantView extends TopServlet{

    static final long serialVersionUID = 38L;
    static Logger logger = LogManager.getLogger(DefendantView.class);
    /**
     * Generates the Defendent form and processes view, add, update and delete
     * operations.
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String f_name = "", l_name="", 
	    dob="",ssn="", did="", id="";

	boolean connectDbOk = false, idFound=false, success=true;
	//
	String message="", action="",entry_time="", 
	    entry_date="";
	String street_num="",street_dir="",
	    street_name="",street_type="",sud_type="",sud_num="",
	    city="Bloomington", state="IN", zip="", invld_addr="",
	    addr_req_date="",pro_supp="", care_of="",
	    street_address="",
	    addr_last_update=""; // not used anymore 

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	User user = null;
	HttpSession session = null;
	DefAddress address = null;
	List<DefAddress> addresses = null;
	Defendant defendant = null;
	session = req.getSession(false);
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
	address = new DefAddress(debug);
	defendant = new Defendant(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    // value = Helper.replaceSpecialChars(value);
	    if (name.equals("did")){
		defendant.setDid(value);
		address.setDefId(did);
		did = value;
	    }
	    else if (name.equals("id")){ // case id
		id = value;
	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	// 
	if(!did.equals("")){	
	    try{
		if(defendant != null){
		    String back = defendant.doSelect();
		    if(back.equals("")){
			addresses = defendant.getAddresses();
		    }
		    else{
			logger.error(back);
			success = false;
			message += "Could not get data "+back;
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
		success = false;
		message += ex;
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	//
	out.println("<div class=\"center\"><h2>");
	if(user.canEdit()){
	    out.println("<a href=\""+url+"Defendent?did="+did+"\">Edit Defendant "+did+"</a>");						
	}
	else{
	    out.println("Defendant "+did);
	}
	out.println("</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("</div>"); 						
	out.println("<fieldset><legend>Defendant Info</legend>");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td align=\"center\">");
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Defendant </td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\" border=\"1\">");
	out.println("<tr><th>First Name</th><th>Last Name</th><th>Care Of");
	out.println("</th></tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println(Helper.replaceSpecialChars(defendant.getF_name()));
	out.println("</td><td class=\"left\">");
	out.println(Helper.replaceSpecialChars(defendant.getL_name()));
	out.println("</td><td class=\"left\">");
	out.println(Helper.replaceSpecialChars(defendant.getCareOfName()));
	out.println("</td></tr>");
	if(user.canEdit()){
	    out.println("<tr><th>D.O.B </th><th>SS#</th><th>DLN</th></tr>");
	    out.println("<tr><td class=\"left\">");	
	    out.println(defendant.getDob());
	    out.println("</td><td class=\"left\">");
	    out.println(defendant.getSsn());
	    out.println("</td><td class=\"left\">");						
	    out.println(defendant.getDln());
	    out.println("</td></tr>");
	}
	out.println("</table></td></tr>");
	if(defendant.hasContactInfo()){
	    out.println("<tr><td>Contact Info</td><td>");
	    out.println(defendant.getContactInfo());
	    out.println("</td></tr>");
	}
	//
	// contact info
	if(addresses == null){
	    addresses = defendant.getAddresses();
	}
	if(addresses != null && addresses.size() > 0){
	    out.println("<tr><td class=\"center title\">Current Address(es)</td></tr>");
	    out.println("<tr><td><table border=\"1\" width=\"100%\">");
	    out.println("<tr><th>Address</th><th>City, State Zip</th><th>Invalid?</th><th>Current Date</th></tr>");
	    for(DefAddress addr: addresses){
		out.println("<tr>");
		out.println("<td class=\"left\">"+addr.getAddress()+"</td>");
		out.println("<td class=\"left\">"+addr.getCityStateZip()+"</td>");
		out.println("<td class=\"left\">"+addr.getInvalid_addr()+"</td>");
		out.println("<td class=\"left\">"+addr.getAddrDate()+"</td>");
		out.println("</tr>");
	    }
	    out.println("</table></td></tr>");
	}
	//
	if(!defendant.getAddr_req_date().equals("")){
	    out.println("<tr><th>Address Request Update Date: ");
	    out.println(defendant.getAddr_req_date()+"</th></tr>");
	}
	out.println("</table></td></tr>");
	//
	List<Case> cases = null;
	CaseList cl = new CaseList(debug, did);
	String back = cl.lookFor();
	if(!back.equals("")){
	    message += back;
	    logger.error(back);
	}
	else{
	    cases = cl.getCases();
	}
	if(cases != null && cases.size() > 0){
	    out.println("<table width=\"90%\"><tr><td align=\"center\">");
	    out.println("<table width=\"50%\" border=\"1\">");
	    out.println("<caption>Defendant Related Cases</caption>");
	    out.println("<tr>"+
			"<th>Case ID</th>"+
			"<th>Received</th>"+
			"<th>Status</th></tr>");
	    for(int i=0;i<cases.size();i++){
		Case cc = cases.get(i);
		if(cc != null){
		    out.println("<tr>");
		    out.println("<td><a href=\""+url+
				"CaseView?id="+cc.getId()+"\">"+
				cc.getId()+"</a></td>");
		    out.println("<td>"+cc.getReceived()+
				"</td>");
		    out.println("<td>"+cc.getCStatus()+
				"</td>");
		    out.println("</tr>");
		}
	    }
	    out.println("</table>");
	    out.println("</td></tr></table>");
	}
	out.print("</fieldset>");
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































