package cn.goour.utils_http;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MyX509TrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
			throws CertificateException {

	}

	public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
			throws CertificateException {

	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	public static SSLSocketFactory getSocketFactory()
			throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		TrustManager[] tm = { new MyX509TrustManager() };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		return ssf;
	}
}