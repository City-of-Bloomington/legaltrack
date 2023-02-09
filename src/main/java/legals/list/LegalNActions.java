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

public class LegalNActions{

    boolean debug;
    String id="",reason="",startBy="",status="", rental_id="";
    String dateFrom="",dateTo="", whichDate="", attention="", case_id="";
    List<LegalNAction> legActions = null;
    static final long serialVersionUID = 46L;
    Logger logger = LogManager.getLogger(LegalNActions.class);
    //
    public LegalNActions(boolean deb){

	debug = deb;
    }
    //
    // setters
    //
    public void  setId(String val){
	id = val;
    }
    public void  setRental_id(String val){
	rental_id = val;
    }
    public void  setCase_id(String val){
	case_id = val;
    }
    public void  setStartBy(String val){
	startBy = val;
    }
    public void  setStatus(String val){
	status = val;
    }
    public void  setAttention(String val){
	attention = val;
    }
    public void  setDateFrom(String val){
	dateFrom = val;
    }
    public void  setDateTo(String val){
	dateTo = val;
    }
    public void  setReason(String val){
	reason = val;
    }
    public void  setWhichDate(String val){
	whichDate = val;
    }
    //
    // getters
    //
    public List<LegalNAction> getLegActions(){
	return legActions;
    }
    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
    public String lookFor(){

	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "select t.lid,t.aid,t.reason,t.actionBy,t.fullName,t.rental_id,t.status,t.date,t.attention,t.case_id,t.sortDate from ";
	String qq2 = "select l.id lid,a.id aid,a.notes reason,a.actionBy actionBy,u.fullName fullName,l.rental_id rental_id,l.status status,date_format(a.actionDate,'%m/%d/%Y') date, l.attention attention, l.case_id case_id,a.actionDate sortDate from rental_legals l join legal_actions a on a.legal_id=l.id left join users u on u.userid=a.actionBy where a.id=(select max(aa.id) from legal_actions aa where aa.legal_id=l.id)";
	String qq3 = "select l.id lid,0 aid,l.reason reason,l.startBy actionBy,u.fullName fullName,l.rental_id rental_id,l.status status,date_format(l.startDate,'%m/%d/%Y') date, l.attention attention, l.case_id case_id,l.startDate sortDate from rental_legals l left join users u on u.userid=l.startBy where l.id not in (select aa.legal_id from legal_actions aa) ";			 
	String where = "";
	if(!attention.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += " t.attention = ?";// '"+attention+"'";
	}
	if(!status.equals("")){
	    if(!where.equals("")) where += " and ";
	    if(status.equals("both")) // need this for legal dept
		where += "(t.status=? or t.status=?)";			
	    else
		where += "t.status=?";
	}
	qq = qq +"("+qq2+" union "+qq3+") t ";
	if(!where.equals("")){
	    qq += " where "+where;
	}
	qq += " order by t.sortDate desc ";
	// System.err.println(qq);
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    int jj=1;
	    stmt = con.prepareStatement(qq);
	    if(!attention.equals("")){
		stmt.setString(jj++, attention);		
	    }

	    if(!status.equals("")){
		if(status.equals("both")){ // need this for legal dept
		    stmt.setString(jj++, "New");
		    stmt.setString(jj++, "Pending");	
		}
		else
		    stmt.setString(jj++, status);
	    }
	    rs = stmt.executeQuery();
	    while(rs.next()){
		if(legActions == null)
		    legActions = new ArrayList<>();
		LegalNAction one =
		    new LegalNAction(debug,
				     rs.getString(1),
				     rs.getString(2),
				     rs.getString(3),
				     rs.getString(4),
				     rs.getString(5),
				     rs.getString(6),
				     rs.getString(7),
				     rs.getString(8),
				     rs.getString(9),
				     rs.getString(10)
				     );
		legActions.add(one);
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}		
	return back;
				
    }


}






















































