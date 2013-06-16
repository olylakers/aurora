
/**
 * @author hantong
 *
 * 2013-6-8 上午9:22:42 
 */

import java.net.Socket;  
public class ShortClient {  
    public static void main(String[] args) {  
        try {  
            Socket socket = new Socket("10.12.16.4", 30001);  
            socket.setKeepAlive(true);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
} 