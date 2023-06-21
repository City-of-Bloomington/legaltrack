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
@WebServlet(urlPatterns = {"/CaseServ"})
public class CaseServ extends TopServlet{

    static final long serialVersionUID = 25L;
    static Logger logger = LogManager.getLogger(CaseServ.class);
    static List<Status> statuses = null;
    static List<CaseType> caseTypes = null;
    static List<Lawyer> lawyers = null;

    String lawyerTypeJsHash = "";
    //
    /**
     * Generates the Case form and processes view, add, update and delete
     * operations.
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
    
	String // f_name = "", l_name="", 
	    street_num="",street_dir="",
	    id="", citeId="", fullName="",
	    street_name="",street_type="",sud_type="",sud_num="", post_dir="",
	    legal_id="",per_day="", pro_supp="",mcc_flag="",
	    status="PD", animals="", street_address="",
	    fine="",court_cost="", care_of="",
	    did="", rental_addr="", invalid_addr="";
	String balance ="", total_due="";
	
	boolean connectDbOk = false, idFound=false, successFlag=true,
	    checkAval=false, proSuppNeeded = false, success = true;
	//
	String message="", action="", 
	    dept="",
	    entry_date="", courtCostDefault="", lawyerid="";
	
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	Enumeration<String> values = req.getParameterNames();

	User user = null;
	HttpSession session = null;
		
	String [] vals;
	String [] defList = null;
	Hashtable<String, String> causeHash = new Hashtable<String, String>();
	List <Address> addresses = null;
	List <Defendant> defendants = null;
	Case cCase = new Case(debug);
	String[] delAddr = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("received")){
		cCase.setReceived(value);
	    }
	    else if (name.equals("filed")) {
		cCase.setFiled(value);
	    }
	    else if (name.equals("lawyerid")) {
		cCase.setLawyerid(value);
	    }
	    else if (name.equals("mcc_flag")) {
		cCase.setMcc_flag(value);
	    }
	    else if (name.equals("marked")) {
		defList =vals;  // array
	    }
	    else if (name.equals("street_num")) {
		street_num = value;
	    }
	    else if (name.equals("street_dir")) {
		street_dir = value;
	    }
	    else if (name.equals("street_name")) {
		street_name = value.toUpperCase();
	    }
	    else if (name.equals("street_type")) {
		street_type = value;
	    }
	    else if (name.equals("sud_type")) {
		sud_type = value;
	    }
	    else if (name.equals("sud_num")) {
		sud_num = value;
	    }
	    /*
	      else if (name.equals("street_address")) {
	      street_address = value;
	      }
	    */
	    else if (name.equals("invalid_addr")) {
		invalid_addr = value;
	    }
	    else if (name.equals("rental_addr")) {
		rental_addr = value;
	    }
	    else if (name.equals("animals")) {
		animals = value;
	    }	
	    else if (name.equals("sent_date")) {
		cCase.setSent_date(value);
	    }
	    else if (name.equals("trans_collect_date")) {
		cCase.setTrans_collect_date(value);
	    }		
	    else if (name.equals("rule_date")) {
		cCase.setRule_date(value);
	    }
	    else if (name.equals("rule_time")) {
		cCase.setRule_time(value);
	    }
	    else if (name.equals("e41_date")) {
		cCase.setE41_date(value);
	    }
	    else if (name.equals("citation_num")) {
		cCase.setCitation_num(value);
	    }
	    else if (name.equals("citation_date")) {
		cCase.setCitation_date(value);
	    }	
	    else if (name.equals("delAddr")) {
		delAddr = vals; // array
	    }
	    else if (name.equals("cause_num")) {
		// cause_num = value.toUpperCase();
	    }
	    else if (name.startsWith("cause")) {
		if(name.length() > 5){
		    String strId = name.substring(5);
		    causeHash.put(strId, value.toUpperCase());
		}
	    }
	    else if (name.equals("case_type")) {
		cCase.setCase_type(value);
	    }
	    else if (name.equals("per_day")) {
		cCase.setPer_day(value);
	    }
	    else if (name.equals("status")) {
		cCase.setStatus(value);
	    }
	    else if (name.equals("prev_status")) {
		cCase.setPrevStatus(value);
	    }	    
	    else if (name.equals("ini_hear_date")) {
		cCase.setIni_hear_date(value);
	    }
	    else if (name.equals("ini_hear_time")) {
		cCase.setIni_hear_time(value.toUpperCase());
	    }
	    else if (name.equals("contest_hear_date")) {
		cCase.setContest_hear_date(value);
	    }
	    else if (name.equals("contest_hear_time")) {
		cCase.setContest_hear_time(value.toUpperCase());
	    }
	    else if (name.equals("misc_hear_date")) {
		cCase.setMisc_hear_date(value);
	    }
	    else if (name.equals("misc_hear_time")) {
		cCase.setMisc_hear_time(value.toUpperCase());
	    }
	    else if (name.equals("pro_supp_time")) {
		cCase.setPro_supp_time(value.toUpperCase());
	    }
	    else if (name.equals("pro_supp")) {
		cCase.setPro_supp(value);
		pro_supp = value;
	    }
	    else if (name.equals("judgment_date")) {
		cCase.setJudgment_date(value);
	    }
	    else if (name.equals("pro_supp_date")) {
		cCase.setPro_supp_date(value);
	    }
	    else if (name.equals("compliance_date")) {
		cCase.setCompliance_date(value);
	    }
	    else if (name.equals("judgment_amount")) {
		cCase.setJudgment_amount(value);
	    }
	    else if (name.equals("fine")){
		cCase.setFine(value);
	    }
	    else if (name.equals("id")){
		cCase.setId(value);
		id = value;
	    }
	    else if (name.equals("did")){
		did = value;
	    }
	    else if (name.equals("citeId")){
		citeId = value;
	    }
	    else if (name.equals("source")){
		cCase.setSource(value);
	    }
	    else if (name.equals("court_cost")){
		cCase.setCourt_cost(value);
	    }
	    else if (name.equals("last_paid_date")) {  
		cCase.setLast_paid_date(value);
	    }
	    else if (name.equals("closed_date")){
		cCase.setClosed_date(value);
	    }
	    else if (name.equals("closed_comments")){
		cCase.setClosed_comments(value);
	    }
	    else if (name.equals("comments")){
		cCase.setComments(value);
	    }
	    else if (name.equals("action")){ 
		// Get, Save, zoom, edit, delete, New, Refresh
		action = value;  
	    }
	    else if (name.equals("action2")){ 
		// for Delete
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	if(debug)
	    System.err.println("Legal addrCheckUrl: "+addrCheckUrl);
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	    fullName = user.getFullName();	    
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	if(caseTypes == null){
	    message += resetArrays();
	}
	Calendar current_cal = Calendar.getInstance();
	entry_date = 
	    (current_cal.get(Calendar.MONTH)+1)+"/"+ 
	    current_cal.get(Calendar.DATE) + 
	    "/" + current_cal.get(Calendar.YEAR);
	//
	if(!animals.equals("")){
	    //
	    // these are nww animals to be added
	    //
	    // System.err.println("animals "+animals);
	    String[] pets = animals.split(",");
	    if(pets != null && pets.length > 0){
		for(int i=0;i<pets.length;i++){
		    System.err.println("Pet "+pets[i]);;
		    String[] one = pets[i].split(":");
		    Animal pet = new Animal(id, one[0], one[1], debug);
		    if(pet.isValid()){
			String back = pet.doSave();
			if(!back.equals("")){
			    message += back;
			    success = false;
			}
		    }
		}
	    }
	}
	if(action.equals("Save") && user.canEdit()){
	    //
	    String qq = "";			
	    try{
		if(user.canEdit()){
		    String back = cCase.doSave();
		    if(back.equals("")){
			id = cCase.getId();
		    }
		    else{
			message += "Could not save "+back;
			success = false;
			logger.error(back);
		    }
		    if(!did.equals("") && success){
			back = cCase.linkDefendantToCase(did);
			if(!back.equals("")){
			    message += back;
			    success = false;
			}
		    }
		}
		else{
		    message += " You Can not save data ";
		    success = false;
		}
	    }
	    catch(Exception ex){
		success = false;
		logger.error(ex);
		message += " Could not save date ";
		message += ex;
	    }
	}
	else if(action.equals("Update") && user.canEdit()){
	    //
	    String str="";
	    try{
		if(user.canEdit()){
		    String back = cCase.doUpdate();
		    if(!back.equals("")){
			message += back;
			success = false;
			logger.error(back);
		    }
		    //
		    // first set the cause_num for each defendant
		    //
		    if(causeHash.size() > 0){
			Enumeration<String> keys = causeHash.keys();
			while(keys.hasMoreElements()){
			    String key = keys.nextElement();
			    String val = causeHash.get(key);
			    Defendant def = new Defendant(key, debug);
			    back = def.updateCauseNumber(id, val);
			    if(!back.equals("")){
				message += back;
				logger.error(back);
				success = false;
			    }
			}
		    }
		    //
		    // check if we need to delete some of the defendents
		    // from this case when the user marks the checkboxes
		    //
		    if(defList != null){
			back = cCase.unlinkDefendant(defList);
			if(!back.equals("")){
			    message += back;
			    success = false;
			}
		    }
		    if(success){
			message += " Updated Successfully";
		    }
		}
		else{
		    message += "You can update data ";
		    success = false;
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
		message += " Could not update data ";
		message += ex;
		success = false;
	    }
	    //
	}
	else if(action.equals("Delete") && user.canDelete()){
	    //
	    try{
		if(user.isAdmin()){
		    String back = cCase.doDelete();
		    id = "";
		    if(!back.equals("")){
			message += "You can not delete "+back;
			success = false;
		    }
		}
		else{
		    message += "You can not delete ";
		    success = false;
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
		message += " Could not delete data ";
		message += ex;
		success = false;
	    }
	    // we need these for address object
	    street_num="";street_dir="";
	    street_name="";street_type="";sud_type="";sud_num="";
	    street_address="";
	    per_day=""; pro_supp=""; mcc_flag=""; fine="";court_cost="";
	    status="PD";
	    lawyerid="";
			
	}
	else if(!id.equals("")){	
	    String back = cCase.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += "Could not retreive data: "+back;
		success = false;
		id = "";
		action = "";
	    }
	}
	if(user.canEdit() &&
	   (action.equals("Save") || action.equals("Update"))){
	    //
	    if(!street_num.equals("") && !street_name.equals("")){
		Address addr = new Address(debug);
		addr.setCase_id(id);
		addr.setStreet_num(street_num);
		addr.setStreet_dir(street_dir);
		addr.setStreet_type(street_type);
		addr.setStreet_name(street_name);
		addr.setSud_num(sud_num);
		addr.setSud_type(sud_type);
		addr.setPost_dir(post_dir);
		addr.setRental_addr(rental_addr);
		// addr.setStreet_address(street_address);
		if(!addr.isValid(addrCheckUrl))
		    invalid_addr="Y";
		else
		    invalid_addr="";
		addr.setInvalid_addr(invalid_addr);
		String back = addr.doSave();
		if(!back.equals("")){
		    success = false;
		    message += " "+back;
		    logger.error(back);
		}
	    }
	    if(activeMail){
		if(action.equals("Save")){
		    //
		    // send an email to the user to inform him
		    // about his request
		    //
		    addresses = cCase.getAddresses();
		    String msg = " For your information "+
			"\n A new Legaltrack Case ID: <a href=\""+url+"CaseServ?id="+id+"\">"+id+"</a> "+
			"\n Added today by "+fullName+
			"\n ";
		    if(addresses != null && addresses.size() > 0){
			msg += " Violation address(es): ";
			for(Address addr: addresses){
			    msg += addr.getAddress();
			    msg += "\n";
			}
			if(debug){
			    logger.debug(" Mail msg: "+msg);
			}
		    }
		    String subject = "LegalTrack New Case: "+id;
		    //
		    // excluded person to himself
		    //
		    String email = caseEmail+emailStr;
		    if(!email.equals("")){ 
			new MsgMail(email, // to
				    user.getUserid()+emailStr,//from
				    subject,
				    msg,
				    null, // CC
				    false);
		    }
		}
		else if(cCase.isStatusClosed()){
		    //
		    // send an email to the user to inform him
		    // about his request
		    //
		    String email = caseEmail+emailStr;
		    String msg = " For your information "+
			"\n related Case ID:<a href=\""+url+"CaseServ?id="+id+"\">"+id+"</a>"+
			"\n The case was closed by "+fullName+
			"\n ";
		    addresses = cCase.getAddresses();		    
		    if(addresses != null && addresses.size() > 0){
			msg += " Violation address(es): ";
			for(Address addr: addresses){
			    msg += addr.getAddress();
			    msg += "\n";
			}
			if(debug){
			    logger.debug(" Mail msg: "+msg);
			}
		    }
		    String subject = "LegalTrack: Closing Case "+id;
		    //
		    // Send email to the other party (legal or HAND)
		    //
		    if(!email.equals("")){ 
			new MsgMail(email, // to
				    user.getUserid()+emailStr,//from
				    subject,
				    msg,
				    null, // CC
				    false);
		    }
		    
		}
	    }
	}
	if(!id.equals("")){
	    LegalList ll = new LegalList(debug);
	    ll.setCase_id(id);
	    String back = ll.lookFor();
	    if(back.equals("")){
		List<Legal> list = ll.getLegals();
		if(list != null && list.size() > 0){
		    legal_id = list.get(0).getId();
		}
	    }
	}
	//
	if(!id.equals("")){
	    per_day = cCase.getPer_day();
	    mcc_flag = cCase.getMcc_flag();
	    pro_supp = cCase.getPro_supp();
	    court_cost = cCase.getCourt_cost();
	    mcc_flag = cCase.getMcc_flag();
	}
	if(!per_day.equals("")) per_day = "checked=\"checked\"";
	if(!mcc_flag.equals("")) mcc_flag = "checked=\"checked\"";
	if(!pro_supp.equals("")) pro_supp = "checked=\"checked\"";
	if(court_cost.equals("")) court_cost = courtCostDefault;
	//
	if(!id.equals("")){
	    //
	    // TODO fix me
	    // find total payments, addresses
	    //
	    Payment pay = new Payment(debug);
	    pay.setId(id);
	    String back = pay.compute();
	    if(back.equals("")){
		total_due = pay.getTotalFine();
		balance = pay.getTotalBalanceStr();
	    }
	    else{
		logger.error(back);
	    }
	    //
	    // delete the addresses that are marked for deletion
	    //
	    if(delAddr != null){
		if(delAddr.length > 0){
		    for(int i=0;i<delAddr.length;i++){
			Address addr = new Address(debug, delAddr[i]);
			addr.doDelete();
		    }
		}
	    }
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<div class=\"center\">");
	if(id.equals("")){
	    out.println("<h3 class=\"titleBar\">New Case </h3>"); 
	}
	else {
	    out.println("<h3 class=\"titleBar\">Edit Case "+id+"</h3>"); 	
	}
	out.println("</div>");
	out.println("<script type=\"text/javascript\">     ");
	out.println("//<![CDATA[  ");        			
	out.println("  function validateForm(){	           ");
	out.println("  with(document.myForm){              ");
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  if(!checkDate(ini_hear_date))return false;     ");
	out.println("  if(!checkDate(contest_hear_date))return false; ");
	out.println("  if(!checkDate(misc_hear_date))return false;    ");
	out.println("  if(!checkDate(received))return false;          ");
	out.println("  if(!checkDate(filed))return false;             ");
	out.println("  if(!checkDate(judgment_date))return false;     ");
	out.println("  if(!checkDate(pro_supp_date))return false;     ");
	out.println("  if(!checkDate(compliance_date))return false;   ");
	out.println("  if(!checkDate(sent_date))return false;         ");
	out.println("  if(!checkDate(last_paid_date))return false;    ");
	out.println("  if(!checkDate(closed_date))return false;       ");
	out.println("  if(!checkDate(rule_date))return false;         ");
	out.println("  if(!checkDate(trans_collect_date))return false;  ");			
	out.println("  if(!checkNumber(fine)){           ");
	out.println("   return false;}			        ");
	out.println("  if(!checkNumber(court_cost)){     ");
	out.println("   return false;}			        ");
	out.println("  if(case_type.selectedIndex <= 0){ ");
	out.println("  alert('You need to select the case type'); ");
	out.println("  case_type.focus();               ");
	out.println("  return false;}			        ");
	// we need to check before we save a new case that one of
	// the featured dates are entered
	//
	// if(action.equals("")){ // Save case only
	//
	// if the case is not close we prevent the user from
	// doing any update without a future date entered
	//
	out.println(" var x = status.options[status.selectedIndex].value;");
	out.println(" if( x == 'CC' || x == 'TC'){                   ");
	out.println("  if(trans_collect_date.value == ''){   ");
	out.println("   alert('Transfer to Collections Date must be entered');");
	out.println("   return false;		            ");
	out.println("  }}                                 ");			
	out.println(" }                                 ");
	// 
	// Everything Ok
	out.println("  return true;		                ");
	out.println(" }	         	                    ");
	out.println(" function pickLawyer(item){                         ");
	out.println(lawyerTypeJsHash);
	out.println(" var x = item.options[item.selectedIndex].value;    ");
	out.println(" if(x.length > 0){                                  ");
	out.println(" var y = lawyerType[x];                    ");
	out.println("  with(document.forms[0]){                 ");
	out.println(" var len = lawyerid.options.length;        ");
	out.println(" for(var jj=0;jj < len;jj++){              ");
	out.println(" if(y == lawyerid.options[jj].value){      ");
	out.println("       lawyerid.selectedIndex = jj;        ");
	out.println("   }}}                                     ");
	out.println(" var y2 = deptType[x];                     ");
	out.println("  if(y2){                                  ");
	out.println(" document.getElementById('dept').firstChild.nodeValue = y2; "); 
	out.println("   }}}                                     ");
	out.println("  function doCount(){                         ");
	out.println("  var lstr = document.forms[0].comments.value.length;");
	out.println("  var rest = 10000 - lstr;                     ");
	out.println("  if(rest < 0){                            ");
	out.println("   rest = 'Max limit exceeded';             ");
	out.println("  }                                                  ");
	out.println("  else{                                               ");
	out.println("      rest = 'Remaining '+rest+' characters';         ");
	out.println("      document.getElementById('rest').firstChild.nodeValue = rest;"); 
	out.println("    }                                             ");
	out.println("  }                                               ");
	out.println(" function showHide(elem){                         ");
	out.println("   var type = elem.options[elem.selectedIndex].value; ");
	out.println("   if(type == 'A'){ // animals                    ");
	out.println("      document.getElementById('animalBlock').style.display='table-row';");
	out.println("     document.getElementById('animalBlock').style.visibility=\"visible\";");
	out.println("    }                                             ");
	out.println("   else {                                         ");
	out.println("      document.getElementById('animalBlock').style.display=\"none\";");
	out.println("     document.getElementById('animalBlock').style.visibility=\"hidden\";");			
	out.println("    }                                               ");
	out.println("  }                                               ");
	out.println("  function validateDelete2(){	        ");
	out.println("   var x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   if(x){                       ");
	out.println("    document.forms[0].action2.value=\"Delete\"; ");
	out.println("    document.forms[0].submit();   ");
	out.println("    return true;                    ");
	out.println("	}	             		     ");
	out.println("  }	             		     ");		
	out.println(" //]]>                                            ");
	out.println(" </script>				                           ");
	if(!message.equals(""))		
	    if(success){
		out.println("<p class=\"center\">"+message+"</p>");
	    }
	    else{
		out.println("<p class=\"warning\">"+message+
			    "</p>");
	    }
	try{
	    if(true){
		//
		// Add/Edit record
		//
		int tabindex = 1;

		out.println("<form name=\"myForm\" method=\"post\" "+
			    "action=\""+url+"CaseServ?\" "+
			    "onsubmit=\"return validateForm()\">");
		out.println("<fieldset>");							
		out.println("<input type=\"hidden\" name=\"animals\" id=\"animals\" value=\"\" />");
		if(!id.equals("")){
		    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
		    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
		    out.println("<input type=\"hidden\" name=\"prev_status\" value=\""+cCase.getStatus()+"\" />");
		}
		out.println("<table width=\"95%\" border=\"1\">");

		if(!id.equals("")){
		    defendants = cCase.getDefendants();
		    if(defendants != null && defendants.size() > 0){
			out.println("<tr><td><table width=\"100%\">");
			out.println("<tr><td class=\"center title\" "+
				    "id=\"b_hd\">Defendant(s)"+
				    "</td></tr>");			
			out.println("<tr><td>"+
				    "<table border=\"1\" width=\"100%\">"+
				    "<tr><th>"+
				    "<div class=\"green\">*</div></th><th>"+
				    "Def. ID</th><th>Cause Number</th><th>Name"+
				    "</th><th>SS#</th><th>D.O.B</th>"+
				    "<th>Address</th>"+
				    "</tr>");
			for(int i=0;i<defendants.size();i++){
			    Defendant def = defendants.get(i);
			    if(def != null){
				out.println("<tr>");	
				String str = def.getDid();
				String did2 = str;
				out.println("<td><input type=\"checkbox\" "+
					    "name=\"marked\" "+
					    "value=\""+str+"\" />"+
					    "</td><td><a href=\""+
					    url+"Defendent?did="+str+
					    "\">"+str+
					    "</a></td>");
				checkAval = true;	
				str = def.getCauseNumForCase(id);
				if(str == null || str.equals("")) str="53C0";
				out.println("<td>");
				out.println("<input type=\"text\" name=\"cause"+did2+
					    "\" tabindex=\""+(tabindex++)+"\""+
					    " size=\"20\" maxlength=\"20\" value=\""+
					    str+"\" /></td>");
				str = def.getFullName();
				if(str == null || 
				   str.trim().equals("") ||
				   str.trim().equals(",")) str = "&nbsp;";
				out.println("<td>"+str+"</td>");
				str = def.getSsn();
				if(str.equals("")) str = "&nbsp;";
				out.println("<td>"+str+"</td>");
				str = def.getDob();
				if(str.equals("")) str = "&nbsp;";
				out.println("<td>"+str+"</td>");
				str = ""+def.getAddress();
				if(str.equals("")) str = "&nbsp;";
				out.println("<td>"+str+"</td>");	
				out.println("</tr>");
			    }
			}
			out.println("</table></td></tr>");
			out.println("</table></td></tr>");
		    }
		}
		String display="none";
		String visibility="hidden";
		if(!id.equals("")){
		    CaseType ct = cCase.getCaseType();
		    if(ct != null && ct.getName().startsWith("Animal")){
			display="table-row";
			visibility="visible";
		    }
		}
		out.println("<tr id=\"animalBlock\" style=\"visibility:"+visibility+";display:"+display+";\">");
		out.println("<td align=\"center\"><table width=\"100%\">");
		out.println("<tr><td colspan=\"2\" "+
			    "id=\"b_nml\" class=\"center title\">"+
			    "Animals Associated with This Case"+
			    "</td></tr>");
		if(!id.equals("")){
		    CaseType ct = cCase.getCaseType();
		    if(ct != null && ct.getName().startsWith("Animal")){
			out.println("<tr><td class=\"left\">");					
			List<Animal> pets = cCase.getPets();
			if(pets != null && pets.size() > 0){
			    String all ="";
			    for(Animal pet:pets){
				if(!all.equals("")) all += ", ";
				all += pet;
				if(!pet.getType().equals("")){
				    all += " ("+pet.getType()+")";
				}
			    }
			    out.println(all);
			    out.println("</td></tr>");
			}
		    }
		}
		out.println("<tr><td id=\"newAnimals\">&nbsp;</td></tr>");
		out.println("<tr><td class=\"left\">");
		out.println("<input type=\"button\" "+
			    " onclick=\"window.open('"+url+
			    "AnimalPop?','Animals','toolbar=0,location=0,"+
			    "directories=0,status=0,menubar=0,scrollbars=2,"+
			    "resizable=0,width=400,height=400'); \""+
			    " value=\"Add New Animals\" />(After that click on Update)</td>");
		if(cCase.hasAnimals()){
		    out.println("<td class=\"right\"><input type=\"button\" onclick=\"document.location='"+url+"AnimalServ?cid="+id+"'\" value=\"Edit Animals\" name=\"action\" /></td>");
		}
		out.println("</tr>");
		out.println("</table></td></tr>");					
		//
		// Case Info
		out.println("<tr><td>");
		out.println("<table width=\"100%\">"+
			    "<tr><td class=\"center title\">Case Info </td></tr>");
		//
		// case info top table 
		out.println("<tr><td><table>"); 
		if(!id.equals("")){
		    out.println("<tr><th>Legal Action: </th><td class=\"left\">");
		    if(!legal_id.equals("")){
			out.println("<a href=\""+url+
				    "LegalServ?id="+legal_id+
				    "\">Related Actions</a>");
		    }
		    else{
			out.println("<a href=\""+url+
				    "StartLegal?case_id="+id+
				    "\">Start Legal Actions </a>(for rental cases)");			
		    }
		    out.println("</td><th>Date</th><th>(mm/dd/yyyy)</th></tr>");
		}
		else{
		    out.println("<tr><th></th><th></th><th>Date</th><th>(mm/dd/yyyy)</th></tr>");
		}
		// 
		out.println("<tr><th>");
		out.println("Status: </th><td class=\"left\">");
		out.println("<select name=\"status\" tabindex=\""+(tabindex++)+"\">");
		if(statuses != null){
		    for(int i=0;i<statuses.size();i++){
			Status sts = statuses.get(i);
			if(sts.getId().equals(cCase.getStatus()))
			    out.println("<option selected=\"selected\" value=\""+
					sts.getId()+"\">"+sts+"</option>\n");
			else
			    out.println("<option value=\""+
					sts.getId()+"\">"+sts+"</option>");
		    }
		}
		out.println("</select>");
		out.println("</td>");
		//
		// Received
		out.println("<th>Received: </th><td class=\"left\">");
		out.println("<input name=\"received\" size=\"10\" class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getReceived()+ "\" maxlength=\"10\" />");
		out.println("</td></tr>");
		//
		// Type
		out.println("<tr><th>");
		out.println("Type:</th><td class=\"left\">");
		out.println("<select name=\"case_type\" tabindex=\""+(tabindex++)+
			    "\" onchange=\"pickLawyer(this);showHide(this);\">");
		out.println("<option value=\"\"></option>");
		if(caseTypes != null){
		    for(CaseType ct: caseTypes){
			if(ct.getId().equals(cCase.getCase_type()))
			    out.println("<option selected=\"selected\" value=\""+
					ct.getId()+"\">"+ct+"</option>");
			else
			    out.println("<option value=\""+
					ct.getId()+"\">"+ct+"</option>");
		    }
		}
		out.println("</select></td>");
		//
		// Sent
		out.println("<th>Sent Letter: </th><td class=\"left\"> ");
		out.println("<input name=\"sent_date\" size=\"10\" "+
			    " class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\" maxlength=\"10\""+
			    " value=\""+cCase.getSent_date() +"\" />");
		out.println("</td></tr>");
		//
		// dept
		out.println("<tr><th>Dept: </th><td class=\"left\"> ");
		CaseType ct = cCase.getCaseType();
		if(ct != null){
		    Department dp = ct.getDepartment();
		    if(dp != null){
			dept = dp.getDept();
		    }
		}
		out.println("<span id=\"dept\">"+dept+"&nbsp;</span></td>");
		//
		// Filed
		out.println("<th>Filed: </th><td class=\"left\"> ");
		out.println("<input name=\"filed\" size=\"10\" "+
			    " class=\"date\" "+						
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getFiled() +"\" maxlength=\"10\" />");
		out.println("</td></tr>");
		//
		out.println("<tr><th>Attorney: </th><td class=\"left\"> ");
		out.println("<select name=\"lawyerid\" tabindex=\""+(tabindex++)+"\">");
		out.println("<option value=\"\"></option>");
		if(lawyers != null){
		    for(int i=0;i<lawyers.size();i++){
			Lawyer lawyer = lawyers.get(i);
			if(lawyer.getEmpid().equals(cCase.getLawyerid()))
			    out.println("<option selected=\"selected\" value=\""+
					lawyer.getEmpid()+"\">"+lawyer+"</option>");
			else if(lawyer.isActive())
			    out.println("<option value=\""+
					lawyer.getEmpid()+"\">"+lawyer+"</option>");
		    }
		}
		out.println("</select>");
		out.println("</td>");
		// 
		out.println("<th>Trans To Collect:</th><td class=\"left\"> ");
		out.println("<input name=\"trans_collect_date\" size=\"10\" "+
			    " class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getTrans_collect_date()+
			    "\" maxlength=\"10\" /></td></tr>");
		//			
		out.println("<tr><th>Citation #: </th><td class=\"left\"> ");
		out.println("<input type=\"text\" name=\"citation_num\" value=\""+cCase.getCitation_num()+"\" size=\"50\" maxlength=\"50\" />");
		if(cCase.hasViolations()){
		    out.println("&nbsp;&nbsp;(multiple)");
		}
		out.println("</td>");
		//
		out.println("<th>Citation Date: </th><td class=\"left\"> ");
		out.println("<input type=\"text\" name=\"citation_date\" "+
			    " class=\"date\" "+
			    " value=\""+cCase.getCitation_date()+"\" size=\"10\" maxlength=\"10\" /></td></tr>");		
		//			
		out.println("</table></td></tr>"); // end of top table
		out.println("<tr><td>&nbsp;</td></tr>");
		//
		// fines
		out.println("<tr><td><table width=\"80%\">");
		out.println("<tr>");
		out.println("<th>Fine ($) </th>");
		out.println("<th>Court Costs ($)</th><td></td>"+
			    "<th>Total Due</th>"+
			    "<th>Balance</th></tr>");
		out.println("<tr><td>");
		out.println("<input name=\"fine\" size=\"10\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getFine()+"\" maxlength=\"10\" />");
		out.println("<input type=\"checkbox\" name=\"per_day\" size=\"10\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\"y\" "+per_day+" />per day</td><td>");
		out.println("<input name=\"court_cost\" size=\"10\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getCourt_cost()+
			    "\" maxlength=\"10\" /></td>");
		out.println("<td><input name=\"mcc_flag\" type=\"checkbox\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\"y\" " + mcc_flag + " />MCC </td>");
		//
		out.println("<td>$"+Helper.formatNumber(total_due)+"</td>");
		out.println("<td>$"+Helper.formatNumber(balance)+"</td></tr>");
		out.println("</table></td></tr>");
		out.println("<tr><td>&nbsp;</td></tr>");
		//
		// Date, time
		out.println("<tr><td><table><tr><td></td><th>Date "+
			    // "<div class=\"green\">mm/dd/yyyy</div>"+
			    "</th><th valign=\"bottom\">Time"+
			    "<div class=\"green\"> hh:mm</div></th><th></th>");
		out.println("<th>Date "+
			    // "<div class=\"green\">mm/dd/yyyy</div>"+
			    "</th><th valign=\"bottom\">Time"+
			    "<div class=\"green\"> hh:mm</div></th></tr>");
		out.println("<tr><th>Initial Hearing</th><td>");
		out.println("<input name=\"ini_hear_date\" size=\"10\" "+
			    " class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getIni_hear_date()+
			    "\" maxlength=\"10\" /></td><td>");
		out.println("<input name=\"ini_hear_time\" size=\"5\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " onkeyup=\"addColon(this,event)\" "+
			    " onchange=\"checkTime(this,event)\" "+
			    "value=\""+cCase.getIni_hear_time()+
			    "\" maxlength=\"5\" /></td>");
		out.println("<th>Contested Hearing</th><td>");
		out.println("<input name=\"contest_hear_date\" size=\"10\" "+
			    " class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getContest_hear_date()+
			    "\" maxlength=\"10\" /></td><td>");
		out.println("<input name=\"contest_hear_time\" size=\"5\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " onkeyup=\"addColon(this,event)\" "+
			    " onchange=\"checkTime(this,event)\" "+
			    "value=\""+cCase.getContest_hear_time()+
			    "\" maxlength=\"5\" /></td></tr>");
		out.println("<tr><th>Misc Hearing</th><td>");
		out.println("<input name=\"misc_hear_date\" size=\"10\" "+
			    " class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getMisc_hear_date()+
			    "\" maxlength=\"10\" /></td><td>");
		out.println("<input name=\"misc_hear_time\" size=\"5\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " onkeyup=\"addColon(this,event)\" "+
			    " onchange=\"checkTime(this,event)\" "+
			    "value=\""+cCase.getMisc_hear_time()+
			    "\" maxlength=\"5\" /></td>");
		out.println("<th>Pro Supp</th><td>");
		out.println("<input name=\"pro_supp_date\" size=\"10\" "+
			    " class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getPro_supp_date()+
			    "\" maxlength=\"10\" /></td><td>");
		out.println("<input name=\"pro_supp_time\" size=\"5\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " onkeyup=\"addColon(this,event)\" "+
			    " onchange=\"checkTime(this,event)\" "+
			    "value=\""+cCase.getPro_supp_time()+
			    "\" maxlength=\"5\" /></td><td>");
		out.println("<input type=\"checkbox\" name=\"pro_supp\" "+pro_supp+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\"y\" /> Flagged</td></tr>");
		out.println("<tr><th>Rule to Show Cause</th><td>");
		out.println("<input name=\"rule_date\" size=\"10\" "+
			    " class=\"date\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " value=\""+cCase.getRule_date()+"\" maxlength=\"10\" /></td><td>");
		out.println("<input name=\"rule_time\" size=\"5\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " onkeyup=\"addColon(this,event)\" "+
			    " onchange=\"checkTime(this,event)\" "+
			    "value=\""+cCase.getRule_time()+"\" maxlength=\"5\" /></td></tr>");
		out.println("</table></td></tr>");
		//
		out.println("<tr><td>&nbsp;</td></tr>");
		//
		// Dates
		out.println("<tr><td><table>"+
			    "<tr><th>Judgment Date</th>");
		out.println("<th>Compliance Date</th>");
		out.println("<th>Last Paid</th>");
		out.println("<th>Date closed</th>");
		out.println("<th>Closed comments</th>");			
		out.println("</tr>");
		out.println("<tr><td>");
		out.println("<input name=\"judgment_date\" size=\"10\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " class=\"date\" "+
			    " value=\""+cCase.getJudgment_date()+
			    "\" maxlength=\"10\" /></td><td>");
		out.println("<input name=\"compliance_date\" size=\"10\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " class=\"date\" "+
			    " value=\""+cCase.getCompliance_date()+
			    "\" maxlength=\"10\" /></td><td>");
		out.println("<input name=\"last_paid_date\" size=\"10\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " class=\"date\" "+
			    " value=\""+cCase.getLast_paid_date()+
			    "\" maxlength=\"10\" /></td>");
		out.println("<td>");			
		out.println("<input name=\"closed_date\" size=\"10\" "+
			    " tabindex=\""+(tabindex++)+"\""+
			    " class=\"date\" "+
			    " value=\""+cCase.getClosed_date()+
			    "\" maxlength=\"10\" /></td><td>");
		out.println("<select name=\"closed_comments\" tabindex=\""+(tabindex++)+"\">");
		for(int i=0;i<Inserts.closedArr.length;i++){
		    if(Inserts.closedArr[i].equals(cCase.getClosed_comments()))
			out.println("<option selected=\"selected\">"+Inserts.closedArr[i]+"</option>");
		    else
			out.println("<option>"+Inserts.closedArr[i]+"</option>");
		}
		out.println("</select></td></tr>");
		//
		out.println("</table></td></tr></table></td></tr>");
		///
		// Violation/Rental Address
		out.println("<tr><td>");
		out.println("<table width=\"80%\">");
		out.println("<tr><th>St. Number </th><th>Dir</th><th>"+
			    "St. Name</th><th>Type</th><th>Post Dir</th>");
		out.println("<th>Sub Unit Type</th><th>"+
			    "Sub Unit Num</th><th>"+
			    "Is Rental?</th></tr>");
		out.println("<tr>");
		out.println("<td><input name=\"street_num\" size=\"8\" "+
			    " maxlength=\"8\" value=\"\" />");
		out.println("</td><td>");
		out.println("<select name=\"street_dir\">");
		for(int i=0;i<Inserts.dirArr.length;i++){
		    out.println("<option>"+Inserts.dirArr[i]+"</option>");
		}
		out.println("</select></td><td>");
		out.println("<input name=\"street_name\" size=\"20\" "+
			    " maxlength=\"20\" value=\"\" />");
		out.println("</td>");
		out.println("<td>");
		out.println("<select name=\"street_type\">");
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
		out.println("</select></td>");
		out.println("<td><select name=\"sud_type\">");
		for(int i=0;i<Inserts.sudKeys.length;i++){
		    out.println("<option value=\""+
				Inserts.sudKeys[i]+"\">"+Inserts.sudInfo[i]+"</option>");
		}
		out.println("</select></td><td>");
		out.println("<input name=\"sud_num\" size=\"8\" "+
			    " maxlength=\"8\" value=\"\" />");
		out.println("</td><td align=\"left\">");
		out.println("<input type=\"checkbox\" name=\"rental_addr\" "+
			    " value=\"Y\" />Rental Address");
		out.println("</td></tr></table></td></tr>");
		addresses = cCase.getAddresses();
		if(addresses != null){
		    //
		    // show current addresses
		    //
		    out.println("<tr><td align=\"center\">");
		    out.println("<table><caption>Violation Address</caption>");
		    out.println("<tr><th>Select to Delete</th><th>Street Address</th>"+
				"<th>Is Rental Address?</th><th>Is Invalid?</th><th>"+
				"Edit</th></tr>");

		    for(int i=0;i<addresses.size();i++){
			Address addr = addresses.get(i);
					
			if(addr != null){
			    out.println("<tr><td><input type=\"checkbox\" name=\"delAddr\" value=\""+
					addr.getId()+"\" />*</td><td>"+
					addr.getAddress()+"</td><td>"+
					addr.getRental_addr()+"</td><td>"+
					addr.getInvalid_addr()+"</td><td>");
			    out.println("<a href=\""+url+"AddressEdit?id="+addr.getId()+
					"\" "+
					">Edit</a></td>");
			    out.println("</tr>");
			}
		    }
		    out.println("</table></td></tr>");
		}
		out.println("<tr><td>");
		out.println("<table><tr><td>");
		out.println("<strong>Comments:</strong><span class=\"green\">("); 
		out.println("Max 10,000 characters) </span>");
		out.println("<span id=\"rest\">&nbsp;</span>");
		out.println("</td></tr>");
		out.println("<tr><td>");
		out.println("<textarea name=\"comments\" id=\"comments\" "+
			    "rows=\"5\" cols=\"70\" onkeyup=\"doCount();\" "+
			    "tabindex=\""+(tabindex++)+"\">");
		out.println(Helper.replaceSpecialChars(cCase.getComments()));
		// out.println(StringEscapeUtils.escapeHtml4(cCase.getComments()));
								
		out.println("</textarea></td>");
		out.println("<td valign=\"top\">");
		out.println("<input type=\"button\" name=\"action\" "+
			    " onclick=\"window.open('"+url+
			    "NoteServ?"+
			    "id="+id+"')\" value=\"Edit Comments\" />");
		out.println("</td></tr>");
		out.println("</table></td></tr>");
		if(id.equals("")){
		    if(user.canEdit()){
			out.println("<tr><td class=\"right\">");					
			out.println("<input type=\"submit\" "+
				    " tabindex=\""+(tabindex++)+"\""+
				    " name=\"action\" value=\"Save\" />");
			out.println("</td></tr>"); 					
		    }
		    out.println("</table>");
		    out.println("</fieldset>");				
		    out.println("</form>");
		}
		else{ // save, update
		    //
		    out.println("<tr><td class=\"right\">");
		    out.println("<table width=\"90%\"><tr>");
		    if((user.canEdit() && user.isInDept("Legal")) ||
		       user.hasRole("Admin")){
			out.println("<td valign=\"top\"><input accesskey=\"u\" "+
				    " tabindex=\""+(tabindex++)+"\""+
				    " type=\"submit\" name=\"action\" value=\"Update\" />");
			out.println("</td>");
		    }
		    out.println("<td valign=\"top\"><input accesskey=\"p\" "+
				" type=\"button\" name=\"action\" value=\"Printable\" onclick=\"document.location='"+url+"CaseView?id="+id+"&amp;action=Printable'\" /> ");
		    out.println("</td><td valign=\"top\">");
		    out.println("<input type=\"button\" name=\"action\" accesskey=\"n\" "+
				" tabindex=\""+(tabindex++)+"\""+
				" onclick=\"document.location='"+url+
				"InsertDef?"+
				"id="+id+"'\" value=\"Add Defendants\" />");
		    out.println("</td><td valign=\"top\">");
		    out.println("<input type=\"button\" name=\"action\" accesskey=\"i\" "+
				" tabindex=\""+(tabindex++)+"\""+
				" onclick=\"document.location='"+url+
				"Violation?"+
				"id="+id+"'\" value=\"Add Violation\" />");
		    out.println("</td><td valign=\"top\">");
		    out.println("<input type=\"button\" name=\"action\" accesskey=\"i\" "+
				" tabindex=\""+(tabindex++)+"\""+
				" onclick=\"document.location='"+url+
				"ViolationAdd?"+
				"id="+id+"'\" value=\"Add Multiple Violations\" />");
		    out.println("</td><td valign=\"top\">");				
		    out.println("<input type=\"button\" name=\"action\" accesskey=\"y\" "+
				" tabindex=\""+(tabindex++)+"\""+
				" onclick=\"document.location='"+url+
				"bill?"+
				"id="+id+"'\" value=\"Payment\" />");
		    out.println("</td>");
		    out.println("<td valign=\"top\">");				
		    out.println("</td></tr>");
		    out.println("</table></td></tr>");
		    out.println("<tr><td "+
				" class=\"right\"><table width=\"90%\">");
		    out.println("<tr><td valign=\"top\">");
		    out.println("<input type=\"button\" name=\"action\" accesskey=\"y\" "+
				" tabindex=\""+(tabindex++)+"\""+
				" onclick=\"document.location='"+url+
				"LegalFileServ?cid="+id+
				"'\" value=\"Attachments\" />");
		    out.println("</td>");										
		    status = cCase.getStatus(); 
		    if(!status.equals("CL")){ // any but closed
			out.println("<td valign=\"top\">");
			out.println("<input type=\"button\" name=\"action\" accesskey=\"s\" "+
				    " tabindex=\""+(tabindex++)+"\""+
				    " onclick=\"window.open('"+url+
				    "StatusSheet?"+
				    "id="+id+"')\" value=\"Status Sheet\" />");
			out.println("</td>");
			if(user.isAdmin() && cCase.hasLetterText()){
			    out.println("<td valign=\"top\">");
			    out.println("<input type=\"button\" name=\"action\" "+
					" tabindex=\""+(tabindex++)+"\""+
					" onclick=\"window.open('"+url+
					"Letter?"+
					"id="+id+"')\" value=\"Mail Letter\" />");
			    out.println("</td>");
			}
		    }
		    if(user.canDelete()){
			out.println("<td valign=\"top\">");
			out.println("<input type=\"button\" name=\"action\" "+
				    "accesskey=\"e\" "+
				    " tabindex=\""+(tabindex++)+"\""+
				    " onclick=\"validateDelete2();\" "+
				    " value=\"Delete\" />");
			out.println("</td>");
		    }
		    out.println("</tr></table></td></tr>");
		    out.println("</table>");
		    out.println("<div class=\"green\">Access keys "+
				"are: Alt + U=Update, P=Printable, N=Add "+
				"Defendant, I=Add Violation, Y=Payment, "+
				"E=Delete</div>");
		    if(checkAval){				
			out.print("<div class=\"green center\" >* Check and click "+
				  "on Update button to delete the defendant(s)/address(s) "+
				  "from this case.</div>");
		    }				
		    out.println("</fieldset>");						
		    // out.println("</td></tr>");
		    // out.println("</table>");
		    // out.println("</fieldset>");
		    out.println("</form>");
		    List<CaseViolation> cvs = cCase.getCaseViolations();
		    if(cvs != null && cvs.size() > 0){
			out.println("<fieldset>");
			out.println("<table width=\"95%\">");
			out.println("<tr><td align=\"center\">");
			Helper.writeViolations(out,url, id, cvs);
			out.println("</td></tr></table>");
			out.println("</fieldset>");
		    }
		    if(cCase.hasFiles()){
			out.println("<fieldset>");
			Helper.printFiles(out, url , cCase.getFiles());
			out.println("</fieldset>");
		    }
		}
	    }
	}catch(Exception ex){
	    logger.error(ex);
	    out.println(ex);
	}
	out.print("<br /><br />");
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";

	out.println(Inserts.jsStrings(url));
	//
	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");	
	out.println("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();		
    }
    /**
     * Makes a list of types of cases and type of status
     *
     */
    String resetArrays(){
	String str ="",str2="", qq ="", ret="";
	int cnt = 0, i=0;
	try {
	    CaseTypeList cl = new CaseTypeList(debug);
	    String back = cl.find();
	    if(back.equals("")){
		caseTypes = cl.getTypes();
	    }
	    else{
		ret += back;
		logger.error(back);
	    }
	    StatusList sl = new StatusList(debug);
	    back = sl.find();
	    if(back.equals("")){
		statuses = sl.getStatuses();
	    }
	    else{
		ret += back;
		logger.error(back);
	    }
	    LawyerList ll = new LawyerList(debug);
	    back = ll.find();
	    if(back.equals("")){
		lawyers = ll.getLawyers();
	    }
	    else{
		ret += back;
		logger.error(back);
	    }
	    back = ll.composeLawyerTypeJS();
	    if(back.equals("")){
		lawyers = ll.getLawyers();
		lawyerTypeJsHash = ll.getLawyerTypeJsHash();
	    }
	    else{
		ret += back;
		logger.error(back);
	    }
	}
	catch(Exception e){
	    logger.error(e+" : "+qq);
	    ret += e;
	}
	return ret;
    }

}






















































