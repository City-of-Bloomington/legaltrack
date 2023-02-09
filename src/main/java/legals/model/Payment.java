package legals.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;
import legals.list.*;
/**
 *
 *
 */

public class Payment{

    boolean debug = false;
    SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");
    static final long serialVersionUID = 66L;
    static Logger logger = LogManager.getLogger(Payment.class);

    String id="", pid="";
    double amount = 0, balance=0, total_paid=0, total_due=0;
    String amountStr = "0", balanceStr="0", total_paidStr="0",
	total_dueStr="0", courtBalanceStr="0";
    String receipt_date="", paidFor=""; // Court or Fine
    String paid_date="",paid_method="",check_no="", clerk="";
    String paid_by="",
	mcc_flag="";	// from legal_def_case
    String cause_num = "53C0"; // multiple one per defendant
    double court_cost = 0, fine = 0;
    String totalFineStr = "0", courtCostStr="0"; // fine + court
    // String totalFineOnlyStr = "0";
    String totalBalanceStr = "0"; // for both fine and court
    String fineBalanceStr = "0";
    double totalBalance = 0, fineBalance=0, courtBalance=0;
    Case ccase = null;
    public Payment(boolean val){
	debug = val;
    }
	
    public Payment(boolean deb, String val){
	debug = deb;				
	setPid(val);
    }
    public Payment(boolean deb, String val, String val2){
	debug = deb;				
	setPid(val);
	setId(val2);
    }
    public Payment(boolean deb,
		   String val,  // pid
		   String val2, // id
		   String val3, // paid_date
		   String val4, // amount
		   String val5, // paid_by
		   String val6, // paid_method
		   String val7, // check_no
		   String val8,	// clerk
		   String val9, // receipt_date
		   String val10  // pidFor
		   ){

		
	setPid(val);
	setId(val2);
	setPaid_date(val3);
	setAmount(val4);
	setPaid_by(val5);
	setPaid_method(val6);
	setCheck_no(val7);
	setClerk(val8);
	setReceipt_date(val9);
	setPaidFor(val10);		
	debug = deb;
    }	
	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setPid(String val){
	if(val != null)
	    pid = val;
    }
    public void setPaid_date(String val){
	if(val != null){
	    paid_date = val;
	}
    }
    public void setPaid_method(String val){
	if(val != null)
	    paid_method = val;
    }
    public void setPaid_by(String val){
	if(val != null)
	    paid_by = val;
    }
    public void setClerk(String val){
	if(val != null)
	    clerk = val;
    }
    public void setPaidFor(String val){
	if(val != null)
	    paidFor = val;
    }
    public void setCase(Case val){
	if(val != null)
	    ccase = val;
    }
    public void setCheck_no(String val){
	if(val != null)
	    check_no = val;
    }
    public void setReceipt_date(String val){
	if(val != null)
	    receipt_date = val;
    }
    public void setMcc_flag(String val){
	if(val != null)
	    mcc_flag = val;
    }
    public void setAmount(String val){
	if(val != null){
	    try{
		amount = Double.valueOf(val).doubleValue();
		amountStr = Helper.formatNumber(val);
	    }catch(Exception ex){}
	}
    }
    //
    // getters
    //
    public String getId(){
	return id ;
    }
    public String getPid(){
	return pid ;
    }
    public String getPaid_date(){
	return paid_date;
    }
    public String getPaid_by(){
	return paid_by;
    }
    public String getPaid_method(){
	return paid_method;
    }
    public String getReceipt_date(){
	return receipt_date;
    }
    public String getClerk(){
	return clerk;
    }
    public String getPaidFor(){
	return paidFor;
    }	
    public String getCheck_no(){
	return check_no;
    }
    public String toString(){
	return amountStr;
    }
    public String getAmount(){
	return amountStr;
    }
    public String getMcc_flag(){ // from legal_def_case
	return mcc_flag;
    }
    public String getTotal_due(){
	return total_dueStr;
    }
    public String getTotalFine(){
	return totalFineStr;
    }
    public String getFine(){
	return ""+fine;
    }
    public String getCourtCost(){
	return ""+court_cost;
    }		
    public String getBalance(){
	return balanceStr;
    }
    public double getTotalBalance(){
	return totalBalance;
    }			
    public String getTotalBalanceStr(){
	return totalBalanceStr;
    }	
    public String getTotal_paid(){
	return total_paidStr;
    }
	
    public String getCause_num(){
	return cause_num;
    }
    public boolean hasFine(){
	return fine > 0;
    }
    public boolean hasCourtCost(){
	return court_cost > 0 && mcc_flag.equals("");
    }	
    public String getCourt_cost(){
	return courtCostStr;
    }
    public double getCourtBalance(){
	return courtBalance;
    }
    public double getFineBalance(){
	return fineBalance;
    }

