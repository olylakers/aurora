package org.aurora.client;


/**
 * @author hantong.oly
 *
 * 2013-6-13  下午5:33:34
 */
public class PushTest {
	
	public static final String appkey = "21281452";
	public static final String secret = "c211167c6bd43f3a2971fc66836a4ed3";
	public static final String userKey = "21281452:c211167c6bd43f3a2971fc66836a4ed3:";
	public static final int userNum = 2;
	public static String msg = "\"picture\":\"i1/T1SEpbXbhXXXaB29.4_053918.jpg\",\"changed_fields\":\"price\",\"num_iid\":1604712634,\"title\":\"16\",\"price\":\"15.00\"";
	
	public static void main(String[] args) throws Exception{
		PushClientFactory pushClientFactory = new PushClientFactory();
		pushClientFactory.init();
		final PushClient[] pushClients = new PushClient[userNum];
		
		for (int i = 0; i < userNum; i++) {
			PushClient pushClient = pushClientFactory.createClient("127.0.0.1");
			pushClient.init();
			pushClients[i] = pushClient;
		}
		
		Thread.sleep(20000);
		new Thread(new Runnable() {
			
			public void run() {
				while (true) {
					for (int i = 0; i < userNum; i++) {
						try {
							pushClients[i].sendMsg(userKey, msg, i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();

		while (true) {
			for (int index = 0; index < userNum; index++) {
				System.out.println(index+" send success: "+pushClientFactory.getSendSuccessNumList().get(index).intValue());
				System.out.println(index+" send failed: "+pushClientFactory.getSendFailedNumList().get(index).intValue());
			}
			System.out.println("==============================");
			Thread.sleep(10000);
		}
	}
}
