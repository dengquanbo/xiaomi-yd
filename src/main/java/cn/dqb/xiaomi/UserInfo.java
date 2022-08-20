package cn.dqb.xiaomi;

import lombok.Data;

@Data
public class UserInfo {

	private String loginToken;

	private String userId;

	public UserInfo(String loginToken, String userId) {
		this.loginToken = loginToken;
		this.userId = userId;
	}
}
