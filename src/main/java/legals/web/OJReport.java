
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
import legals.utils.*;
/**
 * generate O.J. reports
 *
 *
 */
@WebServlet(urlPatterns = {"/OJReport"})
public class OJReport extends TopServlet {

    static final long serialVersionUID = 31L;
    static Logger logger = LogManager.getLogger(OJReport.class);
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
	out.println("<h2>Outstanding Judgment (O.J.) Reports</h2>");
	out.println("</div>");
	out.println("<fieldset><legend>New Report</legend>");
	out.println("<form name=\"myForm\" method=\"post\" onsubmit=\"return validateForm();\" action=\""+url+"OJReport?\" >");
	out.println("<table width=\"90%\">");
	out.println("<tr><td align=\"center\">");
	out.println("<table width=\"70%\"><tr><td class=\"left\">");
	out.println("<ul>");
	out.println("<li> This page allows you to export data as CSV (Excel) files </li>");
	out.println("<li>You can save to your computer.</li>");
	out.println("<li>View or open the file using Excel.</li>");
	out.println("</ul>");
	out.println("</td></tr></table></td></tr>");
	out.println("<tr><td align=\"center\">");		
	out.println("<table width=\"70%\" border=\"1\">");
	out.println("<caption>Select one of the "+
		    " reports below.</caption>");		
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<caption>Report Options</caption>");
	//
	out.println("<tr><th class=\"right small\"> 1 -");
	out.println("<input type=\"radio\" name=\"report\" checked=\"checked\" value=\"oj_report\" "+
		    "/></th><td valign=\"top\" class=\"left\">O.J. Report");
	out.println("</td></tr>");
	out.println("<tr><th class=\"right small\"> 2 -");
	out.println("<input type=\"radio\" name=\"report\" value=\"oj_cr_report\" "+
		    "/></th><td valign=\"top\" class=\"left\">O.J Recalled from C.R.");
	out.println("</td></tr>");
	out.println("<tr><th>&nbsp;</th><td>&nbsp;</td></tr>");	
	out.println("</table></td></tr>");
	out.println("<tr><td>");		
	out.println("<table width=\"100%\">");
	out.println("<caption>Date Range</caption>");
	out.println("<tr><th>&nbsp;</th><th>From (mm/dd/yyyy)</th><th>To (mm/dd/yyyy)</th></tr>");
	out.println("<tr><th class=\"right\">Closed Date</th>");
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

	String date_from="",date_to="", report="";
	    
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
	if(true){
	    boolean fine = true, invalid = true, useUnion = false, single = true;
	    String fileName = "";
	    String qc = " select count(distinct d.did) ";
	    String q = " select distinct c.id ";
	    String qf = " from legal_cases c, legal_defendents d,legal_def_case l ";
	    String qw = " where ", qq = "";
	    qw += " d.did=l.did and c.id=l.id ";												
						
	    String line = "\"ID\",\"Name\",\"Co Defendant\",\"Cause #\",\"Case Type\",\"Judgment Date\",\"Fine\",\"Court Cost\",\"Total\"\n";
	    qw += " and c.status='CL' "; // Closed						
	    if(report.startsWith("oj_report")){
		qw += " and c.closed_comments like 'Outstanding Judgment'";
		fileName = "oj_report";
	    }
	    else{
		qw += " and c.closed_comments like '%from C.R.'";								
		fileName = "oj_cr_report";
	    }

	    qw += " and ((c.fine is not null and c.fine > 0) or (c.court_cost is not null and c.court_cost > 0)) ";
	    useUnion = true;
	    fileName += ".csv";
	    if(!date_from.equals("")){
		qw += " and c.closed_date >= str_to_date('"+date_from+
		    "','%m/%d/%Y') ";
	    }
	    if(!date_to.equals("")){
		qw += " and c.closed_date <= str_to_date('"+date_to+
		    "','%m/%d/%Y') ";
	    }
	    qq = qc + qf + qw;
	    // System.err.println(qq);
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
														
			    String citations = "", citeDates= "", vtypes="",
				totalFine = "", courtCost = "",courtCost2 = "",
				judgment_date="";
			    double fineBalance = 0, courtBalance = 0, balance = 0;
			    str = theCase.getJudgment_date();
			    if(str != null)
				judgment_date = str;
			    Payment pay = new Payment(debug);
			    pay.setId(id);
			    str = pay.compute();
			    fineBalance = pay.getFineBalance(); // totalFine
			    courtBalance = pay.getCourtBalance(); // included in the balance
			    balance = pay.getTotalBalance();
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
			    //
			    //
			    // 
			    Defendant mainDef = defs.get(0);
			    //
			    // for multiple defendants we need only their names
			    //
			    String coDefNames = "";
			    if(defs.size() > 1){
				for(int i=1;i<defs.size();i++){
				    Defendant deff = defs.get(i);
				    if(!coDefNames.equals("")) coDefNames += ", "; 
				    coDefNames += deff.getFullName();
				}
			    }
			    // 
			    rt.set(1,mainDef.getFullName());
			    rt.set(2,coDefNames);
			    rt.setFullName(mainDef.getFullName());
			    str = mainDef.getCauseNumForCase(id);
			    rt.set(3,str);
			    float total = 0f, fines=0f, court=0f;
			    if(caseViols != null && caseViols.size() > 0){
				for(CaseViolation vg:caseViols){
				    ViolCat vcat = vg.getViolCat();
				    if(vcat != null){
					str = vcat.getCategory();
					if(!str.equals("")){
					    vtypes = str;
					}
				    }
				}
			    }
			    rt.set(4, vtypes);
			    rt.set(5, judgment_date);
			    rt.set(7,""+fineBalance);														
			    rt.set(8,""+courtBalance);
			    rt.set(9,""+balance);
			    str = "";
			    records.add(rt);
			}
		    }
		    if(records.size() > 0){
			Collections.sort(records);						
			for(Record rr: records){
			    line = "";
			    for(int i=0;i<6;i++){
				line += "\""+rr.get(i)+"\",";
			    }
			    line += "\""+rr.get(7)+"\",";
			    line += "\""+rr.get(8)+"\",";
			    line += "\""+rr.get(9)+"\"\n";
			    buf.append(line);
			}
		    }
		    // the contentlength is needed for MSIE
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






















































