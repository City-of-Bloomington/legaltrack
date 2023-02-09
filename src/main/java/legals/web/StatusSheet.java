package legals.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
//
// Pdf related
//
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.list.*;
import legals.utils.*;


@WebServlet(urlPatterns = {"/StatusSheet"})
public class StatusSheet extends TopServlet{

    static final long serialVersionUID = 74L;
    static Logger logger = LogManager.getLogger(StatusSheet.class);
    /**
     * Generates the Defendent form and processes view, add, update and delete
     * operations.
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String id="";

	boolean idFound = false, success = true;
	String message="", action="", role="";
	    
	String filed="",fine="",court_cost="", full_name="",
	    address="" ,cityStateZip="",dob="",ssn="", cause_num="",
	    per_day="", balance="";
	//
	// res.setContentType("text/html");
	// PrintWriter out = res.getWriter();
	String name, value;
	HttpSession session = null;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){ // case id
		id = value;
	    }
	}
	User user = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	Case cCase = new Case(debug, id);
	String back = cCase.doSelect();
	if(!back.equals("")){
	    logger.error(back);
	    message += back;
	    success = false;
	}
	else{
	    double fn = 0.0;
	    String str = cCase.getFiled();
	    if(str != null && !str.equals("")) filed = str;
	    str = cCase.getFine();
	    if(str != null && !str.equals("")){
		fine = "$"+str;
		try{
		    fn = Double.parseDouble(str);
		}catch(Exception ex){}; // just ignore
	    }
	    str = cCase.getCourt_cost();
	    if(str != null && !str.equals("")){
		court_cost = "$"+str;
		try{
		    fn = fn + Double.parseDouble(str);
		}catch(Exception ex){}; // just ignore
	    }
	    str = cCase.getPer_day();
	    if(str != null && !str.equals("")) per_day = " Per Day ";
	    if(per_day.equals("")){
		if(fn > 0.){
		    str = ""+fn;
		    balance = "$"+Helper.formatNumber(str);
		}
	    }
	}
	List<Defendant> defs = cCase.getDefendants();
	//
	// paper size legal (A3) 8.5 x 14
	Rectangle pageSize = new Rectangle(612, 756 ); // 8.5" X 14" 612x 1008
        Document document = new Document(pageSize, 18, 18, 36, 36);
	ServletOutputStream out2 = null;
	try{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter.getInstance(document, baos);
	    int count = 0;
	    String spacer  = "________________";
	    String spacer2 = "(______) ______-_______";
	    String str = "", co_name="";
	    count = defs.size();
	    int i=1;
	    document.open();
	    //
	    // For each defendant, one page will be created
	    //
	    for(Defendant deff: defs){
		full_name="";ssn="";dob="";address="";cityStateZip="";
		co_name=""; // care of
		cause_num="53C0";
		str = deff.getFullName2();
		if(str != null) full_name = Helper.initCap(str);
		str = deff.getSsn();
		if(str != null && !str.equals("")) ssn = str;
		else ssn = spacer;
		str = deff.getDob();
		if(str != null && !str.equals("")) dob = str;
		else dob = spacer;
		if(deff.hasCareOf()){
		    co_name = deff.getCareOfName();
		}
		Address addr = deff.getAddress();
		str = addr.getAddress();
		if(str != null) address = Helper.initCap(str);
		str = addr.getCityStateZip();
		if(str != null && !str.trim().equals("")) cityStateZip = str;
		else cityStateZip = spacer;
		str = deff.getCauseNumForCase(id);
		if(str != null) cause_num = str;
		//
		// Pdf starts here
		//
		Font fnt = new Font(Font.TIMES_ROMAN, 12, 
				    Font.NORMAL);
		Font fntb = new Font(Font.TIMES_ROMAN, 12, 
				     Font.BOLD);
		Font fntb2 = new Font(Font.TIMES_ROMAN, 16, 
				      Font.BOLD);
		//
		Paragraph p = new Paragraph();
		p.setAlignment(Element.ALIGN_CENTER);
		Chunk ch = new Chunk("STATUS SHEET\n", fntb2);
		p.add(ch);
		document.add(p);
		//
		// You can use setWidths(widths) instead
		//
		float[] widths = {20f, 35f, 20f, 25f}; // percentages
		PdfPTable table = new PdfPTable(widths);
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		//
		// 1 st row
		Phrase spacePhrase = new Phrase();
		ch = new Chunk(spacer,fnt);
		spacePhrase.add(ch);
		//
		Phrase phrase = new Phrase();
		ch = new Chunk("City Of Bloomington ",fntb);
		phrase.add(ch);
		PdfPCell cell = new PdfPCell(phrase);
		cell.setColspan(2);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		ch = new Chunk("ID: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(id,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		//
		// row 2
		ch = new Chunk("V.",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		table.addCell(" ");
		ch = new Chunk("License Plate #:",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		table.addCell(spacePhrase);
		//
		// row 3
		ch = new Chunk("Defendant: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(full_name ,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk("Number of Tickets: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		table.addCell(spacePhrase);
		//
		if(!co_name.equals("")){
		    ch = new Chunk("c/o: ",fntb);
		    phrase = new Phrase();
		    phrase.add(ch);
		    table.addCell(phrase);
		    ch = new Chunk(co_name ,fnt);
		    phrase = new Phrase();
		    phrase.add(ch);
		    table.addCell(phrase);
		    //
		    table.addCell(" ");
		    table.addCell(" ");
		}
		// row 4
		ch = new Chunk("Address: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(address,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		//
		ch = new Chunk("Cause #: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(cause_num,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		//
		// row 5
		ch = new Chunk("City, State & Zip: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(cityStateZip,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		//
		ch = new Chunk("Date Of Birth: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(dob,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		//
		// row 6
		ch = new Chunk("Phone (Home/Cell): ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		// cell = new PdfPCell(phrase);
		table.addCell(phrase);
		Chunk ch2 =  new Chunk(spacer2,fnt);
		phrase = new Phrase();
		phrase.add(ch2);
		// 
		table.addCell(phrase);
		//
		ch = new Chunk("Social Security #: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(ssn,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		//
		// row 7
		ch = new Chunk("Phone (Work): ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch2 =  new Chunk(spacer2,fnt);
		phrase = new Phrase();
		phrase.add(ch2);
		table.addCell(phrase);

		ch = new Chunk("Balance: ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		table.addCell(spacePhrase);
		//
		document.add(table);
		document.add(Chunk.NEWLINE);
		//
		// Second Table
		float[] widths2 = {12f, 61f, 8f, 12f, 8f}; // Percentages
		table = new PdfPTable(widths2);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setWidthPercentage(100);
		table.getDefaultCell().setBorderWidth(0.5f);
		//
		// 1 st row (Table head)
		phrase = new Phrase();
		ch = new Chunk(" Date ",fntb);
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(" Info / Comments ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(" CC Pd ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(" Balance ",fntb);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		ch = new Chunk(" Initials ",fnt);
		phrase = new Phrase();
		phrase.add(ch);
		table.addCell(phrase);
		//
		// row 2
		ch = new Chunk(filed,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setFixedHeight(25f);
		table.addCell(cell);

		ch = new Chunk("Complaint filed    "+fine+per_day+" + "+
			       court_cost+" CC",fnt);
		phrase = new Phrase();
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setFixedHeight(25f);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		for(int j=0;j<3;j++){
		    phrase = new Phrase();
		    if(j == 1 && !balance.equals("")){
			ch = new Chunk(balance,fntb);
		    }
		    else{
			ch = new Chunk(" ");
		    }
		    phrase.add(ch);
		    cell = new PdfPCell(phrase);
		    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    cell.setFixedHeight(25f);
		    table.addCell(cell);

		}
		//
		for(int j=0;j<5*18;j++){ // 28
		    phrase = new Phrase();
		    ch = new Chunk(" ");
		    phrase.add(ch);
		    cell = new PdfPCell(phrase);
		    cell.setFixedHeight(25f);
		    table.addCell(cell);
		}
		document.add(table);
		//
		// For another defendant start a new page here
		//
		if(i < count ){
		    document.newPage();
		}
		i++;
		//
	    }
	    document.close();
	    res.setHeader("Expires", "0");
	    res.setHeader("Cache-Control", 
			  "must-revalidate, post-check=0, pre-check=0");
	    res.setHeader("Pragma", "public");
	    //
	    // setting the content type
	    res.setContentType("application/pdf");
	    //
	    // the contentlength is needed for MSIE!!!
	    res.setContentLength(baos.size());
	    //
	    out2 = res.getOutputStream();
	    if(out2 != null){
		baos.writeTo(out2);
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    success = false;
	    message = "Error "+ ex;
	}
	finally{
	    if(out2 != null){ 
		out2.flush();
		out2.close();
	    }
	}
    }


}






















































