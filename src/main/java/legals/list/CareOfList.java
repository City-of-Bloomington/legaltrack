package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;

public class CareOfList{ //  extends ArrayList<CareOf>{

    boolean debug;
    static final long serialVersionUID = 23L;
    static Logger logger = LogManager.getLogger(CareOfList.class);
    String def_id="";
    List<CareOf> careOfs = null;
    //
    // basic constructor
    public CareOfList(boolean deb){

	debug = deb;
	//
    }
    public CareOfList(boolean deb, String def_id){

	debug = deb;
	//
	if(def_id != null && !def_id.equals(""))
	    this.def_id = def_id;
    }	
    //
    // setters
    //
    public void setDefId(String val){
	if(val != null)
	    def_id = val;
    }
    //
    // getters
    //

    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
    public List<CareOf> getCareOfs(){
	return careOfs;
    }
    public String lookFor(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "select id,def_id,co_name "+
	    " from legal_care_of ";
	String back = "";
	if(!def_id.equals("")){
	    qq += " where def_id=?";
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	else{		
	    try{
		stmt = con.prepareStatement(qq);
		if(!def_id.equals(""))
		    stmt.setString(1, def_id);
		rs = stmt.executeQuery();
		while(rs.next()){
		    CareOf one = new CareOf(debug,
					    rs.getString(1),
					    rs.getString(2),
					    rs.getString(3));
		    if(careOfs == null)careOfs = new ArrayList<>();
		    careOfs.add(one);
		}
	    }
	    catch(Exception ex){
		back = ex+":"+qq;
		logger.error(back);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	return back;
    }


}






















































