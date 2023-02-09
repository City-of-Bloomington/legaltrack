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
/**
 * 
 *
 */
@WebServlet(urlPatterns = {"/DefAddressEdit"})
public class DefAddressEdit extends TopServlet{

    static final long serialVersionUID = 34L;
    final static String bgcolor = "silver";// #bfbfbf gray
    static Logger logger = LogManager.getLogger(DefAddressEdit.class);	
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
	String message="";
	HttpSession session = null;
	session = req.getSession(false);
	String id="", invalid_addr="", defId="", action="";

	boolean success = true;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	DefAddress addr = new DefAddress(debug);
	Defendant defendant = null;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    //
	    if (name.equals("id")) {
		id = value;
		addr.setId(id);
	    }
	    else if (name.equals("defId")) {
		addr.setDefId(value);
		defId = value;
	    }
	    else if (name.equals("action")) {
		action = value;
		if(action.equals("New")) action = "";
	    }
	    else if (name.equals("street_num")) {
		addr.setStreet_num(value);
	    }
	    else if (name.equals("street_dir")) {
		addr.setStreet_dir(value);				
	    }
	    else if (name.equals("street_name")) {
		addr.setStreet_name(value);
	    }
	    else if (name.equals("street_address")) {
		addr.setStreet_address(value);
	    }						
	    else if (name.equals("street_type")) {
		addr.setStreet_type(value);				
	    }
	    else if (name.equals("post_dir")) {
		addr.setPost_dir(value);				
	    }
	    else if (name.equals("sud_num")) {
		addr.setSud_num(value);				
	    }
	    else if (name.equals("sud_type")) {
		addr.setSud_type(value);				
	    }
	    else if (name.equals("invalid_addr")) {
		addr.setInvalid_addr(value);
	    }
	    else if (name.equals("addr_date")) {
		addr.setAddrDate(value);
	    }
	    else if (name.equals("city")) {
		addr.setCity(value);
	    }
	    else if (name.equals("state")) {
		addr.setState(value);
	    }
	    else if (name.equals("zip")) {
		addr.setZip(value);				
	    }			
	}

	User user = null;
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
	if(!defId.equals("")){
	    defendant = new Defendant(defId, debug);
	    String back = defendant.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
		logger.error(back);
	    }
	}
	if(action.equals("zoom")){
	    //
	    String back = addr.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    if(addr.isInvalid()){
		invalid_addr = "checked";
	    }
	    if(defId.equals("")){
		defId = addr.getDefId();
		defendant = new Defendant(defId, debug);
		back = defendant.doSelect();
		if(!back.equals("")){
		    message += back;
		    success = false;
		    logger.error(back);
		}				
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    // check for address
	    //
	    boolean addrModified = false;
	    if(addr.isModified() && addr.isValid()){
		addrModified = true;
	    }
	    String back = addr.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
		logger.error(back);
	    }
	    else{
		if(addrModified && defendant != null){
		    //
		    // we do not need to check for case type
		    // because that is already done when presenting
		    // the form
		    //
		    // we want parking to be informed
		    //
		    defendant.setAddress(addr);
		    try{
			if(defendant.defHasParkingCase()){
			    back =
				defendant.sendEmailToParking(user,
							     parkingUser,
							     emailStr,
							     false); // an old addr
			    if(back.equals("")){
				message+="<br />";
				message += " Email sent regarding modified address. ";
			    }
			}
		    }
		    catch(Exception ex){};
		}
		message += "Updated successfully";
	    }
	}
	else if(action.equals("Delete") && user.canEdit()){
	    String back = addr.doDelete();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
		success = false;
	    }
	    else{
		message += " Deleted Successfully";
		id = "";
	    }
	}
	if(!invalid_addr.equals("")){
	    invalid_addr = "checked";
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateForm(){		                ");
	out.println("  if ((document.myForm.street_name.value.length == 0)){ "); 
	out.println("     alert(\"Street name is needed\" );   ");
	out.println("    return false;					    ");
	out.println("	}						    ");
	out.println("    return true ;					    ");
	out.println(" }				    ");
	out.println("  function firstFocus(){		                ");
	out.println(" document.myForm.street_num.focus();     ");
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
	out.println("<tr><td>Defendant ID:</td><td>"+defId);
	out.println("&nbsp;&nbsp;");
	if(addr.isInvalid()){
	    out.println("<input type=hidden name=invalid_addr "+ 
			" value=Y /><font color=red>This is Invalid Address</font>");
	}
	out.println("</td></tr>");
	//
	out.println("</table></td></tr>");
	out.println("<tr><td>");
	out.println("<table><caption>Address</caption>");
	out.println("<tr><th>Street Num:</th><td align=left>");
	out.println("<input name=street_num size=6 maxlength=6 value="+
		    addr.getStreet_num()+">");
	out.println("</td></tr>");
	out.println("<tr><th>Street Dir:</th><td align=left>");
	out.println("<select name=street_dir>");
	out.println("<option>\n");
	for(int i=0;i<Helper.dirArr.length;i++){
	    if(addr.getStreet_dir().equals(Helper.dirArr[i]))
		out.println("<option selected>"+addr.getStreet_dir());
	    else
		out.println("<option>"+Helper.dirArr[i]);
	}
	out.println("</select></td></tr>");
	out.println("<tr><th>Street Name:</th><td align=left>");
	out.println("<input type=text name=street_name maxlength=20 value=\""+
		    addr.getStreet_name()+"\" size=20>");
	out.println("</td></tr>");
	//
	// st type
	out.println("<tr><th>Street Type:</th><td align=left>");
	out.println("<select name=street_type>");
	out.println("<option selected value=\"\">\n");
	for(int i=0; i<Helper.strArr.length; i++){
	    if(Helper.strIdArr[i].equals(addr.getStreet_type())){
		out.println("<option selected value=\""+addr.getStreet_type()+
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
	    if(addr.getPost_dir().equals(Helper.dirArr[i]))
		out.println("<option selected>"+addr.getPost_dir());
	    else
		out.println("<option>"+Helper.dirArr[i]);
	out.println("</select></td></tr>");
	//
	// sud type
	out.println("<tr><th>Sub Unit Type:</th><td align=left>");
	out.println("<select name=sud_type>");
	out.println("<option value=\"\">\n");
	for(int i=0; i<Inserts.sudKeys.length; i++){
	    if(addr.getSud_type().equals(Inserts.sudKeys[i]))
		out.println("<option selected value=\""+addr.getSud_type()+"\">"+
			    Inserts.sudInfo[i]);
	    else
		out.println("<option value=\""+Inserts.sudKeys[i]+"\">"+
			    Inserts.sudInfo[i]);
	}
	out.println("</select></td></tr>");
	//
	// sud num
	out.println("<tr><th>Sub Unit Num:</th><td align=left>");
	out.println("<input type=text name=sud_num maxlength=4 size=4 value=\""+
		    addr.getSud_num()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>City:</th><td align=left>");
	out.println("<input type=text name=city maxlength=30 size=30 value=\""+
		    addr.getCity()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>State:</th><td align=left>");
	out.println("<input type=text name=state maxlength=2 size=2 value=\""+
		    addr.getState()+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th>Zip Code:</th><td align=left>");
	out.println("<input type=text name=zip maxlength=10 size=10 value=\""+
		    addr.getZip()+"\" />");
	out.println("</td></tr>");
		
	// if not local we allow to update this
	if(addr.isInvalid()) invalid_addr="checked=\"checked\"";		
	out.println("<tr><th>Invalid Address?</th><td align=left>");
	out.println("<input type=checkbox name=invalid_addr value=\"Y\" "+invalid_addr+" />");
	out.println("</td></tr>");
	out.println("<tr><th>Address Date:</th><td align=left>");
	out.println("<input type=text name=addr_date maxlength=10 size=10 value=\""+
		    addr.getAddrDate()+"\" />");
	out.println("</td></tr>");	
	out.println("</table></td></tr>");
	out.println("<input type=hidden name=defId value="+defId+">");
	if(!id.equals(""))
	    out.println("<input type=hidden name=id value="+id+">");
	//
	if(id.equals("")){
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
			"value=Update>&nbsp;&nbsp;</td>");
	    out.println("</form>");
	    if(user.canEdit()){
		out.println("<td valign=top align=right>");				
		out.println("<form name=delForm method=post "+
			    "onSubmit=\"return validateDelete()\">");
		out.println("<input type=hidden name=id value="+id+">");
		out.println("<input type=hidden name=defId value="+defId+">");
		out.println("<input type=submit name=action "+
			    "value=Delete>");
		out.println("</form></td>");				
	    }
	    out.println("</tr></table></td></tr>");
	}
	out.println("</table>");
	if(defendant != null){
	    List<DefAddress> addrs = defendant.getAddresses();
	    if(addrs != null && addrs.size() > 0){
		out.println("<table border>");
		out.println("<caption> Address(es)</caption>");
		out.println("<tr><th>Address</th>"+
			    "<th>City, State Zip</th>"+
			    "<th>Invalid?</th>"+
			    "<th>Edit</th></tr>");
		for(DefAddress adr:addrs){
		    out.println("<tr><td align=left>"+adr.getAddress());
		    out.println("</td><td align=left>"+adr.getCityStateZip());	
		    out.println("</td><td align=left>"+adr.getInvalid_addr());
		    out.println("</td><td align=left><a href=\""+url+"DefAddressEdit?action=zoom&id="+adr.getId()+"\" />Edit</a>");
		    out.println("</td></tr>");
		}
		out.println("</table>");
	    }
	}
	out.println("<li><a href="+url+"Defendent?did="+defId+
		    "&action=zoom>Back to Related Defendant</a>");
	out.print("<br>");
	out.println("</div>");
	out.println("</body></html>");
	out.flush();
	// 
	out.close();
    }

}






















































