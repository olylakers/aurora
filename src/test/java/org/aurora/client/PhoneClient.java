package org.aurora.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.aurora.channel.handshake.HandShakeAck;
import org.aurora.channel.handshake.HandShakeEnd;
import org.aurora.channel.handshake.HandShakeRequest;
import org.aurora.heartbeat.HeartBeat;
import org.aurora.netty.client.serializer.ClientJsonSerializer;
import org.aurora.netty.serializer.Serializer;

/**
 * 妯℃嫙鎵嬫満瀹㈡埛绔� * @author hantong
 *
 * 2013-6-9 涓嬪崍1:52:30 
 */
public class PhoneClient {
	public static final Serializer SERIALIZER = new ClientJsonSerializer();
	private ByteBuffer receivedBuffer = ByteBuffer.allocate(512);
	private Selector selector;
	private SocketChannel socketChannel = null;
	private AtomicInteger receiveMsgNum = new AtomicInteger();
	
	public static final String appkey = "21281452";
	public static final String secret = "c211167c6bd43f3a2971fc66836a4ed3";
	public static final String userKey = "21281452:c211167c6bd43f3a2971fc66836a4ed3:";
	
	private int id;
	
	public AtomicInteger getReceiveMsgNum() {
		return receiveMsgNum;
	}

	public void setReceiveMsgNum(AtomicInteger receiveMsgNum) {
		this.receiveMsgNum = receiveMsgNum;
	}

	public void connect(String ip, int port, String appkey, String secret, int id) throws Exception {
		socketChannel = SocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(ip, port);
		socketChannel.connect(isa);
		socketChannel.configureBlocking(false);

		this.id = id;
		
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

		HandShakeRequest handShakeRequest = new HandShakeRequest(appkey, secret, String.valueOf(id));
		byte[] hrBytes = SERIALIZER.serialize(handShakeRequest);
		send(hrBytes);

		//
		new Thread(new NioWorker()).start();

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					send(SERIALIZER.serialize(HeartBeat.getSingleton()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 10000L, 10000L, TimeUnit.MILLISECONDS);
	}

	public void send(byte[] msg) throws Exception {
		ByteBuffer sendBuffer = ByteBuffer.allocate(msg.length);
		sendBuffer.put(msg);
		sendBuffer.flip();
		//
		socketChannel.write(sendBuffer);
	}

	/**
	 * 鎺ユ敹娑堟伅
	 */
	private void receive(SelectionKey key) throws Exception {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		socketChannel.read(receivedBuffer);
		receivedBuffer.flip();

		while (receivedBuffer.remaining() > 3) {
			receivedBuffer.mark();
			int length = receivedBuffer.getInt();
			//
			if (length > receivedBuffer.capacity() - 4) {
				receivedBuffer.reset();
				synchronized (receivedBuffer) {
					ByteBuffer temp = receivedBuffer;
					receivedBuffer = ByteBuffer.allocate(length + 4);
					receivedBuffer.put(temp);
				}
				return;
			} else if (receivedBuffer.remaining() < length) {
				receivedBuffer.reset();
				receivedBuffer.compact();
				return;
			}
			//
			byte[] buffer = new byte[length];
			try {
				receivedBuffer.get(buffer);
				Object recMsg = SERIALIZER.deserialize(buffer);
				if (recMsg == null) {
					continue;
				}
				if (recMsg instanceof HandShakeAck) {
					HandShakeAck handShakeAck = (HandShakeAck)recMsg;
					HandShakeEnd finish = new HandShakeEnd(handShakeAck.getKey());
					byte[] hfBytes = SERIALIZER.serialize(finish);
					send(hfBytes);
				}else if(recMsg instanceof HeartBeat){
					
				}else{
					receiveMsgNum.addAndGet(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		receivedBuffer.compact();
	}

	/**
	 * 閫氶亾璇诲啓浠诲姟
	 */
	private class NioWorker implements Runnable {
		public void run() {
			// 濡傛灉鏈噯澶囧ソ锛屽垯闃诲
			while (true) {
				try {
					if (selector.select(500) > 0) {
						Set<SelectionKey> readyKeys = selector.selectedKeys();
						Iterator<SelectionKey> it = readyKeys.iterator();
						//
						while (it.hasNext()) {
							final SelectionKey key = it.next();
							try {
								it.remove();
								if (key.isReadable()) {
									receive(key);
								}
							} catch (Exception e) {
								e.printStackTrace();
								try {
									if (key != null) {
										key.cancel();
										key.channel().close();
									}
								} catch (Exception ex) {
									e.printStackTrace();
								}
							}
						}
					}
				} catch (IOException e) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						e.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		PhoneClient phoneClient = new PhoneClient();
		phoneClient.connect("127.0.0.1", 9000, "", "", 1);
	}

}
