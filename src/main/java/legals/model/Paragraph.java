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

public class Paragraph {

    boolean debug = false;
    static final long serialVersionUID = 64L;
    static Logger logger = LogManager.getLogger(Paragraph.class);
    String type = "", id="", ptext="", porder="";
	
    public Paragraph(boolean val){
	debug = val;
    }
	
    public Paragraph(String val, boolean deb){
	id = val;
	debug = deb;
    }
    public Paragraph(String val,
		     String val2,
		     String val3,
		     String val4,
		     boolean deb){
	setId(val);
	setType(val2);
	setOrder(val3);
	setText(val4);
	debug = deb;
    }	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setType(String val){
	if(val != null)
	    type = val;
    }		
    public void setOrder(String val){
	if(val != null)
	    porder = val;
    }
    public void setText(String val){
	if(val != null)
	    ptext = val;
    }
	
    //
    // getters
    //
    public String getId(){
	return id ;
    }
    public String getType(){
	return type;
    }
    public String getOrder(){
	return porder;
    }	
    public int getIntOrder(){
	int ret = 0;
	try{
	    ret = Integer.parseInt(porder);
	}catch(Exception ex){
	    logger.error(ex);
	}
	return ret;
    }
	
    public String getText(){
	return ptext;
    }
    public String toString(){
	return ptext;
    }
    public boolean isValid(){
	if(ptext.equals("") || type.equals("")) return false;
	return true;
    }
    public String doSave(){
	//
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	if(!isValid()){
	    back = "type or text not set ";
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
	    stmt = con.createStatement();
	    qq = "insert into doc_texts values(0,"; // auto_incr
	    qq += "'"+type+"',"+porder+",'"+Helper.escapeIt(ptext)+"')";
	    if(debug)
		logger.debug(qq);
	    stmt.executeUpdate(qq);
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
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
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    public String doUpdate(){
	//
	String str="", back="";
	String qq = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}	
	qq = "update doc_texts set ";
	qq += "ptext='"+Helper.escapeIt(ptext)+"',type='"+type+"', porder="+
	    porder;
	qq += " where id="+id;
	try{
	    stmt = con.createStatement();
	    if(debug){
		logger.debug(qq);
	    }
	    stmt.executeUpdate(qq);
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
    //
    public String doSelect(){
	String back = "", str="";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	String qq = "select type,porder,ptext "+
	    " from doc_texts where id="+id;		
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
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) type = str;
		str = rs.getString(2);
		if(str != null) porder = str;
		str = rs.getString(3);
		if(str != null) ptext = str;				
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
    //
    public String doDelete(){
	//
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	String qq = " delete from doc_texts where id="+id;
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
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
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
}






















































