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
@WebServlet(urlPatterns = {"/searchDef","/SearchDef"})
public class SearchDef extends TopServlet{

    static final long serialVersionUID = 85L;
    static Logger logger = LogManager.getLogger(SearchDef.class);
    /**
     * Generates the search defendants form and then list the matching records.
     *
     * The user can check a selection of these and clicks the add defendants
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link Case#doGet
     * @see #doGet
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String message="";
	boolean success = true, showAll=false;
	String [] titles = {"Defendant ID",
	    "Name",
	    "DOB",
	    "Address",
	    "Cases Status"
	};
	boolean [] show = {true,
	    true,false,true,true
	};
	String action="";
	String id="",full_name="",dob="",ssn="", dln="";
       	String f_name="",l_name="", street_address="",
	    street_num="",street_dir="", phone="",
	    street_name="",street_type="",sud_type="",sud_num="",
	    city="",state="",zip="", date_from="", date_to="";
	String did="", access="";
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	Defendant def = new Defendant(debug);
	DefAddress addr = new DefAddress(debug);
	DefendantList dl = new DefendantList(debug);
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("did")){
		did = value;
		def.setDid(value);
		addr.setDefId(value);
	    }
	    else if (name.equals("l_name")){
		l_name = value.toUpperCase();
		def.setL_name(value.toUpperCase());
	    }
	    else if (name.equals("f_name")){
				
		f_name = value.toUpperCase();
		def.setF_name(value.toUpperCase());
	    }
	    else if (name.equals("dob")){
		dob=value;
		def.setDob(value);
	    }
	    else if (name.equals("ssn")){
		ssn=value;
		def.setSsn(value);
	    }
	    else if (name.equals("dln")){
		dln=value;
		def.setDln(value);
	    }
	    else if (name.equals("phone")){
		phone=value;
		def.setPhone(value);
	    }
	    else if (name.equals("id")){
		id = value;
		dl.setId(value);
	    }
	    else if (name.equals("street_num")) {
		street_num = value;
		addr.setStreet_num(value);
	    }
	    else if (name.equals("street_address")) {
		street_address = value;
		addr.setStreet_address(value);
	    }						
	    else if (name.equals("street_dir")) {
		street_dir = value;
		addr.setStreet_dir(value);
	    }
	    else if (name.equals("street_name")) {
		street_name = value.toUpperCase();
		addr.setStreet_name(value.toUpperCase());
	    }
	    else if (name.equals("street_type")) {
		street_type = value;
		addr.setStreet_type(value);
	    }
	    else if (name.equals("sud_type")) {
		sud_type = value;
		addr.setSud_type(value);
	    }
	    else if (name.equals("sud_num")) {
		sud_num = value;
		addr.setSud_num(value);
	    }
	    else if (name.equals("city")) {
		city = value.toUpperCase();
		addr.setCity(value.toUpperCase());
	    }
	    else if (name.equals("date_from")) {
		date_from = value;
		dl.setDateFrom(value);
	    }
	    else if (name.equals("date_to")) {
		date_to = value;
		dl.setDateTo(value);
	    }		
	    else if (name.equals("state")) {
		state = value.toUpperCase();
		addr.setState(value.toUpperCase());	
	    }
	    else if (name.equals("zip")) {
		zip = value;
		addr.setZip(value);
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	def.setAddress(addr);
	dl.setDefendant(def);
	//
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?";
	    res.sendRedirect(str);
	    return; 
	}
	//
	// After the browsing  
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	//
	out.println("<center>");
	if(!message.equals("")){
	    if(!success)
		out.println("<h3><font color='red'>"+message+"</h3>");
	}
	out.println("<table align=center width=100% border>");
	out.println("<tr><td align=center "+
		    " style=\"background-color:navy; color:white\">"+
		    "<b>Search Defendant(s)</b>"+
		    "</td></tr>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table>");
	//the real table
	out.println("<form name=myForm method=post >");
		
	//
	// 1st block
	out.println("<tr><td><table>");
	out.println("<tr><td align=right><b>ID:</b></td><td align=left>");
	out.println("<input name=did value=\""+did+"\""+
		    " size=8 maxlength=8></td></tr>");
	out.println("<tr><td align=right>");
	out.println("<b>Last Name(s)</b>:<font color=green>*</font></td><td align=left>");
	out.println("<input name=l_name value=\""+l_name+"\""+
		    " size=50 maxlength=50></td></tr>");
	out.println("<tr><td align=right><b>First Name(s):</b>"+
		    "<font color=green>*</font></td><td align=left>");
	out.println("<input name=f_name value=\""+f_name+"\""+
		    " size=50 maxlength=50></td></tr>");
	//
	out.println("<tr><td align=right><b>SS#:</b></td><td align=left> ");
	out.println("<input name=ssn value=\""+ ssn+"\""+
		    " size=10 maxlength=10></td></tr>");
	out.println("<tr><td align=right><b>DLN:</b></td><td align=left> ");
	out.println("<input name=dln value=\""+ dln+"\""+
		    " size=15 maxlength=20></td></tr>");		
	out.println("<tr><td align=right><b>DOB:</b></td><td align=left> ");
	out.println("<input name=dob value=\""+ dob+"\""+
		    " size=10 maxlength=10>");
	out.println("</td></tr>");
	out.println("<tr><td align=right><b>Phone:</b></td><td align=left> ");
	out.println("<input name=phone value=\""+ phone+"\""+
		    " size=10 maxlength=20>");
	out.println("</td></tr>");		
	// address
	out.println("<tr><td>&nbsp;</td><td align=\"left\">"+
		    "<table>");
	out.println("<tr><td align=\"right\"><b>Address:</b></td><td colspan=\"2\">");
	out.println("<input name=street_address size=30 "+
		    "maxlength=70 value=\""+street_address+"\"></td></tr>");
				
	// 
	// city state zip
	out.println("<tr><th>City</th><th>State</th><th>Zip</th></tr>");
	out.println("<tr><td align=left>");
	out.println("<input name=city size=20 "+
		    "maxlength=30 value=\"\"></td><td align=left>");
	out.println("<input name=state size=2 "+
		    "maxlength=2 value=\"\"></td><td align=left>");
	out.println("<input name=zip size=14 "+
		    "maxlength=14 value=\"\"></td></tr>");
	out.println("<tr><th></th><th>From (mm/dd/yyyy)</th><th>To(mm/dd/yyyy)</th></tr>");
	out.println("<tr><td align=left>Address Date</td>");
	out.println("<td align=left>");
	out.println("<input name=date_from size=10 class=\"date\" "+
		    "maxlength=10 value=\""+date_from+"\"></td><td align=left>");
	out.println("<input name=date_to size=10 class=\"date\" "+
		    "maxlength=10 value=\""+date_to+"\"></td><td align=left>");
	out.println("</td></tr>");	
	out.println("</table></td></tr>");
	out.println("</table></td></tr></table></td></tr>");
	out.println("<tr><td align=center>");
	out.println("<input type=submit "+
		    "name=action value=Browse>");
	out.println("</td></tr></table>");
	out.println("<font color=green size=-1>* You can use part of "+
		    "the name and multiple names space separated. </font><br>");
	out.println("</form><br />");
	out.println(Inserts.jsStrings(url));		
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
		

	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");				
	if(action.equals("Browse")){
	    List<Defendant> defs = null;
	    List<Case> cases = null;
	    String back = dl.lookFor();
	    if(back.equals("")){
		defs = dl.getDefendants();
	    }
	    if(defs != null){
		out.println("<h4>Total Matching Records "+ defs.size() +" </h4>");
		out.println("<table border>");
		out.println("<tr>");
		for (int c = 0; c < titles.length; c++){ 
		    out.println("<th>"+titles[c]+"</th>");
		}	   
		out.println("</tr>");
		for(Defendant deff: defs){
		    out.println("<tr>");
		    //
		    String str = deff.getDid();
		    if(user.canEdit()){
			out.println("<td><a href="+url+"Defendent?"+
				    "did="+str+
				    ">"+str+"</a></td>");
		    }
		    else{
			out.println("<td><a href="+url+"DefendantView?"+
				    "did="+str+
				    ">"+str+"</a></td>");
		    }
		    str = deff.getFullName();
		    if(str == null || str.equals("")) str = "&nbsp;";
		    out.println("<td>"+str+"</td>");
		    str = deff.getDob();
		    if(str == null || str.equals("")) str = "&nbsp;";
		    out.println("<td>"+str+"</td>");
		    addr = deff.getAddress();
		    str = addr.getAddress();
		    if(str == null || str.equals("")) str = "&nbsp;";
		    out.println("<td>"+str+"</td>");
		    str = "";
		    String all="";
		    cases = deff.getCases();
		    if(cases != null && cases.size() > 0){
			for(Case cc:cases){
			    if(!all.equals("")) all += ", ";
			    str = cc.getId();
			    Status status = cc.getCStatus();
			    if(user.canEdit()){
				all += "<a href="+url+"CaseServ?"+
				    "id="+str+
				    ">"+status+"</a>";
			    }
			    else{
				all += "<a href="+url+"CaseView?"+
				    "id="+str+
				    ">"+status+"</a>";
			    }
			}
		    }
		    out.println("<td>"+all+"</td>");	
		    out.println("</tr>");
		}
		out.println("</table><br /><br />");
	    }
	    else{
		out.println("<h4>No match found </h4>");
	    }
	}
	//
	out.print("</div></body></html>");
	out.close();
    }

}






















































