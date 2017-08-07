package cn.goour.utils_http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSONObject;

import cn.goour.utils_security.impl.Base64;
import cn.goour.utils_security.impl.RSAUtils;

public class WebYiBanSendFeeds {
	private static String cookie;// 存储COOKIE信息

	public static void main(String[] args) throws Exception {
		User user = new User();
		String re = login(user.getAccount(), user.getPassword());
		System.out.println("登录时返回的COOKIE信息："+cookie);
		re = sendFeeds("Test "+System.currentTimeMillis());
		
	}
	/**
	 * 发布易班个人动态
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String sendFeeds(String content) throws Exception{
		// 发送动态请求路径
		HttpConfig config = new HttpConfig("http://www.yiban.cn/feed/add");
		config.setAjax(true);
		// 页面来源
		config.setReferer("http://www.yiban.cn/my/publishfeed");
		// 这里是设置COOKIE信息，COOKIE信息采用登录时从服务器获取到的COOKIE信息，
		// 具体的可以稍候查看完整代码
		config.setHeader("cookie", cookie);
		Params params = new Params();
		params.add("content", content);
		params.add("privacy", 0);
		config.setData(params.getParams());
		byte[] re = Http.post(config);
		String jsonStr = new String(re);
		System.out.println(JSONObject.parseObject(jsonStr));
		return jsonStr;
	}
	/**
	 * 易班WEB端登陆操作
	 * @param account
	 * @param password
	 * @return 返回登录结果
	 * @throws Exception
	 */
	public static String login(String account,String password) throws Exception {
		// 首先获取到登陆页面的网页内容
		HttpConfig config = new HttpConfig("https://www.yiban.cn/login?go=http%3A%2F%2Fwww.yiban.cn%2F");
		byte[] re = Http.get(config);
		String html = new String(re);

		// 之后解析网页内容
		Document dom = Jsoup.parse(html);
		// 这里是获取到公钥所在位置的节点信息
		Element login_pr = dom.getElementById("login-pr");
		// 取出公钥信息
		String keys = login_pr.attr("data-keys");
		// 去除公钥信息的多余内容，
		String publicKey = keys.substring(26, keys.length() - 26).replaceAll("\n", "").replaceAll("\r", "");
		System.out.println("服务器返回的COOKIE信息：" + config.getBackHeaderCookieString());
		System.out.println("publicKey:" + publicKey);
		// 加密密码
		String password2 = enCode(password, publicKey);
		System.out.println("加密后的密码：" + password2);
		// 沿用上一次的访问配置信息，里面存有上一次访问时服务器返回的COOKIE信息
		config.setUrl("https://www.yiban.cn/login/doLoginAjax");
		// 设置参数
		Params params = new Params();
		params.add("account", account);
		params.add("password", password2);
		params.add("captcha", "");
		params.add("keysTime", login_pr.attr("data-keys-time"));
		// 把参数加入到访问配置里面
		config.setData(params.getParams());
		config.setAjax(true);// 设置ajax异步信息
		// 设置页面路径来源，之所以要设置，因为我的目的是完全模仿浏览器行为，你可以参照调试工具的请求信息按需设置
		config.setReferer("https://www.yiban.cn/login?go=http%3A%2F%2Fwww.yiban.cn%2F");
		System.out.println("发送的参数：" + config.getData());
		// 执行网络请求
		re = Http.post(config);
		// 把服务器返回的COOKIE信息保存下来，以便下次使用
		cookie = config.getBackHeaderCookieString();
		String jsonStr = new String(re);
		// 因为返回的是一个json格式字符串，而且其中汉字内容还是经过编码的，已我们的大脑看编码过后的内容时很难看得懂的
		// 所以只能把json字符串解析一下，之后再输入，解析后它会自动转换中文内容为我们可识别的汉字
		System.out.println(JSONObject.parseObject(jsonStr));
		return jsonStr;
	}

	/**
	 * 易班WEB端登陆，密码加密
	 * @param password
	 * @param publicKey
	 * @return
	 */
	public static String enCode(String password, String publicKey) {
		String re = null;
		try {
			byte[] bytes = RSAUtils.encryptData(password.getBytes(), Base64.getInstance().Decryption(publicKey));
			re = Base64.getInstance().EncryptionToString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}
}
