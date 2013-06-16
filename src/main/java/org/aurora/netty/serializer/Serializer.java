package org.aurora.netty.serializer;

/**
 * 序列化接口
 * @author hantong
 *
 * 2013-6-5 下午4:47:49 
 */
public interface Serializer {
	
	/**
	 * 对象序列化
	 * @param messageObject
	 * @return
	 * @throws Exception 
	 */
	byte[] serialize(Object messageObject) throws Exception;
	
	 /**
	  * 对象反序列化
	  * @param bytes
	  * @return
	  */
	Object deserialize(byte[] bytes);
}
