package org.aurora.netty.serializer;

import org.aurora.netty.protocol.ProtocolUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * 默认的netty  encoder
 * @author hantong
 *
 * 2013-6-7 下午12:23:18 
 */
public class AuroraNettyEncoder extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		NettyByteBufferWrapper byteBufferWrapper = new NettyByteBufferWrapper();
		ProtocolUtils.encode(msg, byteBufferWrapper);
		return byteBufferWrapper.getBuffer();
	}

}
