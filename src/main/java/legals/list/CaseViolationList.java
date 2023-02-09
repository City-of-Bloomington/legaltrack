package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;


public class CaseViolationList{

    boolean debug = false;
    static final long serialVersionUID = 30L;
    static Logger logger = LogManager.getLogger(CaseViolationList.class);
    String id = ""; // case Id
    List<CaseViolation> caseViolations = null;
    
    public CaseViolationList(boolean val){
	debug = val;
    }
    public CaseViolationList(String val, boolean val2){
	id = val; // violation for case id
	debug = val2;
    }
    public List<CaseViolation> getCaseViolations() {
	return caseViolations;
    }
    //
    //setters
    //

    public String find(){	
	//
	String back = "";
	String qq = "select vid,id,sid,ident,dates,amount, "+
	    " date_format(entered,'%m/%d/%Y'),citations,cid "+
	    " from legal_case_violations ";
	if(!id.equals("")){
	    qq += " where id="+id;
	}
	Statement stmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();				
	    String str="", str2="", str3="", str4="", str5="",
		str6="", str7="", str8="", str9="";
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		//
		str = rs.getString(1);
		str2 = rs.getString(2);
		str3 = rs.getString(3);
		str4 = rs.getString(4);
		str5 = rs.getString(5);
		str6 = rs.getString(6);
		str7 = rs.getString(7);
		str8 = rs.getString(8);
		str9 = rs.getString(9);
		if(str != null && str2 != null){
		    CaseViolation cv = new CaseViolation(str, str2, str3,
							 str4, str5, str6,
							 str7, str8, str9,
							 debug);
		    if(caseViolations == null) caseViolations = new ArrayList<>();
		    caseViolations.add(cv);
		}
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






















































