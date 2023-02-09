package legals.utils;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.list.*;


public class Helper{

    static int c_con = 0;
    public final static String bgcolor = "silver";// #bfbfbf gray
    public final static String fgcolor = "navy";// for titles

    public final static String dirArr[] = {"","E","N","S","W"};
    public final static String positions[] = {"Assistant","Attorney","Counsel","Paralegal"};
    public final static Map<String, String>       mimeTypes = new HashMap<>();
    static {
        mimeTypes.put("image/gif",       "gif");
        mimeTypes.put("image/jpeg",      "jpg");
        mimeTypes.put("image/png",       "png");
        mimeTypes.put("image/tiff",      "tiff");
        mimeTypes.put("image/bmp",       "bmp");
        mimeTypes.put("text/plain",      "txt");
        mimeTypes.put("audio/x-wav",     "wav");
        mimeTypes.put("application/pdf", "pdf");
        mimeTypes.put("audio/midi",      "mid");
        mimeTypes.put("video/mpeg",      "mpeg");
        mimeTypes.put("video/mp4",       "mp4");
        mimeTypes.put("video/x-ms-asf",  "asf");
        mimeTypes.put("video/x-ms-wmv",  "wmv");
        mimeTypes.put("video/x-msvideo", "avi");
        mimeTypes.put("text/html",       "html");

        mimeTypes.put("application/mp4",               "mp4");
        mimeTypes.put("application/x-shockwave-flash", "swf");
        mimeTypes.put("application/msword",            "doc");
        mimeTypes.put("application/xml",               "xml");
        mimeTypes.put("application/vnd.ms-excel",      "xls");
        mimeTypes.put("application/vnd.ms-powerpoint", "ppt");
        mimeTypes.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
    }		
		
    public final static String strIdArr[] = {"",
	"AVE", "BND", "BLVD", 
	"CTR","CIR", "CT", "CRST",
	"DR",  "EXPY", "LN", "PIKE",
	"PKY", "PL",  "RD", "RDG","RUN", "ST", "TER", 
	"TPKE", "TURN","VLY","WAY"};
    //
    public final static String strArr[] = {"",
	"Avenue","Bend", "Boulevard", 
	"Center","Circle", "Court","Crest",
	"Drive", "Expressway", "Lane", "Pike" ,
	"Parkway" ,"Place" ,"Road" ,"Ridge","Run","Street", "Terrace",
	"Turnpike","Turn","Valley","Way"};
    public final static String petTypes[] = {"","Dog","Cat","Other"};
    public final static String allPetTypes = "<option value=\"\"></option>"+
	"<option value=\"Dog\">Dog</option>"+
	"<option value=\"Cat\">Cat</option>"+
	"<option value=\"Other\">Other</option>";
    //
    // xhtmlHeader.inc
    public final static String xhtmlHeaderInc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"+
	"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
    //
    // Non static variables
    //
    boolean debug = false;
    static Logger logger = LogManager.getLogger(Helper.class);
    String [] deptIdArr = null;
    String [] deptArr = null;

