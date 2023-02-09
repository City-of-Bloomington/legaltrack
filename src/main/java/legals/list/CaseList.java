package legals.list;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.sql.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;


public class CaseList implements java.io.Serializable{

    SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");
    String f_name = "", l_name="",  invld_addr="",
	street_num="",street_dir="", id="", citeId="",source="",
	street_name="",street_type="",sud_type="",sud_num="",
	rent_street_num="",rent_street_dir="",
	rent_street_name="",rent_street_type="",rent_sud_type="",
	rent_sud_num="", pro_supp_time="", did="",
	dob_from="",dob_to="",dob_on="",ssn="",cause_num="",
	case_type="",status="", pro_supp="",
	which_date="", date_from="", date_to="",
	which_fee="", fee_from="",fee_to="",
	closed_comments="", comments="", city="",state="",zip="",
	judg_amount_from="", 
	fine_from="",court_cost_from="", citation_num="",
	judg_amount_to="", sortby="", dln="", street_address="",
	fine_to="",court_cost_to="", addrType="";	;
	
    boolean debug = false;
    String errors = "";
    static final long serialVersionUID = 24L;
    static Logger logger = LogManager.getLogger(CaseList.class);	
    boolean forRelease = false, forAddrRequest = false;
    Case lcase = null;
    List<Case> cases = new ArrayList<Case>();
	
    public CaseList(boolean val){
	debug = val;
    }
    // this is useful for list of cases that belong to certain defendant
    public CaseList(boolean deb, String val){
	setDef_id(val);
	debug = deb;
    }
    public CaseList(boolean deb, Case val){
	lcase = val;
	debug = deb;
    }	
    public void setCase(Case val){
	lcase = val; // used for search purpose
    }
    public void setSortby(String val){
	if(!val.equals("")){
	    sortby = val;
	}
    }
    public void setCause_num(String val){
	if(!val.equals("")){
	    cause_num = val.toUpperCase();
	}
    }
    public void setStatus(String val){
	if(!val.equals("")){
	    status = val;
	}
    }
    public void setPro_supp(String val){
	if(!val.equals("")){
	    pro_supp = val;
	}
    }
    public void setF_name(String val){
	if(!val.equals("")){
	    f_name = val;
	}
    }
    public void setL_name(String val){
	if(!val.equals("")){
	    l_name = val;
	}
    }
    public void setDef_id(String val){
	if(!val.equals("")){
	    did = val;
	}
    }		
    public void setDln(String val){
	if(!val.equals("")){
	    dln = val;
	}
    }	
    public void setAddrType(String val){
	if(!val.equals("")){
	    addrType = val;
	}
    }
    public void setStreet_num(String val){
	if(!val.equals("")){
	    street_num = val;
	}
    }
    public void setStreet_address(String val){
	if(!val.equals("")){
	    street_address = val;
	}
    }		
    public void setStreet_dir(String val){
	if(!val.equals("")){
	    street_dir = val;
	}
    }
    public void setStreet_name(String val){
	if(!val.equals("")){
	    street_name = val.toUpperCase();
	}
    }
    public void setStreet_type(String val){
	if(!val.equals("")){
	    street_type = val;
	}
    }
    public void setSud_type(String val){
	if(!val.equals("")){
	    sud_type = val;
	}
    }
    public void setSud_num(String val){
	if(!val.equals("")){
	    sud_num = val;
	}
    }
    public void setCity(String val){
	if(!val.equals("")){
	    city = val.toUpperCase();
	}
    }
    public void setState(String val){
	if(!val.equals("")){
	    state = val.toUpperCase();
	}
    }
    public void setZip(String val){
	if(!val.equals("")){
	    zip = val;
	}
    }
    public void setCitation_num(String val){
	if(!val.equals("")){
	    citation_num = val;
	}
    }	
    public void setInvld_addr(String val){
	if(!val.equals("")){
	    invld_addr = val;
	}
    }
    public void setDate_from(String val){
	if(!val.equals("")){
	    date_from = val;
	}
    }
    public void setDate_to(String val){
	if(!val.equals("")){
	    date_to = val;
	}
    }
    public void setWhich_date(String val){
	if(!val.equals("")){
	    which_date = val;
	}
    }
    public void setCase_type(String val){
	if(!val.equals("")){
	    case_type = val;
	}
    }
    public void setFee_from(String val){
	if(!val.equals("")){
	    fee_from = val;
	}
    }
    public void setFee_to(String val){
	if(!val.equals("")){
	    fee_to = val;
	}
    }
    public void setWhich_fee(String val){
	if(!val.equals("")){
	    which_fee = val;
	}
    }
    public void setClosed_comments(String val){
	if(!val.equals("")){
	    closed_comments = val;
	}
    }		
    public void setComments(String val){
	if(!val.equals("")){
	    comments = val.toUpperCase();
	}
    }
		
