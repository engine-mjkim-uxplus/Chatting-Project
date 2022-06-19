package chatClient;
import java.util.Vector;

import chatServer.MsgVO;
import chatServer.Protocol;

// 통신용 쓰레드 클래스 < 서버의 말을 듣는 역할을 한다 >
public class TalkClientThread extends Thread {
	TalkClient tc = null; 

	public TalkClientThread(TalkClient tc) {
		this.tc = tc; //톡클라이언트의 주소값을 들고 온다
	}

	/*
	 * 서버에서 말한 내용을 들어봅시다.
	 */
	public void run() {
		boolean isStop = false;
		MsgVO mvo = new MsgVO();
		String msg = null;
		int protocol = 0;
		while (!isStop) {
			try {
				mvo = (MsgVO) tc.ois.readObject(); // 톡 서버쓰레드에서 넘어오는 메시지 기다리는중..
				protocol = mvo.getProtocol(); // 프로토콜 읽어 들임
				switch (protocol) {
				case Protocol.ADMISSION: {// 100#apple
					String nickName = mvo.getNickname();
					tc.jta_display.append(nickName + "님이 입장하였습니다.\n");
					System.out.println(nickName+"님이 입장하였습니다");
					Vector<String> v = new Vector<>(); // 백터에 현재 접속한 닉네임을 담는다.
					v.add(nickName);
					tc.dtm.addRow(v); /// 접속인원 보여주는 dtm에 닉네임 추가

				}
					break;
				case Protocol.MESSAGE: {

				}
					break;
				// 채팅보내기 (프로토콜 201)
				case Protocol.GROUP_MESSAGE: {
					String nickName = mvo.getNickname();
					String message = mvo.getMsg();
					tc.jta_display.append("[" + nickName + "]" + message + "\n");
					tc.jta_display.setCaretPosition(tc.jta_display.getDocument().getLength());
				}
					break;
				// 대화명변경 (프로토콜 202)
				case Protocol.NICKNAME_CHANGE: {
					String nickName = mvo.getNickname();
					String afterName = mvo.getAfter_nickname();
					String message = mvo.getMsg();
					// 테이블에 대화명 변경하기
					for (int i = 0; i < tc.dtm.getRowCount(); i++) {
						String imsi = (String) tc.dtm.getValueAt(i, 0);
						if (nickName.equals(imsi)) {
							tc.dtm.setValueAt(afterName, i, 0);
							break;
						}
					}
					// 채팅창에 타이틀바에도 대화명을 변경처리 한다.
					if (nickName.equals(tc.nickName)) {
						tc.setTitle(afterName + "님의 대화창");
						tc.nickName = afterName;
					}
					tc.jta_display.append(message + "\n");
				}
					break;
				// 서버에서 공지사항 보냄(프로토콜 203)
				case Protocol.NOTICE: {
					String nickName = mvo.getNickname();
					String notice = mvo.getMsg();
					String n = "[" + nickName + "]" + notice;
					tc.jta_display.setCaretPosition(tc.jta_display.getDocument().getLength());
					tc.showmsg_Info(n); 
				}
					break;
				// 클라이언트 나가기 누름 (프로토콜 500)
				case Protocol.ROOM_OUT: {
					String nickName = mvo.getNickname();
					msg 			= mvo.getMsg();
					tc.jta_display.append(msg);
					tc.jta_display.setCaretPosition(tc.jta_display.getDocument().getLength());
					for (int i = 0; i < tc.dtm.getRowCount(); i++) {
						String n = (String) tc.dtm.getValueAt(i, 0);
						if (n.equals(nickName)) {
							tc.dtm.removeRow(i); // 나가면 dtm(접속인원)에서 제거
						}
					}
				}
					break;
				// 운영자에 의해 강제퇴장 당했을 경우
				case Protocol.EXPULSION: {
					String nickName = mvo.getNickname();
					if (tc.nickName.equals(nickName)) { // 같은 닉네임이면 종료
						for (int i = 0; i < tc.dtm.getRowCount(); i++) {
							String n = (String) tc.dtm.getValueAt(i, 0);
						}
						tc.showmsg_expulsion();
						tc.dispose();
					} else { // 다른 닉네임이면 강퇴 당한 아이디 채팅창에 그리고 대화목록에서 삭제
						tc.jta_display.append(nickName + "님이 운영자에 의해 강퇴당하셨습니다.\n");
						tc.jta_display.setCaretPosition(tc.jta_display.getDocument().getLength());
						for (int i = 0; i < tc.dtm.getRowCount(); i++) {
							String n = (String) tc.dtm.getValueAt(i, 0);
							if (n.equals(nickName)) {
								tc.dtm.removeRow(i); // 나가면 dtm(접속인원)에서 제거
							}
						}

					}

				}

				}//////////// end of switch
			} catch (Exception e) {
				// TODO: handle exception
			}
		} //////////////////// end of while
	}//////////////////////// end of run
}
