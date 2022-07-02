package chatServer;

import java.io.Serializable;

public class MsgVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int protocol;
	private String nickname;   		// 나의 닉네임
	private String otNickName; 		// 받는 사람 닉네임
	private String after_nickname;
	private String msg;
	private int    roomNum = 0;
	private int    isroomNum = 0;
	
	public MsgVO() {
		
	}
	
	public MsgVO(String chatmsg) {
		this.msg = chatmsg;
	}
	
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
	public int getRoomNum() {
		return roomNum;
	}
	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public int getIsroomNum() {
		return isroomNum;
	}

	public void setIsroomNum(int isroomNum) {
		this.isroomNum = isroomNum;
	}

	public String getOtNickName() {
		return otNickName;
	}

	public void setOtNickName(String otNickName) {
		this.otNickName = otNickName;
	}
	
	
	
}
