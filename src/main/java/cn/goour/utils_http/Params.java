package cn.goour.utils_http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Params {
	private Map<String, Object> params = new HashMap<String, Object>();
	public Params() {
	}
	public void add(String key, Object value){
		params.put(key, value);
	}
	public Object getValue(String key){
		return params.get(key);
	}
	public void remove(String key){
		params.remove(key);
	}
	public String getParams() {
		StringBuffer sBuffer = new StringBuffer();
		for(Entry<String, Object> item:params.entrySet()){
			sBuffer.append(item.getKey());
			sBuffer.append("=");
			try {
				sBuffer.append(URLEncoder.encode(item.getValue().toString(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sBuffer.append("&");
		}
		return sBuffer.toString();
	}
	public Map<String, Object> getParamsMap(){
		return params;
	}
	
	@Override
	public String toString() {
		return "Params [params=" + params + ", getParams()=" + getParams() + "]";
	}
}
