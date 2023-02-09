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
import legals.utils.*;
/**
 *
 */
@WebServlet(urlPatterns = {"/CaseTypeService","/TypeService"})
public class CaseTypeService extends TopServlet{

    static final long serialVersionUID = 27L;
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
	PrintWriter out = null;
	String deptId="", deptName="",typeId="", typeDesc="";
	boolean success = true;
	String message = "", name="", value="", format="";
	CaseTypeList typesl = new CaseTypeList(debug);
	List<CaseType> types = null;
	Enumeration<String> values = req.getParameterNames();
	
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    value = req.getParameter(name).trim();
	    if (name.equals("deptId"))
		typesl.setDeptId(value);
	    else if (name.equals("deptName"))
		typesl.setDeptName(value);			
	    else if (name.equals("typeId"))
		typesl.setTypeId(value);
	    else if(name.equals("typeDesc"))
		typesl.setTypeDesc(value);
	    else if(name.equals("format"))
		format = value;						
	}
	if(!success){
	    res.setContentType("text/xml");
	    out = res.getWriter();						
	    out.println("<?xml version='1.0' encoding='UTF-8'?>");
	    out.println("<types>");
	    out.println("   <type>");
	    out.println("     <id>Error</id>");
	    out.println("     <description>");
	    out.println("Try again later or contact ITS for help");
	    out.println("     </description>");
	    out.println("   </type>");
	    out.println("</types>");
	    out.close();
	    return;
	}
	String str = typesl.find();
	types = typesl.getTypes();
	if(str.equals("")){
	    if(format.equals("")){
		res.setContentType("text/xml");
		out = res.getWriter();
		composeXml(out, types);
	    }
	    else{
		res.setContentType("text/json");
		out = res.getWriter();								
		composeJson(out,types);
	    }
	}
	out.close();
    }
    //
    void composeXml(PrintWriter out, List<CaseType> types){

	String all="<?xml version='1.0' encoding='UTF-8'?>\n<types>\n";
	for(CaseType type: types){
	    all += "  <type>\n";
	    all += "     <id>"+type.getId()+"</id>\n";
	    all += "     <description>"+Helper.replaceAmp(type.getDesc())+"</description>\n";
	    all += "  </type>\n";
	}
	all += "</types>\n";		
	out.println(all);
	if(debug){
	    logger.debug(all);
	}
		
    }
    void composeJson(PrintWriter out, List<CaseType> types){
	String all=""; // "<?xml version='1.0' encoding='UTF-8'?>";
	for(CaseType type: types){
	    if(!all.equals("")) all += ",\n"; 
	    all += "{\"id\":\""+type.getId()+"\",\"description\":\""+Helper.replaceAmp(type.getDesc())+"\"}";
	}
	all = "["+all+"]";		
	out.println(all);
	System.err.println(all);
    }
}






















































