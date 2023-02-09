package legals.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
/**
 *
 */

public class ViolGroupList {

    boolean debug = false;
    static final long serialVersionUID = 79L;
    static Logger logger = LogManager.getLogger(ViolGroupList.class);
    List<ViolGroup> violGroups = new ArrayList<>();
    public ViolGroupList(boolean val){
	debug = val;
    }
    public ViolGroupList(boolean val, List<CaseViolation> cvl){
	debug = val;
	if(cvl != null){
	    for(CaseViolation cv:cvl){
		if(violGroups.size() == 0){
		    ViolGroup vg = new ViolGroup(debug);
		    vg.add(cv);
		    violGroups.add(vg);
		}
		else{
		    boolean found = false;
		    for(int i=0;i<violGroups.size();i++){
			ViolGroup vg = violGroups.get(i);
			if(vg.isSameType(cv.getViolCat())){
			    vg.add(cv);
			    found = true;
			}
		    }
		    if(!found){
			ViolGroup vg = new ViolGroup(debug);
			vg.add(cv);
			if(violGroups == null) violGroups = new ArrayList<>();
			violGroups.add(vg);
		    }
		}
	    }
	}
    }
    public List<ViolGroup> getViolGroups(){
	return violGroups;
    }
}






















































