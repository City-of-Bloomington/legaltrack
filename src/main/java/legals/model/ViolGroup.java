package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
import legals.list.*;
/**
 * 
 *
 */

public class ViolGroup {


    boolean debug = false;
    static Logger logger = LogManager.getLogger(ViolGroup.class);
    String citations ="",dates="", cid="";
    double amount = 0;
    ViolCat violCat = null;
    public ViolGroup(boolean deb){
	debug = deb;
    }
	
    public ViolGroup(boolean deb,
		     String val,
		     String val2,
		     String val3,
		     ViolCat val4){
	debug = deb;
	setCitations(val);
	setDates(val2);
	setAmount(val3);
	setViolCat(val4);
    }
    public boolean isEmpty(){
	return citations.equals("") && dates.equals("");
    }
    public boolean isSameType(ViolCat vc){
	if(vc != null){
	    return vc.getId().equals(cid);
	}
	return false;
    }
    public void add(CaseViolation cv){
	if(isEmpty() || isSameType(cv.getViolCat())){
	    setCitations(cv.getCitations());
	    setDates(cv.getDates());
	    setAmount(cv.getAmount());
	    setViolCat(cv.getViolCat());
	}
    }
    //
    //setters
    //
    public void setCitations(String val){
	if(val != null){
	    if(!citations.equals("")) citations += ", ";
	    citations += val;
	}
    }
    public void setDates(String val){
	if(val != null){
	    if(!dates.equals("")) dates += ", ";
	    dates += val;
	}
    }	
    public void setAmount(String val){
	if(val != null){
	    double dd = 0;
	    try{
		dd = Double.parseDouble(val);
		amount = amount + dd;
	    }catch(Exception ex){
		System.err.println(ex);
	    }
	}
    }
    public void setViolCat(ViolCat val){
	if(val != null && violCat == null){
	    violCat = val;
	    cid = violCat.getId();
	}
    }
    //
    // getters
    //
    public String getCitations(){
	return citations;
    }
    public String getDates(){
	return dates;
    }
    public String getAmount(){
	return ""+amount;
    }	
    public ViolCat getViolCat(){
	return violCat;
    }
	

}






















































