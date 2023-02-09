package legals.model;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
/**
 *
 *
 */

public class User implements java.io.Serializable{

    String userid="", fullName="", dept="", role="";
    boolean debug = false, userExist = false;
    String errors = "";
    static final long serialVersionUID = 76L;
    static Logger logger = LogManager.getLogger(User.class);
    //
    public User(boolean deb, String _userid){
	debug = deb;
	userid=_userid;
	errors += doSelect();
    }
    //
    public User(boolean deb){
	//
	// initialize
	//
	debug = deb;
    }
	
    //
    public User(String val){
	//
	// initialize
	//
	userid = val;
    }
    public boolean userExists(){
	return userExist;
    }
    //
    public String doSave(){
	String msg = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "insert into users values(?,?,?,?)";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, userid);
	    if(dept.isEmpty())
		stmt.setNull(2, Types.VARCHAR);
	    else
		stmt.setString(2, dept);
	    stmt.setString(3, fullName);
	    if(role.isEmpty())
		stmt.setNull(4, Types.VARCHAR);
	    else
		stmt.setString(4, role);
	    stmt.executeUpdate();
	}catch(Exception ex){
	    logger.error(ex+" : "+qq);
	}	
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return msg;
    }
    //
    public String doSelect(){
	String msg = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	String qq = " select * from users where userid='"+userid+"'";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    msg = "Could not connect to DB";
	    return msg;
	}			
	try{
	    stmt = con.createStatement();			
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		String str = rs.getString(2);
		if(str !=null) dept = str;
		str = rs.getString(3);
		if(str !=null) fullName = str;
		str = rs.getString(4);
		if(str !=null) role = str;
		userExist = true;
	    }
	    else{
		msg = " No such user";
	    }
	    rs = null;
	}catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    msg += " "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return msg;
    }
    //
    public String doUpdate(){
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;	
	String str="", msg="";
	String qq = "";
	qq = "update users set fullName=?,role=?,dept=? where userid=?";
	//
	logger.debug(qq);
	con = Helper.getConnection();
	if(con == null){
	    msg = "Could not connect to DB";
	    return msg;
	}			
	try{
	    stmt = con.prepareStatement(qq);			
	    stmt.setString(1, fullName);
	    if(role.equals(""))
		stmt.setNull(2, Types.VARCHAR);		
	    else
		stmt.setString(2, role);
	    if(dept.equals(""))
		stmt.setNull(3, Types.VARCHAR);	
	    else
		stmt.setString(3, dept);
	    stmt.setString(4, userid);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return msg; 
    }
    //
    public String doDelete(){

	String str="";
	String qq = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		

	qq = "delete from  users where userid=?";
	//
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, userid);
	    stmt.executeUpdate();
	    //
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    return ex.toString();
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}	
	return ""; // success
    }
    //
    public boolean hasRole(String val){
	if(role != null && role.indexOf(val) > -1) return true;
	return false;
    }
    public boolean canEdit(){
	return hasRole("Edit");
    }
    public boolean canDelete(){
	return hasRole("Delete");
    }
    public boolean isAdmin(){
	return hasRole("Admin");
    }
    public boolean isInDept(String val){
	if(dept != null){
	    if(dept.equals("All")) return true;
	    else {
		return dept.indexOf(val) > -1;
	    }
	}
	return false;
    }
    //
    // getters
    //
    public String getUserid(){
	return userid;
    }
    public String getFullName(){
	return fullName;
    }
    public String getDept(){
	return dept;
    }
    public String getRole(){
	return role;
    }
    public String getErrors(){
	return errors;
    }
    //
    // setters
    //
    public void setUserid (String val){
	userid = val;
    }
    public void setFullName (String val){
	fullName = val;
    }
    public void setRole (String val){
	role = val;
    }
    public void setDept (String val){
	dept = val;
    }
    public String toString(){
	if(fullName == null) return "";
	else return fullName;
    }

}
