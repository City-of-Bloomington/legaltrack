package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
import legals.list.*;
/**
 * 
 *
 */

public class Rental{

    boolean debug;
    String id="", pull_date="", pull_reason="", lastInspectionDate ="";
    String errors = "";
    static final long serialVersionUID = 67L;
    Logger logger = LogManager.getLogger(Rental.class);
    List<Owner> owners = new ArrayList<Owner>();
    Owner agent = null;
    List<String> addresses = new ArrayList<String>();
    //
    // basic constructor
    public Rental(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public Rental(boolean deb, String id){

	debug = deb;
	//
	// initialize
	//
	if(id != null)
	    this.id = id;

    }
    //
    // None for now is these info are retreived from rental system
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public String  getErrors(){
	return errors;
    }
    public List<String>  getAddresses(){
	return addresses;
    }
    public List<Owner> getOwners(){
	return owners;
    }
    public Owner getAgent(){
	return agent;
    }
    public String getPullDate(){
	return pull_date;
    }
    public String getPullReason(){
	return pull_reason;
    }
    public String getInspectionDate(){
	if(lastInspectionDate.equals("")){
	    findLastInspectionDate();
	}
	return lastInspectionDate;
    }	
    public String findAll(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    back += doSelect(stmt, rs);
	    back += findOwners(stmt, rs);
	    back += findAddresses(stmt, rs);
	    back += findPullDate(stmt, rs);
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;

    }
    //
    public String doSelect(Statement stmt, ResultSet rs){
	//
	String back = "";
	String qq = "select agent from registr where id="+id;
	String str="", agentId="";
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null && !str.equals("0")) agentId = str;
	    }
	    else{
		errors += " Not found ";
	    }
	}
	catch(Exception ex){
	    logger.debug(ex);
	    back += " "+ex.toString();
	}
	if(!agentId.equals("")){
	    agent = new Owner(debug, agentId);
	}
	rs = null;
	return back;
    }
    public String doSelect(){

	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    back += doSelect(stmt, rs);
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }	
    //
    public String findOwners(Statement stmt, ResultSet rs){

	String back = "";
	String qq = "select name_num from regid_name where id="+id;
	List<String> list = null;
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    list = new ArrayList<String>(2);
	    while(rs.next()){
		String str = rs.getString(1);
		list.add(str);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += ex;
	}
	if(list != null && list.size() > 0){
	    owners = new ArrayList<Owner>(list.size());
	    for(String name_num: list){
		if(name_num != null){
		    Owner owner = new Owner(debug, name_num);
		    if(owner != null)
			owners.add(owner);
		}
	    }
	}
	return back;
    }
    public String findOwners(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    back = findOwners(stmt, rs);
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String findAddresses(Statement stmt, ResultSet rs){
	//
	String back = "";
	String qq = "select street_num||' '||street_dir||' '||street_name||' '||street_type||' '||sud_type||' '||sud_num from address2 where registr_id="+id;
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    addresses = new ArrayList<String>(2);
	    while(rs.next()){
		String str = rs.getString(1);
		if(str != null)
		    addresses.add(str.trim());
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += " "+ ex.toString();
	}
	return back;
    }
    public String findAddresses(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    back = findAddresses(stmt, rs);
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;

    }
    //
    public String findPullDate(Statement stmt, ResultSet rs){
	//
	String back = "";
	String qq = "select to_char(r.pull_date,'mm/dd/yyyy'), p.pull_text "+
	    " from registr r, pull_reas p "+
	    " where r.pull_reason=p.p_reason and r.id="+id;
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null)
		    pull_date = str;
		str = rs.getString(2);
		if(str != null)
		    pull_reason = str;
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += " "+ ex.toString();
	}
	return back;
    }
    public String findPullDate(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    back = findPullDate(stmt, rs);
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;

    }
    public String findLastInspectionDate(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	con = Helper.getOraConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    String qq = "select to_char(inspection_date,'mm/dd/yyyy')"+
		" from inspections where id="+id; 
	    qq += " order by inspection_date DESC ";
			
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		String str = rs.getString(1);
		if(str != null){
		    lastInspectionDate = str;
		    break;
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;

    }	
			
}






















































