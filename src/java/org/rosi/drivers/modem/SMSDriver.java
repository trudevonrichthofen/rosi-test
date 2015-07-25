package org.rosi.drivers.modem ;

import java.io.* ;
import gnu.io.* ;
import java.util.*;

public class SMSDriver extends ModemDriver {

  public SMSDriver( String portName ) throws Exception {

     super( portName ) ;

     _checkModem() ;

  }
  private void _checkModem() throws  IOException {
     /*
      * no echo.
      */
     sendSimpleCommand( "ATE0" ) ;
     /*
      * get modem vendor.
      */
     String modemType = checkModem() ;

     if( modemType.compareToIgnoreCase( "huawei" ) != 0 )
       throw new
       IOException("Unknown modem found : "+modemType) ;
  }
  private void _loginModem( String pin ) throws IOException {
     try{
        if( loginModem( pin )  ){
          /*
           * We really logged in now. Give the modem some time to adjust.
           */
          try{
            Thread.sleep(4000L) ;
          }catch(InterruptedException ie ){
            throw new
            IOException("Interrupted sleep after login");
          }
        }
     }catch(IOException ioe ){
        throw new
        IOException("Login failed ! ("+ioe.getMessage()+")");
     }
  }  
  public SMSDriver( String portName , String pin ) throws Exception {

     super( portName ) ;

     _checkModem();

     _loginModem( pin ) ;

  }
  public String [] listSMSs( String pin ) throws IOException {

     _checkModem() ;

     _loginModem( pin ) ;

     sendSimpleCommand( "AT+CMGF=1") ;

     List<String> res = sendSimpleCommand( "AT+CMGL=\"ALL\"") ;

     for( String s : res ){
       System.out.println("   >>> "+s);
     }
     return new String[0];


  }
  public void sendSMS( String pin ,  String phoneNumber , String message ) throws IOException {

     //System.out.println("SENDING to >"+phoneNumber+"< : "+message);
     //System.out.println("Checking Modem");
     _checkModem() ;

     //System.out.println("Logging in");
     _loginModem( pin ) ;

     sendSimpleCommand( "AT+CMGF=1") ;
     sendSimpleCommand( "AT+CMGS=\""+phoneNumber+"\"") ;
     sendSimpleMessage( message );
  }
  public static void main( String[] args ) {
   
    if( args.length < 1 ){
       System.err.println("Usage : ... <device> <pin> [<command>]");
       System.err.println("     <command> : ");
       System.err.println("         send <phoneNumber> <message>");
       System.err.println("         list");
       System.exit(4);
    } 

    try{

       String portName = args[0] ;

       SMSDriver modem = new SMSDriver( portName ) ;

       try {

           if( args.length < 2 ){
              String infoString = modem.getModemInfo() ;
              System.out.println("Modem : "+infoString);
           }else if( args[2].equals( "list" ) ){
              String pin = args[1] ;
              modem.listSMSs(pin);
           }else if( args[2].equals( "send2" ) ){
              if( args.length < 5 ){
                  System.err.println( "Usage : ... <device> <pin> send <phoneNumber> <message>");    
              }else{
                 String pin         = args[1] ;
                 String phoneNumber = args[3] ;
                 String message     = args[4] ;
                 for( int i = 0 ; i < 3 ; i++  ){
                    modem.sendSMS( pin , phoneNumber , message ) ;
                 }
              }
           }else if( args[2].equals( "send" ) ){
              if( args.length < 5 ){
                  System.err.println( "Usage : ... <device> <pin> send <phoneNumber> <message>");    
              }else{
                 String pin         = args[1] ;
                 String phoneNumber = args[3] ;
                 String message     = args[4] ;
                 modem.sendSMS( pin , phoneNumber , message ) ;
              }
           }

       }catch( Exception e ) {

           System.err.println("Problem in command : "+e.getMessage() ) ;
           e.printStackTrace();

       }finally{
           modem.close() ;
       }

    }catch(Exception oe ){
       System.err.println("Problem in command : "+oe.getMessage() ) ;
       oe.printStackTrace();
    }
  }
  
}
