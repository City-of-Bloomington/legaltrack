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
/**
 * 
 *
 */
@WebServlet(urlPatterns = {"/Defendent","/Defendant"})
public class DefendantServ extends TopServlet{

    static final long serialVersionUID = 38L;
    static Logger logger = LogManager.getLogger(DefendantServ.class);
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
	String [] del_addr = null;
	String [] del_care_ofs = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    // value = Helper.replaceSpecialChars(value);
	    if (name.equals("f_name")) {
		defendant.setF_name(value);
	    }
	    else if (name.equals("l_name")) {
		defendant.setL_name(value);
	    }
	    else if (name.equals("dob")) {
		defendant.setDob(value);
	    }
	    else if (name.equals("ssn")) {
		defendant.setSsn(value);
	    }
	    else if (name.equals("dln")) {
		defendant.setDln(value);
	    }
	    else if (name.equals("phone")) {
		defendant.setPhone(value);
	    }
	    else if (name.equals("phone_2")) {
		defendant.setPhone_2(value);
	    }
	    else if (name.equals("email")) {
		defendant.setEmail(value);
	    }			
	    else if (name.equals("did")){
		defendant.setDid(value);
		address.setDefId(did);
		did = value;
	    }
	    else if (name.equals("id")){ // case id
		id = value;
	    }
	    else if (name.equals("city")){
		address.setCity(value);
	    }
	    else if (name.equals("state")){
		address.setState(value);
	    }
	    else if (name.equals("zip")){ 
		address.setZip(value);
	    }
	    else if (name.equals("street_num")) {
		address.setStreet_num(value);
	    }
	    else if (name.equals("street_address")) {
		address.setStreet_address(value);
	    }						
	    else if (name.equals("street_dir")) {
		address.setStreet_dir(value);
	    }
	    else if (name.equals("street_name")) {
		address.setStreet_name(value);
	    }
	    else if (name.equals("street_type")) {
		address.setStreet_type(value);
	    }
	    else if (name.equals("post_dir")) {
		address.setPost_dir(value);
	    }			
	    else if (name.equals("sud_type")) {
		address.setSud_type(value);
	    }
	    else if (name.equals("sud_num")) {
		address.setSud_num(value);
	    }
	    else if (name.equals("invld_addr")) {
		invld_addr = value;
		address.setInvalid_addr(value);
	    }
	    else if (name.equals("addr_req_date")) {
		defendant.setAddr_req_date(value);
	    }
	    else if (name.equals("addr_date")) {
		address.setAddrDate(value);
	    }
	    else if (name.equals("del_addr")){ 
		del_addr = vals;  
	    }
	    else if (name.equals("care_of_id")){ 
		defendant.setCareOfId(value); 
	    }
	    else if (name.equals("care_of_name")){ 
		defendant.setCareOfName(value); 
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    defendant.setAddress(address);
	    String qq = "";
	    try{
		//
		// check if the defendant is in the system already
		//
		DefendantList dList = new DefendantList(defendant, debug);
		String back = dList.lookForExact();
		if(back.equals("")){
		    List<Defendant> defs = dList.getDefendants();
		    if(defs != null && defs.size() > 0){
			defendant = defs.get(0); // we need one
			did = defendant.getDid(); // a sigm that def exists
		    }
		}
		else{
		    logger.error(back);
		    message += back;
		    success = false;
		}
		if(!did.equals("")){
		    message = " This defendant is already in the system <br>";
		    message += " with <a href="+url+"Defendent?did="+did+
			"&action=zoom";
		    if(!id.equals(""))
			message += "&id="+id;
		    message +=">"+did+"</a><br>";
		    success = false;
		}
		else{
		    back = defendant.doSave();
		    if(!back.equals("")){
			logger.error(back);
			message += back;
			success = false;	
		    }
		    else{ // success
			did = defendant.getDid();
		    }
		}
		if(!care_of.equals("")){
		    defendant.addCareOf(care_of);
		}
		//
		// in both cases we enter the (did, id) combo 
		//
		if(!id.equals("") && !did.equals("")){
		    back = defendant.addCaseToDefendant(id);
		    if(!back.equals("")){
			message+="<br />";
			message += back;
		    }
		    else{
			message += "Saved Successfully";
		    }
		}
	    }
	    catch(Exception ex){
		success = false;
		logger.error(ex+" : "+qq);
		if(!message.equals(""))
		    message+="<br />";
		message += ex;
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String str="";
	    try{
		String back = defendant.doUpdate();
		if(!back.equals("")){
		    logger.error(back);
		    message += back;
		    success = false;	
		}
		if(address != null && address.isValid()){
		    back = defendant.addAddress(address);
		    logger.error(back);
		    message += back;
		    success = false;
		}
		if(del_addr != null){
		    back = defendant.deleteAddresses(del_addr);
		    if(!back.equals("")){
			message += back;
			success = false;
		    }
		}
		addresses = defendant.getAddresses();
		message += " Updated Successfully";
	    }
	    catch(Exception ex){
		success = false;
		logger.error(ex);
		if(!message.equals(""))
		    message+="<br />";
		message += ex;
	    }
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    try{
		if(defendant != null){
		    String back = defendant.doDelete();
		    if(back.equals("")){
			message += " Deleted Successfully ";
			did = "";
			defendant = new Defendant(debug);
		    }
		    else{
			message += back;
			logger.error(back);
			success = false;
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
		success = false;
		message += ex;
	    }
	}
	else if(!did.equals("")){	
	    //
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
	// Sending email
	//
	if(action.equals("Save") || action.equals("Update")){
	    if(user.canEdit() && activeMail){
		if(address.isValid() && invld_addr.equals("")){
		    try{
			if(defendant.defHasParkingCase()){
			    String back = defendant.sendEmailToParking(user,
								       parkingUser,
								       emailStr,
								       true);
			    if(!back.equals("")){
				message += " Could not send email "+back;
				success = false;
			    }
			    else{
				if(!message.equals(""))
				    message+="<br />";
				message +=" Email sent regarding invalid address.";
			    }
			}
		    }catch(Exception ex){
			message += ex;
		    }
		}
	    }
	}
	//
	// if(!invld_addr.equals("")) invld_addr = "checked";
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");
	out.println("  function validateForm(){	                     ");
	out.println("  with(document.myForm){                        ");
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("if(!checkDate(dob))return false;                ");
	out.println("if(!checkDate(addr_req_date))return false;      ");
	out.println("if(!checkDate(addr_date))return false;   ");
	out.println("  return true;			        ");
	out.println(" }}	         		        ");
	out.println("  function validateDelete2(){	        ");
	out.println("   var x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   if(x){                       ");
	out.println("    document.forms[0].action2.value=\"Delete\"; ");
	out.println("    document.forms[0].submit();   ");
	out.println("    return true;                    ");
	out.println("	}	             		     ");
	out.println("  }	             		     ");			
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
	// delete startNew
	//
	out.println("<div class=\"center\">");
	if(did.equals("")){
	    out.println("<h2>New Defendant</h2>");
	}
	else { 
	    out.println("<h2>View/Edit Defendant "+did +"</h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"Defendent?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset><legend>Defendant Info</legend>");
	if(!did.equals("")){
	    out.println("<input type=\"hidden\" name=\"did\" value=\""+did+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	    if(defendant.hasCareOf()){
		out.println("<input type=\"hidden\" name=\"care_of_id\" value=\""+defendant.getCareOfId()+"\" />");
	    }
	}
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td class=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td class=\"center title\">");
	out.println("Defendant </td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<tr><th>First Name</th><th>Last Name</th><th>Care Of");
	out.println("</th></tr>");
	out.println("<tr>");
	out.println("<td class=\"left\">");
	out.println("<input name=\"f_name\" "+
		    " size=\"20\" maxlength=\"30\" value=\""+
		    Helper.replaceSpecialChars(defendant.getF_name())+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"l_name\" size=\"20\" maxlength=\"30\" "+
		    " value=\""+
		    Helper.replaceSpecialChars(defendant.getL_name())+
		    "\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"care_of_name\" size=\"20\" maxlength=\"30\""+
		    " value=\""+
		    Helper.replaceSpecialChars(defendant.getCareOfName())+
		    "\" /></td>");
	out.println("</tr>");
	out.println("<tr><th>D.O.B </th><th>SS#</th><th>DLN</th></tr>");
	out.println("<tr><td class=\"left\">");	
	out.println("<input name=\"dob\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+defendant.getDob()+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"ssn\" size=\"10\" maxlength=\"10\" "+
		    " value=\""+defendant.getSsn()+"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<input name=\"dln\" size=\"15\" maxlength=\"20\" "+
		    " value=\""+defendant.getDln()+"\" />");
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td class=\"center title\">Contact Info</td></tr>");
	//
	// contact info
	out.println("<tr><td><table>");
	out.println("<tr><th>Phone #</th><td class=\"left\">");
	out.println("<input name=\"phone\" size=\"12\" maxlength=\"20\" value=\""+defendant.getPhone()+"\" /></td>");
	out.println("<th>Phone 2 #</th><td class=\"left\">");
	out.println("<input name=\"phone_2\" size=\"12\" maxlength=\"20\" value=\""+defendant.getPhone_2()+"\" /></td>");
	out.println("<th>Email</th><td class=\"left\">");
	out.println("<input name=\"email\" size=\"30\" maxlength=\"50\" value=\""+defendant.getEmail()+"\" /></td>");		
	out.println("</tr></table></td></tr>");
	if(!did.equals("")){
	    if(addresses == null){
		addresses = defendant.getAddresses();
	    }
	    if(addresses != null && addresses.size() > 0){
		out.println("<tr><td class=\"center title\">Current Address(es)</td></tr>");
		out.println("<tr><td><table border=\"1\" width=\"100%\">");
		out.println("<tr><th>Delete</th><th>Address</th><th>City, State Zip</th><th>Invalid?</th><th>Current Date</th><th>Edit</th></tr>");
		for(DefAddress addr: addresses){
		    out.println("<tr><td class=\"left\">");
		    out.println("<input type=\"checkbox\" name=\"del_addr\" value=\""+addr.getId()+"\" /></td>");
		    out.println("<td class=\"left\">"+addr.getAddress()+"</td>");
		    out.println("<td class=\"left\">"+addr.getCityStateZip()+"</td>");
		    out.println("<td class=\"left\">"+addr.getInvalid_addr()+"</td>");
		    out.println("<td class=\"left\">"+addr.getAddrDate()+"</td>");
		    out.println("<td class=\"left\"><a href=\""+url+"DefAddressEdit?id="+addr.getId()+"&amp;defId="+did+"&amp;action=zoom\">Edit</a></td>");
		    out.println("</tr>");
		}
		out.println("<tr><td colspan=\"6\"><span class=\"green\">If a parking case and the address is modified or invalid, parking will be notified atuomatically</span> </td></tr>");
		out.println("</table></td></tr>");
	    }
	}
	//
	// Address table
	out.println("<tr><td><table><caption>Add New Address</caption>");
	/*
	  out.println("<td class=\"left\" colspan=\"2\"><input name=\"street_address\" size=\"50\" "+
	  " maxlength=\"70\" value=\"\" /></td></tr>");
	*/
	out.println("<tr><th>St. No. </th><th>Dir</th><th>"+
		    "St. Name</th><th>St. Type</th><th>Post Dir</th></tr>");
	out.println("<tr>");
	out.println("<td class=\"left\"><input name=\"street_num\" size=\"8\" "+
		    " maxlength=\"8\" value=\"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<select name=\"street_dir\">");
	for(int i=0;i<Inserts.dirArr.length;i++){
	    out.println("<option>"+Inserts.dirArr[i]+"</option>");
	}
	out.println("</select></td><td class=\"left\">");
	out.println("<input name=\"street_name\" size=\"20\" maxlength=\"20\" value=\"\" />");
	out.println("</td><td class=\"left\">");
	out.println("<select name=\"street_type\">");
	//					
	for(int i=0;i<Inserts.streetKeys.length;i++){
	    out.println("<option value=\""+
			Inserts.streetKeys[i]+"\">"+Inserts.streetInfo[i]+"</option>");
	}
	out.println("</select>");
	out.println("</td><td>");
	out.println("<select name=\"post_dir\">");
	for(int i=0;i<Inserts.dirArr.length;i++){
	    out.println("<option>"+Inserts.dirArr[i]+"</option>");
	}
	out.println("</select></td></tr>");
	out.println("<tr><th>Sud Type</th><th>Sud Num</th>");
	out.println("<th>City</th><th>State</th><th>Zip</th></tr>");		
	//
	out.println("<tr><td class=\"left\">");
	out.println("<select name=\"sud_type\">");
	//					
	for(int i=0;i<Inserts.sudKeys.length;i++){
	    out.println("<option value=\""+
			Inserts.sudKeys[i]+"\">"+Inserts.sudInfo[i]+
			"</option>");
	}		
	out.println("</select></td><td class=\"left\">");
	out.println("<input name=\"sud_num\" size=\"8\" "+
		    "maxlength=\"8\" value=\"\" /></td>");
	out.println("<tr><td class=\"left\">City:");
	out.println("<input name=\"city\" size=\"20\" "+
		    "maxlength=\"30\" value=\"Bloomington\" /></td><td class=\"left\">");
	out.println("State:<input name=\"state\" size=\"2\" "+
		    "maxlength=\"2\" value=\"IN\" /></td><td class=\"left\">");
	out.println("Zip:<input name=\"zip\" size=\"10\" "+
		    "maxlength=\"14\" value=\"4740\" /></td></tr>");
	out.println("<tr><th>Address Validity?</th><td class=\"left\">"+
		    "<input type=\"checkbox\" name=\"invld_addr\" "+
		    " value=\"y\" />Invalid </td><td></td>"+
		    "<th>Current Date:</th>");
	out.println("<td class=\"left\">"); // replaces old addr_update_date 
	out.println("<input name=\"addr_date\" size=\"10\" maxlength=\"10\" "+
		    " value=\"\" class=\"date\" /></td></tr>");		
	out.println("</table></td></tr>");
	//
	// address request update not used any more (used to check post office)
	//
	out.println("<tr><td><table>");
	out.println("<tr><th> Address Request Update Date:</th><td class=\"left\">");
	out.println("<input name=\"addr_req_date\" size=\"10\" class=\"date\" "+
		    " maxlength=\"10\" value=\""+defendant.getAddr_req_date()+
		    "\" /></td></tr>");
	out.println("</table></td></tr>");
	//
	if(did.equals("")){
	    out.println("</table></td></tr>");							
	    if(user.canEdit()){
		out.println("<tr><td class=\"center\"><input type=\"submit\" "+
			    "accesskey=\"s\" name=\"action\" value=\"Save\" />");
		out.println("</td></tr>");
	    }
	    out.println("</table>");
	    out.println("</fieldset>");			
	    out.println("</form>");
	}
	else{ // Save, Update
	    out.println("<tr><td valign=\"top\" class=\"center\">");
	    out.println("<table width=\"100%\" border=\"1\"><tr><td>");
	    out.println("<table width=\"100%\">");	
	    out.println("<tr>");
	    if((user.canEdit() && user.isInDept("Legal")) ||
	       user.isAdmin()){
		out.println("<td><input accesskey=\"u\" "+
			    "type=\"submit\" name=\"action\" value=\"Update\" />");
		out.println("</td>");
	    }
	    out.println("<td>");
	    out.println("<input type=\"button\" name=\"action\" "+
			"onclick=\"document.location='"+url+
			"CaseServ?did="+did+"'\" "+
			"accesskey=\"n\" "+
			" value=\"Add New Case\" />");
	    out.println("</td>");
	    if(user.canDelete()){
		out.println("<td>");
		out.println("<input type=\"button\" "+
			    " accesskey=\"e\" "+
			    " onclick=\"validateDelete2();\" "+
			    " value=\"Delete\" />");
		out.println("</td>");
	    }
	    out.println("</tr></table></td></tr>");
	    out.println("</table></td></tr>");			
	    out.println("<tr><td class=\"cener\">");
	    out.println("<span class=\"green\">Access keys: Alt +"+
			" U=Update, N=Add New Case, E=Delete"+
			"</span>"+
			"</td></tr>");
	    out.println("</table>");
	    out.println("</td></tr></table>");	
	    out.println("</fieldset>");
	    out.println("</form>");
	}

	if(!did.equals("")){
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
				    "CaseServ?"+
				    "id="+cc.getId()+"\">"+
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
	    else{
		if(!message.equals("")){
		    out.println("<p>"+message+"</p>");
		}
	    }
	}
	out.print("<br /><br />");
	out.println(Inserts.jsStrings(url));		
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
		

	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");				
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































