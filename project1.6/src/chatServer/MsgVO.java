package chatServer;

import java.io.Serializable;

public class MsgVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int protocol;
	private String nickname;
	private String after_nickname;
	private String msg;
	
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAfter_nickname() {
		return after_nickname;
	}
	public void setAfter_nickname(String after_nickname) {
		this.after_nickname = after_nickname;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	
}
