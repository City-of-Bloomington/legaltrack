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

public class ViolCatList {


    boolean debug = false;
    static final long serialVersionUID = 77L;
    static Logger logger = LogManager.getLogger(ViolCatList.class);

    List<ViolCat> violCats = null;

    public ViolCatList(boolean val){
	debug = val;
    }
    //
    //setters
    //

    //
    // getters
    //
    public List<ViolCat> getViolCats(){
	return violCats;
    }

    public String find(){	
	//
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "select cid, category "+
	    " from legal_viol_cats order by 2 ";
	String str="", str2="";
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
	    stmt = con.prepareStatement(qq);
	    rs = stmt.executeQuery();
	    while(rs.next()){
		//
		str = rs.getString(1);
		str2 = rs.getString(2);
		if(str != null && str2 != null){
		    ViolCat vc = new ViolCat(str, str2, debug);
		    if(violCats == null) violCats = new ArrayList<>();
		    violCats.add(vc);
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






















































