package org.aurora.netty.protocol;

import org.aurora.netty.serializer.ByteBufferWrapper;

/**
 * @author hantong
 *
 * 2013-6-5 下午8:48:30 
 */
public interface Protocol {
	
	/**
	 * encode Message to byte & write to network framework
	 * 
	 * @param message
	 * @param byteBuffer
	 * @throws Exception
	 */
	public ByteBufferWrapper encode(Object message,ByteBufferWrapper bytebufferWrapper) throws Exception;

	/**
	 * decode stream to object
	 * 
	 * @param wrapper
	 * @param errorObject stream not enough,then return this object
	 * @return Object 
	 * @throws Exception
	 */
	public Object decode(ByteBufferWrapper wrapper, Object errorObject,int...originPos) throws Exception;

}
