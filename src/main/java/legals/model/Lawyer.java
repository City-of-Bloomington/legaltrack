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

public class Lawyer {


    boolean debug = false;
    static final long serialVersionUID = 43L;
    static Logger logger = LogManager.getLogger(Lawyer.class);

    String id="", empid = "", fname = "", lname="", barNum = "", title = "",
	active = "", position="";
    public Lawyer(boolean val){
	debug = val;
    }
	
    public Lawyer(String val, boolean deb){
	setId(val);
	debug = deb;
    }
    public Lawyer(String val, String val2, boolean deb){
	setId(val);
	setEmpid(val2);
	debug = deb;
    }	
    public Lawyer(String val,
		  String val2,
		  String val3,
		  String val4,
		  String val5,
		  String val6,
		  String val7,
		  String val8,				  
		  boolean deb){
	setId(val);
	setEmpid(val2);		
	setFname(val3);
	setLname(val4);
	setPosition(val5);
	setTitle(val6);		
	setBarNum(val7);
	setActive(val8);
	debug = deb;
    }	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setEmpid(String val){
	if(val != null)
	    empid = val;
    }	
    public void setFname(String val){
	if(val != null)
	    fname = val;
    }
    public void setLname(String val){
	if(val != null)
	    lname = val;
    }
    public void setPosition(String val){
	if(val != null)
	    position = val;
    }	
    public void setBarNum(String val){
	if(val != null)
	    barNum = val;
    }
    public void setTitle(String val){
	if(val != null)
	    title = val;
    }
    public void setActive(String val){
	if(val != null)
	    active = val;
    }
    //
    // getters
    //
    public String getId(){
	return id ;
    }
    public String getEmpid(){
	return empid ;
    }	
    public String getFname(){
	return fname;
    }
    public String getLname(){
	return lname;
    }
    public String getFullName(){
	String ret = fname;
	if(!ret.equals("")) ret += " ";
	ret += lname;
	return ret;
    }	
    public String getBarNum(){
	return barNum;
    }
    public String getTitle(){
	return title ;
    }
    public String getPosition(){
	return position ;
    }	
    public String getActive(){
	return active ;
    }
    public boolean isActive(){
	return !active.equals("") ;
    }
    public boolean isCounsel(){
	return position.equals("Counsel");
    }
    public boolean isAttorney(){
	return position.equals("Attorney");
    }
    public boolean isAssistant(){
	return position.equals("Assistant");
    }
    public String toString(){
	return getFullName();
    }
		
    public String doSave(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "";
	if(lname.equals("")){
	    back = "Name not set ";
	    logger.error(back);
	    return back;
	}
	if(empid.equals("")){
	    back = "username not set ";
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
	    qq = " insert into attorneys values (0,?,?,?,?,?,?,'y')";			
	    pstmt = con.prepareStatement(qq);

	    pstmt.setString(1, empid);
	    pstmt.setString(2, fname);
	    pstmt.setString(3, lname);
	    pstmt.setString(4, position);
	    pstmt.setString(5, title);
	    if(barNum.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6, barNum);
	    pstmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);			
	    rs = pstmt.executeQuery();
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
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    public String doUpdate(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String str="", back="";
	String qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}	
	qq = "update attorneys set empid=?,fname=?,lname=?,position=?,title=?,barNum=?,active=? where id=?";
		

	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, empid);
	    if(fname.equals(""))
		pstmt.setNull(2, Types.VARCHAR);
	    else
		pstmt.setString(2, fname);
	    if(lname.equals(""))
		pstmt.setNull(3, Types.VARCHAR);
	    else			
		pstmt.setString(3, lname);
	    if(position.equals(""))
		pstmt.setNull(4, Types.VARCHAR);
	    else			
		pstmt.setString(4, position);
	    if(title.equals(""))
		pstmt.setNull(5, Types.VARCHAR);
	    else
		pstmt.setString(5, title);
	    if(barNum.equals(""))
		pstmt.setNull(6,Types.VARCHAR);
	    else
		pstmt.setString(6, barNum);
	    if(active.equals(""))
		pstmt.setNull(7,Types.VARCHAR);
	    else
		pstmt.setString(7,"y");
	    pstmt.setString(8,id);
	    pstmt.executeUpdate();			

	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not update data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    public String doDelete(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "";
	String qq = "delete from legal_lawyer_types l,attorneys a where l.lawyerid=a.empid and a.id=?";
	String qq2 = "delete from attorneys where id=?";
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
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.executeUpdate();
	    qq = qq2;
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    pstmt.executeUpdate();			
			
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not delete data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    public String doSelect(){	
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "";
	String qq = "select * "+
	    " from attorneys where ";
	if(!id.equals("")){
	    qq += "id = ? ";
	}
	else{
	    qq += "empid = ? ";
	}
	String str="";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!id.equals("")){	
		pstmt.setString(1, id);
	    }
	    else{
		pstmt.setString(1, empid);
	    }
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		//
		setEmpid(rs.getString(2));
		setFname(rs.getString(3));
		setLname(rs.getString(4));
		setPosition(rs.getString(5));
		setTitle(rs.getString(6));
		setBarNum(rs.getString(7));
		setActive(rs.getString(8));
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not retreive data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}		
	return back;
    }

    public String setCaseTypes(String[] typeId){
		
	String qq = "", back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		logger.error(back);
		return back;
	    }				
	    // first we delete the old ones
	    qq ="delete from legal_lawyer_types where lawyerid='"+
		empid+"'";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    if(typeId != null && typeId.length > 0){
		for(int i=0;i<typeId.length;i++){
		    qq = "insert into legal_lawyer_types values ('" +
			typeId[i]+"','";
		    qq += empid+"')";
		    if(debug){
			logger.debug(qq);
		    }
		    stmt.executeUpdate(qq);
		}
	    }

	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    public String removeType(String typeId){
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String qq = "delete from legal_lawyer_types where typeId='"+
	    typeId+"'";
	String back = "";
		
	if(!typeId.equals("")){
	    if(debug)
		logger.debug(qq);
	    try{
		con = Helper.getConnection();
		if(con == null){
		    back = "Could not connect to DB ";
		    logger.error(back);
		    return back;
		}
		stmt = con.createStatement();
		stmt.executeUpdate(qq);
	    }
	    catch(Exception ex){
		logger.error(ex+" : "+qq);
		back += ex;
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }			
	}
	return back;
    }
	
    public String addType(String typeId){
		
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String qq = "insert into legal_lawyer_types values('"+ typeId+"','"+
	    empid+"')";
	String back = "";
	if(!typeId.equals("")){
	    if(debug)
		logger.debug(qq);
	    try{
		con = Helper.getConnection();
		if(con == null){
		    back = "Could not connect to DB ";
		    logger.error(back);
		    return back;
		}
		stmt = con.createStatement();				
		stmt.executeUpdate(qq);
	    }
	    catch(Exception ex){
		logger.error(ex+" : "+qq);
		back += ex;
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	return back;	
    }

}






















































