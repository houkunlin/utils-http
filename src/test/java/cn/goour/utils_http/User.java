package cn.goour.utils_http;

public class User {
	private String account;
	private String password;
	public User() {
		this.account = "13207725244";
		this.password = "houkunlin0123456";
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "User [account=" + account + ", password=" + password + "]";
	}
	
}
