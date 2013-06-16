package org.aurora.netty.serializer;

import java.nio.ByteOrder;

import org.aurora.channel.handshake.HandShakeAck;
import org.aurora.channel.handshake.HandShakeEnd;
import org.aurora.channel.handshake.HandShakeRequest;
import org.aurora.heartbeat.HeartBeat;
import org.aurora.netty.client.serializer.ClientJsonSerializer;
import org.aurora.netty.event.PushEvent;
import org.aurora.netty.util.EventConstants;
import org.aurora.netty.util.HandShakeUtil;

import com.alibaba.fastjson.JSON;

/**
 * Json 序列化反序列化，使用fastJson
 * @author hantong
 *
 * 2013-6-5 下午4:53:40 
 */
public class JsonSerializer implements Serializer {
	public static final String CHARSET = "UTF-8";
	public static final byte[] HEARTBEAT_BYTES = new byte[4];
	public static final byte[] EMPTY_BYTES = new byte[0];

	private ByteOrder order = ByteOrder.BIG_ENDIAN;

	public byte[] serialize(Object messageObject) throws Exception {
		//心跳消息单独处理
		if(messageObject instanceof HeartBeat){
			return HEARTBEAT_BYTES;
		}
		
		byte[] buffer;
		//序列化握手消息
		if(HandShakeUtil.isHandShakeMsg(messageObject)){
			buffer = serializeHandShakeMsg(messageObject);
		}else if(messageObject instanceof PushEvent){
			//序列化server  push消息
			buffer =  serializePushMsg((PushEvent)messageObject);
		}else{
			buffer = serializeNormalMsg(messageObject);
		}
		
		byte[] result = new byte[buffer.length + 4];
		byte[] lenBytes = IntUtil.toBytes(buffer.length, order);
		System.arraycopy(lenBytes, 0, result, 0, IntUtil.COUNT);

		System.arraycopy(buffer, 0, result, 4, buffer.length);
		return result;
	}


	public Object deserialize(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException();
		}
		//心跳消息
		if (bytes.length == 0) {
			return HeartBeat.getSingleton();
		} else {
			// 握手消息
			if (bytes[0] == EventConstants.HAND_SHAKE) {
				byte mark = bytes[1];
				int handshakeMsgLength = IntUtil.toInt(IntUtil.subArray(bytes, 2, 4), order);
				//
				byte[] dst = IntUtil.subArray(bytes, 6, handshakeMsgLength);
				//
				if (mark == EventConstants.MARK_INIT) {
					return JSON.parseObject(dst, HandShakeRequest.class);
				} else if (mark == EventConstants.MARK_ACK) {
					return JSON.parseObject(dst, HandShakeAck.class);
				} else {
					return JSON.parseObject(dst, HandShakeEnd.class);
				}
			} else {
				// 业务消息
				if(bytes[0] == EventConstants.PUSH_BIZ){
					byte[] userKeyByte = IntUtil.subArray(bytes, 1, 4);
//					int identifier = (identifier_byte1 << 24) + (identifier_byte2 << 16) + (identifier_byte3 << 8) + (identifier_byte4);

					int userKeyButeLen = IntUtil.toInt(userKeyByte, ByteOrder.BIG_ENDIAN);
					byte[] userKey = IntUtil.subArray(bytes, 1+4, userKeyButeLen);
					String userKeyString = new String(userKey);
					PushEvent pushEvent = new PushEvent();
					pushEvent.setUserKey(userKeyString);
					pushEvent.setMessageContent(IntUtil.subArray(bytes, 1+4+userKeyButeLen, bytes.length - 1-4-userKeyButeLen));
					return pushEvent;
				}else{
					byte[] dst = IntUtil.subArray(bytes, 1, bytes.length - 1);
					return JSON.parse(dst);					
				}
			}
		}
	}
	
	private byte[] serializeHandShakeMsg(Object object) throws Exception {
		// 标识是握手哪个阶段的消息
		byte mark = 0;
		if (object instanceof HandShakeRequest) {
			mark = EventConstants.MARK_INIT;
		} else if (object instanceof HandShakeAck) {
			mark = EventConstants.MARK_ACK;
		} else if (object instanceof HandShakeEnd) {
			mark = EventConstants.MARK_END;
		}
		byte[] bytes = JSON.toJSONString(object).getBytes(CHARSET);
		byte[] result = new byte[bytes.length + 4 + 2];
		// 标识是握手消息
		result[0] = (byte) EventConstants.HAND_SHAKE;
		// 标识是握手哪个阶段的消息
		result[1] = mark;

		//
		byte[] lenBytes = IntUtil.toBytes(bytes.length, order);
		System.arraycopy(lenBytes, 0, result, 2, lenBytes.length);
		//
		System.arraycopy(bytes, 0, result, 6, bytes.length);

		return result;
	}
	
	private byte[] serializePushMsg(PushEvent pushEvent) throws Exception {
		byte[] bytes = JSON.toJSONString(pushEvent.getMessageContent()).getBytes(CHARSET);
		byte[] result = new byte[bytes.length + 4 + 1];
		// 标识是push消息
		result[0] = (byte) EventConstants.PUSH_BIZ;

		byte[] lenBytes = IntUtil.toBytes(bytes.length, order);
		System.arraycopy(lenBytes, 0, result, 1, lenBytes.length);
		//
		System.arraycopy(bytes, 0, result, 5, bytes.length);

		return result;
	}
	
	private byte[] serializeNormalMsg(Object object) throws Exception {
		byte[] msg = new byte[]{};
		if(object instanceof byte[]){
			return (byte[])object;
		}else if(object instanceof String){
			msg = ((String)object).getBytes(CHARSET);
		}else{
			msg = JSON.toJSONString(object).getBytes(CHARSET);
		}
		byte[] buffer;
		buffer = new byte[msg.length + 1];
		// 标识是业务消息
		buffer[0] = (byte) EventConstants.RESPONSE_BIZ;
		//
		System.arraycopy(msg, 0, buffer, 1, msg.length);
		
		return buffer;
	}
	
	public static void main(String[] args) throws Exception{
		JsonSerializer jsonSerializer = new JsonSerializer();
		ClientJsonSerializer clientJsonSerializer = new ClientJsonSerializer();
		PushEvent pushEvent = new PushEvent();
		pushEvent.setUserKey("olylakersolylakersolylakersolylakersolylakersolylakers");
		pushEvent.setMessageContent("alert");
		
		byte[] src = clientJsonSerializer.serialize(pushEvent);
		Object object = jsonSerializer.deserialize(IntUtil.subArray(src, 4, src.length - 4));
		if(object instanceof PushEvent){
			PushEvent pushEvent2 = (PushEvent)object;
			System.out.println(pushEvent2.getUserKey());
			System.out.println(new String((byte[])pushEvent2.getMessageContent()));
		}
	}
}
