package org.rosi.execution.modules ;

import java.util.* ;
import java.io.*;
import java.util.concurrent.* ;

import org.rosi.util.*;
import org.rosi.execution.*;

public class FhemActorModule extends RosiModule {

   private RosiCommandProcessor  _commandProcessor = null ; 
   private ModuleContext         _context = null ;
   private RosiRuntimeExecution  _process = null ;
   private PatternTranslator     _filter  = null ;

   private String _fhemPath = "/home/homematic/fhem-5.6/fhem.pl" ;
   private int    _fhemPort = 7072 ;

   private String _actionLogFile  = "/var/log/rosi/rosiCommands.log" ;

   public FhemActorModule( String moduleName , ModuleContext context  )
      throws Exception
   {
      super(moduleName,context);
      log("Initiating.");
      _context = context ;

      _fhemPath = _context.get("fhemPath" , true ) ; 

      String fhemPort = _context.get("fhemPort" , true ) ;
     
      _fhemPort = Integer.parseInt( fhemPort ) ; 

      if( _fhemPort > 0 ) _process   = new RosiRuntimeExecution( _fhemPath , _fhemPort ) ;

      String filterFile = context.get( "filterFile" ) ;
      if( filterFile != null ){
         File f = new File( filterFile ) ;
         if( ! f.exists() )
           throw new
           IllegalArgumentException("Filer file not found : "+ filterFile ) ; 
 
         _filter = new PatternTranslator( f ) ;
      }

   } 
   public String composeFhemCommand( RosiSetterCommand command )
       throws Exception
   {

      String result = "set "+command.getKey()+" "+command.getValue() ;
      if( _filter != null ){

          String [] sub = _filter.translate( command.getKey() ) ;

          if( ( sub != null ) && ( sub.length >=1 ) )
             result = "set "+sub[0]+" "+command.getValue() ;
          else
             result = null;

      }
      return result ;

   }
   public void run(){

      log("Starting.");

      while(true){
          try{

             RosiCommand command  =  take() ;

             if( command instanceof RosiSetterCommand ){

                log("Setter '"+command.getSource()+"' -> '"+getName()+"' cmd="+command ) ;

                try{

                   String fhemCommand = composeFhemCommand( (RosiSetterCommand) command ) ;

                   if( fhemCommand != null ){
                      log("Sending to fhem : '"+fhemCommand+"'");

                      if( _fhemPort > 0 )_process.execute( fhemCommand ) ;
                   }

                }catch(Exception ee ){
                   errorLog( "Exception executing command '"+command+"' : "+ee ); 
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
