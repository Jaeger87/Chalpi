package bot;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class IpUtility {

	
    public static String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Display name:" + netint.getDisplayName() + "\n");
    	sb.append("Name:" + netint.getName());
    	
    	sb.append("Display name:" + netint.getDisplayName() + "\n");
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) 
        	sb.append("InetAddress:" + inetAddress + "\n");
        
        sb.append("\n");
        
        return sb.toString();
     }
}
