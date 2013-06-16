package org.aurora.netty.protocol;

/**
 * 包装client的请求数据
 * @author hantong
 *
 * 2013-6-5 下午9:22:13 
 */
public class RequestWrapper {
	
	private byte[] requestBody;
	
	public RequestWrapper(byte[] requestBody){
		this.requestBody = requestBody;
	}

	public byte[] getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(byte[] requestBody) {
		this.requestBody = requestBody;
	}
	
}
