package org.aurora.netty.client.serializer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

import org.aurora.channel.handshake.HandShakeAck;
import org.aurora.channel.handshake.HandShakeEnd;
import org.aurora.channel.handshake.HandShakeRequest;
import org.aurora.heartbeat.HeartBeat;
import org.aurora.netty.event.PushEvent;
import org.aurora.netty.serializer.IntUtil;
import org.aurora.netty.serializer.Serializer;
import org.aurora.netty.util.EventConstants;
import org.aurora.netty.util.HandShakeUtil;

import com.alibaba.fastjson.JSON;

/**
 * Json 序列化反序列化，使用fastJson
 * @author hantong
 *
 * 2013-6-5 下午4:53:40 
 */
public class ClientJsonSerializer implements Serializer {
	public static final String CHARSET = "UTF-8";
	public static final byte[] HEARTBEAT_BYTES = new byte[4];
	public static final byte[] EMPTY_BYTES = new byte[0];

	private ByteOrder order = ByteOrder.BIG_ENDIAN;

	public byte[] serialize(Object messageObject) throws Exception {	
		//心跳消息单独处理
		if(messageObject instanceof HeartBeat){
			return HEARTBEAT_BYTES;
		}
		
		byte[] msgBuffer;
		//序列化server  push消息
		if(messageObject instanceof PushEvent){
			byte[] msg = new byte[]{};
			PushEvent pushEvent = (PushEvent)messageObject;
			String userKey = pushEvent.getUserKey();
			Object content = pushEvent.getMessageContent();
			if(!(content instanceof String)){
				msg = JSON.toJSONString(content).getBytes(CHARSET);
			}else{
				msg = ((String)content).getBytes(CHARSET);
			}
			byte[] userKeyByte = new byte[]{};
			userKeyByte = ((String)userKey).getBytes(CHARSET);
			byte[] buffer;
			//message body的长度＋userkey的长度＋1个业务标志位＋4个字节长度userKey内容长度
			buffer = new byte[msg.length + userKeyByte.length+ 1+4];
			// 标识是业务消息
			buffer[0] = (byte) EventConstants.PUSH_BIZ;			
			byte[] lenUserKeyBytes = IntUtil.toBytes(userKeyByte.length, order);
			System.arraycopy(lenUserKeyBytes, 0, buffer, 1, 4);
			System.arraycopy(userKeyByte, 0, buffer, 5, userKeyByte.length);
			int startPosition = 1+4+userKeyByte.length;
			System.arraycopy(msg, 0, buffer, startPosition, msg.length);
			
			//
			byte[] result = new byte[buffer.length + 4];
			byte[] lenBytes = IntUtil.toBytes(buffer.length, order);
			System.arraycopy(lenBytes, 0, result, 0, IntUtil.COUNT);

			System.arraycopy(buffer, 0, result, 4, buffer.length);
			return result;
		}else if(HandShakeUtil.isHandShakeMsg(messageObject)){
			msgBuffer = serializeHandShakeMsg(messageObject);
		}else{
			msgBuffer = serializeNormalMsg(messageObject);
		}
		
		byte[] result = new byte[msgBuffer.length + 4];
		byte[] lenBytes = IntUtil.toBytes(msgBuffer.length, order);
		System.arraycopy(lenBytes, 0, result, 0, IntUtil.COUNT);

		System.arraycopy(msgBuffer, 0, result, 4, msgBuffer.length);
		
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
				// decode pushEvent 
				if(bytes[0] == EventConstants.PUSH_BIZ){
					int msgLength = IntUtil.toInt(IntUtil.subArray(bytes, 1, 4), order);
					byte[] msgBytes = IntUtil.subArray(bytes, 5, msgLength);
					String msgContent = JSON.parseObject(msgBytes, String.class);
					return msgContent;
				}else{
					byte[] dst = IntUtil.subArray(bytes, 0, bytes.length);
					try{
						return new String(dst, CHARSET);					
					}catch(UnsupportedEncodingException e){
						return null;
					}
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
	
	private byte[] serializeNormalMsg(Object object) throws Exception {
		byte[] msg = new byte[]{};
		if(!(object instanceof String)){
			msg = JSON.toJSONString(object).getBytes(CHARSET);
		}else{
			msg = ((String)object).getBytes(CHARSET);
		}
		byte[] buffer;
		buffer = new byte[msg.length + 1];
		// 标识是业务消息
		buffer[0] = (byte) EventConstants.PUSH_BIZ;
		//
		System.arraycopy(msg, 0, buffer, 1, msg.length);
		
		byte[] result = new byte[buffer.length + 4];
		byte[] lenBytes = IntUtil.toBytes(buffer.length, order);
		System.arraycopy(lenBytes, 0, result, 0, IntUtil.COUNT);

		System.arraycopy(buffer, 0, result, 4, buffer.length);
		return result;
	}
}
