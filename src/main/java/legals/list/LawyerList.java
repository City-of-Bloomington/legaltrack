package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;

public class LawyerList{

    boolean debug = false;
    static final long serialVersionUID = 44L;
    static Logger logger = LogManager.getLogger(LawyerList.class);
    boolean current = false;
    String lawyerTypeJsHash = "";
    List<Lawyer> lawyers = null;
    public LawyerList(boolean val){
	debug = val;
    }
    //
    //setters
    //
    public void setCurrentOnly(){
	current = true;
    }
    //
    // getters
    //
    public List<Lawyer> getLawyers(){
	return lawyers;
    }
    //
    // a string of type: lawyer mapping to be used in Java Script
    public String getLawyerTypeJsHash(){
	return lawyerTypeJsHash;
    }

    public String find(){	
	//
	String back = "";
	String qq = "select * from attorneys ";
	if(current){
	    qq += " where active is not null ";
	}
	qq += " order by lname ";
	String str="", str2="", str3="", str4="",
	    str5="", str6="", str7="", str8="";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{		
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		//
		str = rs.getString(1);
		str2 = rs.getString(2);
		str3 = rs.getString(3);
		str4 = rs.getString(4);
		str5 = rs.getString(5);
		str6 = rs.getString(6);
		str7 = rs.getString(7);
		str8 = rs.getString(8);				
		Lawyer ll = new Lawyer(str, str2, str3, str4, str5,
				       str6, str7, str8,
				       debug);
		if(lawyers == null) lawyers = new ArrayList<>();
		lawyers.add(ll);
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

    public String composeLawyerTypeJS(){	
	//
	String back = "";
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String str="", str2="";
	Connection con = Helper.getConnection();		
	lawyerTypeJsHash = " var lawyerType = new Array();\n";
	String qq = " select typeId,lawyerid from legal_lawyer_types ";
	if(debug){
	    logger.debug(qq);
	}
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    rs = stmt.executeQuery();
	    while(rs.next()){
		str = rs.getString(1);
		str2 = rs.getString(2);
		if(str != null){
		    lawyerTypeJsHash += "lawyerType['"+str+"']='"+str2+"';\n";
		}
	    }
	    lawyerTypeJsHash += " var deptType = new Array();\n";
	    //
	    qq = " select t.typeId,d.dept from legal_depts d,"+
		"legal_type_dept t where d.deptId=t.deptId ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    rs = stmt.executeQuery();
	    while(rs.next()){
		str = rs.getString(1);
		str2 = rs.getString(2);
		if(str != null){
		    lawyerTypeJsHash += "deptType['"+str+"']='"+Helper.replaceSpecialChars(str2)+"';\n";
		}
	    }
	}catch(Exception ex){
	    logger.error(ex+":"+qq);
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //

}






















































