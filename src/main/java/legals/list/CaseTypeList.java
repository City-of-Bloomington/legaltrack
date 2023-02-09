package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;


public class CaseTypeList{


    boolean debug = false;
    boolean freeTypes = false; // flag to search for unassinged to lawyers
    String lawyerId = "", deptId = "", deptName = "";
    String typeId = "", typeDesc = "";
    static final long serialVersionUID = 26L;
    static Logger logger = LogManager.getLogger(CaseTypeList.class);
    List<CaseType> caseTypes = null;
    public CaseTypeList(boolean val){
	debug = val;
    }
    //
    //setters
    //
    public void setFreeTypes(){
	freeTypes = true;
    }
    public void setLawyerId(String val){
	if(val != null)
	    lawyerId = val;
    }
    public void setDeptId(String val){
	if(val != null)
	    deptId = val;
    }
    public void setDeptName(String val){
	if(val != null)
	    deptName = val;
    }
    public void setTypeId(String val){
	if(val != null)
	    typeId = val;
    }
    public void setTypeDesc(String val){
	if(val != null)
	    typeDesc = val;
    }
    //
    // getters
    //
    public List<CaseType> getTypes(){
	return caseTypes;
    }

    public String find(){	
	//
	String back = "";
	Statement stmt = null;
	ResultSet rs = null;		
	String q = " select typeId, typeDesc from legal_case_types ";
	String qw = "", qo = " order by 2 ";
	if(freeTypes){
	    qw = " where typeId not in "+
		"(select typeId from legal_lawyer_types)";
	}
	else if(!lawyerId.equals("")){
	    qw = " where typeId in "+
		"(select typeId from legal_lawyer_types where lawyerid='"+lawyerId+"')";
	}
	else if(!deptId.equals("")){
	    qw = " where typeId in "+
		"(select typeId from legal_type_dept where deptId='"+deptId+"')";	
	}
	else if(!deptName.equals("")){
	    qw = " where typeId in "+
		"(select typeId from legal_type_dept lt,legal_depts ld where lt.deptId= ld.deptId and ld.dept like '"+deptName+"')";	
	}
	else if(!typeId.equals("")){
	    qw = " where typeId = "+typeId;
	}
	else if(!typeDesc.equals("")){
	    qw = " where typeDesc like '"+typeDesc+"'";
	}
	String qq = q + qw + qo;
	String str="", str2="";
	Connection con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
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
		str = rs.getString(1);
		str2 = rs.getString(2);
		if(str != null && str2 != null){
		    CaseType type = new CaseType(str, str2, debug);
		    if(caseTypes == null) caseTypes = new ArrayList<>();
		    caseTypes.add(type);
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






















































