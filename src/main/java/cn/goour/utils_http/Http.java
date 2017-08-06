package cn.goour.utils_http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import cn.goour.utils_http.io.FileUtils;
import cn.goour.utils_http.io.IO;
import cn.goour.utils_http.tools.FileMimeType;

public class Http {
	public static void main(String[] args) {
		// System.out.println();
		// File f = new
		// File("D:\\workspace\\eclipseJeeNeonWorkspace\\yiban-app\\target\\classes\\pic");
		// File[] files = f.listFiles();
		//// System.out.println(f.getAbsolutePath());
		//// System.out.println(Arrays.toString(files));
		// Map<String, Object> mapObj = new HashMap<String, Object>();
		// Map<String, File[]> mapFiles = new HashMap<String, File[]>();
		// mapObj.put("a", 1);
		// mapObj.put("we", "sd");
		// File[] files2 = new File[3];
		// files2[0]=files[0];
		// files2[1]=files[1];
		// files2[2]=files[2];
		// mapFiles.put("ac[]", files2);

		HttpConfig config = new HttpConfig("http://114.215.91.232/lsHelper/admin/");
		config = new HttpConfig("http://127.0.0.1/test/server.php");
		config = new HttpConfig("http://www.baidu.com");
		// config = new HttpConfig("https://goour.cn/server.php?sd=dsdsd");
		config.setHeader("cookie", "asd=2323");
		try {
			byte[] aa = get(config);
			 System.out.println(new String(aa));

			// byte[] re = post(config,mapObj,mapFiles);
			// System.out.println(config.getBackHeaderCookieString());
			// String reString = new String(re,"utf-8");
			// System.out.println(reString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * HTTP GET请求
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static byte[] get(HttpConfig config) throws Exception {
		byte[] content = null;
		String url = config.getUrl();
		if (config.getData() != null && !config.getData().trim().equals("")) {
			url += "?" + config.getData();
		}
		URL realUrl = new URL(url);

		HttpURLConnection conn = getHttpURLConnection(realUrl);

		setHeader(conn, config);
		conn.setRequestMethod("GET");

		conn.setInstanceFollowRedirects(false);
		conn.setUseCaches(false);
		conn.connect();

		InputStream in = null;

		in = goLocation(conn, config);

		content = IO.read(in);
		in.close();
		conn.disconnect();
		return content;
	}

	/**
	 * HTTP POST一般请求
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static byte[] post(HttpConfig config) throws Exception {
		byte[] content = null;
		String url = config.getUrl();
		URL realUrl = new URL(url);
		HttpURLConnection conn = getHttpURLConnection(realUrl);

		setHeader(conn, config);
		conn.setRequestMethod("POST");

		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);

		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(config.getData());
		out.flush();
		out.close();

		InputStream in = null;

		in = goLocation(conn, config);

		content = IO.read(in);

		in.close();
		conn.disconnect();

		return content;
	}

	/**
	 * HTTP DELETE请求
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static byte[] delete(HttpConfig config) throws Exception {
		byte[] content = null;
		String url = config.getUrl();
		URL realUrl = new URL(url);
		HttpURLConnection conn = getHttpURLConnection(realUrl);

		setHeader(conn, config);
		conn.setRequestMethod("DELETE");

		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);

		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print(config.getData());
		out.flush();
		out.close();

		InputStream in = null;

		in = goLocation(conn, config);

		content = IO.read(in);

		in.close();
		conn.disconnect();

		return content;
	}

	/**
	 * 不建议使用该方法，如需上传文件，请使用post(HttpConfig config, Map<String, Object> mapObj,
	 * Map<String, File[]> mapFiles)方法， 把第二参数置为null
	 * 
	 * @param config
	 * @param files
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static byte[] postFile(HttpConfig config, File[] files, String key) throws Exception {
		byte[] content = null;
		String url = config.getUrl();
		URL realUrl = new URL(url);
		HttpURLConnection conn = getHttpURLConnection(realUrl);

		String boundary = "--------" + System.currentTimeMillis();

		setHeader(conn, config);
		conn.setRequestMethod("POST");

		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);

		OutputStream out1 = new DataOutputStream(conn.getOutputStream());
		String sendDataHeader = "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n\r\n--" + boundary
				+ "\r\n";
		out1.write(sendDataHeader.getBytes());

		byte[] end = ("\r\n--" + boundary + "\r\n").getBytes();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			byte[] fileBytes = FileUtils.read(file);
			byte[] head = ("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName()
					+ "\"\r\n" + "Content-Type: " + FileMimeType.getFileMimeType(file) + "\r\n\r\n").getBytes();
			if (i >= files.length - 1) {
				end = ("\r\n--" + boundary + "--\r\n").getBytes();
			}
			out1.write(head);
			out1.write(fileBytes);
			out1.write(end);
			out1.flush();
		}
		out1.flush();
		out1.close();

		InputStream in = null;

		in = goLocation(conn, config);

		content = IO.read(in);

		in.close();
		conn.disconnect();
		return content;
	}

	/**
	 * HTTP POST请求，文件上传请求
	 * 
	 * @param config
	 * @param mapObj
	 *            表单信息，可为null
	 * @param mapFiles
	 *            文件信息，可为null
	 * @return
	 * @throws Exception
	 */
	public static byte[] post(HttpConfig config, Map<String, Object> mapObj, Map<String, File[]> mapFiles)
			throws Exception {
		byte[] content = null;
		String url = config.getUrl();
		URL realUrl = new URL(url);
		HttpURLConnection conn = getHttpURLConnection(realUrl);

		String boundary1 = "--------" + System.currentTimeMillis();
		String boundary2 = "--" + boundary1;

		setHeader(conn, config);
		conn.setRequestMethod("POST");

		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary1);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);

		OutputStream out1 = new DataOutputStream(conn.getOutputStream());
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		String sendDataHeader = "Content-Type: multipart/form-data; boundary=" + boundary1 + "\r\n";
		bytes.write(sendDataHeader.getBytes());

		byte[] start = boundary2.getBytes();
		byte[] brn = "\r\n".getBytes();
		if (mapObj != null) {
			for (Entry<String, Object> item : mapObj.entrySet()) {
				String key = item.getKey();
				byte[] head = ("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n\r\n").getBytes();
				byte[] body = item.getValue().toString().getBytes();
				bytes.write(brn);
				bytes.write(start);
				bytes.write(brn);
				bytes.write(head);
				bytes.write(body);
			}
		}
		if (mapFiles != null) {
			for (Entry<String, File[]> item : mapFiles.entrySet()) {
				String key = item.getKey();
				File[] files = item.getValue();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					byte[] body = FileUtils.read(file);
					byte[] head = ("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getName()
							+ "\"\r\n" + "Content-Type: " + FileMimeType.getFileMimeType(file) + "\r\n\r\n").getBytes();
					bytes.write(brn);
					bytes.write(start);
					bytes.write(brn);
					bytes.write(head);
					bytes.write(body);
				}
			}
		}

