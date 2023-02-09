package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
/**
 *
 *
 */

public class Status {


    boolean debug = false;
    static Logger logger = LogManager.getLogger(Status.class);

    String statusId = "", statusDesc="";

    public Status(boolean val){
	debug = val;
    }
	
    public Status(String val, boolean deb){
	statusId = val;
	debug = deb;
    }
    public Status(String val, String val2, boolean deb){
	statusId = val;
	statusDesc = val2;
	debug = deb;
    }
	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    statusId = val;
    }
    public void setStatusDesc(String val){
	if(val != null)
	    statusDesc = val;
    }
    //
    // getters
    //
    public String getId(){
	return statusId ;
    }
    public String getStatusDesc(){
	return statusDesc;
    }
    public String getDesc(){
	return statusDesc;
    }
    public String toString(){
	return statusDesc;
    }
    public String doSave(){
	//
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	String qq = "";
	try{
	    stmt = con.createStatement();			
	    qq = "insert into legal_case_status("; // auto_incr
	    qq += "'"+statusId+"','"+Helper.escapeIt(statusDesc)+"')";
	    if(debug)
		logger.debug(qq);
	    stmt.executeUpdate(qq);
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
	String str="", back="";
	String qq = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}		
	qq = "update legal_case_status set ";
	qq += "statusDesc='"+Helper.escapeIt(statusDesc)+"' ";
	qq += " where statusId='"+statusId+"'";
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
	String back = "";
	String qq = "delete from legal_case_status where statusId='"+statusId+"'";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
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
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}				
	String qq = "select statusDesc "+
	    " from legal_case_status where statusId='"+statusId+"'";
	String str="";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) statusDesc = str;
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
    //


}






















































