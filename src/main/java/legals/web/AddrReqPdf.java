
package legals.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
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
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import legals.model.*;
import legals.utils.*;
import legals.list.*;

/**
 *
 */
@WebServlet(urlPatterns = {"/AddrReqPdf"})
public class AddrReqPdf extends TopServlet {

    static final long serialVersionUID = 70L;
    static Logger logger = LogManager.getLogger(AddrReqPdf.class);
    boolean debug = false;
    String monthArr[] = {"",
	"January","February","March","April","May","June",
	"July","August","September","October","November",
	"December"};
    String court[] = {"","I","II","III","IV","V","VI","VII","VIII","IX","X"};
    //
    // such as 1st, 2nd, 3rd, 4th
    String dayOrder[] = {"",
	"st","nd","rd","th","th","th","th","th","th","th",
	"th","th","th","th","th","th","th","th","th","th",
	"st","nd","rd","th","th","th","th","th","th","th",
	"st"};
    //
    // pronouns and their possessive pronouns 
    //
    // Case.identArr[] = {"","He","She","They","It");
    final static String posPronArr[] = {"","his","her","their","its"};

    /**
     * Generates the request form.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	String name, value;
	String action="";
	String judge[] = null, judges="";
	String cur_path="";
	boolean success = true;
	int resolved_month = 0;
	// 
	User user = null;
	HttpSession session = req.getSession(false);
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
	//
	Calendar current_cal = Calendar.getInstance();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	String todayText = monthArr[mm]+" "+dd+", "+yyyy;
	String today = ""+mm+"/"+dd+"/"+yyyy;
	String start_month = ""+mm+"/1/"+yyyy;
	current_cal.add(Calendar.DATE,-6); // one week ago
	mm = current_cal.get(Calendar.MONTH)+1;
	dd = current_cal.get(Calendar.DATE);
	yyyy = current_cal.get(Calendar.YEAR);
	String start_week = ""+mm+"/"+dd+"/"+yyyy;
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	//
	out.println("  function validateForm(){		         ");
	out.println("   with(document.myForm){                   ");
	out.println("   if (date_from.value.length > 0){         ");
	out.println("  if(!validateDate(date_from.value)){       ");
	out.println("     alert(\"Invalid date \"+date_from.value); ");
	out.println("  date_from.focus();                           ");
	out.println("     return false;			            ");
	out.println("	}}                                          ");
	out.println("  	 if (date_to.value.length > 0){             ");
	out.println("  if(!validateDate(date_to.value)){            ");
	out.println("     alert(\"Invalid date \"+date_to.value);   ");
	out.println("  date_to.focus();                             ");
	out.println("     return false;			            ");
	out.println("	}}                                          ");
	out.println("	}                                           ");
	out.println("     return true;				    ");
	out.println("	}	         			    ");
	out.println(" </script>				                 ");
	//
	out.println("<center><h2>Request Address Update</h2>");
	out.println("<form name=\"myForm\" method=\"post\">");				
	out.println("<table width=\"60%\" border=\"1\">");
	out.println("<tr><td align=\"center\" style=\"background-color:navy; color:white\">"+
		    "<b> Mailers </b>"+
		    "</td></tr>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table width=\"100%\">");
	//
	out.println("<tr><td align=\"left\"> ");
	out.println("<table>");
	out.println("<td align=\"left\">Postmaster at:</td><td colspan=\"2\" align=\"left\">");
				
	out.println("<input name=\"post_master\" value=\"Bloomington, IN 47401\" size=\"30\" maxlength=\"80\" /></td></tr>");				

	out.println("<tr><td align=\"left\">Letter Date: </td><td align=\"left\">"+
		    "<input name=\"letter_date\" value=\""+today+
		    "\" size=\"10\" maxlength=\"10\" class=\"date\" /></td></tr>");
	out.println("<tr> ");
	out.println("<td align=\"left\"> ");
	out.println("<tr><td></td><td align=\"left\">From</td><td align=\"left\">To</td></tr>");
	out.println("<tr><td align=\"left\">Check Address Date</td><td>");
	out.println("<input name=\"date_from\" value=\""+start_week+
		    "\" size=\"10\" maxlength=\"10\" class=\"date\" /></td><td>");
	out.println("<input name=\"date_to\" value=\""+today+
		    "\" size=\"10\" maxlength=\"10\" class=\"date\"></td></tr>");
	out.println("</table></td></tr>");
	//
	out.println("</table></td></tr>");
	out.println("<tr><td align=\"right\"><input type=\"submit\" " +
		    "name=\"action\" "+
		    "value=\"Submit\" /></td></tr>");
	//
	out.println("</table>");
	out.println("</form>");
	out.println("<br />");
	String dateStr = "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";
		
	out.println("<script type=\"text/javascript\" src=\""+url+"js/jquery-1.7.2.min.js\"></script>");
	out.println("<script type=\"text/javascript\" src=\""+url+"js/jquery-ui-1.8.18.custom.min.js\"></script>");
	out.println("<script>");
	out.println(" var icons = { header:\"ui-icon-circle-plus\","+
		    "               activeHeader:\"ui-icon-circle-minus\"}");
	out.println("  $( \".date\" ).datepicker("+dateStr+"); ");
	out.println("</script>");			
	out.println("</div>");
	out.print("</center></body></html>");
	out.close();

    }

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	res.setContentType("text/html");

	String date_from="",date_to="", 
	    post_master="",letter_date="";
	String name, value, message="";

	boolean success=true;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("date_from")) {
		date_from=value;
	    }
	    else if (name.equals("date_to")) {
		date_to=value;
	    }
	    else if (name.equals("letter_date")) {
		letter_date=value;
	    }
	    else if (name.equals("post_master")) {
		post_master=value;
	    }						
	}
	User user = null;
	HttpSession session = req.getSession(false);
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
	String month="", year="", day="";
	int dayInt=0, monthInt=0;
	LawyerList attorneysl = new LawyerList(debug);
	attorneysl.setCurrentOnly();
	attorneysl.find();
	List<Lawyer> attorneys = attorneysl.getLawyers();
	if(!letter_date.equals("")){
	    int i = letter_date.indexOf("/");
	    int j = letter_date.lastIndexOf("/");
	    month = letter_date.substring(0,i);
	    try{
		dayInt = Integer.parseInt(letter_date.substring(i+1,j));
		monthInt = Integer.parseInt(letter_date.substring(0,i));
		day = ""+dayInt+dayOrder[dayInt];
		month = monthArr[monthInt];
	    }catch(Exception ex){};
	    year = letter_date.substring(j+1);
	}				
	//
	/// Release judgment
	//
	if(true){
						
	    List<Case> cases = null;
	    CaseList cll = new CaseList(debug);
	    cll.setForAddrRequest();
	    cll.setDate_from(date_from);
	    cll.setDate_to(date_to);
	    String back = cll.find();
	    if(back.equals("")){
		cases = cll.getCases();
	    }
	    String query ="",str="",str2="",address="",
		id="", cause_num="", hearings="", // bunch of dates
		cause_num2="", 
		attorneyFullName="", attorneyBar="", attorneyTitle="",
		full_names="", care_of="",
		def_id="";						
	    //
	    boolean multiple = false;

	    try{
		if(cases == null || cases.size() == 0){
		    PrintWriter out = res.getWriter();
		    Helper.writeHeader(out, url, user);
		    out.println("<p style='text-align:center'>No match found </p>");
		    Helper.writeFooter(out);
		    return;
		}
		else{
		    Rectangle pageSize = new Rectangle(612, 792); // 8.5" X 11"
		    Document document = new Document(pageSize, 54, 54, 54, 36);// 18,18,54,35
		    ServletOutputStream out = null;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    PdfWriter writer = PdfWriter.getInstance(document, baos);
		    document.open();
		    PdfPTable header = getHeader(attorneys);
		    //
		    // check if this is a one defendant case
		    // or multiple, in the old cases the defendants
		    // were listed together using & symbol
		    // we had to use both & and "and" to figure 
		    // out  
		    //
		    for(Case one:cases){
			document.add(header);
			address="";
			cause_num =""; cause_num2=""; hearings="";
			attorneyFullName=""; attorneyBar="";
			attorneyTitle="";	full_names=""; care_of="";
			String city="", state="", zip="";
			id = one.getId();
			hearings = one.getAll_hearings();
			List<Defendant> defs = one.getDefendants();
			multiple = false;
			if(defs != null){
			    for(Defendant one2: defs){
				str = one2.getFullName();

				care_of = one2.getCareOfName();
				if(str.indexOf(" & ") > -1){
				    str = Helper.changeAmpToAnd(str);
				}
				if(!full_names.equals("")){
				    full_names += " and ";
				    multiple = true;
				}
				full_names += str;
				DefAddress addr = one2.getInvalidAddress();
				if(addr != null){
				    address = addr.getAddress();
				    city = addr.getCity();
				    state = addr.getState();
				    zip = addr.getZip();
				}
				str = one2.getCauseNumForCase(id);
				if(!str.equals("")){
				    if(!cause_num.equals("")) cause_num +=", ";
				    cause_num += str;
				}																
			    }
			}
			Lawyer attorney = one.getLawyer();
			if(attorney != null){
			    attorneyFullName = attorney.getFullName();
			    attorneyBar = attorney.getBarNum();
			    attorneyTitle = attorney.getTitle();
			}
			if(full_names.indexOf(" & ") > -1 ){
			    multiple = true;
			    full_names = Helper.changeAmpToAnd(full_names);
			}
			else if(full_names.indexOf(" and ") > -1) 
			    multiple = true;
			writeLetter(
				    document,
				    id,
				    letter_date,
				    hearings,
				    post_master,
				    cause_num,
				    cause_num2,
				    multiple,
				    address,
				    city,
				    state,
				    zip,
				    full_names,
				    attorneyFullName,
				    attorneyBar,
				    attorneyTitle,
				    day,
				    month,
				    year);
			document.newPage();	
		    }
		    document.close();
		    writer.close();
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
		    out = res.getOutputStream();
		    if(out != null){
			baos.writeTo(out);
		    }								
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+query);
	    }
	}
    }
    PdfPTable getHeader(List<Lawyer> attorneys){
	//
	// as file source use the following (on apps)
	// logoUrl = "c:/webapps/ROOT/images/cite/citylogo2.png";
	//
	String str = "";
	String spacer = "   ";
	//
	PdfPTable headTable = null;
	try{
	    //
	    // for http url use
	    // Image image = Image.getInstance(new Url(logoUrl2));
	    //
	    Image image = Image.getInstance(logoUrl);

	    Font fnt = new Font(Font.TIMES_ROMAN, 10, 
				Font.NORMAL);
	    Font fntb = new Font(Font.TIMES_ROMAN, 10, 
				 Font.BOLD);
	    Font fnt2 = new Font(Font.TIMES_ROMAN, 10, 
				 Font.NORMAL);			
	    float[] widths = {33f, 34f, 33f}; // percentages
	    headTable = new PdfPTable(widths);
	    headTable.setWidthPercentage(100);
	    headTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    headTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    float[] width ={33f}; 
	    PdfPTable leftTable = new PdfPTable(width);
	    leftTable.setWidthPercentage(33);
	    leftTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    leftTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
	    // space
	    Phrase spacePhrase = new Phrase();
	    Chunk ch = new Chunk(spacer, fnt);
	    spacePhrase.add(ch);
	    PdfPCell cell = new PdfPCell(spacePhrase);
	    // cell.setColspan(2);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //
	    Phrase phrase = new Phrase();
	    ch = new Chunk("Corporation Counsel ",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //
	    phrase = new Phrase();
	    for(Lawyer one:attorneys){
		if(one.isCounsel()){
		    ch = new Chunk(one.getFullName(),fntb);
		    phrase.add(ch);
		    break;
		}
	    }
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //
	    // space
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //
	    phrase = new Phrase();
	    ch = new Chunk("City Attorney ",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //
	    phrase = new Phrase();
	    for(Lawyer one:attorneys){
		if(one.isAttorney()){
		    ch = new Chunk(one.getFullName(),fntb);
		    phrase.add(ch);
		    break;
		}
	    }			
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    //
	    // two spaces
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    leftTable.addCell(cell);				
	    //
	    // add left table to header table
	    headTable.addCell(leftTable);
	    //
	    // Middle table
	    PdfPTable midTable = new PdfPTable(width);
	    midTable.setWidthPercentage(34);
	    midTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    midTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    // adding image
	    //
	    image.setWidthPercentage(33.0f);				
	    cell = new PdfPCell(image);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    midTable.addCell(cell);
	    //
	    phrase = new Phrase();
	    ch = new Chunk("City of Bloomington ",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    midTable.addCell(cell);
	    phrase = new Phrase();
	    ch = new Chunk("Legal Department ",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    midTable.addCell(cell);
	    //
	    // add midTable to header
	    //
	    headTable.addCell(midTable);
	    //
	    // right table
	    //
	    // Middle table
	    PdfPTable rightTable = new PdfPTable(width);
	    rightTable.setWidthPercentage(3);
	    rightTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    rightTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    rightTable.addCell(cell);
	    //
	    phrase = new Phrase();
	    ch = new Chunk("Assistant City Attorneys",fntb);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);				
	    rightTable.addCell(cell);

	    for(Lawyer one:attorneys){
		if(one.isAssistant()){
		    phrase = new Phrase();					
		    ch = new Chunk(one.getFullName(),fntb);
		    phrase.add(ch);
		    cell = new PdfPCell(phrase);
		    cell.setBorder(Rectangle.NO_BORDER);
		    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		    rightTable.addCell(cell);
		}
	    }						
	    // add right table to header
	    headTable.addCell(rightTable);
	}
	catch(Exception ex){
	    logger.error(ex);
	}
	return headTable;
    }		
    void writeLetter(Document document,
		     String id,
		     String letter_date,
		     String hearings,
		     String post_master,										 
		     String cause_num,
		     String cause_num2,
		     boolean multiple,
		     String address,
		     String city,
		     String state,
		     String zip,
		     String full_names,
		     String attorneyFullName,
		     String attorneyBar,
		     String attorneyTitle,
		     String day,
		     String month,
		     String year
		     ){
	//
	String str = "";
	String spacer = "   ";
	int pp_indent = 20;
	//
	PdfPTable headTable = null;
	try{
	    //
	    // for http url use
	    // Image image = Image.getInstance(new Url(logoUrl2));
	    //
	    // Image image = Image.getInstance(logoUrl);

	    Font fnt = new Font(Font.TIMES_ROMAN, 10, 
				Font.NORMAL);
	    Font fntb = new Font(Font.TIMES_ROMAN, 10, 
				 Font.BOLD);
	    Font fnt2 = new Font(Font.TIMES_ROMAN, 10, 
				 Font.NORMAL);
	    Font fntbu = new Font(Font.TIMES_ROMAN, 10, 
				  Font.BOLD | Font.UNDERLINE);						
						
	    float[] widths = {50f,50f}; // percentages
	    headTable = new PdfPTable(widths);
	    headTable.setWidthPercentage(100);
	    headTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    headTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
						
	    // space
	    Phrase spacePhrase = new Phrase();
	    Chunk ch = new Chunk(spacer, fnt);
	    spacePhrase.add(ch);
	    //
	    Phrase phrase = new Phrase();
	    ch = new Chunk("To: Postmaster ",fnt);
	    phrase.add(ch);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);

	    phrase = new Phrase();
	    ch = new Chunk("ID#: "+id+" Cause #:"+cause_num, fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);

	    // 2nd row
	    phrase = new Phrase();
	    ch = new Chunk("   "+post_master,fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);						
	    //
	    // 2nd row
	    phrase = new Phrase();
	    ch = new Chunk("Date: "+letter_date, fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);
	    if(!hearings.equals("")){
		// third row								
		cell = new PdfPCell(spacePhrase);
		cell.setBorder(Rectangle.NO_BORDER);
		headTable.addCell(cell);	
								
		ch = new Chunk(hearings,fnt);
		phrase = new Phrase();
		phrase.add(ch);
		cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		// 
		headTable.addCell(cell);
	    }
	    document.add(headTable);
	    document.add(spacePhrase);
	    //
	    Paragraph pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setAlignment(Element.ALIGN_CENTER);
	    ch = new Chunk("Address Information Request", fntb);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);

	    pp = new Paragraph();
	    // pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(20);
	    pp.setAlignment(Element.ALIGN_LEFT);
	    // pp.setLeading(0,2);
	    ch = new Chunk("Please furnish this agency with the new address, if available, for the following individual or verify whether the address given below is one at which mail for this individual is currently being delivered.  If the following address is a post office box, please furnish the street address as recorded on the boxholder's application form.\n\n",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);
	    //
	    float[] widths2 = {35f, 65f};
	    PdfPTable table = new PdfPTable(widths2);
	    table.setWidthPercentage(80);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

	    // first row
	    ch = new Chunk("NAME:",fntb);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);								
	    ch = new Chunk(full_names, fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);								
	    //
	    // 2nd row
	    ch = new Chunk("LAST KNOWN ADDRESS:",fntb);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    ch = new Chunk(address,fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 3rd row
	    ch = new Chunk("POST OFFICE:",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
						
	    //
	    ch = new Chunk(city, fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    ch = new Chunk(" STATE:", fntb);
	    phrase.add(ch);
	    ch = new Chunk(state, fnt);
	    phrase.add(ch);
	    ch = new Chunk(" ZIP:", fntb);
	    phrase.add(ch);
	    ch = new Chunk(zip, fnt);
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    document.add(table);
	    document.add(spacePhrase);
						
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setAlignment(Element.ALIGN_LEFT);
	    pp.setLeading(0, 2);
	    ch = new Chunk("I certify that the address information for this individual is required for the performance of this agency's official duties.",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);
	    phrase.add(Chunk.NEWLINE);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);
						
	    table = new PdfPTable(widths);
	    table.setWidthPercentage(100);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

	    // first row
	    PdfPCell emptyCell = new PdfPCell(spacePhrase);
	    emptyCell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(emptyCell);
	    ch = new Chunk("_______________________",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase); 
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 2nd row
	    table.addCell(emptyCell);
	    ch = new Chunk("(Signature of Agency Official)\n\n",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase); 
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 2nd row
	    table.addCell(emptyCell);						
	    ch = new Chunk("Corporation Counsel, City of Bloomington, Indiana\n (Title)",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    document.add(table);
	    //
	    // line separator
	    PdfPTable sepTable = new PdfPTable(1);
	    emptyCell.setBorder(Rectangle.BOTTOM);
	    sepTable.addCell(emptyCell);
	    sepTable.setHorizontalAlignment(Element.ALIGN_LEFT);
	    sepTable.setWidthPercentage(100f);
	    document.add(sepTable);
	    //
	    ch = new Chunk("FOR POST OFFICE USE ONLY",fntb);
	    phrase = new Phrase();
	    phrase.add(ch);
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setAlignment(Element.ALIGN_CENTER);
	    pp.add(phrase);
	    document.add(pp);						
	    //
	    table = new PdfPTable(widths);
	    table.setWidthPercentage(100);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    //
	    // 1st row
	    ch = new Chunk("[ ] Mail is delivered to address given",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    ch = new Chunk("[ ] Moved, left no forwarding address",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);						
	    //
	    // 2nd row
	    ch = new Chunk("[ ] Not known at address given",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    ch = new Chunk("[ ] No such address",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 3rd row
	    ch = new Chunk("[ ] Forwarding Order Expired",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    ch = new Chunk("[ ] Other (Specify):  ________________________________",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 4th row
	    ch = new Chunk("[ ] Box holder's Street Address",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);	
	    //
	    ch = new Chunk("[ ] New Address:",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 5th row
	    ch = new Chunk("   Street__________________________\n\n",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    table.addCell(cell);
	    //
	    // 6th row
	    ch = new Chunk("   Post Office______________________\n\n",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    table.addCell(cell);						
	    //
	    // 7th row
	    ch = new Chunk("   State_____Zip+4_________________\n\n",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    table.addCell(cell);
	    //
	    document.add(table);
	    // separator
	    document.add(sepTable);
	    //
	    table = new PdfPTable(widths);
	    table.setWidthPercentage(100);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    //
	    ch = new Chunk("Agency Return Address:\n\nCity of Bloomington Legal Dept.\nP O Box 100\nBloomington, IN  47402-0100\n",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    ch = new Chunk("POSTMARK",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    document.add(table);
	}
	catch(Exception ex){
	    logger.error(ex);
	}
    }

}






















































