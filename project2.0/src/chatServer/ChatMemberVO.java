package chatServer;
// 외원 정보 조회 VO
public class ChatMemberVO {
	private String id;
	private String pw;

	public ChatMemberVO() {

	}

	// 완전히 데이터 '전달' 용도로만 사용하기 때문에 getter / setter로직만이 필요하지 다른 로직이 필요치않다
	public ChatMemberVO(String id, String pw) {
		super();
		this.id = id;
		this.pw = pw;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

}
