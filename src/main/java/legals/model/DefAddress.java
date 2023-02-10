package legals.model;
import java.sql.*;
import javax.sql.*;
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
	PreparedStatement stmt = null;
	ResultSet rs = null;
	if(defId.isEmpty()){
	    back = "Defendant id not set ";
	    return back;
	}
	if(street_num.equals("") && street_name.equals("")){
	    back = "Street number and name are required";
	    return back;
	}	
				
	String qq = " insert into legal_def_addresses value(0,?,?,?,?,"+
	    "?,?,?,?,?,"+
	    "?,?,?,?)";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    back = fillStatement(stmt);
	    if(back.isEmpty()){
		stmt = con.prepareStatement(qq);
		if(debug){
		    logger.debug(qq);
		}
		qq = "select LAST_INSERT_ID() ";
		if(debug){
		    logger.debug(qq);
		}
		stmt = con.prepareStatement(qq);
		rs = stmt.executeQuery();
		if(rs.next()){
		    id = rs.getString(1);
		}
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
    private String fillStatement(PreparedStatement stmt){
	String back  = "";
	try{
	    stmt.setString(1, defId);
	    if(street_num.equals("")){
		stmt.setNull(2, Types.VARCHAR);
	    }
	    else{
		stmt.setString(2, street_num);
	    }
	    if(street_dir.equals("")){
		stmt.setNull(3, Types.VARCHAR);
	    }
	    else{
		stmt.setString(3, street_dir);
	    }
	    if(street_name.equals("")){
		stmt.setNull(4, Types.VARCHAR);
	    }
	    else{
		stmt.setString(4, street_name);
	    }
	    if(street_type.equals("")){
		stmt.setNull(5, Types.VARCHAR);
	    }
	    else{
		stmt.setString(5, street_type);
	    }
	    if(post_dir.equals("")){
		stmt.setNull(6, Types.VARCHAR);
	    }
	    else{
		stmt.setString(6, post_dir);
	    }
	    if(sud_type.equals("")){
		stmt.setNull(7, Types.VARCHAR);
	    }
	    else{
		stmt.setString(7, sud_type);
	    }
	    if(sud_num.equals("")){
		stmt.setNull(8, Types.VARCHAR);
	    }
	    else{
		stmt.setString(8, sud_num);
	    }
	    if(invalid_addr.equals("")){
		stmt.setNull(9, Types.CHAR);
	    }
	    else{
		stmt.setString(9, "y");
	    }
	    if(city.equals("")){
		stmt.setNull(10, Types.VARCHAR);
	    }
	    else{
		stmt.setString(10, city);
	    }
	    if(state.equals("")){
		stmt.setNull(11, Types.VARCHAR);
	    }
	    else{
		stmt.setString(11, state);
	    }
	    if(zip.equals("")){
		stmt.setNull(12, Types.VARCHAR);
	    }
	    else{
		stmt.setString(12, zip);
	    }
	    if(addr_date.equals("")){
		stmt.setNull(13, Types.DATE);
	    }
	    else{
		stmt.setString(13,"str_to_date('"+addr_date+"','%m/%d/%Y')");
	    }

	}catch(Exception ex){
	    logger.error(ex);
	    back += ex;
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
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = " update legal_def_addresses set defId=?, "+
	    "street_num=?,street_dir = ?, street_name=?,street_type=?,"+
	    "post_dir=?,sud_type=?,sud_num=?,invalid_addr=?,city=?,state=?,"+
	    "zip=?,addr_date=? where id=? ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    back = fillStatement(stmt);
	    if(back.isEmpty()){
		stmt.setString(14, id);
		stmt.executeUpdate();
	    }
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
