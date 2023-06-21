package legals.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
import legals.list.*;

@WebServlet(urlPatterns = {"/AddressEdit"})
public class AddressEdit extends TopServlet{

    final static String bgcolor = "silver";// #bfbfbf gray
    static final long serialVersionUID = 15L;
    static Logger logger = LogManager.getLogger(AddressEdit.class);	
    //
    /**
     * Generates the edit address form.
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
     * Generates the edit address form.
     * operations.
     * @param req
     * @param res
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String action="",address="",street_num="",access="";
	String street_name="", street_dir="", street_type="", post_dir="";
	String sud_num="", sud_type="",
	    street_address="",
	    invalid_addr="", rental_addr="",
	    message="";
	//
	HttpSession session = null;
	session = req.getSession(false);
	String id="", case_id="";

	boolean success = true;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	Address addr = null;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    //
	    if (name.equals("id")) {
		id = value;
	    }
	    else if (name.equals("case_id")) {
		case_id = value;
	    }
	    else if (name.equals("action")) {
		action = value;
		if(action.equals("New")) action = "";
	    }
	    else if (name.equals("street_address")) {
		street_address =value;
	    }
	    else if (name.equals("street_num")) {
		street_num =value;
	    }
	    else if (name.equals("street_dir")) {
		street_dir =value;
	    }
	    else if (name.equals("street_name")) {
		street_name =value.toUpperCase();
	    }
	    else if (name.equals("street_type")) {
		street_type =value;
	    }
	    else if (name.equals("post_dir")) {
		post_dir =value;
	    }
	    else if (name.equals("sud_num")) {
		sud_num = value;  
	    }
	    else if (name.equals("sud_type")) {
		sud_type = value; 
	    }
	    else if (name.equals("invalid_addr")) {
		invalid_addr = value; 
	    }
	    else if (name.equals("rental_addr")) {
		rental_addr = value; 
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
	if(!invalid_addr.equals("")) invalid_addr = "Y";
	if(action.equals("Save") && user.canEdit()){
	    //
	    // check for address
	    //
	    addr = new Address(debug);
	    addr.setCase_id(case_id);
	    addr.setStreet_address(street_address);

	    addr.setStreet_num(street_num);
	    addr.setStreet_dir(street_dir);
	    addr.setStreet_name(street_name);
	    addr.setStreet_type(street_type);
	    addr.setSud_type(sud_type);
	    addr.setSud_num(sud_num);
	    addr.setRental_addr(rental_addr);
	    addr.setInvalid_addr(invalid_addr);
	    String back = addr.doSave();
	    if(back.equals("")){
		id = addr.getId();
		message += "Saved successfully";
	    }
	    else{
		message += back;
		logger.error(back);
		success = false;
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    // check for address
	    //
	    addr = new Address(debug);
	    addr.setId(id);
	    addr.setCase_id(case_id);
	    addr.setStreet_address(street_address);

	    addr.setStreet_num(street_num);
	    addr.setStreet_dir(street_dir);
	    addr.setStreet_name(street_name);
	    addr.setStreet_type(street_type);
	    addr.setSud_type(sud_type);
	    addr.setSud_num(sud_num);
	    addr.setRental_addr(rental_addr);
	    addr.setInvalid_addr(invalid_addr);
	    // check if modified
	    addr.isValid(addrCheckUrl);
	    invalid_addr = addr.getInvalid_addr();
	    String back = addr.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
		logger.error(back);
	    }
	    else{
		message += "Updated successfully";
	    }
	}
	else if(action.equals("Delete") && user.canEdit()){
	    addr = new Address(debug);
	    addr.setId(id);
	    String back = addr.doDelete();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
		success = false;
	    }
	    else{
		message += " Deleted Successfully";
	    }
	}
	else if(!id.isEmpty()){
	    //
	    addr = new Address(debug, id);
	    String back = addr.doSelect();
	    if(back.equals("")){
		street_num = addr.getStreet_num();
		street_dir = addr.getStreet_dir();
		street_type = addr.getStreet_type();
		street_name = addr.getStreet_name();
		sud_num = addr.getSud_num();
		sud_type = addr.getSud_type();
		post_dir = addr.getPost_dir();
		street_address = addr.getStreet_address();
		case_id = addr.getCase_id();
								
		invalid_addr = addr.getInvalid_addr();
		rental_addr = addr.getRental_addr();
	    }
	    else{
		logger.error(back);
		message += back;
		success = false;
	    }
	}
	else{ // new and ""
	    street_num="";street_dir="";street_name="";street_type="";
	    post_dir="";sud_num="";sud_type="";invalid_addr=""; id="";
	    address = "";street_address="";
	}
	if(!invalid_addr.equals("")) invalid_addr="checked=\"checked\"";
	if(!rental_addr.equals("")) rental_addr="checked=\"checked\"";
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateForm(){		                ");
	out.println("  if ((document.myForm.street_address.value.length == 0)){ "); 
	out.println("     alert(\"Street name is needed\" );   ");
	out.println("    return false;					    ");
	out.println("	}						    ");
	out.println("    return true ;					    ");
	out.println(" }				    ");
	out.println("  function firstFocus(){		                ");
	out.println(" document.myForm.street_address.focus();     ");
	out.println(" }				    ");
	out.println("  function validateDelete(){	                 ");
	out.println("   var x = false;                                   ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     return x;                                      ");
	out.println("	}					         ");
	out.println("</script>			    ");
	out.println("</head><body><center>");
	out.println("<h2>Edit Address</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3><font color=green> "+message+
			    "</font></h3>");
	    else
		out.println("<h3><font color=red>"+message+
			    "</font></h3>");
			
	}
	out.println("<form name=myForm method=post >");
	out.println("<table width=80% border><tr><td>");
	out.println("<table width=100%>");
	out.println("<tr><th>Case ID:</th><td align=left>"+case_id);
	out.println("&nbsp;&nbsp;");
	if(!invalid_addr.equals("")){
	    out.println("<input type=hidden name=invalid_addr "+ 
			" value=Y /><font color=red>This is Invalid Address</font>");
	}
	out.println("</td></tr>");
	out.println("<tr><td>&nbsp;</td><td>");
	out.println("<input type=checkbox name=rental_addr "+rental_addr+
		    " value=Y>This is a rental address");
	out.println("</td></tr>");	
	//
	/*
	  out.println("<tr><th>Street Address:</th><td align=left>");
	  out.println("<input name=\"street_address\" size=\"50\" maxlength=\"50\" value=\""+
	  street_address+"\" />");
	*/
	out.println("</td></tr>");
	// st num
	out.println("<tr><th>Street Num:</th><td align=left>");
	out.println("<input name=street_num size=6 maxlength=6 value="+
		    street_num+">");
	out.println("</td></tr>");
	out.println("<tr><th>Street Dir:</th><td align=left>");
	out.println("<select name=street_dir>");
	out.println("<option>\n");
	for(int i=0;i<Helper.dirArr.length;i++){
	    if(street_dir.equals(Helper.dirArr[i]))
		out.println("<option selected>"+street_dir);
	    else
		out.println("<option>"+Helper.dirArr[i]);
	}
	out.println("</select></td></tr>");
	out.println("<tr><th>Street Name:</th><td align=left>");
	out.println("<input type=text name=street_name maxlength=20 value=\""+
		    street_name+"\" size=20>");
	out.println("</td></tr>");
	//
	// st type
	out.println("<tr><th>Street Type:</th><td align=left>");
	out.println("<select name=street_type>");
	out.println("<option selected value=\"\">\n");
	for(int i=0; i<Helper.strArr.length; i++){
	    if(Helper.strIdArr[i].equals(street_type)){
		out.println("<option selected value=\""+street_type+
			    "\">"+Helper.strArr[i]);
	    }
	    else{
		out.println("<option value=\""+Helper.strIdArr[i]+
			    "\">"+Helper.strArr[i]);
	    }
	}
	//
	// post dir
	out.println("<tr><th>Post Dir:</th><td align=left>");
	out.println("<select name=post_dir>");
	out.println("<option>\n");
	for(int i=0;i<Helper.dirArr.length;i++)
	    if(post_dir.equals(Helper.dirArr[i]))
		out.println("<option selected>"+post_dir);
	    else
		out.println("<option>"+Helper.dirArr[i]);
	out.println("</select></td></tr>");
	//
	// sud type
	out.println("<tr><th>Sud Type:</th><td align=left>");
	out.println("<select name=sud_type>");
	out.println("<option value=\"\">\n");
	for(int i=0; i<Inserts.sudKeys.length; i++){
	    if(sud_type.equals(Inserts.sudKeys[i]))
		out.println("<option selected value=\""+sud_type+"\">"+
			    Inserts.sudInfo[i]);
	    else
		out.println("<option value=\""+Inserts.sudKeys[i]+"\">"+
			    Inserts.sudInfo[i]);
	}
	out.println("</select></td></tr>");
	//
	// sud num
	out.println("<tr><th>Sud Num:</th><td align=left>");
	out.println("<input type=text name=sud_num maxlength=4 size=4 value=\""+
		    sud_num+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Invalid Address</th><td align=left>");
	out.println("<input type=checkbox name=invalid_addr value=Y "+
		    invalid_addr+" /></td></tr>");
	out.println("</table></td></tr>");
	out.println("<input type=hidden name=case_id value="+case_id+">");
	if(!id.equals(""))
	    out.println("<input type=hidden name=id value="+id+">");
	//
	if(action.equals("") || action.startsWith("New")||
	   action.equals("Delete")){
	    if(user.canEdit()){
		out.println("<tr><td align=right>  "+
			    "<input type=submit "+
			    "name=action "+
			    "value=Save>&nbsp;&nbsp;&nbsp;"+
			    "</td></tr>");
	    }
	}
	else {  // save, update, zoom
	    out.println("<tr><td><table width=100%><tr><td valign=top "+
			"align=right>If you've made any changes click on");
	    out.println("<input type=submit name=action "+
			"value=Update>&nbsp;&nbsp;");
	    out.println("<input type=submit name=action "+
			"value=New></td>");
	    out.println("</form>");
	    if(user.canEdit()){
		out.println("<td valign=top align=right>");				
		out.println("<form name=delForm method=post "+
			    "onSubmit=\"return validateDelete()\">");
		out.println("<input type=hidden name=id value="+id+">");
		out.println("<input type=hidden name=case_id value="+case_id+">");
		out.println("<input type=submit name=action "+
			    "value=Delete>");
		out.println("</form></td>");				
	    }
	    out.println("</tr></table></td></tr>");
	}
	out.println("</table>");
	out.println("<li><a href="+url+"CaseServ?id="+case_id+
		    "&action=zoom>Back to Related Case</a>");
	out.print("<br>");
	out.println("</div>");
	out.println("</body></html>");
	out.flush();
	// 
	out.close();
    }

}






















































