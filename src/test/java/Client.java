
/**
 * @author hantong
 *
 * 2013-6-8 上午9:21:24 
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;  
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
public class Client {  
    public static void main(String[] args) {  
        try {  
            Socket socket = new Socket("10.12.16.4", 30001);  
            socket.setKeepAlive(true);  
            new Thread(new T(socket)).start();  
 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
} 

class T implements Runnable {  
    public void run() {  
        try {  
            System.out.println(socket.toString());  
            socket.setKeepAlive(true);  
            socket.setSoTimeout(0);  
            String _pattern = "yyyy-MM-dd HH:mm:ss";  
            SimpleDateFormat format = new SimpleDateFormat(_pattern);  
            while (true) {  
                System.out.println("开始：" + format.format(new Date()));  
                try {  
                    InputStream ips = socket.getInputStream();  
                    Integer result = read(ips);
                    if(result != null){
                    	System.out.println(result);
                    }else{
                    	System.out.println("null");
                    }
                }catch(SocketTimeoutException e){  
                    e.printStackTrace();  
                }catch(SocketException e){  
                    e.printStackTrace();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
                Thread.sleep(1000);  
                System.out.println(socket.isBound()); // 是否邦定  
                System.out.println(socket.isClosed()); // 是否关闭  
                System.out.println(socket.isConnected()); // 是否连接  
                System.out.println(socket.isInputShutdown()); // 是否关闭输入流  
                System.out.println(socket.isOutputShutdown()); // 是否关闭输出流  
                System.out.println("结束：" + format.format(new Date()));  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    private Socket socket = null;  
    public T(Socket socket) {  
        this.socket = socket;  
    }  
    public Socket getSocket() {  
        return socket;  
    }  
    public void setSocket(Socket socket) {  
        this.socket = socket;  
    }  
    
    public Integer read(InputStream ips) throws IOException{
		int command = ips.read();
		if (command < 0) return null;
		int status = ips.read();
		if (status < 0) return null;

		int identifier_byte1 = ips.read();
		if (identifier_byte1 < 0) return null;
		int identifier_byte2 = ips.read();
		if (identifier_byte2 < 0) return null;
		int identifier_byte3 = ips.read();
		if (identifier_byte3 < 0) return null;
		int identifier_byte4 = ips.read();
		if (identifier_byte4 < 0) return null;
		int identifier = (identifier_byte1 << 24) + (identifier_byte2 << 16) + (identifier_byte3 << 8) + (identifier_byte4);
		return identifier;
    }
}  
