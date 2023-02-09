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
 *
 */

public class Department {


    boolean debug = false;
    static Logger logger = LogManager.getLogger(Department.class);
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
	
    String deptId = "", dept = ""; // name
	
	
    public Department(boolean val){
	debug = val;
    }
	
    public Department(String val, boolean deb){
	deptId = val;
	debug = deb;
	doSelect();
    }
    public Department(String val, String val2, boolean deb){
	deptId = val;
	dept = val2;
	debug = deb;
    }	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    deptId = val;
    }
    public void setDept(String val){
	if(val != null)
	    dept = val;
    }
	
    //
    // getters
    //
    public String getId(){
	return deptId ;
    }
    public String getDept(){
	return dept;
    }
    public String toString(){
	return dept;
    }
    public String doSave(){
	//
	String back = "";
	if(dept.equals("")){
	    back = "dept name not set ";
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
	    qq = "insert into legal_depts(0,"; // auto_incr
	    qq += "'"+Helper.escapeIt(dept)+"')";
	    if(debug)
		logger.debug(qq);
	    stmt.executeUpdate(qq);
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		deptId = rs.getString(1);
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
	String str="", back="";
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}	
	qq = "update legal_depts set ";
	qq += "dept='"+Helper.escapeIt(dept)+"' ";
	qq += " where deptId='"+deptId+"'";
	try{
	    stmt = con.createStatement();
	    if(debug){
		logger.debug(qq);
	    }
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    back += " Could not update data ";
	    back += ex;			
	    logger.error(back+": "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    public String doDelete(){
	//
	String back = "";
	String qq = "delete from legal_depts where deptId='"+deptId+"'";
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
	}
	catch(Exception ex){
	    back += " Could not delete data ";
	    back += ex;
	    logger.error(back+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    public String doSelect(){	
	//
	String back = "";
	String qq = "select dept "+
	    " from legal_depts where deptId='"+deptId+"'";
	String str="";
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
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) dept = str;
	    }
	}
	catch(Exception ex){
	    back += " Could not retreive data ";
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }

}






















































