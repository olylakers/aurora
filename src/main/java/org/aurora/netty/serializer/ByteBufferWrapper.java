package org.aurora.netty.serializer;


/**
 * @author hantong
 *
 * 2013-6-5 下午8:46:14 
 */
public interface ByteBufferWrapper {

	public ByteBufferWrapper get(int capacity);
	
	public void writeByte(int index,byte data);
	
	public void writeByte(byte data);
	
	public byte readByte();
	
	public void writeInt(int data);
	
	public void writeBytes(byte[] data);
	
	public int readableBytes();
	
	public int readInt();
	
	public void readBytes(byte[] dst);
	
	public int readerIndex();
	
	public void setReaderIndex(int readerIndex);
	
}
