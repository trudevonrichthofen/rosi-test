package org.rosi.execution.modules ;

import java.util.* ;
import java.text.SimpleDateFormat ;
import java.io.* ;

import org.rosi.util.*;
import org.rosi.execution.*;
import org.rosi.drivers.nut.*;


public class NutProducerModule extends RosiModule {

   private RosiCommandProcessor _commandProcessor = null ; 
   private ModuleContext        _context = null ;
   private SimpleDateFormat     _sdf     = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
   private File                 _file    = null ;
   private PatternTranslator    _filter  = null ;
   private NutUpsDriver         _ups     = null ;
   private String _hostName  = null ;
   private String _upsDevice = null ;
   private int    _port      = 0 ;
   private long   _sleepTime = 1000L;


   public NutProducerModule( String moduleName , ModuleContext context  )
      throws Exception {

      super(moduleName,context);

      _context = context ;

      log( "Started");
 
      _hostName   = context.get("hostName"   , true ); 
      _upsDevice  = context.get("upsName"    , true ) ;
      _port       = Integer.parseInt(  context.get("portNumber" , true ) ) ; 
      _sleepTime  = Long.parseLong( context.get("sleepTime" , true ) ) * 1000L ;

      _ups = new NutUpsDriver( _hostName , _port ) ;

      log( "Context created with hostname : "+_hostName+", port : "+_port+", device : "+_upsDevice);

      String filterFile = context.get( "filterFile" ) ;
      if( filterFile != null ){
         File f = new File( filterFile ) ;
         if( ! f.exists() )
           throw new
           IllegalArgumentException("Filer file not found : "+ filterFile ) ;

         _filter = new PatternTranslator( f ) ;
      }


   } 
   public void run(){

       while(true){

          try{

             Map<String,String> map = _ups.getVariableList( _upsDevice ) ; 

             for( Map.Entry e : map.entrySet() ){

                if( _filter != null ){

                   String [] sub = _filter.translate( e.getKey()+":"+e.getValue() ) ;

                   if( ( sub == null ) || ( sub.length < 2 ) )continue ;
         
                   put( new RosiSetterCommand( sub[0] , sub[1] ) ) ;
                }
 
             }

             Thread.sleep( _sleepTime ) ;

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
