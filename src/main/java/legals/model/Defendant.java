package legals.model;
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
import legals.list.*;
/**
 *
 *
 */

public class Defendant implements java.io.Serializable{

    static final long serialVersionUID = 36L;
    static Logger logger = LogManager.getLogger(Defendant.class);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");		
    String did="", dob="", ssn="", dln="",
	f_name="",l_name="",
	addr_last_update="",
	addr_req_date="",
	notes="", phone="", phone_2="", email="";
    String care_of_id = "", care_of_name="";
    boolean debug = false;	
    List<DefAddress> addresses = null; // in case of multiple
    DefAddress address = null;
    List<Case> cases = null;
    List<CareOf> care_ofs = new ArrayList<>();
    String errors = "";
    public Defendant(boolean val){
	debug = val;
	address = new DefAddress(debug);
    }
	
    public Defendant(String val, boolean deb){
	did = val;
	debug = deb;
	address = new DefAddress(debug);
    }
    // coming from rental
    public Defendant(
		     boolean deb,
		     String val,
		     String val2,
		     String val3,
		     String val4){
	debug = deb;
	setFullName(val);
	setPhone(val2);
	setPhone_2(val3);
	setEmail(val4);
	address = new DefAddress(debug);
    }
    //
    // getters
    //
    public String getL_name(){
	return l_name;
    }
    public String getF_name(){
	return f_name;
    }
    public String getDid(){
	return did;
    }
    public String getSsn(){
	return ssn;
    }
    public String getDob(){
	return dob;
    }
    public String getDln(){
	return dln;
    }
	
    public String getPhone(){
	return phone;
    }
    public String getPhone_2(){
	return phone_2;
    }
    public String getPhones(){
	String ret = phone;
	if(!phone_2.equals("")){
	    if(!ret.equals("")) ret += ", ";
	    ret += phone_2;
	}
	return ret;
    }
    public String getEmail(){
	return email;
    }	
	
    public String getAddr_last_update(){
	return addr_last_update;
    }
    public String getAddr_req_date(){
	return addr_req_date;
    }
    public void setDid(String val){
	if(val != null)
	    did = val;
    }
    public void setDob(String val){
	if(val != null)
	    dob = val;
    }
    public void setSsn(String val){
	if(val != null)
	    ssn = val;
    }
    public void setCareOfName(String val){
	if(val != null)
	    care_of_name = val;
    }
    public void setCareOfId(String val){
	if(val != null)
	    care_of_id = val;
    }	
    public void setAddr_last_update(String val){
	if(val != null)
	    addr_last_update = val;
    }
    public void setAddr_req_date(String val){
	if(val != null)
	    addr_req_date = val;
    }
	
    public void setAddress (DefAddress val){
	if(val != null)
	    address = val;
    }
	

    public void setDln (String val){
	if(val != null)
	    dln = val;
    }
    public void setPhone (String val){
	if(val != null)
	    phone = val;
    }
    public void setPhone_2 (String val){
	if(val != null)
	    phone_2 = val;
    }
    public void setEmail (String val){
	if(val != null)
	    email = val;
    }
    public boolean hasCareOf(){
	getCareOfs();
	if(care_ofs != null && care_ofs.size() > 0) return true;
	return false;
    }
    public boolean hasValidAddress(){
	if(addresses == null){
	    getAddresses();
	}
	if(addresses != null){
	    for(DefAddress daddr:addresses){
		if(!daddr.isInvalid()) return true;
	    }
	}
	return false;
    }
    public boolean hasInvalidAddress(){
	if(addresses == null){
	    getAddresses();
	}
	if(addresses != null){
	    for(DefAddress daddr:addresses){
		if(daddr.isInvalid()) return true;
	    }
	}
	return false;
    }
    /*
     * get one valid (most recent) address
     */
    public DefAddress getValidAddress (){
	DefAddress vAddr = null;
	if(addresses == null){
	    getAddresses();
	}
	if(addresses != null){
	    for(DefAddress daddr:addresses){
		if(!daddr.isInvalid()){
		    vAddr = daddr;
		    break;
		}
	    }
	}
	return vAddr;
    }
    /**
     * get one invalid address
     */
    public DefAddress getInvalidAddress (){
	DefAddress invAddr = null;
	if(addresses == null){
	    getAddresses();
	}
	if(addresses != null){
	    for(DefAddress daddr:addresses){
		if(daddr.isInvalid()){
		    invAddr = daddr;
		    break;
		}
	    }
	}
	return invAddr;
    }	
    public DefAddress getAddress (){
	return address;
    }
	
