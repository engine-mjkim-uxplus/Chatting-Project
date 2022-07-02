package chatClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import chatServer.MsgVO;
import chatServer.Protocol;

// 통신용 쓰레드 클래스 < 서버의 말을 듣는 역할을 한다 >
public class TalkClientThread extends Thread {
	ChatView chatView = null;
	TalkClient tc = null;
	List<PrivateChat> prlist = null; // 개인톡방 리스트
	LoginDao loginDao = null;
	boolean isStop;

	public TalkClientThread(TalkClient tc) {
		this.chatView = tc.chatView;
		this.tc = tc;
		this.prlist = new ArrayList<>();
		
	}

	/*
	 * 서버에서 말한 내용을 들어봅시다.
	 */
	// run()메소드 즉 클라이언트 쓰레드는 Client 뷰에서 작성된 메시지가 서버로 보내지고
	// 서버에서 ois즉 ObjectInputStream이 readObject()로 뷰에서 메시지가 전달되기까지 기다렸다가 메시지 받으면
	// 지금 이 쓰레드로 ObjectWriter를 사용하여 메시지를 전달하고
	// 이 쓰레드는 swicth문에서 전달 받은 프로토콜에 맞는 메시지가 사용자 화면에 출력된다.

	public void run() {
		isStop = false;
		MsgVO mvo = new MsgVO();
		String msg = null;
		int protocol = 0;
		run_start: while (!isStop) {
			try {
				mvo = (MsgVO) tc.ois.readObject(); // 톡 서버쓰레드에서 넘어오는 메시지 기다리는중..
				protocol = mvo.getProtocol(); // 프로토콜 읽어 들임
				switch (protocol) {
				case Protocol.ADMISSION: {// 100#apple
					String nickName = mvo.getNickname();
					chatView.jta_display.append(nickName + "님이 입장하였습니다.\n");
					System.out.println(nickName + "님이 입장하였습니다");
					Vector<String> v = new Vector<>(); // 백터에 현재 접속한 닉네임을 담는다.
					v.add(nickName);
					chatView.dtm.addRow(v); /// 접속인원 보여주는 dtm에 닉네임 추가
				}
					break;

				// 방생성 요청
				case Protocol.ROOM_CREATE: {
					String nickName = mvo.getNickname(); // name은 요청한 사람의 닉네임
					int result = chatView.private_chat(nickName);
					if (result == 1) {
						msg = "수락";
						tc.roomCreate_response(nickName, msg);
					} else {
						msg = "거절";
						tc.roomCreate_response(nickName, msg);
					}
				}
					break;
				// 방생성 요청에 대한 응답
				case Protocol.ROOM_ACCEPT: {
					loginDao = new LoginDao();
					msg = mvo.getMsg();
					int isroomNum = mvo.getIsroomNum(); // 대화방 유무, 있을 경우 방번호로 사용
					int roomNum = mvo.getRoomNum();
					String nickName = mvo.getNickname(); // 본인 닉네임
					String otnickName = mvo.getOtNickName(); // 다른 사람 닉네임
					System.out.println(roomNum + "번을(rnum) 서버에서 전달 받았습니다");
					System.out.println(isroomNum + "번을(isnum)서버에서 전달 받았습니다");
					if (msg.equals("수락")) {
						// 대화방이 없을 경우
						if (isroomNum == 0) {
							System.out.println(roomNum + "번 방의 뷰를 실행합니다");
							System.out.println("받은 닉네임은 " + nickName);
							PrivateChat pc = new PrivateChat(this, roomNum, nickName, otnickName);
							prlist.add(pc);
						}
						// 대화방이 있을 경우
						else if (isroomNum != 0) {
							List<MsgVO> list = null;
							// 대화방이 있을경우 isroomNum은 0이 아닌 이미 존재하는 룸번호이다
							PrivateChat pc = new PrivateChat(this, isroomNum, nickName, otnickName);
							list = loginDao.prchatBring(isroomNum); // 대화내용 불러오기
							prlist.add(pc);
							for (int i = 0; i < list.size(); i++) {
								String message = list.get(i).getMsg();
								pc.jta_display.append(message + "\n");
								pc.jta_display.setCaretPosition(pc.jta_display.getDocument().getLength());
							}
							System.out.println(list.size());
						}
					} else {
						chatView.errorMsg("상대방이 요청을 거절하였습니다.");
					}

				}
					break;

				// 개인 채팅보내기
				case Protocol.MESSAGE: {
					String nickName = mvo.getNickname();
					String message = mvo.getMsg();
					int roomNum = mvo.getRoomNum();
					for (PrivateChat prc : prlist) {
						if (prc.getRoomNum() == roomNum) {
							prc.jta_display.append("[" + nickName + "]" + message + "\n");
							prc.jta_display.setCaretPosition(prc.jta_display.getDocument().getLength());
						}
					}

				}
					break;
				// 채팅보내기 (프로토콜 201)
				case Protocol.GROUP_MESSAGE: {
					String nickName = mvo.getNickname();
					String message = mvo.getMsg();
					chatView.jta_display.append("[" + nickName + "]" + message + "\n");
					chatView.jta_display.setCaretPosition(chatView.jta_display.getDocument().getLength());
				}
					break;
				// 대화명변경 (프로토콜 202)
				case Protocol.NICKNAME_CHANGE: {
					String nickName = mvo.getNickname();
					String afterName = mvo.getAfter_nickname();
					String message = mvo.getMsg();
					// 테이블에 대화명 변경하기
					for (int i = 0; i < chatView.dtm.getRowCount(); i++) {
						String imsi = (String) chatView.dtm.getValueAt(i, 0);
						if (nickName.equals(imsi)) {
							chatView.dtm.setValueAt(afterName, i, 0);
							break;
						}
					}
					// 채팅창에 타이틀바에도 대화명을 변경처리 한다.
					if (nickName.equals(tc.nickName)) {
						chatView.setTitle(afterName + "님의 대화창");
						chatView.nickName = afterName;
					}
					chatView.jta_display.append(message + "\n");
				}
					break;
				// 서버에서 공지사항 보냄(프로토콜 203)
				case Protocol.NOTICE: {
					String nickName = mvo.getNickname();
					String notice = mvo.getMsg();
					String n = "[" + nickName + "]" + notice;
					chatView.jta_display.setCaretPosition(chatView.jta_display.getDocument().getLength());
					chatView.successMsg(n);
				}
					break;
				// 개인 대화방 나감
				case Protocol.PRROOM_OUT: {
					int roomNum = mvo.getRoomNum();
					String message = mvo.getMsg(); // "~님이 퇴장하셨습니다"
					for (PrivateChat pr : prlist) {
						if (pr.getRoomNum() == roomNum) {
							pr.errorMsg(message);
							prlist.remove(pr);
							pr.dispose();
							break;
						}
					}
				}
					break;
				// 단톡 대화방 나감
				case Protocol.ROOM_OUT: {
					String nickName = mvo.getNickname();
					msg = mvo.getMsg();
					chatView.jta_display.append(msg);
					chatView.jta_display.setCaretPosition(chatView.jta_display.getDocument().getLength());
					for (int i = 0; i < chatView.dtm.getRowCount(); i++) {
						String n = (String) chatView.dtm.getValueAt(i, 0);
						if (n.equals(nickName)) {
							chatView.dtm.removeRow(i); // 나가면 dtm(접속인원)에서 제거
						}
					}
				}
					break run_start; // 해당자원 반납

				// 운영자에 의해 강제퇴장 당했을 경우
				case Protocol.EXPULSION: {
					if (prlist.size() != 0) {
						for (PrivateChat pc : prlist) {
							String otnickName = pc.otNickName;
							int roomnum = pc.getRoomNum();
							tc.prRoomOut(otnickName, roomnum);
							pc.dispose();
						}	
							tc.expulsion();
						} else {
							tc.expulsion();
						}
				} break;
					
				case Protocol.EXPULSION_RESPONSE:{
					String nickName = mvo.getNickname();
					msg = mvo.getMsg(); // ~님이 강퇴 당하셨습니다
					// 같은 닉네임이면 종료
					if (tc.nickName.equals(nickName)) {
						chatView.successMsg("운영자에 의해 강퇴되었습니다");	
						chatView.dispose();
						break run_start;
					} else { // 다른 닉네임이면 강퇴 당한 아이디 채팅창에 그리고 대화목록에서 삭제
						chatView.jta_display.append(msg + "\n");
						chatView.jta_display.setCaretPosition(chatView.jta_display.getDocument().getLength());
						for (int i = 0; i < chatView.dtm.getRowCount(); i++) {
							String n = (String) chatView.dtm.getValueAt(i, 0);
							if (n.equals(nickName)) {
								chatView.dtm.removeRow(i); // 나가면 dtm(접속인원)에서 제거
							}
						}
					}
				} break;

				}//////////// end of switch
			} catch (Exception e) {
				e.printStackTrace();
			}
		} //////////////////// end of while
}}
