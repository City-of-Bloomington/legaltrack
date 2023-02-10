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
 * search for rental legal cases
 *
 *
 */
@WebServlet(urlPatterns = {"/SearchLegalServ"})
public class SearchLegalServ extends TopServlet{

    static final long serialVersionUID = 69L;
    static Logger logger = LogManager.getLogger(SearchLegalServ.class);
	
    /**
     *
     * The user can check a selection of these and clicks the add defendents
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    
    /**
     * @link Case#doGet
     * @see #doGet
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	boolean showAll=false;
	boolean success = true;

	String action="", message ="";

	String  dateFrom="", dateTo="", whichDate="", id="",rental_id="",
	    status="", startBy="", reason="", case_id="";
	
	Enumeration<String> values = req.getParameterNames();
	List<String> starters = null;
	String [] vals;

	
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
	    }
	    else if (name.equals("status")) {
		status =value;
	    }
	    else if (name.equals("startBy")) {
		startBy =value;
	    }
	    else if (name.equals("reason")) {
		reason =value;
	    }
	    else if (name.equals("dateFrom")) {
		dateFrom = value;
	    }
	    else if (name.equals("dateTo")) {
		dateTo = value;
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
	LegalList ll = new LegalList(debug);
	String back = ll.findStarters();
	if(!back.equals("")){
	    message += back;
	    success = false;
	}
	else{
	    starters = ll.getStarters();
	}
	// Inserts
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar2(url));
	out.println("<div id=\"mainContent\">");
	out.println("<h3 class=\"titleBar\">Search Legals</h3>");
	//
	if(success){
	    if(!message.equals(""))
		out.println("<h2>"+message+"</h2>");
	}
	else{
	    if(!message.equals(""))
		out.println("<h2 class=\"errorMessages\">"+message+"</h2>");
	}
	out.println("<form name=\"myForm\" method=\"post\" action='"+url+
		    "LegalListServ'>");
	//
	// 1st block
	out.println("<fieldset><legend>Search</legend>");
	out.println("<table>");
	out.println("<tr><td><label for=\"id\">Legal ID:</label>");
	out.println("<input name='id' id='id' value='"+id+"'"+
		    " size='8' maxlength='8' />");
	out.println("<label for=\"case_id\">Case ID:</label>");
	out.println("<input name='case_id' id='case_id' value='"+
		    case_id+"'"+
		    " size='8' maxlength='8' /></td></tr>");
	out.println("<tr><td>");
	out.println("<label for=\"rental_id\">Rental ID:</label>");
	out.println("<input name='rental_id' id='rental_id' value='"+
		    rental_id+"'"+
		    " size='8' maxlength='8' />(from HAND RentPro)");
	out.println("</td></tr>");
	out.println("<tr><td>");
	out.println("<label for=\"status\"> Status:</label>"+
		    "<select name=\"status\" id=\"status\">");
	for(int i=0;i<Inserts.searchStatusArr.length;i++){
	    if(status.equals(Inserts.searchStatusArr[i]))
		out.println("<option selected=\"selected\">"+status+
			    "</option>");
	    else
		out.println("<option>"+Inserts.searchStatusArr[i]+"</option>");
	}
	out.println("</select>");
	out.println("</td></tr>");
	out.println("<tr><td>");
	out.println("<label>Case Started By:</label>"+
		    "<select name='startBy' id='startBy'>");
	out.println("<option selected='selected'>\n</option>");
	if(starters != null){
	    for(String starter: starters){
		out.println("<option>"+starter+"</option>");
	    }
	}
	out.println("</select></td></tr>");
	out.println("<tr><td><label>Last Attention:</label>");
	out.println("<input type='radio' name='attention' value='Legal' "+
		    " />Legal Dept.");
	out.println("<input type='radio' name='attention' value='HAND' "+
		    " />HAND Dept.");
	out.println("</td></tr>");
	out.println("<tr><td><label for='reason'>Reasons:</label>");
	out.println("<input name='rason' id='reason' value='"+reason+"'"+
		    " size='40' maxlength='70' />(use key words)");
	out.println("</td></tr>");
		
	out.println("<tr><td><label>Which Date? </label>");
	out.println("<input type='radio' name='whichDate' checked "+
		    "value='startDate'>Started Date </input>");
	out.println("<input type='radio' name='whichDate' "+
		    "value='actionDate'>Action Date </input>");
	out.println("</td></tr>");
	//
	out.println("<tr><td><table>");
	out.println("<tr><td>&nbsp;</td><td>(mm/dd/yyyy)</td><td>&nbsp;</td><td>(mm/dd/yyyy)</td></tr>");
		
	out.println("<tr><td><label for='dateFrom'>Date, from:</label></td><td>");
	out.println("<input name='dateFrom' size='10' class=\"date\" "+
		    "id='dateFrom' value='"+dateFrom+"' /></td><td>");
	out.println("<label for='dateTo'> to:</label></td><td>");
	out.println("<input name='dateTo' size='10' class=\"date\" "+
		    "id='dateTo' value='"+dateTo+"' /></td></tr>");
	out.println("</table></td></tr>");
	//
	out.println("</table>");
	out.println("</fieldset>");
	out.println("<fieldset>");
	out.println("<table class='control'>");
	out.println("<tr><td>");
	out.println("<input type='submit' "+
		    "name='action' value='Search' />");
	out.println("</td></tr></table>");
	out.println("</fieldset>");
	out.println("</form>");
	out.println(Inserts.jsStrings(url));	
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");
	out.println("</div>");
	out.println("</body></html>");
	out.close();

    }
	
}






















































