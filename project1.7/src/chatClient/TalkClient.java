package chatClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// 로그인 후 단톡 채팅방 UI 및 클라이언트 소켓 생성클래스
public class TalkClient {
	//////////////// 통신과 관련한 전역변수 추가 시작//////////////
	Socket socket = null;
	ObjectOutputStream oos = null;// 말 하고 싶을 때
	ObjectInputStream ois = null;// 듣기 할 때
	String nickName = null;// 닉네임 등록
	//////////////// 통신과 관련한 전역변수 추가 끝 //////////////

	LoginDao logindao = new LoginDao();
	
	private TalkClient() {}
	
	private static TalkClient instance;
	
	public static TalkClient getInstance() {
		if ( instance == null ) {
			instance = new TalkClient();
		}
		return instance;
	}
	
	// 소켓 관련 초기화
	public void init(String nickName) { /// 닉네임 결정된 후 서버랑 연결요청한다.
		try {
			// 서버측의 ip주소 작성하기
			socket = new Socket("127.0.0.1", 3002);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			// initDisplay에서 닉네임이 결정된 후 init메소드가 호출되므로
			// 서버에게 내가 입장한 사실을 알린다.(말하기)
			send(100,nickName);// 100은 Integer객체이므로 ""안써도됨 #만 구분자로 ""붙인다
			// 서버에 말을 한 후 들을 준비를 한다.
		} catch (Exception e) {
			// 예외가 발생했을 때 직접적인 원인되는 클래스명 출력하기
			System.out.println(e.toString());
		}
	}

	//로그인 성공시 1반환, 비밀번호 불일치 0반환, 아이디 없을 경우 -1반환
	public String loginCheck(String id, String pw) {
		String result = "";
		result = logindao.login(id, pw);
		return result;
	}
	
	public int idCheck(String id) {
		int result = 0;
		result = logindao.idCheck(id);
		return result;
	}

	public void signUp(String id, String pw, String name) {
		logindao.signup(id, pw, name);
	}
	
	public void setnickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void changeNickName(String afterName) {
		try {
			oos.writeObject(202 + "#" + nickName + "#" + afterName + "#" + nickName + "의 대화명이 " + afterName + "으로 변경되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nickName = afterName;
	}
	
	public void send(int protocol, String msg) {
		try {
			oos.writeObject(protocol + "#" + nickName + "#" + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