    public void setForRelease(){
	forRelease = true;
    }
    public void setForAddrRequest(){
	forAddrRequest = true;
    }
    //
    // getters
    //
    public List<Case> getCases(){
	return cases;
    }
    // setters
    //
    public String find(){
	return lookFor();
    }

    //
    // Look for defendant with similar names and info
    //
    public String lookFor(){
		
	String back = "";
	Connection con = Helper.getConnection();
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}		
	String str="", str2="", str3="",str4="";
	String qf="", qw="", qo="";
	String qq = "select c.id,"+
	    "c.case_type,"+
	    "c.status,"+
	    "c.ini_hear_time,"+
	    "c.contest_hear_time,"+
	    "c.misc_hear_time,"+
	    "c.judgment_amount,"+
	    "c.fine,"+
	    "c.court_cost,"+
	    "date_format(c.ini_hear_date,'%m/%d/%Y'),"+ // 10
			
	    "date_format(c.contest_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.misc_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.pro_supp_date,'%m/%d/%Y'),"+
	    "date_format(c.received,'%m/%d/%Y'),"+
	    "date_format(c.filed,'%m/%d/%Y'),"+
	    "date_format(c.compliance_date,'%m/%d/%Y'),"+
	    "date_format(c.judgment_date,'%m/%d/%Y'),"+
	    "date_format(c.sent_date,'%m/%d/%Y'),"+
	    "date_format(c.last_paid_date,'%m/%d/%Y'),"+
	    "date_format(c.closed_date,'%m/%d/%Y'),"+ // 20
			
	    "c.closed_comments,"+
	    "c.comments,"+
	    "c.pro_supp_time,"+
	    "c.per_day, "+
	    "c.mcc_flag, "+
	    "c.pro_supp, "+
	    "c.lawyerid, "+
	    "date_format(c.rule_date,'%m/%d/%Y'),"+
	    "c.rule_time, "+
	    "date_format(c.e41_date,'%m/%d/%Y'), "+ // 30
			
