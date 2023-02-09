package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
/**
 *
 *
 */

public class ParagraphList {


    boolean debug = false;
    String type = "";
    static final long serialVersionUID = 65L;
    static Logger logger = LogManager.getLogger(ParagraphList.class);
    List<Paragraph> paragraphs = new ArrayList<Paragraph>();

    public ParagraphList(boolean val){
	debug = val;
    }
    //
    //setters
    //
    public void setType(String val){
	if(val != null)
	    type = val;
    }
    //

    public List<Paragraph> getParagraphs(){
	return paragraphs = null;
    }
	
    public String find(){
		
	String back = "";
	Statement stmt = null;
	ResultSet rs = null;
	String q = " select id, type, porder, ptext from doc_texts ";
	String qq = "", qw = "", qo = " order by porder ";
	if(!type.equals("")){
	    qw = " where type ='"+type+"'";
	}
	Connection con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{
	    String str="", str2="", str3="", str4="";
	    try{
		stmt = con.createStatement();				
		qq = q+qw+qo;
		if(debug)
		    logger.debug(qq);
		rs = stmt.executeQuery(qq);
		while(rs.next()){
		    //
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str != null && str2 != null){
			Paragraph pp = new Paragraph(str, str2, str3,
						     str4,
						     debug);
			if(paragraphs == null) paragraphs = new ArrayList<>();
			paragraphs.add(pp);
		    }
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






















































