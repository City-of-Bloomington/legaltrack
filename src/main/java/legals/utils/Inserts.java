package legals.utils;
import java.sql.*;
import java.util.Map;
import java.util.Hashtable;
import legals.model.*;
/**
 *
 */

public class Inserts{

    boolean debug;
    static final long serialVersionUID = 42L;
    //
    // Person title
    public final static String nameTitleArr[] ={"","Mr.","Mrs.","Ms."}; 
    // torts, work_comp, safety
    public final static String statusArr[] ={"New","Pending","Closed","Filed Suit"}; 
    public final static String searchStatusArr[] ={"","New","Pending","Closed","Filed Suit"};
    public final static String identArr[] = {"","He","She","They","It"};
    public final static Map<String, String> posMap = new Hashtable<>();
    static {
	posMap.put("He","his");
	posMap.put("She","her");
	posMap.put("They","their");
	posMap.put("It","its");
    }
    //
    // xhtmlHeaderInc
    public final static String xhtmlHeaderInc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"+
	"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
    //
    // basic constructor
    public Inserts(boolean deb){
	//
	// initialize
	//
	debug = deb;
    }
    public final static String getPosPron(final String ident){
	if(posMap.containsKey(ident)){
	    return posMap.get(ident);
	}
	return "";
    }
    //
    // main page banner
    //
    public final static String banner(String url){

	String banner = "<head>\n"+
	    "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />\n"+
	    "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n"+
	    "<link rel=\"SHORTCUT ICON\" href=\""+url+"images/favicon.ico\" />\n"+
	    "<style type=\"text/css\" media=\"screen\">\n"+
	    "/*<![CDATA[*/"+			
	    "           @import url(\""+url+"css/skins.css\");\n"+
	    "/*]]>*/\n"+		
	    "</style>\n"+
	    "<style type=\"text/css\" media=\"print\">\n"+
	    "/*<![CDATA[*/"+			
	    "body { color:black; background-color:white; margin:0px; font-family:sans-serif; font-size:11pt; }\n"+
	    "/*]]>*/\n"+			
	    "</style>\n"+
	    "<link rel=\"stylesheet\" href=\""+url+"css/jquery-ui.min-1.13.2.css\" type=\"text/css\" media=\"all\" />\n"+
	    "<link rel=\"stylesheet\" href=\""+url+"css/jquery-ui.theme.min-1.13.2.css\" type=\"text/css\" media=\"all\" />\n"+
	    "<style type=\"text/css\">"+  // user defined
	    "/*<![CDATA[*/"+
	    ".ui-datepicker-prev .ui-icon, .ui-datepicker-next .ui-icon {\n "+
	    " background-image: url(\"js/images/ui-icons_228ef1_256x240.png\");\n"+
	    "}\n "+
	    "table.wide {width:100%;border:none}"+
	    "table.widesolid {width:100%;border-style:solid}"+
	    "table.box {width:100%;border:2px solid}"+
	    "table.box th {border:2px solid; text-align:center; vertical-align:bottom; } "+
	    "table.box td {border:2px solid} "+
	    "table.control {width:100%} "+
	    "table.control td {text-align:center; padding:.5em} "+
	    "tr.solid {border-style:solid}\n"+
	    "td.right {text-align:right}\n"+
	    "td.left {text-align:left}\n"+
	    "td.center {text-align:center}\n"+
	    "th.small{width:15%}\n"+
	    "th.right {text-align:right}\n"+
	    "td.title{background-color:navy;color:white;font-weight:bold;}\n"+
	    "p.warning { color:red}\n"+
	    "p.center {text-align:center}\n"+
	    "div.center {text-align:center}\n"+
	    "div.left {text-align:left}\n"+	
	    "div.green { font-size:80%; color:green}\n"+
	    "span.center {text-align:center}\n"+
	    "span.green { font-size:80%; color:green}\n"+
	    "/*]]>*/\n"+
	    "</style>\n"+
	    //
	    // Java Script common for all pages
	    //
	    " <script type=\"text/javascript\">                 \n"+
	    "//<![CDATA[                                        \n"+
	    "  function checkDate(dt){                          \n"+     
	    "    var dd = dt.value;                             \n"+
	    "   if(!dd || dd.length == 0) return true;          \n"+
	    "   var dar = dd.split(\"/\");                      \n"+
	    " if(dar.length < 3){                               \n"+
	    " alert(\"Not a valid date: \"+dd);                 \n"+
	    "      dt.select();                                 \n"+
	    "      dt.focus();                                  \n"+
	    "      return false;}                               \n"+
	    "   var m = dar[0];                                 \n"+
	    "   var d = dar[1];                                 \n"+
	    "   var y = dar[2];                                 \n"+
	    "   if(isNaN(m) || isNaN(d) || isNaN(y)){           \n"+
	    "      alert(\"Not a valid date: \"+dd);                 \n"+
	    "      dt.select();                                 \n"+
	    "      dt.focus();                                  \n"+
	    "      return false; }                              \n"+
	    "  if(!validateDate(m,d,y)) {                        \n"+
	    "      alert(\"Not a valid date: \"+dd);                 \n"+
	    "      dt.select();                                 \n"+
	    "      dt.focus();                                  \n"+
	    "      return false;                                \n"+
	    "      }                                            \n"+
	    "    return true;                                   \n"+
	    " }                                              \n"+
	    " function isFutureDate(dt){     \n"+
	    "   var dd = dt.value;                             \n"+
	    "   if(!dd || dd.length == 0) return false;         \n"+
	    "   var dar = dd.split(\"/\");                      \n"+
	    "   if(dar.length < 3){                               \n"+
	    "      alert(\"Not a valid date: \"+dd);                 \n"+
	    "      return false;                               \n"+
	    "   }                                  \n"+			
	    "   var m = dar[0];                                 \n"+
	    "   var d = dar[1];                                 \n"+
	    "   var y = dar[2];                                 \n"+
	    "   var date = new Date(y,(m-1),d);    \n"+ // month start with 0
	    "   var today = new Date();           \n"+
	    "   var diff = (date.getTime()) - (today.getTime());  \n"+// trans to digits
	    "   if(diff < 0){                 \n"+
	    "      return false;                               \n"+
	    "   }                                  \n"+			
	    "   return true;                      \n"+
	    " }                                  \n"+
	    " function validateDate(month,day,year){     \n"+
	    "   if(!isNaN(month)){                         \n"+
	    "      month = month - 1;                         \n"+
	    "   }                                          \n"+
	    //
	    "   var date = new Date(year,month,day);       \n"+
	    "   if(year != date.getFullYear()){             \n"+
	    "                                           \n"+
	    "    return false;                              \n"+
	    "  }                                            \n"+
	    "  if(month != date.getMonth()){                 \n"+
	    "    return false;                              \n"+
	    "  }                                            \n"+
	    "  if(day != date.getDate()){                     \n"+
	    "    return false;                               \n"+
	    "  }                                            \n"+
	    "  return true;                                 \n"+
	    " }                                             \n"+
	    "   function indexOf(xx,s){               \n"+
	    "       var  l = xx.length;               \n"+
	    "       var o = -1;                       \n"+
	    "     for(var i=0; i<l; i++){             \n"+
	    "        var c = xx.charAt(i);            \n"+
	    "       if(c == s) o = i;                 \n"+
            "      }                                  \n"+
	    "    return o;                            \n"+
	    "   }                                     \n"+
	    "  function checkNumber(dt){                        \n"+     
	    "    var dd = dt.value;                             \n"+
	    "   if(dd.length == 0) return true;                \n"+
	    "     if(isNaN(dd)){                                \n"+
	    "      alert(\"Not a valid Number: \"+dd);          \n"+
	    "      dt.select();                                 \n"+
	    "      dt.focus();                                  \n"+
	    "      return false;                                \n"+
	    "        }                                          \n"+
	    "    return true;                                   \n"+
	    "   }                                               \n"+
	    "  function validateDelete(){	                    \n"+
	    "   var x = false;                                  \n"+
	    " x = confirm(\"Are you sure you want to delete this record\");\n"+
	    "   return x;                                       \n"+
	    "	}	             		              \n"+
	    "  function refreshPage(){	              \n"+
	    "    document.forms[0].submit();            \n"+
	    "  }                                      \n"+
	    "  function addColon(time,e){	          \n"+
	    " keyCode = (window.Event) ? e.which: e.keyCode;  \n"+
	    "  if(keyCode > 47 && keyCode < 58){              \n"+
	    "  if(time.value.length == 2) {                   \n"+
	    "  if(time.value.indexOf(\":\") == -1){           \n"+
	    "   time.value = time.value+\":\";        \n"+
	    "  }}}}                                   \n"+
	    "  function checkTime(time,e){	          \n"+
	    "   keyCode = (window.Event) ? e.which: e.keyCode;     \n"+
	    "   if(keyCode > 47 && keyCode < 58){                 \n"+
	    "     var len = time.value.length;                      \n"+
	    "     if(len < 5) {                                     \n"+
	    "       if(time.value.indexOf(\":\") == 1){              \n"+
	    "         time.value = \"0\"+time.value;                   \n"+
	    "      }                                                 \n"+
	    "      if(time.value.indexOf(\":\") == 2){              \n"+
	    "        if(len == 3) {                                    \n"+
	    "         time.value = time.value+\"00\";                  \n"+
	    "        }else {                                          \n"+
	    "         time.value = time.value+\"0\";                   \n"+
	    "  }}}}}                                             \n"+
	    " //]]>                                    \n"+			
	    " </script>				                  \n"+
	    "<title>LegalTrack - City of Bloomington, Indiana</title>\n"+
	    "</head>\n"+
	    "<body>\n"+
	    "<div id=\"banner\">\n"+
	    "  <div id=\"application_name\">LegalTrack</div>\n"+
	    "  <div id=\"location_name\">City Of Bloomington, IN</div>\n"+
	    "  <div id=\"navigation\"></div>\n"+
	    "</div>";
	return banner;
    }
    public final static String jsStrings(String url){
	String str = "";
	str += "<script type=\"text/javascript\" src=\""+url+"js/jquery-3.6.1.min.js\"></script>\n";
	str += "<script type=\"text/javascript\" src=\""+url+"js/jquery-ui.min-1.13.2.js\"></script>";	    
	return str;
    }
	

