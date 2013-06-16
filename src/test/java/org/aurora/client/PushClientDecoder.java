package org.aurora.client;

import org.aurora.heartbeat.HeartBeat;
import org.aurora.netty.client.serializer.ClientJsonSerializer;
import org.aurora.netty.serializer.Serializer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * @author hantong
 *
 * 2013-6-16 上午9:37:16 
 */
public class PushClientDecoder extends FrameDecoder {
	public static final Serializer SERIALIZER = new ClientJsonSerializer();

	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
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
				return SERIALIZER.deserialize(bytes);
			} else {
				buffer.resetReaderIndex();
			}
		}
		return null;
	}

}
