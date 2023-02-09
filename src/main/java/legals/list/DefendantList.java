package legals.list;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;


public class DefendantList implements java.io.Serializable{

    String id="", date_from="", date_to=""; // case id
	
    boolean debug = false;
    String errors = "";
    static final long serialVersionUID = 37L;
    static Logger logger = LogManager.getLogger(DefendantList.class);	
	
    Defendant defendant = null;
    List<Defendant> defendants = null;
	
    public DefendantList(boolean val){
	debug = val;
    }
	
    public DefendantList(String val, boolean deb){
	id = val;
	debug = deb;
    }
    public DefendantList(Defendant def, boolean deb){
	defendant = def;
	debug = deb;
    }	
    public void setDefendant(Defendant def){
	defendant = def; // used for search purpose
    }
    public void setId(String val){
	id = val;
    }
    public void setDateFrom(String val){
	if(val != null)
	    date_from = val;
    }
    public void setDateTo(String val){
	if(val != null)
	    date_to = val;
    }		
    //
    // getters
    //
    public List<Defendant> getDefendants(){
	return defendants;
    }
    // setters
    // finds defendants that are linked to certain case
    //

    public String find(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	String qq = "select did "+
	    "from legal_def_case where id=" + id;
	if(id.equals("")){
	    back = " Need to set case Id ";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	List<String> list = new ArrayList<String>();
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		back = " Could not connect to DB ";
		logger.error(back);
		return back;
	    }					
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		str = rs.getString(1);
		if(str != null) list.add(str);
	    }
	    if(list != null && list.size() > 0){
		for(int i=0;i<list.size();i++){
		    str = list.get(i);
		    if(str != null){
			Defendant def = new Defendant(str, debug);
			def.doSelect();
			if(defendants == null)
			    defendants = new ArrayList<Defendant>();
			if(def != null) defendants.add(def);
		    }
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);	
	}
	return back;		
    }
    //
    // Look for defendant with similar names and info
    //
    public String lookFor(){
		
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean addrFlag = false;
	String back = "";
	String qq = "select did from legal_defendents ";
	String qw = "";
	if(defendant != null){
	    String str = defendant.getDid();
	    if(str != null && !str.equals("")){
		qw += "did ="+str+"";
	    }
	    else{
		qw += "did > 0 ";
	    }
	    str = defendant.getL_name();
	    if(!str.equals("")){
		if(str.indexOf(" ")  > -1){
		    String [] tokens = str.split("\\s");
		    String all = "(";
		    for(int i=0;i<tokens.length;i++){
			if(i > 0) all += " or ";
			all += "l_name like '%"+Helper.replaceQuote(tokens[i])+"%'";
		    }
		    all += ")";
		    if(!qw.equals("")) qw += " and ";
		    qw += all;
		}
		else{
		    if(!qw.equals("")) qw += " and ";
		    qw += "l_name like '%"+Helper.replaceQuote(str)+"%'";
		}
	    }
	    str = defendant.getF_name();
	    if(!str.equals("")){
		if(str.indexOf(" ") > -1){
		    String [] tokens = str.split("\\s");
		    String all = "(";
		    for(int i=0;i<tokens.length;i++){
			if(i > 0) all += " or ";
			all += "f_name like '%"+Helper.replaceQuote(tokens[i])+"%'";
		    }
		    all += ")";
		    if(!qw.equals("")) qw += " and ";
		    qw += all;
		}
		else{
		    if(!qw.equals("")) qw += " and ";
		    qw += "f_name like '%"+Helper.replaceQuote(str)+"%'";
		}
	    }
	    if(!defendant.getSsn().equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "ssn ='"+defendant.getSsn()+"'";
	    }
	    if(!defendant.getDln().equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "dln ='"+defendant.getDln()+"'";
	    }
	    if(!defendant.getPhone().equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "( phone like '"+defendant.getPhone()+"' or phone_2 like '"+defendant.getPhone()+"') ";
	    }		
	    if(!defendant.getDob().equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "dob =str_to_date('"+defendant.getDob()+"','%m/%d/%Y')";
	    }
	    Address addr = defendant.getAddress();
	    if(addr != null){
		str = addr.getStreet_num();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.street_num ='"+str+"'";
		    addrFlag = true;
		}
		str = addr.getStreet_dir();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.street_dir ='"+str+"'";
		    addrFlag = true;
		}
		str = addr.getStreet_name();				
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.street_name like '%"+Helper.replaceQuote(str)+"%'";
		    addrFlag = true;
		}
		str = addr.getStreet_address();				
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.street_address like '%"+Helper.replaceQuote(str)+"%'";
		    addrFlag = true;
		}												
		str = addr.getStreet_type();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.street_type  like '"+str+"'";
		    addrFlag = true;
		}	
		str = addr.getSud_num();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.sud_num like '"+str+"'";
		    addrFlag = true;
		}	
		str = addr.getSud_type();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.sud_type like '"+str+"'";
		    addrFlag = true;
		}		
		str = addr.getCity();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.city like '"+str+"'";
		    addrFlag = true;
		}					
		str = addr.getState();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.state like '"+str+"'";
		    addrFlag = true;
		}					
		str = addr.getZip();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.zip like '"+str+"'";
		    addrFlag = true;
		}	
	    }
	    if(!date_from.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "a.addr_date >= str_to_date('"+date_from+"','%m/%d/%Y')";
		addrFlag = true;
	    }
	    if(!date_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "a.addr_date <= str_to_date('"+date_to+"','%m/%d/%Y')";
		addrFlag = true;
	    }			
	}
	if(addrFlag){
	    qq += ", legal_def_addresses a ";
	    qw += " and did=a.defId ";				
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}
	qq += " order by l_name,f_name ";
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	List<String> list = new ArrayList<String>();
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		back = " Could not connect to DB ";
		logger.error(back);
		return back;
	    }		
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		str = rs.getString(1);
		if(str != null) list.add(str);
	    }
	    if(list != null && list.size() > 0){
		defendants = new ArrayList<Defendant>();
		for(int i=0;i<list.size();i++){
		    str = list.get(i);
		    if(str != null){
			Defendant def = new Defendant(str, debug);
			str = def.doSelect();
			if(str.equals("")){
			    defendants.add(def);
			}
		    }
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;		
    }
    //
    // Look for defendant with similar names and info
    //
    public String lookForExact(){
		
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	String back = "";
	String qq = "select did from legal_defendents ";
	String qw = "", str="";
	boolean addrFlag = false;		
	if(defendant != null){
	    qw += " did > 0 ";
	    str = defendant.getL_name();
	    if(!str.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "l_name like '"+Helper.replaceQuote(str)+"'";
	    }
	    str = defendant.getF_name();
	    if(!str.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "f_name like '"+Helper.replaceQuote(str)+"'";
	    }
	    if(!defendant.getSsn().equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "ssn ='"+defendant.getSsn()+"'";
	    }
	    if(!defendant.getDln().equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "dln ='"+defendant.getDln()+"'";
	    }		
	    if(!defendant.getDob().equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "dob =str_to_date('"+defendant.getDob()+"','%m/%d/%Y')";
	    }
	    Address addr = defendant.getAddress();
	    if(addr != null){
		str = addr.getStreet_num();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.street_num ='"+str+"'";
		    addrFlag = true;
		}
		str = addr.getStreet_name();				
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.street_name like '"+Helper.replaceQuote(str)+"'";
		    addrFlag = true;					
		}				
		str = addr.getCity();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.city like '"+str+"'";
		    addrFlag = true;
		}					
		str = addr.getState();
		if(str != null && !str.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "a.state like '"+str+"'";
		    addrFlag = true;
		}					
	    }
	}
	if(addrFlag){
	    qq += ", legal_def_addresses a ";
	    qw += " and did = a.defId ";
	}		
	if(!qw.equals("")){
	    qq += " where "+qw;
	}
	qq += " order by l_name,f_name ";
	if(debug){
	    logger.debug(qq);
	}
	List<String> list = new ArrayList<String>();
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		back = " Could not connect to DB ";
		logger.error(back);
		return back;
	    }		
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		str = rs.getString(1);
		if(str != null) list.add(str);
	    }
	    if(list != null && list.size() > 0){
		defendants = new ArrayList<Defendant>();
		for(int i=0;i<list.size();i++){
		    str = list.get(i);
		    if(str != null){
			Defendant def = new Defendant(str, debug);
			str = def.doSelect();
			if(str.equals("")){
			    defendants.add(def);
			}
		    }
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;		
    }
}
