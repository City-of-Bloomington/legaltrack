package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;

public class AddressList{

    boolean debug;
    String id="", caseId="", rental_addr="";
    static final long serialVersionUID = 16L;
    static Logger logger = LogManager.getLogger(AddressList.class);	
    String dateFrom="",dateTo="";
    List<Address> addresses = null;
    //
    // basic constructor
    public AddressList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public AddressList(boolean deb, String caseId){

	debug = deb;
	//
	// initialize
	//
	this.caseId = caseId;
    }
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
    public void setCase_id(String val){
	if(val != null && !val.equals(""))
	    caseId = val;
    }
    public void setCaseId(String val){
	if(val != null && !val.equals(""))
	    caseId = val;
    }
    public void setRental_addr(String val){
	if(val != null && !val.equals(""))
	    rental_addr = val;
    }
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public List<Address>  getAddresses(){
	return addresses;
    }
    //
    public String lookFor(){
	//
	String back;
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "select id,caseId, "+
	    "street_num,street_dir,street_name,street_type,"+
	    "post_dir, sud_type,sud_num, "+
	    "invalid_addr,rental_addr "+//,street_address "+
	    "from legal_addresses ";
	String qw = "";
				
	if(!caseId.equals("")){
	    qw += " caseId=? ";
	    //qw += " caseId="+caseId;
	}
	if(!rental_addr.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " rental_addr is not null ";
	    // qw += " rental_addr = 'Y'";
	}
	if(!qw.equals(""))
	    qq += " where "+qw;
	String message = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    if(!caseId.equals("")){
		stmt.setString(1, caseId);
	    }						
	    rs = stmt.executeQuery();
	    while(rs.next()){
		if(addresses == null)
		    addresses = new ArrayList<Address>();
		String str   = rs.getString(1);
		String str2  = rs.getString(2);
		String str3  = rs.getString(3);
		String str4  = rs.getString(4);
		String str5  = rs.getString(5);
		String str6  = rs.getString(6);
		String str7  = rs.getString(7);
		String str8  = rs.getString(8);
		String str9  = rs.getString(9);
		String str10 = rs.getString(10);
		String str11 = rs.getString(11);
		String str12 = ""; // rs.getString(12);
		Address addr = new Address(debug,
					   str, str2, str3,
					   str4,str5,str6,
					   str7,str8,str9,str10,str11,
					   str12);
		addresses.add(addr);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    return ex.toString();
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);		
	}
	return message;
    }
	
}






















































