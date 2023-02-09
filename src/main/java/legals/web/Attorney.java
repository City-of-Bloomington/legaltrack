package legals.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.list.*;
import legals.model.*;
import legals.utils.*;
/**
 *
 *
 */
@WebServlet(urlPatterns = {"/Attorney"})
public class Attorney extends TopServlet{

    static final long serialVersionUID = 20L;
    final static String bgcolor = "silver";// #bfbfbf gray
    static Logger logger = LogManager.getLogger(Attorney.class);

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
    
	// 
	boolean connectDbOk = false, success = true;
	//
	String message="", action="";
	String id="",empid="",
	    barNum=""; // attorney bar number (such as 23442-53)
	String removeType="", addType="";

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	Enumeration<String> values = req.getParameterNames();

	List<CaseType> caseTypes = null;
	List<CaseType> freeTypes = null;
	List<CaseType> lawyerTypes = null;
	String [] typeId = null;
	String [] vals;
	//String [] typeId = null;
	String [] freeTypeId = null;
	Lawyer lawyer = new Lawyer(debug);
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
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("empid")) {
		lawyer.setEmpid(value);
		empid = value;
	    }
	    if (name.equals("id")) {
		lawyer.setId(value);
		id = value;
	    }			
	    else if (name.equals("fname")) {
		lawyer.setFname(value);
	    }
	    else if (name.equals("lname")) {
		lawyer.setLname(value);
	    }			
	    else if (name.equals("title")) {
		lawyer.setTitle(value);
	    }
	    else if (name.equals("position")) {
		lawyer.setPosition(value);
	    }			
	    else if (name.equals("active")) {
		lawyer.setActive(value);
	    }
	    else if (name.equals("barNum")){
		lawyer.setBarNum(value);
	    }
	    else if (name.equals("typeId")){
		typeId = vals;
	    }
	    else if (name.equals("removeType")){
		removeType = value;
	    }
	    else if (name.equals("addType")){
		addType = value;
	    }
	    else if (name.equals("action")){ 
		// Get, Save, zoom, edit, delete, New, Refresh
		action = value;  
	    }
	}
	if(true){
	    CaseTypeList cl = new CaseTypeList(debug);
	    String back = cl.find();
	    if(back.equals("")){
		caseTypes = cl.getTypes();
	    }
	    else{
		logger.error(back);
		success = false;
		message += back;
	    }
	}
        if(action.equals("Save")){
	    //
	    if(user.canEdit()){
		String back = lawyer.doSave();
		if(back.equals("")){
		    message += " Saved Successfully";
		    id = lawyer.getId();
		}
		else{
		    logger.error(back);
		    success = false;
		    message += " Could not save "+back;		
		}
	    }
	    else{
		success = false;
		message += " Could not save ";	
	    }
	}
        else if(action.equals("Update")){
	    //
	    String str="", qq = "";
	    if(user.canEdit()){
		String back = lawyer.doUpdate();
		if(back.equals("")){
		    message += " Updated Successfully ";
		}
		else{
		    logger.error(back);
		    success = false;
		    message += " Could not update "+back;		
		}
		if(success && typeId != null){
		    back = lawyer.setCaseTypes(typeId);
		    if(back.equals("")){
			message += " Updated Successfully ";
		    }
		    else{
			logger.error(back);
			success = false;
			message += " Could not update "+back;		
		    }	
		}				
	    }
	    else{
		success = false;
		message += " Could not save ";	
	    }
	}
	else if(action.equals("Delete")){
	    //
	    if(user.canDelete()){
		String back = lawyer.doDelete();
		if(back.equals("")){
		    message += " Deleted Successfully ";
		}
		else{
		    success = false;
		    message += " Could not delete "+back;
		}
	    }
	    else{
		success = false;
		message += " You could not delete ";
	    }
	    if(success){
		id="";empid="";
		typeId = null;
		lawyerTypes = null;
	    }
	}
	else if(action.equals("Remove") && user.canEdit()){
	    //
	    // remove a type from the lawyer's list
	    if(!removeType.equals("")){
		String back = lawyer.removeType(removeType);
		if(back.equals("")){
		    message += " Removed Successfully";
		}
		else{
		    logger.error(back);
		    success = false;
		    message += back;
		}
	    }
	}
	else if(action.equals("Add") && user.canEdit()){
	    // add a type to the lawyer's list
	    if(!addType.equals("")){
		String back = lawyer.addType(addType);
		if(back.equals("")){
		    message += " Added Successfully";
		}
		else{
		    logger.error(back);
		    success = false;
		    message += back;
		}
	    }
	}
	else if(action.startsWith("New")){
	    id="";
	    typeId = null;
	}
	if(action.equals("zoom") ||
	   action.equals("Add") ||
	   action.equals("Update") ||
	   action.equals("Remove")){	
	    //
	    String back = lawyer.doSelect();
	    if(!back.equals("")){
		logger.error(back);
		message += " Error retreiving data "+back;
		success = false;
	    }
	    empid = lawyer.getEmpid();
	    CaseTypeList ctl = new CaseTypeList(debug);
	    ctl.setLawyerId(empid);
	    back = ctl.find();
	    if(!back.equals("")){
		logger.error(back);
		message += " Error retreiving data "+back;
		success = false;
	    }
	    else{
		lawyerTypes = ctl.getTypes();
	    }	
	    //
	    ctl = new CaseTypeList(debug);
	    ctl.setFreeTypes();
	    back = ctl.find();
	    if(!back.equals("")){
		logger.error(back);
		message += " Error retreiving data "+back;
		success = false;
	    }
	    else{
		freeTypes = ctl.getTypes();
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
		
	out.println("<center><h2>Attorney</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");
	}
	//
	out.println("<form name=myForm method=post>");
	//
	out.println("<center><table border><tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table width=100%>");
	out.println("<tr><td align=center "+
		    "style=\"background-color:navy; color:white\">"+
		    "<b>Attorney</b>"+
		    "</td></tr>");
		
	out.println("<tr><td align=left><br /><u><b>Notice:</b></u> "+
		    "<font color=green size=-1>"+
		    "This page is intended to be used to: "+
		    "<ul>"+
		    "<li> Add a new attorney or update current attorney data."+
		    "<li> Assign violation types to each attorney after the attorney record is created."+
		    "<li> Redistribute violation types among attorneys, "+
		    "Since you can not assign the same violation type to two"+
		    "<br />"+
		    " different attorneys. Follow the following approach; "+
		    "Suppose you want to assign certain violation type to "+
		    "<br />"+
		    " Attorney A instead of Attorney B. "+
		    "<br />"+
		    "First you deselect this type from Attorney B"+
		    " and then hit the Remove Button. "+
		    "<br />"+
		    " Second bring Attorney A page and select the same "+
		    " violation type and hit the Add button."+
		    "</ul>"+
		    "</font><br />");
	out.println("</td></tr></table></td></tr>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table width=100%>");
	// 
	if(!id.equals("")){
	    out.println("<tr><td align=right><b>Attorney ID:</td>"+
			"<td align=left>"+id+"</td></tr>");
	    out.println("<input type=hidden name='id' value='"+
			id+"'>");
	}
	out.println("<tr><td align=right><b>Username:</td>"+
		    "<td align=left>");
	out.println("<input type='text' name='empid' size='20' "+
		    " maxlength='50' value=\""+lawyer.getEmpid()+"\" ></td></tr>");
	out.println("<tr><td align=right><b>First Name:</td>"+
		    "<td align=left>");
	out.println("<input type='text' name='fname' size='30' "+
		    " maxlength='50' value='"+Helper.replaceSpecialChars(lawyer.getFname())+
		    "'></td></tr>");
	//
	out.println("<tr><td align=right><b>Last Name:</td>"+
		    "<td align=left>");
	out.println("<input type='text' name='lname' size='30' "+
		    " maxlength='50' value='"+Helper.replaceSpecialChars(lawyer.getLname())+
		    "'></td></tr>");		
	out.println("<tr><td align=right><b>Attorney Title:</td>"+
		    "<td align=left>");
	out.println("<input type='text' name='title' size='30' "+
		    " maxlength='30' value='"+Helper.replaceSpecialChars(lawyer.getTitle())+
		    "'></td></tr>");
	out.println("<tr><td align=right><b>Position:</td>"+
		    "<td align=left>");
	out.println("<select name=\"position\">");
	out.println("<option value=\"\"></option>");
	for(String one:Helper.positions){
	    String selected = lawyer.getPosition().equals(one)?"selected=\"selected\"":"";
	    out.println("<option "+selected+">"+one+"</option>");
	}
	out.println("</select></td></tr>");
	//
	out.println("<tr><td align=right><b>Attorney Bar #:</td>"+
		    "<td align=left>");
	out.println("<input type='text' name='barNum' size='10' "+
		    " maxlength='10' value='"+lawyer.getBarNum()+
		    "'></td></tr>");
	//
	out.println("<tr><td colspan='2' align=left>* The Attorney who ends his/her "+
		    "employment with The City, it is better to be marked as "+
		    "inative <br />"+
		    "by unchecking the checkbox below instead of deleting "+
		    "the whole record</td></tr>");
	out.println("<tr><td align=right><b>Active:*</b></td>"+
		    "<td align=left>");
	String checked = lawyer.isActive()?"checked=\"checked\"":"";
	out.println("<input type='checkbox' name='active' "+checked+
		    " value='y'>Yes </td></tr>");
	out.println("</table></td></tr>");		
	if(!id.equals("")){
	    out.println("<tr><td align=center "+
			"style=\"background-color:navy; color:white\">"+
			"<b>Attorney Violation Type Association</b>"+
			"</td></tr>");
	    out.println("<tr><td bgcolor="+Helper.bgcolor+">");
			
	    out.println("<table width=100%><tr>");
	    out.println("<td align='left' "+
			"valign='top'><b>Current Unassociated Violation Types</b><br />"+
			"<font color=green>To associate a new type to this attorney select<br /> one from the list below "+
			" and click on 'Add' button "+
			" </font></td>");
	    out.println("<td>&nbsp;</td>");
	    out.println("<td align='left' "+
			"valign='top'><b>Current violation types sssociated "+
			"with this Attorney</b><br /><font color=green> To remove a type from the list "+
			"select the type and click on <br />'Remove' button</font>");
			

	    out.println("</td></tr>");

	    out.println("<tr><td align=right>");
			
	    out.println("<select name='addType' size='10'>");
	    if(freeTypes != null){
		for(CaseType ft: freeTypes){
		    out.println("<option value='"+ft.getId()+"'>"+
				ft.getDesc()+"</option>");
		}
	    }
	    out.println("</select></td>");
	    out.println("<td valign='middle' align='center'>");
	    out.println("<table><tr><td> &lt;&lt;</td><td align=left>");
	    out.println("<input type='submit' name='action' value='Remove'></td>");
	    out.println("<td>&nbsp;</td></tr>");
	    out.println("<tr><td> &nbsp;</td><td align=right>");
	    out.println("<input type='submit' name='action' value='Add'></td>");
	    out.println("<td>&gt;&gt;</td></tr></table></td>");
	    out.println("<td align=left><select name='removeType' size='10'>");
	    if(lawyerTypes != null){
		for(CaseType ct: lawyerTypes){
		    out.println("<option value='"+ct.getId()+"'>"+
				ct+"</option>");
		}
	    }
	    out.println("</select></td>");
	    out.println("</tr></table></td></tr>");			
	}
	if(action.equals("")|| 
	   action.startsWith("New") ||
	   (action.equals("Save") && !success) ||
	   action.startsWith("Delete")){ 
	    //
	    if(user.canEdit()){
		out.println("<tr><td>");
		out.println("<table width=100%>");
		out.println("<tr><td align=right bgcolor="+Helper.bgcolor+">");
		out.println("<input type='submit' "+
			    " name=action value='Save'>");
		out.println("</td></tr></table></td></tr>"); 
		out.println("</form>");
	    }
	}
	else { // save, update
	    //
	    out.println("<tr><td bgcolor="+bgcolor+
			" valign=top align=right><table width=60%><tr>");
	    if(user.canEdit()){
		out.println("<td valign=top><input "+
			    " type='submit' name='action' value='Update'>");
		out.println("</td>");
	    }
	    if(user.canDelete()){
		out.println("<td valign=top><input "+
			    " type=submit name='action' value='New Attorney'>");
		out.println("</form></td><td>");
		out.println("<form onsubmit='return validateDelete()'>");
		out.println("<input type=hidden name='id' value='"+
			    id+"'>");
		out.println("<input type='submit' name='action' "+
			    " value='Delete'>");
		out.println("</form>");
		out.println("</td>");
			
	    }
	    out.println("</tr></table></td></tr>");
	}
	//
	if(true){
	    List<Lawyer> lawyers = null;
	    LawyerList ll = new LawyerList(debug);
	    String back = ll.find();
	    if(back.equals("")){
		lawyers = ll.getLawyers();
	    }
	    else{
		logger.error(back);
		success = false;
	    }
	    String all = "";
	    if(lawyers != null && lawyers.size() > 0){
		for(Lawyer lyr: lawyers){
		    if(!lyr.getId().equals(id)){
			all += "<tr><td align=left><a href='"+url+
			    "Attorney?id="+lyr.getId()+"&action=zoom"+
			    "'>"+Helper.replaceSpecialChars(lyr.getFullName())+
			    "</a></td><td>"+lyr.getBarNum()+"</td><td>"+
			    lyr.getPosition()+"</td><td>"+
			    Helper.replaceSpecialChars(lyr.getTitle())+
			    "</td><td>"+lyr.getActive()+"</td></tr>\n";
		    }
		}
	    }
	    if(!all.equals("")){
		out.println("<tr><td align='center'>");
		out.println("<table><caption>Attorney List</caption>"+
			    "<tr><th>Attorney</th><th>Bar Number</th>"+
			    "<th>Position</th>"+
			    "<th>Title</th><th>Active</th></tr>\n"+
			    all+
			    "</table></td></tr>");
	    }
	}
	out.println("</table>");
	out.println("</div>");
	out.print("</center></body></html>");
	out.flush();
	out.close();

    }

}






















































