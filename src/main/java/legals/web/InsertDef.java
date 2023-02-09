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
 */
@WebServlet(urlPatterns = {"/InsertDef"})
public class InsertDef extends TopServlet{

    static final long serialVersionUID = 41L;
    static Logger logger = LogManager.getLogger(InsertDef.class);
    /**
     * Generates the search defendants form and then list the matching records.
     *
     * The user can check a selection of these and clicks the add defendents
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
	String message = "";
	boolean success = true, showAll=false;
	String [] titles = {"Check",
	    "Defendant ID",
	    "Name",
	    "SSN",
	    "DOB",
	    "Address"
	};
	String action="";
	String id="",full_name="",dob="",ssn="";
       	String f_name="",l_name="";
	String did="", access="",
	    street_num="",street_dir="",
	    street_name="",street_type="",sud_type="",sud_num="",
	    city="",state="",zip="";
	String [] defList = null;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("did")){
		did = value;
	    }
	    else if (name.equals("l_name")){
		l_name = value;
	    }
	    else if (name.equals("f_name")){
		f_name = value;
	    }
	    else if (name.equals("dob")){
		dob=value;
	    }
	    else if (name.equals("marked")){
		defList=vals; // an array
	    }
	    else if (name.equals("ssn")){
		ssn=value;
	    }
	    else if (name.equals("id")){
		id=value;
	    }
	    else if (name.equals("street_num")) {
		street_num = value;
	    }
	    else if (name.equals("street_dir")) {
		street_dir = value;
	    }
	    else if (name.equals("street_name")) {
		street_name = value;
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
	    else if (name.equals("city")) {
		city = value.toUpperCase();
	    }
	    else if (name.equals("state")) {
		state = value.toUpperCase();
	    }
	    else if (name.equals("zip")) {
		zip = value;
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
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
	if(action.startsWith("Insert") && user.canEdit()){
	    if(defList != null){
		if(defList.length > 0){
		    for(int i=0;i<defList.length; i++){
			String str = defList[i];
			if(str != null){
			    Defendant def = new Defendant(str, debug);
			    String back = def.addCaseToDefendant(id);
			    if(!back.equals("")){
				message += back;
				success = false;
			    }
			}
		    }
		}
	    }
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
	out.println("<center><h3>Search People to Add to a Case</h3>");
	out.println("<h3>Look for available defendants in legaltrack</h3>");
	if(action.startsWith("Insert")){
	    if(success){
		out.println("<font color=green>");
		out.println("<h3>The selected records were added "+
			    "successfully</h3>");
		out.println("</font>");
	    }
	    else {
		out.println("<font color=red>");
		out.println("<h3>Some/All of the selected records were "+
			    "NOT added successfully</h3>");
		out.println("</font>");
	    }
	}
	out.println("<table align=center width=100% border>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table>");
	//the real table
	out.println("<form name=myForm method=post >");
	//			"onSubmit=\"return validateForm()\">");
	//
	out.println("<input type=hidden name=id value=\""+id+"\">");
	//
	// 1st block
	out.println("<tr><td><table>");
	out.println("<tr><td align=right><b>ID:</b></td><td>");
	out.println("<input name=did value=\""+did+"\""+
		    " size=8 maxlength=8></td></tr>");
	out.println("<tr><td align=right>");
	out.println("<b>Last Name(s)</b>:<font color=green>*</font></td><td>");
	out.println("<input name=l_name value=\""+l_name+"\""+
		    " size=50 maxlength=50></td></tr>");
	out.println("<tr><td align=right><b>First Name(s):</b>"+
		    "<font color=green>*</font></td><td>");
	out.println("<input name=f_name value=\""+f_name+"\""+
		    " size=50 maxlength=50></td></tr>");
	//
	out.println("<tr><td align=right><b>SS#:</b></td><td> ");
	out.println("<input name=ssn value=\""+ ssn+"\""+
		    " size=10 maxlength=10></td><td>");

	out.println("<tr><td align=right><b>DOB:</b></td><td> ");
	out.println("<input name=dob value=\""+ ssn+"\""+
		    " size=10 maxlength=10></td></tr>");
	// address
	out.println("<tr><td valign=top align=right><br><b>Address:</b>"+
		    "</td><td><table>");
	out.println("<tr><td>St. No. </td><td>Dir</td><td>"+
		    "St. Name</td></tr>");
	out.println("<tr>");
	out.println("<td><input name=street_num size=8 "+
		    "maxlength=8 value=\""+street_num+"\">");
	out.println("</td><td>");
	out.println("<select name=street_dir>");
	for(int i=0;i<Inserts.dirArr.length;i++){
	    out.println("<option>"+Inserts.dirArr[i]);
	}
	out.println("</select></td><td>");
	out.println("<input name=street_name size=20 "+
		    "maxlength=20 value=\""+street_name+"\">");
	out.println("</td></tr>");
	out.println("<tr><td>St. Type</td><td>Sud Type</td><td>"+
		    "Sud Num</td></tr>");
	//
	out.println("<tr><td>");
	out.println("<select name=street_type>");
	for(int i=0;i<Inserts.streetKeys.length;i++){
	    out.println("<option value=\""+
			Inserts.streetKeys[i]+"\">"+Inserts.streetInfo[i]);
	}
	out.println("</select></td><td>");
	out.println("<select name=sud_type>");
	for(int i=0;i<Inserts.sudKeys.length;i++){
	    out.println("<option value=\""+
			Inserts.sudKeys[i]+"\">"+
			Inserts.sudInfo[i]);
	}
	out.println("</select></td><td>");
	out.println("<input name=sud_num size=8 "+
		    "maxlength=8 value=\""+sud_num+"\"></td></tr>");
	// 
	// city state zip
	out.println("<tr><td>City</td><td>State</td><td>Zip</td></tr>");
	out.println("<tr><td>");
	out.println("<input name=city size=20 "+
		    "maxlength=30 value=\"\"></td><td>");
	out.println("<input name=state size=2 "+
		    "maxlength=2 value=\"\"></td><td>");
	out.println("<input name=zip size=14 "+
		    "maxlength=14 value=\"\"></td></tr>");
	out.println("</table></td></tr>");

	out.println("</table></td></tr>");
	out.println("</table>");
		
	out.println("<tr><td align=\"center\">");
	out.println("<input type=submit "+
		    "name=action value=Browse></td></tr>");
	out.println("<tr><td align='center'>");
		
	out.println("<font color=green size=-1>* You can use part of "+
		    "the name, multiple names space separated. </font><br>");
	out.println("<li><a href="+url+"CaseServ?id="+id+"&action=zoom"+
		    ">Back to Related Case</a></td></tr>");
	if(action.equals("Browse")){
	    out.println("<tr><td>");
	    Defendant def = new Defendant(debug);
	    DefAddress addr = new DefAddress(debug);
	    if(!did.equals("")){
		def.setDid(did);
		addr.setDefId(did);
	    }
	    if(!ssn.equals("")){
		def.setSsn(ssn);
	    }
	    if(!dob.equals("")){
		def.setDob(dob);
	    }
	    if(!street_num.equals("")){
		addr.setStreet_num(street_num);
	    }
	    if(!street_dir.equals("")){
		addr.setStreet_dir(street_dir);
	    }
	    if(!street_type.equals("")){
		addr.setStreet_type(street_type);
	    }
	    if(!street_name.equals("")){
		addr.setStreet_name(street_name);
	    }
	    if(!sud_type.equals("")){
		addr.setSud_type(sud_type);
	    }
	    if(!sud_num.equals("")){
		addr.setSud_num(sud_num);
	    }
	    if(!city.equals("")){
		addr.setCity(city);
	    }
	    if(!state.equals("")){
		addr.setState(state);
	    }
	    if(!zip.equals("")){
		addr.setZip(zip);
	    }
	    if(!l_name.equals("")){
		def.setL_name(l_name);
	    }
	    if(!f_name.equals("")){
		def.setF_name(f_name);
	    }
	    def.setAddress(addr);

	    List<Defendant> defs = null;
	    DefendantList dl = new DefendantList(debug);
	    dl.setDefendant(def);
	    String back = dl.lookFor();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		defs = dl.getDefendants();
	    }
	    if(!success){
		out.println("<h4>Error "+ message +" </h4>");
		out.println("</div></body></html>");
		return;
	    }
	    if(defs != null)
		out.println("<h4>Total Matching Records "+ defs.size() +" </h4>");
	    else
		out.println("<h4>No match found </h4>");
	    if(defs != null && defs.size() > 0){
		out.println("<font color=green>");
		out.println("Select defendant(s) and "+
			    " click on"+
			    " Insert Selection Button<br>");
		out.println("</font>");
		out.println("<table border>");
		out.println("<tr>");
		for (int c = 0; c < titles.length; c++){ 
		    out.println("<th>"+titles[c]+"</th>");
		}	   
		out.println("</tr>");
		for (Defendant deff: defs){
		    String str = deff.getDid();
		    out.println("<tr><td>");
		    out.println("<input type=checkbox name=marked "+
				"value="+str+"></td>");
		    out.println("<th>"+str+"</th>");
					
		    str = deff.getFullName();
		    if(str == null || 
		       str.trim().equals(",") ||
		       str.trim().equals("")) str = "&nbsp;";
		    out.println("<td>"+str+"</td>");
		    str = deff.getSsn();
		    if(str == null || 
		       str.trim().equals("")) str = "&nbsp;";
		    out.println("<td>"+str+"</td>");
		    str = deff.getDob();
		    if(str == null || 
		       str.trim().equals("")) str = "&nbsp;";
		    out.println("<td>"+str+"</td>");
		    addr = deff.getAddress();
		    str = "&nbsp;";
		    if(addr != null){
			str = addr.getAddress();
			if(str == null || str.trim().equals(""))
			    str = "&nbsp;";
		    }
		    out.println("<td>"+str+"</td>");						
		    out.println("</tr>");	
		}
		out.println("</table></td></tr>");
		if(user.canEdit()){
		    out.println("<tr><td align=\"center\">");
		    out.println("<input type=submit name=action "+
				"value=\"Insert Selection\" />");
		    out.println("</td></tr>");
		}
	    }
	    out.println("</table>");
	}
	out.println("</form>");
	//
	out.print("</div></body></html>");
	out.close();

    }

}






















































