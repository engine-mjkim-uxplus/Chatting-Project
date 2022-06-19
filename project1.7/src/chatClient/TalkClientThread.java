package chatClient;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import chatServer.MsgVO;
import chatServer.Protocol;

// 통신용 쓰레드 클래스 < 서버의 말을 듣는 역할을 한다 >
public class TalkClientThread extends Thread {
	ChatView chatview = null;
	TalkClient tc = null;

	public TalkClientThread(ChatView chatview) {
		this.chatview = chatview;
		this.tc = chatview.tc;
	}

	/*
	 * 서버에서 말한 내용을 들어봅시다.
	 */
	// run()메소드 즉 클라이언트 쓰레드는 Client 뷰에서 작성된 메시지가 서버로 보내지고
	// 서버에서 ois즉 ObjectInputStream이 readObject()로 뷰에서 메시지가 전달되기까지 기다렸다가 메시지 받으면
	// 지금 이 쓰레드로 ObjectWriter를 사용하여 메시지를 전달하고
	// 이 쓰레드는 swicth문에서 전달 받은 프로토콜에 맞는 메시지가 사용자 화면에 출력된다.

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
					chatview.jta_display.append(nickName + "님이 입장하였습니다.\n");
					System.out.println(nickName + "님이 입장하였습니다");
					Vector<String> v = new Vector<>(); // 백터에 현재 접속한 닉네임을 담는다.
					v.add(nickName);
					chatview.dtm.addRow(v); /// 접속인원 보여주는 dtm에 닉네임 추가

				}
					break;
				case Protocol.MESSAGE: {

				}
					break;
				// 채팅보내기 (프로토콜 201)
				case Protocol.GROUP_MESSAGE: {
					String nickName = mvo.getNickname();
					String message = mvo.getMsg();
					chatview.jta_display.append("[" + nickName + "]" + message + "\n");
					chatview.jta_display.setCaretPosition(chatview.jta_display.getDocument().getLength());
				}
					break;
				// 대화명변경 (프로토콜 202)
				case Protocol.NICKNAME_CHANGE: {
					String nickName = mvo.getNickname();
					String afterName = mvo.getAfter_nickname();
					String message = mvo.getMsg();
					// 테이블에 대화명 변경하기
					for (int i = 0; i < chatview.dtm.getRowCount(); i++) {
						String imsi = (String) chatview.dtm.getValueAt(i, 0);
						if (nickName.equals(imsi)) {
							chatview.dtm.setValueAt(afterName, i, 0);
							break;
						}
					}
					// 채팅창에 타이틀바에도 대화명을 변경처리 한다.
					if (nickName.equals(tc.nickName)) {
						chatview.setTitle(afterName + "님의 대화창");
						chatview.nickName = afterName;
					}
					chatview.jta_display.append(message + "\n");
				}
					break;
				// 서버에서 공지사항 보냄(프로토콜 203)
				case Protocol.NOTICE: {
					String nickName = mvo.getNickname();
					String notice = mvo.getMsg();
					String n = "[" + nickName + "]" + notice;
					chatview.jta_display.setCaretPosition(chatview.jta_display.getDocument().getLength());
					chatview.successMsg(n);
				}
					break;
				// 클라이언트 나가기 누름 (프로토콜 500)
				case Protocol.ROOM_OUT: {
					String nickName = mvo.getNickname();
					msg = mvo.getMsg();
					chatview.jta_display.append(msg);
					chatview.jta_display.setCaretPosition(chatview.jta_display.getDocument().getLength());
					for (int i = 0; i < chatview.dtm.getRowCount(); i++) {
						String n = (String) chatview.dtm.getValueAt(i, 0);
						if (n.equals(nickName)) {
							chatview.dtm.removeRow(i); // 나가면 dtm(접속인원)에서 제거
						}
					}
				}
					break;
				// 운영자에 의해 강제퇴장 당했을 경우
				case Protocol.EXPULSION: {
					String nickName = mvo.getNickname();
					if (tc.nickName.equals(nickName)) { // 같은 닉네임이면 종료
						chatview.successMsg("운영자에 의해 강퇴 당하셨습니다");
						chatview.dispose();
					} else { // 다른 닉네임이면 강퇴 당한 아이디 채팅창에 그리고 대화목록에서 삭제
						chatview.jta_display.append(nickName + "님이 운영자에 의해 강퇴당하셨습니다.\n");
						chatview.jta_display.setCaretPosition(chatview.jta_display.getDocument().getLength());
						for (int i = 0; i < chatview.dtm.getRowCount(); i++) {
							String n = (String) chatview.dtm.getValueAt(i, 0);
							if (n.equals(nickName)) {
								chatview.dtm.removeRow(i); // 나가면 dtm(접속인원)에서 제거
							}
						}

					}

				}

				}//////////// end of switch
			} catch (Exception e) {
				// TODO: handle exception
			}
		} //////////////////// end of while
	}
}
