package legals.list;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
/**
 * 
 */

public class LegalFileList{

    boolean debug;
    static final long serialVersionUID = 23L;
    static Logger logger = LogManager.getLogger(LegalFileList.class);
    String cid="";
    List<LegalFile> files = null;
    //
    public LegalFileList(boolean deb){

	debug = deb;
    }
    public LegalFileList(boolean deb,
			 String val){

	debug = deb;
	setCid(val);

    }
    //
    // setters
    //
    public void setCid(String val){
	if(val != null){
	    cid = val;
	}
    }
    public List<LegalFile> getFiles(){
	return files;
    }
    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qo = "";
	String qq = "select id,cid,"+
	    "date_format(file_date,'%m/%d/%Y'),load_file, "+
	    "notes,old_name from legal_files ";
	String back="", qw = "";
		
	if(!cid.equals("")){
	    qw = " cid = ? ";
	}
	if(!qw.equals("")){
	    qw = " where "+qw;
	}
	qq = qq + qw;			
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    pstmt = con.prepareStatement(qq);
	    if(!cid.equals(""))
		pstmt.setString(1, cid);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		LegalFile one = new LegalFile(debug,
					      rs.getString(1),
					      rs.getString(2),
					      rs.getString(3),
					      rs.getString(4),
					      rs.getString(5),
					      rs.getString(6));
		if(files == null)
		    files = new ArrayList<>();
		if(!files.contains(one))
		    files.add(one);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    return ex.toString();
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return "";
    }

	

}






















































