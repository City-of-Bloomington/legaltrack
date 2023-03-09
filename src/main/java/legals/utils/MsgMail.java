package legals.utils;
import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * msgmail creates a very simple text/plain message and sends it.
 * <p>
 * usage: <code>java msgmail <i>to from smtphost true|false</i></code>
 * where <i>to</i> and <i>from</i> are the destination and
 * origin email addresses, respectively, and <i>smtphost</i>
 * is the hostname of the machine that has the smtp server
 * running. The last parameter either turns on or turns off
 * debugging during sending.
 *
 * @author Sun microsystems
 */
public class MsgMail{

    static String msgText = "";
    static String to = "";
    static String from = "";
    static String host = "smtp.bloomington.in.gov"; 
    static boolean debug = true;
    static String subject = "Start Legal";
    static String cc = null;
    static final long serialVersionUID = 54L;
    Logger logger = LogManager.getLogger(MsgMail.class);
    /**
     * The main initializer.
     *
     * For test purpose.
     * @param args the message arguements
     */

    /**
     * The main constructor.
     *
     * @param to2 to email address
     * @param from2 from email address
     * @param msg2 the message
     * @param cc2 the cc email address
     * @param debug2 the debug flag true|false
     */
    public MsgMail(String to2,
		   String from2,
		   String subject2,
		   String msg2,
		   String cc2,
		   boolean debug2){

	to = to2;
	cc = cc2;
	from = from2;
	msgText = msg2;
	debug = debug2;
	if(subject2 != null) subject = subject2;
	doSend();
    }		
    public MsgMail(
		   String to2,
		   String from2,
		   String subject2,
		   String msg2,
		   String cc2){
	to = to2;
	cc = cc2;
	from = from2;
	msgText = msg2;
	if(subject2 != null) subject = subject2;
    }
    public String doSend(){
	String back = "";				
	if(to == null || to.equals("")){
	    back = "error email to not set ";
	    logger.error(back);
	    return back;
	}
	// create some properties and get the default Session
	Properties props = new Properties();
	props.put("mail.smtp.host", host);
	if (debug) props.put("mail.debug", "true");

	Session session = Session.getDefaultInstance(props, null);
	session.setDebug(debug);
	
	try {
	    // create a message
	    Message msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(from));
	    InternetAddress[] address = {new InternetAddress(to)};
	    msg.setRecipients(Message.RecipientType.TO, address);
	    if(cc != null){
		InternetAddress[] address2 = {new InternetAddress(cc)};
		msg.setRecipients(Message.RecipientType.CC, address2);
	    }
	    msg.setSubject(subject);
	    msg.setSentDate(new Date());
	    // If the desired charset is known, you can use
	    // setText(text, charset)
	    msg.setText(msgText);
	    Transport.send(msg);
	} catch (MessagingException mex){

	    logger.error("\n--Exception handling in msgmail.java "+mex);
	    Exception ex = mex;
	    do {
		if (ex instanceof SendFailedException) {
		    SendFailedException sfex = (SendFailedException)ex;
		    javax.mail.Address [] invalid = sfex.getInvalidAddresses();
		    if (invalid != null) {
			logger.error("    ** Invalid Addresses");
			if (invalid != null) {
			    for (int i = 0; i < invalid.length; i++) 
				logger.error("         " + invalid[i]);
			}
		    }
		    javax.mail.Address [] validUnsent = sfex.getValidUnsentAddresses();
		    if (validUnsent != null) {
			logger.error("    ** ValidUnsent Addresses");
			if (validUnsent != null) {
			    for (int i = 0; i < validUnsent.length; i++) 
				logger.error("         "+validUnsent[i]);
			}
		    }
		    javax.mail.Address [] validSent = sfex.getValidSentAddresses();
		    if (validSent != null) {
			logger.error("    ** ValidSent Addresses");
			if (validSent != null) {
			    for (int i = 0; i < validSent.length; i++) 
				logger.error("         "+validSent[i]);
			}
		    }
		}
		if (ex instanceof MessagingException)
		    ex = ((MessagingException)ex).getNextException();
		else { // any other exception
		    logger.error(ex);
		    ex = null;
		}
	    } while (ex != null);
	} catch (Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	return back;
    }

}
