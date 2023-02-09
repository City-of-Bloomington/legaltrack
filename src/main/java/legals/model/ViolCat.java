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

public class ViolCat {

    boolean debug = false;
    static Logger logger = LogManager.getLogger(ViolCat.class);

    String cid = "", category="";

    public ViolCat(boolean val){
	debug = val;
    }
	
    public ViolCat(String val, boolean deb){
	cid = val;
	debug = deb;
    }
    public ViolCat(String val, String val2, boolean deb){
	cid = val;
	setCategory(val2);
	debug = deb;
    }	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    cid = val;
    }
    public void setCid(String val){
	if(val != null)
	    cid = val;
    }
    public void setCategory(String val){
	if(val != null)
	    category = val;
    }
    //
    // getters
    //
    public String getId(){
	return cid ;
    }
    public String getCategory(){
	return category;
    }
    public String toString(){
	return category;
    }
    public String doSave(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	String back = "";
	if(category.equals("")){
	    back = "Category not set ";
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
		
	String qq = "";			
	try{
	    qq = "insert into legal_viol_cats(0,?)"; // auto_incr
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, category);
	    stmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);			
	    rs = stmt.executeQuery();
	    if(rs.next()){
		cid = rs.getString(1);
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
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String str="", back="";
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
		
	qq = "update legal_viol_cats set category=? where cid=? ";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, category);
	    stmt.setString(2, cid);
	    stmt.executeUpdate();
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
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "delete from legal_viol_cats where cid=?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, cid);
	    stmt.executeUpdate();
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
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "select category"+
	    " from legal_viol_cats where cid=?";
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
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, cid);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) category = str;
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






















