    //
    // basic constructor
    public Helper(boolean deb){
	//
	// initialize
	//
	debug = deb;
    }
    public final static String getFileExtensionFromName(String name)
    {
        String ext = "";
        try {
            if (name.indexOf(".") > -1) {
                ext = name.substring(name.lastIndexOf(".") + 1);
            }
        }
        catch (Exception ex) {

        }
        return ext;
    }
    public final static void printFiles(PrintWriter out,
				 String url,
				 List<LegalFile> files){
	if(files == null || files.size() == 0) return;
	out.println("<table><caption>Attachments</caption>");
	out.println("<tr><th>Date</th><th>File Name</th><th>Notes<th></tr>");
	for(LegalFile one:files){
	    out.println("<tr><td>"+one.getDate()+"</td>"+
			"<td><a href=\""+url+"LegalFileServ?action=download&id="+one.getId()+"\">"+one.getOldName()+"</a></td>"+
			"<td>"+one.getNotes()+"</td></tr>");
	}
	out.println("</table>");
    }						
    public final static String findFileType(File file)
    {
        String fileType = "";
        try {
            String pp   = file.getAbsolutePath();
            Path   path = Paths.get(pp);
            fileType = Files.probeContentType(path);
            System.err.println(fileType);
        }
        catch (Exception ex) {
            System.err.println(" fle type excep " + ex);
        }
        return fileType;
    }
    public final static String getFileExtension(File file)
    {
        String ext = "";
        try {
            // name does not include path
            String name     = file.getName();
            String pp       = file.getAbsolutePath();
            Path   path     = Paths.get(pp);
            String fileType = Files.probeContentType(path);
            if (fileType != null) {
                // application/pdf
                if (fileType.endsWith("pdf")) {
                    ext = "pdf";
                }
                // image/jpeg
                else if (fileType.endsWith("jpeg")) {
                    ext = "jpg";
                }
                // image/gif
                else if (fileType.endsWith("gif")) {
                    ext = "gif";
                }
                // image/bmp
                else if (fileType.endsWith("bmp")) {
                    ext = "bmp";
                }
                // application/msword
                else if (fileType.endsWith("msword")) {
                    ext = "doc";
                }
                // application/vnd.ms-excel
                else if (fileType.endsWith("excel")) {
                    ext = "csv";
                }
                // application/vnd.openxmlformats-officedocument.wordprocessingml.document
                else if (fileType.endsWith(".document")) {
                    ext = "docx";
                }
                // text/plain
                else if (fileType.endsWith("plain")) {
                    ext = "txt";
                }
                // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
                else if (fileType.endsWith(".sheet")) {
                    ext = "xlsx";
                }
                // audio/wav
                else if (fileType.endsWith("wav")) {
                    ext = "wav";
                }
                // text/xml
                else if (fileType.endsWith("xml")) {
                    ext = "xml";
                }
                else if (fileType.endsWith("html")) {
                    ext = "html";
                }
                // video/mng
                else if (fileType.endsWith("mng")) {
                    ext = "mng";
                }
                else if (fileType.endsWith("mpeg")) {
                    ext = "mpg";
                }
                // video/mp4
                else if (fileType.endsWith("mp4")) {
                    ext = "mp4";
                }
                else if (fileType.endsWith("avi")) {
                    ext = "avi";
                }
                else if (fileType.endsWith("mov")) {
                    ext = "mov";
                }
                // quick time video
                else if (fileType.endsWith("quicktime")) {
                    ext = "qt";
                }
                else if (fileType.endsWith("wmv")) {
                    ext = "wmv";
                }
                else if (fileType.endsWith("asf")) {
                    ext = "asf";
                }
                // flash video
                else if (fileType.endsWith("flash")) {
                    ext = "swf";
                }
                else if (fileType.startsWith("image")) {
                    ext = "jpg";
                } // if non of the above we check the file name
                else if (name.indexOf(".") > -1) {
                    ext = name.substring(name.lastIndexOf(".") + 1);
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println(" fle ext excep " + e);
        }
        return ext;
    }				
    public final static String getHashCodeOf(String buffer){

	String key = "Apps Secret Key "+getToday();
	byte[] out = performDigest(buffer.getBytes(),buffer.getBytes());
	String ret = bytesToHex(out);
	return ret;
	// System.err.println(ret);

    }
    public final static byte[] performDigest(byte[] buffer, byte[] key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer);
            return md5.digest(key);
        } catch (Exception e) {
	    System.err.println(e);
        }
        return null;
    }
    /**
     * repaces the & symbol with the string 'and'.
     * 
     * @param str the input string
     * @returns the modified string
     */
    public final static String changeAmpToAnd(final String str){
	String str2 = "";
	for(int i=0;i<str.length();i++){
	    if(str.charAt(i) == '&') 
		str2 += "and";
	    else
		str2 += str.substring(i,i+1);
	}
	return str2;
    }
    public final static void writeHeader(PrintWriter out, String url, User user){
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
    }
    public final static void writeFooter(PrintWriter out){
	out.println("</div>");
	out.println("</body>");
	out.println("</html>");
	out.close();
    }		