    //
    // main page banner
    //
    public final static String banner2(String url, String bodyStr, String js){

	String banner = "<head>\n"+
	    "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />\n"+
	    "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n"+
	    "<link rel=\"SHORTCUT ICON\" href=\""+url+"images/favicon.ico\" />\n"+
	    "<style type=\"text/css\" media=\"screen\">\n"+
	    "           @import url(\""+url+"css/skins.css\");\n"+
	    "</style>\n"+
	    "<style type=\"text/css\" media=\"print\">\n"+
	    "      @import url(\"https://apps.bloomington.in.gov/skins/isotope/print.css\");\n"+
	    "body { color:black; background-color:white; margin:0px; font-family:sans-serif; font-size:11pt; }\n"+
	    "</style>\n"+
	    "<link rel=\"stylesheet\" href=\""+url+"css/jquery-ui.min-1.13.2.css\" type=\"text/css\" media=\"all\" />\n"+
	    "<link rel=\"stylesheet\" href=\""+url+"css/jquery-ui.theme.min-1.13.2.css\" type=\"text/css\" media=\"all\" />\n"+
	    "<style type=\"text/css\">"+  // user defined
	    "/*<![CDATA[*/"+
	    ".ui-datepicker-prev .ui-icon, .ui-datepicker-next .ui-icon {\n "+
	    " background-image: url(\"js/images/ui-icons_228ef1_256x240.png\");\n"+
	    "}\n "+
	    "table.wide {width:100%;border:none}"+
	    "table.widesolid {width:100%;border-style:solid}"+
	    "table.box {width:100%;border:2px solid}"+
	    "table.box th {border:2px solid; text-align:center; vertical-align:bottom; } "+
	    "table.box td {border:2px solid} "+
	    "table.control {width:100%} "+
	    "table.control td {text-align:center; padding:.5em} "+
	    "tr.solid {border-style:solid}\n"+
	    "td.right {text-align:right}\n"+
	    "</style>\n";
	if(!js.equals("")){
	    banner += " <script type=\"text/javascript\">\n"+
		js+
		" </script> \n";
	}
	//
	banner += "<title>LegalTrack - City of Bloomington, Indiana</title>\n"+
	    "</head>\n";		
	banner += "<body "+bodyStr+">\n";

	banner += "<div id=\"banner\">\n"+
	    "  <div id=\"application_name\">LegalTrack</div>\n"+
	    "  <div id=\"location_name\">City Of Bloomington, IN</div>\n"+
	    "  <div id=\"navigation\"></div>\n"+
	    "</div>";
	return banner;
    }
	
