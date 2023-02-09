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

public class StatusList {

    boolean debug = false;
    static final long serialVersionUID = 73L;
    static Logger logger = LogManager.getLogger(StatusList.class);
    List<Status> statuses = null;

    public StatusList(boolean val){
	debug = val;
    }
    //
    // getters
    //
    public List<Status> getStatuses(){
	return statuses;
    }

    public String find(){	
	//
	String back = "";
		
	String qq = "select statusId, statusDesc "+
	    " from legal_case_status order by 2 ";
	String str="", str2="";
	Statement stmt = null;
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
	    stmt = con.createStatement();			
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		//
		str = rs.getString(1);
		str2 = rs.getString(2);
		if(str != null && str2 != null){
		    Status status = new Status(str, str2, debug);
		    if(statuses == null)
			statuses = new ArrayList<>();
		    statuses.add(status);
		}
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






















































