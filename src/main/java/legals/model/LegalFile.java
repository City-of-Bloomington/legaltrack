
package legals.model;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
import legals.list.*;
/**
 *
 */

public class LegalFile{
	
    boolean debug;
    static Logger logger = LogManager.getLogger(LegalFile.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String id="", cid ="", errors="", 
	notes="", // name="",
	load_file="", // added_by_id="",
	file_date="", old_name="";
    //
    //
    // basic constructor
    public LegalFile(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public LegalFile(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	setId(val);
    }
    public LegalFile(boolean deb,
		     String val,
		     String val2,
		     String val3,
		     String val4,
		     String val5,
		     String val6
		     ){

	debug = deb;
	setId(val);
	setCid(val2);
	setFileDate(val3);		
	setLoadFile(val4);
	setNotes(val5);
	setOldName(val6);
    }	
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setCid(String val){
	if(val != null)		
	    cid = val;
    }
    public void setFileDate(String val){
	if(val != null)
	    file_date = val;
    }
    public void setDate(String val){
	if(val != null)
	    file_date = val;
    }		
    public void setNotes(String val){
	if(val != null)
	    notes = val;
    }
    public void setLoadFile(String val){
	if(val != null)
	    load_file = val;
    }
    public void setName(String val){
	if(val != null)
	    load_file = val;
    }
    public void setOldName(String val){
	if(val != null)
	    old_name = val;
    }				
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public String  getCid(){
	return cid;
    }
    public String  getNotes(){
	return notes;
    }
    public String  getLoadFile(){
	return load_file;
    }
    public String  getName(){
	return load_file;
    }
    public String  getOldName(){
	return old_name;
    }		
    public String  getFileDate(){
	if(id.equals("")){
	    file_date = Helper.getToday(); // mm/dd/yyyy format
	}
	return file_date;
    }
    public String  getDate(){
	return getFileDate();
    }
    public boolean hasNotes(){
	return !notes.equals("");
    }
    public String  getErrors(){
	return errors;
    }
    public String getFullPath(String dir, String ext){
	String path = dir;
	String yy="", separator="/"; // linux
	separator = File.separator;
	if(load_file.equals("")){
	    if(id.equals("")){
		composeName(ext);
	    }
	    else{
		doSelect();
	    }
	}
	if(file_date.equals("")){
	    file_date = Helper.getToday();
	}
	if(!file_date.equals("")){
	    yy = file_date.substring(6); // year 4 digits
	}
	if(!yy.equals("")){
	    path += yy;
	}
	path += separator;
	File myDir = new File(path);
	if(!myDir.isDirectory()){
	    myDir.mkdirs();
	}
	return path;
    }

    /**
     * for download purpose
     */
    public String getPath(String dir){
	String path = dir;
	String yy="", separator="/"; // linux
	separator = File.separator;
	if(!file_date.equals("")){
	    yy = file_date.substring(6); // year 4 digits
	}
	if(!yy.equals("")){
	    path += yy;
	}
	path += separator;
	return path;
    }	
    public String composeName(String ext){
	String back = getNewIndex();
	if(back.equals("")){
	    load_file = "legal_"+cid+"_"+id+"."+ext;
	    file_date = Helper.getToday();
	}
	return back;
    }
    public String getNewIndex(){
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	file_date = Helper.getToday();
	String qq = "select max(id) from legal_files ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{

	    pstmt = con.prepareStatement(qq);			
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		id = ""+(rs.getInt(1)+1);
	    }
	    else{
		id = "1"; // to start
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
    public String doSave(){

	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	file_date = Helper.getToday();
	String qq = "insert into legal_files values(?,?,now(),?,?,?)";
	if(load_file.equals("")){
	    back = "File name not set ";
	    logger.error(back);
	    return back;
	}
	if(cid.equals("")){
	    back = "Related case id not set ";
	    logger.error(back);
	    return back;
	}				
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    logger.error(back);
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);		
	    pstmt.setString(2,cid);
	    pstmt.setString(3,load_file);
	    if(notes.equals(""))
		pstmt.setNull(4,Types.VARCHAR);
	    else
		pstmt.setString(4,notes);
	    pstmt.setString(5,old_name);						
	    pstmt.executeUpdate();
	    //
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
    public String doUpdate(){
		
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "update legal_files set notes=? ";
	qq += " where id=? ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    logger.error(back);
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    pstmt = con.prepareStatement(qq);
	    int jj=1;
	    pstmt.setString(jj++,notes);
	    pstmt.setString(jj,id);
	    pstmt.executeUpdate();
	    back += doSelect();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
	
    public String doDelete(){
		
	String back = "", qq = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    qq = "delete from legal_files where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	    load_file =  "";notes="";
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;

    }
	
    //
    public String doSelect(){
	String back = "";
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq = "select id,"+
	    " cid,"+
	    " date_format(file_date,'%m/%d/%Y'), "+						
	    " load_file,notes,old_name "+
	    " from legal_files where id=?";		
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
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		setCid(rs.getString(2));
		setFileDate(rs.getString(3));
		setLoadFile(rs.getString(4));								
		setNotes(rs.getString(5));
		setOldName(rs.getString(6));
	    }
	    else{
		return "Record "+id+" Not found";
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);			
	}
	return back;
    }	

}






















































