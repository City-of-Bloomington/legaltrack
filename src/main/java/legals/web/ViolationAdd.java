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

@WebServlet(urlPatterns = {"/ViolationAdd"})
public class ViolationAdd extends TopServlet{

    static final long serialVersionUID = 83L;
    List<ViolCat> violCats = null;
    static Logger logger = LogManager.getLogger(ViolationAdd.class);
    //
    

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
    
	String category = "", subcat="", codes="",description="", dates="",
	    overide_code="",overide_descp="",ident="",id="", vid="",
	    cid="",sid="",amount="",complaint="";
	// 
	String street_num="",street_dir="",street_name="",street_type="",
	    sud_type="",sud_num="";
	String rent_street_num="",rent_street_dir="",rent_street_name="",
	    rent_street_type="",rent_sud_type="",rent_sud_num="",
	    cause_num="",city="",state="",zip="",invld_addr="", amount2="";

	boolean success = true;
	//
	String message="", action="", action2="";
	
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	Enumeration<String> values = req.getParameterNames();
	HttpSession session = null;
	session = req.getSession(false);

	/*
	  String[] subcatIdArr = null;
	  String[] subcatArr = null;
	*/
	List<ViolSubcat> violSubcats = null;
		
	String [] vals;
	String [] defList = null;
	CaseViolMultiple cv = new CaseViolMultiple(debug);
	Case mcase = null;
	ViolSubcat vsub = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("codes")) {
		codes = value;
	    }
	    else if (name.startsWith("dates")) {
		cv.setDates(name, value);
	    }
	    else if (name.startsWith("amount")) {
		cv.setAmount(name, value);
	    }
	    else if (name.startsWith("citations")) {
		cv.setCitations(name, value);
	    }		
	    else if (name.equals("id")){
		if(!value.equals("")){
		    cv.setId(value);
		    id = value;
		}
	    }
	    else if (name.equals("cid")){ // category id
		if(!value.equals("")){
		    cv.setCid(value);
		    cid = value;
		}
	    }
	    else if (name.equals("sid")){ // subcat id
		if(!value.equals("")){			
		    cv.setSid(value);
		    sid = value;
		}
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("action2")){ 
		if(!value.equals(""))
		    action = value;  
	    }
	}
	User user = null;
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
	if(action.equals("Save")){
	    //
	    String back = cv.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		message = "Saved Successfully";
		sid = cv.getSid();
		vsub = cv.getViolSubcat();
		cid = cv.getCid();
	    }
	}
	if(vsub != null){
	    amount2 = vsub.getAmount();
	    cid = vsub.getCid();
	    subcat = vsub.getSubcat();
	}
	else if(!sid.equals("")){
	    vsub = new ViolSubcat(sid, debug);
	    vsub.doSelect();
	    amount2 = vsub.getAmount();
	    subcat = vsub.getSubcat();
	}
	if(true){
	    ViolCatList vcl = new ViolCatList(debug);
	    String back = vcl.find();
	    if(back.equals("")){
		violCats = vcl.getViolCats();
	    }
	    if(cid.equals("") && !id.equals("")){
		mcase = new Case(debug, id);
		back = mcase.doSelect();
		if(back.equals("")){
		    String cid_t = mcase.findCroosRefViolation();
		    if(!cid_t.equals("")){
			cid = cid_t;
		    }
		}
	    }
	    if(!(cid.equals("") || cid.equals("0"))){
		//
		ViolSubcatList vsl = new ViolSubcatList(cid, debug);
		back = vsl.find();
		if(back.equals("")){
		    violSubcats = vsl.getViolSubcats();
		    if(violSubcats != null && violSubcats.size() == 1){
			vsub = violSubcats.get(0);
			amount2 = vsub.getAmount();
			subcat = vsub.getSubcat();
			sid = vsub.getSid();
		    }
		}
		else{
		    message += back;
		    success = false;
		}
	    }
	}
	if(amount.equals("")) amount = amount2;
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	//
	out.println("<script type=\"text/javascript\">            ");
	out.println("  function runNext(){                        ");
	out.println("   document.myForm.action2.value = \"Next\"; ");
	out.println("     document.myForm.submit();               ");
	out.println("	}			       		                  ");
	//
	out.println(" </script>				                 ");

    	out.println("<center><h2>Add Multiple Violations</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");
	}

	if(action.startsWith("Next")){
	    out.println("<h3>Continue with Violation</h3>");
	}
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	out.println("<input type=hidden name=id value="+id+">");
	out.println("<input type=hidden name=action2 value=\"\">");
	// category related 
	if(cid.equals("")){
	    if(violCats != null){
		out.println("<input type=hidden name=vid value=\"\" />");
		out.println("<input type=hidden name=sid value=\"\" />");
	    }
	}
	else{
	    out.println("<input type=hidden name=cid value=\""+cid+"\">");
	    if(!sid.equals("")){
		out.println("<input type=hidden name=sid value=\""+sid+"\">");
	    }
	}
	out.println("<center><table border><tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table width=100%>");
		
	out.println("<tr><td align=center colspan=2"+
		    " style=\"background-color:navy; color:white\">"+
		    "<b>Violation</b>"+
		    "</td></tr>");
	List<Defendant> defs = null;
	DefendantList dl = new DefendantList(id, debug);
	String back = dl.find();
	if(!back.equals("")){
	    message += back;
	    success = false;
	}
	else{
	    defs = dl.getDefendants();
	}
	if(defs != null && defs.size() > 0){
	    out.println("<tr><td colspan=2>");
	    Helper.writeDefs(out, url, id, defs);
	    out.println("</td></tr>");
	}
	out.println("<tr><td>&nbsp;</td></tr>"); // separator		
	out.println("<tr><td align=center colspan=2>You can enter up to five violation at a time. Each violation would have one citation date, one citation number and a fine amount</td></tr>");
	out.println("<tr><td align=center colspan=2>Each group of violations will have the same category and subcategory</td></tr>");		
	out.println("<tr><td align=right><b>Case ID:</td>"+
		    "<td><a href="+url+
		    "CaseServ?action=zoom&id="+id+">"+
		    id+"</b><br>");
	int tabindex=1;
	out.println("<tr><td align=right><b>Violation Category: </b></td>"+
		    "<td align=left>");
	if(cid.equals("") && violCats != null){
	    out.println("<select name=cid tabindex="+tabindex+
			" onChange=\"runNext()\">");
	    out.println("<option value=\"\">Pick A Category</option>");
	    tabindex++;
	    for(ViolCat vcc:violCats){
		out.println("<option value=\""+vcc.getId()+"\">"+vcc+"</option>");
	    }
	    out.println("</xelect></td></tr>");
	}
	else if(violCats != null){
	    for(ViolCat vcc:violCats){
		if(vcc.getId().equals(cid)){
		    out.println(vcc);
		    break;
		}
	    }
	    out.println("</td></tr>");
	}

	//
	// Sub category
	//
	if(!cid.equals("")){
	    out.println("<tr><td align=right valign=top><b>Sub category: "+
			"</b></td>");
	    out.println("<td align=left>");
	    if(sid.equals("")){
		if(violSubcats != null){
		    out.println("<select name=sid tabindex="+tabindex+
				" onChange=\"runNext()\">");
		    tabindex++;
		    out.println("<option value=\"\">Pick A Sub Category</option>");					
		    for(ViolSubcat vsubc:violSubcats){
			if(vsubc.getId().equals(sid))
			    out.println("<option selected value=\""+
					vsubc.getId()+"\">"+vsubc.getSubcat());
			else
			    out.println("<option value=\""+
					vsubc.getId()+"\">"+
					vsubc.getSubcat());
			
		    }
		    out.println("</select>");
		    out.println("<input type=hidden name=vid value=\"\" />");					
		}
		else{
		    //
		    out.println("<font color=red>No Subcategory found. Need to add subcategory and related complaint description<br /> and BMC using Edit Category option in the menu.<br></font>"); 
		}
	    }
	    else{
		// after being saved we do not allow editing
		out.println(subcat);
	    }
	}
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td colspan=\"2\"><table>");
	out.println("<tr><th></th><th>Fine Amounts</th><th>Citation #</th><th>Date (mm/dd/yyyy)</th></tr>");
	for(int jj=0;jj<5;jj++){
	    out.println("<tr><td align=left>"+(jj+1)+"</td>");
	    out.println("<td align=left>");			
	    out.println("<input name=amount"+jj+" tabindex="+tabindex+" value=\""+amount+"\" size=6 maxlength=10></td>");
	    tabindex++;
	    out.println("<td align=left>");
	    out.println("<input name=citations"+jj+" size=30 maxlength=50 tabindex="+tabindex+" value=\"\" /></td>");
	    tabindex++;
	    out.println("<td align=left>");
	    out.println("<input name=dates"+jj+" size=10 maxlength=10 tabindex="+
			tabindex+" value=\"\" />");
	    tabindex++;
	    out.println("</td></tr>");
	}
	out.println("</table></td></tr>");
	out.println("<tr><td>");
	out.println("<table width=100%>");
	out.println("<tr><td align=right bgcolor="+Helper.bgcolor+">");
	if(!sid.equals("")){
	    out.println("<input type=submit accesskey=S tabindex="+
			tabindex+" name=action value=Save>");
	    out.println(" After saving, records will be listed in the bottom of the page.");
	}
	tabindex++;
	out.println("</td></tr>"); 
	out.println("</form>");
	out.println("<tr><td align=center>");
	out.println("<center><font color=green size=-1>Access keys "+
		    "are: Alt + N=Next, S=Save "+
		    "</font></center></td></tr></table></td></tr>");
	out.println("</table>");
	if(true){
	    CaseViolationList cvl = new CaseViolationList(id, debug);
	    String str = cvl.find();
	    List<CaseViolation> cvs = cvl.getCaseViolations();
	    if(str.equals("") && cvs != null && cvs.size() > 0){
		Helper.writeViolations(out, url, id, cvs);
	    }
	}
	out.print("</center></div></body></html>");
	out.flush();
	out.close();

    }

}






















































