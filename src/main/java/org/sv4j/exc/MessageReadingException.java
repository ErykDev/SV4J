package org.sv4j.exc;
/**
 * Exception for fail when reading message
 */
public class MessageReadingException extends Exception {
    public MessageReadingException(String s, Exception base){
        super(s,base);
    }
    public MessageReadingException(String s){
        super(s);
    }
}
