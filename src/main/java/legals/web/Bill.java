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

@WebServlet(urlPatterns = {"/Bill","/bill"})
public class Bill extends TopServlet{

    static final long serialVersionUID = 84L;
    static Logger logger = LogManager.getLogger(Bill.class);
    static final String [] payMethod = {
	"",
	"Cash",
	"Check",
	"M.O.",
	"C.C.",
	"B.C.",
	"Other"
    };

    /**
     * Generates the payment form and process it.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String action="";
	double amount = 0, balance=0, total_paid=0, total_due=0;
	String amountStr = "0", balanceStr="0", receipt_date="";
	String paid_date="",paid_method="",check_no="",clerk="";
	String paid_by="",pid="",id="", message="",cause_num="";
	boolean success = true;
	Enumeration<String> values = req.getParameterNames();
	HttpSession session = null;
	session = req.getSession(false);
	out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
	String [] vals;
	Payment pay = new Payment(debug);
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("amount")) {
		if(!value.equals("")){				
		    pay.setAmount(value);
		}
	    }
	    else if (name.equals("clerk")) {
		pay.setClerk(value);
	    }
	    else if (name.equals("paid_method")) {
		pay.setPaid_method(value);
	    }
	    else if (name.equals("paid_by")) {
		pay.setPaid_by(value);
	    }
	    else if (name.equals("paid_date")) {
		pay.setPaid_date(value);
	    }
	    else if (name.equals("check_no")) {
		pay.setCheck_no(value);
	    }
	    else if (name.equals("paidFor")) {
		pay.setPaidFor(value);
	    }	
	    else if (name.equals("id")) {
		id = value;
		pay.setId(value);
	    }
	    else if (name.equals("pid")) {
		pid = value;
		pay.setPid(value);
	    }
	    else if (name.equals("action")){ 
		// bill, change pay status
		action = value;  
	    }
	}
	User user = null;
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
	//
	// we need these for default values of dates
	Calendar current_cal = Calendar.getInstance();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	String today = ""+mm+"/"+dd+"/"+yyyy;
	//
	if(paid_date.equals(""))
	    paid_date = today;
	if(action.equals("Save")){
	    //
	    if(user.canEdit()){
		String back = pay.doSave();
		if(!back.equals("")){
		    message += back;
		    success = false;
		    logger.error(back);
		}
		else{
		    message += "Save successfully";
		    pid = pay.getPid();
		}
	    }
	}
	else if(action.equals("Update")){
	    //
	    if(user.canEdit()){
		String back = pay.doUpdate();
		if(!back.equals("")){
		    message += back;
		    success = false;
		    logger.error(back);
		}
		else{
		    message += "Update successfully";
		}
	    }
	}
	else if(action.equals("zoom") || action.startsWith("Print")){
	    //
	    String back = pay.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
		logger.error(back);
	    }
	}
	else if(action.equals("Delete") && user.isAdmin()){
	    //
	    if(user.isAdmin()){
		String back = pay.doDelete();
		if(!back.equals("")){
		    message += back;
		    success = false;
		    logger.error(back);
		}
		else{
		    pay = new Payment(debug);
		    pay.setId(id);
		    pid = "";
		    message += "Deleted successfully";
		}
	    }
	}
	else if(action.startsWith("New")){
	    pay = new Payment(debug); // we do not want pid
	    pay.setId(id);
	    pid = "";
	}
	pay.compute(); // for the balance and others

	//
	// Check the balance and get the static info
	//
	if(!pay.getClerk().equals("")) clerk = "checked";

	if(!action.startsWith("Print")){
	    out.println(Inserts.xhtmlHeaderInc);
	    out.println(Inserts.banner(url));
	    out.println(Inserts.menuBar(url, true));
	    out.println(Inserts.sideBar(url, user));
	    out.println("<div id=\"mainContent\">");
	    out.println("<script type=\"text/javascript\">     ");
	    out.println("   function formatNumber(xx){                      ");
	    out.println("     var  x = new String(xx);                      "); 
	    out.println("     var  n = indexOf(x,\".\");                    ");
	    out.println("      if(n > -1){                                  ");
	    out.println("       var  l = x.length;                          ");
	    out.println("       var  r = n*1+3*1;                           ");
	    out.println("        if( r < l ){                               ");
	    out.println("        var y = x.substring(0,r);                  ");
	    out.println("        return y;                                  ");
	    out.println("          }                                        ");
	    out.println("        }                                          ");
	    out.println("    return x;                                      ");
	    out.println("     }                                             ");
	    out.println("   function indexOf(xx,s){                         ");
	    out.println("       var  l = xx.length;                         ");
	    out.println("       var o = -1;                                 ");
	    out.println("     for(var i=0; i<l; i++){                       ");
	    out.println("        var c = xx.charAt(i);                      ");
	    out.println("       if(c == s) o = i;                           ");
	    out.println("      }                                            ");
	    out.println("    return o;                                      ");
	    out.println("   }                                               ");
	    out.println("  function validateForm(formObj){	                ");
	    out.println("  with(formObj){                           ");
	    //
	    // checking dates and numeric values
	    // check the numbers
	    //
	    out.println("if(!checkDate(paid_date))return false;     ");
	    out.println(" }                                         ");
	    out.println(" if(!checkNumber(document.myForm.amount)){ ");
	    out.println("   return false;}			        ");
	    // 
	    // Everything Ok
	    out.println("  return true;			            ");
	    out.println(" }	         		                ");
	    out.println("  function computeBalance(formNo){ ");
	    out.println("  var form = eval('document.myform'+formNo); ");
	    out.println("  var a = form.amount.value;            ");
	    out.println("  var b = form.prev_balance.value;      ");
	    out.println("  if(a.length > 0 && b.length > 0){                 ");
	    out.println("  if(isNaN(a)){                                     ");
	    out.println("    alert(a+\" Not a valid number\");               ");
	    out.println("    form.amount.focus();                            ");
	    out.println("  return; }                                         ");
	    out.println("  if(isNaN(b)){                                     ");
	    out.println("    alert(b+\" Not a valid number\");               ");
	    out.println("  return; }                                         ");
	    out.println("  var c = b*1-a*1;                                  ");
	    out.println("    document.getElementById('balance'+formNo).innerHTML='$'+formatNumber(c);      ");
	    out.println("  }}                                               ");
	    out.println(" </script>				                             ");
	    //
	    // show any error message
	    //
	    if(!message.equals("")){
		if(!success)
		    out.println("<h3><font color=red>"+message+"</font></h3>");
	    }
	}
	if(action.startsWith("Print")){
	    //
	    out.println("<html><head><title>Receipt</title></head><body>");
	    out.println("<center>");
	    out.println("<img src=\""+url+"images/citylogo.gif\" "+
			" width=70 height=70 alt=\"City Logo\"><br>");
	    out.println("<h2>RECEIPT</h2>");
	    out.println("<h3>CITY OF BLOOMINGTON, IN<br><i>"+
			"LEGAL DEPARTMENT</i></h3>");
	    out.println("<table width=80%>");
	    out.println("<tr><td align=right><b>Receipt No. </b></td>"+
			"<td align=left>"+pay.getPid()+"</td></tr>");
	    out.println("<tr><td align=right><b>Receipt Date: </b></td>"+
			"<td align=left>"+pay.getReceipt_date()+"</td></tr>");
	    out.println("<tr><td align=right><b>Received From: </b></td>"+
			"<td align=left>"+pay.getPaid_by()+"</td></tr>");
						
	    out.println("<tr><td align=right><b>The Sum of: </b></td>"+
			"<td align=left>$"+pay.getAmount()+"</td></tr>");
	    if(!pay.getPaidFor().equals("")){
		out.println("<tr><td align=right><b>Paid for: </b></td>"+
			    "<td align=left>"+pay.getPaidFor()+"</td></tr>");
	    }
	    out.println("<tr><td align=right><b>Cause Number: </b></td>"+
			"<td align=left>"+pay.getCause_num()+"</td></tr>");
	    out.println("<tr><td align=right><b>Payment by: </b></td>"+
			"<td align=left>"+pay.getPaid_method()+"</td></tr>");
	    if(!pay.getCheck_no().equals("")){
		out.println("<tr><td align=right><b>Check Number: </b></td>"+
			    "<td align=left>"+pay.getCheck_no()+"</td></tr>");
	    }
	    if(!pay.getClerk().equals("") || !pay.getMcc_flag().equals("")){
		out.println("<tr><td align=right><b>MCC </b></td>");
		out.println("<td><form><input type=checkbox checked></form>");
		out.println("</td></tr>");
	    }
	    out.println("</table>");
	    out.println("<br><br>");
	    out.println("<b>Approved by the State Board of Accounts, "+
			"2004.<b>");
	    out.println("<br><br>");
	    out.println("<font size=+1>Thank you for your payment</font><br>");
	    out.println("</body></html>");
	}
	else{
	    //
	    // Add/Edit record
	    //
	    Payment payc = new Payment(debug, null, id);
	    Payment payf = new Payment(debug, null, id);
	    payf.setPaidFor("Fine");
	    payf.doSelect();
	    payf.compute();
	    payc.setPaidFor("Court");
	    payf.doSelect();
	    payc.compute();
	    out.println("<table width=95% border>");
	    out.println("<tr><td width=60%>");
	    printPayForm(out, payf, user, "1");
	    out.println("</td><td align=center>");
	    List<Payment> pays = null;
	    PaymentList paysl = new PaymentList(debug, id);
	    String back = paysl.find();
	    if(!back.equals("")){
		logger.error(back);
	    }
	    else{
		pays = paysl.getPayments();
		printPayments(out, pays, user);
		out.println("</td></tr>");		
	    }
	    if(payc.hasCourtCost()){
		out.println("<tr><td width=60%>");
		printPayForm(out, payc, user, "2");
		out.println("</td><td align=center>");
		paysl = new PaymentList(debug, id);
		paysl.setPaidFor("Court");
		back = paysl.find();
		if(!back.equals("")){
		    logger.error(back);
		}
		else{
		    pays = paysl.getPayments();
		    printPayments(out, pays, user);
		    out.println("</td></tr>");
		}
	    }
	    out.println("<tr><td align=left><b>Total Balance (Fines & Court):</b> $"+pay.getTotalBalance()+"</td><td>&nbsp;</td></tr>");
	    out.println("</table>");
	    out.println("<center><a href=\""+url+"CaseServ?id="+id+
			"&action=zoom\">"+
			"Back to Case "+id+"</a></center><br><br>");		
	}
	out.println("</div></body>");
	out.println("</html>");			
    }

    /**
     * @see doPost.
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
    void printPayForm(PrintWriter out, Payment pay, User user, String formNo){

	String clerk = "";
	String id = pay.getId();
	String pid = pay.getPid();
	out.println("<form method=post name=\"myform"+formNo+"\" " +
		    "onsubmit=\"return validateForm(this)\">");
	out.println("<input type=hidden name=\"id\" value=\""+id+"\" />");
	if(!pay.getPaidFor().equals("Fine")){				
	    out.println("<input type=hidden name=\"prev_balance\" value=\""+
			pay.getCourtBalance()+"\" />");
	}
	else{
	    out.println("<input type=hidden name=\"prev_balance\" value=\""+
			pay.getFineBalance()+"\" />");
	}
	if(!pay.getPaidFor().equals("")){
	    out.println("<input type=hidden name=\"paidFor\" value=\""+
			pay.getPaidFor()+"\" />");
	}
	if(!pid.equals("")){
	    out.println("<input type=hidden name=pid value="+pid+">");
	}	
	out.println("<table width=100%>");
	String title = pay.getPaidFor();
	if(title.equals("")) title = "Fine";
	if(pid.equals(""))
	    title = " New "+title;
	else
	    title = "View/Edit "+title;
	if(title.endsWith("Court")) title += " Cost "; 
	out.println("<caption>"+title+" Payment</caption>");
	if(!pid.equals("")){
	    out.println("<tr><th>Receipt #:</th><td align=left> "+pid+"</td></tr>");
	}
	out.println("<tr><th>Receipt date:</th><td align=left>");
	out.println("<input name=paid_date size=10 "+
		    "maxlength=10 value=\""+(!pay.getPaid_date().equals("")?pay.getPaid_date():Helper.getToday())+"\"></td></tr>");
	out.println("<tr><th>Paid by: </th><td align=left>");
	out.println("<input name=paid_by size=30 "+
		    "maxlength=50 value=\""+pay.getPaid_by()+"\" /></td></tr>");
	//
	out.println("<tr><th>Amount Paid: </th><td align=left>");
	out.println("<input name=amount size=8 "+
		    " onChange=\"computeBalance("+formNo+");\" "+
		    "maxlength=8 value=\""+pay.getAmount()+"\" /></td></tr>");
	//
	if(!pay.getPaidFor().equals("Court")){
	    out.println("<tr><th>Fine: </th>");
	    out.println("<td align=left>$"+pay.getFine()+"</td></tr>");						
	}
	else{
	    out.println("<tr><th>Court Cost: </th>");
	    out.println("<td align=left>$"+pay.getCourtCost()+"</td></tr>");
	}
	//
	out.println("<tr><th>Current Balance: </th><td align=left>");
	if(!pay.getPaidFor().equals("Fine")){				
	    out.println("<div id=\"balance"+formNo+"\">$"+
			pay.getCourtBalance()+"</div></td></tr>");
	}
	else{
	    out.println("<div id=\"balance"+formNo+"\">$"+
			pay.getFineBalance()+"</div></td></tr>");
	}				
	//
	out.println("<tr><th>Payment Method: </th><td align=left>");
	out.println("<select name=paid_method>");
	for(int i=0;i<payMethod.length;i++){
	    if(payMethod[i].equals(pay.getPaid_method()))
		out.println("<option selected>"+payMethod[i]);
	    else
		out.println("<option>"+payMethod[i]);
	}
	out.println("</select></td><td>");
	out.println("<tr><th>Check No.:</th><td align=left> ");
	out.println("<input name=check_no size=15 "+
		    "maxlength=15 value=\""+pay.getCheck_no()+"\"></td></tr>");
	out.println("<tr><th>MCC:</th><td> ");
	if(!pay.getMcc_flag().equals("") || !pay.getClerk().equals("")) clerk ="checked=\"checked\"";
	out.println("<input type=checkbox name=clerk  "+
		    clerk+" value=\"y\"></td></tr>");
	if(pid.equals("")){ 
	    out.println("<tr><td colspan=2 align=center>");
	    if(user.canEdit()){
		out.println("<input type=submit name=action value=Save>");
	    }
	    out.println("</td></tr>"); 
	    out.println("</form>");
	}
	else{ // save, update
	    out.println("<tr><td colspan=2 "+
			" valign=top align=right><table width=60%>");
	    if(user.canEdit()){
		out.println("<td valign=top><input "+
			    "type=submit name=action value=Update>");
		out.println("</td>");
	    }
	    out.println("<td valign=top><input "+
			"type=submit name=action value='New Payment'>");
	    out.println("</td>");				
	    out.println("<td valign=top>");
	    out.println("<input type=\"button\" name=\"action\" "+
			"value=\"Printable\" onclick=\"window.open('"+url+
			"Bill?id="+id+"&pid="+pid+"&action=Printable');return false;\" /> ");
	    out.println("</td></form>");
	    out.println("<td valign=top>");
	    out.println("</td></form>");
	    //
	    // This option is not needed, it will never be used
	    //
	    if(user.isAdmin()){ 
		out.println("<td><form name=myform onSubmit=\"return "+
			    "validateDelete();\">");
		out.println("<input type=hidden name=id value="+id+">");
		out.println("<input type=hidden name=pid value="+pid+">");
		out.println("<input type=submit name=action "+
			    "value=Delete>");
		out.println("</form></td>");
	    }
	    out.println("</tr></table></td></tr>");
	}
	out.println("</table>");
    }
    void printPayments(PrintWriter out, List<Payment> pays, User user){

	if(pays == null || pays.size() == 0){
	    out.println("<p>No payment paid yet</p>");
	    return;
	}
	out.println("<table width=100%><tr><th>Payment</th>"+
		    "<th>Amount</th><th>Date</th></tr>");
	out.println("<caption>Payment History</caption>");
	for(Payment pp: pays){
	    out.println("<tr><td align=left>");
	    if(user.hasRole("Edit")){ 
		out.println("<a href=\""+url+"Bill?id="+pp.getId()+
			    "&pid="+pp.getPid()+
			    "&action=Print\">"+
			    pp.getPid()+"</a>");
	    }
	    else{
		out.println(pp.getPid());
	    }
	    out.println("</td>");
	    String str = pp.getAmount();
	    if(str == null) str = "&nbsp;";
	    out.println("<td align=left>$"+str+"</td>");
	    str = pp.getPaid_date();
	    if(str == null) str = "&nbsp;";
	    out.println("<td align=left>"+str+"</td></tr>");
	}
	out.println("</table>");
    }
}























