    public final static String bytesToHex(byte in[]) {
	byte ch = 0x00;
	int i = 0; 
	if (in == null || in.length <= 0)
	    return null;
	String pseudo[] = {"0", "1", "2",
	    "3", "4", "5", "6", "7", "8",
	    "9", "A", "B", "C", "D", "E",
	    "F"};
	StringBuffer out = new StringBuffer(in.length * 2);
	while (i < in.length) {
	    ch = (byte) (in[i] & 0xF0); // Strip off high nibble
		
	    ch = (byte) (ch >>> 4);
	    // shift the bits down
	    
	    ch = (byte) (ch & 0x0F);    
	    // must do this is high order bit is on!

	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    i++;
	}
	String rslt = new String(out);
	return rslt;
    }    
    //
    /**
     * Adds escape character before certain characters
     *
     */
    public final static String escapeIt(String s) {
		
	StringBuffer safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	boolean noSlashBefore = true;
	while (c < len) {                           
	    if ((safe.charAt(c) == '\'' ||
		 safe.charAt(c) == '"') && noSlashBefore){
		safe.insert(c, '\\');
		c += 2;
		len = safe.length();
		noSlashBefore = true;
	    }
	    else if(safe.charAt(c) == '\\'){
		c++;
		noSlashBefore = false;
	    }
	    else {
		c++;
		noSlashBefore = true;
	    }
	}
	return safe.toString();
    }
    //
    // users are used to enter comma in numbers such as xx,xxx.xx
    // as we can not save this in the DB as a valid number
    // so we remove it 
    public final static String cleanNumber(String s) {

	if(s == null) return null;
	String ret = "";
	int len = s.length();
	int c = 0;
	int ind = s.indexOf(",");
	if(ind > -1){
	    ret = s.substring(0,ind);
	    if(ind < len)
		ret += s.substring(ind+1);
	}
	else
	    ret = s;
	return ret;
    }
    /**
     * replaces the special chars that has certain meaning in html
     *
     * @param s the passing string
     * @returns string the modified string
     */
    public final static String replaceSpecialChars(String s) {
	char ch[] ={'\'','\"','>','<'};
	String entity[] = {"&#39;","&#34;","&gt;","&lt;"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    public final static String replaceAmp(String s) {
	char ch[] ={'&'};
	String entity[] = {"&amp;"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    public final static String replaceQuote(String s) {
	char ch[] ={'\''};
	String entity[] = {"_"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    /**
     * adds another apostrify to the string if there is any next to it
     *
     * @param s the passing string
     * @returns string the modified string
     */
    public final String doubleApostrify(String s) {
	StringBuffer apostrophe_safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	while (c < len) {                           
	    if (apostrophe_safe.charAt(c) == '\'') {
		apostrophe_safe.insert(c, '\'');
		c += 2;
		len = apostrophe_safe.length();
	    }
	    else {
		c++;
	    }
	}
	return apostrophe_safe.toString();
    }
    //
    public final static Connection getConnection(){
	boolean production = true;
	return getConnection(production);
    }
    //
    public final static Connection getConnection(boolean production){
	return getConnectionProd();
    }	
    //	
	
    public final static Connection getConnectionProd(){ // pooling

	Connection con = null;
	int trials = 0;
	boolean pass = false;
	while(trials < 3 && !pass){
	    try{
		trials++;
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/MySQL_legals");
		con = ds.getConnection();
		if(con == null){
		    String str = " Could not connect to DB ";
		    logger.error(str);
		}
		else{
		    pass = testCon(con);
		    if(pass){
			c_con++;
			logger.debug("Got connection: "+c_con);
			logger.debug("Got connection at try "+trials);
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}
	return con;
    }
	
    final static boolean testCon(Connection con){
		
	boolean pass = false;
	Statement stmt  = null;
	ResultSet rs = null;
	String qq = "select 1+1";		
	try{
	    if(con != null){
		stmt = con.createStatement();
		logger.debug(qq);
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    pass = true;
		}
	    }
	    rs.close();
	    stmt.close();
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	return pass;
    }	
    public final static Connection getOraConnection(){

	Connection con = null;
	try{
	    Context initCtx = new InitialContext();
	    Context envCtx = (Context) initCtx.lookup("java:comp/env");
	    DataSource ds = (DataSource)envCtx.lookup("jdbc/Ora_rent");
	    con = ds.getConnection();
	    if(con == null){
		String str = " Could not connect to DB ";
		logger.error(str);
	    }
	    else{
		c_con++;
		logger.debug("Got connection Ora: "+c_con);
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	}
	return con;
		
    }

    /**
     * Connect to Oracle database
     *
     * @param dbStr database connect string
     * @param dbUser database user string
     * @param dbPass database password string
     */
    public final static Connection databaseConnect(String dbStr, 
						   String dbUser, 
						   String dbPass) {
	Connection con=null;
	try {
	    Class.forName("oracle.jdbc.driver.OracleDriver");
	    con = DriverManager.getConnection(dbStr,
					      dbUser,dbPass);

	}
	catch (Exception sqle) {
	    System.err.println(sqle);
	}
	return con;
    }
    /**
     * Disconnect the database and related statements and result sets
     * 
     * @param con
     * @param stmt
     * @param rs
     */
    public final static void databaseDisconnect(Connection con,Statement stmt,
						ResultSet rs) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null) stmt.close();
	    stmt = null;
	    if(con != null) con.close();
	    con = null;
			
	    logger.debug("Closed Connection "+c_con);
	    c_con--;
	    if(c_con < 0) c_con = 0;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { ; }
		con = null;
	    }
	}
    }
    public final static void databaseDisconnect(Connection con,
						ResultSet rs,
						Statement... stmt) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null){
		for(Statement one:stmt){
		    if(one != null)
			one.close();
		    one = null;
		}
	    }
	    if(con != null) con.close();
	    con = null;
	    logger.debug("Closed Connection "+c_con);
	    c_con--;
	    if(c_con < 0) c_con = 0;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { }
		rs = null;
	    }
	    if (stmt != null) {
		try {
		    for(Statement one:stmt){										
			if(one != null)
			    one.close(); 
			one = null;
		    }
		} catch (SQLException e) { }
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { }
		con = null;
	    }
	}
    }				
    /**
     * Write the number in bbbb.bb format needed for currency.
     * = toFixed(2)
     * @param dd the input double number
     * @returns the formated number as string
     */
    public final static String formatNumber(double dd){
	//
	String str = ""+dd;
	String ret="";
	int l = str.length();
	int i = str.indexOf('.');
	int r = i+3;  // required length to keep only two decimal
	// System.err.println(str+" "+l+" "+r);
	if(i > -1 && r<l){
	    ret = str.substring(0,r);
	}
	else{
	    ret = str;
	}
	return ret;
    }

    //
    // format a number with only 2 decimal
    // usefull for currency numbers
    //
    public final static String formatNumber(String that){

	int ind = that.indexOf(".");
	int len = that.length();
	String str = "";
	if(ind == -1){  // whole integer
	    str = that + ".00";
	}
	else if(len-ind == 2){  // one decimal
	    str = that + "0";
	}
	else if(len - ind > 3){ // more than two
	    str = that.substring(0,ind+3);
	}
	else str = that;

	return str;
    }

    //
     // Non static methods and variables
    //
    public String[] getDeptIdArr(){
	return deptIdArr;
    }
    public String[] getDeptArr(){
	return deptArr;
    }
    /**
     * given dept id, returns the dept name.
     *
     * @param array of departments ids
     * @param array of departments names
     * @param specific dept id
     * @returns dept name
     */
    public final static String getDeptName(String[] deptIdArr, String[] deptArr, String id){
	String ret = "";
	if(deptArr != null){
	    for(int i=0;i<deptArr.length;i++){
		if(deptIdArr[i].equals(id)){
		    ret = deptArr[i];
		    break;
		}
	    }
	}
	return ret;
    }
    //
    public final static String getToday(){

	String day="",month="",year="";
	Calendar current_cal = Calendar.getInstance();
	int mm =  (current_cal.get(Calendar.MONTH)+1);
	int dd =   current_cal.get(Calendar.DATE);
	year = ""+ current_cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }
    public final static int getCurrentYear(){

	Calendar current_cal = Calendar.getInstance();
	int yy = current_cal.get(Calendar.YEAR);
	return yy;
    }		
    //
    public final static String getToday2(){

	String months[] = {"","Jan","Feb","March","April","May","June","July",
	    "Aug","Sept","Oct","Nov","Dec"};
	String day="",month="",year="";
	Calendar current_cal = Calendar.getInstance();
	int mm =  (current_cal.get(Calendar.MONTH)+1);
	int dd =   current_cal.get(Calendar.DATE);
	year = ""+ current_cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return months[mm]+" "+day+", "+year;
    }	

    public final static String initCapWord(String str_in){
	String ret = "";
	if(str_in !=  null){
	    if(str_in.length() == 0) return ret;
	    else if(str_in.length() > 1){  
		if(str_in.charAt(1) == '\'' && str_in.length() > 2){ // O'Donell
		    ret = str_in.substring(0,3).toUpperCase()+
			str_in.substring(3).toLowerCase();
		}
		else{
		    ret = str_in.substring(0,1).toUpperCase()+
			str_in.substring(1).toLowerCase();
		}
	    }
	    else{
		ret = str_in.toUpperCase();   
	    }
	}
	// System.err.println("initcap "+str_in+" "+ret);
	return ret;
    }	
    //
    // initial cap a word
    //
    public final static String initCapWord2(String str_in){
	String ret = "";
	if(str_in !=  null){
	    if(str_in.length() == 0) return ret;
	    else if(str_in.length() > 1){
		ret = str_in.substring(0,1).toUpperCase()+
		    str_in.substring(1).toLowerCase();
	    }
	    else{
		ret = str_in.toUpperCase();   
	    }
	}
	// System.err.println("initcap "+str_in+" "+ret);
	return ret;
    }
    //
    // init cap a phrase
    //
    public final static String initCap(String str_in){
	String ret = "";
	if(str_in != null){
	    if(str_in.indexOf(" ") > -1){
		String[] str = str_in.split("\\s"); // any space character
		for(int i=0;i<str.length;i++){
		    if(i > 0) ret += " ";
		    ret += initCapWord(str[i]);
		}
	    }
	    else
		ret = initCapWord(str_in);// it is only one word
	}
	return ret;
    }
    public final static User findUserFromList(List<User> users, String empid){
	User foundUser = null;
	if(users != null && users.size() > 0){ 
	    for (int i=0;i<users.size();i++){
		User user = users.get(i);
		if(user != null && user.getUserid().equals(empid)){
		    foundUser = user;
		    break;
		}
	    }
	}
	return foundUser;
    }
    /**
     * check if certain date is in mm/dd/yyyy format and valid
     */
    public final static boolean isValidDate(String date){
	String pattern = "(0?[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/(\\d{4})";
		
	// Create a Pattern object
	Pattern r = Pattern.compile(pattern);

	// Now create matcher object.
	Matcher m = r.matcher(date);
	if (m.find( )) {
	    // if we get the exact match then it is OK
	    //
	    if(date.equals(m.group(0))) return true;
	}
	return false;
    }
    public final static void writeDefs(PrintWriter out,
				String url,
				String id,
				List<Defendant> defs){	

	out.println("<table border width=100%>"+
		    "<caption>Defendant(s)</caption>"+
		    "<tr>"+
		    "<td>Name</td>"+
		    "<td>Cause #</td>"+
		    "<td>D.O.B</td>"+
		    "<td>Address</td></tr>");
	for(Defendant def:defs){
	    out.println("<tr>");
	    out.println("<td><a href=\""+url+"Defendent?action=zoom&did="+id+"&did="+def.getDid()+"\">"+def.getFullName()+"</a></td>");
	    String str = def.getCauseNumForCase(id);				
	    if(str.equals("")) str = "&nbsp;";
	    out.println("<td>"+str+"</td>");
	    str = def.getDob();				
	    if(str.equals("")) str = "&nbsp;";
	    out.println("<td>"+str+"</td>");
	    str = def.getStreetAddress();
	    if(str.equals("")) str = "&nbsp;";
	    out.println("<td>"+str+"</td>");
	    out.println("</tr>");
	}
	out.println("</table>");
    }
    public final static void writeViolations(PrintWriter out,
				      String url,
				      String id,
				      List<CaseViolation> cvl){
	String str="", str2="", sid="";
	float total = 0f, ff = 0f;
	out.println("<table border><tr>"+
		    "<th>Violation</th><th>Fines</th>"+
		    "<th>Citation #</th>"+
		    "<th>Dates</th>"+
		    "</tr>");
	out.println("<caption>Violations </caption>");
	for(CaseViolation ccv:cvl){
	    out.println("<tr><td>");
	    String vid = ccv.getVid();
	    sid = ccv.getSid();
	    ViolSubcat vsc = ccv.getViolSubcat();
	    String str3 = ccv.getViolCat().getCategory();
	    if(str3.equals("")){
		str3 = str;
	    }
	    if(vsc != null){
		str = vsc.getSubcat();
		if(str3.indexOf(str) == -1){
		    str3 += " ("+str+")";
		}
	    }
	    out.println("<a href="+url+"Violation?vid="+vid+
			"&amp;action=zoom"+
			"&amp;id="+id+">"+str3+"</a></td>");
			
	    str2 = ccv.getAmount();
	    str = str2;
	    if(str2.equals("")) str2 = "&nbsp;";
	    out.println("<td align=\"right\">$"+str2+"</td>");					
	    if(!str.equals("")){
		try{
		    ff = Float.parseFloat(str);
		}catch(Exception ex){
		} 
		if (ff > 0){
		    total += ff;
		}
	    }
	    str = ccv.getCitations();
	    if(str.equals("")) str = "&nbsp;";
	    out.println("<td>"+str+"</td>");
	    str = ccv.getDates();
	    if(str.equals("")) str = "&nbsp;";
	    out.println("<td>"+str+"</td>");
	    out.println("</tr>");
	}
	if(cvl.size() > 1 ){
	    out.println("<tr><td>Total</td><td align=\"right\">$"+
			total+"</td><td colspan=2>&nbsp;</td></tr>");
	}
	out.println("</table>");
    }
}






















































