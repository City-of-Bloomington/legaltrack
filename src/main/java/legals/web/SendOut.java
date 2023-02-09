
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
/**
 *
 */
@WebServlet(urlPatterns = {"/SendOut"})
public class SendOut extends TopServlet {

    static final long serialVersionUID = 70L;
    static Logger logger = LogManager.getLogger(SendOut.class);
    String monthArr[] = {"",
	"January","February","March","April","May","June",
	"July","August","September","October","November",
	"December"};
    String court[] = {"","I","II","III","IV","V","VI","VII","VIII","IX","X"};
    //
    // such as 1st, 2nd, 3rd, 4th
    String dayOrder[] = {"",
	"st","nd","rd","th","th","th","th","th","th","th",
	"th","th","th","th","th","th","th","th","th","th",
	"st","nd","rd","th","th","th","th","th","th","th",
	"st"};
    //
    // pronouns and their possessive pronouns 
    //
    // Case.identArr[] = {"","He","She","They","It");
    final static String posPronArr[] = {"","his","her","their","its"};

    /**
     * Generates the request form.
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
	String action="";
	String judge[] = null, judges="";
	String cur_path="";
	boolean success = true;
	int resolved_month = 0;
	// 
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
	//
	// location to save the data source file for mail merge
	//
	cur_path = server_path;
	//
	Calendar current_cal = Calendar.getInstance();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	String todayText = monthArr[mm]+" "+dd+", "+yyyy;
	String today = ""+mm+"/"+dd+"/"+yyyy;
	String start_month = ""+mm+"/1/"+yyyy;
	current_cal.add(Calendar.DATE,-6); // one week ago
	mm = current_cal.get(Calendar.MONTH)+1;
	dd = current_cal.get(Calendar.DATE);
	yyyy = current_cal.get(Calendar.YEAR);
	String start_week = ""+mm+"/"+dd+"/"+yyyy;
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	//
	out.println("  function validateForm(){		         ");
	out.println("   with(document.myForm){                   ");
	out.println("   if (date_from.value.length > 0){         ");
	out.println("  if(!validateDate(date_from.value)){       ");
	out.println("     alert(\"Invalid date \"+date_from.value); ");
	out.println("  date_from.focus();                           ");
	out.println("     return false;			            ");
	out.println("	}}                                          ");
	out.println("  	 if (date_to.value.length > 0){             ");
	out.println("  if(!validateDate(date_to.value)){            ");
	out.println("     alert(\"Invalid date \"+date_to.value);   ");
	out.println("  date_to.focus();                             ");
	out.println("     return false;			            ");
	out.println("	}}                                          ");
	out.println("  	 if (day_date.value.length > 0){            ");
	out.println("  if(!validateDate(day_date.value)){           ");
	out.println("     alert(\"Invalid date \"+day_date.value);  ");
	out.println("  day_date.focus();                            ");
	out.println("     return false;				    ");
	out.println("	}}                                          ");
	out.println("	}                                           ");
	out.println("     return true;				    ");
	out.println("	}	         			    ");
	out.println("  function confirmClear(){	                    ");
	out.println("   var x = false;                              ");
	out.println("   x = confirm(\"Are you sure you want to clear Pro Supp Defendants\");");
	out.println("  if(x){                                            ");
	out.println("   document.myForm.action2.value=\"Clear Pro Supp\";");
	out.println("   document.myForm.submit();                        ");
	out.println("	}}			        	         ");
	out.println(" </script>				                 ");
	//
	out.println("<h2><center>Send Out</center></h2>");
	// out.println("<form name=myForm method=post>");
	out.println("<center><table width=60% border>");
	out.println("<tr><td align=center style=\"background-color:navy; color:white\">"+
		    "<b> Mailers </b>"+
		    "</td></tr>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table border width=100%>");

	//
	out.println("<tr><td colspan=2><center>Select from the following "+
		    " options:</td></tr>");
	out.println("<tr><td></td><td>");
	out.println("<ul>");
	out.println("<li><a href=\""+url+"ReleasePdf\">Release of Judgment</a></li>");
	out.println("<li><a href=\""+url+"AddrReqPdf\">Request Address Update</a></li>");				
	out.println("</ul>");
	out.println("</td></tr>");
	/*
	//
	// 1 
	out.println("<tr><td align=right valign=top> - ");
	out.println("<input type=radio name=report checked value=addr "+
	"></td><td valign=top align=left>Request Address Update. <br>");
	out.println("<table><tr><td align=right>Postmaster at:</td><td align=left>");
	out.println("<input name=pos value=\"Bloomington, IN 47401\""+
	" size=30 maxlength=30></td></tr>");
	out.println("<tr><td align=right>Letter Date:</td><td align=left>");
	out.println("<input name=letter_date value=\""+todayText+
	"\" size=20 maxlength=20></td></tr>");
	out.println("<tr><td colspan=2 align=left><table><tr><td> "+
	"</td><td>From</td><td>To</td></tr>");
	out.println("<tr><td align=right>Check Address Date</td><td>");
	out.println("<input name=ad_date_from value=\""+start_week+
	"\" class=\"date\" size=10 maxlength=10></td><td>");
	out.println("<input class=\"date\" name=ad_date_to value=\""+today+
	"\" size=10 maxlength=10></td></tr></table></td></tr>");
	out.println("</table></td></tr>");
	//
	// 2
	out.println("<tr><td align=right valign=top> - ");
	out.println("<input type=radio name=report "+
	"value=motion "+
	"></td><td align=left>Motion to Dismiss without Prejudice<br> "+
	"Motion Issue Date:"+
	"<input name=motion_date value=\""+today+
	"\" size=10 maxlength=10 class=\"date\"> <br>");
	out.println("</td></tr>");
	//
	// 3
	out.println("<tr><td align=right valign=top> - ");
	out.println("<input type=radio name=report value=release "+
	"></td><td align=left>Release of Judgment<br> Letter Date "+
	"<input name=letter_date2 value=\""+today+
	"\" size=10 maxlength=10 class=\"date\" ><br>");
	out.println("<table><tr><td></td><td>From</td><td>To</td></tr>");
	out.println("<tr><td align=right>Closed Date</td><td>");
	out.println("<input name=date_from value=\""+start_week+
	"\" size=10 maxlength=10 class=\"date\"></td><td>");
	out.println("<input name=date_to value=\""+today+
	"\" size=10 maxlength=10 class=\"date\"></td></tr>");
	out.println("</table></td></tr>");
	//
	// 4
	out.println("<tr><td align=right valign=top> - ");
	out.println("<input type=radio name=report value=complaint "+
	"></td><td align=left>Complaints <br> "+
	"Letter Date "+
	"<input name=letter_date3 value=\""+today+
	"\" size=10 maxlength=10 class=\"date\"><br>");
	out.println("<table><tr><td></td><td>From</td><td>To</td></tr>");
	out.println("<tr><td align=right>Filed Date</td><td>");
	out.println("<input name=co_date_from value=\""+start_week+
	"\" size=10 maxlength=10 class=\"date\"></td><td>");
	out.println("<input name=co_date_to value=\""+today+
	"\" size=10 maxlength=10 class=\"date\"></td></tr>");
	out.println("</table>");
	out.println("</td></tr>");
	//
	// 5 Collection
	out.println("<tr><td align=right valign=top> - ");
	out.println("<input type=radio name=report value=collection "+
	"></td><td align=left>To Collection (Excel File)<br> "+
	"Submit Date "+
	"<input name=letter_date4 value=\""+today+
	"\" size=10 maxlength=10 class=\"date\"><br>");
	out.println("<table><tr><td></td><td>From</td><td>To</td></tr>");
	out.println("<tr><td align=right>Trans to Collect Date</td><td>");
	out.println("<input name=col_date_from value=\""+start_month+
	"\" size=10 maxlength=10 class=\"date\"></td><td>");
	out.println("<input name=col_date_to value=\""+today+
	"\" size=10 maxlength=10 class=\"date\"></td></tr>");
	out.println("</table>");
	out.println("</td></tr>");
	//
	// 6
	out.println("<tr><td align=right valign=top> - ");
	out.println("<input type=radio name=report "+
	"value=pro_supp "+
	"></td><td align=left> ");
	out.println("<table width=100%><tr><td valign=top>Pro Supp List"+
	"</td>");
	out.println("<td align=right valign=top>");
	out.println("<input type=hidden name=action2 value=\"\">");
	out.println("<input type=button "+
	"onClick=\"confirmClear()\" "+
	"value=\"Clear Current Pro Supp's\">");
	out.println("</td></tr><tr><td>&nbsp;</td></tr></table></td></tr>");
	//
	//
	*/
	out.println("</table></td></tr>");
	/*
	  out.println("<tr><td colspan=2 align=right><input type=submit " +
	  "name=action "+
	  "value=Submit></td></tr>");
	*/
	out.println("</table></td></tr>");
	// 
	out.println("</table>");
	// out.println("</form>");				
	out.println("<br />");
	out.println(Inserts.jsStrings(url));	
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
		
	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	// out.println("  $( \"#last_cycle_date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");			
	out.println("</div>");
	out.print("</center></body></html>");
	out.close();

    }
    /**
     * Processes the request and generates the data and write them to a file.
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
	Statement stmt = null, stmt2=null, stmt3=null;
	ResultSet rs = null, rs2=null, rs3=null;

	PrintWriter out = res.getWriter();			  
	String day_date="",date_from="",date_to="", report="", judge="",
	    pos="",letter_date="", cur_path="", motion_date="", 
	    ad_date_from="", ad_date_to="", letter_date2="", letter_date3="",
	    co_date_from="", co_date_to="", action2="",
	    letter_date4="", col_date_from="", col_date_to="";
	    
	String name, value, message="";

	boolean connectDbOk = false, success=true;
	BufferedWriter bw=null,bw2=null;
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
	    else if (name.equals("co_date_from")) {
		co_date_from=value;
	    }
	    else if (name.equals("co_date_to")) {
		co_date_to=value;
	    }
	    else if (name.equals("col_date_from")) {
		col_date_from=value;
	    }
	    else if (name.equals("col_date_to")) {
		col_date_to=value;
	    }			
	    else if (name.equals("ad_date_from")) {
		ad_date_from=value;
	    }
	    else if (name.equals("ad_date_to")) {
		ad_date_to=value;
	    }
	    else if (name.equals("letter_date")) {
		letter_date=value;
	    }
	    else if (name.equals("letter_date2")) {
		letter_date2=value;
	    }
	    else if (name.equals("letter_date3")) {
		letter_date3=value;
	    }
	    else if (name.equals("letter_date4")) {
		letter_date4=value;
	    }			
	    else if (name.equals("motion_date")) {
		motion_date=value;
	    }
	    else if (name.equals("pos")) {
		pos = value;
	    }
	    else if (name.startsWith("day_date")) {
		if(!value.equals(""))
		    day_date = value;
	    }
	    else if (name.equals("report")) {
		report=value;
	    }
	    else if (name.equals("action2")) {
		action2=value;
	    }
	}
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
		stmt2 = con.createStatement();
		stmt3 = con.createStatement();
	    }
	    else{
		success = false;
		message = "Could not connect to DB ";
		logger.error(message);
	    }
	}
	catch(Exception ex){
	    message += ex;
	    success = false;
	    logger.error(ex);
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
	cur_path = server_path;
	//
	Vector<String> wherecases = new Vector<String>();
	if(action2.startsWith("Clear")){
	    //
	    // clear the defendant pro_supp flag
	    //
	    writeHeader(out, user);
	    int nct = 0;
	    String qq = " update legal_cases set pro_supp=null where "+
		" pro_supp is not null ";
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		nct = stmt.executeUpdate(qq);
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+":"+qq);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	    out.println("<h3>Cleared "+nct+" records </h3>");
	    writeFooter(out);
	    return;
	}
	else if(report.equals("addr")){
	    writeHeader(out, user);			
	    String qc = " select count(*) ";
	    String qq = " select c.id,concat_ws(' ',"+
		"da.street_num,da.street_dir,da.street_name,"+
		"da.street_type,da.sud_type,"+
		"da.sud_num),da.city,da.state,da.zip, "+
		"upper(concat_ws(' ',d.f_name,d.l_name)),l.cause_num, "+
		"concat_ws(', ',date_format(c.ini_hear_date,'%m/%d/%Y'),"+
		"date_format(c.contest_hear_date,'%m/%d/%Y'),"+
		"date_format(c.misc_hear_date,'%m/%d/%Y'),"+
		"date_format(c.pro_supp_date,'%m/%d/%Y')) ";
	    String qf = "from legal_cases c,  ";
	    qf += " legal_defendents d,legal_def_case l,legal_def_addresses da ";
	    String qw = "where da.invalid_addr like 'y' "+
		" and not c.status='CL' "; // except closed
	    qw += " and  d.did = l.did and l.id=c.id and d.did=da.defId ";
	    String qo = " order by 6 ";
	    String query ="",str="",str2="",address="",city="",state="",
		zip="", id="", old_id="", defs="", hearings="", cause_num="";
	    if(!ad_date_from.equals("")){
		qw += " and d.addr_req_date >= str_to_date('"+ad_date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!ad_date_to.equals("")){
		qw += " and d.addr_req_date <= str_to_date('"+ad_date_to+
		    "','%m/%d/%Y') ";
	    }
	    int nct = 0;
	    int jj = pos.indexOf(","); // get city name 
	    //
	    if(jj > 0) 
		city = pos.substring(0,jj).toUpperCase();
	    else {
		jj = pos.indexOf(" ");
		if(jj > 0) city = pos.substring(0,jj).toUpperCase();
	    }
	    if(!city.equals("")){
		qw += " and upper(da.city) like '"+city+"'";
	    }
	    city = "";
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
		    // check if this directory exists, if not create one
		    //
		    bw = new BufferedWriter(new 
					    FileWriter(cur_path+"addr_request.xls",false));
		    if(bw == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;

		    }
		    bw2 = new BufferedWriter(new 
					     FileWriter(cur_path+"addr_request.txt",false));
		    if(bw2 == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw.write("<html><head><title>Lables</title></head><body>");
		    bw.newLine();
		    bw.write("<table border>");
		    bw.newLine();
		    bw.write("<tr><td>Post Office</td>"+  
			     "<td>ID</td>"+
			     "<td>Date</td>"+ 
			     "<td>Full Name</td>"+  
			     "<td>Address</td>"+ 
			     "<td>City</td>"+ 
			     "<td>State</td>"+
			     "<td>Zip</td>"+
			     "<td>Cause #</td>"+
			     "<td>Hearings</td></tr>" );
					
		    bw.newLine();
		    //
		    // The label table as Excel/html
		    //
		    bw2.write("\"post_office\","+  
			      "\"ID\","+
			      "\"letter_date\","+  
			      "\"full_name\","+ 
			      "\"address\","+
			      "\"city\","+
			      "\"state\","+
			      "\"zip\","+
			      "\"cause_no\","+
			      "\"hearings\"" );
		    bw2.newLine();
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0;
		    while(rs.next()){
			id = rs.getString(1);
			if(!id.equals(old_id)){
			    address="";city="";state="";zip="";defs="";cause_num="";
			    hearings="";
			    old_id = id;
			    str = rs.getString(2);
			    if(str != null ) address = str.trim();
			    str = rs.getString(3);
			    if(str != null ) city = str;
			    str = rs.getString(4);
			    if(str != null ) state = str;
			    str = rs.getString(5);
			    if(str != null ) zip = str;
			    str = rs.getString(6);
			    if(str != null ) defs = str;
			    str = rs.getString(7);
			    if(str != null && str.length() > 4)
				cause_num = str.substring(4);
			    str = rs.getString(8);
			    if(str != null && !str.trim().equals(""))
				hearings = "Hearings: "+str;
			    nct++;
			    bw.write("<tr><td>"+pos+"</td>"+
				     "<td>"+id+"</td>"+
				     "<td>"+letter_date+"</td>"+ 
				     "<td>"+defs+"</td>"+
				     "<td>"+address+"</td>"+
				     "<td>"+city+"</td>"+
				     "<td>"+state+"</td>"+
				     "<td>"+zip+"</td>"+
				     "<td>53C0"+cause_num+"</td>"+
				     "<td>"+hearings+"</td></tr>");
			    bw.newLine();
			    bw2.write("\""+pos+"\","+  
				      "\""+id+"\","+
				      "\""+letter_date+"\","+  
				      "\""+defs+"\","+ 
				      "\""+address+"\","+
				      "\""+city+"\","+
				      "\""+state+"\","+
				      "\""+zip+"\","+
				      "\""+cause_num+"\","+
				      "\""+hearings+"\"");	
			    bw2.newLine();
			}
		    }
		    bw.write("</table></body></html>");
		    bw.newLine();
		    bw.flush();
		    bw.close();
		    bw2.flush();
		    bw2.close();
		    //
		    out.println("Data written to file successfully<br>");
		    out.println("Total number of processed records: "+
				nct+"<br>");
		    out.println("The Excel file can be found at ");
		    out.println("<a href=\"#\" "+
				"onClick=\"window.open('file:///J:/"+
				"departments/Legal/public/addr_request.xls',"+
				"'Address',"+
				"'toolbar=1,location=1,"+
				"directories=0,status=1,menubar=1,"+
				"scrollbars=1,top=100,left=100,"+
				"resizable=1,width=650,height=600');\">");
		    out.println("J:\\departments\\Legal\\public\\addr_request.xls</a>");
		    //
		    out.println("<br> or <a href=\"#\" onClick=\"window.open("+
				"'file:///I:/public/addr_request.xls',"+
				"'Address',"+
				"'toolbar=1,location=1,"+
				"directories=0,status=1,menubar=1,"+
				"scrollbars=1,top=100,left=100,"+
				"resizable=1,width=650,height=600');\">");
		    out.println("I:\\public\\addr_request.xls</a><br>");
		    out.println("Now run the 'Legal Address Merge ' from your desktop by double clicking on the related Icon (POR) <br>");
		}
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+":"+query);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	    writeFooter(out);
	}
	else if(report.equals("motion")){
	    //
	    writeHeader(out, user);
	    //
	    // this where the close_comment is either dismissed or complied
	    // and status is in Motion to Dismiss (MD) and close date is 
	    // null (will be changed after sending the letter
	    //
	    String qc = " select count(*) ";
	    String qq = " select c.id,l.cause_num, "+
		" upper(concat_ws(' ',d.f_name,d.l_name)), "+
		"concat_ws(' ',"+
		"da.street_num,da.street_dir,da.street_name,"+
		"da.street_type,da.sud_type,"+
		"da.sud_num),da.city,da.state,da.zip ";
	    String qf = "from legal_cases c,legal_def_case l,  "+
		"legal_defendents d, legal_def_addresses da ";
	    String qw = "where c.closed_comments in ('Dismissed','Complied') "+
		" and c.status='MD' and closed_date is null and c.id=l.id "+
		" and l.did=d.did and d.did=da.defId ";

	    String qo = " order by 2 ";
	    String query ="",str="",str2="",address="",city="",state="",
		zip="", id="", old_id="", defs="", cause_num="",
		cause_num2="", day="",month="",year="";
	    int nct = 0, i=0, j=0;
	    String courtNum = "";
	    if(!motion_date.equals("")){
		i = motion_date.indexOf("/");
		j = motion_date.lastIndexOf("/");
		month = motion_date.substring(0,i);
		day = motion_date.substring(i+1,j);
		year = motion_date.substring(j+1);
	    }
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
		    //
		    // check if this directory exists, if not create one
		    //
		    bw = new BufferedWriter(new 
					    FileWriter(cur_path+"motion_request.xls",false));
		    if(bw == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw2 = new BufferedWriter(new 
					     FileWriter(cur_path+"motion_request.txt",false));
		    if(bw2 == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw.write("<html><head><title>Lables</title></head><body>");
		    bw.newLine();
		    bw.write("<table border>");
		    bw.newLine();
		    bw.write("<tr>"+
			     "<td>Full Name</td>"+  
			     "<td>Cause NO.</td>"+
			     "<td>Cause NO 2</td>"+
			     "<td>Date</td>"+ 
			     "<td>Day</td>"+
			     "<td>Month</td>"+
			     "<td>Year</td>"+
			     "<td>Judge</td>"+
			     "<td>Court</td>"+
			     "<td>Address</td>"+ 
			     "<td>City</td>"+ 
			     "<td>State</td>"+
			     "<td>Zip</td></tr>" );
		    bw.newLine();
		    //
		    // The label table as Excel/html
		    //
		    bw2.write("\"full_name\","+  
			      "\"cause_no\","+  
			      "\"cause_no2\","+
			      "\"motion_date\","+
			      "\"day\","+
			      "\"month\","+
			      "\"year\","+
			      "\"judge\","+
			      "\"court\","+
			      "\"address\","+
			      "\"city\","+
			      "\"state\","+
			      "\"zip\"" );
		    bw2.newLine();
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0; 
		    int jj=0;
		    while(rs.next()){
			id = rs.getString(1);
			address="";city="";state="";zip="";defs="";
			old_id = id;
			str = rs.getString(2);
			if(str != null){
			    cause_num = str.substring(4);
			    cause_num2 = str.substring(4,9);
			    courtNum = str.substring(3,5);
			    try{
				jj = Integer.parseInt(courtNum);
				courtNum = court[jj];
			    }catch(Exception ex){};
			}
			nct++;
			str = rs.getString(3);
			if(str != null){
			    if(!defs.equals("")) defs += " and ";
			    defs += str;
			} 
			str = rs.getString(4);
			if(str != null ) address = str.trim();
			str = rs.getString(5);
			if(str != null ) city = str;
			str = rs.getString(6);
			if(str != null ) state = str;
			str = rs.getString(7);
			if(str != null ) zip = str;
			if(!defs.equals("")){
			    defs = replaceAmp(defs);
			}
			query = "select judge from legal_judges "+
			    "where jid="+jj;
			if(debug){
			    logger.debug(query);
			}
			rs2 = stmt2.executeQuery(query);
			if(rs2.next()){
			    str = rs2.getString(1);
			    if(str != null)judge = str;
			}
			bw.write("<tr><td>"+defs+"</td>"+
				 "<td>"+cause_num+"</td>"+ 
				 "<td>"+cause_num2+"</td>"+
				 "<td>"+motion_date+"</td>"+
				 "<td>"+day+"</td>"+
				 "<td>"+month+"</td>"+
				 "<td>"+year+"</td>"+
				 "<td>"+judge+"</td>"+
				 "<td>"+courtNum+"</td>"+
				 "<td>"+address+"</td>"+
				 "<td>"+city+"</td>"+
				 "<td>"+state+"</td>"+
				 "<td>"+zip+"</td></tr>");
			bw.newLine();
			bw2.write("\""+defs+"\","+  
				  "\""+cause_num+"\","+  
				  "\""+cause_num2+"\","+
				  "\""+motion_date+"\","+    
				  "\""+day+"\","+
				  "\""+month+"\","+ 
				  "\""+year+"\","+    
				  "\""+judge+"\","+
				  "\""+courtNum+"\","+    
				  "\""+address+"\","+
				  "\""+city+"\","+
				  "\""+state+"\","+
				  "\""+zip+"\"");	
			bw2.newLine();
		    }
		    bw.write("</table></body></html>");
		    bw.newLine();
		    bw.flush();
		    bw.close();
		    bw2.flush();
		    bw2.close();
		    //
		    out.println("Data written to file successfully<br>");
		    out.println("Total number of processed records: "+
				nct+"<br>");
		    out.println("The Excel file can be found at ");
		    out.println("<a href=\"#\" "+
				"onClick=\"window.open('file:///J:/"+
				"departments/Legal/public/motion_request.xls',"+
				"'Address',"+
				"'toolbar=1,location=1,"+
				"directories=0,status=1,menubar=1,"+
				"scrollbars=1,top=100,left=100,"+
				"resizable=1,width=650,height=600');\">");
		    out.println("J:\\departments\\Legal\\public\\motion_request.xls</a>");
		    out.println("<br> or <a href=\"#\" onClick=\"window.open("+
				"'file:///I:/public/motion_request.xls',"+
				"'Address',"+
				"'toolbar=1,location=1,"+
				"directories=0,status=1,menubar=1,"+
				"scrollbars=1,top=100,left=100,"+
				"resizable=1,width=650,height=600');\">");
		    out.println("I:\\public\\motion_request.xls</a><br>");
		    out.println("Now run the 'Legal Motion Merge' from your "+
				"desktop by double clicking on the related Icon (MTD)<br>");
		}
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+":"+query);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	    writeFooter(out);
	}
	else if(report.equals("release")){
	    //
	    // this where the close_comment is Paid
	    // and status is Closed 
	    //
	    writeHeader(out, user);
	    //			
	    String qc = " select count(*) ";
	    String qq = " select c.id,l.cause_num,"+
		"date_format(c.judgment_date,'%d, %Y'), "+
		"date_format(c.judgment_date,'%m'), "+
		"concat_ws(' ',ll.fname,ll.lname),ll.barNum,ll.title, "+
		" upper(concat_ws(' ',d.f_name,d.l_name)), "+
		"concat_ws(' ',da.street_num,da.street_dir,da.street_name,"+
		"da.street_type,da.sud_type,"+
		"da.sud_num),da.city,da.state,da.zip,d.did ";
	    String qq2 = "select co_name from legal_care_of where def_id=";
	    String qf = "from legal_cases c, attorneys ll,  "+
		" legal_defendents d, legal_def_case l, "+
		" legal_def_addresses da ";
	    String qw = "where c.closed_comments='Paid' "+
		" and c.status='CL' and judgment_date is not null and "+
		" ll.empid=c.lawyerid and d.did=da.defId "+
		" and d.did = l.did and l.id=c.id ";
	    if(!date_from.equals("")){
		qw += " and c.closed_date >= str_to_date('"+date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!date_to.equals("")){
		qw += " and c.closed_date <= str_to_date('"+date_to+
		    "','%m/%d/%Y') ";
	    }
	    String qo = " order by 2 ";
	    String query ="",str="",str2="",address="",city="",state="",
		zip="", id="", old_id="", defs="", cause_num="", jdate="",
		cause_num2="", day="",month="",year="",
		lawyerFullName="", lawyerBarNum="", lawyerTitle="",
		co_name = "", def_id="";
	    int nct = 0, i=0, j=0, dayInt=0, monthInt=0;
	    String courtNum = "";
	    boolean multiple = false;
	    
	    if(!letter_date2.equals("")){
		i = letter_date2.indexOf("/");
		j = letter_date2.lastIndexOf("/");
		month = letter_date2.substring(0,i);
		try{
		    dayInt = Integer.parseInt(letter_date2.substring(i+1,j));
		    monthInt = Integer.parseInt(letter_date2.substring(0,i));
		    day = ""+dayInt+dayOrder[dayInt];
		    month = monthArr[monthInt];
		}catch(Exception ex){};
		year = letter_date2.substring(j+1);
	    }
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
		    //
		    // check if this directory exists, if not create one
		    //
		    bw = new BufferedWriter(new 
					    FileWriter(cur_path+"release_request.xls",false));
		    if(bw == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw2 = new BufferedWriter(new 
					     FileWriter(cur_path+"release_request.txt",false));
		    if(bw2 == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw.write("<html><head><title>Release Request"+
			     "</title></head><body>");
		    bw.newLine();
		    bw.write("<table border>");
		    bw.newLine();
		    bw.write("<tr>"+
			     "<td>Full Name</td>"+  
			     "<td>Cause NO.</td>"+
			     "<td>Cause NO 2</td>"+
			     "<td>Date</td>"+ 
			     "<td>Day</td>"+
			     "<td>Month</td>"+
			     "<td>Year</td>"+
			     "<td>Judgment Date</td>"+
			     "<td>Address</td>"+ 
			     "<td>Defendent</td>"+
			     "<td>Has/Have</td>"+
			     "<td>Lawyer Name</td>"+
			     "<td>Lawyer Bar #</td>"+
			     "<td>Lawyer Title</td></tr>");
		    bw.newLine();
		    //
		    // The label table as Excel/html
		    //
		    bw2.write("\"full_name\","+  
			      "\"cause_no\","+  
			      "\"cause_no2\","+
			      "\"letter_date\","+
			      "\"day\","+
			      "\"month\","+
			      "\"year\","+
			      "\"judg_date\","+
			      "\"address\","+
			      "\"defendent\","+
			      "\"posses\","+
			      "\"lawyerFullName\","+
			      "\"LawyerBarNum\","+
			      "\"LawyerTitle\"");
		    bw2.newLine();
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0; 
		    int jj=0;
		    while(rs.next()){
			id = rs.getString(1);
			address="";city="";state="";zip="";defs="";
			jdate="";cause_num ="";cause_num2="";
			co_name="";
			old_id = id;
			str = rs.getString(2);
			if(str != null){
			    if(str.length() > 3)
				cause_num = str.substring(4);
			    if(str.length() > 4)
				cause_num2 = str.substring(4,5);
			}
			str = rs.getString(3);
			if(str != null ) jdate = str.trim();
			jj = rs.getInt(4);
			jdate = monthArr[jj]+" "+jdate;
			str = rs.getString(5);
			if(str != null) lawyerFullName = str;
			str = rs.getString(6);
			if(str != null) lawyerBarNum = str;
			str = rs.getString(7);
			if(str != null) lawyerTitle = str;
			nct++;
			// 
			// second query starts here
			//
			str = rs.getString(8);
			if(str != null){
			    if(!defs.equals("")) defs += " and ";
			    defs += str;
			} 
			str = rs.getString(9);
			if(str != null ) address = str.trim();
			str = rs.getString(10);
			if(str != null ){
			    city = str;
			    if(!city.equals("")){
				if(!address.equals("")) address += ", ";
				address += city;
			    }
			}
			str = rs.getString(11);
			if(str != null ){
			    state = str;
			    if(!state.equals("")){
				if(!address.equals("")) address += ", ";
				address += state;
			    }
			}
			str = rs.getString(12);
			if(str != null ){
			    zip = str;
			    if(!zip.equals("")){
				if(!address.equals("")) address += " ";
				address += zip;
			    }
			}
			str = rs.getString(13);
			if(str != null ){
			    def_id = str;
			}
			query = qq2+def_id;
			if(debug){
			    logger.debug(query);
			}
			rs2 = stmt2.executeQuery(query);
			if(rs2.next()){
			    str = rs2.getString(1);
			    if(str != null) co_name = str;
			}
			if(!co_name.equals("")){
			    address = "c/o "+co_name+" "+address;
			}
			//
			// check if this is a one defendant case
			// or multiple, in the old cases the defendants
			// were listed together using & symbol
			// we had to use both & and "and" to figure 
			// out  
			//
			multiple = false;
			if(defs.indexOf(" & ") > -1 ){
			    multiple = true;
			    defs = replaceAmp(defs);
			}
			else if(defs.indexOf(" and ") > -1) 
			    multiple = true;
			bw.write("<tr><td>"+defs+"</td>"+
				 "<td>"+cause_num+"</td>"+ 
				 "<td>"+cause_num2+"</td>"+
				 "<td>"+letter_date2+"</td>"+
				 "<td>"+day+"</td>"+
				 "<td>"+month+"</td>"+
				 "<td>"+year+"</td>"+
				 "<td>"+jdate+"</td>"+
				 "<td>"+address+"</td>");
			if(multiple){
			    bw.write("<td>Defendants</td>");
			    bw.write("<td>have</td>");
			}
			else{
			    bw.write("<td>Defendant</td>");
			    bw.write("<td>has</td>");
			}
			bw.write("<td>"+lawyerFullName+"</td>");
			bw.write("<td>"+lawyerBarNum+"</td>");
			bw.write("<td>"+lawyerTitle+"</td></tr>");
			bw.newLine();
			bw2.write("\""+defs+"\","+  
				  "\""+cause_num+"\","+  
				  "\""+cause_num2+"\","+
				  "\""+letter_date2+"\","+    
				  "\""+day+"\","+
				  "\""+month+"\","+ 
				  "\""+year+"\","+    
				  "\""+jdate+"\","+
				  "\""+address+"\",");
			if(multiple){
			    bw2.write("\"Defendants\",");
			    bw2.write("\"have\",");
			}
			else{
			    bw2.write("\"Defendant\",");
			    bw2.write("\"has\",");
			}
			bw2.write("\""+lawyerFullName+"\",");
			bw2.write("\""+lawyerBarNum+"\",");
			bw2.write("\""+lawyerTitle+"\"");
			bw2.newLine();
		    }
		    bw.write("</table></body></html>");
		    bw.newLine();
		    bw.flush();
		    bw.close();
		    bw2.flush();
		    bw2.close();
		    //
		    out.println("Data written to file successfully<br>");
		    out.println("Total number of processed records: "+
				nct+"<br>");
		    out.println("The Excel file can be found at ");
		    out.println("<a href=\"#\" "+
				"onClick=\"window.open('file:///J:/"+
				"departments/Legal/public/release_request.xls',"+
				"'Address',"+
				"'toolbar=1,location=1,"+
				"directories=0,status=1,menubar=1,"+
				"scrollbars=1,top=100,left=100,"+
				"resizable=1,width=650,height=600');\">");
		    out.println("J:\\departments\\Legal\\public\\release_request.xls</a>");
		    out.println("<br> or <a href=\"#\" onClick=\"window.open("+
				"'file:///I:/public/release_request.xls',"+
				"'Address',"+
				"'toolbar=1,location=1,"+
				"directories=0,status=1,menubar=1,"+
				"scrollbars=1,top=100,left=100,"+
				"resizable=1,width=650,height=600');\">");
		    out.println("I:\\public\\release_request.xls</a><br>");
		    out.println("Now run the 'Legal Release Merge' from your desktop by double clicking on the related Icon (REL)<br>");
		}
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+":"+query);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	    writeFooter(out);
	}
	else if(report.equals("complaint")){ // not needed any more
	    //
	    // PART I Letter merge
	    // ===================
	    // these are the new violations in the system
	    // to get the cause num from the court for them
	    // they are known from the entry date
	    //
	    writeHeader(out, user);
	    //
	    String qc = " select count(*) ";
	    String qq = " select c.id,"+
		"'Rental Address',"+ // 2
		"c.court_cost,"+
		"c.fine,c.per_day, "+ // 5
		" upper(d.l_name),upper(d.f_name), "+
		"concat_ws(' ',ll.fname,ll.lname),ll.barNum,ll.title, "+
		"concat_ws(' ',da.street_num,da.street_dir,da.street_name,"+
		"da.street_type,da.sud_type,"+
		"da.sud_num),da.city,da.state,da.zip,d.did,l.cause_num, "+// 15
		"date_format(c.ini_hear_date,'%D'), "+ // day with suffix
		"date_format(c.ini_hear_date,'%M'),"+
		"date_format(c.ini_hear_date,'%Y')";
	    String qq2 = "select concat_ws(' ',upper(d.f_name),upper(d.l_name)) from legal_defendents d,legal_def_case l where d.did = l.did and l.id=";
	    String qq3 = " select v.dates,v.amount,vc.codes,"+ // 3
		"v.ident, "+  // 
		"vc.complaint "; // 5
	    //
	    // just to get the dept name
	    String qq4 = " select d.dept from legal_cases c,legal_depts d,"+
		" legal_type_dept t where c.case_type=t.typeId "+
		" and d.deptId=t.deptId  and c.id=";
	    //
	    String qf = " from legal_cases c, "+
		"legal_defendents d, legal_def_case l,"+
		"legal_case_violations v,"+
		"legal_def_addresses da,"+
		"attorneys ll ";
	    //
	    String qf3 = " from legal_case_violations v,"+
		"legal_viol_subcats vc ";
	    String qw = " where d.did = l.did and l.id=c.id and "+
		" c.id=v.id and v.entered is not null "+
		" and da.invalid_addr is null "+ // avoid invalid addresses
		" and c.lawyerid=ll.empid "+
		" and d.did=da.defId ";
	    if(!co_date_from.equals("")){
		qw += " and c.filed >= str_to_date('"+co_date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!co_date_to.equals("")){
		qw += " and c.filed <= str_to_date('"+co_date_to+
		    "','%m/%d/%Y') ";
	    }
	    String qw2 = " where d.did = l.did "+
		" and da.invalid_addr is null "+
		" and d.did=da.defId "+
		" and l.id=";
	    String qw3 = " where v.sid = vc.sid and v.id=";
	    String qo = " order by 6,7 ";
	    String qq5 = "select co_name from legal_care_of where def_id=";
	    String query ="",str="",str2="", address="", address2="",
		city="", state="", fine="", dept="",
		zip="", id="", old_id="", defs="", cause_num="", jdate="",
		dates="", complaint="",codes="",amount="",ident="", posPron="",
		cause_num2="", day="",month="",year="",court_cost="",
		moreOne = "", per_day = "", old_def = "", old_did = "",
		lawyerFullName ="", lawyerBarNum = "", lawyerTitle = "";
	    String ini_hear_day="", ini_hear_month="", ini_hear_year="",
		ini_hear="";
	    String co_name = "";
	    int nct=0, i=0, j=0, dayInt=0, monthInt=0;
	    String courtNum = "";
	    boolean multiple = false;
	    if(!letter_date3.equals("")){
		i = letter_date3.indexOf("/");
		j = letter_date3.lastIndexOf("/");
		month = letter_date3.substring(0,i);
		try{
		    dayInt = Integer.parseInt(letter_date3.substring(i+1,j));
		    monthInt = Integer.parseInt(letter_date3.substring(0,i));
		    day = ""+dayInt+dayOrder[dayInt];
		    month = monthArr[monthInt];
		}catch(Exception ex){};
		year = letter_date3.substring(j+1);
	    }
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
		    out.println("<center><h3>No records matched </h3>");
		    out.println("Verify the date range and then try again");
		    out.println("<br></center></body></html>");
		    writeFooter(out);
		    Helper.databaseDisconnect(con,stmt,rs);
		    return;
		}
		else{
		    //
		    // check if this directory exists, if not create one
		    //
		    bw = new BufferedWriter(new 
					    FileWriter(cur_path+"complaints.xls",false));
		    if(bw == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw2 = new BufferedWriter(new 
					     FileWriter(cur_path+"complaints.txt",false));
		    if(bw2 == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw.write("<html><head><title>Complaint Request"+
			     "</title></head><body>");
		    bw.newLine();
		    bw.write("<table border>");
		    bw.newLine();
		    bw.write("<tr>"+
			     "<td>Full Name</td>"+  
			     "<td>Date</td>"+ 
			     "<td>Day</td>"+
			     "<td>Month</td>"+
			     "<td>Year</td>"+
			     "<td>Dates</td>"+
			     "<td>Codes</td>"+
			     "<td>Complaint</td>"+
			     "<td>Fine</td>"+
			     "<td>Address</td>"+ 
			     "<td>Rent Address</td>"+ 
			     "<td>City, State Zip</td>"+
			     "<td>Court Cost</td>"+
			     "<td>Co-Defendants</td>"+
			     "<td>Defendent</td>"+
			     "<td>Lawyer Name</td>"+
			     "<td>Lawyer BarNum</td>"+
			     "<td>Lawyer Title</td>"+
			     "<td>Dept</td>"+
			     "<td>Cause Number</td>"+
			     "<td>Init Hearing Day</td>"+
			     "<td>Init Hearing Month</td>"+
			     "<td>Init Hearing Year</td>"+
			     "</tr>");

		    bw.newLine();
		    //
		    // The label table as Excel/html
		    //
		    bw2.write("\"full_name\","+  
			      "\"letter_date\","+
			      "\"day\","+
			      "\"month\","+
			      "\"year\","+
			      "\"dates\","+
			      "\"Codes\","+
			      "\"Complaint\","+
			      "\"Fines\","+
			      "\"address\","+
			      "\"rentAddress\","+
			      "\"cityStateZip\","+
			      "\"courtCost\","+
			      "\"codef\","+
			      "\"defendent\","+
			      "\"lawyerFullName\","+
			      "\"lawyerBarNum\","+
			      "\"lawyerTitle\","+
			      "\"Dept\","+
			      "\"cause_no\","+
			      "\"ini_hear_day\","+
			      "\"ini_hear_month\","+
			      "\"ini_hear_year\"");
		    bw2.newLine();
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    int jj=0;
		    String lastInsert = "";
		    String total_text="", did="",codef=""; // Codefendants
		    float total = 0f;
		    String lastArr[] = null;
		    String firstArr[] = null;
		    boolean in = false;
		    while(rs.next()){
			co_name = ""; // care of name
			id = rs.getString(1);
			did = rs.getString(15); 
			if(id.equals(old_id) && did.equals(old_did)) continue;
			old_id = id; 
			old_did = did;
			AddressList al = new AddressList(debug);
			al.setCase_id(id);
			// al.setRental_addr("Y");
			List<Address> addresses = null;						
			String back = al.lookFor();
			if(back.equals("")){
			    addresses = al.getAddresses(); // rental only
			    if(addresses != null){
				Address adr = addresses.get(0);
				address2 = adr.getAddress();
			    }
			}
			//
			str = rs.getString(3); // court cost
			if(str != null){
			    if(str.endsWith(".0")) str += "0";
			    if(str.startsWith("0")) str = courtCostDefault;
			    court_cost = str; 
			}
			fine = rs.getString(4);
			str = rs.getString(5); // per_day flag
			if(str != null && !str.equals(""))
			    fine += " per day";
			//
			str = rs.getString(8);
			if(str != null) lawyerFullName=str;
			str = rs.getString(9);
			if(str != null) lawyerBarNum=str;
			str = rs.getString(10);
			if(str != null) lawyerTitle=str;
			//
			// Get the dept name
			//
			query = qq4+id;
			if(debug){
			    logger.debug(query);
			}
			rs2 = stmt2.executeQuery(query);
			if(rs2.next()){
			    dept = rs2.getString(1);
			    if(dept == null) dept = "";
			}
			//
			// Get the defendant(s) names 
			//
			query = qq2+id+" and l.did != "+did;
			if(debug){
			    logger.debug(query);
			}
			rs2 = stmt2.executeQuery(query);
			while(rs2.next()){
			    str = rs2.getString(1);
			    if(str != null){
				if(!codef.equals("")) codef +=", ";
				codef += str;
			    }
			}
			// get the co_name
			query = qq5+did;
			if(debug){
			    logger.debug(query);
			}
			rs2 = stmt2.executeQuery(query);
			while(rs2.next()){
			    str = rs2.getString(1);
			    if(str != null) co_name = str;
			}
			jj = 0;
			//
			// get the first name and last name
			// from the first query
			//
			str = rs.getString(6);
			str2 = rs.getString(7);
			if(str != null || str2 != null){
			    if(str == null) str = "";
			    if(str2 == null) str2 = "";
			    jj++;
			    defs = (str2+" "+str).trim();
			}
			str = rs.getString(11).trim();
			if(str != null){
			    // trim and remove more than one space
			    address = str.trim().replaceAll("  "," "); 
			}
			if(!co_name.equals("")){
			    address = "c/o: "+co_name+" "+address;
			}
			str = rs.getString(12);
			if(str != null ){
			    city = str;
			}
			str = rs.getString(13);
			if(str != null ){
			    state = str;
			}
			str = rs.getString(14);
			if(str != null ){
			    zip = str;
			}
			str = rs.getString(16);
			if(str != null && str.length() > 4)
			    cause_num=str.substring(4);
			str = rs.getString(17);
			if(str != null)
			    ini_hear_day = str;
			str = rs.getString(18);
			if(str != null)
			    ini_hear_month = str;
			str = rs.getString(19);
			if(str != null)
			    ini_hear_year = str;						
			// 
			// Get the violation info
			//
			query = qc+qf3+qw3+id;
			if(debug){
			    logger.debug(query);
			}
			rs2 = stmt2.executeQuery(query);
			int nct2=0;
			if(rs2.next()){
			    nct2 = rs2.getInt(1);
			}
			if(nct2 > 0){
			    query = qq3+qf3+qw3+id; 
			    if(debug){
				logger.debug(query);
			    }
			    rs2 = stmt2.executeQuery(query);
			    dates="";codes="";
			    while(rs2.next()){
				str = rs2.getString(1);  // dates
				if(str != null ){
				    if(!dates.equals("")){
					// 
					// avoid same dates
					if(dates.indexOf(str) == -1){
					    dates = 
						dates.replaceAll(" and ",", ");
					    dates += " and ";
					    dates += str.trim();
					}
				    }
				    else 
					dates = str.trim();
				}
				str = rs2.getString(3);   // codes
				if(str != null){
				    if(!codes.equals("")){
					//
					// get rid of (, and)
					str = str.replaceAll(","," ");
					str = str.replaceAll("and"," ");
					
					String codeArr[] = str.split("\\s");
					for(i=0;i<codeArr.length;i++){
					    codeArr[i] = 
						codeArr[i].trim();
					    // 
					    // if not in the list add it
					    if(codes.indexOf(codeArr[i]) 
					       == -1){
						codes += ", "+codeArr[i];
						lastInsert = codeArr[i];
					    }
					}
					// it could happen that the code array 
					// are the same, nothing will be added
					// therefore we check if the prev for
					// loop added anything
					//
					if(!lastInsert.equals("")){
					    //
					    // get rid of the old and
					    //
					    codes = 
						codes.replaceAll(" and ",", "); 
					    //
					    // we are adding 'and' just before
					    // the last code
					    //
					    codes = 
						codes.replaceAll(", "+lastInsert,
								 " and "+
								 lastInsert);
					    lastInsert = ""; 
					}
				    }
				    else{
					// first time
					codes = str.trim();
				    }			    
				}
				str = rs2.getString(4); // ident
				if(str != null)
				    ident = str; 
				str = rs2.getString(5);  // complaint
				if(str != null){
				    if(!complaint.equals("")){
					complaint += " and ";
				    }
				    complaint += str.trim();
				}
			    }
			}
			//
			// write this record
			//
			multiple = false;
			if(defs.indexOf(" & ") > -1){
			    defs = replaceAmp(defs);
			}
			if(defs.indexOf(" and ") 
			   > -1){ 
			    multiple = true;
			    moreOne="s";
			}
			if(!ident.equals("")){
			    posPron = getPosPron(ident);
			}
			//
			// in the coplaint text we have both, _his/her_
			// and _his/her/their_
			//
			complaint = 
			    complaint.replaceAll("_his/her_",
						 posPron);
			complaint = 
			    complaint.replaceAll("_his/her/their_",
						 posPron);
			//
			// for certain violations the address
			// of violation is needed but could be
			// the same as defendant address
			//
			if(address2.equals("")) address2 = address;
			complaint = complaint.replaceAll("_address_",
							 address2);
			bw.write("<tr><td>"+defs+"</td>"+
				 "<td>"+letter_date3+"</td>"+
				 "<td>"+day+"</td>"+
				 "<td>"+month+"</td>"+
				 "<td>"+year+"</td>"+
				 "<td>"+dates+"</td>"+
				 "<td>"+codes+"</td>"+
				 "<td>"+complaint+"</td>"+
				 "<td>$"+fine+"</td>"+
				 "<td>"+address+"</td>"+
				 "<td>"+address2+"</td>"+
				 "<td>"+city+", "+state+" "+
				 zip+"</td>"+
				 "<td>"+court_cost+"</td>");
			bw.write("<td>"+codef+"</td>");
			if(multiple){
			    bw.write("<td>Defendants</td>");
			}
			else{
			    bw.write("<td>Defendant</td>");
			}
			bw.write("<td>"+lawyerFullName+"</td>");
			bw.write("<td>"+lawyerBarNum+"</td>");
			bw.write("<td>"+lawyerTitle+"</td>");
			bw.write("<td>"+dept+"</td>");
			bw.write("<td>53C0"+cause_num+"</td>");
			bw.write("<td>"+ini_hear_day+"</td>");
			bw.write("<td>"+ini_hear_month+"</td>");
			bw.write("<td>"+ini_hear_year+"</td>");
			bw.write("</tr>");
			bw.newLine();
			bw2.write("\""+defs+"\","+  
				  "\""+letter_date3+"\","+    
				  "\""+day+"\","+
				  "\""+month+"\","+ 
				  "\""+year+"\","+    
				  "\""+dates+"\","+
				  "\""+codes+"\","+
				  "\""+complaint+"\","+
				  "\"$"+fine+"\","+ // total_text
				  "\""+address+"\","+
				  "\""+address2+"\","+
				  "\""+city+", "+state+" "+zip+"\","+
				  "\""+court_cost+"\","+
				  "\""+codef+"\",");
			bw2.write("\""+moreOne+"\",");
			bw2.write("\""+lawyerFullName+"\",");
			bw2.write("\""+lawyerBarNum+"\",");
			bw2.write("\""+lawyerTitle+"\",");
			bw2.write("\""+dept+"\",");
			bw2.write("\""+cause_num+"\",");
			bw2.write("\""+ini_hear_day+"\",");
			bw2.write("\""+ini_hear_month+"\",");					
			bw2.write("\""+ini_hear_year+"\"");
			bw2.newLine();
			//
			address=""; address2="";city=""; state=""; 
			zip=""; defs=""; fine="";per_day=""; codef="";
			codes="";complaint="";ident="";amount="";
			dates=""; posPron=""; cause_num="";
			ini_hear_day="";ini_hear_month="";ini_hear_year="";
			in = false; moreOne="";multiple=false;
		    }
		}
		bw.write("</table></body></html>");
		bw.newLine();
		bw.flush();
		bw.close();
		bw2.flush();
		bw2.close();
		//
		out.println("Data written to file successfully<br>");
		out.println("Total number of processed records: "+
			    nct+"<br>");
		out.println("The Excel file can be found at ");
		out.println("<a href=\"#\" "+
			    "onClick=\"window.open('file:///J:/"+
			    "departments/Legal/public/complaints.xls',"+
			    "'Address',"+
			    "'toolbar=1,location=1,"+
			    "directories=0,status=1,menubar=1,"+
			    "scrollbars=1,top=100,left=100,"+
			    "resizable=1,width=650,height=600');\">");
		out.println("J:\\departments\\Legal\\public\\complaints.xls</a>");
		out.println("<br> or <a href=\"#\" onClick=\"window.open("+
			    "'file:///I:/public/complaints.xls',"+
			    "'Address',"+
			    "'toolbar=1,location=1,"+
			    "directories=0,status=1,menubar=1,"+
			    "scrollbars=1,top=100,left=100,"+
			    "resizable=1,width=650,height=600');\">");
		out.println("I:\\public\\complaints.xls</a><br>");
		out.println("Now from your desktop you can run <br>"+
			    "<li> 'Legal Complaint Mail' merger (LCM)");

		out.println("<li> 'Complaint Mail Label' merger (CML)<br>");
		out.println("<li> 'Complaint Folder Label' merger (CFL)<br>");
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+":"+query);
	    }
	    //
	    // PART II lables merge 
	    // ====================
	    // Generate two kind of labels, mailing label and folder label data
	    // A: For mailing  label, each will have the defendant name,
	    //    address, city, state, zip. There will be 
	    //   1 - One label for the group of defendents
	    //   2 - Two labels for each individual defendant
	    //    i.e. for a one defendant case, there will be 3 labels and for
	    //     two defendant case there will be 5 labels, and so on adding
	    //     two labels per additional defendant
	    //
	    // B: For folder labal, there will be one per case with all 
	    //    defendants names, case type and year 
	    //
	    qq = " select c.id,"+
		"ct.category,upper(d.l_name),upper(d.f_name),d.did,"+
		"date_format(c.ini_hear_date,'%m/%d/%Y') ";

	    qq2 = " select upper(d.l_name),upper(d.f_name), "+
		"concat_ws(' ',da.street_num,da.street_dir,da.street_name,"+
		"da.street_type,da.sud_type,"+
		"da.sud_num),da.city,da.state,da.zip,l.cause_num ";
	    qf = " from legal_cases c,legal_case_violations v,"+
		"legal_viol_subcats vc, legal_viol_cats ct,  "+
		"legal_defendents d,legal_def_case l, "+
		"legal_def_addresses da ";
	    //
	    String qf2 = "from legal_defendents d, legal_def_case l ";
	    qw = "where c.id=v.id and v.sid=vc.sid "+
		" and v.entered is not null and vc.cid=ct.cid "+
		" and da.invalid_addr is null "+ // avoid invalid addresses
		" and d.did=l.did and l.id=c.id "+
		" and d.did=da.defId ";
	    if(!co_date_from.equals("")){
		qw += " and c.filed >= str_to_date('"+co_date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!co_date_to.equals("")){
		qw += " and c.filed <= str_to_date('"+co_date_to+
		    "','%m/%d/%Y') ";
	    }
	    qw2 = " where d.did = l.did "+
		" and da.invalid_addr is null "+
		" and l.id=";
	    qo = " order by 3,4 ";
	    String type="", defs2="";
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
		    //
		    // check if this directory exists, if not create one
		    //
		    bw = new BufferedWriter(new 
					    FileWriter(cur_path+"compMailLabels.txt",false));
		    if(bw == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw2 = new BufferedWriter(new 
					     FileWriter(cur_path+"compFolderLabels.txt",false));
		    if(bw2 == null){
			out.println("Could not open file");
			out.close();
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    //
		    // mail label titles
		    //
		    bw.write("\"Contact_Name\","+  
			     "\"Address\","+
			     "\"city\",\"State\",\"Zip\"");
		    bw.newLine();
		    //
		    // The folder label titles
		    //
		    bw2.write("\"full_name\","+  
			      "\"type\","+
			      "\"ini_date\","+
			      "\"cause_num\"");
		    bw2.newLine();
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    nct = 0; 
		    int jj = 0;
		    id="";
		    String last="",first="", did="";
		    boolean in = false;
		    HashSet<String> set = new HashSet<String>(50);
		    old_id="";old_did="";
		    while(rs.next()){
			cause_num="";
			id = rs.getString(1);
			did = rs.getString(5);
			if(id.equals(old_id) && did.equals(old_did))continue;
			old_id = id;
			old_did = did;
			if(!set.contains(id)){
			    set.add(id);
			    address=""; city=""; state=""; 
			    zip=""; defs=""; type=""; defs2="";first="";
			    last=""; ini_hear="";
			    str = rs.getString(2);
			    if(str != null){
				type = str;
			    }
			    str = rs.getString(6);
			    if(str != null){
				ini_hear = str;
			    }
			    query = qc+qf2+qw2+id;
			    if(debug){
				logger.debug(query);
			    }
			    rs2 = stmt2.executeQuery(query);
			    int nct2 = 0;
			    if(rs2.next()){
				nct2 = rs2.getInt(1);
			    }
			    // jj=0;
			    if(nct2 > 0){
				query = qq2+qf2+qw2+id+" order by 1"; 
				if(debug){
				    logger.debug(query);
				}
				rs2 = stmt2.executeQuery(query);
				jj=0;
				while(rs2.next()){
				    str = rs2.getString(1);
				    str2 = rs2.getString(2);
				    if(str2 == null) str2 = "";
				    if(str != null){
					str = str.trim();
					last = str;
					str2 = str2.trim();
					first = str2;
					if(!defs.equals("")) defs += ", ";
					if(!defs2.equals("")) defs2 += ", ";
					defs += first+" "+last;
					defs2 += last+", "+first;
					str = rs2.getString(3);
					if(str != null) 
					    address = str.trim();
					str = rs2.getString(4);
					if(str != null){
					    city = str;
					}
					str = rs2.getString(5);
					if(str != null){
					    state = str;
					}
					str = rs2.getString(6);
					if(str != null){
					    zip = str;
					}
					str = rs2.getString(7);
					if(str != null){
					    cause_num = str;
					}	
					jj++;
					//
					// one lable (changed from 2)
					bw.write("\""+(first+" "+
						       last).trim()+"\","+
						 "\""+address+"\","+    
						 "\""+city+"\",\""+
						 state+"\",\""+zip+"\"");
					bw.newLine();
					//
					// folder label (individual)
					//
					bw2.write("\""+
						  (last+", "+first).trim()+"\","+  
						  "\""+type+"\","+
						  "\""+ini_hear+"\","+
						  "\""+cause_num+"\"");
					bw2.newLine();
				    }
				}
				//
				bw.write("\""+defs.trim()+"\","+  
					 "\""+address+"\","+    
					 "\""+city+"\",\""+state+"\",\""+
					 zip+"\"");
				bw.newLine();
				//
				// folder label (only for multiple)
				//
				if(jj > 1){
				    if(cause_num.length() > 5){
					cause_num = cause_num.substring(0,5);
				    }
				    for(i=0;i<jj;i++){
					bw2.write("\""+defs2.trim()+"\","+  
						  "\""+type+"\","+
						  "\""+ini_hear+"\","+
						  "\""+cause_num+"\"");
					bw2.newLine();
				    }
				}
			    }
			}
		    }
		}
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+":"+query);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	    writeFooter(out);
	    if(bw != null){
		bw.flush();
		bw.close();
		bw2.flush();
		bw2.close();
	    }
	}
	else if(report.startsWith("collect")){  // not needed any more
	    String qc = " select count(*) ";
	    String q = " select c.id ";
	    String qf = " from legal_cases c ";
	    String qw = "", qq = "";
	    if(!col_date_from.equals("")){
		qw += " c.trans_to_collect >= str_to_date('"+col_date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!col_date_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += " c.trans_to_collect <= str_to_date('"+col_date_to+
		    "','%m/%d/%Y') ";
	    }
	    if(!qw.equals("")) qw = " where "+qw;
	    qq = qc + qf + qw;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		int count = 0;
		if(rs.next()){
		    count = rs.getInt(1);
		}
		if(count == 0){
		    //
		    // no match found
		    //
		    writeHeader(out, user);
		    out.println("<div id=\"mainContent\">");					
		    out.println("<h3>No records matched </h3>");
		    writeFooter(out);
		    return;
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	else if(report.startsWith("pro_sup")){
	    //
	    // avoid defendants with bad addresses
	    //
	    String address="",city="",state="",zip="",
		query="", str="", str2="", defs="";
	    String qc = " select count(*) ";
	    String qq = " select c.id,upper(d.l_name),upper(d.f_name) ";  
	    String qf = " from legal_cases c,"+
		"legal_defendents d,legal_def_case l,legal_def_addresses da ";
	    String qw = "where "+
		" da.invalid_addr is null "+ // avoid invalid addresses
		" and d.did=l.did and l.id=c.id and c.pro_supp='y' "+
		" and d.did=da.defId ";

	    String qq2 = " select upper(d.l_name),upper(d.f_name), "+
		"concat_ws(' ',"+
		"da.street_num,da.street_dir,da.street_name,"+
		"da.street_type,da.sud_type,"+
		"da.sud_num),da.city,da.state,da.zip ";
	    String qf2 = " from legal_defendents d, legal_def_case l, "+
		"legal_def_addresses da ";
	    String qw2 = " where d.did = l.did "+
		" and da.invalid_addr is null "+
		" and d.did=da.defId "+
		" and l.id=";
	    String qo  = " order by d.l_name,d.f_name ";
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
		    out.println("<center><h3>No records matched </h3>");
		    out.println("Verify that the Pro Supp checkbox is checked"+
				" for those defendants and try again later");
		    out.println("<br>");
		    writeFooter(out);
		    Helper.databaseDisconnect(con,stmt,rs);
		    return;
		}
		else{
		    //
		    // check if this directory exists, if not create one
		    //
		    bw = new
			BufferedWriter(new 
				       FileWriter(cur_path+"pro_supp.xls",false));
		    if(bw == null){
			out.println("Could not open file");
			writeFooter(out);
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw2 = new
			BufferedWriter(new 
				       FileWriter(cur_path+"pro_supp.txt",false));
		    if(bw2 == null){
			out.println("Could not open file");
			writeFooter(out);
			Helper.databaseDisconnect(con,stmt,rs);
			return;
		    }
		    bw.write("<html><head><title>Pro Supp Labels"+
			     "</title></head><body>");
		    bw.newLine();
		    bw.write("<table border>");
		    bw.newLine();
		    bw.write("<tr>"+
			     "<td>Full Name</td>"+  
			     "<td>Address</td>"+ 
			     "<td>City</td>"+
			     "<td>State</td>"+
			     "<td>Zip code</td></tr>");
		    bw2.write("\"Contact_Name\","+  
			      "\"Address\","+
			      "\"city\",\"State\",\"Zip\"");
		    bw2.newLine();
		    query = qq+qf+qw+qo;
		    if(debug){
			logger.debug(query);
		    }
		    rs = stmt.executeQuery(query);
		    int jj=0;
		    String id="";
		    String lastArr[] = null;
		    String firstArr[] = null;
		    String last="",first="";
		    boolean in = false;
		    HashSet<String> set = new HashSet<String>(50);
		    while(rs.next()){
			id = rs.getString(1);
			if(set.contains(id)) continue;
			else 
			    set.add(id);
			address=""; city=""; state=""; 
			zip=""; defs=""; first="";last="";
			query = qc+qf2+qw2+id;
			if(debug){
			    logger.debug(query);
			}
			rs2 = stmt2.executeQuery(query);
			int nct2 = 0;
			if(rs2.next()){
			    nct2 = rs2.getInt(1);
			}
			jj=0;
			if(nct2 > 0){
			    lastArr = new String[nct2];
			    firstArr = new String[nct2];
			    for(int k=0;k<nct2;k++){
				lastArr[k] = "";
				firstArr[k] = "";
			    }
			    str = rs.getString(2);
			    if(str != null) last = str;
			    str = rs.getString(3);
			    if(str != null) first = str;
			    lastArr[0] = last;
			    firstArr[0] = first;
			    defs = first+" "+last;
			    jj++;
			    query = qq2+qf2+qw2+id+" "+qo; 
			    if(debug){
				logger.debug(query);
			    }
			    rs2 = stmt2.executeQuery(query);
			    while(rs2.next()){
				str = rs2.getString(1);
				str2 = rs2.getString(2);
				if(str2 == null) str2 = "";
				if(str != null){
				    str = str.trim();
				    str2 = str2.trim();
				    if(!first.equals(str2) || 
				       !last.equals(str)){
					if(jj < nct2){ // should not happen
					    lastArr[jj] = str;
					    firstArr[jj] = str2;
					}
					defs += ", ";
					defs += str2+" "+str; 
					jj++;
				    }
				}
				str = rs2.getString(3);
				if(str != null) 
				    address = str.trim();
				str = rs2.getString(4);
				if(str != null){
				    city = str;
				}
				str = rs2.getString(5);
				if(str != null){
				    state = str;
				}
				str = rs2.getString(6);
				if(str != null){
				    zip = str;
				}
			    }
			    //
			    // four labels each
			    //
			    int lblCnt = 2; // for single defendant case
			    if(defs.indexOf(",") > -1){
				lblCnt = 3; // for multi defendant case
			    }
			    for(int i=0;i<lblCnt;i++){
				bw.write("<tr><td>"+defs.trim()+"</td>"+  
					 "<td>"+address+"</td>"+    
					 "<td>"+city+"</td><td>"+state+
					 "</td><td>"+
					 zip+"</td></tr>");
				bw.newLine();
				bw2.write("\""+defs.trim()+"\","+  
					  "\""+address+"\","+    
					  "\""+city+"\",\""+state+"\",\""+
					  zip+"\"");
				bw2.newLine();
			    }
			    for(int k=0;k<lastArr.length;k++){ 
				//
				// Two Labels each / one label now
				//
				//for(int i=0;i<2;i++){
				bw.write("<tr><td>"+
					 (firstArr[k]+" "+
					  lastArr[k]).trim()+"</td><td>"+
					 address+"</td><td>"+    
					 city+"</td><td>"+
					 state+"</td><td>"+
					 zip+"</td></tr>");
				bw.newLine();	
				bw2.write("\""+
					  (firstArr[k]+" "+
					   lastArr[k]).trim()+"\","+
					  "\""+address+"\","+    
					  "\""+city+"\",\""+
					  state+"\",\""+
					  zip+"\"");
				bw2.newLine();	
			    }
			}
			defs="";address="";city="";state="";zip="";
		    }
		    bw.write("</table></body></html>");
		    if(bw != null){
			bw.flush();
			bw.close();
			bw2.flush();
			bw2.close();
		    }
		}
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.debug(ex+" : "+query);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	    if(nct > 0){
		out.println("Data written to file successfully<br>");
		out.println("Total number of processed records: "+
			    nct+"<br>");
		out.println("The Excel file can be found at ");
		out.println("<a href=\"#\" "+
			    "onClick=\"window.open('file:///J:/"+
			    "departments/Legal/public/pro_supp.xls',"+
			    "'Pro_supp',"+
			    "'toolbar=1,location=1,"+
			    "directories=0,status=1,menubar=1,"+
			    "scrollbars=1,top=100,left=100,"+
			    "resizable=1,width=650,height=600');\">");
		out.println("J:\\departments\\Legal\\public\\pro_supp.xls</a>");
		out.println("<br> or <a href=\"#\" onClick=\"window.open("+
			    "'file:///I:/public/pro_supp.xls',"+
			    "'Pro_supp',"+
			    "'toolbar=1,location=1,"+
			    "directories=0,status=1,menubar=1,"+
			    "scrollbars=1,top=100,left=100,"+
			    "resizable=1,width=650,height=600');\">");
		out.println("I:\\public\\pro_supp.xls</a><br>");
		out.println("Now from your desktop you can run <br>"+
			    "<li> 'Pro Supp Label' merger (PSL)");
	    }
	    writeFooter(out);			
	}
	//
    }
    //
    String getPosPron(String ident){
	//
	String ret = "";
	for(int i=0;i<Inserts.identArr.length;i++){
	    if(Inserts.identArr[i].equals(ident)){
		ret = posPronArr[i];
		break;
	    }
	}
	return ret;
    }
    /**
     * repaces the & symbol with the string 'and'.
     * 
     * @param str the input string
     * @returns the modified string
     */
    String replaceAmp(String str){
	String str2 = "";
	for(int i=0;i<str.length();i++){
	    if(str.charAt(i) == '&') 
		str2 += "and";
	    else
		str2 += str.substring(i,i+1);
	}
	return str2;
    }
    void writeHeader(PrintWriter out, User user){
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
    }
    void writeFooter(PrintWriter out){
	out.println("</div>");
	out.println("</body>");
	out.println("</html>");
	out.close();
    }
}






















































