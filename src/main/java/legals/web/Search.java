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
//

@WebServlet(urlPatterns = {"/Search","/Browse"})
public class Search extends TopServlet {

    int maxlimit = 100; // limit on records
    static final long serialVersionUID = 22L;
    static Logger logger = LogManager.getLogger(Search.class);
    String bgcolor="silver";
    static List<Status> statuses = null;
    static List<CaseType> caseTypes = null;
    /**
     * Generates the form for the search engine.
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	boolean success = true;
	
	String name, value;
	//
	// New vars
	//
	String f_name = "", l_name="", 
	    street_num="",street_dir="", id="", citeId="",source="",
	    street_name="",street_type="",sud_type="",sud_num="",
	    rent_street_num="",rent_street_dir="",
	    rent_street_name="",rent_street_type="",rent_sud_type="",
	    rent_sud_num="", 
	    dob="",ssn="",cause_num="",case_type="",status="",
	    
	    ini_hear_date_from="",contest_hear_date_from="",
	    misc_hear_date_from="", received_from="", filed_from="",
	    judgment_date_from="",pro_supp_date_from="",
	    compliance_date_from="",sent_date_from="", 
	    last_paid_date_from="",closed_date_from="",
	    
	    ini_hear_date_to="",contest_hear_date_to="",
	    misc_hear_date_to="", received_to="", filed_to="",
	    judgment_date_to="",pro_supp_date_to="",compliance_date_to="",
	    sent_date_to="", last_paid_date_to="",closed_date_to="",
	    
	    ini_hear_time="",contest_hear_time="",
	    misc_hear_time="", street_address="",
	    closed_comments="", comments="",

	    judgment_amount="", 
	    fine="",court_cost="", message="";

	//
	// old stuff
	//
	int category = 0, resolved_month=0, category2=0;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	

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
	if(statuses == null){
	    message += resetArrays();
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");

	out.println("<script type=\"text/javascript\">");
	out.println("                            ");
	out.println("  function checkChoice(field, i) {       ");
	out.println(" if (i == 0) { // All                    ");
	out.println(" if (field.checked == true) {            ");
	out.println(" var j= document.myForm.elements.length; ");
	//out.println(" alert(\"total elements \"+j);         ");
	out.println(" for (i = j-1; i >j-25; i--){            ");
	//out.println(" alert(\"total elements \"+document.myForm.elements[i].name); ");
	out.println("    document.myForm.elements[i].checked = false;   ");
	out.println("   }}}   ");
	out.println("   else if (i == 2) {               ");
	out.println(" if (field.checked == true) {       ");
	out.println("    document.myForm.cc_all.checked = false;   ");
	out.println(" var j= document.myForm.elements.length; ");
	//out.println(" alert(\"total elements \"+j); ");
	out.println(" for (i = j-1; i >j-24; i--){ ");
	//out.println(" alert(\"total elements \"+document.myForm.elements[i].name); ");
	out.println("    document.myForm.elements[i].checked = false;   ");
	out.println("   }}}   ");
	out.println("   else  {  // A checkbox other than Any selected. ");
	out.println("    if (field.checked == true) { ");
	out.println("      document.myForm.cc_all.checked = false;  ");
	out.println("      document.myForm.cc_non.checked = false;  ");
	out.println("  }}}                                          ");
	out.println("  function validateForm(){	           ");
	out.println("  with(document.myForm){              ");
	out.println("if(!checkDate(date_from))return false;       ");
	out.println("if(!checkDate(date_to))return false;       ");
	out.println(" }	         		                    ");
	out.println("  return true;			                ");
	out.println(" }	         		                    ");
	out.println(" </script>				            ");

	//
	out.println("<center><table align=\"center\" border>");
	out.println("<tr><td align=center style=\"background-color:navy; color:white\">"+
		    "<b>Case Search</b></td></tr>");
	out.println("<tr><td bgcolor=\""+bgcolor+"\">"); // #e0e0e0 light gray
	out.println("<table>");
	out.println("<form name=\"myForm\" method=\"post\" onsubmit=\"return validateForm()\">");

	// id
	out.println("<tr><td align=\"left\"><b>Case ID: </b></td><td align=\"left\">");
	out.println("<input type=\"text\" name=\"id\" maxlength=\"8\" size=\"8\"" +
		    " tabindex=\"1\" value=\"\" /></td><td align=\"right\">");
	out.println("<b>Pro Supp?</b></td><td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"pro_supp\" value=\"y\" tabindex=\"2\">"+
		    " Flagged</td></tr> ");
	out.println("<tr><td align=\"left\"><b>Cause #: </b></td><td align=\"left\">");
	out.println("<input name=\"cause_num\" size=\"20\" tabindex=\"3\" "+
		    "value=\""+cause_num+"\" maxlength=\"30\" />");
	out.println("</td><td align=\"right\">");
	out.println("<b>Type: </b></td><td align=\"left\">");
	out.println("<select name=\"case_type\" tabindex=\"4\">");
	out.println("<option selected value=\"\">\n");
	if(caseTypes != null){
	    for(CaseType ctype: caseTypes){
		out.println("<option value=\""+
			    ctype.getId()+"\">"+ctype+"</option>");
	    }
	}
	out.println("</select></td></tr>");

	out.println("<tr><td align=\"left\">");
	out.println("<b>Status: </b></td><td align=\"left\">");
	out.println("<select name=\"status\" tabindex=\"4\">");
	out.println("<option value=\"\">All</option>\n");
	if(statuses != null){
	    for(Status cstatus: statuses){
		out.println("<option value=\""+
			    cstatus.getId()+"\">"+cstatus+"</option>");
	    }
	}
	out.println("</select>");
	out.println("</td><td align=\"left\"><b>Closed Comments:</b></td><td align=\"left\">");
	out.println("<select name=\"closed_comments\" tabindex=\"5\">");
	out.println("<option value=\"\">All</option>");
	for(int i=0;i<Inserts.closedArr.length;i++){
	    out.println("<option>"+Inserts.closedArr[i]+"</option>");
	}
	out.println("</select></td></tr>");
	out.println("<tr><td align=\"left\">");
	out.println("<b>Citation #: </b></td><td align=\"left\">");
	out.println("<input name=\"citation_num\" size=\"11\" maxlength=\"11\" "+
		    " value=\"\" /></td><td></td></tr>");	
	out.println("</table></td></tr>");
	// 
	// Name
	out.println("<tr><td bgcolor=\""+bgcolor+"\">"+
		    "<table><tr><td></td><td>Last Name</td><td>First Name"+
		    "</td><td>SSN</td><td>DLN</td></tr>");
	out.println("<tr><td><b>Defendant:</b></td><td><input name=l_name "+
		    " tabindex=\"6\" size=\"20\" maxlength=\"30\" value=\"\" />");
	out.println("</td><td>");
	out.println("<input name=\"f_name\" size=\"20\" maxlength=\"30\" tabindex=\"7\" value=\"\" />");
	out.println("</td><td>");
	out.println("<input name=\"ssn\" size=\"11\" maxlength=\"11\" tabindex=\"8\" value=\"\" />");
	out.println("</td><td>");
	out.println("<input name=\"dln\" size=\"11\" maxlength=\"11\" tabindex=\"8\" value=\"\">");		
	out.println("</td><td>");
	out.println("</table></td></tr>");	
	//
	// Address Search
	out.println("<tr><td align=\"center\" style=\"background-color:navy; color:white\">"+
		    "<b>Address Search</b></td></tr>");

	out.println("<tr><td bgcolor=\""+bgcolor+"\"><table><tr><td>"+
		    "<input type=\"radio\" name=\"addrType\" value=\"defendant\" checked=\"checked\" /></td><td>");
	out.println("Defendant's Address, or </td><td>");
	out.println("<input type=\"radio\" name=\"addrType\" value=\"violation\" /></td><td>");
	out.println("Violation Address </td></tr></table></td></tr>");
	//
	out.println("<tr><td bgcolor=\""+bgcolor+"\" align=\"left\"><table>");
	out.println("<caption>Address</caption>");
	out.println("<tr><td></td><td>St. No. </td><td>Dir</td><td>"+
		    "St. Name</td></tr>");
	out.println("<tr><td rowspan=\"5\" valign=\"top\"><b>Address</b>");
	out.println("</td><td><input name=\"street_num\" size=\"8\" tabindex=\"17\" "+
		    "maxlength=\"8\" value=\""+street_num+"\" />");
	out.println("</td><td>");
	out.println("<select name=\"street_dir\" tabindex=\"18\" >");
	for(int i=0;i<Inserts.dirArr.length;i++){
	    out.println("<option>"+Inserts.dirArr[i]);
	}
	out.println("</select></td><td>");
	out.println("<input name=\"street_name\" size=\"20\" tabindex=\"19\" "+
		    "maxlength=\"20\" value=\""+street_name+"\" />");
	out.println("</td></tr>");
	out.println("<tr><td>St. Type</td><td>Sub Unit Type</td><td>"+
		    "Sub Unit Num</td></tr>");
	//
	out.println("<tr><td>");
	out.println("<select name=\"street_type\" tabindex=\"20\">");
	for(int i=0;i<Inserts.streetKeys.length;i++){
	    out.println("<option value=\""+
			Inserts.streetKeys[i]+"\">"+Inserts.streetInfo[i]+"</option>");
	}
	out.println("</select></td><td>");
	out.println("<select name=\"sud_type\" tabindex=\"21\">");
	for(int i=0;i<Inserts.sudKeys.length;i++){
	    out.println("<option value=\""+
			Inserts.sudKeys[i]+"\">"+Inserts.sudInfo[i]+"</option>");
	}
	out.println("</select></td><td>");
	out.println("<input name=\"sud_num\" size=\"8\" tabindex=\"22\" "+
		    "maxlength=\"8\" value=\""+sud_num+"\" />");

	out.println("</td></tr></table></td></tr>");

	out.println("<tr><td bgcolor=\""+bgcolor+"\">");
	out.println("<table>");
	out.println("<tr><td align=\"left\"><b>Address Validity?</b></td><td align=\"right\">");
	out.println("<input type=\"radio\" name=\"invld_addr\" value=\"Y\" tabindex=\"23\" /></td>"+
		    "<td align=\"left\">Invalid, </td><td align=\"right\">");
	out.println("<input type=\"radio\" name=\"invld_addr\" value=\"N\" tabindex=\"24\" /></td>"+
		    "<td align=\"left\">Valid </td></tr>");
	out.println("</td></tr></table></td></tr>");
	//
	// Dates table
	out.println("<tr><td align=center style=\"background-color:navy; color:white\">"+
		    "<b>Date Search</b></td></tr>");
	out.println("<tr><td bgcolor=\""+bgcolor+"\"><table>");
	out.println("<tr><td colspan=\"4\" align=\"left\">To search by date:"+
		    "<ul><li>First pick the specified date from the list below </li>"+
		    "<li> Then enter the date range in the fields below.</li>"+
		    "</ul></td></tr>");
	out.println("<tr><td colspan=\"4\" align=\"left\"><b>Pick the Date</b></td></tr>");
	out.println("<tr>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" value=\"received\""+
		    " checked=\"checked\" />Received</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" value=\"filed\" />"+
		    "Filed</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"ini_hear_date\" />Initial Hearing</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"contest_hear_date\" />Contested Hearing</td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"misc_hear_date\" />Misc Hearing</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"pro_supp_date\" />Pro Supp.</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"judgment_date\" />Judgement</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"compliance_date\" />Compliance</td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"sent_date\" />Sent</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"closed_date\" />Closed</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"addr_req_date\" />Address Request</td>");
	out.println("<td align=\"left\"><input type=\"radio\" name=\"which_date\" "+
		    "value=\"last_paid_date\" />Last Paid</td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td colspan=\"2\" align=\"left\">"+
		    "<input type=\"radio\" name=\"which_date\" value=\"rule_date\" />"+
		    "Rule to Show Cause</td>");
	out.println("<td align=\"left\">"+
		    "<input type=\"radio\" name=\"which_date\" value=\"e41_date\" />"+
		    "41.e Date</td>");
	out.println("<td align=\"left\">"+
		    "<input type=\"radio\" name=\"which_date\" value=\"dob\" />"+
		    "Defendant D.O.B</td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td colspan=\"2\" align=\"left\">"+
		    "<input type=\"radio\" name=\"which_date\" value=\"paid_date\" />"+
		    "Receipt Date</td>");
	out.println("</tr>");				
	out.println("<tr><td colspan=\"4\"><b>Enter the date range </b></td></tr>");
	out.println("<tr><td>&nbsp;</td><td>From (mm/dd/yyyy)</td><td>To (mm/dd/yyyy)</td></tr>");
	out.println("<tr><td><b>Date:</b></td><td>");
	out.println("<input name=\"date_from\" class=\"date\" value=\"\" size=\"10\" maxlength=\"10\" />");
	out.println("</td><td>");	
	out.println("<input name=\"date_to\" class=\"date\" value=\"\" size=\"10\" maxlength=\"10\" />");	
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	//
	// Fines
	out.println("<tr><td align=\"center\" style=\"background-color:navy; color:white\">"+
		    "<b>Fines Search</b></td></tr>");
	out.println("<tr><td bgcolor=\""+bgcolor+"\"><table><tr><td>");
	out.println("<b>Pick Fee Type</b></td></tr>");
	out.println("<tr><td><b>Fee Type:</b>");
	out.println("<input type=\"radio\" name=\"which_fee\" checked=\"checked\" value=\"fine\" />Fine ");
	out.println("<input type=\"radio\" name=\"which_fee\" value=\"court_cost\"/>Court Cost");
	out.println("<input type=\"radio\" name=\"which_fee\" value=\"judgment_amount\"/>Judgement Amount");
	out.println("</td></tr>");
	out.println("<tr><td><b>Enter the fee range</b></td></tr>");
	out.println("<tr><td>"+
		    "<table><tr><td>&nbsp;</td>"+
		    "<td>From($)</td><td>To($)</td></tr>");
	// Fee
	out.println("<tr><td align=\"right\"><b>Fees:</td>");
	out.println("<td><input name=\"fee_from\" value=\""+
		    "\" tabindex=\"68\" size=\"8\" maxlength=\"8\" /></td>");
	out.println("<td><input name=\"fee_to\" value=\""+
		    "\" tabindex=\"69\" size=\"8\" maxlength=\"8\" /></td></tr>");
	out.println("</table></td></tr></table></td></tr>");
	//
	out.println("<tr><td bgcolor=\""+bgcolor+"\">");
	out.println("<table>"+
		    "<tr><td valign=\"bottom\">");
	out.println("<b>Comments:</b></td><td><font color=\"green\" size=\"-1\" >");
	out.println("key words</font><br>");
	out.println("<input name=\"comments\" value=\""+
		    "\" tabindex=\"74\" size=\"30\" maxlength=\"80\" /></td></tr>"+
		    "</table></td></tr>");
	out.println("<tr><td bgcolor=\""+bgcolor+"\">");
	out.println("<table>"+
		    "<tr><td valign=\"bottom\">");
	out.println("<b>Sort by:</b></td><td><select name=\"sortby\">");
	out.println("<option value=\"c.id\">ID</option>");
	out.println("<option value=\"l.cause_num\">Cause #</option>");
	out.println("<option value=\"d.l_name,d.f_name\""+
		    " selected>Defendat Name</option>");
	out.println("<option value=\"c.ini_hear_date\">Initial Hearing</option>");
	out.println("<option value=\"c.pro_supp_date\">Pro Supp Date</option>");
	out.println("<option value=\"c.filed\">Filed Date</option>");
	out.println("<option value=\"c.judgment_date\">Judgment Date</option>");
	out.println("<option value=\"c.compliance_date\">Compliance Date</option>");
	out.println("</select></td></tr>");
	out.println("</table></td></tr>");
	//
	out.println("<tr><td align=center><input type=\"submit\" "+
		    "name=\"browse\" "+
		    "value=\"Search\" /></td></tr>");
	// 
	// check fields table
	out.println("<tr><td>"+
		    "<center><table><tr><td colspan=\"4\" "+
		    "align=\"center\"><font "+
		    "size=\"+1\" color=\"green\"><b>Show only the checked fields "+
		    "below</b></font></td><tr>");
	//
	// 1st row
	out.println("<tr><td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_all\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,0)\""+
		    ">Select all</td>");
	out.println("<td>&nbsp;</td>");
	out.println("<td>&nbsp;</td>");
	out.println("<td align=\"left\"><input type=\"checkbox\" name=\"cc_non\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,2)\""+
		    "/>DeSelect all</td></tr>");
	//
	// 2nd
	out.println("<tr>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_name\" checked "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Defendent Name</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_dob\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>D.O.B</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_case_num\" "+
		    "checked value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Cause #</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_def_addr\" checked "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Defendant Address</td>");
	out.println("</tr>");
	//
	// 3rd row
	out.println("<tr>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_case_type\" checked "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Case Type</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_status\" "+
		    "value=\"checked\" checked "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Status</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_addr\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Violation Address</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_received\" checked "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Received Date</td>");
	out.println("</tr>");
	//
	// 4th row
	out.println("<tr>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_sent\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Sent Date</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_filed\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Filed Date</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_ini\" checked "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Initial Hearing</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_contest\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Contest Hearing</td>");
	out.println("</tr>");
	//
	// 5th row
	out.println("<tr>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_misc\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Misc Hearing</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_judg_date\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Judgment Date</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_pro_supp\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Pro Supp.</td>");
	out.println("<td align=\"left\">");
										   
	out.println("<input type=\"checkbox\" name=\"cc_compliance\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Compliance Date</td>");
	out.println("</tr>");
	//
	// 6th row
	out.println("<tr>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_judg_amount\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Judgment Amount</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_fine\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Fine</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_court_cost\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Court Cost</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_close_date\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Close Date</td>");
	out.println("</tr>");
	//
	// 7th row
	out.println("<tr>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_paid_date\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Last Paid Date</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_rule_date\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Rule Show Cause</td>");
	out.println("<td align=\"left\">");
	out.println("<input type=\"checkbox\" name=\"cc_close_com\" "+
		    "value=\"checked\" "+
		    "onclick=\"checkChoice(this,1)\""+
		    "/>Close Commnets</td>");
	out.println("</tr>");
	out.println("</form></table></td></tr>");
	out.println("</table><br /><br />");
	out.println(Inserts.jsStrings(url));	
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");			
	out.println("</div>");
		
	out.print("</body></html>");
	out.close();
    }
    /**
     * Processes the search request and arranges the output in a table.
     * @param req
     * @param res
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	res.setContentType("text/html");
	String titles[] ={"ID",
	    "Name ",
	    "Def. Address",
	    "D.O.B",
	    "Cause #",   // 5

	    "Viol. Address",
	    "Case Type",
	    "Status",
	    "Received Date",  
	    "Sent Date",   // 10

	    "File Date",
	    "Initial Hearing",
	    "Contested Hearing",
	    "Misc Hearing",  
	    "Pro Supp. Date", // 15

	    "Judgment Date",
	    "Compliance Date",
	    "Judgment Aount",    
	    "Fine",     
	    "Court Costs", // 20

	    "Last Paid Date",
	    "Closed Date",
	    "Rule to Show Cause",
	    "Closed Comments",
	    "Comments" // 25
	};  


	// fields to be shown
	boolean show[] = { true,true,false,false,false,
	    false,false,false,false,false,  
	    false,false,false,false,false,
	    false,false,false,false,false,
	    false,false,false,false,false

	};
	//
	String f_name = "", l_name="",  invld_addr="",
	    street_num="",street_dir="", id="", citeId="",source="",
	    street_name="",street_type="",sud_type="",sud_num="",
	    rent_street_num="",rent_street_dir="",
	    rent_street_name="",rent_street_type="",rent_sud_type="",
	    rent_sud_num="", pro_supp_time="",
	    dob_from="",dob_to="",dob_on="",ssn="",cause_num="",
	    case_type="",status="", pro_supp="",
	    which_date="", date_from="", date_to="",
	    closed_comments="", comments="", city="",state="",zip="",
	    judg_amount_from="", street_address="",
	    fine_from="",court_cost_from="",
	    judg_amount_to="", sortby="",
	    fine_to="",court_cost_to="", addrType="";
	String which_fee="", fee_from="", fee_to="";
	String outputType="table", message="";

	PrintWriter out = res.getWriter();			  
	boolean showAll = false, success = true;
	boolean connectDbOk = false, defTbl = false, addrTbl=false;
	String name, value;

	Enumeration<String> values = req.getParameterNames();
	CaseList cl = new CaseList(debug);
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();

	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();

	    if (name.equals("id")){
		id = value;
	    }
	    else if (name.equals("sortby")) {
		cl.setSortby(value);
	    }
	    else if (name.equals("cause_num")) {
		cl.setCause_num(value);
	    }
	    else if (name.equals("status")){
		cl.setStatus(value);
	    }
	    else if (name.equals("pro_supp")){
		pro_supp = value;
		cl.setPro_supp(value);
	    }
	    else if (name.equals("f_name")) {
		cl.setF_name(value);
	    }
	    else if (name.equals("l_name")) {
		cl.setL_name(value);
	    }
	    else if (name.equals("citation_num")) {
		cl.setCitation_num(value);
	    }
	    else if (name.equals("closed_comments")){
		cl.setClosed_comments(value);
	    }						
	    else if (name.equals("addrType")) {
		cl.setAddrType(value);
	    }
	    else if (name.equals("street_num")) {
		cl.setStreet_num(value);
	    }
	    else if (name.equals("street_dir")) {
		cl.setStreet_dir(value);
	    }
	    else if (name.equals("street_name")) {
		cl.setStreet_name(value);
	    }
	    else if (name.equals("street_address")) {
		cl.setStreet_address(value);
	    }						
	    else if (name.equals("street_type")) {
		cl.setStreet_type(value);
	    }
	    else if (name.equals("sud_type")) {
		cl.setSud_type(value);
	    }
	    else if (name.equals("sud_num")) {
		cl.setSud_num(value);
	    }
	    else if (name.equals("city")) {
		cl.setCity(value);
	    }
	    else if (name.equals("state")) {
		cl.setState(value);	
	    }
	    else if (name.equals("zip")) {
		cl.setZip(value);
	    }
	    else if (name.equals("dln")) {
		cl.setDln(value);
	    }		
	    else if (name.equals("invld_addr")) {
		invld_addr = value;
		cl.setInvld_addr(value);
	    }
	    else if (name.equals("date_from")) {
		cl.setDate_from(value);
	    }
	    else if (name.equals("date_to")) {
		cl.setDate_to(value);
	    }
	    else if (name.equals("which_date")) {
		cl.setWhich_date(value);
	    }
	    else if (name.equals("case_type")) {
		cl.setCase_type(value);
	    }
	    else if (name.equals("fee_from")) {
		cl.setFee_from(value);
	    }
	    else if (name.equals("fee_to")) {
		cl.setFee_to(value);
	    }
	    else if (name.equals("which_fee")) {
		cl.setWhich_fee(value);
	    }
	    else if (name.equals("comments")){
		cl.setComments(value);
	    }
	    else if (name.equals("cc_name")){
		show[1] = true;
	    }
	    else if (name.equals("cc_def_addr")){
		show[2] = true;
	    }
	    else if (name.equals("cc_dob")){
		show[3] = true;
	    }
	    else if (name.equals("cc_case_num")){
		show[4] = true;
	    }
	    else if (name.equals("cc_addr")){
		show[5] = true;
	    }
	    else if (name.equals("cc_case_type")){
		show[6] = true;
	    }
	    else if (name.equals("cc_status")){
		show[7] = true;
	    }
	    else if (name.equals("cc_received")){
		show[8] = true;
	    }
	    else if (name.equals("cc_sent")){
		show[9] = true;
	    }
	    else if (name.equals("cc_filed")){
		show[10] = true;
	    }
	    else if (name.equals("cc_ini")){
		show[11] = true;
	    }
	    else if (name.equals("cc_contest")){
		show[12] = true;
	    }
	    else if (name.equals("cc_misc")){
		show[13] = true;
	    }
	    else if (name.equals("cc_pro_supp")){
		show[14] = true;
	    }
	    else if (name.equals("cc_judg_date")){
		show[15] = true;
	    }
	    else if (name.equals("cc_compliance")){
		show[16] = true;
	    }
	    else if (name.equals("cc_judg_amount")){
		show[17] = true;
	    }
	    else if (name.equals("cc_fine")){
		show[18] = true;
	    }
	    else if (name.equals("cc_court_cost")){
		show[19] = true;
	    }
	    else if (name.equals("cc_paid_date")){
		show[20] = true;
	    }
	    else if (name.equals("cc_close_date")){
		show[21] = true;
	    }
	    else if (name.equals("cc_rule_date")){
		show[22] = true;
	    }
	    else if (name.equals("cc_close_com")){
		show[23] = true;
	    }
	    else if (name.equals("cc_all")){
		showAll = true;
	    }
	}
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"/Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"/Login";
	    res.sendRedirect(str);
	    return; 
	}
	if(!id.equals("")){
	    String str  = "";
	    if(user.canEdit()){
		str = url+"CaseServ?id="+id;
	    }
	    else{
		str = url+"CaseView?id="+id;
	    }
	    res.sendRedirect(str);
	    return; 
	}
	List<Case> cases = null;
	String back = cl.lookFor();
	if(!back.equals("")){
	    message += back;
	    success = false;
	}
	else{
	    cases = cl.getCases();
	    if(cases != null){
		if(cases.size() == 1){
		    Case ccase = cases.get(0);
		    if(ccase != null){
			String str = "";
			if(user.canEdit()){
			    str = url+"CaseServ?id=" + ccase.getId();
			}
			else{
			    str = url+"CaseView?id=" + ccase.getId();
			}
			res.sendRedirect(str);
			return; 
		    }
		}
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	//
	// using CaseList class
	//
	if(!success){
	    out.println("<center><h2>"+message+"</h2></center>");
	}
	else{
	    List<Defendant> defs = null;
	    List<Address> addrs = null;
	    List<String> causes = null;
	    int row = 0;
	    if(cases == null || cases.size() == 0){
		out.println("<h4> No match found.</h4>");
	    }
	    else if(cases.size() > 0){
		out.println("<h4> Total " + cases.size() + " record.</h4>");
		out.println("<table border>");
		try{
		    for(Case ccase: cases){
			String dobStr="", causeStr="", defAddrStr="";
			if(row%20 == 0){
			    out.println("<tr>");
			    for (int c = 0; c < titles.length; c++){
				if(show[c] || showAll)
				    out.println("<th>"+titles[c]+
						"</th>");
			    }
			    out.println("</tr>");						
			}
			if(row%3 == 0)
			    out.println("<tr bgcolor=\"#CDC9A3\">");
			else
			    out.println("<tr>");
			row++;
			String color = "";
			if(show[0] || showAll){
			    out.println("<td valign=\"top\" align=\"left\">");
			    if(user.canEdit()){
				out.println("<a href=\"" + url +
					    "CaseServ?id=" + ccase.getId() + 
					    "\">" + 
					    ccase.getId() + "</a></td>");
			    }
			    else{
				out.println("<a href=\"" + url +
					    "CaseView?id=" + ccase.getId() + 
					    "\">" + 
					    ccase.getId() + "</a></td>");
			    }
			}
			String all = "", str ="";					
			if(show[1] || show[3] || show[4] || showAll){
			    defs = ccase.getDefendants();
			    if(defs != null && defs.size() > 0){
				for(Defendant dd: defs){
				    str = dd.getFullName();
				    if(!str.equals("")){
					if(!all.equals("")) all += "<br /> ";
					all += str;
				    }
				    str = dd.getDob();
				    if(!str.equals("")){
					if(!dobStr.equals("")) dobStr += ", ";
					dobStr += str;
				    }
				    str = dd.getStreetAddress();
				    if(defAddrStr.indexOf(str) == -1){
					if(!defAddrStr.equals("")) defAddrStr += ", ";
					defAddrStr += str;
				    }
				}
			    }
			}
			if(show[1] || showAll){
			    if(all.equals("")) all = "&nbsp;";
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[2] || showAll){
			    all = defAddrStr ;
			    if(all.equals("")) all = "&nbsp;";
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[3] || showAll){
			    all = dobStr ;
			    if(all.equals("")) all = "&nbsp;";
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[4] || showAll){	
			    causes = ccase.getCauseNums();
			    all = "";
			    if(causes != null && causes.size() > 0){
				for(String str2: causes){
				    if(str2 != null && !str2.equals(""))
					if(!all.equals("")) all += ", ";
				    all += str2;		
				}
			    }
			    if(all.equals("")) all = "&nbsp;";
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");	
			}
			if(show[5] || showAll){
			    addrs = ccase.getAddresses();
			    all = ""; str ="";
			    if(addrs != null && addrs.size() > 0){
				for(Address ad: addrs){
				    str = ad.getAddress();
				    if(!str.equals("")){
					if(!all.equals("")) all += ", ";
					all += str;
				    }
				}
			    }
			    if(all.equals("")) all = "&nbsp;";
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[6] || showAll){
			    str = "&nbsp;" ;
			    CaseType ct = ccase.getCaseType();
			    if(ct != null) str = ct.getDesc();
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[7] || showAll){
			    str = "&nbsp;" ;
			    Status st = ccase.getCStatus();
			    if(st != null) str = st.getDesc();
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[8] || showAll){
			    str = "" ;
			    str = ccase.getReceived();
			    if(str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[9] || showAll){
			    str = "" ;
			    str = ccase.getSent_date();
			    if(str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[10] || showAll){
			    str = "" ;
			    str = ccase.getFiled();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[11] || showAll){
			    str = "" ; all = "";
			    str = ccase.getIni_hear_date();
			    if(str != null && !str.equals("")){
				all = str;
			    }
			    str = ccase.getIni_hear_time();
			    if(str != null && !str.equals("")){
				if(!all.equals("")) all += " ";
				all += str;
			    }
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[12] || showAll){
			    str = "" ; all="";
			    str = ccase.getContest_hear_date();
			    if(str != null && !str.equals("")){
				all = str;
			    }
			    str = ccase.getContest_hear_time();
			    if(str != null && !str.equals("")){
				if(!all.equals("")) all += " ";
				all += str;
			    }
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[13] || showAll){
			    str = "" ; all = "";
			    str = ccase.getMisc_hear_date();
			    if(str != null && !str.equals("")){
				all = str;
			    }
			    str = ccase.getMisc_hear_time();
			    if(str != null && !str.equals("")){
				if(!all.equals("")) all += " ";
				all += str;
			    }
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[14] || showAll){
			    str = "" ; all = "";
			    str = ccase.getPro_supp_date();
			    if(str != null && !str.equals("")){
				all = str;
			    }
			    str = ccase.getPro_supp_time();
			    if(str != null && !str.equals("")){
				if(!all.equals("")) all += " ";
				all += str;
			    }
			    out.println("<td valign=\"top\" align=\"left\">"+all+"</td>");
			}
			if(show[15] || showAll){
			    str = "" ;
			    str = ccase.getJudgment_date();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[16] || showAll){
			    str = "" ;
			    str = ccase.getCompliance_date();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[17] || showAll){
			    str = "" ;
			    str = ccase.getJudgment_amount();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[18] || showAll){
			    str = "" ;
			    str = ccase.getFine();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[19] || showAll){
			    str = "" ;
			    str = ccase.getCourt_cost();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[20] || showAll){
			    str = "" ;
			    str = ccase.getLast_paid_date();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[21] || showAll){
			    str = "" ;
			    str = ccase.getClosed_date();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[22] || showAll){
			    str = "" ;
			    str = ccase.getRule_date();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[23] || showAll){
			    str = "" ;
			    str = ccase.getClosed_comments();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			if(show[24] || showAll){
			    str = "" ;
			    str = ccase.getComments();
			    if(str == null || str.equals("")) str = "&nbsp;"; 
			    out.println("<td valign=\"top\" align=\"left\">"+str+"</td>");
			}
			out.println("</tr>");
		    }
		}catch(Exception ex){
		    logger.error(ex);
		}
		out.println("</table>");
	    }
	}
	out.println("</div");
	out.println("</body>");
	out.println("</html>");


    }
    /**
     * Makes list of types and status.
     *
     * Writes as global variables, these are shared amoung threads as 
     * they rarely change.
     * @return a String of error or exceptions if any
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
		logger.error(back);
	    }
	    StatusList sl = new StatusList(debug);
	    back = sl.find();
	    if(back.equals("")){
		statuses = sl.getStatuses();
	    }
	    else{
		logger.error(back);
	    }
	}
	catch(Exception e){
	    logger.error(e);
	    ret += e;
	}
	return ret;
    }

}






















































