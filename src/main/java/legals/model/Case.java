package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
import legals.list.*;
/**
 *
 *
 */

public class Case {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");	
    boolean debug = false, basic = false;
    static Logger logger = LogManager.getLogger(Case.class);
    String 
	id="", citeId="",source="", citation_num="",
	per_day="",
	dob="",ssn="",case_type="",status="PD",
	ini_hear_date="",contest_hear_date="", post_dir="",
	misc_hear_date="", received="", filed="", pro_supp="",
	ini_hear_time="08:30",contest_hear_time="", e41_date="",
	misc_hear_time="", pro_supp_time="", mcc_flag="", rule_date="",rule_time="",
	judgment_date="",pro_supp_date="",compliance_date="",
	judgment_amount="", sent_date="", invld_addr="", addr_req_date="",
	fine="",court_cost="",last_paid_date="",closed_date="",
	closed_comments="", comments="", trans_collect_date="",
	lawyerid="", citation_date="";
	
    Status cStatus = null;
    CaseType caseType = null;
    Lawyer lawyer = null;
    List<Defendant> defendants = null;
    List<Address> addresses = null;
    List<CaseViolation> caseViolations = null;
    List<ViolGroup> violGroups = null;
    List<String> causeNums = null;
    List<Animal> pets = null;
    List<LegalFile> files = null;
    public Case(boolean val){
	debug = val;
    }
	
    public Case(boolean deb, String val){
	setId(val);
	debug = deb;
    }

    public Case(boolean deb, boolean base, String val){
	setId(val);
	debug = deb;
	basic = base; // when not all features are needed;
    }
    // coming from rental 
    public Case(boolean deb,
		String val,
		String val2,
		String val3,
		String val4){
	debug = deb;
	setCase_type(val);
	setStatus(val2); // case_status
	setReceived(val3);				
	setComments(val4); 
    }		
    public Case(boolean deb,
		String val,
		String val2,
		String val3,
		String val4,
		String val5,
		String val6,
		String val7,
		String val8,
		String val9,
		String val10,
		String val11,
		String val12,
		String val13,
		String val14,
		String val15,
		String val16,
		String val17,
		String val18,
		String val19,
		String val20,
		String val21,
		String val22,
		String val23,
		String val24,
		String val25,
		String val26,
		String val27,
		String val28,
		String val29,
		String val30,
		String val31,
		String val32,
		String val33
		){
				
	debug = deb;
	setId(val);
	setCase_type(val2);
	setStatus(val3);
	setIni_hear_time(val4);
	setContest_hear_time(val5);
	setMisc_hear_time(val6);
	setJudgment_amount(val7);
	setFine(val8);
	setCourt_cost(val9);
	setIni_hear_date(val10);
				
	setContest_hear_date(val11);
	setMisc_hear_date(val12);
	setPro_supp_date(val13);
	setReceived(val14);
	setFiled(val15);
	setCompliance_date(val16);
	setJudgment_date(val17);
	setSent_date(val18);
	setLast_paid_date(val19);
	setClosed_date(val20);
				
	setClosed_comments(val21);
	setComments(val22);
	setPro_supp_time(val23);
	setPer_day(val24);
	setMcc_flag(val25);
	setPro_supp(val26);
	setLawyerid(val27);
	setRule_date(val28);
	setRule_time(val29);
	setE41_date(val30);
				
	setTrans_collect_date(val31);
	setCitation_date(val32);
	setCitation_num(val33);
    }
    //
    //setters
    //
	
