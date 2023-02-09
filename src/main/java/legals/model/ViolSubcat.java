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

public class ViolSubcat {


    boolean debug = false;
    static final long serialVersionUID = 80L;
    static Logger logger = LogManager.getLogger(ViolSubcat.class);
    String sid="", cid = "", subcat="", complaint="", codes="", amount="";

    public ViolSubcat(boolean val){
	debug = val;
    }
	
    public ViolSubcat(String val, boolean deb){
	sid = val;
	debug = deb;
    }
    public ViolSubcat(String val,
		      String val2,
		      String val3,
		      String val4,
		      String val5,
		      String val6,
		      boolean deb){
	sid = val;
	cid = val2;
	setSubcat(val3);
	setComplaint(val4);
	setCodes(val5);
	setAmount(val6);
	debug = deb;
    }	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    sid = val;
    }
    public void setCid(String val){
	if(val != null)
	    cid = val;
    }
    public void setSubcat(String val){
	if(val != null)
	    subcat = val;
    }
    public void setComplaint(String val){
	if(val != null)
	    complaint = val;
    }
    public void setCodes(String val){
	if(val != null)
	    codes = val;
    }
    public void setAmount(String val){
	if(val != null)
	    amount = val;
    }
    //
    // getters
    //
    public String getId(){
	return sid ;
    }
    public String getSid(){
	return sid ;
    }
    public String getCid(){
	return cid ;
    }
    public String getSubcat(){
	return subcat;
    }
    public String getComplaint(){
	return complaint;
    }
    public String getCodes(){
	return codes;
    }
    public String getAmount(){
	return amount;
    }
    public String toString(){
	return subcat;
    }
    public String doSave(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	if(subcat.equals("")){
	    back = "Sub category not set ";
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
		
	String qq = "";			
	try{
	    qq = "insert into legal_viol_subcats(0,?,?,?,?,?)"; // auto_incr
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, cid);
	    stmt.setString(2, subcat);
	    if(complaint.equals(""))
		stmt.setString(3, null);
	    else
		stmt.setString(3, complaint);
	    if(codes.equals(""))
		stmt.setString(4, null);
	    else
		stmt.setString(4, codes);
	    if(amount.equals(""))
		stmt.setString(5, "0");
	    else
		stmt.setString(5, amount);
	    stmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);			
	    rs = stmt.executeQuery();
	    if(rs.next()){
		sid = rs.getString(1);
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
		
	qq = "update legal_viol_subcats set subcat=?,complaint=?,codes=?,amount=? where sid = ? ";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1,subcat);
	    if(complaint.equals(""))
		stmt.setString(2, null);
	    else
		stmt.setString(2, complaint);
	    if(codes.equals(""))
		stmt.setString(3, null);
	    else
		stmt.setString(3, codes);
	    if(amount.equals(""))
		stmt.setString(4, "0");
	    else
		stmt.setString(4, amount);
	    stmt.setString(5, sid);
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
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;				
	String back = "";
	String qq = "delete from legal_viol_subcats where sid=?";
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
	    stmt.setString(1,sid);
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
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String back = "";
	String qq = "select cid,subcat,complaint,codes,amount "+
	    " from legal_viol_subcats where sid=?";
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
	    stmt.setString(1,sid);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) cid = str;
		str = rs.getString(2);
		if(str != null) subcat = str;
		str = rs.getString(3);
		if(str != null) complaint = str;
		str = rs.getString(4);
		if(str != null) codes = str;
		str = rs.getString(5);
		if(str != null) amount = str;
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






















































