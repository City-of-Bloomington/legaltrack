package legals.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.list.*;

/**
 *
 */
@WebServlet(urlPatterns = {"/AttorneyService"})
public class AttorneyService extends TopServlet{

    static final long serialVersionUID = 21L;
    String source = "Bloomington City Hall and Police Department";
    static Logger logger = LogManager.getLogger(CaseTypeService.class);
    /**
     * Generate the search form for ticket database.
     * @param req the request input stream
     * @param res the response output stream
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	doGet(req,res);
    }
    /**
     * Generate the report data.
     * @param req the request input stream
     * @param res the response output stream
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{
	res.setStatus(HttpServletResponse.SC_OK);
	res.setContentType("application/json");
		
	PrintWriter out = res.getWriter();
	String deptId="", deptName="",typeId="", typeDesc="";
	boolean success = true;
	String message = "", name="", value="";
	Enumeration<String> values = req.getParameterNames();
	List<Lawyer> list = null;
	LawyerList alist = new LawyerList(debug);
	alist.setCurrentOnly(); // active attorneys only
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    value = req.getParameter(name).trim();
	}
	message = alist.find();
	if(message.equals("")){
	    list = alist.getLawyers();
	    composeJson(out, list);	    
	}
	else{
	    out.println("{\"Error\":\""+message+"\"}");
	    out.close();
	    return;
	}
	out.close();
	
    }
    //
    void composeJson(PrintWriter out, List<Lawyer> list){

	// String all="{\"attorneys\":[";
	String all = "";
	for(Lawyer one: list){
	    if(!all.equals("")) all +=",";
	    all += "{\"id\":\""+one.getId()+"\",\"empid\":\""+one.getEmpid()+"\",\"fullName\":\""+one.getFullName()+"\",\"position\":\""+one.getPosition()+"\",\"title\":\""+one.getTitle()+"\"}";
	}
	all = "{\"attorneys\":["+all+"]}";		
	out.println(all);
	if(debug){
	    logger.debug(all);
	}
		
    }

}






















































