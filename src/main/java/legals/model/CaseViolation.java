package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import javax.sql.*;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
/**
 *
 *
 */

public class CaseViolation {


    boolean debug = false;
    static final long serialVersionUID = 29L;
    static Logger logger = LogManager.getLogger(CaseViolation.class);

    String vid = "", sid="", id="", ident="", dates="", amount="", entered="";
    String citations = "", cid="";
    ViolSubcat violSubcat = null;
    ViolCat violCat = null;
    public CaseViolation(boolean val){
	debug = val;
    }
	
    public CaseViolation(String val, boolean deb){
	vid = val;
	debug = deb;
    }
    public CaseViolation(String val,
			 String val2,
			 String val3,
			 String val4,
			 String val5,
			 String val6,
			 String val7,
			 String val8,
			 String val9,
			 boolean deb){
	vid = val;
	id = val2;
	setSid(val3);
	setIdent(val4);
	setDates(val5);
	setAmount(val6);
	setEntered(val7);
	setCitations(val8);
	setCid(val9);
	debug = deb;
    }
    //
    //setters
    //
    public void setVid(String val){
	if(val != null)
	    vid = val;
    }
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setSid(String val){
	if(val != null)
	    sid = val;
    }
    public void setCid(String val){
	if(val != null)
	    cid = val;
    }	
    public void setIdent(String val){
	if(val != null)
	    ident = val;
    }
    public void setDates(String val){
	if(val != null)
	    dates = val;
    }
    public void setAmount(String val){
	if(val != null)
	    amount = val;
    }
    public void setEntered(String val){
	if(val != null)
	    entered = val;
    }
    public void setCitations(String val){
	if(val != null)
	    citations = val;
    }	
    //
    // getters
    //
    public String getVid(){
	return vid ;
    }
    public String getId(){
	return id ;
    }
    public String getSid(){
	return sid ;
    }
    public String getCid(){
	// for the old violation cid was in subcat
	// therefore we check to see if this is an old violation
	// if so we get cid from it
	if(violSubcat == null){
	    getViolSubcat();
	}
	if(violSubcat != null){
	    cid = violSubcat.getCid();
	}
	return cid ;
    }	
    public String getIdent(){
	return ident;
    }
    public String cleanDate(String date){
				
	// Jan, Feb, March, Apr, May, June, July, Aug, Sept, Oct, Nov, Dec
	String date2 = date; // if no issue we just return
	// if we have "." in the date we removed
	date2 = date;
	// if we have "." in the date we removed
	if(date.indexOf(".") > -1){ // avoid Jan. 14, 2011
	    date2 = date.replaceAll(Pattern.quote("."),"");
	    // this works as well dates.replace(".","");
	}				
	if(date2.indexOf("Sept") > -1){
	    date2 = date2.replace("Sept","Sep");
	}
	else if(date2.indexOf("March") > -1){
	    date2 = date2.replace("March","Mar");
	}
	else if(date2.indexOf("April") > -1){
	    date2 = date2.replace("April","Apr");
	}
	else if(date2.indexOf("June") > -1){
	    date2 = date2.replace("June","Jun");
	}				
	else if(date2.indexOf("July") > -1){
	    date2 = date2.replace("July","Jul");
	}
	return date2;
    }
    /*
     * Update: now it should only return one date
     */
    public String getDates(){
	/*
	 * to switch from "Jan 10, 2011' to "01/10/2011"
	 * also some dates are like "Jan. 10, 2011"
	 * need to get rid of '.' in them 
	 * SHORT is completely numeric, such as 12.13.52 or 3:30pm
	 * MEDIUM is longer, such as Jan 12, 1952
	 * LONG is longer, such as January 12, 1952 or 3:30:32pm 
	 */
	// this was causing problems
	DateFormat dfm = DateFormat.getDateInstance(DateFormat.MEDIUM);
	//
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);		
	SimpleDateFormat ft = new SimpleDateFormat ("MM/dd/yyyy");
	SimpleDateFormat df2 = new SimpleDateFormat ("M d, yyyy");				
	String str = "", dates2="";
	boolean pass = false;
	if(!dates.equals("")){
	    if(Helper.isValidDate(dates)){
		return dates;
	    }
	    java.util.Date date = null;
	    dates2 = cleanDate(dates);						
	    try{
		date = df.parse(dates2);
		str = ft.format(date);
		pass = true;
	    }
	    catch(Exception ex){
		logger.error(" first try "+ex);
	    }
	    if(!pass){ // check if it is of type Jan 15, 2007
		try{
		    date = dfm.parse(dates2);
		    str = ft.format(date);
		}
		catch(Exception ex){
		    logger.error(ex);
		    str = dates;
		}
	    }
	    if(!pass){
		try{
		    date = df2.parse(dates2);
		    str = ft.format(date);
		    pass = true;
		}
		catch(Exception ex){
		    logger.error(ex);
		    str = dates;
		}
	    }
	}
	return str;
    }
    public String getAmount(){
	return amount;
    }
    public String getEntered(){
	return entered;
    }
    public String getCitations(){
	return citations;
    }	
    //
    public ViolSubcat getViolSubcat(){
	if(violSubcat == null && !sid.equals("")){
	    violSubcat = new ViolSubcat(sid, debug);
	    violSubcat.doSelect();
	}
	return violSubcat;
    }
    public ViolCat getViolCat(){
	if(violCat == null){
	    if(cid.equals("") && violSubcat == null){
		getViolSubcat();
	    }
	    if(violSubcat != null){
		cid = violSubcat.getCid();
	    }
	    if(!cid.equals("")){
		ViolCat vc = new ViolCat(cid, debug);
		String str = vc.doSelect();
		if(str.equals("")){
		    violCat = vc;
		}
	    }
	}
	return violCat;
    }
    public String doSave(){
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
		
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
		
	String qq = "";
	entered = Helper.getToday();
	try{
	    qq = "insert into legal_case_violations values(0,"; // auto_incr
	    qq += ""+id+",";
	    if(sid.equals(""))
		qq += "null,";
	    else
		qq += ""+sid+",";
	    if(ident.equals(""))
		qq += "null,";
	    else
		qq += "'"+ident+"',";
	    if(dates.equals(""))
		qq += "null,";
	    else
		qq += "'"+Helper.escapeIt(dates)+"',";
	    if(amount.equals(""))
		qq += "null,";
	    else
		qq += "'"+amount+"',";
	    qq += "CURRENT_DATE,";
	    if(citations.equals(""))
		qq += "null,";
	    else
		qq += "'"+citations+"',";
	    if(cid.equals(""))
		qq += "null";
	    else
		qq += ""+cid+"";
				
	    qq += ")";
	    if(debug)
		logger.debug(qq);
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		System.err.println(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		vid = rs.getString(1);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save date ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}				
	return back;
    }
    public String doUpdate(){
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
		
	String str="", back="";
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}	
	qq = "update legal_case_violations set ";
	if(sid.equals("")){
	    qq += "sid=null,";
	}
	else{
	    qq += "sid="+sid+",";
	}
	if(cid.equals("")){
	    qq += "cid=null,";
	}
	else{
	    qq += "cid="+cid+",";
	}	
	if(ident.equals("")){
	    qq += "ident=null,";
	}
	else{
	    qq += "ident='"+ident+"',";
	}
	if(citations.equals("")){
	    qq += "citations=null,";
	}
	else{
	    qq += "citations='"+citations+"',";
	}	
	if(dates.equals("")){
	    qq += "dates=null,";
	}
	else{
	    qq += "dates='"+dates+"',";
	}
	if(amount.equals("")){
	    qq += "amount=null";
	}
	else{
	    qq += "amount='"+amount+"'";
	}
	qq += " where vid="+vid;
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not update data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}				
	return back;
    }
    public String doDelete(){
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	String back = "";
	String qq = "delete from legal_case_violations where vid="+vid;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}	
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not delete data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    public String doSelect(){	
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
		
	String back = "";
	String qq = "select id,sid,ident,dates,amount, "+
	    " date_format(entered,'%m/%d/%Y'),citations,cid "+
	    " from legal_case_violations where vid='"+vid+"'";
	String str="";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}	
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) id = str;
		str = rs.getString(2);
		if(str != null) sid = str;
		str = rs.getString(3);
		if(str != null) ident = str;
		str = rs.getString(4);
		if(str != null) dates = str;
		str = rs.getString(5);
		if(str != null && !str.equals("0")) amount = str;
		str = rs.getString(6);
		if(str != null) entered = str;
		str = rs.getString(7);
		if(str != null) citations = str;
		str = rs.getString(8);
		if(str != null) cid = str;
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not retreive data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
	

}






















































