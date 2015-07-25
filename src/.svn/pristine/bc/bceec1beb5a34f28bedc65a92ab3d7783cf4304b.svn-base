package org.rosi.execution.modules ;

import java.util.* ;
import java.text.SimpleDateFormat ;
import java.io.* ;

import org.rosi.util.*;
import org.rosi.execution.*;
import org.rosi.drivers.modem.*;


public class SMSActionModule extends RosiModule {

   private class SMSContext {

      private String accessPin           = null  ;
      private String defaultPhoneNumber  = null ;
      private SMSDriver smsDriver        = null ;

      private SMSContext( String portName , String pin ) throws Exception {
         this.accessPin = pin ;
         this.smsDriver = new SMSDriver( portName , pin ) ;
      }
      
      private void sendMessage( String phoneNumber , String message ) throws Exception{
         this.smsDriver.sendSMS( this.accessPin , phoneNumber , message ) ;
      }
      private void sendMessage( String message ) throws Exception{
         this.smsDriver.sendSMS( this.accessPin , this.defaultPhoneNumber , message ) ;
      }
   }
   private RosiCommandProcessor _commandProcessor = null ; 
   private ModuleContext        _context = null ;
   private SimpleDateFormat     _sdf     = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
   private File                 _file    = null ;
   private PatternTranslator    _filter  = null ;

   private SMSContext _smsContext = null ;

   public SMSActionModule( String moduleName , ModuleContext context  )
      throws Exception {

      super(moduleName,context);

      _context = context ;

      log( "Started");
 
      String portName = context.get("portName" , true ); 
      String pin      = context.get("pin" , true ) ;

      _smsContext = new SMSContext( portName , pin ) ;

      log( "Context created with "+portName+" and "+pin);

      _smsContext.defaultPhoneNumber = context.get("phoneNumber" , true ) ;
 
      log( "Default phone number is : "+_smsContext.defaultPhoneNumber );

      String filterFile = context.get( "filterFile" ) ;
      if( filterFile != null ){
         File f = new File( filterFile ) ;
         if( ! f.exists() )
           throw new
           IllegalArgumentException("Filer file not found : "+ filterFile ) ;

         _filter = new PatternTranslator( f ) ;
      }


   } 
   private String buildMessage( String key , String value ){
  
      StringBuffer sb = new StringBuffer() ;
 
      sb.append(_sdf.format( new Date() )).append(";").
         append(key).append(";").
         append(value).append(";");

      return sb.toString();

   } 
   public void run(){

       while(true){

          try{

             RosiCommand c  = take() ;
 
             if( ! ( c instanceof RosiSetterCommand ) ){
                 errorLog("Received an unexpected command type : "+c.getClass().getName() ) ;
                 continue ;
             }

             RosiSetterCommand command = new RosiSetterCommand( (RosiSetterCommand) c ) ; 

             debug("SMS received : "+command);
             if( _filter != null ){

                String [] sub = _filter.translate( command.getKey()+":"+command.getValue() ) ;

                if( ( sub == null ) || ( sub.length == 0 ) )continue ;

                for( int i = 0 ; i < sub.length ; i++ ){
                  debug( "SUB["+i+"] : >"+sub[i]+"<");
                }
                command.setKey( sub[0] ) ;
                if( sub.length > 1 )command.setValue( sub[1] ) ;
 
                if( sub.length > 2 ){
                   log("SMS ("+sub[2]+") : "+command);
                   _smsContext.sendMessage( sub[2] ,  buildMessage( command.getKey() , command.getValue() ) ) ; 
                }else{
                   log("SMS (default phone) : "+command);
                   _smsContext.sendMessage( buildMessage( command.getKey() , command.getValue() ) ) ; 
                } 
 
             }

          }catch(InterruptedException ieee ){
             errorLog("Was interrupted and stopped" ) ;
             break ;
          }catch(Exception eee ){
             errorLog( "Got exeception in main loop: "+eee ) ;
             if( isDebugMode() )eee.printStackTrace();
             break ;
          }
       }

   }
   public void setCommandProcessor( RosiCommandProcessor commandProcessor ){
     _commandProcessor = commandProcessor ;
   }
}