    public void  setId(String val){
	if(val != null)
	    id = val;
    }
    public void  setReceived(String val){
	if(val != null)
	    received = val;
    }
    public void  setSource(String val){
	if(val != null)
	    source = val;
    }
    public void  setCitation_num(String val){
	if(val != null)
	    citation_num = val;
    }	
    public void  setCase_type(String val){
	if(val != null)
	    case_type = val;
    }
    public void  setStatus(String val){
	if(val != null)
	    status = val;
    }
    public void  setTrans_collect_date(String val){
	if(val != null)
	    trans_collect_date = val;
    }	
    public void  setSent_date(String val){
	if(val != null)
	    sent_date = val;
    }
    public void  setCitation_date(String val){
	if(val != null)
	    citation_date = val;
    }	
    public void  setIni_hear_date(String val){
	if(val != null)
	    ini_hear_date = val;
    }
    public void  setIni_hear_time(String val){
	if(val != null)
	    ini_hear_time = val;
    }
    public void  setContest_hear_date(String val){
	if(val != null)
	    contest_hear_date = val;
    }
    public void  setContest_hear_time(String val){
	if(val != null)
	    contest_hear_time = val;
    }
    public void  setMisc_hear_date(String val){
	if(val != null)
	    misc_hear_date = val;
    }
    public void  setMisc_hear_time(String val){
	if(val != null)
	    misc_hear_time = val;
    }
    public void  setFiled(String val){
	if(val != null)
	    filed = val;
    }
    public void  setJudgment_date(String val){
	if(val != null)
	    judgment_date = val;
    }
    public void  setCompliance_date(String val){
	if(val != null)
	    compliance_date = val;
    }
    public void  setPro_supp_date(String val){
	if(val != null)
	    pro_supp_date = val;
    }
    public void  setJudgment_amount(String val){
	if(val != null)
	    judgment_amount = val;
    }
    public void  setFine(String val){
	if(val != null)
	    fine = val;
    }
    public void  setCourt_cost(String val){
	if(val != null)
	    court_cost = val;
    }
    public void  setLast_paid_date(String val){
	if(val != null)
	    last_paid_date = val;
    }
    public void  setClosed_date(String val){
	if(val != null)
	    closed_date = val;
    }
    public void  setClosed_comments(String val){
	if(val != null)
	    closed_comments = val;
    }
    public void  setComments(String val){
	if(val != null)
	    comments = val;
    }
    public void  setPro_supp_time(String val){
	if(val != null)
	    pro_supp_time = val;
    }
    public void  setPer_day(String val){
	if(val != null)
	    per_day = val;
    }
    public void  setMcc_flag(String val){
	if(val != null)
	    mcc_flag = val;
    }
    public void  setPro_supp(String val){
	if(val != null)
	    pro_supp = val;
    }
    public void  setLawyerid(String val){
	if(val != null)
	    lawyerid = val;
    }
    public void  setRule_date(String val){
	if(val != null)
	    rule_date = val;
    }
    public void  setRule_time(String val){
	if(val != null)
	    rule_time = val;
    }
    public void  setE41_date(String val){
	if(val != null)
	    e41_date = val;
    }
    public void  setCaseType(CaseType val){
	if(val != null)
	    caseType = val;
    }
    public void  setLawyer(Lawyer val){
	if(val != null)
	    lawyer = val;
    }
    public void  setCaseStatus(Status val){
	if(val != null)
	    cStatus = val;
    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getCase_type(){
	return case_type ;
    }
    public String getStatus(){
	return status ;
    }
    public String getCitation_num(){
	return citation_num ;
    }	
    public String getSent_date(){
	return sent_date ;
    }
    public String getTrans_collect_date(){
	return trans_collect_date ;
    }	
    public String getIni_hear_date(){
	return ini_hear_date ;
    }
    public String getIni_hear_time(){
	return ini_hear_time ;
    }
    public String getContest_hear_date(){
	return contest_hear_date ;
    }
    public String getContest_hear_time(){
	return contest_hear_time ;
    }
    public String getMisc_hear_date(){
	return misc_hear_date ;
    }
    public String getMisc_hear_time(){
	return misc_hear_time ;
    }
    public String getFiled(){
	return filed ;
    }
    public String getJudgment_date(){
	return judgment_date ;
    }
    public String getCompliance_date(){
	return compliance_date ;
    }
    public String getPro_supp_date(){
	return pro_supp_date ;
    }
    public String getJudgment_amount(){
	return judgment_amount ;
    }
    public String getAll_hearings(){
	String hearings = "";
	hearings = getIni_hear_date();
	String str = getContest_hear_date();
	if(!str.equals("")){
	    if(!hearings.equals("")) hearings += ", ";
	    hearings += str;
	}
	str = getMisc_hear_date();
	if(!str.equals("")){
	    if(!hearings.equals("")) hearings += ", ";
	    hearings += str;
	}
	str = getPro_supp_date();
	if(!str.equals("")){
	    if(!hearings.equals("")) hearings += ", ";
	    hearings += str;
	}
	return hearings;
    }
    public String getFine(){
	return fine ;
    }
    public String getCourt_cost(){
	return court_cost ;
    }
    public String getLast_paid_date(){
	return last_paid_date ;
    }
    public String getClosed_date(){
	return closed_date ;
    }
    public String getClosed_comments(){
	return closed_comments ;
    }
    public String getComments(){
	return comments ;
    }
    public String getPro_supp_time(){
	return pro_supp_time ;
    }
    public String getPer_day(){
	return per_day ;
    }
    public String getMcc_flag(){
	return mcc_flag ;
    }
    public String getPro_supp(){
	return pro_supp ;
    }
    public String getLawyerid(){
	return lawyerid ;
    }
    public String getRule_date(){
	return rule_date ;
    }
    public String getRule_time(){
	return rule_time ;
    }
    public String getE41_date(){
	return e41_date ;
    }
    public String getCitation_date(){
	return citation_date ;
    }	
    public String getReceived(){
	return received ;
    }
    public Status getCStatus(){
	if(cStatus == null && !status.equals("")){
	    Status cs = new Status(status, debug);
	    String back = cs.doSelect();
	    if(back.equals("")){
		cStatus = cs;
	    }
	}
	return cStatus;
    }
    public Lawyer getLawyer(){
	if(lawyer == null && !lawyerid.equals("")){
	    Lawyer lyr = new Lawyer(null, lawyerid, debug);
	    String back = lyr.doSelect();
	    if(back.equals("")){
		lawyer = lyr;
	    }
	}
	return lawyer;
    }
    public CaseType getCaseType(){
	if(caseType == null && !case_type.equals("")){
	    CaseType ct = new CaseType(case_type, debug);
	    String back = ct.doSelect();
	    if(back.equals("")){
		caseType = ct;
	    }
	}
	return caseType;
    }
    public List<Defendant> getDefendants(){
	if(defendants == null){
	    findDefendants();
	}
	return defendants;
		
    }
    public List<Address> getAddresses(){
	if(addresses == null){
	    findAddresses();
	}
	return addresses;
    }
    public List<String> getCauseNums(){
	if(causeNums == null){
	    findCauseNums();
	}
	return causeNums;
    }
    public String getCauseNumsStr(){
	if(causeNums == null){
	    findCauseNums();
	}
	String ret = "";
	if(causeNums != null){
	    for(String str:causeNums){
		if(!ret.equals("")){
		    ret += ", ";
		}
		ret += str;
	    }
	}
	return ret;
    }		
    public List<Animal> getPets(){
	return pets;
    }
    public boolean hasAnimals(){
	return pets != null && pets.size() > 0;
    }
    public boolean hasViolations(){
	if(caseViolations == null){
	    getCaseViolations();
	}
	if(caseViolations != null && caseViolations.size() > 0) return true;
	return false;
    }
    //
    public List<CaseViolation> getCaseViolations(){
		
	if(caseViolations == null && !id.equals("")){
	    CaseViolationList cvl = new CaseViolationList(id, debug);
	    String back = cvl.find();
	    if(back.equals("")){
		caseViolations = cvl.getCaseViolations();
	    }
	}
	return caseViolations;
    }
    public List<ViolGroup> getViolGroups(){
		
	if(violGroups == null && !id.equals("")){
	    if(caseViolations == null){
		getCaseViolations();
	    }
	    if(caseViolations != null){
		ViolGroupList violgl = new ViolGroupList(debug, caseViolations);
		violGroups = violgl.getViolGroups();
	    }
	}
	return violGroups;
    }
    //
    // one of these dates must be entered when the data are
    // entered the first time
    //
    private boolean validateRequiredDates(){
	// this condition is relaxed according to Patty (legal)
	return true;
    }
    public boolean equals(Object gg){
	boolean match = false;
	if (gg != null && gg instanceof Case){
	    match = id.equals(((Case)gg).id);
	}
	return match;
    }
    public int hashCode(){
	int code = 0;
	try{
	    code = Integer.parseInt(id);
	}catch(Exception ex){};
	return code;
    }
    public String toString(){
	return id;
    }
    public boolean hasFiles(){
	if(files == null)
	    findFiles();
	return files != null && files.size() > 0;
    }
    public List<LegalFile> getFiles(){
	return files;
    }
    public void  findFiles(){
	if(!id.equals("") && files == null){
	    LegalFileList lfl = new LegalFileList(debug, id);
	    String back = lfl.find();
	    if(back.equals("")){
		List<LegalFile> ones = lfl.getFiles();
		if(ones != null && ones.size() > 0){
		    files = ones;
		}
	    }
	}
    }
    /*
     * find out if this case has letter texts associated with
     * the violation type
     */
    public boolean hasLetterText(){

	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	String qq = "select count(*) from doc_texts where type= ? ";
	// case_type+"'",
	String back = "";
	boolean ret = false;
	if(case_type.equals("")) return false;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return false;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, case_type);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		if(rs.getInt(1) > 0) ret = true;
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return ret;
    }
    public String doSave(){
	//
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	// pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(date_writen).getTime()));								
	String qq = "";			
	try{
	    qq = "insert into legal_cases values "+
		"(0,?,?,?,?, ?,?,?,?,?,"+
		"?,?,?,?,?, ?,?,?,?,?,"+
		"?,?,?,?,?, ?,?,?,?,?,"+
		"?,?,?)";
	    stmt = con.prepareStatement(qq);
	    back = setParams(stmt);
	    stmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    logger.debug(qq);
	    stmt2 = con.prepareStatement(qq);
	    rs = stmt2.executeQuery(qq);
	    if(rs.next()){
		id = rs.getString(1);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save date ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}
	back += resetObjects();			
	return back;
    }
    String setParams(PreparedStatement stmt){

	String back = "";
	int jj=1;
	try{
	    if(received.equals("")){
		received = Helper.getToday();
	    }
	    stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(received).getTime()));
	    if(case_type.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);
	    }
	    else {
		stmt.setString(jj++, case_type);
	    }
	    if(status.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);
	    }
	    else {
		stmt.setString(jj++, status);
	    }				
	    if(sent_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(sent_date).getTime()));						
	    if(ini_hear_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(ini_hear_date).getTime()));							
	    if(ini_hear_time.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);				
	    }
	    else {
		stmt.setString(jj++, ini_hear_time);
	    }
	    if(contest_hear_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(contest_hear_date).getTime()));									
	    if(contest_hear_time.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);				
	    }
	    else {
		stmt.setString(jj++, contest_hear_time);
	    }
	    if(misc_hear_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(misc_hear_date).getTime()));
	    if(misc_hear_time.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);				
	    }
	    else {
		stmt.setString(jj++, misc_hear_time);
	    }
	    if(filed.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(filed).getTime()));
	    if(judgment_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(judgment_date).getTime()));
	    if(compliance_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(compliance_date).getTime()));
	    if(pro_supp_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(pro_supp_date).getTime()));
	    if(judgment_amount.equals(""))
		judgment_amount="0";
	    stmt.setString(jj++, judgment_amount);
	    if(fine.equals(""))
		fine= "0";
	    stmt.setString(jj++, fine);
	    if(court_cost.equals(""))
		court_cost = "0";
	    stmt.setString(jj++, court_cost);
	    if(last_paid_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(last_paid_date).getTime()));				
	    if(closed_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(closed_date).getTime()));		
	    if(closed_comments.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);				
	    }
	    else {
		stmt.setString(jj++, closed_comments);
	    }
	    if(comments.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);				
	    }
	    else {
		stmt.setString(jj++,comments);
	    }
	    if(pro_supp_time.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);				
	    }
	    else {
		stmt.setString(jj++, pro_supp_time);
	    }
	    if(per_day.equals("")){
		stmt.setNull(jj++, Types.CHAR);	
	    }
	    else {
		stmt.setString(jj++, per_day);
	    }
	    if(mcc_flag.equals("")){
		stmt.setNull(jj++, Types.CHAR);
	    }
	    else{
		stmt.setString(jj++, "y");
	    }
	    if(pro_supp.equals("")){
		stmt.setNull(jj++, Types.CHAR);
	    }
	    else{
		stmt.setString(jj++, "y");
	    }				
	    if(lawyerid.equals("")){
		stmt.setNull(jj++, Types.VARCHAR);
	    }
	    else{
		stmt.setString(jj++, lawyerid);
	    }
	    if(rule_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(rule_date).getTime()));		
	    if(rule_time.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, rule_time);
	    if(e41_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(e41_date).getTime()));		
	    if(citation_num.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else{
		stmt.setString(jj++,citation_num);
	    }
	    if(trans_collect_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(trans_collect_date).getTime()));		
	    if(citation_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else
		stmt.setDate(jj++, new java.sql.Date(dateFormat.parse(citation_date).getTime()));		
	}catch(Exception ex){
	    back += ex;
	}
	return back;
    }
    //
    public String doUpdate(){
	//
	String str = "", back = "", oldStatus = "";

	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "update legal_cases set "+
	    "received=?,"+
	    "case_type=?,"+
	    "status=?,"+
	    "sent_date=?,"+
	    "ini_hear_date=?,"+
						
	    "ini_hear_time=?,"+
	    "contest_hear_date=?,"+
	    "contest_hear_time=?,"+
	    "misc_hear_date=?,"+
	    "misc_hear_time=?,"+
						
	    "filed=?,"+
	    "judgment_date=?,"+
	    "compliance_date=?,"+
	    "pro_supp_date=?,"+
	    "judgment_amount=?,"+
						
	    "fine=?,"+
	    "court_cost=?,"+
	    "last_paid_date=?,"+
	    "closed_date=?,"+
	    "closed_comments=?,"+
						
	    "comments=?,"+
	    "pro_supp_time=?,"+
	    "per_day=?,"+
	    "mcc_flag=?,"+
	    "pro_supp=?,"+
						
	    "lawyerid=?,"+
	    "rule_date=?,"+
	    "rule_time=?,"+
	    "e41_date=?,"+
	    "citation_num=?,"+
						
	    "trans_collect_date=?,"+
	    "citation_date=? "+
	    " where id=? ";
	if(debug){
	    logger.debug(qq);
	}				
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    setParams(stmt);
	    stmt.setString(33, id);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not update data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	if(!basic){
	    findDefendants();
	}
	back += resetObjects();

	return back;
    }
    public String updateComments(){
	//
	String str = "", back = "";

	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "update legal_cases set comments=? where id=?";
	if(debug){
	    logger.debug(qq);
	}				
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    if(comments.isEmpty()){
		stmt.setNull(1, Types.VARCHAR);
	    }
	    else{
		stmt.setString(1, comments);
	    }
	    stmt.setString(2, id);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not update data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }    
    public String doDelete(){
	//
	String back = "";
	String qq = "delete from legal_payments where id=?";
	String qq2 = "delete from legal_def_case where id=?";
	String qq3 = "delete from legal_case_violations where id=?";
	String qq4 = "delete from legal_addresses where caseId=?";
	String qq5 = "delete from legal_cases where id=? ";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
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
	    stmt.setString(1, id);
	    stmt.executeUpdate();
	    qq = qq2;
	    if(debug)			
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);			
	    stmt.executeUpdate();
	    qq = qq3;
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);				
	    stmt.executeUpdate();
	    qq = qq4;
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);				
	    stmt.executeUpdate();
	    qq = qq5;
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);				
	    stmt.executeUpdate();
	    per_day="";
	    case_type="";
	    status="PD"; id="";
	    ini_hear_date="";contest_hear_date="";
	    misc_hear_date=""; received=""; filed="";
	    mcc_flag="";
	    ini_hear_time="08:30";contest_hear_time="";
	    misc_hear_time="";pro_supp_time="";
	    lawyerid="";
	    judgment_date="";pro_supp_date="";compliance_date="";
	    judgment_amount=""; sent_date="";
	    fine="";court_cost="";last_paid_date="";closed_date="";
	    closed_comments=""; comments=""; rule_date="";
	    rule_time=""; e41_date=""; citation_num="";
	    addresses = null;
	}
	catch(Exception ex){
	    back += " Error: "+ex+":"+qq;			
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}					
	return back;
    }
    //
    public String doSelect(){	
	//
	String back = "";
	String qq = "select "+
	    "c.case_type,c.status,"+
	    "c.ini_hear_time,c.contest_hear_time,c.misc_hear_time,"+
	    "c.judgment_amount,c.fine,c.court_cost,"+
	    "date_format(c.ini_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.contest_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.misc_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.pro_supp_date,'%m/%d/%Y'),"+
	    "date_format(c.received,'%m/%d/%Y'),"+
	    "date_format(c.filed,'%m/%d/%Y'),"+
	    "date_format(c.compliance_date,'%m/%d/%Y'),"+
	    "date_format(c.judgment_date,'%m/%d/%Y'),"+
	    "date_format(c.sent_date,'%m/%d/%Y'),"+
	    "date_format(c.last_paid_date,'%m/%d/%Y'),"+
	    "date_format(c.closed_date,'%m/%d/%Y'),"+
	    "c.closed_comments,c.comments,c.pro_supp_time,c.per_day, "+
	    "c.mcc_flag, "+
	    "c.pro_supp, "+
	    "c.lawyerid, "+
	    "date_format(c.rule_date,'%m/%d/%Y'),"+
	    "c.rule_time, "+
	    "date_format(c.e41_date,'%m/%d/%Y'), "+
	    "date_format(c.trans_collect_date,'%m/%d/%Y'), "+
	    "date_format(c.citation_date,'%m/%d/%Y'), "+			
	    "c.citation_num, "+
	    "t.typeDesc,s.statusDesc,"+
	    "l.id,l.empid,l.fname,l.lname,l.position,l.title,l.barNum,l.active "+	
	    " from legal_cases c join legal_case_types t on c.case_type=t.typeId"+
	    " join legal_case_status s on c.status=s.statusId "+
	    " left join attorneys l on c.lawyerid=l.empid "+
	    " where c.id=?";
	String str="";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		logger.error(back);
		return back;
	    }			
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) case_type = str;
		str = rs.getString(2);
		if(str != null) status = str;
		str = rs.getString(3);
		if(str != null) ini_hear_time = str;
		str = rs.getString(4);
		if(str != null) contest_hear_time = str;
		str = rs.getString(5);
		if(str != null) misc_hear_time = str;
		str = rs.getString(6);
		if(str != null && !str.equals("0")) judgment_amount = str;
		str = rs.getString(7);
		if(str != null && !str.equals("0")) fine = str;
		str = rs.getString(8);
		if(str != null && !str.equals("0")) court_cost = str;
		str = rs.getString(9);
		if(str != null) ini_hear_date = str;
		str = rs.getString(10);
		if(str != null) contest_hear_date = str;
		str = rs.getString(11);
		if(str != null) misc_hear_date = str;
		str = rs.getString(12);
		if(str != null) pro_supp_date = str;
		str = rs.getString(13);
		if(str != null) received = str;
		str = rs.getString(14);
		if(str != null) filed = str;
		str = rs.getString(15);
		if(str != null) compliance_date = str;
		str = rs.getString(16);
		if(str != null) judgment_date = str;
		str = rs.getString(17);
		if(str != null) sent_date = str;
		str = rs.getString(18);
		if(str != null) last_paid_date = str;
		str = rs.getString(19);
		if(str != null) closed_date = str;
		str = rs.getString(20);
		if(str != null) closed_comments = str;
		str = rs.getString(21);
		if(str != null) comments = str;
		str = rs.getString(22);
		if(str != null) pro_supp_time = str;
		str = rs.getString(23);
		if(str != null) per_day = str;
		str = rs.getString(24);
		if(str != null) mcc_flag = str;
		str = rs.getString(25);
		if(str != null) pro_supp = str;
		str = rs.getString(26);
		if(str != null) lawyerid = str;
		str = rs.getString(27);
		if(str != null) rule_date = str;
		str = rs.getString(28);
		if(str != null) rule_time = str;
		str = rs.getString(29);
		if(str != null) e41_date = str;
		str = rs.getString(30);
		if(str != null) trans_collect_date = str;
		str = rs.getString(31);
		if(str != null) citation_date = str;
		str = rs.getString(32);
		if(str != null) citation_num = str;
		String typeDesc = rs.getString(33);
		String statusDesc = rs.getString(34);
		String lid = rs.getString(35);
		String lempid = rs.getString(36);				
		String lfname = rs.getString(37);
		String llname = rs.getString(38);
		String lposition = rs.getString(39);
		String ltitle = rs.getString(40);				
		String lbar = rs.getString(41);
		String lactive = rs.getString(42);
		if(typeDesc != null && !case_type.equals("")){
		    caseType = new CaseType(case_type, typeDesc, debug);
		}
		if(statusDesc != null && !status.equals("")){
		    cStatus = new Status(status, statusDesc, debug);
		}
		if(lid != null && lfname != null){
		    lawyer = new Lawyer(lid,lempid,lfname,llname,lposition,ltitle,lbar,lactive,debug);
		}
	    }
	    else{
		back = id+" not found ";
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
	back += resetObjects();				
		
	return back;
		
    }

    public String linkDefendantToCase(String defId){
		
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	if(id.equals("") || defId.equals("")){
	    back = "Case ID or defendant ID not set ";
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	String qq = " insert into legal_def_case values(?,?,null)";
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.setString(2, defId);
	    stmt.executeUpdate();

	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	back += findDefendants();
	return back;
    }
    public String unlinkDefendant(String[] defsId){
		
	String back = "", qq = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	if(id.equals("")){
	    back = "Case ID is not set ";
	    return back;
	}
	if(con == null){
	    con = Helper.getConnection();
	}
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	qq = " delete from legal_def_case where id=? and did=? ";
	try{
	    stmt = con.prepareStatement(qq);
	    for(int i=0;i<defsId.length;i++){
		stmt.setString(1, id);
		stmt.setString(2, defsId[i]);
		stmt.executeUpdate();
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	back += findDefendants();
	return back;
    }
    //
    public String resetObjects(){
	//
	String back = "";
	if(!status.equals("") && cStatus == null){
	    cStatus = new Status(status, debug);
	    String str = cStatus.doSelect();
	    if(!str.equals(""))
		back += str;
	}
	if(!lawyerid.equals("") && lawyer == null){
	    lawyer = new Lawyer(lawyerid, debug);
	    String str = lawyer.doSelect();
	    if(!str.equals(""))
		back += str;
	}
	if(!case_type.equals("") && caseType == null){
	    caseType = new CaseType(case_type, debug);
	    String str = caseType.doSelect();
	    if(!str.equals(""))
		back += str;
	}
	if(pets == null){
	    AnimalList petsl = new AnimalList(debug, id);
	    String str = petsl.find();
	    if(!str.equals(""))
		back += str;
	    else
		pets = petsl.getAnimals();
	}	
	return back;
    }
    //
    // it is assumed that all parts of the address are set
    //
    public String saveAddress(Address addr){
	String back = "";
	if(addr != null){
	    back += addr.doSave();
	    if(!back.equals("")){
		logger.error(back);
	    }
	}
	return back;
    }
    //
    //
    public String findAddresses(){

	String back = "";
	if(id.equals("")){
	    return "";
	}
	AddressList al = new AddressList(debug, id);
	back = al.lookFor();
	if(back.equals("")){
	    addresses = al.getAddresses();
	}
	return back;
    }

    public String findDefendants(){

	String back = "";
	if(id.equals("")){
	    return back;
	}
	DefendantList dl = new DefendantList(id, debug);
	String str = dl.find();
	if(!str.equals(""))
	    back += str;
	else
	    defendants = dl.getDefendants();
	return back;
		
    }
    /**
     * check if this case has single defendant
     */
    public boolean hasSingleDef(){
	if(defendants == null){
	    findDefendants();
	}
	if(defendants != null && defendants.size() == 1){
	    return true;
	}
	return false;
    }
    /**
     * check if this case has multiple defendants
     */
    public boolean hasMultipleDef(){
	if(defendants == null){
	    findDefendants();
	}
	if(defendants != null && defendants.size() > 1){
	    return true;
	}
	return false;
    }
    /**
     * find the cause numbers
     */
    public String findCauseNums(){

	String back = "";
	if(id.equals("")){
	    return "";
	}
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "select "+
	    "d.cause_num from "+							
	    "legal_def_case d where d.id = ? ";
	if(debug){
	    System.err.println(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}				
	try{

	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    causeNums = new ArrayList<String>();
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) causeNums.add(str);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}		
	return back;
    }
    /**
     * make another case similar to this one with the same addresses, and
     * defendants to make it easy to add multiple cases against the same
     * group of defendants, where the user can do simple changes to dates
     * and types, save time on typing 
     */
    public String duplicate(){
	//
	// we need to make sure we have the list of addresses and defendants
	// already in the this object
	//
	String back = "";
	String oldId = id;
	if(addresses == null){
	    back = findAddresses();
	}
	if(defendants == null){
	    back += findDefendants();
	}
	String str = doSave();
	if(str.equals("")){
	    if(addresses != null){
		for(Address addr:addresses){
		    addr.setCase_id(id);
		    str = addr.doSave();
		}
	    }
	    if(defendants != null){
		for(Defendant deff:defendants){
		    String def_id = deff.getDid();
		    str = linkDefendantToCase(def_id);
		}
	    }
	}
	else{
	    back += str;
	}
	return back;
    }
    public String findCroosRefViolation(){
	String cid_t="", back="";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	if(case_type.equals("")) return cid_t;
	String qq = "select cid from legal_cross_ref "+
	    " where typeId = ? ";
	if(debug){
	    System.err.println(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return cid_t;
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, case_type);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) cid_t = str;
	    }
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}		
	return cid_t;	
    }

}






















































