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
 *
 */

public class Owner{

    boolean debug;
    String name_num="", name="", address="", city="", state="", zip="",
	phone_work="", phone_home="",  notes="";
    static final long serialVersionUID = 61L;
    Logger logger = LogManager.getLogger(Owner.class);
    //
    // basic constructor
    public Owner(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public Owner(boolean deb, String id){

	debug = deb;
	//
	// initialize
	//
	this.name_num=id;
	doSelect();
    }
    //
    // setters
    //

    //
    // getters
    //
    public String  getId(){
	return name_num;
    }
    public String  getName(){
	return name;
    }
    public String  getAddress(){
	return address;
    }
    public String  getNotes(){
	return notes;
    }
    public String  getCityStateZip(){
	String str = "";
	if(!city.equals(""))
	    str += city;
	if(!state.equals("")){
	    if(!str.equals(""))str += ", ";
	    str += state;
	}
	if(!zip.equals("")){
	    if(!str.equals(""))str += " ";
	    str += zip;
	}
	return str;
    }
    public String  getPhones(){
	String str="";
	if(!phone_work.equals("")) str += phone_work;
	if(!phone_home.equals("")){
	    if(!str.equals(""))str += ", ";
	    str += phone_home;
	}
	return str;
    }
    //
    // We do not need Save/Update for this class as it saved in the rental
    // database where saving/updating are taken care of (for now)
    //
    public String doSave(Statement stmt){

	return "";
    }
    public String doUpdate(Statement stmt){

	return ""; // success
    }
    //
    public String doDelete(Statement stmt){
	return "";
    }
    //
    public String doSelect(){
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String back = "";
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}		
	String qq = "select * from name where name_num="+name_num;
	String str="";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		str = rs.getString(2);
		if(str != null) name = str;
		str = rs.getString(3);
		if(str != null) address = str;
		str = rs.getString(4);  
		if(str != null) city = str; 
		str = rs.getString(5);
		if(str != null) state = str;
		str = rs.getString(6);
		if(str != null) zip = str;
		str = rs.getString(7);
		if(str != null) phone_work = str;
		str = rs.getString(8);
		if(str != null) phone_home = str;
		str = rs.getString(9);
		if(str != null) notes = str;
	    }
	    else{
		return "Record "+name_num+" Not found";
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    return ex.toString();
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }

}






















































