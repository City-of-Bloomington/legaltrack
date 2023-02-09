package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
/**
 *
 *
 */

public class CareOf{
	
    boolean debug;
    static Logger logger = LogManager.getLogger(CareOf.class);
	
    String id="", def_id="", co_name="";
		
    //
    //
    // basic constructor
    public CareOf(boolean deb){

	debug = deb;
	//
    }
    public CareOf(boolean deb, String id){

	debug = deb;
	//
	setId(id);
    }
    public CareOf(boolean deb, String id, String def_id, String co_name){

	debug = deb;
	//
	// initialize
	//
	setId(id);
	setDefId(def_id);
	setCoName(co_name);
    }
    //
    // setters
    //
    public void  setId(String val){
	if(val != null)
	    id = val;
    }
    public void  setDefId(String val){
	if(val != null)
	    def_id = val;
    }
    public void  setCoName(String val){
	if(val != null)
	    co_name = val;
    }
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public String  getCaseId(){
	return def_id;
    }
    public String  getCoName(){
	return co_name;
    }
    public String toString(){
	return co_name;
    }

    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "insert into legal_care_of values(0,?,?)";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    try{
		stmt = con.prepareStatement(qq);
		stmt.setString(1, def_id);
		stmt.setString(2, co_name);
		stmt.executeUpdate();
		qq = "select LAST_INSERT_ID() ";
		if(debug){
		    logger.debug(qq);
		}
		stmt = con.prepareStatement(qq);
		rs = stmt.executeQuery();
		if(rs.next()){
		    id = rs.getString(1);
		}
	    }
	    catch(Exception ex){
		back += ex;
		logger.error(ex);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }			
	}
	return back;
    }
    //

    public String doUpdate(){

	String back="";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "";
	qq = "update legal_care_of set co_name = ? where id = ?";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{		
	    try{
		stmt = con.prepareStatement(qq);
		stmt.setString(1, co_name);
		stmt.setString(2, id);
		stmt.executeUpdate();
	    }
	    catch(Exception ex){
		back = ex+":"+qq;
		logger.error(back);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	return back; // success
    }
    public String doDelete(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "delete from legal_care_of where id = ?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    try{
		stmt = con.prepareStatement(qq);
		stmt.setString(1, id);
		stmt.executeUpdate();
	    }
	    catch(Exception ex){
		back += ex;
		logger.error(ex);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }			
	}

	return back;

    }
	
    public String doSelect(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = " select def_id,co_name from legal_care_of where id=?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    try{
		stmt = con.prepareStatement(qq);
		stmt.setString(1, id);
		rs = stmt.executeQuery();
		if(rs.next()){
		    String str = rs.getString(1);
		    if(str != null) def_id = str;
		    str = rs.getString(2);
		    if(str != null) co_name = str;
		}
	    }
	    catch(Exception ex){
		back += ex;
		logger.error(ex);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	return back;

    }	

}






















































