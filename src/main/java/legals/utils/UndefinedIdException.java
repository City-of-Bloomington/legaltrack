package legals.utils;
/**
 *
 */

public class UndefinedIdException extends Exception{

    static final long serialVersionUID = 75L;
    UndefinedIdException(String msg){
	super(msg);
    }

    UndefinedIdException(String msg, Throwable t){
	super(msg,t);
    }

}
