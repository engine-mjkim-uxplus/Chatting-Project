package chatClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import chatServer.MsgVO;
import chatServer.Protocol;

// 로그인 후 단톡 채팅방 UI 및 클라이언트 소켓 생성클래스
// 클라이언트와 서버와의 통신용만으로 쓸 것임
// 통신용 컨트롤러 역할
public class TalkClient  {
	//////////////// 통신과 관련한 전역변수 추가 시작//////////////
	Socket socket = null;
	ObjectOutputStream oos = null;// 말 하고 싶을 때
	ObjectInputStream ois = null;// 듣기 할 때
	String nickName = null;// 닉네임 등록
	ChatView chatView = null;
	//////////////// 통신과 관련한 전역변수 추가 끝 //////////////
	TalkClientThread tct = null;
	
	public TalkClient(ChatView chatView, String nickName) {
		this.chatView = chatView;
		this.nickName = nickName;
	}

	/***********************************************************************************
	 * init() 메소드 소켓 관련 초기화(클라이언트 입장) 클라이언트가 로그인한 후 init()메소드 실행되고 바인드된 서버 주소에 연결 되어
	 * socket을 생성한다. 이 socket은 서버와 클라이언트가 통신하는 데 쓰이게 됩니다
	 ***********************************************************************************/
	public void init() { /// 닉네임 결정된 후 서버랑 연결요청한다.
		try {
			// 서버측의 ip주소 작성하기
			socket = new Socket("127.0.0.1", 3002);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			// initDisplay에서 닉네임이 결정된 후 init메소드가 호출되므로
			// 서버에게 내가 입장한 사실을 알린다.(말하기)
			MsgVO mvo = new MsgVO();
			mvo.setProtocol(Protocol.ADMISSION);
			mvo.setNickname(nickName);
			oos.writeObject(mvo);
			this.tct = new TalkClientThread(this);
			this.tct.start();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	
	/*************************************************************************
	 * 밑의 메소드들은 ChatView에서의 이벤트에대한 이벤트 처리하는 메소드들 즉 클라이언트에서 서버로 말하는 것을 MsgVO에 담아 전달하는
	 * 역할을 합니다
	 * 
	 * @param msg : ChatView에서 사용자가 텍스트에 입력한 메시지
	 **************************************************************************/

	// 단체 톡방 메시지 보내기
	public void groupMsg(String msg) {
		try {
			MsgVO mvo = new MsgVO();
			mvo.setNickname(nickName);
			mvo.setMsg(msg);
			mvo.setProtocol(Protocol.GROUP_MESSAGE);
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.toString();
		}
	}
	
	// 개인 톡방 메시지 보내기
	public void privateMsg(String msg, int roomNum, String otNickname) {
		try {
			MsgVO mvo = new MsgVO();
			mvo.setMsg(msg);
			mvo.setRoomNum(roomNum);
			mvo.setNickname(nickName);
			mvo.setOtNickName(otNickname); // 받는사람
			mvo.setProtocol(Protocol.MESSAGE);
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.toString();
		}
	}

	// 단체 대화방 퇴장 시(Protocol.ROOM_OUT)
	public void roomOut() {
		// 현재 대화중인 개인 대화방 종료
		if(tct.prlist.size() != 0) {
			for(PrivateChat pc : tct.prlist) {
				String otnickName= pc.otNickName;
				int roomnum = pc.getRoomNum();
				prRoomOut(otnickName, roomnum);
			}
		}
		try {
			MsgVO mvo = new MsgVO();
			mvo.setNickname(nickName);
			mvo.setProtocol(Protocol.ROOM_OUT);
			mvo.setMsg(nickName + "님이 퇴장하셨습니다.");
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tct.isStop = true; // 쓰레드 종료되도록( ★자원 반납★ ) /////////테스트중
	}
	// 개인 대화방 퇴장 시
	public void prRoomOut(String otnickName, int roomnum) {
		try {
			MsgVO mvo = new MsgVO();
			mvo.setNickname(nickName);
			mvo.setOtNickName(otnickName);
			mvo.setRoomNum(roomnum);
			mvo.setMsg(nickName + "님이 퇴장하셨습니다");
			mvo.setProtocol(Protocol.PRROOM_OUT);
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 대화방 생성
	public void roomCreate(String nickName) {
		try {
			MsgVO mvo = new MsgVO();
			mvo.setNickname(nickName); // 받는 사람 이름 세팅
			mvo.setProtocol(Protocol.ROOM_CREATE);
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 강퇴 시 개인 화면 닫기
	public void expulsion() {
		try {
			MsgVO mvo = new MsgVO();
			mvo.setNickname(nickName);
			mvo.setMsg("운영자가 " +nickName + "님을 강퇴하였습니다");
			mvo.setProtocol(Protocol.EXPULSION_RESPONSE);
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 닉네임 변경
	public void changeNickName(String afterName) {
		try {
			MsgVO mvo = new MsgVO();
			mvo.setProtocol(Protocol.NICKNAME_CHANGE);
			mvo.setNickname(nickName);
			mvo.setAfter_nickname(afterName);
			mvo.setMsg(nickName + "님의 대화명이 " + afterName + "으로 변경되었습니다");
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//  대화방 요청에 대한 응답
	public void roomCreate_response(String nickName, String msg) {
		try {
			MsgVO mvo = new MsgVO();
			mvo.setOtNickName(nickName); // 대화 요청한 사람의 닉네임
			mvo.setMsg(msg);		     // 수락 || 거절
			mvo.setProtocol(Protocol.ROOM_ACCEPT);
			oos.writeObject(mvo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 개인 대화방 열려 있는지 체크
	public boolean isRoom(String otnickName) {
		boolean isRoom = true;
		
		for(PrivateChat pr :tct.prlist) {
			if(otnickName.equals(pr.otNickName)) {
				return false;
			} 
		}
		return isRoom;
	}

}