		bytes.write(brn);
		bytes.write(start);
		bytes.write("--".getBytes());
		bytes.write(brn);

		out1.write(bytes.toByteArray());
		out1.flush();
		out1.close();

		InputStream in = null;

		in = goLocation(conn, config);

		content = IO.read(in);
		bytes.close();
		in.close();
		conn.disconnect();
		return content;
	}

	public static void showHeaders(Map<String, List<String>> map) {
		// for (String key : map.keySet()) {
		// System.out.println(key + "--->" + map.get(key));
		// }
	}

	public static void showURL(URL url) {
		// System.out.println("HTTP Host :" + url.getHost());
		// System.out.println("HTTP Url :" + url.getProtocol() + "://" +
		// url.getHost() + ":"
		// + (url.getPort() == -1 ? url.getDefaultPort() : url.getPort()) +
		// url.getPath());
		// System.out.println("HTTP Query:" + url.getQuery());
	}

	/**
	 * 执行重定向操作
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static HttpURLConnection getLocation(HttpConfig config) throws Exception {
		String url = config.getUrl();
		URL realUrl = new URL(url);
		HttpURLConnection conn = getHttpURLConnection(realUrl);

		setHeader(conn, config);

		conn.setInstanceFollowRedirects(false);
		conn.setUseCaches(false);
		conn.connect();
		return conn;
	}

	/**
	 * 设置发送头部信息
	 * 
	 * @param conn
	 * @param config
	 */
	public static void setHeader(HttpURLConnection conn, HttpConfig config) {
		conn.setConnectTimeout(config.getConnectTimeout());
		conn.setReadTimeout(config.getReadTimeout());
		// 设置头部信息
		for (Entry<String, String> header : config.getSendHeader().entrySet()) {
			conn.setRequestProperty(header.getKey(), header.getValue());
		}

		String cookieString = config.getHeader("cookie");
		if (cookieString == null || cookieString.trim().equals("")) {
			cookieString = "";
		}
		// 复用COOKIE信息，获取到上次http访问得到的COOKIE信息，再加入新的COOKIE
		conn.setRequestProperty("Cookie", config.getBackHeaderCookieString() + cookieString);

	}

	/**
	 * 得到相对地址，在页面重定向的时候要用到
	 * 
	 * @param absolutePath
	 * @param relativePath
	 * @return
	 */
	public static String getAbsUrl(String absolutePath, String relativePath) {
		try {
			URL absoluteUrl = new URL(absolutePath);
			URL parseUrl = new URL(absoluteUrl, relativePath);
			return parseUrl.toString();
		} catch (MalformedURLException e) {
			return "";
		}
	}

	/**
	 * 使用此方法解决多次重定向问题
	 * 
	 * @param conn
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static InputStream goLocation(HttpURLConnection conn, HttpConfig config) throws Exception {
		InputStream in = conn.getInputStream();
		config.setBackHeader(conn.getHeaderFields());

		showHeaders(conn.getHeaderFields());

		int i = 0;
		while (conn.getHeaderField("Location") != null) {
			String locationUrl = conn.getHeaderField("Location");
			locationUrl = getAbsUrl(config.getUrl(), locationUrl);// .replaceAll("^https",
			// "http");

			in.close();// 关闭上一个输入流
			conn.disconnect();

			config.setUrl(locationUrl);
			conn = Http.getLocation(config);

			in = conn.getInputStream();
			config.setBackHeader(conn.getHeaderFields());

			showHeaders(conn.getHeaderFields());

			if (++i > 15) {
				throw new IOException("重定向次数太多！");
			}
		}
		return in;
	}

	public static boolean isHttps(URL url) {
		return url.getProtocol().equals("https");
	}

	/**
	 * 以HTTPS协议初始化请求
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static HttpURLConnection initHttps(URL url) throws IOException, Exception {
		URLConnection conn1 = url.openConnection();
		HttpsURLConnection conn = (HttpsURLConnection) conn1;

		// Certificate[] list = conn.getServerCertificates();
		// for (int i = 0; i < list.length; i++) {
		// Certificate cert=list[i];
		// }
		//
		// 设置域名校验
		conn.setHostnameVerifier(new MyHostnameVerifier());
		conn.setSSLSocketFactory(MyX509TrustManager.getSocketFactory());

		return conn;
	}

	/**
	 * 以HTTP协议初始化请求
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection initHttp(URL url) throws IOException {
		URLConnection conn1 = url.openConnection();
		HttpURLConnection conn = (HttpURLConnection) conn1;
		return conn;
	}

	/**
	 * 传入URL返回请求对象
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static HttpURLConnection getHttpURLConnection(URL url) throws Exception {
		showURL(url);
		HttpURLConnection conn = null;
		if (isHttps(url)) {
			conn = initHttps(url);
		} else {
			conn = initHttp(url);
		}
		return conn;
	}
}