    public String doSave(){
	//
	String back = "";
	if(amount <= 0){
	    back = "Payment amount not set ";
	    return back;
	}
	String qq = "";
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	int jj=1;
	qq = "insert into legal_payments values (0,?,?,?,?, ?,?,?,?)";
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(jj++, id);
	    if(paid_date.equals(""))
		paid_date = Helper.getToday();
	    stmt.setDate(jj++, new java.sql.Date(dtf.parse(paid_date).getTime()));						
	    stmt.setDouble(jj++, amount);
	    if(paid_by.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, paid_by);
	    if(paid_method.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, paid_method);
	    if(check_no.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, check_no);
	    if(clerk.equals(""))
		stmt.setNull(jj++, Types.CHAR);
	    else
		stmt.setString(jj++, "y");
	    if(paidFor.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, paidFor);
	    stmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt2 = con.prepareStatement(qq);
	    rs = stmt2.executeQuery();
	    if(rs.next()){
		pid = rs.getString(1);
	    }
	    back += compute();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save date ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}
	return back;
    }

    public String doUpdate(){
	//
	String back = "";
	if(pid.equals("")){
	    back = " Pid not set ";
	    return back;
	}
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}

	String str="";
	String qq = "update legal_payments set paid_date=?,amount=?,paid_by=?,paid_method=?, check_no=?,clerk=?, paidFor=? where pid=? ";
	int jj=1;
	if(debug){
	    logger.debug(qq);
	}

	try{
	    stmt = con.prepareStatement(qq);				
	    if(paid_date.equals(""))
		paid_date = Helper.getToday();
	    stmt.setDate(jj++, new java.sql.Date(dtf.parse(paid_date).getTime()));						
	    stmt.setDouble(jj++, amount);
	    if(paid_by.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, paid_by);
	    if(paid_method.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, paid_method);
	    if(check_no.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, check_no);
	    if(clerk.equals(""))
		stmt.setNull(jj++, Types.CHAR);
	    else
		stmt.setString(jj++, "y");
	    if(paidFor.equals(""))
		stmt.setNull(jj++, Types.VARCHAR);
	    else
		stmt.setString(jj++, paidFor);
	    stmt.setString(jj++, pid);
	    stmt.executeUpdate();

	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not update data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	back += compute();				
	return back;
    }

    public String doDelete(){
	//
	String back = "";
	String qq = "delete from legal_payments where pid=?";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, pid);
	    stmt.executeUpdate();
	    back += compute();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not delete data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
	
    public String doSelect(){	
	//
	String back = "";
	String str="";
	String qq = " select id,date_format(paid_date,'%m/%d/%Y'),"+
	    " amount,paid_by,paid_method,check_no,clerk,"+
	    " date_format(paid_date,'%M %d, %Y'),paidFor from legal_payments "+
	    " where pid=?";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}
				
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, pid);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) id = str;
		str = rs.getString(2);
		if(str != null) paid_date = str;
		amount = rs.getDouble(3);
		amountStr = Helper.formatNumber(""+amount);
		str = rs.getString(4);
		if(str != null) paid_by = str;
		str = rs.getString(5);
		if(str != null) paid_method = str;
		str = rs.getString(6);
		if(str != null) check_no = str;
		str = rs.getString(7);
		if(str != null) clerk = str;
		str = rs.getString(8);
		if(str != null) receipt_date = str;
		str = rs.getString(9);
		if(str != null) paidFor = str;
	    }
	    back += compute();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not retreive data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    // to find total payment
    //
    public String compute(){
	//
	// find the total payments
	//
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null, stmt3=null;
	ResultSet rs = null;
	String back = "", qq = "", qq2="", qq3="";
	double viol_fine = 0, totalFineOnly = 0;
	double total_paid_fine =0, total_paid_court = 0;
	if(ccase == null){
	    ccase = new Case(debug, id);
	    back = ccase.doSelect();
	    if(!back.equals("")){
		logger.error(back);
	    }
	    else{
		mcc_flag = ccase.getMcc_flag();
		List<String> causes = ccase.getCauseNums();
		if(causes != null && causes.size() > 0){
		    for(String str: causes){
			if(cause_num.indexOf(str) == -1){ // no duplicates
			    if(!cause_num.equals("")) cause_num += ", ";
			    cause_num += str;
			}						
		    }
		}
	    }
	}
	qq = "select sum(amount),'court' from "+
	    " legal_payments where id=? and paidFor='Court'"+
	    " union select sum(amount),'fine' from "+
	    " legal_payments where id=? and (paidFor is null or paidFor='Fine')";
	qq2 = " select fine,court_cost from "+
	    " legal_cases where id=? ";
	//
	// total paid for both court and fine
	qq3 = " select sum(amount) from "+
	    " legal_payments where id=? ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}				
	try{

	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.setString(2, id);						
	    rs = stmt.executeQuery();
	    while(rs.next()){
		String str = rs.getString(1);
		if(str != null){
		    total_paid = rs.getDouble(1);
		    str = rs.getString(2);
		    if(str.equals("court"))
			total_paid_court = total_paid;										
		    else{
			total_paid_fine = total_paid;
		    }
		}
	    }
	    total_paid = total_paid_court+total_paid_fine;
	    if(paidFor.equals("Court")){
		total_paidStr = Helper.formatNumber(""+total_paid_court);
	    }
	    else{
		total_paidStr = Helper.formatNumber(""+total_paid_fine);
	    }
	    qq = qq2;
	    if(debug){
		logger.debug(qq);
	    }
	    stmt2 = con.prepareStatement(qq);
	    stmt2.setString(1, id);
	    rs = stmt2.executeQuery();
	    if(rs.next()){
		fine = rs.getDouble(1);
		court_cost = rs.getDouble(2);
	    }
	    if(total_paid == 0){
		qq = qq3;
		if(debug){
		    logger.debug(qq);
		}
		stmt3=con.prepareStatement(qq);
		stmt3.setString(1, id);
		rs = stmt.executeQuery();
		if(rs.next()){
		    total_paid = rs.getDouble(1);
		    total_paid_fine = total_paid;
		}
	    }
	    courtBalance = court_cost - total_paid_court;						
	    fineBalance = fine - total_paid_fine;
	    if(fineBalance < 0) fineBalance = 0;
	    if(courtBalance < 0) courtBalance = 0;
	    totalBalance = fineBalance+courtBalance;
						
	    totalFineStr = Helper.formatNumber(fine);
	    if(totalBalance < 0) totalBalance = 0;
	    totalBalanceStr = Helper.formatNumber(totalBalance);
						
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	    back +=" Error getting data "+ex;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2, stmt3);
	}
	return back;
    }

}






















































