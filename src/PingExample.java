
import java.net.InetAddress;
import java.net.URL;

import org.tempuri.* ;

import com.vmware.vim25.mo.ServiceInstance;

public class PingExample
{
   
	public static void main(String[] args) throws Exception
    {
	
		String host = "130.65.133.238";
	   //String host = "10.0.2.15";
       System.out.println( "Ping Host: " + host ) ;
       Service service = new Service();
       ServiceSoap port = service.getServiceSoap(); 
       String result = port.pingHost( host ) ;
       System.out.println( "Ping Result: " + result ) ;
       
	
		//InetAddress adr=InetAddress.getByName("130.65.133.62/sdk"); 
		//System.out.println("Reachable Host: "+adr.isReachable(10000));  
		//boolean status = adr.isReachable(3000);  
		//System.out.println("Reachable Host:" + status);    
		
    }
	
}
