package cn.goour.utils_http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2632058136291193491L;
	private String url;
	private Map<String, List<String>> backHeader = new HashMap<String, List<String>>();
	private Map<String, String> sendHeader = new HashMap<String, String>();
	/* private Map<String, Object> paramsMap = new HashMap<>(); */
	private int connectTimeout = 30000;
	private int readTimeout = 30000;
	private String data;

	public static HttpConfig getInstance() {
		return new HttpConfig();
	}

	public HttpConfig() {
		sendHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		sendHeader.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		// sendHeader.put("Accept-Encoding","gzip, deflate, br");
		sendHeader.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		sendHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
		sendHeader.put("Connection", "Keep-Alive");
	}

	public HttpConfig(String url) {
		this();
		this.url = url;
	}

	public HttpConfig(String url, Map<String, String> head) {
		this();
		this.url = url;
		this.sendHeader.putAll(head);
	}

	public HttpConfig(String url, String data) {
		this();
		this.url = url;
		this.setData(data);
	}

	public HttpConfig(String url, String data, Map<String, String> head) {
		this();
		this.url = url;
		this.setData(data);
		this.sendHeader.putAll(head);
	}

	/*
	 * public HttpConfig(String url, Map<String, Object> paramsMap) { this();
	 * this.url = url; this.paramsMap.putAll(paramsMap); }
	 * 
	 * public HttpConfig(String url, Map<String, Object> paramsMap, Map<String,
	 * String> headMap) { this(); this.url = url;
	 * this.paramsMap.putAll(paramsMap); this.sendHeader.putAll(headMap); }
	 */
	/**
	 * 链接服务器后会返回头部信息，采用此方法重置原有的头部信息，以方便其中的COOKIE可以在下次请求中使用
	 * 
	 * @param headerMap
	 */
	public void setBackHeader(Map<String, List<String>> headerMap) {
		for (String key : headerMap.keySet()) {
			if (key == null)
				continue;
			List<String> list = headerMap.get(key);
			List<String> list2 = this.backHeader.get(key);
			// System.out.println(key);
			if (list2 == null) {
				List<String> list3 = new ArrayList<String>();
				list3.addAll(list);
				this.backHeader.put(key, list3);
			} else {
				if (key.equals("Set-Cookie")) {// 合并重复的Cookie
					list2.addAll(list);
					HashMap<String, Integer> map = new HashMap<String, Integer>();
					for (int i = 0; i < list2.size(); i++) {
						String cookieKey = list2.get(i).split("=")[0];
						if (map.containsKey(cookieKey)) {
							list2.set(map.get(cookieKey), list2.get(i));
							list2.remove(i--);
							// System.out.print(i+cookieKey+" ");
						}
						map.put(cookieKey, i);
					}
				}
			}

		}
	}

	/**
	 * @return backHeader
	 */
	public Map<String, List<String>> getBackHeader() {
		return backHeader;
	}

	/**
	 * 通过setBackHeader(Map<String, List<String>> headerMap)
	 * 后获取其中的COOKIE信息，以字符串的形式返回
	 * 
	 * @return
	 */
	public String getBackHeaderCookieString() {
		StringBuffer re = new StringBuffer();
		if (backHeader != null) {
			List<String> ckie = backHeader.get("Set-Cookie");
			if (ckie != null) {
				for (int i = 0; i < ckie.size(); i++) {
					String coo = ckie.get(i);
					re.append(coo.split(";")[0] + ";");
				}
			}
		}
		return re.toString();
	}

	/**
	 * 以Map的形式返回setBackHeader(Map<String, List<String>> headerMap)的COOKIE信息
	 * 
	 * @return
	 */
	public String getBackHeaderCookieMap() {
		StringBuffer re = new StringBuffer();
		List<String> ckie = backHeader.get("Set-Cookie");
		if (ckie != null) {
			for (int i = 0; i < ckie.size(); i++) {
				String coo = ckie.get(i);
				System.out.println(ckie);
				re.append(coo.split(";")[0] + ";");
			}
		}
		return re.toString();
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            要设置的 url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return data
	 */
	public String getData() {
		// StringBuffer sBuffer = new StringBuffer();
		// if (paramsMap != null) {
		// for(Entry<String, Object> item:paramsMap.entrySet()){
		// sBuffer.append(item.getKey());
		// sBuffer.append("=");
		// sBuffer.append(item.getValue());
		// sBuffer.append("&");
		// }
		// }
		// return sBuffer.toString();
		return this.data;
	}

	/**
	 * 通过字符串设置Data，如果为空，则清空已有的Data
	 * 
	 * @param data
	 *            要设置的 data
	 */
	public void setData(String data) {
		this.data = data;
		// if (NullValid.isNull(data)) {
		// paramsMap.clear();
		// return;
		// }
		// String[] line = data.split("&");
		// for (int i = 0; i < line.length; i++) {
		// String[] item = line[i].split("=");
		// if (item.length == 2) {
		// paramsMap.put(item[0], item[1]);
		// }
		// }
	}

	/**
	 * @return referer
	 */
	public String getReferer() {
		return sendHeader.get("Referer");
	}

	/**
	 * @param referer
	 *            要设置的 referer
	 */
	public void setReferer(String referer) {
		sendHeader.put("Referer", referer);
	}

	/**
	 * @return userAgent
	 */
	public String getUserAgent() {
		return sendHeader.get("User-Agent");
	}

	/**
	 * @param userAgent
	 *            要设置的 userAgent
	 */
	public void setUserAgent(String userAgent) {
		sendHeader.put("User-Agent", userAgent);
	}

	public void setHeader(String key, String value) {
		sendHeader.put(key.toLowerCase(), value);
	}

	public String getHeader(String key) {
		return sendHeader.get(key);
	}

	/**
	 * @return sendHeader
	 */
	public Map<String, String> getSendHeader() {
		return sendHeader;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setAjax(boolean isAjax) {
		if (isAjax) {
			sendHeader.put("X-Requested-With", "XMLHttpRequest");
		} else {
			sendHeader.remove("X-Requested-With");
		}

	}

	public boolean isAjax() {
		String ajax = sendHeader.get("X-Requested-With");
		if (ajax != null && ajax.equals("XMLHttpRequest")) {
			return true;
		}
		return false;
	}

	public void setDefaultAccept() {
		sendHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	}

	public void setJsonAccept() {
		sendHeader.put("Accept", "application/json, text/javascript, */*; q=0.01");
	}

	/*
	 * public Map<String, Object> getParamsMap() { return paramsMap; }
	 * 
	 * public void setParamsMap(Map<String, Object> paramsMap) { this.paramsMap
	 * = paramsMap; }
	 */
}
