package org.aurora.netty.serializer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;


/**
 * @author hantong
 *
 * 2013-6-5 下午8:44:41 
 */
public class NettyByteBufferWrapper implements ByteBufferWrapper {

	private ChannelBuffer buffer;
	
	public NettyByteBufferWrapper(){
		;
	}
	
	public NettyByteBufferWrapper(ChannelBuffer in){
		buffer = in;
	}
	
	public ByteBufferWrapper get(int capacity) {
		buffer = ChannelBuffers.dynamicBuffer(capacity);
		return this;
	}

	public byte readByte() {
		return buffer.readByte();
	}

	public void readBytes(byte[] dst) {
		buffer.readBytes(dst);
	}

	public int readInt() {
		return buffer.readInt();
	}

	public int readableBytes() {
		return buffer.readableBytes();
	}

	public int readerIndex() {
		return buffer.readerIndex();
	}

	public void setReaderIndex(int index) {
		buffer.setIndex(index, buffer.writerIndex());
	}

	public void writeByte(byte data) {
		buffer.writeByte(data);
	}

	public void writeBytes(byte[] data) {
		buffer.writeBytes(data);
	}

	public void writeInt(int data) {
		buffer.writeInt(data);
	}
	
	public ChannelBuffer getBuffer(){
		return buffer;
	}

	public void writeByte(int index, byte data) {
		buffer.writeByte(data);
	}

}
