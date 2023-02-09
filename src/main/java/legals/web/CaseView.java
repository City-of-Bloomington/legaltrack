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
@WebServlet(urlPatterns = {"/CaseView"})
public class CaseView extends TopServlet{

    static final long serialVersionUID = 25L;
    static Logger logger = LogManager.getLogger(CaseView.class);
    static List<Status> statuses = null;
    static List<CaseType> caseTypes = null;
    static List<Lawyer> lawyers = null;

    String lawyerTypeJsHash = "";
    //
    /**
     * view the the legal Case
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
	    id="", citeId="",
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
	    if (name.equals("id")){
		cCase.setId(value);
		id = value;
	    }
	    else if(name.equals("action")){
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
	if(caseTypes == null){
	    message += resetArrays();
	}
	if(!id.equals("")){	
	    //
	    try{
		String back = cCase.doSelect();
		if(!back.equals("")){
		    logger.error(back);
		    message += "Could not retreive data: "+back;
		    success = false;
		    id = "";
		    action = "";
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
		message += " Could not delete data ";
		message += ex;
		success = false;
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
	    per_day = cCase.getPer_day();
	    mcc_flag = cCase.getMcc_flag();
	    pro_supp = cCase.getPro_supp();
	    court_cost = cCase.getCourt_cost();
	    mcc_flag = cCase.getMcc_flag();
	}
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
	}
	if(action.startsWith("Print")){
	    out.println(Inserts.xhtmlHeaderInc);
	    out.println(Inserts.banner(url));
	}
	else{
	    out.println(Inserts.xhtmlHeaderInc);
	    out.println(Inserts.banner(url));
	    out.println(Inserts.menuBar(url, true));
	    out.println(Inserts.sideBar(url, user));

	}
	out.println("<div id=\"mainContent\">");				
	if(!message.equals("")){		
	    if(success){
		out.println("<p class=\"center\">"+message+"</p>");
	    }
	    else{
		out.println("<p class=\"warning\">"+message+"</p>");
	    }
	}		
	out.println("<div class=\"center\">");
	if(user.canEdit() && action.equals("")){
	    out.println("<h3 class=\"titleBar\">Edit Legal Case: <a href=\""+url+"CaseServ?action=Edit&id="+id+"\">"+id+"</a></h3>");
	}
	else{
	    out.println("<h3 class=\"titleBar\">View Legal Case ID:"+id+"</h3>");
	}
	out.println("</div>");
	//
	try{

	    out.println("<table width=\"90%\">");
	    //
	    defendants = cCase.getDefendants();
	    if(defendants != null && defendants.size() > 0){
		String defTitle[] = {"Full Name",
		    "Address","City, State"};
		String qq = "select count(*) from legal_defendents l,"+
		    "legal_def_case d where l.did = d.did and d.id = "+id;
		out.println("<tr><td colspan=\"2\" align=\"center\">");
		out.println("<table border=\"1\">");
		out.println("<caption>Defendant(s) Info</caption>");
		out.println("<tr>");
		for(int i=0;i<defTitle.length;i++)
		    out.println("<th>"+defTitle[i]+"</th>");
		out.println("</tr>");
		for(int i=0;i<defendants.size();i++){
		    Defendant def = defendants.get(i);
		    if(def != null){
			out.println("<tr>");						
			out.println("<td>"+def.getFullName()+"</td>");
			String str = def.getStreetAddress();
			if(str == null || str.equals("")) str="&nbsp;";
			out.println("<td>"+str+"</td>");
			str = def.getCityStateZip();
			if(str == null || str.equals("")) str="&nbsp;";
			out.println("<td>"+str+"</td>");
			out.println("</tr>");
		    }
		}
		out.println("</table></td></tr>");
	    }
	    //
	    out.println("<tr><td class=\"right\" width=\"20%\">");
	    out.println("<b>Type: </b></td><td class=\"left\"> ");
	    out.println(cCase.getCaseType()); // toString
	    out.println("</td></tr>");
	    out.println("<tr><td class=\"right\">");
	    out.println("<b>Status: </b></td><td class=\"left\"> ");
	    out.println(cCase.getCStatus());
	    out.println("</td></tr>");
	    if(true){
		CaseType ct = cCase.getCaseType();
		if(ct != null){
		    Department dp = ct.getDepartment();
		    if(dp != null){
			out.println("<tr><td class=\"right\"><b>Department: </b></td><td class=\"left\">");												
			out.println(dp);
			out.println("</td></tr>");
		    }
		}
	    }
	    out.println("<tr><td class=\"right\"><b>Attorney: </b></td><td class=\"left\">");
	    if(true){
		Lawyer ly = cCase.getLawyer();
		if(ly != null){
		    out.println(ly);
		}
	    }
	    out.println("</td></tr>");
	    out.println("<tr><td class=\"right\">");
	    out.println("<b>Citation #: </b></td><td class=\"left\">");
	    out.println(cCase.getCitation_num());
	    if(cCase.hasViolations()){
		out.println("&nbsp;&nbsp;(multiple)");
	    }
	    out.println("</td></tr>");
	    out.println("<tr><td class=\"right\">");
	    out.println("<b>Citation Date: </b></td><td class=\"left\">");
	    if(cCase.hasViolations()){
		out.println("&nbsp;&nbsp;(multiple)");
	    }
	    else{
		out.println(cCase.getCitation_date());
	    }
	    out.println("</td></tr>");		
	    out.println("<tr><td class=\"right\">");
	    out.println("<b>Date Received: </b></td><td class=\"left\">");
	    out.println(cCase.getReceived());
	    out.println("</td></tr>");
	    if(!cCase.getSent_date().equals("")){
		out.println("<tr><td class=\"right\">");
		out.println("<b>Sent: </b></td><td class=\"left\"> ");
		out.println(cCase.getSent_date());
		out.println("</td></tr>");
	    }
	    if(!cCase.getFiled().equals("")){
		out.println("<tr><td class=\"right\">");
		out.println("<b>Filed: </b></td><td class=\"left\"> ");
		out.println(cCase.getFiled());
		out.println("</td></tr>");
	    }
	    if(!cCase.getIni_hear_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Initial Hearing: "+
			    "</b></td><td class=\"left\">");
		out.println(cCase.getIni_hear_date());
		if(!cCase.getIni_hear_time().equals("")){
		    out.println(" at "+cCase.getIni_hear_time());
		}
		out.println("</td></tr>");
	    }
	    if(!cCase.getContest_hear_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Contested Hearing: "+
			    "</b></td><td class=\"left\">");
		out.println(cCase.getContest_hear_date());
		if(!cCase.getContest_hear_time().equals("")){
		    out.println(" at "+cCase.getContest_hear_time());
		}
		out.println("</td></tr>");
	    }
	    if(!cCase.getMisc_hear_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Misc Hearing: </b></td>"+
			    "<td class=\"left\">");
		out.println(cCase.getMisc_hear_date());
		if(!cCase.getMisc_hear_time().equals("")){
		    out.println(" at "+cCase.getMisc_hear_time());
		}
		out.println("</td></tr>");
	    }
	    if(!cCase.getPro_supp_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Pro Supp: </b></td><td class=\"left\">");
		out.println(cCase.getPro_supp_date());
		if(!cCase.getPro_supp_time().equals("")){
		    out.println(" at "+cCase.getPro_supp_time());
		}
		out.println("</td></tr>");
	    }
	    if(!cCase.getRule_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Rule to Show Cause Date: </b></td>"+
			    "<td class=\"left\">");
		out.println(cCase.getRule_date());
		if(!cCase.getRule_time().equals("")){
		    out.println(" at "+cCase.getRule_time());
		}
		out.println("</td></tr>");
	    }
	    //
	    if(!cCase.getJudgment_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Judgment Date: </b></td>"+
			    "<td class=\"left\">");
		out.println(cCase.getJudgment_date()+"</td><td>");
	    }
	    if(!cCase.getCompliance_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Compliance Date: "+
			    "</b></td><td class=\"left\">");
		out.println(cCase.getCompliance_date()+"</td></tr>");
	    }
	    if(!cCase.getLast_paid_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Last Paid</b></td><td class=\"left\">");
		out.println(cCase.getLast_paid_date()+"</td></tr>");
	    }
	    if(!cCase.getClosed_date().equals("")){
		out.println("<tr><td class=\"right\"><b>Date closed</b></td>"+
			    "<td class=\"left\">");
		out.println(cCase.getClosed_date()+"</td></tr>");
	    }
	    if(!cCase.getClosed_comments().equals("")){
		out.println("<tr><td class=\"right\"><b>Closed comments</b></td>"+
			    "<td class=\"left\">");
		out.println(Helper.replaceSpecialChars(cCase.getClosed_comments()) + "</td></tr>");
	    }
	    //
	    if(!cCase.getFine().equals("") && !cCase.getFine().equals("0")){
		out.println("<tr><td class=\"right\"><b>Fine: </b></td><td class=\"left\">$");
		out.println(cCase.getFine());
		if(!cCase.getPer_day().equals("")) out.println(" Per day");
		out.println("</td></tr>");
		out.println("<tr><td class=\"right\"><b>Court Costs</b></td>"+
			    "<td class=\"left\">$");
		out.println(cCase.getCourt_cost());
		if(!cCase.getMcc_flag().equals("")) out.println(" MCC ");
		out.println("</td></tr>");
		out.println("<tr><td class=\"right\"><b>Total Due</b></td><td class=\"left\">$");
		out.println(Helper.formatNumber(total_due)+"</td></tr>");
		out.println("<tr><td class=\"right\"><b>Balance</b></td><td class=\"left\">$");
		out.println(Helper.formatNumber(balance)+"</td></tr>");
	    }
	    addresses = cCase.getAddresses();
	    //
	    if(addresses != null){
		//
		// show current addresses
		//
		out.println("<tr><td colspan=\"2\" align=\"center\"><table border=\"1\">"+
			    "<caption>Violation Address(es)</caption>");
										
		out.println("<tr><th>Street Address</th>"+
			    "<th>Is Rental Address?</th><th>Is Invalid?</th>"+
			    "</tr>");

		for(int i=0;i<addresses.size();i++){
		    Address addr = addresses.get(i);
					
		    if(addr != null){
			out.println("<td class=\"left\">"+
				    addr.getAddress()+"</td><td class=\"left\">"+
				    addr.getRental_addr()+"&nbsp;</td><td class=\"left\">"+
				    addr.getInvalid_addr()+"&nbsp;</td><td class=\"left\">");
			out.println("</tr>");
		    }
		}
		out.println("</table></td></tr>");
	    }
	    //
	    if(!cCase.getComments().equals("")){
		out.println("<tr><td class=\"right\" valign=\"top\"><b>Comments:</b></td><td class=\"left\">");
		out.println(Helper.replaceSpecialChars(cCase.getComments()));
		out.println("</td></tr>");
	    }
	    //
	    if(true){
		List<CaseViolation> cvs = cCase.getCaseViolations();
		if(cvs != null && cvs.size() > 0){
		    out.println("<tr><td colspan=\"2\" align=\"center\">");
		    out.println("<table border=\"1\">");
		    out.println("<caption>Violations </caption>");
		    out.println("<tr>"+
				"<th>Violations </th>"+
				"<th>Fines</th>"+
				"<th>Citations #</th>"+
				"<th>Dates</th></tr>");
		    float ff = 0f, total=0;
		    for(int i=0;i<cvs.size();i++){
			ff = 0f;
			CaseViolation cv = cvs.get(i);
			String str = cv.getViolCat().getCategory();
			if(str == null) str = "&nbsp;";
			if(str.length() > 30)
			    str = str.substring(0,30);
			out.println("<tr>");
			out.println("<td class=\"left\">"+str+"</td>");
			str = cv.getAmount();
			if(str != null){
			    try{
				ff = Float.parseFloat(str);
				if (ff > 0){
				    total += ff;
				}
			    }catch(Exception ex){}  
			}
			out.println("<td class=\"right\">$"+str+"</td>");
			str = cv.getCitations();
			if(str.equals("")) str = "&nbsp;";
			out.println("<td class=\"left\">"+str+"</td>");
			str = cv.getDates();
			if(str.equals("")) str = "&nbsp;";
			out.println("<td class=\"left\">"+str+"</td>");		
			out.println("</tr>");
		    }
		    if(cvs.size() > 1 ){
			out.println("<tr><td class=\"left\">Total</td><td class=\"right\">$"+
				    total+"</td><td colspan=\"2\">&nbsp;</td></tr>");
		    }
		    out.println("</table></td></tr>");
		}
	    }
	    out.println("</table>");
	}catch(Exception ex){
	    logger.error(ex);
	    out.println(ex);
	}
	out.println("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();		
    }
    /**
     * Makes a list of types of cases and type of status
     *
     * @return a String of exceptions if any
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






















































