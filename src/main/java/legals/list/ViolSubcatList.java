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

public class ViolSubcatList {

    boolean debug = false;
    static final long serialVersionUID = 81L;
    static Logger logger = LogManager.getLogger(ViolSubcatList.class);
    List<ViolSubcat> violSubcats = null;
    String cid = "";
    public ViolSubcatList(boolean val){
	debug = val;
    }
    public ViolSubcatList(String val, boolean deb){
	cid = val;
	debug = deb;
    }
    //
    // getters
    //
    public List<ViolSubcat> getViolSubcats(){
	return violSubcats;
    }

    public String find(){	
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String back = "";
	String qq = "select sid, cid, subcat, complaint, codes, amount "+
	    " from legal_viol_subcats ";
	if(!cid.equals("")){
	    qq += " where cid="+cid;
	}
	qq += " order by subcat ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		//
		String str="", str2="", str3="", str4="", str5="", str6="";

		str = rs.getString(1);
		str2 = rs.getString(2);
		str3 = rs.getString(3);
		str4 = rs.getString(4);
		str5 = rs.getString(5);
		str6 = rs.getString(6);
		if(str != null && str2 != null){
		    ViolSubcat vs = new ViolSubcat(str, str2,
						   str3, str4,
						   str5, str6,
						   debug);
		    if(violSubcats == null) violSubcats = new ArrayList<>();
		    violSubcats.add(vs);
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






















































