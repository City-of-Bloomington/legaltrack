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

@WebServlet(urlPatterns = {"/LegalServ"})
public class LegalServ extends TopServlet{

    static final long serialVersionUID = 48L;
    Logger logger = LogManager.getLogger(LegalServ.class);
    String oraDbStr = "";
    /**
     *
     * @param req
     * @param res
     *
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGet
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String type="", id="",  // legal_id
	    case_id="", act_id="", // action id (if any)
	    rental_id="", startDate="",reason="",startByName="",
	    status="", oldStatus="", actionBy="",
	    notes="",actionDate="", new_case_id="",
	    attention="", oldAttention="";
	// Rental related
	String pull_date="", pull_reason="";
		
	boolean success = true;
       	String username = "", fullName="", message = "",action = "";

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String [] vals;
	Rental rental = null;
	List<Owner> owners = null;
	List<String> addresses = new ArrayList<String>();
	List<Action> actions = null;
	Owner agent = null;
	Legal legal = null;
	User user = null;
	Enumeration<String> values = req.getParameterNames();

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("attention")) {
		attention =value;
	    }
	    else if (name.equals("id")) {
		id =value;
	    }
	    else if (name.equals("act_id")) {
		act_id =value;
	    }
	    else if (name.equals("new_case_id")) {
		new_case_id =value;
	    }
	    else if (name.equals("notes")) {
		notes =value.trim();
	    }
	    else if (name.equals("actionBy")) {
		actionBy =value;
	    }
	    else if (name.equals("actionDate")) {
		actionDate =value;
	    }
	    else if (name.equals("case_id")) {
		case_id =value;
	    }
	    else if (name.equals("status")) {
		status =value;
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	if(true){
	    HttpSession session = null;
	    session = req.getSession();
	    if(session != null){
		user = (User)session.getAttribute("user");
		if(user != null){
		    username = user.getUserid();
		    fullName = user.getFullName();
		}
	    }
	}
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
	// Get the Case info (this will be always done)
	//
	if(true){
	    legal = new Legal(debug, id); 
	    String back = legal.doSelect();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
		success = false;
		legal = null;
	    }
	    else{
		if(!new_case_id.equals("")){
		    back = legal.doUpdate(new_case_id);
		    if(!back.equals("")){
			logger.error(back);
			message += back;
			success = false;
		    }
		}
		rental_id = legal.getRental_id();
		case_id = legal.getCase_id();
		startDate = legal.getStartDate();
		reason = legal.getReason();
		startByName = legal.getStartByName();
		oldStatus = legal.getStatus();
		oldAttention = legal.getAttention();
		actions = legal.getActions();
		type = legal.getViolationType();
		if(status.equals("")) status = oldStatus;
		if(attention.equals("")) attention = oldAttention;
	    }
	}
	if(action.equals("Save") && user.canEdit()){
	    //	   
	    // Save a new action 
	    //
	    if(!notes.equals("")){
		Action ac = new Action(debug);
		ac.setLegal_id(id);
		ac.setNotes(notes);
		ac.setActionBy(username);
		ac.setActionByName(fullName);
		String back = ac.doSave();
		if(back.equals("")){
		    act_id = ac.getId();
		    message += "Data saved successfully";
		    //
		    if(status.equals("New")){ // change the status to Pending
			status="Pending";
			if(attention.equals("")) attention = "Legal";
			back = legal.doUpdate(status, attention);
			if(!back.equals("")){
			    logger.error(back);
			    message += " "+back;
			    success = false;
			}
		    }
		}
		else{
		    System.err.println(back);
		    message += back;
		    success = false;
		}
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    Action ac = new Action(debug, act_id);
	    String back = ac.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    ac.setNotes(notes);
	    back = ac.doUpdate();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		message += "Data updated successfully";
	    }
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    // System.err.println("delete record");
	    //
	    Action ac = new Action(debug, act_id);
	    String back = ac.doDelete();
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    else{
		act_id="";
	    }
	}
	else if(action.equals("zoom") || action.startsWith("Print")){	
	    //
	    if(!act_id.equals("")){
		Action ac = new Action(debug, act_id);
		String back = ac.doSelect();
		if(back.equals("")){
		    actionBy = ac.getActionBy();
		    actionDate = ac.getActionDate();
		    notes = ac.getNotes();
		    id = ac.getLegal_id();
		}
		else{
		    message += back;
		    logger.error(back);
		    success = false;
		    action = "";
		}
	    }
	}
	if(action.startsWith("New")){
	    act_id="";
	    actionBy="";
	    actionDate = "";
	    notes = "";
	    action="";
	}
	if(true){
	    //
	    // Get the rental info
	    // we have rental_id from Case (in top)
	    //
	    rental = new Rental(debug, rental_id);
	    String back = rental.findAll();
	    if(back.equals("")){
		owners = rental.getOwners();
		addresses = rental.getAddresses();
		agent = rental.getAgent();
		pull_date = rental.getPullDate();
		pull_reason = rental.getPullReason();
	    }
	    else{
		message += back;
		success = false;
	    }
	}
	//
	// after every action we may need to change the status 
	// on the case
	//
	if(user.canEdit() && 
	   (action.equals("Save") || action.equals("Update"))){
	    //
	    // status is the only thing we update here
	    //
	    if(status.equals("")) status = "Pending";
	    if(attention.equals("")) attention = "Legal";
	    //
	    // we do not let changing the status back to New
	    if(status.equals("New")){
		message = "Can't change status to New";
		success = false;
		if(!oldStatus.equals("")) status = oldStatus;
	    }
	    String back = legal.doUpdate(status, attention);
	    if(!back.equals("")){
		message += " could not change status "+back;
		success = false;
	    }
	    boolean closeStatusFlag = false;
	    if(status.equals("Closed") && !oldStatus.equals("Closed")){
		//
		// closing the case record
		//
		back = legal.doCloseCase();
		if(!back.equals("")){
		    message += " could not change case status "+back;
		    success = false;
		}
		else{
		    closeStatusFlag = true;
		}
	    }
	    legal = new Legal(debug, id); 
	    back = legal.doSelect();
	    if(!back.equals("")){
		message += back;
		logger.error(back);
		success = false;
		legal = null;
	    }
	    else{
		actions = legal.getActions(); // to refresh
	    }
	    if(activeMail){
		if(action.equals("Save")){
		    //
		    // send an email to the user to inform him
		    // about his request
		    //
		    String msg = " For your information "+
			"\n Rental record ID : "+rental_id+
			"\n related legal ID: "+id +
			"\n related Case ID: "+case_id+
			"\n a new action was added today by "+fullName+
			"\n stating that: "+notes+
			"\n The reason for the legal action was "+reason+
			"\n ";
		    if(addresses.size() > 0){
			msg += " Rental address(es): ";
			for(String str: addresses){
			    msg += str;
			    msg += "\n";
			}
			if(debug){
			    logger.debug(" Mail msg: "+msg);
			}
		    }
		    String subject = "Legal Actions: ";
		    if(!reason.equals("")){
			if(reason.length() > 30)
			    subject += reason.substring(0,30);
			else
			    subject += reason;
		    }
		    //
		    // excluded person to himself
		    //
		    String email = handMail;
		    String cc_email = handMail2;
		    if(attention.equals("Legal")){
			email = legalMail;
			cc_email = null;
		    }
		    if(!email.equals("") && !email.contains(user.getUserid())){ 
			new MsgMail(email, // to
				    user.getUserid()+emailStr,//from
				    subject,
				    msg,
				    cc_email, // CC
				    false);
		    }
		}
		else if(closeStatusFlag){
		    //
		    // send an email to the user to inform him
		    // about his request
		    //
		    String msg = " For your information "+
			"\n Rental record ID : "+rental_id+
			"\n related legal ID: "+id +
			"\n related Case ID:"+case_id+
			"\n The case was closed by "+fullName+
			"\n ";
		    if(addresses.size() > 0){
			msg += " Rental address(es): ";
			for(String str: addresses){
			    msg += str;
			    msg += "\n";
			}
			if(debug){
			    logger.debug(" Mail msg: "+msg);
			}
		    }
		    String subject = "Legal Actions: Closing legal ID "+id;
		    //
		    // Send email to the other party (legal or HAND)
		    //
		    String email = handMail;
		    String cc_email = handMail2;										
		    if(email.contains(user.getUserid())){
			email = legalMail;
			cc_email = null;
		    }
		    if(!email.equals("")){ 
			new MsgMail(email, // to
				    user.getUserid()+emailStr,//from
				    subject,
				    msg,
				    cc_email, // CC
				    false);
		    }
		}
	    }
	}
	if(action.equals("Save") && notes.equals("")){
	    action = "";
	}
	String legalCheck = "checked=\"checked\"", handCheck = "";
	if(attention.equals("Legal")){
	    legalCheck = "checked='checked' ";
	    handCheck = "";
	}
	else if(attention.equals("HAND")){
	    handCheck = "checked='checked' ";
	    legalCheck = "";
	}
	//
	// Inserts
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar2(url));
	out.println("<div id=\"mainContent\">");
	out.println("<h3 class=\"titleBar\">Rental Case </h3>"); 
	//
	// if we have any message, it will be shown here
	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");
	}
	else{
	    if(!message.equals(""))
		out.println("<p class=\"warning\">"+message+"</p>");
	}
	try{
	    //
			
	    out.println("<form id=\"myForm\" method=\"post\" action=\""+url+"LegalServ?\" >");
	    out.println("<fieldset><legend>Rental Case</legend>");		
	    if(!id.equals("")){
		out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    }
	    if(!act_id.equals("")){
		out.println("<input type=\"hidden\" name=\"act_id\" value=\""+act_id+"\" />");
	    }		

	    out.println("<table>");
	    out.println("<tr><td class=\"left\">");
	    out.println("<label>Legal ID: </label>"+id+"</td></tr>");

	    if(!case_id.equals("")){
		out.println("<tr><td class=\"left\"><label>Case ID: "+
			    "</label><a href=\""+url+
			    "CaseServ?action=zoom&amp;id="+case_id+
			    "\">"+case_id+"</a></td></tr>");
	    }
	    else {
		out.println("<tr><td class=\"left\"><label for=\"case_id\">Case ID: </label>");
		out.println("<input type=\"text\" name=\"new_case_id\" id=\"case_id\" "+
			    "value=\"\" size=\"8\" maxlength=\"10\" />");
		out.println("(if you know the Case ID enter it here)</td></tr>");
	    }
	    out.println("<tr><td class=\"left\">");
	    out.println("<label for=\"status\">Status: </label>"+
			"<select name=\"status\" id=\"status\" >");
	    out.println("<option selected=\"selected\">"+status+
			"</option>");
	    for(int i=0;i<Inserts.statusArr.length;i++){
		if(!status.equals(Inserts.statusArr[i]))
		    out.println("<option>"+Inserts.statusArr[i]+"</option>");
	    }
	    out.println("</select>");
	    out.println(" If no more actions are needed change the status to 'Closed' or 'Filed Suit'. </td></tr>");
	    out.println(" <tr><td class=\"left\"><label>Violation Type: </label>"+
			type+"</td></tr>");
	    out.println("<tr><td class=\"left\"><a href=\""+url+"Logout?switchTo=rental&amp;id="+rental_id+
			"\">Switch to Rental "+rental_id+"</a> (This link will log you out from legaltrack)</td></tr>");
	    if(addresses != null && addresses.size() > 0){
		out.println("<tr><td class=\"left\"><table><tr><td style=\"vertical-align:top,text-align:left\">");
		out.println("<label>Property Address(s)</label></td><td>");
		for (int i=0;i<addresses.size();i++){
		    if(i > 0) out.println("<br />");
		    out.println(addresses.get(i));
		}
		out.println("</td></tr></table></td></tr>");
	    }
	    if(owners != null && owners.size() > 0){
		out.println("<tr><td class=\"left\">"+
			    "<label>Owner(s) Info</label></td></tr>");
		out.println("<tr><td class=\"left\"><table>");
		out.println("<tr><td>&nbsp;</td><th>Name</th><th>Address</th>"+
			    "<th>City, State Zip</th></tr>");
		for(Owner owner: owners){
		    out.println("<tr><td>&nbsp;</td><td align=\"left\">"+
				owner.getName()+"</td>");
		    out.println("<td class=\"left\">"+owner.getAddress()+"</td>");
		    out.println("<td class=\"left\">"+owner.getCityStateZip()+"</td></tr>");
		}
		out.println("</table></td></tr>");
	    }
	    if(agent != null){
		out.println("<tr><td class=\"left\"><label>Agent Info:</label></td></tr>");
		out.println("<tr><td><table><tr><td>&nbsp;</td><th>Name</th><th>Address</th>"+
			    "<th>City, State Zip</th></tr>");
		out.println("<tr><td>&nbsp;</td>");
		out.println("<td class=\"left\">"+agent.getName()+"</td>");
		out.println("<td class=\"left\">"+agent.getAddress()+"</td>");
		out.println("<td class=\"left\">"+agent.getCityStateZip()+"</td></tr>");
		out.println("</table></td></tr>");
	    }
	    out.println("<tr><td>&nbsp;</td></tr>");
	    out.println("<tr><td class=\"left\"><label>Last Pull Date: </label>"+pull_date);
	    out.println("</td></tr>");
	    out.println("<tr><td class=\"left\"><label>Last Pull Reason: </label>"+pull_reason);
	    out.println("</td></tr>");
	    out.println("<tr><td class=\"left\"><label>Initiated By: </label>"+startByName);
	    out.println("</td></tr>");
	    out.println("<tr><td class=\"left\"><label>Start Date: </label>"+startDate);
	    out.println("</td></tr>");
	    out.println("<tr><td class=\"left\"><label>Reasons: </label>"+reason);
	    out.println("</td></tr>");
	    out.println("</table>");
	    out.println("</fieldset>");
	    out.println("<fieldset><legend>Next Attention</legend>");
	    out.println("<table>");
	    out.println("<tr><td>Set next action attention to the department that need to follow-up your current action <br />from the options below and then press on 'Save' or 'Update' button.</td></tr>");
	    out.println("<tr><td class=\"left\"><label>Next Action Attention: </label>");
	    out.println("<input type='radio' name='attention' value='Legal' "+
			legalCheck+" />Legal Dept.");
	    out.println("<input type='radio' name='attention' value='HAND' "+
			handCheck+" />HAND Dept.");
	    out.println("</td></tr>");
	    out.println("</table>");
	    out.println("</fieldset>");
	    out.println("<fieldset><legend>Actions</legend>");
	    out.println("<table>");
	    out.println("<tr><td>Scroll down to review the previous actions</td></tr>");
	    out.println("<tr><td>To Add a New Action, write in the field below.</td></tr>"); 
	    out.println("<tr><td><label>Action Notes</label></td></tr>");
	    out.println("<tr><td><textarea name=\"notes\" rows=\"5\" cols=\"70\">");
	    out.println(Helper.replaceSpecialChars(notes));
	    out.println("</textarea></td></tr>");
	    out.println("</table></fieldset>");
	    //
	    if(act_id.equals("")){ 
		if(user.canEdit()){
		    out.println("<fieldset>");
		    out.println("<table class=\"control\">");
		    out.println("<tr><td>");
		    out.println("<input type=\"submit\" "+
				"value=\"Save\" "+
				"name=\"action\" class=\"submit\" />");
		    out.println("</td></tr>");
		    out.println("</table></fieldset>");				
		}
		out.println("</form>");
	    }
	    else{ // save, update
		//
		out.println("<fieldset>");
		out.println("<table class='control'>");			
		out.println("<tr><td>");
		if(user != null && user.canEdit()){
		    out.println("<td><input value=\"Update\" "+
				"type=\"submit\" name=\"action\" "+
				"class=\"submit\" />");
		    out.println("</td>");
		}
		out.println("<td><input value=\"New Action\" "+
			    "type=\"submit\" name=\"action\" "+
			    "class=\"submit\" />");
		out.println("</td></tr>");
		out.println("</table></fieldset>");
		out.println("</form>");
			
		if(user != null && user.canDelete()){ // can delete his record
		    out.println("<form id=\"myForm2\" action=\""+url+"LegalServ?\" onSubmit=\"return validateDelete();\" >");
		    out.println("<input type=\"hidden\" name=\"id\" value=\""+
				id+"\" />");
		    out.println("<input type=\"hidden\" name=\"act_id\" value=\""+
				act_id+"\" />");				
		    out.println("<fieldset>");
		    out.println("<table class='control'>");			
		    out.println("<tr><td>");	
		    out.println("<input type=\"submit\" name=\"action\" "+
				"value=\"Delete\" />");
		    out.println("</td></tr>");
		    out.println("</table></fieldset>");		
		    out.println("</form>");
		}
	    }
	    if(actions != null && actions.size() > 0){
		out.println("<fieldset><legend>Actions History</legend>");
		out.println("<table><tr><th>Date</th>"+
			    "<th>Initiated By</th><th>Action</th></tr>");

		for(int i=0;i<actions.size();i++){
		    Action act = actions.get(i);
		    out.println("<tr><td class=\"left\">");
		    out.println(act.getActionDate());
		    out.println("</td><td class=\"left\">");
		    out.println(act.getActionByName());
		    out.println("</td><td class=\"left\">");
		    out.println(Helper.replaceSpecialChars(act.getNotes()));
		    out.println("</td></tr>");
		}
		out.println("</table>");
		out.println("</fieldset>");
	    }
	}catch(Exception ex){
	    ex.printStackTrace();
	}
	out.println("</div>");
	out.println("</body></html>");
	out.flush();
	out.close();

    }

}






















































