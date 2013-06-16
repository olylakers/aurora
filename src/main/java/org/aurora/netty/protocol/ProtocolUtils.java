package org.aurora.netty.protocol;

import org.aurora.heartbeat.HeartBeat;
import org.aurora.netty.client.serializer.ClientJsonSerializer;
import org.aurora.netty.serializer.ByteBufferWrapper;
import org.aurora.netty.serializer.JsonSerializer;
import org.aurora.netty.serializer.Serializer;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 根据协议从io中读取相应的bytes
 * @author hantong
 *
 * 2013-6-5 下午8:41:18 
 */
public class ProtocolUtils {

	public static final int HEADER_LEN = 2;
	
	public static final byte CURRENT_VERSION = (byte)1;
	
	public static final Serializer DEFAULT_SERIALIZER = new JsonSerializer();
	public static final Serializer CLIENT_SERIALIZER = new ClientJsonSerializer();
	
	public static ByteBufferWrapper encode(Object message,ByteBufferWrapper bytebufferWrapper) throws Exception {
		byte[] data = DEFAULT_SERIALIZER.serialize(message);
		ByteBufferWrapper byteBuffer = bytebufferWrapper.get(data.length);
		byteBuffer.writeBytes(data);
		return bytebufferWrapper;
	}

	public static Object decode(ChannelBuffer buffer, Object errorObject) throws Exception {
		if (buffer.readableBytes() >= 4) {
			buffer.markReaderIndex();
			int length = buffer.readInt();
			
			if (length < 0) {
				return null;
			} else if (length == 0) {
				return HeartBeat.getSingleton();
			} else if (buffer.readableBytes() >= length) {
				byte[] bytes = new byte[length];
				buffer.readBytes(bytes);
				return new RequestWrapper(bytes);
			} else {
				buffer.resetReaderIndex();
			}
		}

		return null;
	}
	
	/**
	 * 对于pusheEvent，只decode对应的userKey，真正的消息内容decode，直接发送到客户端
	 * @param buffer
	 * @param errorObject
	 * @return
	 * @throws Exception
	 */
	public static Object decodePushEvent(ChannelBuffer buffer, Object errorObject) throws Exception {
		if (buffer.readableBytes() >= 4) {
			buffer.markReaderIndex();
			int length = buffer.readInt();
			
			if (length < 0) {
				return null;
			} else if (length == 0) {
				return HeartBeat.getSingleton();
			} else if (buffer.readableBytes() >= length) {
				byte[] bytes = new byte[length];
				buffer.readBytes(bytes);
				return DEFAULT_SERIALIZER.deserialize(bytes);
			} else {
				buffer.resetReaderIndex();
			}
		}

		return null;
	}

}