    //
    public final static String menuBar(String url, boolean logged){
	String menu = "<div class='menuBar'>\n<ul>";
	if(logged){
	    menu += "<li><a href=\""+url+"/Logout\">Logout</a></li>\n";
	}
	menu += "</ul></div>\n";
	return menu;
    }
    //
    // sidebar
    //
    public final static String sideBar(String url, User user){

	String ret = "<div id=\"leftSidebar\" class=\"left sidebar\">"+
	    "<h3 class=\"titleBar\">Options Menu</h3>\n"+
	    "<ul>\n";
	if(user.canEdit()){
	    ret += "<li><a href=\""+url+"CaseServ\">New Case</a></li>\n"+
		"<li><a href=\""+url+"Defendant?\">New Defendant</a></li>\n";
	}
	ret += "<li><a href=\""+url+"Search?\">Search</a></li>\n"+
	    "<li><a href=\""+url+"SearchDef?\">Search Defendants</a></li>\n";
	if(user.canEdit() && (user.isInDept("Legal") || user.isAdmin())){
	    ret += "<li><a href=\""+url+"ViolCats?\">Violation Categories</a></li>\n";				
	    ret += "<li><a href=\""+url+"/Attorney\">Attorneys</a></li>\n"+
		"<li><a href=\""+url+"LetterText\">Violation Letters</a></li>\n"+
		"<li><a href=\""+url+"Reporter\">Reports</a></li>\n"+
		"<li><a href=\""+url+"SendOut?\">Send Outs</a></li>\n"+
		"<li><a href=\""+url+"OJReport\">O.J. Reports</a></li>\n"+
		"<li><a href=\""+url+"CollectReport?\">Collection Reports</a></li>\n";
	}
	if(user.canEdit()){ 		
	    ret += "<li><a href=\""+url+"Starter?type=action\">Switch to Actions Menu</a></li>\n";
	}
	ret += "</ul></div>";
	return ret;

    }
    //
    // for start legal menu
    //
    public final static String sideBar2(String url){

	String ret = "<div id=\"leftSidebar\" class=\"left sidebar\">"+
	    "<h3 class=\"titleBar\">Legal Action</h3>\n"+
	    "<ul>\n"+
	    "<li><a href=\""+url+"LegalListServ?status=New\">New Actions</a></li>\n"+
	    "<li><a href=\""+url+"LegalNActionsServ?status=Pending\">Ongoing Actions</a></li>\n"+
	    "<li><a href=\""+url+"LegalNActionsServ?attention=Legal&amp;status=both\">Legal Attention</a></li>\n"+
	    "<li><a href=\""+url+"LegalNActionsServ?attention=HAND&amp;status=Pending\">HAND Attention</a></li>\n"+
	    "<li><a href=\""+url+"SearchLegalServ\">Search Actions</a></li>\n"+
	    "<li><a href=\""+url+"Starter?\">Switch to Legal Menu</a></li>\n"+
	    "</ul></div>";
	return ret;

    }
    //


