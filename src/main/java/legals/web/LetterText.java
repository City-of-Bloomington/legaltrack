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
 * Generate an email message from the input of a request.
 *
 * Not Used yet
 *
 * Gives the user the ability to modify the content of a request
 * and add more feedback before sending the request.
 *
 */
@WebServlet(urlPatterns = {"/LetterText"})
public class LetterText extends TopServlet{

    static final long serialVersionUID = 50L;
    Logger logger = LogManager.getLogger(LetterText.class);

    /**
     * Generates the mail form and then sends the email.
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
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String message="";
	String action="";
	String type = "", id="";
	String ptext = "";
	String order = "";
	int lastOrder = 0;
	boolean success = true;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	Paragraph ph = new Paragraph(debug);
	ParagraphList pl = new ParagraphList(debug);
	List<Paragraph> pls = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		ph.setId(value);
	    }
	    else if (name.equals("type")){
		ph.setType(value);
		pl.setType(value);
		type = value;
	    }
	    else if (name.equals("ptext")) {
		ph.setText(value);
	    }
	    else if (name.equals("order")) {
		ph.setOrder(value);
	    }
	    else if(name.equals("action")){
		action = value;  
	    }
	}

	User user = null;
	HttpSession session = req.getSession(false);
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
	if(action.equals("Save")){
	    String back = ph.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	if(action.startsWith("Switch")){
	    type="";
	}
	if(!type.equals("")){
	    String back = pl.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		pls = pl.getParagraphs();
	    }
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<center>");
	out.println("<h3 class=\"titleBar\">Violation Letter Editor </h3>");
	out.println("</center>");
	if(type.equals("")){
	    //
	    out.println("<form>");
	    out.println("<center><table align=center width=80% border>");
	    out.println("<tr><td>To start, Select the Violation Type</td>");
	    //
	    List<CaseType> types = null;
	    CaseTypeList ctl = new CaseTypeList(debug);
	    String back = ctl.find();
	    if(back.equals("")){
		types = ctl.getTypes();
	    }
	    out.println("<td>");
	    out.println("<select name='type'>");
	    out.println("<option value=''>Select One</option>");
	    for(CaseType typ:types){
		out.println("<option value=\""+typ.getId()+"\">"+typ.getDesc()+
			    "</option>");
	    }
	    out.println("</select></td></tr>");
	    //
	    // Add/Edit record
	    //
	    out.println("<tr><td colspan=2 align=right>");	
	    out.println("<input type=submit name=action "+
			"value=Next></td></tr>");
	    out.println("</table>");
	    out.println("</form>");
	    out.println("</div>");
	}
	else{ 
	    //
	    out.println("Please follow these notes");
	    out.println("<ul>");
	    out.println("<li>You will be entering letter texts in  paragraphs, one at a time, starting from top of the letter to the end</li>");
	    out.println("<li>You do not need to enter any related header or footer of the letter, these are added automatically.</li>");
	    out.println("<li>To ensure proper order of paragraphs in the letter we are going to give each paragraph an order starting with 1 and so on</li>");
	    out.println("<li>Set paragraph order in the letter. The first paragraph will be 1, the second 2 and so on.</li>");
	    out.println("<li>Enter the text for one paragraph at a time.</li>");
	    out.println("<li>If you wamt to embed certain items in the paragraph text such as address, name, date, etc use the format _item_ for any specific item you want to be replaced by its corresponding item in legaltrack when the letter is composed.</li>"); 
	    out.println("<li>Current predefined item are: _fee_ (violation fee), _address_ (violation address) and _inspectionDate_ (rental inspection date). We will add more if needed.</li>");
	    out.println("<li>Save, to start a new paragraph.</li>");
	    out.println("<li>If you want to edit certain paragraph click on the Edit button next to it</li>");
	    out.println("<li>To issue a violation letter, go to the related case and click on the button 'Mail Letter', it will show in PDF format that you can print and mail (it may take some time to appear).</li>");
	    out.println("</ul>");
	    out.println("<form>");
	    out.println("<input type=hidden name=type value=\""+type+"\" />");
	    out.println("<table>");
	    if(pls != null && pls.size() > 0){
		out.println("<tr><th>Order</th><th>Text</th><th>Edit</th></tr>");
		for(Paragraph pp:pls){
		    out.println("<tr><td valign=top>"+pp.getOrder()+
				"</td><td>"+pp.getText()+
				"</td><td>");
		    out.println("<input type=button "+
				" onclick=\"window.open('"+url+
				"ParaEdit?id="+pp.getId()+
				"','Paragraph','toolbar=0,location=0,"+
				"directories=0,status=0,menubar=0,"+
				"scrollbars=0,top=200,left=200,"+
				"resizable=1,width=600,height=500');return false;\""+								
				")\" value=\"Edit\">");
		    out.println("</td></tr>");
		    if(lastOrder < pp.getIntOrder()){
			lastOrder = pp.getIntOrder();
		    }
		}
	    }
	    out.println("<tr><th>Order</th><th>Text</th><th>&nbsp;</th></tr>");
	    out.println("<tr><td valign=top>"+
			"<select name=\"order\">"+
			"<option>"+(lastOrder+1)+"</option>"+
			"<option>"+(lastOrder+2)+"</option>"+
			"<option>"+(lastOrder+3)+"</option>"+
			"</select></td>");
	    out.println("<td><textarea name=ptext rows=8 cols=50 wrap>");
	    out.println("</textarea></td>");
	    out.println("<td>&nbsp;</td></tr>");
	    out.println("<tr><td colspan=3 align=right>");
	    out.println("<table><tr><td align=right>");
	    out.println("<input type=submit name=action value=Save>");
	    out.println("</td><td>");
	    out.println("<input type=submit name=action value=\"Switch to Another  Violation Type\" />");
	    out.println("</td></tr><table>");
	    out.println("</td></tr>");
	    out.println("</table>");
	    out.println("</form>");
	}
		
	out.print("</body></html>");
	out.close();
    }

}






















































