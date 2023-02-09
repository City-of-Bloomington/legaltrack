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

@WebServlet(urlPatterns = {"/ViolCats"})
public class ViolCatsServ extends TopServlet{

    static final long serialVersionUID = 78L;
    static Logger logger = LogManager.getLogger(ViolCatsServ.class);
    final static String identArr[] = {"","He","She","They","It"};

    String[] catsIdArr = null;
    String[] catsArr = null;
    //
    // New subcategory list
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
    
	Connection con = null, con2=null;
	Statement stmt = null, stmt2=null;
	ResultSet rs = null;
	String category = "", subcat="", codes="", action2="",
	    cid="",sid="",amount="",complaint="";
	// 
	boolean connectDbOk = false, success = true;
	String message="", action="";
	String subcat2="",codes2="",amount2="",complaint2="";
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	Enumeration<String> values = req.getParameterNames();

	String[] subcatIdArr = null;
	String[] subcatArr = null;
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"/Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"/Login?";
	    res.sendRedirect(str);
	    return; 
	}
		
	String [] vals;
	String [] defList = null;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    value = Helper.replaceSpecialChars(value);
	    //
	    if (name.equals("codes")) {
		codes = value;
	    }
	    else if (name.equals("amount")) {
		amount = value;
	    }
	    else if (name.equals("cid")){ // category id
		cid = value;
	    }
	    else if (name.equals("category")){ // category id
		category = value;
	    }
	    else if (name.equals("subcat")){ 
		subcat = value;
	    }
	    else if (name.equals("codes")){ 
		codes = value;
	    }
	    else if (name.equals("complaint")){ 
		complaint = value;
	    }
	    else if (name.equals("codes")){ 
		codes = value;
	    }
	    else if (name.equals("amount")){ 
		amount = value;
	    }
	    else if (name.equals("sid")){ // subcat id
		sid = value;
	    }
	    else if (name.equals("action")){ 
		// Get, Save, zoom, edit, delete, New, Refresh
		action = value;  
	    }
	    else if (name.equals("action2")){ 
		// Next
		action2 = value;  
	    }
	}
	if(!action2.equals("")) action = action2;
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
		if(catsArr == null){
		    message += resetArrays(stmt,rs);
		}
	    }
	    else{
		message += " Could not connect to DB ";
		success = false;
		logger.error(message);
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    message += ex;
	    success = false;
	}
	//
	Calendar current_cal = Calendar.getInstance();
        String today = 
	    (current_cal.get(Calendar.MONTH)+1)+"/"+ 
	    current_cal.get(Calendar.DATE) + 
	    "/" + current_cal.get(Calendar.YEAR);
	//
	if(action.startsWith("Switch")){
	    //
	    // start with new selection of categories
	    sid=""; cid="";
	    System.err.println("In switch");
	}
	if(true){
	    //
	    String qq = "", str="", str2="";
	    try{
		if(cid.equals("") && !sid.equals("")){
		    qq = "select cid "+
			" from legal_viol_subcats where sid= "+sid;
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    if(rs.next()){
			cid = rs.getString(1);
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
		message += ex;
		success = false;
	    }
	}
	if(!sid.equals("")){
	    //
	    String qq = "", str="", str2="";
	    try{
		qq = "select subcat,complaint,codes,amount "+
		    " from legal_viol_subcats where sid= "+sid;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		while(rs.next()){
		    str = rs.getString(1);
		    if(str != null) subcat2 = str;
		    str = rs.getString(2);
		    if(str != null) complaint2 = str;
		    str = rs.getString(3);
		    if(str != null) codes2 = str;
		    str = rs.getString(4);
		    if(str != null) amount2 = str;
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
		message += ex;
		success = false;
	    }
	}
	if(action.startsWith("Add as New Category")){
	    //
	    String qq ="";			
	    try{
		qq = " select (max(cid)+1) from legal_viol_cats ";
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    cid = rs.getString(1); // we need this
		}
		qq = " insert into legal_viol_cats values("+cid+",'"+
		    Helper.escapeIt(category)+"')";
		if(debug){
		    System.err.println(qq);
		}
		stmt.executeUpdate(qq);
		//
		// As the list changed we need to reset it
		//
		message += resetArrays(stmt,rs);
		subcatArr = null;
		subcatIdArr = null;
	    }
	    catch(Exception ex){
		logger.error(ex+" : "+qq);
		message += ex;
		success = false;
	    }
	}
	else if(action.startsWith("Add as New Sub")){
	    //
	    String qq ="";			
	    try{
		qq = " select (max(sid)+1) from legal_viol_subcats ";
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    sid = rs.getString(1); // We need this since it is a key
		}
		qq = "insert into legal_viol_subcats values ("+sid+","+
		    cid+",";
		if(subcat.equals(""))
		    qq += "null,";
		else
		    qq += "'"+Helper.escapeIt(subcat)+"',";
		if(complaint.equals(""))
		    qq += "null,";
		else
		    qq += "'"+Helper.escapeIt(complaint)+"',";
		if(codes.equals(""))
		    qq += "null,";
		else
		    qq += "'"+Helper.escapeIt(codes)+"',";
		if(amount.equals("")){
		    qq += "0";
		    amount="0";
		}
		else {
		    qq += ""+amount+"";
		}
		qq += ")";
		if(debug){
		    System.err.println(qq);
		}
		stmt.executeUpdate(qq);
		message += "Saved successfully";
	    }
	    catch(Exception ex){
		success = false;
		logger.error(ex+":"+qq);
		message += ex;
		message += "Error in Saving";
	    }
	}
        else if(action.equals("Update") && user.canEdit()){
	    //
	    String str="";
	    String qq = "";
	    qq = "update legal_viol_subcats set ";
	    if(subcat.equals(""))
		qq += "subcat=null,";
	    else
		qq += "subcat='"+Helper.escapeIt(subcat)+"',";
	    if(codes.equals(""))
		qq += "codes=null,";
	    else
		qq += "codes='"+Helper.escapeIt(codes)+"',";

	    if(amount.equals("")){
		qq += "amount=0,";
		amount = "0";
	    }
	    else
		qq += "amount="+amount+",";
	    if(complaint.equals(""))
		qq += "complaint=null";
	    else
		qq += "complaint='"+Helper.escapeIt(complaint)+"'";

	    qq += " where sid="+sid;
	    //
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		stmt.executeUpdate(qq);
		message += "Updated successfully";
	    }
	    catch(Exception ex){
		logger.error(ex+" : "+qq);
		success = false;
		message += ex;
		message += " Error Updating";
	    }
	}
	else if(action.equals("Delete") && user.canEdit()){
	    //
	    String qq = "delete from legal_viol_subcats where sid= "+sid;

	    if(debug){
		logger.debug(qq);
	    }
	    try{
		stmt.executeUpdate(qq);
		message += "Deleted successfully";
	    }
	    catch(Exception ex){
		logger.error(ex+" : "+qq);
		success = false;
		message += ex;
		message += "Error deleting";
	    }
	    amount="";codes="";sid="";subcat="";complaint="";
	}
	else if(action.equals("zoom") || 
		(action.equals("Next") && !sid.equals(""))){
	    //
	    String qq = "select "+
		"subcat,amount,codes,complaint,cid "+
		" from legal_viol_subcats "+
		" where sid="+sid;
	    String str="";
	    try{
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    if(str != null) subcat = str;
		    str = rs.getString(2);
		    if(str != null) amount = str;
		    str = rs.getString(3);
		    if(str != null) codes = str;
		    str = rs.getString(4);  
		    if(str != null) complaint = str; 
		    str = rs.getString(5);  
		    if(str != null) cid = str; 
		}
	    }
	    catch(Exception ex){
		success = false;
		logger.error(ex+":"+qq);
		message += ex;
		message += " Error retreiving data";
	    }
	}
	if(true){
	    String qq = "";
	    try{
		if(!(cid.equals("") || cid.equals("0"))){
		    // 
		    String str="",str2="";
		    qq = "select count(sid) "+
			" from legal_viol_subcats where cid= "+cid;
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    if(rs.next()){
			int cnt = rs.getInt(1);
			if(cnt > 0){
			    subcatIdArr = new String[cnt];
			    subcatArr = new String[cnt];
			    for(int i=0;i<cnt;i++){
				subcatIdArr[i] = "";
				subcatArr[i] = "";
			    }
			}
		    }
		    int i=0;
		    qq = "select sid,subcat "+
			" from legal_viol_subcats where cid= "+cid+
			" order by subcat";
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    while(rs.next()){
			str = rs.getString(1);
			if(str == null) str = ""; 
			str2 = rs.getString(2);
			if(str2 == null) str2 = "";
			if(!str.equals("")){
			    subcatIdArr[i] = str;
			    subcatArr[i] = str2;
			    i++;
			}
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
		message += ex;
		success = false;
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	//
	if(action.equals("") || 
	   action.equals("Next") ||
	   action.equals("Delete")){

	}
	else{
	    out.println("   function formatNumber(xx){            ");
	    out.println("     var  x = new String(xx);            "); 
	    out.println("     var  n = indexOf(x,\".\");          ");
	    //out.println("       alert(\" x idex \"+x+\" \"+n);  ");
	    out.println("      if(n > -1){                        ");
	    out.println("       var  l = x.length;                ");
	    out.println("       var  r = n*1+3*1;                 ");
	    out.println("        if( r < l ){                     ");
	    out.println("        var y = x.substring(0,r);        ");
	    //out.println("       alert(\" x idex \"+x+\" \"+y);  ");
	    out.println("        return y;                        ");
	    out.println("          }                              ");
	    out.println("        }                                ");
	    out.println("    return x;                            ");
	    out.println("     }                                   ");
	}
	//
	out.println("  function runNext(){                               ");
	out.println("   document.myForm.action2.value = \"Next\";        ");
	out.println("     document.myForm.submit();                      ");
	out.println("	}			       		         ");
	out.println(" </script>				                 ");


    	out.println("<center><h2>Violation Category Editor</h2>          ");
	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");   
	}
	else{
	    if(!message.equals(""))
		out.println("<h3><font color='red'>"+message+"</font></h3>"); 
	}
	//
	if(action.startsWith("Next")){
	    out.println("<h3>Continue with This Category</h3>");
	}
	
	//
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	//
	out.println("<center><table border><tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table width=100%>");
	out.println("<tr><td align=center colspan=2 "+
		    "style=\"background-color:navy; color:white\">"+
		    "<b>Violation</b>"+
		    "</td></tr>");
	out.println("<input type=hidden name=action2 value=\"\">");
	//
	out.println("<tr><td colspan=2 align=center>"+
		    "<b>Violation Main Category </b></td></tr>");
	int seq = 1;
	if(cid.equals("")){
	    out.println("<tr><td colspan=2><font color=green size=-1>"+
			"Select a category from the list below "+
			" and click on 'Next'<br></td></tr>");
	    out.println("<tr><td align=right><b>Category:</b></td><td align=left>");
	    out.println("<select name=cid tabindex="+seq+" onChange=\"runNext()\">");
	    seq++;
	    for(int i=0;i<catsIdArr.length;i++){
		out.println("<option value=\""+catsIdArr[i]+"\">"+catsArr[i]);
	    }
	    out.println("</xelect>");
	    out.println("</td></tr>");
	    // 
	    // A work arround to fix an error 
	    out.println("<input type=hidden name=sid value=\" \">");
	    out.println("<tr><td colspan=2 align=right>");
	    out.println("<input tabindex="+seq+
			" accesskey=\"N\""+
			" type=submit name=action value=Next></td></tr>");
	    seq++;
	    //
	    out.println("<tr><td colspan=2><font color=green size=-1>"+
			"If you want to add a new category enter its title "+
			"in the field below and click on 'Add New Category'"+
			"</td></tr>");
	    out.println("<tr><td align=right><b>New Category: </b></td>");
	    out.println("<td align=left><input name=category tabindex="+seq+
			" size=40 maxlength=50>"+
			"</td></tr>");
	    seq++;
	    out.println("<tr><td colspan=2 align=right>");
	    out.println("<input tabindex="+seq+" accesskey=\"A\""+
			" type=submit name=action value=\"Add as New Category\">");
	    seq++;
	    out.println("</td></tr>");
	    out.println("<tr><td colspan=2 align=center>");
	    out.println("<font color=green size=-1>Access keys "+
			"are: Alt + N=Next, A=Add "+
			"</font></td></tr>");
	    out.println("</table></td></tr>"); // End Category table
	    out.println("</form>");
	}
	else{  // !cid.equals("")
	    //
	    out.println("<tr><td align=center><b>Category:</b>");
	    for(int i=0;i<catsIdArr.length;i++){
		if(catsIdArr[i].equals(cid))
		    out.println(catsArr[i]);
	    }
	    out.println("</td></tr>");
	    out.println("<input type=hidden name=cid value="+cid+">");
	    out.println("<tr><td align=right><input type=submit name=action "+
			"tabindex="+seq+
			" accesskey=S "+
			" value=\"Switch to Another Category\">");
	    out.println("</td></tr>");
	    seq++;
	    out.println("</table></td></tr>"); // End Category table
	    //
	    // Sub category
	    //
	    out.println("<tr><td bgcolor="+Helper.bgcolor+"><table width=100%>");
	    out.println("<tr><td align=center "+
			" style=\"background-color:navy; color:white\">"+
			"<b>Violation Subcategory</b>"+
			"</td></tr>");
	    out.println("<tr><td><font color=green size=-1>"+
			"Select a subcategory from the list below to view "+
			"the details, if you need to do any changes, make "+
			"sure to click on the 'Update' button<br>"+
			"If you want to add a new subcategory  "+
			"enter the details below and then click on "+
			"'Add as New Subcategory' "+
			"</font></td></tr>");
	    //
	    out.println("<tr><td><b>Subcategory: </b>");
	    out.println("<select name=sid tabindex="+seq+
			" onChange=\"runNext()\">");
	    out.println("<option value=''>Pick One</option>");
	    if(subcatArr  != null){
		for(int i=0;i<subcatIdArr.length;i++){
		    if(subcatIdArr[i].equals(sid)){
			out.println("<option selected value=\""+
				    subcatIdArr[i]+"\">"+subcatArr[i]);
			subcat = subcatArr[i];
		    }
		    else
			out.println("<option value=\""+subcatIdArr[i]+"\">"+
				    subcatArr[i]);
		}
	    }
	    out.println("</select>");
	    seq++;
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Subcategory Text: </b></td></tr> ");
	    out.println("<tr><td>");
	    out.println("<textarea name=subcat tabindex="+seq+
			" rows=3 cols=70 wrap>");
	    out.println(subcat);
	    out.println("</textarea>");
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Complaint: </b><br> ");
	    out.println("<font color=green size=-1>Notes:<br>"+
			"<li> For rental/violation address that need to be included use _address_ instead of just _______ in your text which will be replaced by the real address in the mail merge process."+
			"<li> For the possessive pronoun he/she/they use _he/she_ for the ones that will be replaced by the right one in mail merge process.</font></td></tr>");  
	    out.println("<tr><td>");
	    out.println("<textarea name=complaint rows=5 cols=70 tabindex="+
			seq+" wrap>");
	    out.println(complaint);
	    out.println("</textarea>");
	    out.println("</td></tr>");
	    seq++;
	    out.println("<tr><td><b>BM Codes:</b>");
	    out.println("<input name=codes value=\""+codes+
			"\" tabindex="+seq+
			" size=60 maxlength=80></td></tr> ");
	    seq++;
	    out.println("<tr><td><b>Fines: </b> $");
	    out.println("<input name=amount value=\""+amount+"\" tabindex="+seq
			+" size=8 maxlength=8><font color=green size=-1>"+
			" Enter 0 if fines are unknown or unspecified "+
			"</font></td></tr>");
	    seq++;
	    //
	    // buttons block
	    // 
	    out.println("<tr><td align=right><table width=60%>");
	    if(sid.equals("")){
		out.println("<tr><td valign=top>");
		out.println("<input type=submit name=action accesskey=N "+
			    "tabindex="+seq+
			    " value=Next>");
		seq++;
		out.println("</td><td valign=top>");
		out.println("<input type=submit name=action tabindex="+seq+
			    " value=\"Add as New Subcategory\"></td></tr>");
		seq++;
		out.println("</table></td></tr>");
		out.println("<tr><td align=center>");
		out.println("<font color=green size=-1>Access keys "+
			    "are: Alt + N=Next A=Add, S=Switch "+
			    "</font></td></tr>");
		out.println("</form>");
	    }
	    else{
		out.println("<tr>");
		if(user.canEdit()){
		    out.println("<td valign=top>");
		    out.println("<input type=submit name=action tabindex="+seq+
				" accesskey=U "+
				" value=Update></td>");
		    seq++;
		}
		out.println("<td valign=top>");
		out.println("<input type=submit name=action tabindex="+seq+
			    " accesskey=A "+
			    " value=\"Add as New Subcategory\">");
		seq++;
		out.println("</form>");
		out.println("</td>");
		if(user.canEdit()){
		    out.println("<td valign=top>");
		    out.println("<form name=myform onSubmit=\"return "+
				"validateDelete();\">");
		    out.println("<input type=hidden name=cid value="+cid+">");
		    out.println("<input type=hidden name=sid value="+sid+">");
		    out.println("<input type=submit name=action tabindex="+seq+
				" accesskey=D "+
				" value=Delete></td>");
		    out.println("</form>");
		}
				
		out.println("</tr>");

		out.println("<tr><td colspan=3 align=center>");
		out.println("<font color=green size=-1>Access keys "+
			    "are: Alt + U=Update, A=Add, D=Delete, S=Switch "+
			    "</font></td></tr>");
		out.println("</table></td></tr>");
	    }
	    out.println("</table></td></tr>");
	    // 
	    out.println("</table>");
	}
	out.println("</div>");
	out.println("</body></html>");
	out.flush();
	out.close();
	Helper.databaseDisconnect(con, stmt, rs);		

    }
    /**
     * Makes a list of categories of violations
     *
     * @param stmt
     * @param rs 
     * @return a String of exceptions if any
     */
    String resetArrays(Statement stmt,
		       ResultSet rs){
	String str ="",str2="", qq ="", ret="";
	int cnt = 0, i=0;
	qq = "select count(cid) "+
	    " from legal_viol_cats ";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		cnt = rs.getInt(1);
	    }
	    if(cnt > 0){
		catsArr = new String[cnt];
		catsIdArr = new String[cnt];
		for(int j=0;j<cnt;j++){ 
		    catsArr[j] = "";
		    catsIdArr[j] = "";
		}
		qq = "select cid,category "+
		    " from legal_viol_cats order by category ";
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		while(rs.next()){
		    str = rs.getString(1);
		    if(str == null) str=""; 
		    str2 = rs.getString(2);
		    if(str.equals("0")) str2="";
		    if(!str.equals("")){
			catsIdArr[i] = str;
			catsArr[i] = str2;
			i++;
		    }
		}
	    }
	}
	catch(Exception e){
	    ret += e;
	}
	return ret;
    }

}






















































