package legals.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
/**
 *
 *
 */
@WebServlet(urlPatterns = {"/ParaEdit"})
public class ParaEdit extends TopServlet{

    static final long serialVersionUID = 63L;
    static Logger logger = LogManager.getLogger(ParaEdit.class);
    /**
     *
     *
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

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String action="";
	String id="", message="";
	boolean success = true;
	String [] vals;
	Enumeration<String> values = req.getParameterNames();
	Paragraph ph = new Paragraph(debug);
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		ph.setId(value);
		id = value;
	    }
	    else if (name.equals("ptext")) {
		ph.setText(value);
	    }
	    else if (name.equals("order")) {
		ph.setOrder(value);
	    }
	    else if (name.equals("type")) {
		ph.setType(value);
	    }
	    else if (name.equals("action")) {
		action = value;
	    }
	}
	//
	if(action.equals("Update")){
	    String back = ph.doUpdate();
	    if(!back.equals("")){
		message += back;
	    }	
	}
	if(action.equals("Delete")){
	    String back = ph.doDelete();
	    if(!back.equals("")){
		message += back;
	    }	
	}
	else if(!id.equals("")){
	    String back = ph.doSelect();
	    if(!back.equals("")){
		message += back;
	    }
	}
	//
	out.println("<html><head><title>Paragraph Editor</title>");
	out.println("<script type='text/javascript'>");
	out.println(" function makeSure(){          ");
	out.println(" var x=confirm('Are you sure you want to delete?');");
	out.println(" return x;}                    ");
	out.println(" </script>		                               ");
	if(action.equals("") || !success){
	    out.println(" </head><body> ");
	}
	else{
	    out.println(" </head><body onload='window.close();opener.window.location.reload();'> ");
	}
	//
	out.println("<center><h3>Paragraph Text </h3>");
	if(!success){
	    out.println("<p>"+message+"</p>");
	}
	out.println("<form name=myForm method=post>");
	out.println("<input type=hidden name=id value=\""+ph.getId()+"\" />");
	out.println("<input type=hidden name=type value=\""+ph.getType()+"\" />");
	out.println("<table border width=\"90%\">");
	out.println("<tr><td>");
	out.println("<table>");
	out.println("<tr><td><b>Paragraph ID:</b>"+id+"</td></tr>");
	out.println("<tr><td><b>Paragraph Order:</b>");
	out.println("<select name=order>");
	for(int i=1;i<10;i++){
	    if(i == ph.getIntOrder()){
		out.println("<option selected>"+i+"</option>");
	    }
	    else{
		out.println("<option>"+i+"</option>");
	    }
	}
	out.println("</select></td></tr>");	
	out.println("<tr><td><b>Paragraph Text:</b></td></tr>");
	out.println("<tr><td>");
	out.println("<textarea name=ptext id=ptext rows=10 cols=60 "+
		    "wrap>");
	out.println(ph.getText());
	out.println("</textarea></td></tr>");
	out.println("<tr><td align=right>");	
	out.println("<table><tr><td>If you make any change click <input "+
		    "type=\"submit\" "+
		    "name=\"action\" value=\"Update\" />");
	out.println("<td align=right> "+
		    "<input type=\"submit\" "+
		    "onclick=\" return makeSure();\" "+
		    "name=\"action\" value=\"Delete\" />");		
	out.println("</td></tr></table></td></tr>");
	out.println("</table></td></tr>");
	out.println("</table>");
	out.println("</form>");
	out.println("<li><a href=javascript:window.close();>"+
		    "Close This Window</a></li>");
	out.print("</body></html>");
	out.close();
    }

}






















































