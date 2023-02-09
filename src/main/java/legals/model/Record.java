package legals.model;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class Record implements Comparable<Record>{


    boolean debug = false;
    static final long serialVersionUID = 66L;
    static Logger logger = LogManager.getLogger(Record.class);
    String[] item = null;
    String fullName = "";
    int size = 0;
    public Record(boolean val){
	debug = val;
    }
	
    public Record(int size, boolean deb){
	debug = deb;
	if(size > 0){
	    this.size = size;
	    item = new String[size];
	    for(int i=0;i<size;i++){
		item[i]="";
	    }
	}
    }
    public void copyFrom(Record rr){
	for(int i=0;i<rr.getSize();i++){
	    item[i] = rr.get(i);
	}
	fullName = rr.getFullName();
    }
    //
    //setters
    //
    public void set(int id, String val){
	if(val != null)
	    item[id] = val;
    }	
    public void setItem(int id, String val){
	if(val != null)
	    item[id] = val;
    }
    public void setFullName(String val){
	if(val != null)
	    fullName = val;
    }
    //
    // getters
    //	
    public String get(int id){
	return item[id];
    }
    public String getFullName(){
	return fullName;
    }
    public int getSize(){
	return size;
    }
    public String toString() {
	return fullName;
    }
    public int compareTo(Record rr){
	return fullName.compareTo(rr.getFullName());
    }
    public int hashCode() {
        return 31*fullName.hashCode() + 11;
    }
    public boolean equals(Object o) {
        if (!(o instanceof Record))
            return false;
        Record n = (Record) o;
        if(!n.getFullName().equals(fullName)) return false;
	for(int i=0;i< item.length;i++){
	    if(n.get(i) == null || !n.get(i).equals(item[i])) return false;
	}
	return true;
    }	

}






















































