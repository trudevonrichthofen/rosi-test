package org.rosi.drivers.nut ;

import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.util.*;

public class NutUpsDriver {

   private static      int UPSD_PORT =  3493 ;
   private PrintWriter    _writer   = null ;
   private BufferedReader _reader   = null ;
   private int            _port     = 0 ;
   private String         _hostname = null ;
   private Socket         _socket   = null ;

   public static String  _patternString = "VAR[ ]*([a-zA-Z0-9]*)[ ]*([a-zA-Z\\.0-9]*)[ ]*\"(.*)\"" ;
   public static Pattern _pattern = Pattern.compile( _patternString ) ;

   public NutUpsDriver( String hostname , int port ) throws IOException {
        _hostname = hostname ;
        _port     = port ;
   }
   private void connect() throws IOException {

       _socket = new Socket( _hostname , _port ) ;
       //System.out.println("Connected to : "+_hostname);
       try{
          _writer = new PrintWriter(  new OutputStreamWriter( _socket.getOutputStream() ) ) ;
       }catch(Exception e){
          try{ _socket.close() ; _socket = null ; }catch(Exception eee ){}
       }
       //System.out.println("Writer opened");
       try{
          _reader = new BufferedReader(  new InputStreamReader( _socket.getInputStream() ) ) ;
       }catch(Exception e){
          try{ _writer.close() ; _writer = null ; }catch(Exception eee ){}
          try{ _socket.close() ; _socket = null ; }catch(Exception eee ){}
       }
       //System.out.println("Reader opened");
   }
   public Map<String,String> getVariableList( String device ) throws IOException {

     String requestString = "list var "+device;

     connect();

     _writer.println( requestString ) ;
     _writer.flush();

     //     System.out.println("Request sent : "+requestString);

     int status = checkReplyOk( _reader.readLine() ) ;
     if( status != 0 )
       throw new
       IOException("Reply sequence confused : expected '0' got : "+status);

     Map<String,String> map = new HashMap<String,String>() ;
     while( true  ){

        String s = _reader.readLine() ;

        if( checkReplyOk( s ) != 1 )break ; 

        Matcher m = _pattern.matcher( s );

        if( ! m.matches() )
           throw new
           IOException("Unexpected syntax in reply from 'upsd' : >"+s+"<");

        //for( int i = 1 ; i <= m.groupCount() ; i++ ){
        //    System.out.print(" "+m.group(i));
        //}
        //System.out.println("");
        map.put( m.group(2) , m.group(3) ) ;

     }
     close();
     return map;
   }

   private int checkReplyOk( String message ) throws IOException {
     //System.out.println("Got message : "+message);
     if( message == null ) 
       throw new
       IOException("Premature end of information");

     String [] tokens = message.split(" ") ;

     if( tokens.length == 0 )
       throw new
       IOException("Unexpected empty line from 'upsd'");
       
     if( tokens[0].compareToIgnoreCase("err") == 0 )
       throw new
       IOException("Error reported from 'upsd' : "+tokens[0]);  
    
     if( tokens.length < 4 )
       throw new
       IOException("List didn't start with proper message");
 
     if( tokens[0].compareToIgnoreCase("begin") == 0 )return 0; 
     if( tokens[0].compareToIgnoreCase("end") == 0 )return 2; 
     if( tokens[0].compareToIgnoreCase("var") == 0 )return 1; 

     throw new
     IOException("Unexpected keyword in reply from upsd : "+tokens[0]);
   }
   public void close() throws IOException {

      _writer.println( "logout" ) ;
      _writer.flush();

      while( _reader.readLine() != null );
      try{ _writer.close() ; _writer = null ; }catch(Exception eee ){}
      try{ _reader.close() ; _reader = null ; }catch(Exception eee ){}
      try{ _socket.close() ; _socket = null ; }catch(Exception eee ){}
   }
   public static void main( String [] args ) throws Exception {
/*
        Matcher m = _pattern.matcher(args[0]);
        boolean b = m.matches();
        System.out.println("result : "+b+" count "+m.groupCount());

        for( int i = 1 ; i <= m.groupCount() ; i++ ){
            System.out.println("  "+i+" "+m.group(i));
        }
*/
      NutUpsDriver ups = new NutUpsDriver( "homecontrol2" ,  UPSD_PORT ) ;
      System.out.println("----");
      Map<String,String> map = ups.getVariableList("aeg") ;
      System.out.println("Result ; "+map);
      while( true ){
         System.out.println("----");
         map = ups.getVariableList("aeg") ;
         for( Map.Entry e : map.entrySet() ){
           System.out.println("  "+e.getKey()+ " -> "+e.getValue() ) ;
         }
         Thread.sleep(5000L);
      }
      //ups.close() ;
   }
}
