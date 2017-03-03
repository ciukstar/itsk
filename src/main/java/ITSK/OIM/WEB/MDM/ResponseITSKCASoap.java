
package ITSK.OIM.WEB.MDM;

import java.util.HashMap;

/**
 *
 * @author birillo
 */
public class ResponseITSKCASoap {
    
    HashMap propertyMap;
    String log = "";
    String result = "ERROR";
 
     public HashMap getPropertyMap() {
          return propertyMap;
     }
 
     public void setPropertyMap(HashMap parametrMap) {
          this.propertyMap = parametrMap;
     }
    
}
