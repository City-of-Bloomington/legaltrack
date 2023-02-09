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

public class Animal {


    boolean debug = false;
    static Logger logger = LogManager.getLogger(Animal.class);

    String id = "", cid="", name="", pet_type="";

    public Animal(boolean val){
	debug = val;
    }
	
    public Animal(String val, boolean deb){
	id = val;
	debug = deb;
    }
    public Animal(String val, String val2, String val3, boolean deb){
	setCid(val);
	setName(val2);
	setType(val3);
	debug = deb;
    }	
    public Animal(String val, String val2, String val3, String val4, boolean deb){
	if(val != null)		
	    id = val;
	if(val2 != null)
	    cid = val2;
	if(val3 != null)		
	    name = val3;
	if(val4 != null)		
	    pet_type = val4;		
	debug = deb;
    }
	
    //
    //setters
    //
    public void  setId(String val){
	if(val != null)
	    id = val;
    }
    public void  setCid(String val){
	if(val != null)
	    cid = val;
    }	
    public void  setName(String val){
	if(val != null)
	    name = val.trim();
    }
    public void  setType(String val){
	if(val != null)
	    pet_type = val.trim();
    }	
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getCid(){
	return cid;
    }	
    public String getName(){
	return name;
    }
    public String getType(){
	return pet_type;
    }	
    public String toString(){
	return name;
    }
    public boolean equals(Object gg){
	boolean match = false;
	if (gg != null && gg instanceof Animal){
	    match = id.equals(((Animal)gg).id);
	}
	return match;
    }
    public int hashCode(){
	int code = 0;
	try{
	    code = Integer.parseInt(id);
	}catch(Exception ex){};
	return code;
    }		
    /**
     * check if the required file are present
     */
    public boolean isValid(){
	if(cid.equals("") ||
	   name.equals(""))
	    return false; 
	return true;
    }
    public String doSave(){
	//
	String back = "";
	if(!isValid()){
	    back = "All fields are required ";
	    return back;
	}
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
		
	String qq = "";
	qq = "insert into legal_animals values(0,?,?,?)"; // auto_incr
	if(debug)
	    logger.debug(qq);				
	try{
	    stmt = con.prepareStatement(qq);			
	    stmt.setString(1, cid);
	    stmt.setString(2, name);
	    if(pet_type.equals(""))
		stmt.setNull(3, Types.INTEGER);
	    else
		stmt.setString(3, pet_type);
	    stmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt2 = con.prepareStatement(qq);
	    rs = stmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }	
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save date ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}
	return back;
    }
    public String doUpdate(){
	//
	String str="", back="";
	String qq = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	qq = "update legal_animals set name=?,pet_type=? where id=? ";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, name);
	    if(pet_type.equals(""))
		stmt.setNull(2, Types.INTEGER);
	    else
		stmt.setString(2, pet_type);
	    stmt.setString(3, id);
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
	String qq = "delete from legal_animals where id=? ";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
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
    //
    public String doSelect(){
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	con = Helper.getConnection();
	String qq = "select cid,name,pet_type "+
	    " from legal_animals where id=? ";
		
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}						
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) cid = str;
		str = rs.getString(2);
		if(str != null) name = str;
		str = rs.getString(3);
		if(str != null) pet_type = str;				
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }

}






















































