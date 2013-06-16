package org.aurora.netty.util;

/**
 * @author hantong
 *
 * 2013-6-3 下午9:32:02 
 */
public class ConfigUtil {
	
	public static Integer parseInt(Object value, Integer defaultValue){
		if(value != null){
			if(value instanceof Integer){
				return (Integer)value;
			}else if(value instanceof String){
				try{
					return Integer.parseInt(value.toString());
				}catch(NumberFormatException e){
					return defaultValue;
				}
			}
		}
		
		return defaultValue;
	}
	
	public static Integer parseInt(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else if (value instanceof String) {
				return Integer.valueOf((String) value);
			}
		}
		return null;
	}
}
