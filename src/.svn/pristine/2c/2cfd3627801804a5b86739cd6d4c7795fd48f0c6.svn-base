package org.rosi.util ;

public class RosiCommand {
 
   private String _command = null ;
   private String _key = null , _value = null ;
   private String _source = null ;
   public RosiCommand( String command ){
      _command = command ;
   }
   public RosiCommand( String key , String value ){
      _key   = key ;
      _value = value ;
   }
   public String toString(){
     if( _command != null ){
        return "Command: "+_command ;
     }else{
        return "("+_key+":"+_value+")" ;
     }
   }
   public String getKey(){ 
     return _key ;
   }
   public String getValue(){ 
     return _value ;
   }
   public void setKey( String key){
     _key = key ;
   }
   public void setValue( String value ){
     _value = value ;
   }
   public String getSource(){ 
     return _source ;
   }
   public void setSource( String source ){ 
     _source = source ;
   }
   
}
