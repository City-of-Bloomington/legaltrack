package legals.web;
import java.net.URI;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.utils.*;

public class TopServlet extends HttpServlet {
    static String url = "", logoUrl="", emailStr="";
    static String server_path="", // /mnt/mounts/legal/
	server_path2="", // /srv/data/legaltrack/files/
	legalMail="",
	addrCheckUrl="",
	handMail="", 
	handMail2="",
	caseEmail="",
	parkingUser="", courtCostDefault="114";
    
    static String rentalUrl = ""; //old url5 
    static boolean debug = false, activeMail=false;
    static Configuration config = null;
    static Logger logger = LogManager.getLogger(TopServlet.class);
    static ServletContext context = null;
    public void init(ServletConfig conf){
	try{
	    context = conf.getServletContext();
	    url = context.getInitParameter("url");
	    String str = context.getInitParameter("debug");
	    if(str != null && str.equals("true")) debug = true;
	    str = context.getInitParameter("activeMail");
	    if(str != null && str.equals("true")) activeMail = true;	    
	    str = context.getInitParameter("server_path");
	    if(str != null) server_path = str;
	    str = context.getInitParameter("server_path2");
	    if(str != null) server_path2 = str;	    
	    str = context.getInitParameter("addrCheckUrl");
	    if(str != null)
		addrCheckUrl = str;
	    str = context.getInitParameter("rentalUrl");
	    if(str != null)
		rentalUrl = str;	    
	    str = context.getInitParameter("logoUrl");
	    if(str != null)
		logoUrl = str;
	    str = context.getInitParameter("emailStr");
	    if(str != null)
		emailStr = "@"+str;
	    str = context.getInitParameter("parkingUser");
	    if(str != null)
		parkingUser = str;
	    str = context.getInitParameter("legalMail");
	    if(str != null)
		legalMail = str;
	    str = context.getInitParameter("caseEmail");
	    if(str != null)
		caseEmail = str;
	    str = context.getInitParameter("handMail");
	    if(str != null)
		handMail = str;
	    str = context.getInitParameter("handMail2");
	    if(str != null)
		handMail2 = str;
	    str = context.getInitParameter("courtCost");
	    if(str != null)
		courtCostDefault = str;	    
	    String username = context.getInitParameter("adfs_username");
	    String auth_end_point = context.getInitParameter("auth_end_point");
	    String token_end_point = context.getInitParameter("token_end_point");
	    String callback_uri = context.getInitParameter("callback_uri");
	    String client_id = context.getInitParameter("client_id");
	    String client_secret = context.getInitParameter("client_secret");
	    String scope = context.getInitParameter("scope");
	    String discovery_uri = context.getInitParameter("discovery_uri");
	    config = new
		Configuration(auth_end_point, token_end_point, callback_uri, client_id, client_secret, scope, discovery_uri, username);
	    // System.err.println(config.toString());
	}catch(Exception ex){
	    System.err.println(" top init "+ex);
	    logger.error(" "+ex);
	}
    }

}