	    "date_format(c.trans_collect_date,'%m/%d/%Y'), "+
	    "date_format(c.citation_date,'%m/%d/%Y'), "+			
	    "c.citation_num, "+		//33
	    "t.typeDesc,s.statusDesc ";

				
	if(!sortby.equals("")){
	    qo = " order by "+sortby;
	}
	else
	    qo = " order by d.l_name,d.f_name ";
	qf = " from legal_cases c join legal_case_types t on c.case_type=t.typeId"+
	    " join legal_case_status s on c.status=s.statusId ";
	qf += " left join legal_def_case l on l.id=c.id "+
	    " left join legal_defendents d on l.did=d.did ";
	if(which_date.equals("paid_date")){
	    qf += " join legal_payments p on p.id=c.id ";
	}
	String suff="";
	if(addrType.startsWith("def")){
	    suff = "da";
	    qf += " left join legal_def_addresses da on d.did=da.defId ";						
	}
	else{
	    suff = "a";
	    qf += " left join legal_addresses a on c.id=a.caseId ";						
	}
	if(!ssn.equals("")){
	    qw += "d.ssn=? ";
	}
	else if(!dln.equals("")){
	    qw += "d.dln=? ";
	}	
	else if(!cause_num.equals("")){
	    qw += "l.cause_num=? ";
	}
	else {
	    if(forRelease){ // release of judgment 
		qw = " c.closed_comments='Paid' "+
		    " and c.status='CL' and judgment_date is not null ";
		if(!date_from.equals("")){
		    qw += " and c.closed_date >= str_to_date('"+date_from+"','%m/%d/%Y')";
		}
		if(!date_to.equals("")){
		    qw += " and c.closed_date <= str_to_date('"+date_to+"','%m/%d/%Y') ";
		}								
	    }
	    else if(forAddrRequest){
		// we have 0 valid address  and at least one invalid addr
		qw = " 0 = (select count(*) from  legal_def_addresses da where d.did=da.defId and da.invalid_addr is null) and 0 < (select count(*) from  legal_def_addresses da where d.did=da.defId and da.invalid_addr is not null) and not c.status='CL' "; // except closed
		if(!date_from.equals("")){
		    qw += " and d.addr_req_date >= str_to_date('"+date_from+"','%m/%d/%Y')";
		}
		if(!date_to.equals("")){
		    qw += " and d.addr_req_date <= str_to_date('"+date_to+"','%m/%d/%Y') ";
		}		
	    }
	    else{
		if(!closed_comments.equals("")){
		    if(!qw.equals("")) qw += " and ";										
		    qw = " c.closed_comments=? ";
		}
		if(!status.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw +=  "c.status=? ";
		}
		if(!pro_supp.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "c.pro_supp='y' ";
		}
		if(!f_name.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "d.f_name like  ? ";
		}
		if(!l_name.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "d.l_name like  ? ";
		}
		if(!did.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "l.did =  ? ";
		}
		if(!street_num.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += suff+".street_num = ? ";
		}
								
		if(!street_dir.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += suff+".street_dir = ? ";
		}
		if(!street_type.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += suff+".street_type = ? ";
		}
		if(!street_name.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += suff+".street_name like  ? ";
		}
		if(!sud_type.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += suff+".sud_type = ? ";
		}
		if(!sud_num.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += suff+".sud_num = ? ";
		}
		if(!city.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += " d.city like ? ";
		}
		if(!state.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "d.state like ? ";
		}
		if(invld_addr.equals("y")){
		    if(!qw.equals("")) qw += " and ";
		    if(addrType.startsWith("def"))
			qw += suff+".invld_addr is not null";
		    else
			qw += suff+".invalid_addr is not null";
		}
		if(invld_addr.equals("n")){
		    if(!qw.equals("")) qw += " and ";
		    if(addrType.startsWith("def"))
			qw += suff+".invld_addr is null";
		    else
			qw += suff+".invalid_addr is null";	
		}
		if(!zip.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "d.zip like ? ";
		}
		if(!case_type.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "c.case_type like ? ";
		}
		if(!citation_num.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "c.citation_num = ? ";
		}
		if(!date_from.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    if(which_date.equals("dob")){
			qw += "d.dob >=str_to_date('"+date_from+
			    "','%m/%d/%Y')";
		    }
		    else if(which_date.equals("addr_req_date")){
			qw += "d.addr_req_date >=str_to_date('"+date_from+
			    "','%m/%d/%Y')";
		    }
		    else if(which_date.equals("paid_date")){
			qw += "p.paid_date >=str_to_date('"+date_from+
			    "','%m/%d/%Y')";
		    }
		    else if(!which_date.equals("")){
			qw += "c."+which_date+" >=str_to_date('"+date_from+
			    "','%m/%d/%Y')";		
		    }
		}
		if(!date_to.equals("")){
		    if(which_date.equals("dob")){
			if(!qw.equals("")) qw += " and ";
												
			qw += "d.dob <=str_to_date('"+date_to+
			    "','%m/%d/%Y')";
		    }
		    else if(which_date.equals("addr_req_date")){
			if(!qw.equals("")) qw += " and ";
												
			qw += "d.addr_req_date <=str_to_date('"+date_to+
			    "','%m/%d/%Y') ";
		    }
		    else if(which_date.equals("paid_date")){
			qw += "p.paid_date <=str_to_date('"+date_to+
			    "','%m/%d/%Y')";
		    }										
		    else if(!which_date.equals("")){
			if(!qw.equals("")) qw += " and ";
												
			qw += "c."+which_date+" <=str_to_date('"+date_to+
			    "','%m/%d/%Y') ";		
		    }
		}
		if(!which_fee.equals("")){
		    if(!fee_from.equals("")){
			if(!qw.equals("")) qw += " and ";
												
			qw += "c."+which_fee+" >= ? ";
		    }
		    if(!fee_to.equals("")){
			if(!qw.equals("")) qw += " and ";
												
			qw += "c."+which_fee+" <= ? ";
		    }
		}
		if(!comments.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "upper(c.comments) like ? ";
		}
		if(!closed_comments.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += "c.closed_comments like ? ";
		}
	    }
	}
	qq += qf ;
	if(!qw.equals("")){
	    qq += " where "+qw;
	}
	qq += qo;
	if(debug){
	    logger.debug(qq);
	}
	try{
	    int c = 1;
	    pstmt = con.prepareStatement(qq);
	    if(!ssn.equals("")){
		pstmt.setString(c++, ssn);
	    }
	    else if(!dln.equals("")){
		pstmt.setString(c++, dln);								
	    }	
	    else if(!cause_num.equals("")){
		pstmt.setString(c++, cause_num);
	    }
	    else {
		if(!(forRelease || forAddrRequest)){
		    if(!closed_comments.equals("")){
			pstmt.setString(c++, closed_comments);
		    }										
		    if(!status.equals("")){
			pstmt.setString(c++, status);
		    }
		    if(!f_name.equals("")){
			pstmt.setString(c++, "%"+f_name+"%");
		    }
		    if(!l_name.equals("")){
			pstmt.setString(c++, "%"+l_name+"%");
		    }
		    if(!did.equals("")){
			pstmt.setString(c++, did);
		    }
		    if(!street_num.equals("")){
			pstmt.setString(c++, street_num);
		    }
		    if(!street_dir.equals("")){
			pstmt.setString(c++, street_dir);
		    }
		    if(!street_type.equals("")){
			pstmt.setString(c++, street_type);
		    }
		    if(!street_name.equals("")){
			pstmt.setString(c++, street_name+"%");
		    }
		    if(!sud_type.equals("")){
			pstmt.setString(c++, sud_type);
		    }
		    if(!sud_num.equals("")){
			pstmt.setString(c++, sud_num);
		    }
		    if(!city.equals("")){
			pstmt.setString(c++, city);
		    }
		    if(!state.equals("")){
			pstmt.setString(c++, state);
		    }
		    if(!zip.equals("")){
			pstmt.setString(c++, zip+"%");
		    }
		    if(!case_type.equals(""))
			pstmt.setString(c++, case_type);
		    if(!citation_num.equals(""))
			pstmt.setString(c++, "%"+citation_num+"%");
		    if(!which_fee.equals("")){
			if(!fee_from.equals("")){
			    pstmt.setString(c++, fee_from);
			}
			if(!fee_to.equals("")){
			    pstmt.setString(c++, fee_to);
			}
		    }
								
		    if(!comments.equals("")){
			pstmt.setString(c++, "%"+comments+"%");
		    }
		    if(!closed_comments.equals("")){
			pstmt.setString(c++, "%"+closed_comments+"%");
		    }
		}
	    }
	    rs = pstmt.executeQuery();
	    Set<String> set = new HashSet<String>();
	    while(rs.next()){
		if(cases == null)
		    cases = new ArrayList<Case>();				
		str = rs.getString(1); // id avoid dups
		if(str != null && !set.contains(str)){
		    set.add(str);
		    Case one = new Case(debug,
					rs.getString(1),
					rs.getString(2),
					rs.getString(3),
					rs.getString(4),
					rs.getString(5),
					rs.getString(6),
					rs.getString(7),
					rs.getString(8),
					rs.getString(9),
					rs.getString(10),
					rs.getString(11),
					rs.getString(12),
					rs.getString(13),
					rs.getString(14),
					rs.getString(15),
					rs.getString(16),
					rs.getString(17),
					rs.getString(18),
					rs.getString(19),
					rs.getString(20),
					rs.getString(21),
					rs.getString(22),
					rs.getString(23),
					rs.getString(24),
					rs.getString(25),
					rs.getString(26),
					rs.getString(27),
					rs.getString(28),
					rs.getString(29),
					rs.getString(30),
					rs.getString(31),
					rs.getString(32),
					rs.getString(33));
		    str = rs.getString(2); //type_id
		    str2 = rs.getString(3); // status_id
		    str3 = rs.getString(34); // type
		    str4 = rs.getString(35); // status;
		    if(str != null && str3 != null){
			CaseType ct = new CaseType(str, str3, debug);
			one.setCaseType(ct);
		    }
		    if(str2 != null && str4 != null){
			Status st = new Status(str2, str4, debug);
			one.setCaseStatus(st);
		    }
		    if(!cases.contains(one))
			cases.add(one);
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;		
    }

}
