package org.aurora.client;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hantong.oly
 *
 * 2013-6-13  下午5:33:34
 */
public class ReceiveTest {
	
	public static final String appkey = "21281452";
	public static final String secret = "c211167c6bd43f3a2971fc66836a4ed3";
	public static final String userKey = "21281452:c211167c6bd43f3a2971fc66836a4ed3:";
	public static final int userNum = 2;
	public static String msg = "\"picture\":\"i1/T1SEpbXbhXXXaB29.4_053918.jpg\",\"changed_fields\":\"price\",\"num_iid\":1604712634,\"title\":\"16\",\"price\":\"15.00\"";
	private static ServerPushClient serverPushClient;
	private static List<PhoneClient> phoneClients = new ArrayList<PhoneClient>();
	
	public static void main(String[] args) throws Exception{
		startReceive("127.0.0.1", 9000);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			public void run() {
				for (int index = 0; index < userNum; index++) {
					System.out.println(index+" receive: "+phoneClients.get(index).getReceiveMsgNum().intValue());
				}
				System.out.println("end");				
			}
		}));
		
		Thread.sleep(20000);
		while (true) {
			for (int index = 0; index < userNum; index++) {
				System.out.println(index+" receive: "+phoneClients.get(index).getReceiveMsgNum().intValue());
			}
			System.out.println("==============================");
			Thread.sleep(10000);
		}
	}
	
	public static void startServerPushClient(String ip, int innerPort) throws Exception{
		serverPushClient = new ServerPushClient();
		serverPushClient.connect(ip, innerPort);
		
		new Thread(new Runnable() {
			
			public void run() {
				while(true){
					for(int index = 0 ;index<userNum; index++){
						try {
							serverPushClient.sendMsg(userKey, msg, index);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}				
			}
		}).start();
	}
	
	public static void startPhoneClient(String ip, int outPort, String appkey, String secret, int index) throws Exception{
		PhoneClient phoneClient = new PhoneClient();
		phoneClients.add(phoneClient);
		phoneClient.connect(ip, outPort, appkey, secret, index);
	}
	
	public static void startReceive(String ip, int outPort) throws Exception{
		for(int index = 0 ;index<userNum; index++){
			startPhoneClient(ip, outPort, appkey, secret, index);
		}
	}
	
	
}
