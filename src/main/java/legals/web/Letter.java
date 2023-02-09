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

// check if used
@WebServlet(urlPatterns = {"/Letter"})
public class Letter extends TopServlet{

    static final long serialVersionUID = 49L;
    static Logger logger = LogManager.getLogger(Letter.class);
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
	String message="", action="", type = "";
	    
	String filed="",fee="",court_cost="", full_name="",
	    address="" ,cityStateZip="",dob="",ssn="", cause_num="",
	    per_day="", inspectionDate="", rental_id="";
	String signAttorney ="Pat Mulvi"; 
	//
	// res.setContentType("text/html");
	// PrintWriter out = res.getWriter();
	String name, value;
	HttpSession session = null;
	Enumeration<String> values = req.getParameterNames();
	String [] attorneys = {"Margie Rice",
	    "Vickie Renfrow"};
		
	String [] assistAttorneys = {"Susan Failey",
	    "Barbara E. McKinney",
	    "Jacquelyn F. Moore",
	    "Patricia M. Mulvihill",
	    "Michael M. Rouker",
	    "Inge Van der Cruysse"};

	ParagraphList pl = new ParagraphList(debug);
	List<legals.model.Paragraph> pls = null;
	String [] vals;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){ // case id
		id = value;
	    }
	}
	if(url.equals("")){
	    url    = getServletContext().getInitParameter("url");
	    //
	    String debug2 = getServletContext().getInitParameter("debug");
	    if(debug2.equals("true")) debug = true;
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
	    String str = cCase.getFiled();
	    if(str != null && !str.equals("")) filed = str;
	    str = cCase.getFine();
	    if(str != null && !str.equals("")) fee = "$"+str;
	    str = cCase.getCourt_cost();
	    if(str != null && !str.equals("")) court_cost = "$"+str;
	    str = cCase.getPer_day();
	    if(str != null && !str.equals("")) per_day = " Per Day ";
	    str = cCase.getCase_type();
	    if(str != null && !str.equals("")) type = str;
	    LegalList ll = new LegalList(debug);
	    ll.setCase_id(id);
	    back = ll.lookFor();
	    if(back.equals("")){
		List<Legal> list = ll.getLegals();
		if(list != null){
		    for(Legal lg: list){
			if(lg != null){
			    rental_id = lg.getRental_id();
			    break;
			}
		    }
		}
		if(!rental_id.equals("")){
		    Rental rt = new Rental(debug, rental_id);
		    inspectionDate = rt.getInspectionDate();
		}
	    }
	}
	if(!type.equals("")){
	    pl.setType(type);
	    back = pl.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		pls = pl.getParagraphs();
	    }
	}
	List<Defendant> defs = cCase.getDefendants();
	//
	// paper size legal (A3) 8.5 x 14
	Rectangle pageSize = new Rectangle(612, 792); // 8.5" X 11"
        Document document = new Document(pageSize, 24, 24, 36, 36);
	ServletOutputStream out2 = null;
	try{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter.getInstance(document, baos);
	    int count = 0;
	    String spacer  = " ";
	    String str = "";
	    count = defs.size();
	    int i=1;
	    document.open();
	    //
	    // Setting needed fornts
	    //
	    Font fnt = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);
	    Font fntb = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
	    Font fntb2 = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
	    //								  
	    Phrase spacePhrase = new Phrase();
	    Chunk ch = new Chunk(spacer, fnt);
	    spacePhrase.add(ch);
	    full_name=""; address="";cityStateZip="";
	    //
	    // Pdf starts here
	    //
	    Paragraph p = new Paragraph();
	    //
	    // You can use setWidths(widths) instead
	    //
	    float[] widths = {75f, 25f}; // percentages
	    PdfPTable table = new PdfPTable(widths);
	    table.setWidthPercentage(100);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	    float[] widths2 = {100f}; // left table
	    PdfPTable leftTable = new PdfPTable(widths2);
	    leftTable.setWidthPercentage(100);
	    leftTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    leftTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	    // right table
	    PdfPTable rightTable = new PdfPTable(widths2);
	    rightTable.setWidthPercentage(100);
	    rightTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    rightTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
	    Phrase phrase = new Phrase();
	    ch = new Chunk("Corporation Counsel ",fntb);
	    phrase.add(ch);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    ch = new Chunk(attorneys[0], fntb);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //
	    // empty two line2
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    leftTable.addCell(cell);			
	    //
	    ch = new Chunk("City Attorney ",fntb);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //				
	    ch = new Chunk(attorneys[1], fntb);
	    phrase = new Phrase();				
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    leftTable.addCell(cell);					
	    table.addCell(leftTable);
			
	    // right table
	    ch = new Chunk("Assistant City Attorneys", fntb);
	    phrase = new Phrase();				
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    rightTable.addCell(cell);
	    //
	    // list of assistant attorneys
	    for(String str2: assistAttorneys){
		ch = new Chunk(str2, fntb);
		phrase = new Phrase();				
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		rightTable.addCell(cell);
	    }
	    table.addCell(rightTable);
	    //
	    // header
	    document.add(table);
	    document.add(Chunk.NEWLINE);
	    document.add(Chunk.NEWLINE);
	    document.add(Chunk.NEWLINE);
	    //
	    // date
	    p = new Paragraph();
	    p.setAlignment(Element.ALIGN_LEFT);
	    ch = new Chunk(Helper.getToday2(),fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    p.add(spacePhrase);
	    p.add(Chunk.NEWLINE);
	    document.add(p);
	    //
	    full_name = "";
	    for(Defendant deff: defs){
		str = deff.getFullName2();
		if(i == 1){
		    Address addr = deff.getAddress();
		    address = addr.getAddress();
		    cityStateZip = addr.getCityStateZip();
		    address = Helper.initCap(address);
		}
		if(str != null){
		    str = Helper.initCap(str);
		}
		if(!full_name.equals("")){
		    full_name += ", ";
		}
		full_name += str;
		i++;
	    }
	    //
	    p = new Paragraph();
	    p.setAlignment(Element.ALIGN_LEFT);
	    ch = new Chunk(full_name, fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    ch = new Chunk(address, fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    ch = new Chunk(cityStateZip, fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    p.add(Chunk.NEWLINE);
	    document.add(p);
	    //
	    // violation address from the case
	    //
	    address = "";
	    List<legals.model.Address> addresses = cCase.getAddresses();
	    if(addresses != null && addresses.size() > 0){
		for(legals.model.Address addr: addresses){
		    address = addr.getAddress();
		    if(!address.equals("")) break; // we want the first
		}
	    }
	    p = new Paragraph();
	    p.setAlignment(Element.ALIGN_LEFT);
	    ch = new Chunk("RE: Property located at "+address+", Bloomington", fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    p.add(Chunk.NEWLINE);
	    document.add(p);
	    //
	    p = new Paragraph();
	    p.setAlignment(Element.ALIGN_LEFT);
	    if(defs.size() > 1){
		ch = new Chunk("Dear Property Owners", fnt);
	    }
	    else{
		ch = new Chunk("Dear Property Owner", fnt);
	    }
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    p.add(Chunk.NEWLINE);
	    document.add(p);
	    //			
	    // letter content
	    if(pls != null && pls.size() > 0){
		for(legals.model.Paragraph pp: pls){
		    p = new Paragraph();
		    p.setAlignment(Element.ALIGN_LEFT);
		    str = pp.getText();
		    if(str.indexOf("_") > -1){
			if(str.indexOf("_fee_") > -1){
			    str = str.replace("_fee_",fee);
			}
			if(str.indexOf("_address_") > -1){
			    str = str.replace("_address_",address);
			}
			if(str.indexOf("_inspectionDate_") > -1){
			    str = str.replace("_inspectionDate_", inspectionDate);
			}	
		    }
		    ch = new Chunk(str, fnt);
		    p.add(ch);
		    p.add(Chunk.NEWLINE);
		    p.add(Chunk.NEWLINE);						
		    document.add(p);
		}
	    }
	    //
	    p = new Paragraph();
	    p.setAlignment(Element.ALIGN_LEFT);
	    p.add(Chunk.NEWLINE);	
	    ch = new Chunk("Sincerely", fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    p.add(Chunk.NEWLINE);
	    p.add(Chunk.NEWLINE);	
	    ch = new Chunk(signAttorney, fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);
	    ch = new Chunk("Assistant City Attorney", fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);	
	    p.add(Chunk.NEWLINE);
	    ch = new Chunk("cc: Housing and Neighborhood Development Department", fnt);
	    p.add(ch);
	    p.add(Chunk.NEWLINE);	
	    p.add(Chunk.NEWLINE);
	    document.add(p);
	    //			
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
	//cCase.doCleanup();
    }


}






















































