package org.rosi.execution.modules ;

import java.util.* ;
import java.io.*;
import java.net.*;
import java.util.concurrent.* ;

import org.rosi.util.*;
import org.rosi.execution.*;
import org.rosi.drivers.fritzbox.*;

public class FritzboxActorModule extends RosiModule {

   private RosiCommandProcessor  _commandProcessor = null ; 
   private ModuleContext         _context = null ;
   private RosiRuntimeExecution  _process = null ;
   private PatternTranslator     _filter  = null ;

   private String _loginURI      = "/login_sid.lua" ;
   private String _serviceURI    = "/webservices/homeautoswitch.lua" ;

   private FritzboxDriver    _fritzbox = null ;

   private String _actionLogFile  = "/var/log/rosi/rosiCommands.log" ;

   public FritzboxActorModule( String moduleName , ModuleContext context  )
      throws Exception
   {
      super(moduleName,context);
      log("Initiating.");
      _context = context ;

      _initializeFritzbox() ;

      String filterFile = context.get( "filterFile" ) ;
      if( filterFile != null ){
         File f = new File( filterFile ) ;
         if( ! f.exists() )
           throw new
           IllegalArgumentException("Filer file not found : "+ filterFile ) ; 
 
         _filter = new PatternTranslator( f ) ;
      }

   } 
   private void _initializeFritzbox() throws Exception {

      String userString  = _context.get("user"     , true ) ; 
      String passString  = _context.get("password" , true ) ; 
      String urlString   = _context.get("URL"      , true ) ; 

      String dryrun = _context.get("dryrun" ) ;
      if( ( dryrun != null ) && ( dryrun.equals("yes") )){
         _fritzbox = null ;
         return ;
      }

      _fritzbox = new FritzboxDriver( urlString , _loginURI , _serviceURI ) ;
      _fritzbox.setCredentials( userString , passString ) ;

      try{
          _fritzbox.authenticate() ;
      }catch(FileNotFoundException fnf ){
         errorLog("Server Error. URL not found at server : "+fnf.getMessage() ) ;
      }catch(HttpRetryException httpe ){
         int rc = httpe.responseCode() ;
         if( rc == 403 )errorLog("Authentication Failed" ) ;
         else errorLog("Login Failed : "+httpe.getMessage() ) ;
         throw httpe ;
      }catch(Exception ee ){
         errorLog("Contacting fritzbox failed due to : "+ee ) ;
         throw ee ;
      }

      new Thread( new KeepFritzboxAlive() ).start() ; 

   } 
   public RosiSetterCommand createFritzboxCommand( RosiSetterCommand command )
       throws Exception
   {
 
      command = new RosiSetterCommand( command ) ;

      if( _filter != null ){

          String [] sub = _filter.translate( command.getKey() ) ;

          if( ( sub != null ) && ( sub.length >=1 ) )command.setKey( sub[0] ) ; 
          else return null ;
      }
      return command ;

   }
   private class KeepFritzboxAlive implements Runnable {

      public void run(){

         log("KeepFritzboxAlive started");
         try{
           
            while(true){

               Thread.sleep( 2L * 60L * 1000L ) ;
               log("Sending keep alive to fritzbox"); 
               _fritzbox.getDeviceList() ;
               
            }
         }catch(Exception ee ){
            errorLog("Exception in keep alive thread: "+ee);
            errorLog("KeepFritzboxAlive stopped due to an error");    
         }
         log("KeepFritzboxAlive finished");
         
      }
   }
   private void executeFritzboxCommand( RosiSetterCommand command ) throws Exception {
  
      if( _fritzbox == null )return ;  

      String deviceName = command.getKey() ;
      String value      = command.getValue() ;

      _fritzbox.setDevice( deviceName , value.equals("on") ? 1 : 0 ) ;

   }
   public void run(){

      log("Starting.");

      while(true){
          try{

             RosiCommand command  =  take() ;

             if( command instanceof RosiSetterCommand ){

                log("Setter '"+command.getSource()+"' -> '"+getName()+"' cmd="+command ) ;

                try{

                   RosiSetterCommand fbc = createFritzboxCommand( (RosiSetterCommand) command ) ;
                   if( fbc != null ){

                       log("Sending to fritzbox : '"+fbc+"'");

                       executeFritzboxCommand( fbc ) ;
                   }

                }catch(Exception ee ){
                   errorLog( "Exception executing fritzbox command '"+command+"' : "+ee ); 
                }
             }else{
                log("unkown (ignored) '"+command.getSource()+"' -> '"+getName()+"' cmd="+command ) ;
             }

          }catch(InterruptedException ieee ){
             errorLog("Interrupted in main loop: "+ieee.getMessage() ) ;
             break ;
          }catch(Exception eee ){
             errorLog("Runtime Error in main loop : "+eee.getMessage() ) ;
          }
       }

   }
   public void setCommandProcessor( RosiCommandProcessor commandProcessor ){
     _commandProcessor = commandProcessor ;
   }
}
