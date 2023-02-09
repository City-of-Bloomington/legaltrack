package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;


public class AnimalList{ 


    boolean debug = false;
    String cid = "";
    static final long serialVersionUID = 17L;
    static Logger logger = LogManager.getLogger(AnimalList.class);
    List<Animal> animals = null;
    public AnimalList(boolean val){
	debug = val;
    }
    public AnimalList(boolean val, String val2){
	debug = val;
	if(val2 != null)
	    cid = val2;
    }	
    //
    //setters
    //
    public void setCid(String val){
	if(val != null)
	    cid = val;
    }
    public List<Animal> getAnimals(){
	return animals;
    }
    //
    // getters
    //
    public String find(){
		
	String back = "", str="", str2="", str3="";
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "select id, name,pet_type "+
	    " from legal_animals where cid=?";// +cid;		
	Connection con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, cid);
	    rs = stmt.executeQuery();
	    while(rs.next()){
		str = rs.getString(1);
		str2 = rs.getString(2);
		str3 = rs.getString(3);	
		if(str != null && str2 != null){
		    Animal pet = new Animal(str, cid, str2, str3, debug);
		    if(animals == null)
			animals = new ArrayList<>();
		    if(!animals.contains(pet))
			animals.add(pet);
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






















































