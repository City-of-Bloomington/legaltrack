package legals.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.json.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;



@WebServlet(urlPatterns = {"/LegalAndCaseServ","/LegalAndCase.do"})
public class LegalAndCaseServ extends TopServlet{

    static final String emailStr = "@bloomington.in.gov";		
    static final long serialVersionUID = 71L;
    static Logger logger = LogManager.getLogger(LegalAndCaseServ.class);
    /**
     * Generates the varince form and processes view, add, update and delete
     * operations.
     *
     * @param req
     * @param res
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
    
	res.setContentType("text/text");
	PrintWriter out = res.getWriter();
	String name, value;
	String action="",reason="",startDate="",status="",
	    startBy="", startByName="", jsonData="",
	    rental_id="", case_type="", case_status="", legal_id="",
	    pull_date="", pull_reason="",
	    addresses = "", // for email
	    attention="Legal"; // from HAND to Legal (default)
	//
		
	Enumeration<String> values = req.getParameterNames();
	String[] vals;

	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("jsonLegal")) {
		jsonData = value;
		System.err.println(name+" "+value);
	    }
	    else{
		System.err.println(name+" "+value);
	    }
	}
	if(!jsonData.equals("")){
	    JSONObject jObj = new JSONObject(jsonData);
	    if(jObj.has("rental_id")){
		rental_id = jObj.getString("rental_id");
	    }
	    if(jObj.has("startBy")){
		startBy = jObj.getString("startBy");
	    }
	    if(jObj.has("startDate")){
		startDate = jObj.getString("startDate");
	    }						
	    if(jObj.has("attention")){
		attention = jObj.getString("attention");
	    }						
	    if(jObj.has("case_type")){
		case_type = jObj.getString("case_type");
	    }
	    if(jObj.has("status")){
		status = jObj.getString("status");
	    }
	    if(jObj.has("reason")){
		reason = jObj.getString("reason");
	    }
	    if(jObj.has("pull_date")){
		pull_date = jObj.getString("pull_date");
	    }						
	    if(jObj.has("pull_reason")){
		pull_reason = jObj.getString("pull_reason");
	    }
	    if(jObj.has("case_status")){
		case_status = jObj.getString("case_status");
	    }						
	    if(jObj.has("action")){
		action = jObj.getString("action");
	    }
	    Case cCase = new Case(debug, case_type, case_status, startDate, "Start legal from rental ");
	    String back = cCase.doSave();
	    String case_id = cCase.getId();
	    Legal legal = new Legal(debug,
				    case_id,
				    rental_id,
				    startBy,
				    reason,
				    status,
				    startDate,
				    attention,
				    pull_date,
				    pull_reason);
	    back = legal.doSave();
	    legal_id = legal.getId();
	    System.err.println(" save legal id "+legal_id);
	    System.err.println(" rid "+rental_id);
	    System.err.println(" startBy "+startBy);
	    System.err.println(" date "+startDate);
	    System.err.println(" status "+status);
	    System.err.println(" case_status "+case_status);
	    System.err.println(" attention "+attention);
	    System.err.println(" reason "+reason);
	    System.err.println(" case_type "+case_type);
	    System.err.println(" case_id "+case_id);						
	    System.err.println(" action "+action);
	    if(jObj.has("owners")){
		JSONArray defArr = jObj.getJSONArray("owners");
		for (int i = 0; i < defArr.length(); i++) {
		    JSONObject dObj = defArr.getJSONObject(i);
		    String def_name="", address="", city="", state="",
			zip="", phone="", phone_2="", email="";
		    if(dObj.has("name")){
			def_name = dObj.getString("name");
		    }
		    if(dObj.has("address")){
			address = dObj.getString("address");
		    }
		    if(dObj.has("city")){
			city = dObj.getString("city");
		    }
		    if(dObj.has("state")){
			state = dObj.getString("state");
		    }
		    if(dObj.has("zip")){
			zip = dObj.getString("zip");
		    }
		    if(dObj.has("phone")){
			phone = dObj.getString("phone");
		    }
		    if(dObj.has("phone_2")){
			phone_2 = dObj.getString("phone_2");
		    }
		    if(dObj.has("email")){
			email = dObj.getString("email");
		    }										
		    // save defendant
		    System.err.println("def name "+def_name);
		    System.err.println("def address "+address);
		    System.err.println("def city "+city);
		    System.err.println("def state "+state);
		    System.err.println("def zip "+zip);
		    System.err.println("def phone "+phone);
		    System.err.println("def phone_2 "+phone_2);
		    System.err.println("def email "+email);
		    Defendant deff = new Defendant(debug, def_name, phone, phone_2,email);
		    back = deff.doSave();
		    String defId = deff.getDid();
		    System.err.println(" def save id "+defId);
		    back += deff.addCaseToDefendant(case_id);
		    DefAddress defAdr = new DefAddress(debug,
						       defId,
						       address,
						       city,
						       state,
						       zip);
		    back += defAdr.doSave();
		    System.err.println(" def addr save id "+defAdr.getId());
		}
	    }
	    if(jObj.has("addresses")){
		JSONArray addrArr = jObj.getJSONArray("addresses");
		for (int i = 0; i < addrArr.length(); i++) {								
		    JSONObject adObj = addrArr.getJSONObject(i);
		    String address="", invalid_addr="";
		    boolean invalid=false;
		    if(adObj.has("street_address")){
			address = adObj.getString("street_address");
			if(!addresses.equals("")) addresses +=", ";
			addresses += address;
		    }
		    if(adObj.has("invalid")){
			invalid = adObj.getBoolean("invalid");
			if(invalid) invalid_addr="y";
		    }
		    Address addr = new Address(debug, case_id, address, "y", invalid_addr);
		    back = addr.doSave();
		    System.err.println(" addr "+address+" "+invalid);
		    System.err.println(" save addr "+back);
		}
	    }
	    //
	    // send the email
	    //
	    if(activeMail){
		String msg = " For your information "+
		    "\n Rental record ID : "+rental_id+
		    "\n related Legal Action ID: "+legal_id +
		    "\n a new action was added today by "+startBy+
		    "\n The reason for the legal action was "+reason+
		    "\n ";
		if(!addresses.equals("")){
		    msg += " Rental address(es): "+addresses+"\n";
		}
		String subject = "Legal Actions: ";
		if(!reason.equals("")){
		    if(reason.length() > 30)
			subject += reason.substring(0,30);
		    else
			subject += reason;
		}
		//
		String[] legalContArr = null;
		if(legalMail.indexOf(",") > -1){
		    legalContArr = legalMail.split(",");
		}
		String email = "", cc="";
		if(legalContArr != null && legalContArr.length > 1){
		    email = legalContArr[0].trim()+emailStr;
		    for(int i=1;i<legalContArr.length;i++){
			if(!cc.equals("")) cc +=",";
			cc += legalContArr[i].trim()+emailStr;
		    }
		}
		else{
		    email = legalMail+emailStr;
		}
		if(cc.equals("")) cc = null;
		String from = "";
		if(!startBy.equals("")){
		    from = startBy+emailStr;
		}
		if(!email.equals("")){
		    MsgMail mgm = 
			new MsgMail(email, // to
				    from,//from
				    subject,
				    msg,
				    cc);
		    back = mgm.doSend();
		}
	    }
	}
	out.println("legal_id='"+legal_id+"'\n");
	out.flush();
	out.close();
    }

}





















































