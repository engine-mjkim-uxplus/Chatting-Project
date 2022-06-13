package chatServer;
// 단톡 전체 메시지 백업용 VO클래스
public class ChatMsgVO {
	private String chatmsg;
	private String nicname;
	private String days; 
	private String hours;
	
	public ChatMsgVO() {
		
	}
	
	
	public ChatMsgVO(String chatmsg, String nicname, String days, String hours) {
		super();
		this.chatmsg = chatmsg;
		this.nicname = nicname;
		this.days = days;
		this.hours = hours;
	}

	public String getChatmsg() {
		return chatmsg;
	}

	public void setChatmsg(String chatmsg) {
		this.chatmsg = chatmsg;
	}

	public String getNicname() {
		return nicname;
	}

	public void setNicname(String nicname) {
		this.nicname = nicname;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	} 
	
	
	
	
}
