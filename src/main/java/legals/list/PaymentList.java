package legals.list;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
/**
 * 
 *
 */

public class PaymentList{

    String id="";
	
    boolean debug = false;
    String errors = "";
    String paidFor = "";
    static final long serialVersionUID = 67L;
    static Logger logger = LogManager.getLogger(PaymentList.class);	
    List<Payment> payments = null;
    public PaymentList(boolean val){
	debug = val;
    }
    public PaymentList(boolean deb, String val){
	if(val != null)
	    id = val;
	debug = deb;
    }
    public void setId(String val){
	if(val != null)
	    id= val; // used for search purpose
    }
    public void setPaidFor(String val){
	if(val != null)
	    paidFor = val; 
    }
    public List<Payment> getPayments(){
	return payments;
    }
    //
    public String find(){
		
	String back = "";
		
	if(id.equals("")){
	    back = " Need to set case ID ";
	    return back;
	}
	String qq = " select pid,id,"+
	    " date_format(paid_date,'%m/%d/%Y'),"+
	    " amount,paid_by,"+
	    " paid_method,check_no,"+
	    " clerk,date_format(paid_date,'%M %d, %Y'),"+
	    " paidFor from legal_payments "+
	    " where id=?";
	if(!paidFor.equals("")){
	    qq += " and paidFor = ? ";
	}
	else{
	    qq += " and (paidFor is null or paidFor = 'Fine') ";
	}
	String str="", str2="", str3="", str4="", str5="", str6="",
	    str7="", str8="", str9="", str10="";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}		
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    if(!paidFor.equals("")){
		stmt.setString(2, paidFor);
	    }
	    rs = stmt.executeQuery();
	    while(rs.next()){
		str = rs.getString(1);
		str2 = rs.getString(2);
		str3 = rs.getString(3);
		str4 = rs.getString(4);
		str5 = rs.getString(5);
		str6 = rs.getString(6);
		str7 = rs.getString(7);
		str8 = rs.getString(8);
		str9 = rs.getString(9);
		str10 = rs.getString(10);
		Payment pay = new Payment(debug, str, str2, str3, str4, str5, str6, str7, str8, str9, str10);
		if(payments == null) payments = new ArrayList<>();
		payments.add(pay);
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

}
