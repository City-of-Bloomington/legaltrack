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

@WebServlet(urlPatterns = {"/AnimalPop"})
public class AnimalPop extends TopServlet{

    static final long serialVersionUID = 18L;
    static Logger logger = LogManager.getLogger(AnimalPop.class);
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
	Enumeration<String> values = req.getParameterNames();
	String names[] = {"","","",""};
	String types[] = {"","","",""};
	String [] vals=null, del_id=null;
	List<Animal> pets = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    // value = Helper.replaceSpecialChars(value);
	    if (name.equals("action")){ 
		action = value;  
	    }
	}
	out.println("<html><head><title>Case Comments</title>");
		
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("  function copyData(){                         ");
	out.println("  var pets = '';                                ");
	out.println("   pets = opener.document.getElementById('animals').value;");
	out.println("   with(document.forms[0]){                    ");
	out.println("     for(var i=0;i<5;i++){                      ");
	out.println("       var vl = eval('pet_name'+i).value;    ");
	out.println("       var ob = eval('pet_type'+i);    ");
	out.println("       var tp = ob.options[ob.selectedIndex].value;");
	out.println("       if(vl != ''){                 ");
	out.println("          if(pets != ''){ pets += ', ';}       ");		
	out.println("          pets += vl+':'+tp;                 ");
	out.println("       }                                        ");
	out.println("    }}                                        ");
	out.println("    if(pets != ''){                 ");
        out.println("     opener.document.getElementById('animals').value = pets; ");
        out.println("     opener.document.getElementById('newAnimals').innerHTML = 'New animals: '+pets; ");		
	out.println("    }                                        ");
	out.println("    window.close();                        ");
	out.println("  }                                        ");
	out.println(" </script>				        ");
	out.println(" </head> ");
	if(action.equals("") || !success){
	    out.println(" <body> ");
	}
	else{
	    out.println(" <body onload='window.close();'> ");
	}		
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
	out.println("<form name=myForm method=post "+
		    "onsubmit=\"return copyData()\">");
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
	    out.println("<tr><td colspan=3><b>Case ID: </b> <a href="+url+"CaseServ?action=zoom&id="+cid+">"+cid+"</a></td></tr>");

	}
	out.println("<tr><td colspan=3>You can add four animal at a time</td></tr>");
	out.println("<tr><td></td><td> Name</td><td>Type</td></tr>");		
	for(int i=0;i<5;i++){
	    out.println("<tr><td>"+(i+1)+" </td><td><input name=\"pet_name"+i+"\""+
			" value=\"\" /></td><td>");
	    out.println("<select name=\"pet_type"+i+"\">");
	    out.println(Helper.allPetTypes);
	    out.println("</select></td></tr>");
	}
	out.println("</table></td></tr>");
	//
	out.println("<tr><td align=right><input type=submit "+
		    "accessKey=s name=action value=Submit>");
	out.println("</td></tr>");
	out.println("</table>");
	out.println("</form>");
	out.println("<li><a href=javascript:window.close();>"+
		    "Close This Window</a>");		
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}






















































