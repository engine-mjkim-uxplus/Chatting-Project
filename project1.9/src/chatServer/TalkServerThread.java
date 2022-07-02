package chatServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;


public class TalkServerThread extends Thread {
	SocketThread   		  sk	 			= null;
	TalkServerView        view          	= null;
	Socket 				  client 			= null;
	ObjectOutputStream    oos   			= null; 
	ObjectInputStream     ois    			= null; 
	String 			      nickName 			= null; // 현재 서버에 접속한 클라이언트 스레드 닉네임 저장
	Vector<Object>		  oneRow			= null;
	MsgVO				  mvo				= null; // 입장에 대한 msg 가지고 있음
	ChatDao				  chatDao			= null;
	public TalkServerThread(SocketThread sk) {
		this.sk 	= sk;		 // 소켓쓰레드 주소값
		this.client = sk.socket; // 방금 접속한 클라이언트의 정보(ip,port)
		this.view   = sk.view;
		this.chatDao = new ChatDao(); 
		try {
			oos = new ObjectOutputStream(client.getOutputStream()); // client소켓으로부터 아웃풋스트림 얻음
			ois = new ObjectInputStream(client.getInputStream());   // client소켓으로부터 인풋스트림 얻음
			mvo = (MsgVO)ois.readObject(); 				// 사용자 nickName(JoptionPane) 읽어들임
			nickName = mvo.getNickname(); 
			String days = sk.getDate();
			String hours = sk.getTime();
			view.jta_log.append("[" + days + hours + "]" 
								    +nickName + "님이 입장하였습니다.\n"); // 서버에 찍음
			for (TalkServerThread tst : sk.globalList) {
				this.send(tst.mvo); // mvo에 프로토콜 100과 nickname 담겨있다
			}						// 방금 접속한 사용자에게 이전 접속자들 접속했다고 화면에 뛰운다

			// 현재 접속한 클라이언트의 닉네임, ip, 접속시간을 현재접속인원 창에 추가한다
			InetAddress ip = client.getInetAddress();
			String time    = sk.getDate() + sk.getTime();
			oneRow  = new Vector<>();
			oneRow.add(nickName);
			oneRow.add(ip);
			oneRow.add(time);
			view.dtm.addRow(oneRow);

			sk.globalList.add(this);// 현재 서버에 입장한 클라이언트 스레드 추가하기
			this.broadCasting(mvo); // ※※※ 현재접속자를 포함한 각 클라이언트 쓰레드에게 접속하였다고 여기서 전달 ※※※
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	// 현재 입장해 있는 친구들 모두에게 메시지 전송하기
	public void broadCasting(MsgVO mvo) {
		for (TalkServerThread tst : sk.globalList) {
			tst.send(mvo); // 각 클라이언트와 소통하는 소켓들이 클라이언트 쓰레드에게 메시지 보낸다
		}
	}

	// 클라이언트에게 말하기 구현
	public void send(MsgVO mvo) {
		try {
			oos.writeObject(mvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 클라이언트가 말하는 것을 듣는 역할. TalkClient가 말한 것을 듣고 TalkClientThread에게 보내는 역할
	public void run() {
		MsgVO mvo = new MsgVO();
		boolean isStop = false;
		int protocol = 0;
		try {
			run_start: while (!isStop) { 
				mvo = (MsgVO) ois.readObject(); // 사용자에게 입력 받을 때 까지 기다린다.				
				protocol = mvo.getProtocol();// 100 | 200 | 300 | 400 | 500
					
				switch (protocol) {
				
				// 개인 대화방 생성
				case Protocol.ROOM_CREATE:{
					String name  = mvo.getNickname(); // name은 신청 받는 사람의 이름이다
					for(TalkServerThread tst :sk.globalList ) {
						if(tst.nickName.equals(name)) {
							mvo.setNickname(this.nickName); // 보내는 사람
							tst.send(mvo);
							break;
						}
					}
				} break;	
				
				// 개인 대화방 요청 수락( 0: 방없음 ) 
				case Protocol.ROOM_ACCEPT:{
					String otnickName  = mvo.getOtNickName(); // 대화 요청한 사람의 닉네임
					int roomNum = 0;
					int isroomNum = 0;
					if(mvo.getMsg().equals("수락")) {
						// 방의 유무 조회
						isroomNum = chatDao.searchRoomNum(otnickName,nickName); 
						System.out.println("방의 유무는 "+isroomNum+ "번 입니다"); // 단위테스트
						//방이 없으면 방번호 생성해서 클라이언트 쓰레드에게 전달
						if(isroomNum == 0) {
							chatDao.createRoomNum(otnickName, nickName); 		 // 방생성
							roomNum = chatDao.searchRoomNum(otnickName,nickName);// 생성된 방번호 조회
							for(TalkServerThread tst :sk.globalList ) {
								if(tst.nickName.equals(otnickName)) { 
									mvo.setOtNickName(nickName); // 수락한 사람 닉네임
									mvo.setNickname(otnickName); // 자기자신
									mvo.setRoomNum(roomNum);   // 방의상태(유무)
									mvo.setIsroomNum(isroomNum);
									tst.send(mvo);
									break;
								}
							}
							mvo.setRoomNum(roomNum);
							mvo.setOtNickName(otnickName); // 수락한 사람 닉네임
							mvo.setNickname(nickName); // 자기자신
							mvo.setIsroomNum(isroomNum);
							send(mvo); // 자기 자신에게도 보낸다
						} // 방이 이미 있을 경우 
						else if(isroomNum != 0) {
							// 자기 자신에게 보내기
							mvo.setIsroomNum(isroomNum);
							send(mvo); 
							// 상대방에게 보내기
							for(TalkServerThread tst :sk.globalList ) {
								if(tst.nickName.equals(otnickName)) { 
									mvo.setOtNickName(nickName); // 수락한 사람 닉네임
									mvo.setNickname(otnickName); // 자기자신
									tst.send(mvo);
									break;
								}
							}
						}
					}	
					else if(mvo.getMsg().equals("거절")) {
						for(TalkServerThread tst :sk.globalList ) {
							if(tst.nickName.equals(otnickName)) {
								tst.send(mvo); // 상대방에게 거절했다고 알림
								break;
							}
						}	
					}
				} break;
				// 개인톡방 메시지 보내기
				case Protocol.MESSAGE: {
					String days = sk.getDate();
					String hours = sk.getTime();
					String message = "[" + nickName + "]"+ mvo.getMsg();
					int roomNum= mvo.getRoomNum();
					String otNickname  = mvo.getOtNickName();
					mvo.setNickname(nickName);
					System.out.println(roomNum + "번 방에서 서버로 메시지가 왔습니다");
					for(TalkServerThread tst :sk.globalList ) {
						if(tst.nickName.equals(otNickname)) {
							tst.send(mvo); // 상대방에게 보내기
							break;
						} 
					}
					send(mvo); // 자기 자신에게도 보낸다
					ChatMsgVO cvo = new ChatMsgVO(message, roomNum, days, hours);
					chatDao.prchatMsg(cvo); // 대화내용 DB에 저장
				}
				break;
				// 단체 대화 전달
				case Protocol.GROUP_MESSAGE: { // 채팅보냈을 때 (DB에 대화내용 저장)
					String message = mvo.getMsg();
					String nickName = mvo.getNickname();
					String days = sk.getDate();
					String hours = sk.getTime();
					broadCasting(mvo);
					ChatMsgVO cvo = new ChatMsgVO(message, nickName, days, hours);
					
					chatDao.chatMsg(cvo); // 대화내용 DB 테이블에 기록
				} break;
				// 대화명 변경에 대해 단체로 전달
				case Protocol.NICKNAME_CHANGE: {
					this.nickName = mvo.getAfter_nickname();
					broadCasting(mvo);
				} break;
				// 개인 대화방 퇴장 시
				case Protocol.PRROOM_OUT: {
					String otNickname= mvo.getOtNickName();
					for(TalkServerThread tst :sk.globalList ) {
						if(tst.nickName.equals(otNickname)) {
							tst.send(mvo); // 상대방에게 보내기
							break;
						} 
					}
				} break;		
				// 클라이언트 강퇴 시
				case Protocol.EXPULSION_RESPONSE:{
					broadCasting(mvo);
					sk.globalList.remove(this);
					sk.userCount();
				} break run_start; // 해당 자원 반납
				
				// 클라이언트 퇴장 시 (단체톡)
				case Protocol.ROOM_OUT: { 
					broadCasting(mvo);
					sk.globalList.remove(this); // 클라이언트 나갔으므로 통신 쓰레드 지움
					sk.showNumber_Conpeople();
					sk.userCount(); // 접속인원 JTexField 초기화 
				}
				break run_start; // 클라이언트 퇴장시 반복문 빠져나가면서 쓰레드 종료
				}// end of switch
			} // end of while
		} catch (Exception e) {
			e.printStackTrace();
		}
	}///////////////////////// end of run

}