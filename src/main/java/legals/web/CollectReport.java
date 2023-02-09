
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
 *
 */
@WebServlet(urlPatterns = {"/CollectReport"})
public class CollectReport extends TopServlet {

    static final long serialVersionUID = 31L;
    static Logger logger = LogManager.getLogger(CollectReport.class);
    String pc_path = "j:\\departments\\legal\\public\\test\\";

    //
    // pronouns and their possessive pronouns 
    //
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
	String name, value;
	String action="";
	String page = "";
	String cur_path="";
	boolean success = true;
	int resolved_month = 0;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("page")) {
		page = value;
	    }	
	}
	if(!page.equals("")){
	    doPost(req, res);
	    return;
	}
	PrintWriter out = res.getWriter();
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
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");		
	//
	out.println("  function validateForm(){		         ");
	out.println("   with(document.myForm){                   ");
	out.println("   if (date_from.value.length > 0){         ");
	out.println("  if(!checkDate(date_from.value)){       ");
	out.println("     alert(\"Invalid date \"+date_from.value); ");
	out.println("  date_from.focus();                           ");
	out.println("     return false;			            ");
	out.println("	}}                                          ");
	out.println("  	 if (date_to.value.length > 0){             ");
	out.println("  if(!checkDate(date_to.value)){            ");
	out.println("     alert(\"Invalid date \"+date_to.value);   ");
	out.println("  date_to.focus();                             ");
	out.println("     return false;			            ");
	out.println("	}}                                          ");
	out.println("	}                                           ");
	out.println("     return true;				    ");
	out.println("	}	         			    ");
	out.println(" //]]>   ");		
	out.println(" </script>				                 ");
	//
	out.println("<div class=\"center\">");
	out.println("<h2>Collection Reports</h2>");
	out.println("</div>");
	out.println("<fieldset><legend>New Report</legend>");
	out.println("<form name=\"myForm\" method=\"post\" onsubmit=\"return validateForm();\" action=\""+url+"CollectReport?\" >");
	out.println("<table width=\"90%\">");
	out.println("<tr><td align=\"center\">");
	out.println("<table width=\"70%\"><tr><td class=\"left\">");
	out.println("<ul>");
	out.println("<li> This page allows you to export data as CSV (Excel) files </li>");
	out.println("<li>You can save to your computer.</li>");
	out.println("<li>View or open the file using Excel.</li>");
	out.println("<li>ftp the file to collection agency.</li>");
	out.println("</ul>");
	out.println("</td></tr></table></td></tr>");
	out.println("<tr><td align=\"center\">");		
	out.println("<table width=\"70%\" border=\"1\">");
	out.println("<caption>Select one of the "+
		    " reports below.</caption>");		
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<caption>To Collection Court Reports</caption>");
	//
	out.println("<tr><th class=\"right small\"> 1 -");
	out.println("<input type=\"radio\" name=\"report\" checked=\"checked\" value=\"cc_singleValid\" "+
		    "/></th><td valign=\"top\" class=\"left\">Single Defenant w/Valid Addresses");
	out.println("</td></tr>");
	out.println("<tr><th class=\"right small\"> 2 -");
	out.println("<input type=\"radio\" name=\"report\" value=\"cc_singleInvalid\" "+
		    "/></th><td valign=\"top\" class=\"left\">Single Defendant w/Invalid Addresses");
	out.println("</td></tr>");
	out.println("<tr><th class=\"right small\"> 3 -");
	out.println("<input type=\"radio\" name=\"report\" value=\"cc_multipleValid\" "+
		    "/></th><td valign=\"top\" class=\"left\">Multiple Defendants w/Valid Addresses");
	out.println("</td></tr>");

	out.println("<tr><th>&nbsp;</th><td>&nbsp;</td></tr>");	
	out.println("</table></td></tr>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");		
	out.println("<caption>To Collection Reports</caption>");
	out.println("<tr><th class=\"right small\"> 4 -");
	out.println("<input type=\"radio\" name=\"report\" value=\"singleValid\" "+
		    " /></th><td valign=\"top\" class=\"left\">Single Defendant w/Valid Addresses");
	out.println("</td></tr>");
	out.println("<tr><th class=\"right small\"> 5 -");
	out.println("<input type=\"radio\" name=\"report\" value=\"singleInvalid\" "+
		    " /></th><td valign=\"top\" class=\"left\">Single Defendant w/Invalid Addresses");
	out.println("</td></tr>");
	out.println("<tr><th class=\"right small\"> 6 -");
	out.println("<input type=\"radio\" name=\"report\" value=\"multipleValid\" "+
		    " /></th><td valign=\"top\" class=\"left\">Multiple Defendants w/Valid Addresses");
	out.println("</td></tr>");
	out.println("<tr><th class=\"right small\"> 7 -");
	out.println("<input type=\"radio\" name=\"report\" value=\"collection\" "+
		    " /></th><td valign=\"top\" class=\"left\">Collection Recall Report");
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td>");		
	out.println("<table width=\"100%\">");
	out.println("<caption>Date Range</caption>");
	out.println("<tr><th>&nbsp;</th><th>From (mm/dd/yyyy)</th><th>To (mm/dd/yyyy)</th></tr>");
	out.println("<tr><th class=\"right\">Trans to Collect Date</th>");
	out.println("<td><input name=\"date_from\" class=\"date\" value=\"\" size=\"10\" maxlength=\"10\" /></td>");
	out.println("<td><input name=\"date_to\" class=\"date\" value=\"\" size=\"10\" maxlength=\"10\" />");		
	out.println("</td></tr>");			
	out.println("</table></td></tr>");		
	//
	out.println("<tr><td colspan=\"2\" align=\"right\"><input type=\"submit\" " +
		    "value=\"Submit\" /></td></tr>");
	out.println("</table>");
	out.println("</td></tr>");			
	out.println("</table>");		
	// 
	out.println("</form><br />");
	out.println("</fieldset>");
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
		
	out.println("<script type=\"text/javascript\" src=\""+url+"js/jquery-1.7.2.min.js\"></script>");
	out.println("<script type=\"text/javascript\" src=\""+url+"js/jquery-ui-1.8.18.custom.min.js\"></script>");
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
     * Processes the request and generates the data and write them to a file.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null, rs2=null, rs3=null;

	String date_from="",date_to="", report="", page="1";
	    
	String name, value, message="", urlStr = "";
	User user = null;
	boolean success = true;
	BufferedWriter bw=null,bw2=null;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("date_from")) {
		date_from = value;
	    }
	    else if (name.equals("date_to")) {
		date_to = value;
	    }
	    else if (name.equals("report")) {
		report = value;
	    }
	    else if (name.equals("page")) {
		page = value;
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
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
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
	if(report.equals("collection")){
	    // collection option 7
	    //
	    /*
	      select c.id,                                                                      concat(dd.l_name,' ',dd.f_name) full_name,                                      dc.cause_num cause_num,                                                         concat_ws(' ',da.street_num,da.street_dir,da.street_name,da.street_type,da.post_dir,da.sud_type,da.sud_num) Defendant_address,                                  lv.citations,lv.amount,lv.dates,vs.complaint                                    from legal_cases c                                                              left join legal_addresses ad on ad.caseId=c.id                                  left join legal_def_case dc on dc.id=c.id                                       left join legal_defendents dd on dd.did=dc.did                                  left join legal_def_addresses da on da.defId=dd.did                             left join legal_case_violations lv on lv.id=c.id                                left join legal_viol_subcats vs on vs.sid=lv.sid                                where c.status = 'CL' and c.closed_comments like '%41%'                         and c.closed_date >= str_to_date('11/01/2019','%m/%d/%Y') limit 5
							
	    */
	    urlStr = "CollectReport?report="+report+"&page=2";		
	    String fileName = "Collection_recall.csv";
	    String qq =  " select c.id,"+
		" concat(dd.l_name,' ',dd.f_name) full_name,"+
		" concat_ws(' ',da.street_num,da.street_dir,da.street_name,da.street_type,da.post_dir,da.sud_type,da.sud_num) address,"+  
		"lv.citations,lv.amount,lv.dates,vs.complaint, "+ //7
		"c.citation_num,c.fine,lt.typeDesc ";
						
	    String qf = " from legal_cases c "+
		"join legal_case_types lt on lt.typeId=c.case_type "+
		"join legal_def_case dc on dc.id=c.id "+
		"join legal_defendents dd on dd.did=dc.did "+
		"left join legal_def_addresses da on da.defId=dd.did "+
		"left join legal_case_violations lv on lv.id=c.id  "+
		"left join legal_viol_subcats vs on vs.sid=lv.sid ";
	    String qw = " where c.status = 'CL' and c.closed_comments like '%41%'";

	    String line = "\"ID\",\"Defendant\",\"Address\",\"Citations\",\"Fines\",\"Citation Dates\",\"Violation\"\n";
	    if(!date_from.equals("")){
		urlStr += "&date_from="+date_from;
		qw += " and c.closed_date >= str_to_date('"+date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!date_to.equals("")){
		urlStr += "&date_to="+date_to;	
		qw += " and c.closed_date <= str_to_date('"+date_to+
		    "','%m/%d/%Y') ";
	    }
	    qq += qf + qw;
	    if(debug){
		logger.debug(qq);
	    }
	    int count = 0;
	    try{
		rs = stmt.executeQuery(qq);
		rs.last();
		count = rs.getRow();
		if(count == 0){
		    //
		    // no match found
		    //
		    res.setContentType("text/html");
		    PrintWriter out = res.getWriter();			  
		    writeHeader(out, user);
		    out.println("<div id=\"mainContent\">");					
		    out.println("<h3>No records matched </h3>");
		    writeFooter(out);
		}
		else{
		    if(page.equals("1")){
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			String bodyStr = "onload=\"redirect();\" ";
			String jsStr = " function redirect(){ \n";
			jsStr += " window.location='"+urlStr+"'; \n";
			jsStr += " } \n";
			writeHeader2(out, user, bodyStr, jsStr);
			out.println("<div id=\"mainContent\">");
			out.println("<h2>Collection Recall Report </h2>");
			out.println("<ul><li>A csv (Excel) file will be generated. </li><li>The browser will ask you to Open or Save. </li><li>Please choose Save to save it to your computer.</li> <li>It may take some time, please wait. </li>");
			String info = "";
			info = "This is a collection recall report ";
			writeFooter(out);
			return;
		    }
		    //
		    res.setHeader("Expires", "0");
		    res.setHeader("Cache-Control", 
				  "must-revalidate, post-check=0, pre-check=0");
		    res.setHeader("Content-Disposition","attachment; filename="+fileName);
		    res.setHeader("Pragma", "public");
		    //
		    // setting the content type
		    res.setContentType("application/csv");
		    // header row					
		    StringBuffer buf = new StringBuffer(line);					
		    String str= "";
		    List<Record> records = new ArrayList<Record>();
		    rs.beforeFirst();
		    String addr="",cite_num="",fine="", type="";
		    while(rs.next()){
			addr ="";cite_num="";fine="";
			Record rt = new Record(7,debug);
			str = rs.getString(3);
			if(str != null)
			    addr = str;
			str = rs.getString(8);
			if(str != null)
			    cite_num = str;
			str = rs.getString(9);
			if(str != null)
			    fine = str;
			str = rs.getString(10);
			if(str != null)
			    type = str;
			for(int jj=0;jj<3;jj++){
			    str = rs.getString(jj+1);
			    if(str == null) str = "";
			    rt.set(jj,str);
			}
			str = rs.getString(4);
			if(str == null) str = cite_num;
			rt.set(3, str);
			str = rs.getString(5);
			if(str == null) str = fine;
			rt.set(4, str);
			str = rs.getString(6);
			if(str == null) str = "";
			rt.set(5, str);														
			str = rs.getString(7);
			if(str == null) str = type;
			if(!addr.equals("") && str.indexOf("_address_") > -1){
			    str = str.replaceAll("_address_", addr);
			}
			rt.set(6, str);		
			records.add(rt);
		    }
		    if(records.size() > 0){
			// Collections.sort(records);						
			for(Record rr: records){
			    line = "";
			    for(int i=0;i<rr.getSize();i++){
				if(i > 0) line += ",";
				line += "\""+rr.get(i)+"\"";
			    }
			    line += "\n";
			    buf.append(line);
			}
			// the contentlength is needed for MSIE!!!
			res.setContentLength(buf.length());
			ServletOutputStream out2 = res.getOutputStream();
			out2.print(buf.toString());
			out2.flush();
			out2.close();
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	else{
	    urlStr = "CollectReport?report="+report+"&page=2";			
	    boolean fine = true, invalid = true, useUnion = false, single = true;
	    String fileName = "";
	    String qc = " select count(distinct d.did) ";
	    String q =  " select distinct c.id ";
	    String qf = " from legal_cases c ";
	    String qw = " where ", qq = "";

	    String line = "\"ID\",\"Name\",\"Co Defendant\",\"Cause #\",\"Type\",\"Citation #\",\"Citation Date\",";
	    if(report.startsWith("cc_")){
		qw += " c.status='CC' "; // to collection court // heather
		fileName = "cc";
	    }
	    else{
		qw += " c.status='TC' "; // to collection // patty
	    }
	    if(!report.contains("single")){
		single = false;
	    }
	    fileName += "Fines";
	    qw += "  and ((c.fine is not null and c.fine > 0) or (c.court_cost is not null and c.court_cost > 0)) ";
	    line += "\"Fine\",\"Court Cost\",";
	    useUnion = true;
	    //
	    if(report.endsWith("Invalid")){
		line += "\"Invalid Address\",";
		fileName += "InvalidAddresses";
		qf += ", legal_defendents d,legal_def_case l,legal_def_addresses da ";
		qw += " and d.did=l.did and c.id=l.id and d.did=da.defId ";
		qw += " and da.invalid_addr is not null ";
	    }
	    else{
		line += "\"Valid Address\",";
		invalid = false;
		fileName += "ValidAddresses";				
		qf += ", legal_defendents d,legal_def_case l,legal_def_addresses da ";
		qw += " and d.did=l.did and c.id=l.id and d.did=da.defId ";
		qw += " and da.invalid_addr is null ";
	    }
	    line += "\"Phone #\",\"Email\",\"dob\",\"dln\",\"ssn\",\"Animal Name\",\"Judgment Date\",\"Violation Address\"\n";
	    fileName += ".csv";
	    if(!date_from.equals("")){
		urlStr += "&date_from="+date_from;
		qw += " and c.trans_collect_date >= str_to_date('"+date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!date_to.equals("")){
		urlStr += "&date_to="+date_to;	
		qw += " and c.trans_collect_date <= str_to_date('"+date_to+
		    "','%m/%d/%Y') ";
	    }
	    qq = qc + qf + qw;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		int count = 0;				
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    count = rs.getInt(1);
		}
		if(count == 0){
		    //
		    // no match found
		    //
		    res.setContentType("text/html");
		    PrintWriter out = res.getWriter();			  
		    writeHeader(out, user);
		    out.println("<div id=\"mainContent\">");					
		    out.println("<h3>No records matched </h3>");
		    writeFooter(out);
		}
		else{
		    if(page.equals("1")){
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			String bodyStr = "onload=\"redirect();\" ";
			String jsStr = " function redirect(){ \n";
			jsStr += " window.location='"+urlStr+"'; \n";
			jsStr += " } \n";
			writeHeader2(out, user, bodyStr, jsStr);
			out.println("<div id=\"mainContent\">");
			out.println("<h2>Transfer to Collection Report </h2>");
			out.println("<ul><li>A csv (Excel) file will be generated. </li><li>The browser will ask you to Open or Save. </li><li>Please choose Save to save it to your computer.</li> <li>It may take some time, please wait. </li>");
			String info = "";
			info = "This is a fine and court costs report for ";
			if(single)
			    info += "single defendant cases ";
			else
			    info += "multiple defendants cases ";
			if(invalid)
			    info += " with invalid addresses. ";
			else
			    info += " with valid addresses. ";
			out.println("<li>"+info+"</li></ul>");
			writeFooter(out);
			return;
		    }
		    //
		    res.setHeader("Expires", "0");
		    res.setHeader("Cache-Control", 
				  "must-revalidate, post-check=0, pre-check=0");
		    res.setHeader("Content-Disposition","attachment; filename="+fileName);
		    res.setHeader("Pragma", "public");
		    //
		    // setting the content type
		    res.setContentType("application/csv");
		    // header row					
		    StringBuffer buf = new StringBuffer(line);					
		    String line2 = "";
		    qq = q + qf + qw;
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    Set<String> inSet = new HashSet<String>();
		    Case theCase = null;
		    String str= "", str2="";
		    List<Defendant> defs = null;
		    List<DefAddress> defAddrs = null;
		    List<Address> addrs = null;
		    List<CaseViolation> caseViols = null;
		    List<Record> records = new ArrayList<Record>();
		    int jj = 1;
		    while(rs.next()){
			String address = "", 
			    id = rs.getString(1);
			if(!inSet.contains(id)){
			    inSet.add(id);
			    theCase = new Case(debug, id);
			    str = theCase.doSelect();
			    if(!str.equals("")){
				logger.error(str+" ERROR case id "+id);
				continue;
			    }
			    if(single){
				if(!theCase.hasSingleDef()) continue;
			    }
			    else{
				if(!theCase.hasMultipleDef()) continue;
			    }
			    String citations = "", citeDates= "", vtypes="",
				totalFine = "", courtCost = "",courtCost2 = "";
			    str = theCase.getCitation_num();
			    if(!str.equals("")){
				citations = str;
			    }
			    str = theCase.getCitation_date();
			    if(!str.equals("")){
				citeDates = str;
			    }
			    Payment pay = new Payment(debug);
			    pay.setId(id);
			    str = pay.compute();
			    str = pay.getFine(); // totalFine
			    if(!str.equals("")){
				totalFine = str;
			    }
			    str = theCase.getCourt_cost();
			    if(!str.equals("")){
				courtCost = str;
			    }	
			    caseViols = theCase.getCaseViolations();
			    if(vtypes.equals("")){
				CaseType type = theCase.getCaseType();
				vtypes = ""+type;
			    }
			    defs = theCase.getDefendants();
			    //
			    // skip cases with no defendants
			    //
			    if(defs == null || defs.size() == 0) continue;
			    str = "";
			    Record rt = new Record(18,debug);
			    rt.set(0,id);
			    List<Animal> pets = theCase.getPets();
			    if(pets != null && pets.size() > 0){
				for(Animal pet:pets){
				    if(!str.equals("")) str += " ";
				    str += pet;
				}
			    }
			    rt.set(15,str); 
			    //
			    str = theCase.getJudgment_date();
			    rt.set(16,str); 
			    //
			    addrs = theCase.getAddresses();
			    Address addr = null;
			    if(addrs != null && addrs.size() > 0){
				addr = addrs.get(0);
			    }
			    str = "";
			    if(addr != null){
				str = addr.getFullAddress();
			    }
			    rt.set(17,str);			
			    // 
			    Defendant mainDef = defs.get(0);
			    //
			    // for multiple defendants we need only their names
			    //
			    String coDefNames = "";
			    if(!single){
				if(defs.size() > 1){
				    for(int i=1;i<defs.size();i++){
					Defendant deff = defs.get(i);
					if(!coDefNames.equals("")) coDefNames += ", "; 
					coDefNames += deff.getFullName();
				    }
				}
			    }
			    // 
			    DefAddress defAddr = null;
			    if(invalid){
				// we do not include defs who
				// has valid address
				// 
				if(mainDef.hasValidAddress()) continue;
				defAddr = mainDef.getInvalidAddress();
			    }
			    else{
				// not include who do not have valid
				if(!mainDef.hasValidAddress()) continue;
				defAddr = mainDef.getValidAddress();
			    }
			    if(defAddr == null) continue;
			    String defFullAddr = defAddr.getFullAddress();
			    //
			    rt.set(1,mainDef.getFullName());
			    rt.set(2,coDefNames);
			    rt.setFullName(mainDef.getFullName());
			    str = mainDef.getCauseNumForCase(id);
			    rt.set(3,str);
			    str = mainDef.getPhones();
			    rt.set(10,str);
			    str = mainDef.getEmail();
			    rt.set(11,str);
			    str = mainDef.getDob();
			    rt.set(12,str);
			    str = mainDef.getDln();
			    rt.set(13,str);
			    str = mainDef.getSsn();
			    rt.set(14,str);
			    courtCost2 = courtCost;
			    if(caseViols != null && caseViols.size() > 0){
				for(CaseViolation vg:caseViols){
				    str = vg.getCitations();
				    if(!str.equals("")){
					citations = str;
				    }
				    str = vg.getDates();
				    if(!str.equals("")){
					citeDates = str;
				    }
				    str = vg.getAmount();
				    if(!str.equals("")){
					totalFine = str;
				    }	
				    ViolCat vcat = vg.getViolCat();
				    if(vcat != null){
					str = vcat.getCategory();
					if(!str.equals("")){
					    vtypes = str;
					}
				    }
				    Record rr2 = new Record(rt.getSize(),debug);
				    rr2.copyFrom(rt);
				    rr2.set(4,vtypes);
				    rr2.set(5,citations);
				    rr2.set(6,citeDates);
				    rr2.set(7,totalFine);
				    rr2.set(8,courtCost2);
				    courtCost2="0";
				    str = "";
				    str = defFullAddr;
				    rr2.set(9,str);
				    defFullAddr = "";
				    records.add(rr2);
				}
			    }
			    else{ // get the info from the case
				rt.set(4,vtypes);
				rt.set(5,citations);
				rt.set(6,citeDates);
				rt.set(7,totalFine);
				rt.set(8,courtCost);
				str = "";
				str = defFullAddr;
				rt.set(9,str);
				records.add(rt);
			    }
			}
		    }
		    if(records.size() > 0){
			Collections.sort(records);						
			for(Record rr: records){
			    line = "";
			    for(int i=0;i<rr.getSize();i++){
				if(i > 0) line += ",";
				line += "\""+rr.get(i)+"\"";
			    }
			    line += "\n";
			    buf.append(line);
			}
		    }
		    // the contentlength is needed for MSIE!!!
		    res.setContentLength(buf.length());
		    ServletOutputStream out2 = res.getOutputStream();
		    out2.print(buf.toString());
		    out2.flush();
		    out2.close();
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
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
    void writeHeader2(PrintWriter out, User user, String bodyStr, String jsStr){
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner2(url, bodyStr, jsStr));
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






















































