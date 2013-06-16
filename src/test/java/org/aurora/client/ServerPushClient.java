package org.aurora.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.aurora.channel.handshake.HandShakeAck;
import org.aurora.channel.handshake.HandShakeEnd;
import org.aurora.netty.client.serializer.ClientJsonSerializer;
import org.aurora.netty.event.PushEvent;
import org.aurora.netty.serializer.Serializer;

/**
 * 模拟手机客户端
 * @author hantong
 *
 * 2013-6-9 下午1:52:30 
 */
public class ServerPushClient {
	public static final Serializer SERIALIZER = new ClientJsonSerializer();
	private ByteBuffer receivedBuffer = ByteBuffer.allocate(512);
	private Selector selector;
	private SocketChannel socketChannel = null;	
	private List<AtomicInteger> sendNumList = new ArrayList<AtomicInteger>(); 

	public List<AtomicInteger> getSendNumList() {
		return sendNumList;
	}

	public void setSendNumList(List<AtomicInteger> sendNumList) {
		this.sendNumList = sendNumList;
	}

	public void connect(String ip, int port) throws Exception {
		// 建立连接
		socketChannel = SocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(ip, port);
		socketChannel.connect(isa);
		socketChannel.configureBlocking(false);

		// 注册到selector
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		
		new Thread(new NioWorker()).start();
	}

	public void sendMsg(String userKey, String msg, int id) throws Exception{
		sendNumList.add(new AtomicInteger());
		sendNumList.get(id).incrementAndGet();
		PushEvent pushEvent = new PushEvent();
		pushEvent.setUserKey(userKey+id);
		pushEvent.setMessageContent(msg);
		byte[] hrBytes = SERIALIZER.serialize(pushEvent);
		send(hrBytes);
	}
	
	public void send(byte[] msg) throws Exception {
		ByteBuffer sendBuffer = ByteBuffer.allocate(msg.length);
		sendBuffer.put(msg);
		sendBuffer.flip();
		//
		socketChannel.write(sendBuffer);
	}

	/**
	 * 接收消息
	 */
	private void receive(SelectionKey key) throws Exception {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		// 读取消息
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
//				System.out.println(recMsg);
				//
				// 如果接收到Server发送的握手反馈消息，则回送握手完成消息
				if (recMsg instanceof HandShakeAck) {
					// 构造握手完成消息包
					HandShakeAck handShakeAck = (HandShakeAck)recMsg;
					HandShakeEnd finish = new HandShakeEnd(handShakeAck.getKey());
					byte[] hfBytes = SERIALIZER.serialize(finish);
					// 发送
					send(hfBytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		receivedBuffer.compact();
	}

	/**
	 * 通道读写任务
	 */
	private class NioWorker implements Runnable {
		public void run() {
			// 如果未准备好，则阻塞
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
								// 读
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
		ServerPushClient pushClient = new ServerPushClient();
		pushClient.connect("127.0.0.1", 9001);
	}

}