    public List<DefAddress> getAddresses (){
	if(addresses == null){
	    DefAddressList dal = new DefAddressList(debug, did);
	    String str = dal.lookFor();
	    if(str.equals("")){
		addresses = dal.getAddresses();
	    }
	    else{
		logger.error(str);
	    }
	}
	return addresses;
    }
    public List<Case> getCases(){
	if(cases == null && !did.equals("")){
	    CaseList cl = new CaseList(debug, did);
	    String str = cl.lookFor();
	    if(str.equals("")){
		cases = cl.getCases();
	    }
	}
	return cases;
    }
    public String addAddress (DefAddress val){
	String back = "";
	if(val != null && val.isValid()){
	    DefAddress addr = val;
	    if(!did.equals("")){
		addr.setDefId(did);
		back = addr.doSave();
		if(back.equals(""))
		    address = addr;
	    }
	    else{
		back = "Defendant id not set ";
	    }
	}
	return back;
    }	
    public String getStreetAddress(){
	String ret = "";
	if(address != null){
	    ret =  address.getAddress();
	}
	return ret;
    }
    public String getCityStateZip(){
	String ret = "";
	if(address != null){
	    ret =  address.getCityStateZip();
	}
	return ret;
    }
    public void setFullName(String val){
	if(val != null){
	    if(val.indexOf(",") > 0){
		String[] arr = val.split(",");
		if(arr.length > 1){
		    l_name = arr[0];
		    f_name = arr[1];
		}
		if(arr.length > 2){
		    f_name += " "+arr[2];
		}
	    }
	    else{
		l_name = val;
	    }
	}
    }
    public String getFullName(){
	String str = l_name;
	if(!f_name.equals("")){
	    if(!str.equals(""))
		str += ", ";
	    str += f_name;
			
	}
	return str;
    }
    public String getFullName2(){
	String str = f_name;
	if(!l_name.equals("")){
	    if(!str.equals(""))
		str += " ";
	    str += l_name;
	}
	return str;
    }
    public String getCareOfName(){
	if(care_ofs == null){
	    getCareOfs();
	}
	if(care_ofs != null && care_ofs.size() > 0){
	    CareOf one = care_ofs.get(0);
	    return one.getCoName();
	}
	return "";
    }
    public String getCareOfId(){
	if(care_ofs == null){
	    getCareOfs();
	}
	if(care_ofs != null && care_ofs.size() > 0){
	    CareOf one = care_ofs.get(0);
	    return one.getId();
	}
	return "";
    }
    public boolean hasContactInfo(){
	return !(phone.equals("") && phone_2.equals("") && email.equals(""));
    }
    public String getContactInfo(){
	String ret = phone;
	if(!phone_2.equals("")){
	    if(ret.equals("")){
		ret += ", ";
	    }
	    ret += phone_2;						
	}
	if(!email.equals("")){
	    if(!ret.equals("")){
		ret += ", ";
	    }
	    ret += email;
	}				
	return ret;
    }
    //
    // setters
    //
    /*
      public void setFullName(String val){
      //
      // used to set the name from rental owners where we have the full name
      // the names have the composition of xxxx, yyyyy
      // if the name contains a comma we assume that the first part is last name
      // otherwise the whole name will be in l_name and we leave f_name name empty
      //
      int ind =-1;
      ind = val.indexOf(",");  
      if(ind > 0){
      l_name = val.substring(0,ind);
      if(ind+1 < val.length())
      f_name = val.substring(ind+1).trim();
			
      }
      else{
      l_name = val;
      }
      }
    */	
    public void setF_name (String val){
	f_name = val.toUpperCase();
    }
    public void setL_name (String val){
	l_name = val.toUpperCase();
    }
    //
    public String doSelect(){
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
				
	String back = "";
	if(did.equals("")){
	    back = " Defendant id not set yet ";
	    return back;
	}
	String qq = "select f_name,l_name, "+
	    "ssn,date_format(dob,'%m/%d/%Y'), "+
	    "date_format(addr_req_date,'%m/%d/%Y'),"+
	    "date_format(addr_last_update,'%m/%d/%Y'),"+
	    "dln,phone,phone_2,email "+
	    "from legal_defendents where did=?";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}				
	String str="";
	try{

	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, did);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) f_name = str;
		str = rs.getString(2);
		if(str != null) l_name = str;
		str = rs.getString(3);
		if(str != null)
		    ssn = str;
		str = rs.getString(4);
		if(str != null)
		    dob = str;
		str = rs.getString(5);
		if(str != null) addr_req_date = str;
		str = rs.getString(6);
		if(str != null) addr_last_update = str;
		str = rs.getString(7);
		if(str != null)
		    dln = str;
		str = rs.getString(8);
		if(str != null)
		    phone = str;
		str = rs.getString(9);
		if(str != null)
		    phone_2 = str;
		str = rs.getString(10);
		if(str != null)
		    email = str;
		DefAddressList dal = new DefAddressList(debug, did);
		str = dal.lookFor();
		if(str.equals("")){
		    addresses = dal.getAddresses();
		    if(addresses != null && addresses.size() > 0){
			address = addresses.get(0); //  first one
		    }
		}
		else{
		    back += str;
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
	
    public String doSave(){
	return doInsert();
    }
    public String doInsert(){
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
				
	String back = "";
	String qq = " insert into legal_defendents values(0,?,?,?,?, null,null,?,?,?, ?)";

	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();				
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}						
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, l_name);
	    stmt.setString(2, f_name);
	    if(dob.equals(""))
		stmt.setNull(3, Types.DATE);
	    else{
		java.util.Date date = sdf.parse(dob);
		String str = sdf2.format(date);
		stmt.setString(3, str);
	    }
	    if(ssn.equals(""))
		stmt.setNull(4, Types.VARCHAR);
	    else						
		stmt.setString(4, ssn);
	    if(dln.equals(""))
		stmt.setNull(5, Types.VARCHAR);
	    else						
		stmt.setString(5, dln);
	    if(phone.equals(""))
		stmt.setNull(6, Types.VARCHAR);
	    else						
		stmt.setString(6, phone);
	    if(phone_2.equals(""))
		stmt.setNull(7, Types.VARCHAR);
	    else						
		stmt.setString(7, phone_2);
	    if(email.equals(""))
		stmt.setNull(8, Types.VARCHAR);
	    else						
		stmt.setString(8, email);						
	    stmt.executeUpdate();
						
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt2 = con.prepareStatement(qq);						
	    rs = stmt2.executeQuery();
	    if(rs.next()){
		did = rs.getString(1);
	    }
	    address.setDefId(did);
	    if(address.isValid()){
		back += address.doSave();
		if(!back.equals("")){
		    logger.error(back);
		}
	    }
	    if(!care_of_name.equals("")){
		CareOf one = new CareOf(debug);
		one.setDefId(did);
		one.setCoName(care_of_name);
		back += one.doSave();
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}
	return back;
		
    }
    public String doUpdate(){

	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;				
	String back = "";
	String qq = "";
	if(did.equals("")){
	    back = " Defendant id not set yet ";
	    return back;
	}
	if(l_name.equals("")){
	    back = " Defendant last name is required ";
	    return back;
	}
	if(f_name.equals("")){
	    back = " Defendant first name is required ";
	    return back;
	}				
	qq = "update legal_defendents set l_name=?,f_name=?,dob=?,ssn=?,addr_req_date=?,addr_last_update=?,dln=?,phone=?,phone_2=?,email=? where did=?";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = " Could not connect to DB ";
	    logger.error(back);
	    return back;
	}							
	try{
	    int jj=1;
	    stmt = con.prepareStatement(qq);
	    stmt.setString(jj++, l_name);
	    stmt.setString(jj++, f_name);
	    if(dob.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else{
		java.util.Date date = sdf.parse(dob);
		String str = sdf2.format(date);
		stmt.setString(jj++, str);
	    }
	    if(ssn.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else						
		stmt.setString(jj++, ssn);
	    if(addr_req_date.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else{
		java.util.Date date = sdf.parse(addr_req_date);
		String str = sdf2.format(date);
		stmt.setString(jj++, str);
	    }
	    if(addr_last_update.equals(""))
		stmt.setNull(jj++, Types.DATE);
	    else{
		java.util.Date date = sdf.parse(addr_last_update);
		String str = sdf2.format(date);
		stmt.setString(jj++, str);
	    }						
	    if(dln.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else						
		stmt.setString(jj++, dln);
	    if(phone.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else						
		stmt.setString(jj++, phone);
	    if(phone_2.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else						
		stmt.setString(jj++, phone_2);
	    if(email.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else						
		stmt.setString(jj++, email);		
	    stmt.setString(jj++, did);
	    stmt.executeUpdate();
	    if(!care_of_name.equals("")){
		CareOf one = new CareOf(debug);
		one.setDefId(did);
		one.setCoName(care_of_name);				
		if(!care_of_id.equals("")){ // do update
		    one.setId(care_of_id);
		    back = one.doUpdate();
		}
		else{ // new
		    back = one.doSave();
		}
	    }
	    else if(!care_of_id.equals("")){ // means delete
		CareOf one = new CareOf(debug, care_of_id);
		back = one.doDelete();
	    }
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
	
    public String doDelete(){
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null, stmt3=null, stmt4=null,stmt5=null;
	ResultSet rs = null;
	String qq = "", qq2="", message="", id="";
	int cnt = 0;
	qq = "select count(*) from legal_def_case where did = ?";
	qq2 = " select id from legal_def_case where did=?";
	if(did.equals("")){
	    message = " Defendant id not set yet ";
	    return message;
	}
	con = Helper.getConnection();
	if(con == null){
	    message = " Could not connect to DB ";
	    logger.error(message);
	    return message;
	}
	if(debug){
	    System.err.println(qq);
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, did);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		cnt = rs.getInt(1);
	    }
	    if(cnt > 1){
		message += "This defendant has more than one case linked"+
		    " to her/him. You need to remove all the cases from this "+
		    " defendant first ";
	    }
	    else{
		if(debug){
		    System.err.println(qq2);
		}
		stmt2 = con.prepareStatement(qq2);
		stmt2.setString(1, did);
		rs = stmt2.executeQuery();
		if(rs.next()){
		    id = rs.getString(1);
		}
		qq = "delete from legal_def_case where did=? ";
		if(debug){
		    logger.debug(qq);
		}
		stmt3 = con.prepareStatement(qq);
		stmt3.setString(1, did);
		stmt3.executeUpdate();
		qq = "delete from legal_def_addresses where defId=? ";
		if(debug){
		    logger.debug(qq);
		}
		stmt4 = con.prepareStatement(qq);
		stmt4.setString(1, did);
		stmt4.executeUpdate();								

		qq = "delete from legal_defendents where did=? ";
		if(debug){
		    logger.debug(qq);
		}
		stmt5 = con.prepareStatement(qq);
		stmt5.setString(1, did);
		stmt5.executeUpdate();								
		if(!id.equals("")){
		    qq = "delete from legal_payments where id=?";
		    if(debug){
			logger.debug(qq);
		    }
		    stmt = con.prepareStatement(qq);
		    stmt.setString(1, id);
		    stmt.executeUpdate();											

		    qq = "delete from legal_def_case where id=?";
		    if(debug){
			logger.debug(qq);
		    }
		    stmt2 = con.prepareStatement(qq);
		    stmt2.setString(1, id);
		    stmt2.executeUpdate();		
		    qq = "delete from legal_case_violations where id=?";
		    if(debug){
			logger.debug(qq);
		    }
		    stmt3 = con.prepareStatement(qq);
		    stmt3.setString(1, id);
		    stmt3.executeUpdate();		
		    qq = "delete from legal_cases where id=? ";
		    if(debug){
			logger.debug(qq);
		    }
		    stmt4 = con.prepareStatement(qq);
		    stmt4.setString(1, id);
		    stmt4.executeUpdate();		
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	    message += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2, stmt3, stmt4, stmt5);
	}
	return message;
    }
    //
    public String getCauseNumForCase(String id){
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String cause_num = "", msg="";
	String qq = "select "+
	    "d.cause_num from "+							
	    "legal_def_case d where d.did =? and d.id = ?";
	if(debug){
	    System.err.println(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    msg = "No db connection ";
	    return msg;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, did);
	    stmt.setString(2, id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null && !str.equals("53C0")) cause_num = str;
	    }
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return cause_num;
		
    }
    //
    public String addCaseToDefendant(String id){
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	if(did.equals("")){
	    back = " Defendant id not set yet ";
	    return back;
	}
	String qq = " insert into legal_def_case values(?,?,null)";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "No db connection ";
	    return back;
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.setString(2, did);
	    stmt.executeUpdate();
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

    public String updateCauseNumber(String case_id, String cause){
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	if(did.equals("")){
	    back = " Defendant id not set yet ";
	    return back;
	}
	String qq = " update legal_def_case set cause_num=? "+
	    " where id=? and did= ?";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "No db connection ";
	    return back;
	}						
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, cause);
	    stmt.setString(2, case_id);
	    stmt.setString(3, did);
	    stmt.executeUpdate();
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
    /**
     * check if the defendant has parking ticket
     * no matter the status open or closed (modified 2/16/2012 req Heather)
     */
	
    public boolean defHasParkingCase()
		
	throws UndefinedIdException{
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	String qq = "select count(*) from legal_cases c,legal_def_case l "+
	    "where c.case_type='P' "+
	    " and l.id=c.id and l.did=?";
	boolean ret = false;
	if(did.equals("")){
	    back = "Defendant Id not set ";
	    return false;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "No db connection ";
	    return false;
	}
	if(debug)
	    logger.debug(qq);				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, did);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		int cnt = rs.getInt(1);
		if(cnt > 0){
		    ret = true;
		}
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
    //
    public String sendEmailToParking(User user,
			      String parkingUser,
			      String emailStr,
			      boolean newAddress){
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String id="", 
	    cause_num="", back="", str="";
	boolean found = false;
	if(did.equals("")){
	    back = " Defendant id not set yet ";
	    return back;
	}
	String qq = "select "+
	    "c.id,l.cause_num "+
	    " from legal_defendents d,legal_cases c,legal_def_case l "+
	    "where c.id=l.id and l.did = d.did "+
	    " and d.did="+did;
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		back = "Could not connect to DB ";
		logger.error(back);
		return back;
	    }			
			
	    if(debug){
		System.err.println(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) id = str;
		str = rs.getString(2);
		if(str != null) cause_num = str;
		found = true;
	    }
	}
	catch(Exception ex){
	    back += " Could not retreive data";
	    System.err.println(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	if(found){
	    String msg = "";
	    if(newAddress){
		msg = " Parking case has a modified new address. ";
	    }
	    else{
		msg = " Parking case has bad address. ";
		msg += "\n\r Email Notice that the Court shows the following "+
		    "\n\r Defendant address as bad";				
	    }
	    msg += "\n\r "+
		"\n\r Defendant Name: "+getFullName()+
		"\n\r "+
		"\n\r Defendant Address: "+address.getAddress()+
		"\n\r City: "+address.getCity()+
		"\n\r State: "+address.getState()+
		"\n\r Zip: "+address.getZip()+
		"\n\r Legal Track ID: "+id+
		"\n\r cause #: "+cause_num;
			
	    String email = parkingUser+emailStr;
	    String subject = "Court: Defendant Invalid Address ";
	    try{
		new MsgMail(email, // to
			    user.getUserid()+emailStr,//from
			    subject,
			    msg,
			    null, // CC
			    false);
	    }
	    catch(Exception ex){
		System.err.println(ex);
		back += " could not send "+ex;
	    }
	}
	return back;
    }
    //
    public String toString(){
	String ret = "";
	ret += " FullName: "+f_name+" "+l_name;
	ret += "\n Address: "+address;
	return ret;
    }
    //
    public String deleteAddresses(String[] list){
	String back = "";
	for(String str: list){
	    DefAddress addr = new DefAddress(debug, str);
	    back += addr.doDelete();
	}
	addresses = null; // for refresh purpose
	return back;
    }
    public List<CareOf> getCareOfs(){
	if(care_ofs == null && !did.equals("")){
	    CareOfList cares = new CareOfList(debug, did);
	    String back = cares.lookFor();
	    if(back.equals("")){
		care_ofs = cares.getCareOfs();
	    }
	}
	return care_ofs;
    }
    public String addCareOf(String  co_name){
	String back = "";
	if(co_name != null && !co_name.equals("")){
	    if(did.equals("")){
		back = "def id not set ";
	    }
	    else{
		CareOf one = new CareOf(debug);
		one.setDefId(did);
		one.setCoName(co_name);
		back = one.doSave();
		if(back.equals("") && care_ofs != null){
		    care_ofs.add(one);
		}
	    }
	}
	return back;
    }
    public String deleteCareOf(String[]  care_of_ids){
	String back = "";
	if(care_of_ids != null){
	    for(String str:care_of_ids){
		back += deleteCareOf(str);
	    }
	}
	return back;
    }
    public String deleteCareOf(String  care_of_id){
	String back = "";
	if(care_of_id != null && !care_of_id.equals("")){
	    CareOf one = new CareOf(debug, care_of_id);
	    back = one.doDelete();
	}
	return back;
    }	
}
