
package legals.web;

import java.util.*;
import java.io.*;
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
import legals.list.*;
import legals.utils.*;
/**
 *
 */
@WebServlet(urlPatterns = {"/ReleasePdf"})
public class ReleasePdf extends TopServlet {

    static final long serialVersionUID = 70L;
    static Logger logger = LogManager.getLogger(ReleasePdf.class);
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
	out.println("<center><h2>Release Of Judgment</h2>");
	out.println("<form name=\"myForm\" method=\"post\">");				
	out.println("<table width=\"60%\" border=\"1\">");
	out.println("<tr><td align=\"center\" style=\"background-color:navy; color:white\">"+
		    "<b> Mailers </b>"+
		    "</td></tr>");
	out.println("<tr><td bgcolor="+Helper.bgcolor+">");
	out.println("<table width=\"100%\">");

	//
	// Release
	out.println("<tr><td align=\"left\">");
	out.println("<table>");
	out.println("<tr><td align=\"left\">Letter Date:</td><td align=\"left\"> "+				
		    "<input name=\"letter_date\" value=\""+today+
		    "\" size=\"10\" maxlength=\"10\" class=\"date\" />");
	out.println("</td></tr>");
				
	out.println("<tr><td></td><td align=\"left\">From</td><td align=\"left\">To</td></tr>");
	out.println("<tr><td align=\"left\">Closed Date: </td><td>");
	out.println("<input name=\"date_from\" value=\""+start_week+
		    "\" size=\"10\" maxlength=\"10\" class=\"date\" /></td><td>");
	out.println("<input name=\"date_to\" value=\""+today+
		    "\" size=\"10\" maxlength=\"10\" class=\"date\"></td></tr>");
	out.println("</table></td></tr>");
	//
	//
	out.println("</table></td></tr>");
	out.println("<tr><td align=\"right\"><input type=\"submit\" " +
		    "name=\"action\" "+
		    "value=\"Submit\" /></td></tr>");
	//
	out.println("</table>");
	out.println("</form>");
	out.println("<br>");
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
	    pos="",letter_date="";
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
	    cll.setForRelease();
	    cll.setDate_from(date_from);
	    cll.setDate_to(date_to);
	    String back = cll.find();
	    if(back.equals("")){
		cases = cll.getCases();
	    }
	    String query ="",str="",str2="",address="",
		id="", cause_num="", judg_date="",
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
		    // left right top bottom
		    Document document = new Document(pageSize, 54, 54, 54, 18);// 18,18,54,35
		    ServletOutputStream out = null;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    PdfWriter writer = PdfWriter.getInstance(document, baos);
		    document.open();										
		    //
		    // check if this is a one defendant case
		    // or multiple, in the old cases the defendants
		    // were listed together using & symbol
		    // we had to use both & and "and" to figure 
		    // out  
		    //
		    for(Case one:cases){
			address="";
			cause_num =""; cause_num2=""; judg_date="";
			attorneyFullName=""; attorneyBar="";
			attorneyTitle="";	full_names=""; care_of="";
			id = one.getId();
			judg_date = one.getJudgment_date();
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
				str = one2.getCauseNumForCase(id);
				if(!str.equals("")){
				    if(!cause_num.equals("")) cause_num +=", ";
				    cause_num += str;
				}
				DefAddress addr = one2.getValidAddress();
				address = "";
				if(addr != null){
				    str = addr.getAddress();
				    if(!str.equals("")){
					address += str;
				    }
				    str = addr.getCityStateZip();
				    if(!str.equals("")){
					if(!address.equals("")) address += " ";
					address += str;
				    }																				
				}
			    }
			}
			if(!care_of.equals("")){
			    address = "c/o "+care_of+" "+address;
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
			writeLetter(document,
				    letter_date,
				    judg_date,
				    cause_num,
				    cause_num2,
				    multiple,
				    address,
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
    void writeLetter(Document document,
		     String letter_date,
		     String judg_date,
		     String cause_num,
		     String cause_num2,
		     boolean multiple,
		     String address,
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

	    Font fnt = new Font(Font.TIMES_ROMAN, 12, 
				Font.NORMAL);
	    Font fntb = new Font(Font.TIMES_ROMAN, 12, 
				 Font.BOLD);
	    Font fnt2 = new Font(Font.TIMES_ROMAN, 12, 
				 Font.NORMAL);
	    Font fntbu = new Font(Font.TIMES_ROMAN, 12, 
				  Font.BOLD | Font.UNDERLINE);						
						
	    float[] widths = {45f, 10f, 45f}; // percentages
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
	    ch = new Chunk("STATE OF INDIANA ",fnt);
	    phrase.add(ch);
	    PdfPCell cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);

	    phrase = new Phrase();
	    ch = new Chunk(") ",fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);

	    phrase = new Phrase();
	    ch = new Chunk("IN THE MONROE CIRCUIT COURT",fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);						
	    //
	    // 2nd row
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);
	    phrase = new Phrase();
	    ch = new Chunk(")SS:",fnt);
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);							
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);	
	    // third row
	    ch = new Chunk("COUNTY OF MONROE",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    // 
	    headTable.addCell(cell);
	    ch = new Chunk(")",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);
	    //
	    phrase = new Phrase();						
	    ch = new Chunk(" CAUSE NO. ",fnt);
	    phrase.add(ch);
	    ch = new Chunk(cause_num,fnt);
	    phrase.add(ch);						
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    headTable.addCell(cell);		
	    document.add(headTable);
	    //
	    document.add(spacePhrase);
	    //
	    float[] widths2 = {50f, 20f, 30f}; // percentages
	    PdfPTable table = new PdfPTable(widths2);
	    table.setWidthPercentage(100);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

	    // first row
	    ch = new Chunk("CITY OF BLOOMINGTON,",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);								
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    ch = new Chunk(")",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);								
	    //
	    // 2nd row
	    /*
	      cell = new PdfPCell(spacePhrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	      cell = new PdfPCell(spacePhrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	      ch = new Chunk(")",fnt);
	      phrase = new Phrase();
	      phrase.add(ch);
	      cell = new PdfPCell(phrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	    */
	    //
	    // 3rd row
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    ch = new Chunk("Plaintiff,",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    ch = new Chunk(")",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);						
	    //
	    //4th row
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    ch = new Chunk(")",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 5th row
	    ch = new Chunk("vs.",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase); // align righ
	    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    ch = new Chunk(")",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);						
	    //
	    // 6th row
	    /*
	      cell = new PdfPCell(spacePhrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	      cell = new PdfPCell(spacePhrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	      ch = new Chunk(")",fnt);
	      phrase = new Phrase();
	      phrase.add(ch);
	      cell = new PdfPCell(phrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	    */
	    //
	    // 7th row
	    ch = new Chunk(full_names, fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    ch = new Chunk(")",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    // 8th row
	    /*
	      cell = new PdfPCell(spacePhrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	      cell = new PdfPCell(spacePhrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	      ch = new Chunk(")",fnt);
	      phrase = new Phrase();
	      phrase.add(ch);
	      cell = new PdfPCell(phrase);
	      cell.setBorder(Rectangle.NO_BORDER);
	      table.addCell(cell);
	    */
	    //
	    // 9th row
	    cell = new PdfPCell(spacePhrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    if(multiple)
		ch = new Chunk("Defendants",fnt);
	    else
		ch = new Chunk("Defendant",fnt);								
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);						
	    ch = new Chunk(")",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    document.add(table);
	    //
	    // middle title
	    //
	    Paragraph pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(pp_indent);
	    pp.setAlignment(Element.ALIGN_CENTER);
						
	    ch = new Chunk("RELEASE OF JUDGMENT",fntbu);
	    phrase = new Phrase();
	    phrase.add(Chunk.NEWLINE);									
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);
	    //
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(pp_indent);
	    pp.setAlignment(Element.ALIGN_LEFT);
	    //
	    // space between lines is called leading
	    // twice the font (fixed, multiple) or (24,0) for 12 pt font
	    //
	    pp.setLeading(0,2); 						
	    ch = new Chunk("Plaintiff City of Bloomington, by and through counsel, for its release of judgment in the above-entitled cause would state the following:",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);
	    //
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(pp_indent);
	    pp.setAlignment(Element.ALIGN_LEFT);
	    pp.setLeading(0,2); 									
	    String defendant = "Defendant";
	    String pos = "has";
	    if(multiple){
		defendant = "Defendants";
		pos = "have";
	    }
	    ch = new Chunk("That "+defendant+" "+full_names+" "+pos+" satisfied the judgment entered by this Court on "+judg_date+", and that said judgment is hereby released and forever discharged by Plaintiff through its counsel.",fnt);
						
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);						
	    //
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(pp_indent);
	    pp.setAlignment(Element.ALIGN_LEFT);
	    pp.setLeading(0,2); 								
	    ch = new Chunk("WHEREFORE, Plaintiff City of Bloomington would hereby release this judgment on this "+day+" day of "+month+", "+year+". ",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);
	    document.add(spacePhrase);
	    //
	    float[] widths3 = {50f, 50f}; 
	    table = new PdfPTable(widths3);
	    table.setWidthPercentage(100);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    //
	    // first row
	    PdfPCell emptyCell = new PdfPCell(spacePhrase);
	    emptyCell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(emptyCell);
						
	    ch = new Chunk(attorneyFullName+" Atty. No. "+attorneyBar, fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.TOP);
	    table.addCell(cell);

	    // 2nd row
	    table.addCell(emptyCell);
	    ch = new Chunk(attorneyTitle, fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    // 3rd row
	    table.addCell(emptyCell);
	    ch = new Chunk("City of Bloomington", fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    // 4th row
	    table.addCell(emptyCell);
	    ch = new Chunk("401 N. Morton Street/P. O. Box 100", fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    // 5th row 
	    table.addCell(emptyCell);
	    ch = new Chunk("Bloomington, IN  47402", fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);						
	    // 6th row
	    table.addCell(emptyCell);
	    ch = new Chunk("(812) 349-3426", fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);	
	    document.add(table);
	    // 
	    ch = new Chunk("TO THE CLERK", fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);
	    document.add(phrase);
	    //
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(pp_indent);
	    pp.setAlignment(Element.ALIGN_LEFT);
	    ch = new Chunk("Please release this judgment entered in the judgment record books of the Monroe Circuit Court.",fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);
	    document.add(spacePhrase);
	    //
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(pp_indent);
	    pp.setAlignment(Element.ALIGN_CENTER);
	    ch = new Chunk("CERTIFICATE OF SERVICE" ,fntbu);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);								
	    //
	    pp = new Paragraph();
	    pp.setIndentationLeft(20);
	    pp.setFirstLineIndent(pp_indent);
	    pp.setAlignment(Element.ALIGN_LEFT);
	    pp.setLeading(0,1);
	    ch = new Chunk("I hereby certify that a true and accurate copy of the foregoing RELEASE OF JUDGMENT was mailed to "+defendant+" "+full_names+", "+address+", this ______ day of _______, "+Helper.getCurrentYear(), fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    phrase.add(Chunk.NEWLINE);
	    phrase.add(Chunk.NEWLINE);
	    phrase.add(Chunk.NEWLINE);						
	    pp.add(phrase);
	    document.add(pp);
	    //
	    table = new PdfPTable(widths3);
	    table.setWidthPercentage(100);
	    table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
	    //
	    table.addCell(emptyCell);
	    ch = new Chunk(attorneyFullName, fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.TOP);
	    table.addCell(cell);
	    //
	    table.addCell(emptyCell);
	    ch = new Chunk(attorneyTitle, fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    //
	    table.addCell(emptyCell);
	    ch = new Chunk("City of Bloomington", fnt);
	    phrase = new Phrase();
	    phrase.add(ch);
	    cell = new PdfPCell(phrase);
	    cell.setBorder(Rectangle.NO_BORDER);
	    table.addCell(cell);
	    document.add(table);
	}
	catch(Exception ex){
	    logger.error(ex);
	}
    }

}






















































