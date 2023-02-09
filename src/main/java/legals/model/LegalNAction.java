package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
import legals.list.*;
/**
 *
 */

public class LegalNAction{

    SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");
    boolean debug;
    String id="", rental_id="", status="", reason="", action_id="",
	date="", startBy="", fullName="", 
	attention="", case_id="",
	errors="";
    // action fields
    String notes="",action_by="",action_date="", actionByName="";
    static final long serialVersionUID = 45L;
    Logger logger = LogManager.getLogger(LegalNAction.class);
    //
    Action recentAction = null; // most recent action
    //
    // basic constructor
    public LegalNAction(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public LegalNAction(boolean deb, String id){

	debug = deb;
	this.id = id;
	//
	// initialize
	//
    }
    // 
    public LegalNAction(boolean deb,
			String val,
			String val2,
			String val3,
			String val4,
			String val5,
												
			String val6,
			String val7,
			//
			String val8,
			String val9,
			String val10
			){

	debug = deb;
	setId(val);
	setAction_id(val2);
	setReason(val3);
	setStartBy(val4);
	setFullName(val5);
	setRental_id(val6);
				
	setStatus(val7);
	setDate(val8);
	setAttention(val9);
	setCase_id(val10);
    }		
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setCase_id(String val){
	if(val != null)
	    case_id = val;
    }
    public void setAction_id(String val){
	if(val != null)
	    action_id = val;
    }		
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }
    public void setStatus(String val){
	if(val != null)
	    status = val;
    }
    public void setStartBy(String val){
	if(val != null)
	    startBy = val;
    }
    public void setFullName(String val){
	if(val != null)
	    fullName = val;
    }		
    public void setDate(String val){
	if(val != null)
	    date = val;
    }	
    public void setReason(String val){
	if(val != null)
	    reason = val;
    }
    public void setAttention(String val){
	if(val != null)
	    attention = val;
    }
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public String  getRental_id(){
	return rental_id;
    }
    public String  getCase_id(){
	return case_id;
    }
    public String  getReason(){
	return reason;
    }
    public String  getStatus(){
	return status;
    }
    public String  getStartBy(){
	return startBy;
    }
    public String  getFullName(){
	return fullName;
    }
    public String  getDate(){
	return date;
    }
    public String  getErrors(){
	return errors;
    }
    public String  getAttention(){
	return attention;
    }
    //
    /**
       select l.id,a.id,l.reason,l.startBy,u.fullName,l.rental_id,l.status,date_format(l.startDate,'%m/%d/%Y'), l.attention, l.case_id, a.notes,a.actionBy,a.actionDate from rental_legals l left join users u on u.userid=l.startBy join legal_actions a on a.legal_id=l.id where l.id>1880 and a.id=(select max(aa.id) from legal_actions aa where aa.legal_id=l.id) and l.status not in ('Closed')
       select l.id,0,l.reason,l.startBy,u.fullName,l.rental_id,l.status,date_format(l.startDate,'%m/%d/%Y'), l.attention, l.case_id from rental_legals l left join users u on u.userid=l.startBy where l.id>1850 and l.id not in (select aa.legal_id from legal_actions aa) 			 
			 

    */

}






















































