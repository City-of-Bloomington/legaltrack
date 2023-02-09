package legals.model;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import java.text.*;
import javax.sql.*;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
/**
 *
 *
 */

public class CaseViolMultiple {


    boolean debug = false;
    static final long serialVersionUID = 28L;
    static Logger logger = LogManager.getLogger(CaseViolMultiple.class);

    String vid = "", sid="", id="", ident="", entered="";
    String cid="";
    String dates[] = {"","","","",""};
    String amount[] = {"","","","",""};
    String citations[] = {"","","","",""};
    ViolSubcat violSubcat = null;
    ViolCat violCat = null;
    public CaseViolMultiple(boolean val){
	debug = val;
    }
	
    public CaseViolMultiple(String val, boolean deb){
	vid = val;
	debug = deb;
    }
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setSid(String val){
	if(val != null)
	    sid = val;
    }
    public void setCid(String val){
	if(val != null)
	    cid = val;
    }	
    public void setDates(String name, String val){
	if(val != null && !val.equals("")){
	    int jj = -1;
	    try{
		jj = Integer.parseInt(name.substring(5));
	    }
	    catch(Exception ex){};
	    if(jj > -1){
		dates[jj] = val;
	    }
	}
    }
    public void setAmount(String name, String val){
	if(val != null && !val.equals("")){
	    int jj = -1;
	    try{
		jj = Integer.parseInt(name.substring(6));
	    }
	    catch(Exception ex){};
	    if(jj > -1){
		amount[jj] = val;
	    }
	}
    }
    public void setEntered(String val){
	if(val != null)
	    entered = val;
    }
    public void setCitations(String name, String val){
	if(val != null && !val.equals("")){
	    int jj = -1;
	    try{
		jj = Integer.parseInt(name.substring(9));
	    }
	    catch(Exception ex){};
	    if(jj > -1){
		citations[jj] = val;
	    }
	}
    }	
    //
    // getters
    //
    public String getId(){
	return id ;
    }
    public String getSid(){
	return sid ;
    }
    public String getCid(){
	// for the old violation cid was in subcat
	// therefore we check to see if this is an old violation
	// if so we get cid from it
	if(violSubcat == null){
	    getViolSubcat();
	}
	if(violSubcat != null){
	    cid = violSubcat.getCid();
	}
	return cid ;
    }	
    public String getIdent(){
	return ident;
    }
    public String getEntered(){
	return entered;
    }
    //
    public ViolSubcat getViolSubcat(){
	if(violSubcat == null && !sid.equals("")){
	    violSubcat = new ViolSubcat(sid, debug);
	    violSubcat.doSelect();
	}
	return violSubcat;
    }
    public ViolCat getViolCat(){
	if(violCat == null){
	    if(cid.equals("") && violSubcat == null){
		getViolSubcat();
	    }
	    if(violSubcat != null){
		cid = violSubcat.getCid();
	    }
	    if(!cid.equals("")){
		ViolCat vc = new ViolCat(cid, debug);
		String str = vc.doSelect();
		if(str.equals("")){
		    violCat = vc;
		}
	    }
	}
	return violCat;
    }
    public String doSave(){
	//
	String back = "";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	String qq = "";
	try{
	    qq = "insert into legal_case_violations values(0,?,?,?,?,?,CURRENT_DATE,?,?)";
	    pstmt = con.prepareStatement(qq);
	    for(int jj = 0;jj<5;jj++){
		if(!amount[jj].equals("") && !dates[jj].equals("")){
		    pstmt.setString(1, id);
		    pstmt.setString(2, sid);
		    pstmt.setNull(3,Types.VARCHAR);
		    pstmt.setString(4,dates[jj]);
		    pstmt.setString(5,amount[jj]);
		    if(citations[jj].equals(""))
			pstmt.setNull(6,Types.VARCHAR);				
		    else
			pstmt.setString(6,citations[jj]);						
		    pstmt.setString(7, cid);
		    pstmt.executeUpdate();
		}
	    }	
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}				
	return back;
    }

}






















































