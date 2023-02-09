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

public class CaseType {

    boolean debug = false;
    static Logger logger = LogManager.getLogger(CaseType.class);
    String typeId = "", typeDesc = "";
    Department department = null;
	
    public CaseType(boolean val){
	debug = val;
    }
	
    public CaseType(String val, boolean deb){
	typeId = val;
	debug = deb;
    }
    public CaseType(String val, String val2, boolean deb){
	typeId = val;
	typeDesc = val2;
	debug = deb;
    }	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    typeId = val;
    }
    public void setDesc(String val){
	if(val != null)
	    typeDesc = val;
    }
    //
    // getters
    //
    public String getId(){
	return typeId ;
    }
    public String getDesc(){
	return typeDesc;
    }
    public String getName(){
	return typeDesc;
    }	
    public String getTypeDesc(){
	if(typeDesc.indexOf("&") > -1){
	    typeDesc = typeDesc.replaceAll("&","and");
	}
	return typeDesc;
    }
    public Department getDepartment(){
	return department ;
    }
    public String toString(){
	return getTypeDesc();
    }
    public String doSave(){
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
		
	String back = "";
	if(typeId.equals("") || typeDesc.equals("")){
	    back = "typeId or typeDesc not set ";
	    logger.error(back);
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	String qq = "";			
	try{
	    stmt = con.createStatement();
	    qq = "insert into legal_case_types("; // auto_incr
	    qq += "'"+typeId+"','"+Helper.escapeIt(typeDesc)+"')";
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
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
		
	String str="", back="";
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}	
	qq = "update legal_case_types set ";
	qq += "typeDesc='"+Helper.escapeIt(typeDesc)+"' ";
	qq += " where typeId='"+typeId+"'";
	try{
	    stmt = con.createStatement();
	    if(debug){
		logger.debug(qq);
	    }
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
    //
    public String doSelect(){
	//
		
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}		
	String qq = "select typeDesc "+
	    " from legal_case_types where typeId='"+typeId+"'";
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
		if(str != null) typeDesc = str;
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
	back += resetDept();
	return back;
    }
    //
    public String doDelete(){
	//
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;


	//
	// first delete from join table
	//
	String qq = " delete from legal_type_dept t where t.typeId='"+typeId+"'";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}	
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    qq = "delete from legal_case_types where typeId='"+typeId+"'";
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
    //
    public String resetDept(){
		
	String str="", back="", deptId = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	if(typeId.equals("")){
	    back = "case type id not set ";
	    logger.error(back);
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}			
	String qq = "select d.deptId, d.dept from "+
	    " legal_type_dept t,legal_depts d"+
	    " where t.deptId=d.deptId and t.typeId='"+typeId+"'";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();						
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) deptId = str;
		str = rs.getString(2);
		if(!deptId.equals("")){
		    department = new Department(deptId, str, debug);
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






















































