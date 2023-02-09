package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;

public class DefAddressList{

    boolean debug;
    String id="", defId="", rental_addr="";
    static final long serialVersionUID = 35L;
    static Logger logger = LogManager.getLogger(DefAddressList.class);	
    String dateFrom="",dateTo="";
    List<DefAddress> addresses = null;
    //
    // basic constructor
    public DefAddressList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public DefAddressList(boolean deb, String defId){

	debug = deb;
	//
	// initialize
	//
	this.defId = defId;
    }
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
    public void setDefId(String val){
	if(val != null && !val.equals(""))
	    defId = val;
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
    public List<DefAddress>  getAddresses(){
	return addresses;
    }
    //
    //
    public String lookFor(){
	//
	String back;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String qq = "select id from legal_def_addresses ";
	String qw = "";
	if(!defId.equals("")){
	    qw += " defId="+defId;
	}
	if(!rental_addr.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " rental_addr = 'Y'";
	}
	if(!qw.equals(""))
	    qq += " where "+qw;
	qq += " order by addr_date desc, id desc ";
	String message = "";
	con = Helper.getConnection();
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
	    List<String> list = new ArrayList<String>();
	    while(rs.next()){
		String str = rs.getString(1);
		list.add(str);
	    }
	    if(list.size() > 0){
		for(int i=0;i<list.size();i++){
		    String str = list.get(i);
		    DefAddress addr = new DefAddress(debug, str);
		    back = addr.doSelect();
		    if(back.equals("")){
			if(addresses == null) addresses = new ArrayList<>();
			addresses.add(addr);
		    }
		    else{
			logger.error(back);
			message += " "+back;
		    }
		}
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






















































