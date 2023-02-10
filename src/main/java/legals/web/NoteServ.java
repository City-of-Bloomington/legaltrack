package legals.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;

// either not used or need fix
//
@WebServlet(urlPatterns = {"/NoteServ"})
public class NoteServ extends TopServlet{

    static final long serialVersionUID = 60L;
    static Logger logger = LogManager.getLogger(NoteServ.class);
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
    /**
     * @link #doGet
     * @see #doGet
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String action="";
	String id="", comments = "", message="";
	boolean success = true;
	String [] vals;
	Enumeration<String> values = req.getParameterNames();
	Case lcase = new Case(debug);
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
		lcase.setId(id);
	    }
	    else if (name.equals("comments")) {
		comments = value;
		lcase.setComments(value);
	    }
	    else if (name.equals("action")) {
		action = value;
	    }
	}
	//
	if(!id.equals("")){
	    if(action.equals("")){
		String back = lcase.doSelect();
		if(back.isEmpty()){
		    comments = lcase.getComments();
		}
	    }
	    else{ // update
		lcase.updateComments();
	    }
	}
	//
	out.println("<html><head><title>Case Comments</title>");
	out.println("<script type='text/javascript'>");
	out.println("  function copyDataToForm(){                         ");
	out.println("  var str = document.forms[0].comments.value;        ");
	out.println("  if(str.length > 10000){                            ");
	out.println("   alert('Entered text exceeds the max limit');      ");
	out.println("  }                                                  ");
	out.println("  else{                                               ");
        out.println(" opener.document.getElementById(\"comments\").value = str; "); 
	// out.println("     window.close();                              ");  
	out.println("    }                                             ");
	out.println("  }                                               ");
	out.println("  function doCount(){                         ");
	out.println("  var lstr = document.forms[0].comments.value.length;");
	out.println("  var rest = 10000 - lstr;                     ");
	out.println("  if(rest < 0){                            ");
	out.println("   rest = 'Max limit exceeded';             ");
	out.println("  }                                                  ");
	out.println("  else{                                               ");
	out.println("      rest = 'Remaining '+rest+' characters';         ");
        out.println("      document.getElementById('rest').firstChild.nodeValue = rest;"); 
	out.println("    }                                             ");
	out.println("  }                                               ");		
	out.println(" </script>		                               ");
	if(action.equals("") || !success){
	    out.println(" </head><body> ");
	}
	else{
	    out.println(" </head><body onload='window.close();'> ");
	}
	//
	out.println("<center><h3>Case Comments </h3>");
	if(!success){
	    out.println("<p>"+message+"</p>");
	}
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return copyDataToForm()\">");
	out.println("<input type=hidden name=id value="+id+" />");
	out.println("<table border width=\"90%\">");
	out.println("<tr><td>");
	out.println("<table>");
	out.println("<tr><td><b>Case ID:</b>"+id+"</td></tr>");
	out.println("<tr><td><b>Comments: </b><font size=-1 color=green>(Max 10,000 characters)</font>");
	out.println("<span id='rest'>&nbsp;</span>");
	out.println("<tr><td>");
	out.println("<textarea name=comments id=comments rows=25 cols=80 "+
		    "onkeyup=doCount() wrap>");
	out.println(comments);
	out.println("</textarea></td></tr>");
	out.println("<tr><td align=right>If you make any change click <input "+
		    "type=\"submit\" "+
		    "name=\"action\" value=\"Update\">");
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	out.println("</table>");
	out.println("</form>");
	out.println("<li><a href=javascript:window.close();>"+
		    "Close This Window</a>");
	out.print("</body></html>");
	out.close();
    }

}






















