    public final static String[] closedArr = {
	"",
	"Paid",
	"Complied",
	"Dismissed",
	"Outstanding judgment",
	// "Outstanding judgment recalled from C.R.",
	"Outstanding Judgment or 41(e) recalled from C.R."
    };
    //

    public final static String sudKeys[] = {"",
	"APT",
	"BSMT",
	"BLDG",
	"DEPT", 	
	"FL", 
	"FRNT",
	"HNGR",
	"KEY",
	"LBBY",  
	"LOT",
	"LOWR",
	"OFC",
	"PH",
	"PIER",
	"REAR",
	"RM",
	"SIDE",
	"SLIP",
	"SPC",
	"STOP",
	"STE",
	"TRLR",
	"UNIT",
	"UPPR"
    };
    
    public final static String sudInfo[] = { "",
	"APARTMENT",
	"BASEMENT",
	"BUILDING",
	"DEPARTMENT",
	"FLOOR",
	"FRONT",
	"HANGER",
	"KEY",
	"LOBBY",
	"LOT",
	"LOWER",
	"OFFICE",
	"PENTHOUSE",
	"PIER",
	"REAR", 
	"ROOM",
	"SIDE",
	"SLIP",
	"SPACE",
	"STOP",
	"SUITE",
	"TRAILER",
	"UNIT",
	"UPPER",
    };
    //
    public final static String streetKeys[] = {"",
	"AVE", "BND", "BLVD", "CIR", "CT", 
	"CTR", "DR",  "EXPY", "LN", "PIKE",
	"PKY", "PL",  "RD",    "ST", "TER", 
	"TPKE", "TURN"};
    //
    public final static String streetInfo[] = {"",
	"Avenue","Bend", "Boulevard", "Circle", "Court",
	"Center", "Drive", "Expressway", "Lane", "Pike" ,
	"Parkway" ,"Place" ,"Road" , "Street", "Terrace",
	"Turnpike","Turn"};    
    //
    public final static String dirArr[] = {"","N","S","E","W"};


}






















































