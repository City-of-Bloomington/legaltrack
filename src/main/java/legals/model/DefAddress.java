package legals.model;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
/**
 * 
 *
 *
 */

public class DefAddress extends Address{

    String defId="", addr_date="";
    static final long serialVersionUID = 32L;
    static Logger logger = LogManager.getLogger(DefAddress.class);
    public DefAddress(boolean deb){
	super(deb);
    }
    public DefAddress(boolean deb, String val){
	super(deb, val);
    }
    public DefAddress(boolean deb,
		      String val,
		      String val2,
		      String val3,
		      String val4,
		      String val5){
	super(deb);
	setDefId(val);
	setStreet_address(val2);
	setCity(val3);
	setState(val4);
	setZip(val5);
    }
    public String getDefId(){
	return defId;
    }
    public String getAddrDate(){
	return addr_date;
    }
    public void setDefId (String val){
	if(val != null)
	    defId = val;
    }
    public void setAddrDate (String val){
	if(val != null)
	    addr_date = val;
    }
    //
    public String toString(){
	return getAddress();
    }
    /** 
     * this is needed to avoid saving null addresses
     */
    public boolean isValid(){
	String str = getAddress();
	return !str.equals("");
    }
    public boolean isLocal(){
	if(city.equals("") && state.equals("")) return true;
	if(city.toUpperCase().equals("BLOOMINGTON") &&
	   state.toUpperCase().equals("IN")) return true;
	return false;
    }
    //
    public String doSave(){
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	/*
	  if(street_address.equals("")){
	  back = "Street address is required";
	  return back;
	  }
	*/
	if(street_num.equals("") && street_name.equals("")){
	    back = "Street number and name are required";
	    return back;
	}	
				
	String qq = " insert into legal_def_addresses value(0,"+defId+",";
	if(street_num.equals("")){
	    qq += "null,";
	}
	else {
	    qq += "'"+street_num+"',";
	}
	if(street_dir.equals("")){
	    qq += "null,";
	}
	else {
	    qq += "'"+street_dir+"',";
	}
	if(street_name.equals("")){
	    qq += "null,";
	}
	else {
	    qq += "'"+Helper.escapeIt(street_name)+"',";
	}
	if(street_type.equals("")){
	    qq += "null,";
	}
	else {
	    qq += "'"+street_type+"',";
	}
	if(post_dir.equals("")){
	    qq += "null,";
	}
	else {
	    qq += "'"+post_dir+"',";
	}
	if(sud_type.equals("")){
	    qq += "null,";
	}
	else {
	    qq += "'"+sud_type+"',";
	}
	if(sud_num.equals("")){
	    qq += "null,";
	}
	else {
	    qq += "'"+sud_num+"',";
	}
	if(invalid_addr.equals(""))
	    qq += "null,";
	else
	    qq += "'Y',";
	if(city.equals(""))
	    qq += "null,";
	else
	    qq += "'"+city+"',";
	if(state.equals(""))
	    qq += "null,";
	else
	    qq += "'"+state+"',";
	if(zip.equals(""))
	    qq += "null,";
	else
	    qq += "'"+zip+"',";
	if(addr_date.equals(""))
	    qq += "null";
	else
	    qq += "str_to_date('"+addr_date+"','%m/%d/%Y')";
	/*
	  if(street_address.equals("")){
	  street_address = getAddress();
	  }
	  qq += "'"+street_address+"'";
	*/
	qq += ")";
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		id = rs.getString(1);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not save address ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String doDelete(){
	String back = "";
	String qq = "delete from legal_def_addresses where id="+id;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not delete address "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String doSelect(){
	//
	String str="", back = "";
	String qq = "select defId,"+
	    "street_num,street_dir,street_name,street_type,"+
	    "sud_type,sud_num,post_dir, "+
	    "invalid_addr,city,state,zip,date_format(addr_date,'%m/%d/%Y') "+
	    // ",street_address "+
	    "from legal_def_addresses where id="+id;
	if(debug){
	    logger.debug(qq);
	}
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
		
	con = Helper.getConnection();
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
	    if(rs.next()){
		//
		str = rs.getString(1);
		defId = str;
		str = rs.getString(2);
		if(str != null) street_num = str;
		str = rs.getString(3);
		if(str != null) street_dir = str;
		str = rs.getString(4);
		if(str != null) street_name = str;
		str = rs.getString(5);  
		if(str != null) street_type = str; 
		str = rs.getString(6);
		if(str != null) sud_type = str;
		str = rs.getString(7);
		if(str != null) sud_num = str;
		str = rs.getString(8);
		if(str != null) post_dir = str;
		str = rs.getString(9);
		if(str != null && !str.toUpperCase().equals("N"))
		    invalid_addr = str.toUpperCase();
		str = rs.getString(10);
		if(str != null) city = str;
		str = rs.getString(11);
		if(str != null) state = str;
		str = rs.getString(12);
		if(str != null) zip = str;
		str = rs.getString(13);
		if(str != null) addr_date = str;
		/*
		  str = rs.getString(14);
		  if(str != null) street_address = str;
		*/
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not save address "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String doUpdate(){
	//		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String qq = " update legal_def_addresses set ";		
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(street_address.equals("")){
	    back="Street address is required";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    if(street_num.equals("")){
		qq += "street_num = null,";
	    }
	    else {
		qq += "street_num='"+street_num+"',";
	    }
	    if(street_dir.equals("")){
		qq += "street_dir = null,";
	    }
	    else {
		qq += "street_dir='"+street_dir+"',";
	    }
	    if(street_name.equals("")){
		qq += "street_name=null,";
	    }
	    else {
		qq += "street_name='"+Helper.escapeIt(street_name)+"',";
	    }
	    if(street_type.equals("")){
		qq += "street_type=null,";
	    }
	    else {
		qq += "street_type='"+street_type+"',";
	    }
	    if(post_dir.equals("")){
		qq += "post_dir=null,";
	    }
	    else {
		qq += "post_dir='"+post_dir+"',";
	    }
	    if(sud_type.equals("")){
		qq += "sud_type=null,";
	    }
	    else {
		qq += "sud_type='"+sud_type+"',";
	    }
	    if(sud_num.equals("")){
		qq += "sud_num=null,";
	    }
	    else {
		qq += "sud_num='"+sud_num+"',";
	    }
	    if(invalid_addr.equals(""))
		qq += "invalid_addr=null,";
	    else
		qq += "invalid_addr='Y',";
	    if(addr_date.equals(""))
		qq += "addr_date=null,";
	    else
		qq += "addr_date=str_to_date('"+addr_date+"','%m/%d/%Y'),";
	    if(street_address.equals("")){
		street_address = getAddress();
	    }
	    qq += "street_address='"+street_address+"',";						
	    if(city.equals(""))
		qq += "city=null,";
	    else
		qq += "city='"+city+"',";
	    if(zip.equals(""))
		qq += "zip=null,";
	    else
		qq += "zip='"+zip+"',";
	    if(state.equals(""))
		qq += "state=null";
	    else
		qq += "state='"+state+"'";				
	    qq += " where id="+id;
	    if(debug){
		logger.debug(qq);
	    }
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = " Could not update address "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    /**
     * this function is called before an address is updated, therefore we
     * campare to its previous values
     */
    public boolean isModified(){
	String back = "";
	DefAddress old = new DefAddress(debug, id);
	back = old.doSelect();
	return !equals(old);
    }
    /**
     * two addresses are equals if all components are equal
     */
    public boolean equals(DefAddress addr){
	if(street_address.equals("")){
	    street_address = getAddress();
	}
	return street_address.equals(addr.getAddress());
    }
}
