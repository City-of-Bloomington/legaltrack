package legals.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;

@WebServlet(urlPatterns = {"/StartLegal"})
public class StartLegal extends TopServlet{
    
    static final long serialVersionUID = 71L;
    static Logger logger = LogManager.getLogger(StartLegal.class);
    final static String bgcolor = "silver";// #bfbfbf gray
    /**
     * Generates the varince form and processes view, add, update and delete
     * operations.
     *
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGet
     * @see #doGet
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String action="",reason="",startDate="",status="",
	    startBy="", startByName="",
	    attention="Legal"; // from HAND to Legal (default)
	//
	HttpSession session = null;
	session = req.getSession(false);
	Connection con = null, con2 = null;
	Statement stmt = null, stmt2 = null;
	ResultSet rs = null, rs2 = null;

	String id="", rental_id="", case_id="", case_type="", message="";
	boolean success = true;
	User user = null;
		
	Enumeration<String> values = req.getParameterNames();
	String[] vals;

	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("rental_id")) {
		rental_id = value;
	    }
	    else if (name.equals("case_id")) {
		case_id = value;
	    }
	    else if (name.equals("reason")) {
		reason = value;
	    }
	    else if (name.equals("startDate")) {
		startDate = value;
	    }
	    else if (name.equals("action")) {
		action = value;
	    }
	}
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
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		success = false;
		message += " could not connect to database";
	    }
	    //
	    // for rental
	    //
	    con2 = Helper.getOraConnection();
	    if(con2 != null){
		stmt2 = con2.createStatement();
	    }
	    else{
		success = false;
		message += " could not connect to rental database";
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    success = false;
	    message = "Could not connect to Database "+ex;
	}
	if(success &&
	   action.equals("Save") && user.canEdit()){
	    //
	    String qq = "", str = "";
	    int cnt = 0;
	    try{
		//
		// check if this rental_id exists
		//
		if(!rental_id.equals("")){
		    qq = "select count(*) from registr where id="+rental_id;
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt2.executeQuery(qq);
		    if(rs.next()){
			cnt = rs.getInt(1);
		    }
		}
		else{
		    message += " Rental record ID is required ";
		    success = false;
		    action = "";
		}
		if(cnt == 0){
		    message += " Rental record ID "+rental_id;
		    message += " does not exist. Please verify and enter ";
		    message += " rental ID again";
		    success = false;
		    action = ""; // to show the form again
		}
		if(success){
		    if(startDate.equals("")){
			startDate = Helper.getToday();
		    }
		    startBy = user.getUserid();
		    qq = "insert into rental_legals values (0,";
		    qq += "str_to_date('"+startDate+"','%m/%d/%Y'),";
		    qq += ""+rental_id+",";
		    if(!reason.equals("")){
			qq += "'"+Helper.escapeIt(reason)+"',";
		    }
		    else{
			qq += "null,";
		    }
		    qq += "'"+startBy+"','New','Legal',"+ //attention
			case_id;
		    qq += ")";
		    if(debug){
			logger.debug(qq);
		    }
		    stmt.executeUpdate(qq);
		    qq = "select LAST_INSERT_ID() ";
		    if(debug)
			logger.debug(qq);
		    rs = stmt.executeQuery(qq);
		    if(rs.next()){
			id = rs.getString(1);
		    }
		    message +=" Saved successfully";
		}
	    }
	    catch(Exception ex){
		success = false;
		message += ex;
		logger.error(ex+":"+qq);
	    }
	}
	if(action.equals("Save") && success){
	    String str = url+"LegalServ?id="+id;
	    res.sendRedirect(str);
	    Helper.databaseDisconnect(con, stmt, rs);
	    Helper.databaseDisconnect(con2, stmt2, rs);
	    return;
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<center><h2>Start Legal (Title 16)</h2> ");
	if(!message.equals("")){
	    if(success)
		out.println(message+"<br />");
	    else
		out.println("<font color='red'>"+message+"</font><br />");
	}
	out.println("<script type='text/javascript'>");
	out.println(" function validateForm(){     ");
	out.println("   var str = document.forms[0].rental_id.value;");
	out.println("   str = str.trim();   ");
	out.println("   if(str.length == 0){ ");
	out.println("     alert('Rental ID is required'); ");
	out.println("     document.forms[0].rental_id.focus(); ");
	out.println("     return false;                        ");
	out.println("    }                                     ");
	out.println("   return true;                           ");
	out.println(" }                                        ");
	out.println("</script>                                 ");
	out.println("<form name=myForm method=\"post\" "+
		    "onsubmit=\"return validateForm()\">");
	out.println("<input type='hidden' name='case_id' value='"+case_id+"'>");
	out.println("<table width=80% border>");
	out.println("<tr><td><table>");
	out.println("<tr><td align='center'>It is assumed that the address(s) and defendnants info are already entered </td></tr>");
	out.println("<tr><td><b>Rental ID:</b>");
	out.println("<input type='text' name='rental_id' size='10' "+
		    " value='"+rental_id+"'>");
	out.println(" (from rentpro) </td></tr>");
	out.println("<tr><td><b>Start Date:</b>");
	out.println("<input type='text' name='startDate' size='10' "+
		    "value='"+startDate+"'>");
	out.println(" (Today if left empty) </td></tr>");
	out.println("<tr><td><b>Reasons </b>(max 500 characters) ");
	out.println("</td></tr>");
	out.println("<tr><td><textarea name='reason' rows='10' cols='60' "+
		    " wrap>");
	out.println(reason);
	out.println("</textarea></td></tr>");
	if(user.canEdit()){
	    out.println("<tr><td align='right'>");
	    out.println("<input type='submit' name='action' value='Save' />");
	    out.println("</td></tr>");
	}
	out.println("</table></td></tr></table>");
	out.println("</center>");
	out.println("</div>");
	out.print("</body></html>");
	out.close();
	Helper.databaseDisconnect(con,stmt,rs);
	Helper.databaseDisconnect(con2,stmt2,rs);
	//
    }

}





















































