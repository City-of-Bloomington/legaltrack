package legals.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.list.*;
import legals.utils.*;

@WebServlet(urlPatterns = {"/Reporter"})
public class Reporter extends TopServlet {

    static final long serialVersionUID = 68L;
    static Logger logger = LogManager.getLogger(Reporter.class);
    /**
     * Generates the request form for this kind of reports.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	String name, value;
	String assigned_to="", assigned_to_2="",
	    assigned_to_3="", usetodaysdate="";
	String date_resolved="", added_by="",empid="", pcid="";
	String action="", problem="", resolved="n", pub="";
	String time_spent="", resolved_day="",
	    resolved_year="", resolved_notes="";
	String entry_date="", id="", subcat="", title="";
	String allDepts="",busCat="\n",subcat2="";
	String date_res_to="", date_res_from="";
	String date_req_to="", date_req_from="";
	String time_spent_to="", time_spent_from="";
	String req_mm_to="", req_dd_to="", req_yy_to="";
	String req_mm_from="", req_dd_from="", req_yy_from="";
	String allCats = "", allSubcatArrs="", allSubcat="";
	String email = "", dept="", fullname="", phone="";

	int category = 0, resolved_month=0, category2=0;
	User user = null;
	HttpSession session = null;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	}
	// 
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
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	//
	out.println("<script type=\"text/javascript\">");
	out.println("  function fillDate(obj){              ");
	out.println("   if(obj.value.length > 0){           ");
	out.println("    document.myForm.day_date.value=obj.value; ");
	out.println("     }                                 ");
	out.println("   }                                   ");
	out.println("  function validateForm(){		        ");
	out.println("   with(document.myForm){                  ");
	out.println("   if (date_from.value.length > 0){        ");
	out.println("  if(!checkDate(date_from)){      ");
	out.println("     alert(\"Invalid date \"+date_from.value); ");
	out.println("  date_from.focus();                       ");
	out.println("     return false;			        ");
	out.println("	}}                                      ");
	out.println("  	 if (date_to.value.length > 0){         ");
	out.println("  if(!checkDate(date_to)){        ");
	out.println("     alert(\"Invalid date \"+date_to.value);   ");
	out.println("  date_to.focus();                             ");
	out.println("     return false;			            ");
	out.println("	}}                                          ");
	out.println("  	 if (day_date.value.length > 0){            ");
	out.println("  if(!checkDate(day_date)){           ");
	out.println("     alert(\"Invalid date \"+day_date.value);  ");
	out.println("  day_date.focus();                        ");
	out.println("     return false;				");
	out.println("	}}                                      ");
	out.println("	}                                       ");
	out.println("     return true;				");
	out.println("	}	         		    	");
	//       
	out.println(" </script>				        ");

	//
	out.println("<h2><center>Reports</center></h2>");
	out.println("<center><table align=center border>");
	out.println("<tr><td align=center "+
		    " style=\"background-color:navy; color:white\">"+
		    "<b>Reports</b>"+
		    "</td></tr>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table>");

	out.println("<form name=myForm method=post onsubmit=\"return validateForm()\">");
	//
	out.println("<tr><td colspan=2><center>Select from the following "+
		    " options below.</td></tr>");
	// 1 
	out.println("<tr><td align=right> - ");
	out.println("<input type=hidden name=day_date value=\"\" />");
	out.println("<input type=radio name=report checked "+
		    "value=init "+
		    "></td><td align=left>Initial Hearing on:"+ 
		    "<input name=day_date0 value=\"\" onchange=\"fillDate(this);\" "+
		    "size=10 maxlength=10>"+
		    " </td><td>");
	// 2
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=contest "+
		    "></td><td align=left>Contested Hearing on: "+ 
		    "<input name=day_date2 value=\"\" onchange=\"fillDate(this);\" "+
		    "size=10 maxlength=10>"+
		    " </td><td>");
	// 3
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=supp_pro "+
		    "></td><td align=left>Proceedings Supplemental Hearing on: "+
		    "<input name=day_date3 value=\"\" onchange=\"fillDate(this);\" "+
		    "size=10 maxlength=10>"+
		    " </td><td>");
	// 4
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=judgment "+
		    "></td><td align=left>Judgments Report for Certain Period."+ 
		    " </td><td>");
	// 5
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=legal "+
		    "></td><td align=left>Legal Litigation Cases "+
		    " </td><td>");
	// 6
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=show_cause "+
		    "></td><td align=left>Rule to Show Cause Cases on:"+
		    "<input name=day_date4 value=\"\" onchange=\"fillDate(this);\" "+
		    "size=10 maxlength=10>"+
		    " </td><td>");
	// 7
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=collect "+
		    "></td><td align=left>Transfer to Collection within date range below."+
		    " </td><td>");
	// 8
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=collect_court "+
		    "></td><td align=left>Transfer to Collection Court within date range below."+
		    " </td><td>");				
	// 9
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=anyDate "+
		    "></td><td align=left>Any Case within Date Range below "+
		    " </td><td>");
	// 10
	out.println("<tr><td align=right> - ");
	out.println("<input type=radio name=report "+
		    "value=payment "+
		    "></td><td align=left>Payment Received Through Date Range below "+
		    " </td><td>");		
	//
	out.println("<tr><td colspan=2 align=center><font color=green size=-1> "+
		    " For certain options you may "+
		    " set the start and end dates of the period that "+
		    " these reports will cover in the date fields below."+
		    " </td></tr>");
	out.println("<tr><td colspan=2 align=center>");
	out.println("<table>");
	out.println("<tr><td></td><td>from</td><td>To</td></tr>");
	out.println("<tr><td></td><td><font color=green size=-1>"+
		    "mm/dd/yyyy</font></td>"+
		    "<td><font color=green size=-1>mm/dd/yyyy"+
		    "</font></td></tr>");
	out.println("<tr><td>Date Range</td>");
	out.println("<td><input type=text name=date_from value=\""+
		    "\" size=10 maxlength=10 class=\"date\" "+
		    " /></td> ");
	out.println("<td><input type=text name=date_to value=\""+
		    "\" size=10 maxlength=10 class=\"date\" "+
		    " /></td></tr> ");
	out.println("</table>");
	out.println("</td></tr>");
	//
	out.println("<tr><td colspan=2><hr></td></tr>");
	out.println("<tr><td colspan=2 align=right><input type=submit " +
		    "value=Submit></td></tr>");
	out.println("</table></td></tr>");
	// 
	out.println("</form></table>");
	out.println("<br />");
	out.println(Inserts.jsStrings(url));	
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");		
	out.print("</div></body></html>");
	out.close();
    }
    /**
     * Processes the request and generates the report.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	res.setContentType("text/html");
	Connection con = null;
	Statement stmt = null, stmt2=null;
	ResultSet rs = null, rs2 = null;
	PrintWriter out = res.getWriter();			  
	String day_date="",date_from="",date_to="", report="", message="";
       	String name, value;
	boolean success = true;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
			
	    if (name.equals("date_from")) {
		date_from=value;
	    }
	    else if (name.equals("date_to")) {
		date_to=value;
	    }
	    else if (name.equals("day_date")) {
		if(!value.equals(""))
		    day_date = value;
	    }
	    else if (name.equals("report")) {
		report=value;
	    }
	}
	//
	User user = null;
	HttpSession session = null;
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
		stmt2 = con.createStatement();
	    }
	    else{
		success = false;
		message = " could not connet to Database";
		logger.error(message);
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    success = false;
	    message += ex;
	}
	Calendar current_cal = Calendar.getInstance();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	//
	Vector<String> wherecases = new Vector<String>();
	if(report.equals("init")){
	    //
	    String qc = " select count(*) ", qq="",qf="",qw="", qo;

	    qq = " select c.id,"+
		" l.cause_num,"+
		" t.typeDesc, "+
		" date_format(c.ini_hear_date,'%M %d, %Y'),"+
		" c.ini_hear_time, "+
		" concat_ws(', ',d.l_name,d.f_name) ";
	    qf = " from legal_cases c,legal_case_types t, "+
		" legal_defendents d, legal_def_case l ";
	    qw = " where c.case_type=t.typeId "+
		" and str_to_date('"+day_date+"','%m/%d/%Y')=ini_hear_date "+
		" and d.did = l.did and l.id=c.id ";
	    qo = " order by 5,6,2 ";
	    String query ="",str="", oldTime ="", row="";
	    int nct = 0;
	    try{
		query = qc+qf+qw;
		if(debug){
		    logger.debug(query);
		}
		rs = stmt.executeQuery(query);
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0;
		    while(rs.next()){
			if(nct == 0){
			    str = rs.getString(4); // date 
			    out.println("<center><h2>"+str+"</h2>");
			    out.println("<h2>Initial Hearing</h2>");
			    str = rs.getString(5); // time
			    if(str == null) str ="";
			    else { 
				out.println("<h2>"+str+"</h2></center>");
				oldTime = str; // we keep track prev time
				out.println("<table width=80%>");
			    }
			}
			nct++;
			str = rs.getString(5);  // time
			if(str == null) str = "";
			if(!str.equals(oldTime)){ // if time changed
			    //
			    // write what we have so far
			    //
			    out.println("</table>");
			    out.println("<center><h2>"+str+
					"</h2></center>");
			    out.println("<table width=80%>");
			    oldTime = str; 
			}

			str = rs.getString(6);
			row = "<tr><td>"+str+"</td><td>";
			str = rs.getString(2);
			if(str !=null){
			    row += " "+str;
			}
			row += "</td><td align=right>";
			str = rs.getString(3);
			if(str !=null){
			    row += " "+str;
			}
			row += "</td></tr>";
			out.println(row);
			// vec2.add(row);
		    }
		    out.println("</table>");
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+query);
	    }
	}
	else if(report.equals("contest")){
	    //
	    String qc = " select count(*) ";
	    String qq = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, "+
		"date_format(c.contest_hear_date,'%M %d, %Y'),"+
		"c.contest_hear_time, "+
		"concat_ws(', ',d.l_name,d.f_name) ";
	    String qf = 
		"from legal_cases c,legal_case_types t, "+
		"legal_defendents d, legal_def_case l ";
	    String qw = 
		" where c.case_type=t.typeId "+
		" and str_to_date('"+day_date+
		"','%m/%d/%Y')=contest_hear_date "+
		" and d.did = l.did and l.id=c.id ";
	    String qo = " order by 5,6,2 ";
	    String query ="",str="",str2="", row="";
	    int nct = 0;
	    try{
		query = qc+qf+qw;
		if(debug){
		    logger.debug(query);
		}
		rs = stmt.executeQuery(query);
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0;
		    while(rs.next()){
			if(nct == 0){
			    str = rs.getString(4); // date 
			    out.println("<center><h2>"+str+"</h2>");
			    out.println("<h2>Contested Hearing</h2>");
			    str = rs.getString(5); // time
			    if(str != null){ 
				out.println("<h2>"+str+"</h2></center>");
				str2 = str; // we keep track of prev time
				out.println("<table width=60%>");
			    }
			}
			nct=1;
			str = rs.getString(5);
			if(str == null) str = "";
			if(!str.equals(str2)){
			    //
			    out.println("</table>");
			    out.println("<center><h2>"+str+
					"</h2></center>");
			    out.println("<table width=60%>");
			    str2 = str; 
			}
			str = rs.getString(6);
			if(str != null){
			    row = "<tr><td>"+str+"</td><td>";
			    str = rs.getString(2);
			    if(str !=null){
				row += " "+str;
			    }
			    row += "</td><td align=right>";
			    str = rs.getString(3);
			    if(str !=null){
				row += " "+str;
			    }
			    row += "</td></tr>";
			    out.println(row);
			}
		    }
		    out.println("</table>");
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+query);
	    }
	}
	else if(report.equals("supp_pro")){
	    //
	    String qc = " select count(*) ";
	    String qq = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, "+
		"date_format(c.pro_supp_date,'%M %d, %Y'),"+
		"c.pro_supp_time, "+
		" concat_ws(', ',d.l_name,d.f_name) ";
	    String qf = 
		"from legal_cases c,legal_case_types t, "+
		"legal_defendents d, legal_def_case l ";
	    String qw = 
		" where c.case_type=t.typeId "+
		" and str_to_date('"+day_date+"','%m/%d/%Y')=pro_supp_date "+
		" and d.did = l.did and l.id=c.id ";
	    String qo = " order by 5,6,2 ";
	    String query ="",str="",str2="",row="";
	    int nct = 0;
	    try{
		query = qc+qf+qw;
		if(debug){
		    logger.debug(query);
		}
		rs = stmt.executeQuery(query);
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0;
		    while(rs.next()){
			if(nct == 0){
			    str = rs.getString(4); // date 
			    out.println("<center><h2>"+str+"</h2>");
			    out.println("<h2>Proceedings Supplemental "+
					"Hearing</h2>");
			    str = rs.getString(5); // time
			    if(str == null) str ="";
			    if(str != null){ 
				out.println("<h2>"+str+"</h2></center>");
				str2 = str; // we keep track of old time
				out.println("<table width=60%>");
			    }
			}
			str = rs.getString(5);
			if(str == null) str = "";
			if(str != null){ 
			    if(!str.equals(str2)){
				//
				// write what we have so far
				//
				out.println("</table>");
				out.println("<center><h2>"+str+
					    "</h2></center>");
				out.println("<table width=60%>");
				str2 = str; 
			    }
			}
			nct++;
			str = rs.getString(6);
			if(str != null){
			    row = "<tr><td>"+str+"</td><td>";
			    str = rs.getString(2);
			    if(str !=null){
				row += " "+str;
			    }
			    row += "</td><td align=right>";
			    str = rs.getString(3);
			    if(str !=null){
				row += " "+str;
			    }
			    row += "</td></tr>";
			    out.println(row);
			}
		    }
		    out.println("</table>");
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+query);
	    }
	}
	else if(report.equals("judgment")){
	    String titles[] ={"Name","Cause #","Fine","CC",
		"Start","Compliance","P.S. Date"};
	    String qc = " select count(*) ";
	    String qq = " select c.id,d.did,"+
		"concat_ws(', ',d.l_name,d.f_name),"+
		"l.cause_num,"+
		"c.fine,"+
		"c.court_cost,"+
		"date_format(c.last_paid_date,'%m/%d/%Y'),"+
		"date_format(c.compliance_date,'%m/%d/%Y'),"+
		"date_format(c.pro_supp_date,'%m/%d/%Y') ";

	    String qf = 
		"from legal_cases c, "+
		"legal_defendents d, legal_def_case l ";

	    String qw = 
		" where (c.status='PD' or c.status='PP') and c.id=l.id and "+
		" d.did=l.did ";

	    if(!date_from.equals("")){
		qw += " and str_to_date('"+date_from+
		    "','%m/%d/%Y') <= compliance_date ";
	    }
	    if(!date_to.equals("")){
		qw += " and str_to_date('"+date_to+
		    "','%m/%d/%Y') >= compliance_date ";
	    }
	    String qo = " order by 1,2 ";
	    String query ="",str="",str2="";
	    int nct = 0, row=0;
	    String prev_id="",did="", id="", pid="", def="", rest="";
	    try{
		query = qc+qf+qw;
		if(debug){
		    logger.debug(query);
		}
		rs = stmt.executeQuery(query);
		if(rs.next()){
		    nct = rs.getInt(1);
		    // out.println("<br>Total match "+nct+"<br>");
		}
		if(nct == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    out.println("<center><h3>Judgments ");
		    if(!date_from.equals("")){
			if(!date_to.equals("")){
			    out.println(date_from+" - "+date_to);
			}
			else{
			    out.println("From "+date_from);
			}
		    }
		    else{
			if(!date_to.equals("")){
			    out.println("Up to "+date_to);
			}
		    }
		    out.println("</h3>");
		    out.println("<table border width=100%>");
		    out.println("<tr>");
		    for(int i=0;i<titles.length;i++)
			out.println("<th><font size=-1>"+titles[i]+
				    "</font></th>");
		    out.println("</tr>");
		    String bgcolor = Helper.bgcolor; 
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    while(rs.next()){
			id = rs.getString(1);
			did = rs.getString(2);	
			if(id.equals(prev_id)){ // same case
			    str = rs.getString(3);
			    if(str != null){
				if(!def.equals("")) def += " & ";
				def += str;
			    }
			}
			else{ // new case
			    prev_id=id;
			    row++;
			    //
			    // write the last line
			    //
			    if(row%3 == 0) bgcolor="silver";
			    else bgcolor="";
			    if(!rest.equals("")){
				def +="</font></td>";
				out.println(def+rest);
			    }
			    if(row%30 == 0){
				//
				// write titles every 30 rows
				out.println("<tr>");
				for(int i=0;i<titles.length;i++)
				    out.println("<th><font size=-1>"+titles[i]+
						"</font></th>");
				out.println("</tr>");
			    }
			    def = "<tr bgcolor=\""+bgcolor+
				"\"><td><font size=-1>";
			    str = rs.getString(3);
			    if(str != null)
				def += str;
			    rest = "";
			    for(int i=4;i<10;i++){
				str = rs.getString(i);
				if(i == 5 || i == 6)
				    rest += "<td align='right'><font size=-1>";
				else
				    rest += "<td><font size=-1>";
				if(str != null)
				    rest += str;
				else
				    rest += "&nbsp;";
				rest += "</font></td>";
			    }
			    rest += "</tr>";
			}
		    }
		    if(!rest.equals("")){
			def +="</font></td>";
			out.println(def+rest);
		    }
		    out.println("</table>");
		}
	    }
	    catch(Exception ex){
		logger.error(ex+" : "+query);
	    }
	}
	else if(report.equals("legal")){
	    String titles[] ={"Name", 
		"Rental Address",
		"Date Received",
		"Case Type",
		"Letter Sent Date",
		"Date Filed",
		"Cause #",
		"Initial Hearing Date",
		"Fine Amnt",
		"Closed Date",
		"Comments"
	    };
	    String qc = " select count(*) ";
	    String qq = " select c.id,d.did,"+
		"concat_ws(', ',d.l_name,d.f_name),"+
		"concat_ws(' ',a.street_num,a.street_dir,"+
		"a.street_name,a.street_type,"+
		"a.sud_type,a.sud_num),"+
		"date_format(c.received,'%m/%d/%Y'),"+
		"t.typeDesc,"+
		"date_format(c.sent_date,'%m/%d/%Y'),"+
		"date_format(c.filed,'%m/%d/%Y'),"+
		"l.cause_num,"+
		"date_format(c.ini_hear_date,'%m/%d/%Y'),"+
		"c.fine,"+
		"date_format(c.closed_date,'%m/%d/%Y'),"+
		"comments ";

	    String qf = 
		"from legal_cases c, legal_case_types t,"+
		"legal_defendents d, legal_def_case l,legal_addresses a ";
	    String qw = 
		" where "+
		" c.status in ('PD','IH','CH', 'PS','WP','WR') and "+
		" c.id=l.id and "+
		" d.did=l.did and c.case_type=t.typeId and "+
		" c.id=a.caseId and a.rental_addr is not null ";
	    if(!date_from.equals("")){
		qw += " and str_to_date('"+date_from+
		    "','%m/%d/%Y') <= judgment_date ";
	    }
	    if(!date_to.equals("")){
		qw += " and str_to_date('"+date_to+
		    "','%m/%d/%Y') >= judgment_date ";
	    }
	    //
	    String qo = " order by 1,2 ";
	    String query ="",str="",str2="";
	    int nct = 0, row=0;
	    String prev_id="",did="", id="", pid="", def="", rest="";
	    try{
		query = qc+qf+qw;
		if(debug){
		    logger.debug(query);
		}
		rs = stmt.executeQuery(query);
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    out.println("<center><h3>Legal Litigation Cases </h3>");

		    if(!date_from.equals("")){
			out.println("<h3>");
			if(!date_to.equals("")){
			    out.println(date_from+" - "+date_to);
			}
			else{
			    out.println("From "+date_from);
			}
			out.println("</h3>");
		    }
		    else{
			if(!date_to.equals("")){
			    out.println("<h3> Up to "+date_to +"</h3>");
			}
		    }
		    out.println("<table border>");
		    out.println("<tr>");
		    for(int i=0;i<titles.length;i++)
			out.println("<th><font size=-1>"+titles[i]+
				    "</font></th>");
		    out.println("</tr>");
		    String bgcolor = Helper.bgcolor;
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    while(rs.next()){
			id = rs.getString(1);
			did = rs.getString(2);	
			if(id.equals(prev_id)){ // same case
			    str = rs.getString(3);
			    if(str != null){
				if(!def.equals("")) def += " & ";
				def += str;
			    }
			}
			else{ // new case
			    prev_id=id;
			    row++;
			    //
			    // write the last line
			    //
			    if(row%3 == 0) bgcolor="silver";
			    else bgcolor="";
			    if(!rest.equals("")){
				def +="</td>";
				out.println(def+rest);
			    }
			    if(row%30 == 0){
				//
				// write titles every 30 rows
				out.println("<tr>");
				for(int i=0;i<titles.length;i++)
				    out.println("<th><font size=-1>"+
						titles[i]+"</font></th>");
				out.println("</tr>");
			    }
			    def = "<tr bgcolor=\""+bgcolor+
				"\"><td><font size=-1>";
			    str = rs.getString(3);
			    if(str != null)
				def += str;
			    rest = "";
			    for(int i=4;i<14;i++){
				str = rs.getString(i);
				rest += "<td><font size=-1>";
				if(!(str == null || str.trim().equals("")))
				    rest += str;
				else
				    rest += "&nbsp;&nbsp;";
				rest += "</font></td>";
			    }
			    rest += "</tr>";
			}
		    }
		    if(!rest.equals("")){
			def +="</font></td>";
			out.println(def+rest);
		    }
		    out.println("</table>");
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+query);
	    }
	}
	else if(report.equals("show_cause")){
	    //
	    String qc = " select count(*) ";
	    String qq = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, "+
		"date_format(c.rule_date,'%M %d, %Y'),"+
		"c.rule_time, "+
		" concat_ws(', ',d.l_name,d.f_name) ";
	    String qf = 
		"from legal_cases c,legal_case_types t, "+
		"legal_defendents d, legal_def_case l ";
	    String qw = 
		" where c.case_type=t.typeId "+
		" and str_to_date('"+day_date+"','%m/%d/%Y')=c.rule_date "+
		" and d.did = l.did and l.id=c.id ";
			
	    String qo = " order by 5,6,2 ";
	    String query ="",str="",str2="",row="";
	    int nct = 0;
	    try{
		query = qc+qf+qw;
		if(debug){
		    logger.debug(query);
		}
		rs = stmt.executeQuery(query);
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0;
		    while(rs.next()){
			if(nct == 0){
			    str = rs.getString(4); // date 
			    out.println("<center><h2>"+str+"</h2>");
			    out.println("<h2>Rule to Show Cause </h2>");
			    str = rs.getString(5); // time
			    if(str == null) str ="";
			    if(str != null){ 
				out.println("<h2>"+str+"</h2></center>");
				str2 = str; // we keep track of old time
				out.println("<table width=60%>");
			    }
			}
			str = rs.getString(5);
			if(str == null) str = "";
			if(str != null){ 
			    if(!str.equals(str2)){
				//
				// write what we have so far
				//
				out.println("</table>");
				out.println("<center><h2>"+str+
					    "</h2></center>");
				out.println("<table width=60%>");
				str2 = str; 
			    }
			}
			nct++;
			str = rs.getString(6);
			if(str != null){
			    row = "<tr><td>"+str+"</td><td>";
			    str = rs.getString(2);
			    if(str !=null){
				row += " "+str;
			    }
			    row += "</td><td align=right>";
			    str = rs.getString(3);
			    if(str !=null){
				row += " "+str;
			    }
			    row += "</td></tr>";
			    out.println(row);
			}
		    }
		    out.println("</table>");
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+query);
	    }
	}
	else if(report.equals("collect")){
	    //
	    CaseList llc = new CaseList(debug);
	    llc.setStatus("TC");
	    llc.setWhich_date("trans_collect_date");
	    llc.setDate_from(date_from);
	    llc.setDate_to(date_to);
	    String back = llc.lookFor();
	    List<Case> list = llc.getCases();
	    if(back.equals("")){
		if(list.size() == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    out.println("<center><h2>Transfer To Collection </h2>");
		    out.println("<table width=95% border>");
		    out.println("<tr><th>ID</th>"+
				"<th>Citation #</th>"+
				"<th>Names</th>"+
				"<th>Type</th>"+
				"<th>Trans Collect Date</th>"+
				"</tr>");
		    for(Case one:list){
			String cit_nums = "", def_names="", str="",cause_nums="";
			String all = "<tr>";
			List<Defendant> defs = one.getDefendants();
			List<CaseViolation> vols = one.getCaseViolations();
			CaseType type = one.getCaseType();
			str = one.getId();
			all = "<td><a href=\""+url+"CaseServ?id="+str+"&action=zoom\">"+str+"</td>";
			cit_nums = one.getCitation_num();
			if(vols != null){
			    for(CaseViolation cv:vols){
				if(!cit_nums.equals("")) cit_nums += ", ";
				cit_nums += cv.getCitations();
			    }
			}
			if(defs != null && defs.size() > 0){
			    for(Defendant ddef:defs){
				if(!def_names.equals("")) def_names +="<br />";
				def_names += ddef.getFullName();
			    }
			}
			if(cit_nums.equals("")){
			    cit_nums = "&nbsp;";
			}
			all += "<td>"+cit_nums+"</td>";
			if(def_names.equals("")){
			    def_names = "&nbsp;";
			}
			all += "<td>"+def_names+"</td>";
			if(type != null){
			    all += "<td>"+type.getName()+"</td>";
			}
			else{
			    all += "<td>&nbsp;</td>";
			}
			str = one.getTrans_collect_date();
			if(str == null || str.equals(""))
			    str = "&nbsp;";
			all += "<td>"+str+"</td>";
			all += "</tr>";
			out.println(all);												

		    }
		    out.println("</table><br /><br />");
		}
	    }
	    /*
	      String qc = " select count(*) ";
	      String qq = " select c.id,"+
	      "l.cause_num,"+
	      "c.citation_num,"+
	      "s.statusDesc,"+
	      "t.typeDesc, "+
	      "date_format(c.trans_collect_date,'%M %d, %Y'), "+
	      " concat_ws(', ',d.l_name,d.f_name) ";
	      String qf = 
	      "from legal_cases c,legal_case_types t, "+
	      "legal_defendents d, legal_def_case l,legal_case_status s ";
	      String qw = 
	      " where c.case_type=t.typeId "+
	      " and s.statusId=c.status "+
	      " and c.status='TC' "+
	      " and d.did = l.did and l.id=c.id ";
	      if(!date_from.equals(""))
	      qw += " and str_to_date('"+date_from+"','%m/%d/%Y') <= c.trans_collect_date ";
	      if(!date_to.equals("")){
	      qw += " and str_to_date('"+date_to+"','%m/%d/%Y') >= c.trans_collect_date ";
	      }
	      String qo = " order by 5,2 ";
	      String query ="",str="",str2="",row="";
	      int nct = 0;
	      try{
	      query = qc+qf+qw;
	      if(debug){
	      logger.debug(query);
	      }
	      rs = stmt.executeQuery(query);
	      if(rs.next()){
	      nct = rs.getInt(1);
	      }
	      if(nct == 0){
	      out.println("<h3>No records matched </h3>");
	      }
	      else{
	      query = qq+qf+qw+qo;
	      if(debug){
	      logger.debug(query);
	      }
	      rs = stmt.executeQuery(query);
	      nct = 0;
	      out.println("<center><h2>Transfer To Collection </h2>");
	      out.println("<table width=95% border>");
	      out.println("<tr><th>ID</th><th>Casue #</th>"+
	      "<th>Citation #</th>"+
	      "<th>Defendant(s)</th>"+
	      "<th>Status</th>"+
	      "<th>Type</th>"+
	      "<th>Trans Collect Date</th>"+
	      "<th>Name</th></tr>");
	      while(rs.next()){
	      nct++;
	      String all = "<tr>";
	      str = rs.getString(1);
	      if(str != null){
	      all = "<td><a href=\""+url+"CaseServ?id="+str+"&action=zoom\">"+str+"</td>";
	      }
	      for(int i=2;i<8;i++){
	      str = rs.getString(i);
	      if(str == null) str = "&nbsp;";
	      all += "<td>"+str+"</td>";
	      }
	      all += "</tr>";
	      out.println(all);
	      }
	      out.println("</table><br /><br />");
	      }
	      }
	      catch(Exception ex){
	      out.println("Error "+ex+" "+query);
	      logger.error(ex+":"+query);
	      }
	    */
	}
	else if(report.equals("collect_court")){
	    //
	    CaseList llc = new CaseList(debug);
	    llc.setStatus("CC");
	    llc.setWhich_date("trans_collect_date");
	    llc.setDate_from(date_from);
	    llc.setDate_to(date_to);
	    String back = llc.lookFor();
	    List<Case> list = llc.getCases();
	    if(back.equals("")){
		if(list.size() == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    out.println("<center><h2>Transfer To Collection Court</h2>");
		    out.println("<table width=95% border>");
		    out.println("<tr><th>ID</th>"+
				"<th>Cause #</th>"+
				"<th>Citation #</th>"+
				"<th>Names</th>"+
				"<th>Type</th>"+
				"<th>Trans Collect Date</th>"+
				"</tr>");
		    for(Case one:list){
			String cit_nums = "", def_names="", str="",cause_nums="";
			String all = "<tr>";
			List<Defendant> defs = one.getDefendants();
			List<CaseViolation> vols = one.getCaseViolations();
			CaseType type = one.getCaseType();
			str = one.getId();
			all = "<td><a href=\""+url+"CaseServ?id="+str+"&action=zoom\">"+str+"</td>";
			cit_nums = one.getCitation_num();
			cause_nums = one.getCauseNumsStr();
			if(cause_nums.equals("")){
			    cause_nums = "&nbsp;";
			}
			all += "<td>"+cause_nums+"</td>";
			if(vols != null){
			    for(CaseViolation cv:vols){
				if(!cit_nums.equals("")) cit_nums += ", ";
				cit_nums += cv.getCitations();
			    }
			}
			if(defs != null && defs.size() > 0){
			    for(Defendant ddef:defs){
				if(!def_names.equals("")) def_names +="<br />";
				def_names += ddef.getFullName();
			    }
			}
			if(cit_nums.equals("")){
			    cit_nums = "&nbsp;";
			}
			all += "<td>"+cit_nums+"</td>";
			if(def_names.equals("")){
			    def_names = "&nbsp;";
			}
			all += "<td>"+def_names+"</td>";
			if(type != null){
			    all += "<td>"+type.getName()+"</td>";
			}
			else{
			    all += "<td>&nbsp;</td>";
			}
			str = one.getTrans_collect_date();
			if(str == null || str.equals(""))
			    str = "&nbsp;";
			all += "<td>"+str+"</td>";
			all += "</tr>";
			out.println(all);												

		    }
		    out.println("</table><br /><br />");
		}
	    }

	}				
	else if(report.equals("anyDate")){
	    //
	    String titles[] = {"ID","Cause #","Type","Date Type","Date","Time","Defendant"};
	    String qc = " select count(*) ";
	    String qq1 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, 'Rule to Show Date',"+
		"date_format(c.rule_date,'%M %d, %Y'),"+
		"c.rule_time, "+
		" concat_ws(', ',d.l_name,d.f_name) ";
			
	    String qf = 
		"from legal_cases c,legal_case_types t, "+
		"legal_defendents d, legal_def_case l ";
	    String qw = 
		" where c.case_type=t.typeId "+
		" and d.did = l.did and l.id=c.id ";
	    String qw1 = "";
	    if(!date_from.equals(""))
		qw1 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.rule_date ";
	    if(!date_to.equals("")){
		if(!qw1.equals("")) qw1 += " and ";
		qw1 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.rule_date ";	
	    }
	    String qq2 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, 'Compliance Date',"+
		"date_format(c.compliance_date,'%M %d, %Y'),"+
		"' ', "+
		" concat_ws(', ',d.l_name,d.f_name) ";
	    String qw2 = "";
	    if(!date_from.equals(""))
		qw2 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.compliance_date ";
	    if(!date_to.equals("")){
		if(!qw2.equals("")) qw2 += " and ";
		qw2 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.compliance_date ";	
	    }
	    String qq3 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, 'Initial Hearing Date',"+
		"date_format(c.ini_hear_date,'%M %d, %Y'),"+
		"c.ini_hear_time, "+
		" concat_ws(', ',d.l_name,d.f_name) ";
	    String qw3 = "";
	    if(!date_from.equals(""))
		qw3 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.ini_hear_date ";
	    if(!date_to.equals("")){
		if(!qw3.equals("")) qw3 += " and ";
		qw3 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.ini_hear_date ";	
	    }
	    String qq4 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, 'Pro Supp Date',"+
		"date_format(c.pro_supp_date,'%M %d, %Y'),"+
		"c.pro_supp_time, "+
		" concat_ws(', ',d.l_name,d.f_name) ";
	    String qw4 = "";
	    if(!date_from.equals(""))
		qw4 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.pro_supp_date ";
	    if(!date_to.equals("")){
		if(!qw4.equals("")) qw4 += " and ";				
		qw4 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.pro_supp_date ";	
	    }
	    String qq5 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, 'Judgement Date',"+
		"date_format(c.judgment_date,'%M %d, %Y'),"+
		"' ', concat_ws(', ',d.l_name,d.f_name) ";
	    String qw5 = "";
	    if(!date_from.equals(""))
		qw5 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.judgment_date ";
	    if(!date_to.equals("")){
		if(!qw4.equals("")) qw5 += " and ";	
		qw5 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.judgment_date ";
	    }
	    String qq6 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, 'Contest Hearing Date',"+
		"date_format(c.contest_hear_date,'%M %d, %Y'),"+
		"c.contest_hear_time, concat_ws(', ',d.l_name,d.f_name) ";
	    String qw6 = "";
	    if(!date_from.equals(""))
		qw6 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.contest_hear_date ";
	    if(!date_to.equals("")){
		if(!qw6.equals("")) qw6 += " and ";	
		qw6 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.contest_hear_date ";
	    }
	    String qq7 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, 'Misc Hearing Date',"+
		"date_format(c.misc_hear_date,'%M %d, %Y'),"+
		"c.misc_hear_time, concat_ws(', ',d.l_name,d.f_name) ";
	    String qw7 = "";
	    if(!date_from.equals(""))
		qw7 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.misc_hear_date ";
	    if(!date_to.equals("")){
		if(!qw7.equals("")) qw7 += " and ";	
		qw7 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.misc_hear_date ";
	    }
	    String qq8 = " select c.id,"+
		"l.cause_num,"+
		"t.typeDesc, '41.e Date',"+
		"date_format(c.e41_date,'%M %d, %Y'),"+
		"' ', concat_ws(', ',d.l_name,d.f_name) ";
	    String qw8 = "";
	    if(!date_from.equals(""))
		qw8 += " str_to_date('"+date_from+"','%m/%d/%Y') <= c.e41_date ";
	    if(!date_to.equals("")){
		if(!qw8.equals("")) qw8 += " and ";	
		qw8 += " str_to_date('"+date_to+"','%m/%d/%Y') >= c.e41_date ";
	    }
	    String qo = " order by 4,5,3 ";
			
	    String query ="",str="",str2="",row="";
	    int nct = 0;
	    try{
		query = qc+qf+qw+" and (("+qw1+") or ("+qw2+") or ("+qw3+") or ("+qw4+
		    ") or ("+qw5+") or ("+qw5+") or ("+qw6+") or ("+qw7+") or ("+qw8+"))";
				
		if(debug){
		    logger.debug(query);
		}
		rs = stmt.executeQuery(query);
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    out.println("<h3>No records matched </h3>");
		}
		else{
		    query = qq1+qf+qw+" and "+qw1+" union "+
			qq2+qf+qw+" and "+qw2+" union "+
			qq3+qf+qw+" and "+qw3+" union "+
			qq4+qf+qw+" and "+qw4+" union "+
			qq5+qf+qw+" and "+qw5+" union "+
			qq6+qf+qw+" and "+qw6+" union "+
			qq7+qf+qw+" and "+qw7+" union "+
			qq8+qf+qw+" and "+qw8+" "+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0;
		    out.println("<center>");
		    out.println("<h2>Events in the Date Range "+date_from+" - "+date_to+"</h2>");
		    out.println("<table><tr>");
		    for(int i=0;i<titles.length;i++){
			out.println("<th>"+titles[i]+"</th>");
		    }
		    out.println("</tr>");
		    while(rs.next()){
			out.println("<tr>");
			str = rs.getString(1);
			out.println("<td><a href=\""+url+"Case?action=zoom&id="+str+
				    "\">"+str+"</a></td>");
			for(int i=2;i <= titles.length;i++){
			    str = rs.getString(i);
			    if(str == null) str = "&nbsp;";
			    out.println("<td>"+str+"</td>");
			}
			out.println("</tr>");
		    }
		    out.println("</table>");
		    out.println("</center>");
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+query);
	    }
	}
	else if(report.equals("payment")){
	    String titles[] = {"Department","Payment Received"};
	    String titles2[] = {"Violation","Payment Received"};
	    String qc = " select count(*) ";
	    String q = " select "+
		"t.typeDesc type,"+
		" sum(p.amount) ";
	    String q2 = " select "+
		"d.dept dept,"+
		" sum(p.amount) ";
			
			
	    String qf = 
		"from legal_cases c,legal_case_types t, "+
		" legal_payments p";
	    String qf2 =
		"from legal_cases c,legal_case_types t, "+
		" legal_type_dept dt,legal_depts d,"+
		" legal_payments p";
	    String qw = 
		" where c.case_type=t.typeId "+
		" and p.id = c.id ";
	    String qw2 = 
		" where c.case_type=t.typeId "+
		" and p.id = c.id "+
		" and dt.deptId=d.deptId and dt.typeId=t.typeId";
	    if(!date_from.equals("")){
		qw += " and ";
		qw += " str_to_date('"+date_from+"','%m/%d/%Y') <= p.paid_date";
		qw2 += " and ";
		qw2 += " str_to_date('"+date_from+"','%m/%d/%Y') <= p.paid_date";	
	    }
	    if(!date_to.equals("")){
		qw += " and ";
		qw += " str_to_date('"+date_to+"','%m/%d/%Y') >= p.paid_date";
		qw2 += " and ";
		qw2 += " str_to_date('"+date_to+"','%m/%d/%Y') >= p.paid_date";
	    }
	    String qo = " group by type order by type ";
	    String qo2 = " group by dept order by dept ";
	    int nct = 0;
	    String qq = qc+qf2+qw2;
	    try{
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    out.println("<h3>No amount received </h3>");
		}
		else{
		    qq = q2+qf2+qw2+qo2;
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    out.println("<center>");
		    out.println("<h2>Payment Received "+date_from+" - "+date_to+"</h2>");
		    out.println("<br />");
		    out.println("<table border=1><tr>");
		    for(int i=0;i<titles.length;i++){
			out.println("<th>"+titles[i]+"</th>");
		    }
		    out.println("</tr>");
		    float total = 0f;
		    while(rs.next()){
			String str = rs.getString(1);
			String str2 = rs.getString(2);
			total += rs.getFloat(2);
			out.println("<tr><td>"+str+"</td><td align=right>$"+str2+"</tr>");
		    }
		    out.println("<tr><th>Total</th><td align=right>$"+total+"</tr>");
		    out.println("</table>");
		    qq = q+qf+qw+qo;
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    out.println("<br />");
		    out.println("<table border=1><tr>");
		    for(int i=0;i<titles2.length;i++){
			out.println("<th>"+titles2[i]+"</th>");
		    }
		    out.println("</tr>");
		    total = 0f;
		    while(rs.next()){
			String str = rs.getString(1);
			String str2 = rs.getString(2);
			total += rs.getFloat(2);
			out.println("<tr><td>"+str+"</td><td align=right>$"+str2+"</tr>");
		    }
		    out.println("<tr><th>Total</th><td align=right>$"+total+"</tr>");
		    out.println("</table>");
		    out.println("<br /><br />");
		}	
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	}
	out.println("</div></body>");
	out.println("</html>");
	Helper.databaseDisconnect(con, stmt, rs);
		
    }
    //
    // Writes a set of rows in html table
    // the first vector contains the list of names and vector2 contains
    // the html code to output but after sorting according to what is in
    //  vector 1
    // 
    void writeRows(PrintWriter out,Vector<String> vec, Vector<String> vec2){

	String rows[] = null;
	if(vec.size() > 0){
	    Object ob[] = vec.toArray();
	    if(ob != null){
		rows = new String[ob.length];
		for(int i=0;i<ob.length;i++){
		    rows[i] = (String)ob[i];
		}
	    }
	    int ind[] = sorter(rows);
	    ob = vec2.toArray();
	    if(ob != null){
		rows = new String[ob.length];
		for(int i=0;i<ob.length;i++){
		    rows[i] = (String)ob[i];
		}
	    }
	    for(int i=0;i<ind.length;i++){
		out.println(rows[ind[i]]);
	    }
	}
    }
    //
    void testSorter(){
	String[] test = {"Zar","Abc","Dba","Aa","Car","NaD","Nad","Fat","Mad"};
	String[] test2 = {"Abb","Aaa"};
	String[] test3 = {};
	int[] ind = null;
	ind = sorter(test);
	ind = sorter(test2);
	ind = sorter(test3);
    }
    int[] sorter(String[] input){

	if(input == null || input.length < 1) return null;
	int[] index = new int[input.length];
	int start = 0, jj=0;
	int len = input.length;
	for(int i=0;i<len;i++) index[i] = i;
	String tmp="";
	//
	while(start+1 < len){
	    for(int i=start+1;i<len;i++){
		if(input[start].compareTo(input[i]) > 0){
		    // 
		    // perform the swap
		    //
		    tmp = input[start];
		    jj = index[start];
		    input[start] = input[i];
		    index[start] = index[i];
		    input[i] = tmp;
		    index[i] = jj;
		}
	    }
	    start++;
	}
	return index;
    }

}






















































