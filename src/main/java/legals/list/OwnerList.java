package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
/**
 * 
 *
 */

public class OwnerList{

    boolean debug;
    String id="";
    String dateFrom="",dateTo="";
	static final long serialVersionUID = 62L;
	Logger logger = LogManager.getLogger(OwnerList.class);
    List<String> owners = null;
    //
    // basic constructor
    public OwnerList(boolean deb){

		debug = deb;
		//
		// initialize
		//
    }
    public OwnerList(boolean deb, String val){

		debug = deb;
		//
		// initialize
		//
		setId(val);
    }
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public List<String> getOwners(){
	return owners;
    }
    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
    public String lookFor(){
		//
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String qq = "select name_num from regid_name where id="+id;
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
			return back;
	}
	try{
	    stmt = con.createStatement();			
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    owners = new ArrayList<>();
	    while(rs.next()){
		String str = rs.getString(1);
		owners.add(str);
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






















































