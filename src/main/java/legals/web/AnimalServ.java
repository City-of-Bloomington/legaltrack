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
@WebServlet(urlPatterns = {"/AnimalServ"})
public class AnimalServ extends TopServlet{

    static final long serialVersionUID = 19L;
    static Logger logger = LogManager.getLogger(AnimalServ.class);
    /**
     * Generates the animal form and processes view, add, update and delete
     * operations.
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String f_name = "", l_name="", 
	    dob="",ssn="", did="", id="";

	boolean success = true;
	//
	String message="", action="", cid="";
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	User user = null;
	HttpSession session = null;
	Address address = null;
	Defendant defendant = null;
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

	Enumeration<String> values = req.getParameterNames();
	String names[] = {"","","",""};
	String types[] = {"","","",""};
	String [] vals=null, del_id=null;
	List<Animal> pets = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("cid")) {
		cid = value;
	    }
	    else if (name.equals("name0")) {
		if(!value.equals(""))
		    names[0] = value;
	    }
	    else if (name.equals("name1")) {
		if(!value.equals(""))
		    names[1] = value;
	    }
	    else if (name.equals("name2")) {
		if(!value.equals(""))
		    names[2] = value;
	    }
	    else if (name.equals("name3")) {
		if(!value.equals(""))
		    names[3] = value;
	    }
	    else if (name.equals("pet_type0")) {
		if(!value.equals(""))
		    types[0] = value;
	    }
	    else if (name.equals("pet_type1")) {
		if(!value.equals(""))
		    types[1] = value;
	    }
	    else if (name.equals("pet_type2")) {
		if(!value.equals(""))
		    types[2] = value;
	    }
	    else if (name.equals("pet_type3")) {
		if(!value.equals(""))
		    types[3] = value;
	    }			
	    else if (name.equals("del_id")) {
		if(!value.equals(""))
		    del_id = vals;
	    }	
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	if(action.equals("Submit") && user.canEdit()){
	    //
	    String back = "";
	    int j=0;
	    for(String str: names){
		Animal pet = new Animal(cid, str, types[j], debug);
		if(pet.isValid()){
		    back += pet.doSave();
		}
		j++;
	    }
	    if(!back.equals("")){
		logger.error(back);
		message += back;
		success = false;
	    }
	    if(del_id != null){
		for(String str: del_id){
		    if(!str.equals("")){
			Animal pet = new Animal(str, debug);
			back += pet.doDelete();
		    }
		}
	    }
	}
	if(true){
	    AnimalList petsl = new AnimalList(debug, cid);
	    String back = petsl.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		pets = petsl.getAnimals();
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println(" </script>				        ");
	//
    	// delete startNew
	//
	out.println("<center><h2>Add Animals</h2>");
	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");

	}
	else{
	    if(!message.equals(""))
		out.println("<h3><font color='red'>"+message+
			    "</font></h3>");
	}
	out.println("<form name=myForm method=post>");
	if(!cid.equals(""))
	    out.println("<input type=hidden name=cid value=\""+cid+"\" >");		
	//
	out.println("<table border width=80%>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	//
	// Add/Edit record
	//
	out.println("<table>");
	if(!cid.equals("")){
	    out.println("<tr><td colspan=3 align=left><b>Case ID: </b> <a href="+url+"CaseServ?action=zoom&id="+cid+">"+cid+"</a></td></tr>");

	}
	int jj = 1;
	if(pets != null && pets.size() > 0){
	    out.println("<tr><td colspan=3>Current Animals in this Case</td></tr>");
	    out.println("<tr><th></th><th>Name</th><th>Type</th></tr>");
	    for(Animal pet: pets){
		out.println("<tr><td align=left>"+jj+" <input type=checkbox name=\"del_id\" value=\""+
			    pet.getId()+"\" /></td><td align=left>"+pet+
			    "</td><td align=left>"+pet.getType()+"</td></tr>");
		jj++;
	    }
	}
	out.println("<tr><td colspan=3>You can add four animal at a time</td></tr>");
	out.println("<tr><td></td><td> Name</td><td>Type</td></tr>");		
	for(int i=0;i<4;i++){
	    out.println("<tr><td align=left>"+(jj+i)+" </td>"+
			"<td align=left><input name=name"+i+
			" value=\"\" /></td><td align=left>");
	    out.println("<select name=\"pet_type"+i+"\">");
	    out.println(Helper.allPetTypes);
	    out.println("</select></td></tr>");
	}
	out.println("</table></td></tr>");
	//
	if(user.canEdit()){
	    out.println("<tr><td align=right><input type=submit "+
			"accessKey=s tabindex=14 name=action value=Submit>");
	    out.println("</td></tr>");
	}
	out.println("</table>");
	out.println("</form>");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































