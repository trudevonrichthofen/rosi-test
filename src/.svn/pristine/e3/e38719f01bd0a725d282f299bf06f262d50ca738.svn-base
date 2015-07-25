package org.rosi.drivers.fritzbox ;

import java.util.* ;
   
public class FritzboxDeviceInfo {

   private String  _name    = null ;
   private boolean _present = false ;
   private String  _device  = null ;
   private int     _state   = -1 ;
   private float   _temperature = (float)0.0 ; 
   private float   _power       = (float)0.0 ; 

   public FritzboxDeviceInfo(){}
   
   public void setName( String name ){
       _name = name ;
   }
   public void setPresent( String value ){   
       _present = value.equals("1") ;
   }
   public void setTemperature( String temp ){
      _temperature = ((float)Integer.parseInt( temp )) / (float)10.0 ;
   }
   public void setPower( String temp ){
      _power = ((float)Integer.parseInt( temp )) / (float)1000.0 ;
   }
   public String getName(){ return _name ; }
   public boolean isPresent(){ return _present ; }
   
   public String toString(){
      StringBuffer sb = new StringBuffer() ;
      sb.append( _name ).append("(").append(_present?"OK":"DISCON") ;
      if( _device != null )sb.append(",ain=").append(_device) ;
      if( _present ){
      	 sb.append(",status=").append( _state < 0 ? "?" : ( _state == 0 ? "off" : "on" ) ) ;
      }
      sb.append(",t=").append(_temperature) ; 
      sb.append(",p=").append(_power) ; 
      sb.append(")") ;
      return sb.toString() ;
   }
   public void setDeviceID( String deviceID ){
     _device = deviceID ;
   }
   public void setState( int state ){
      _state = state ;
   }
   public void setState( String state ){
      _state = state.equals("1") ? 1 : ( state.equals("0") ? 0 : -1 ) ;
   }
   
}

