package chatClient;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

// 통신용 쓰레드 클래스 < 서버의 말을 듣는 역할을 한다 >
public class TalkClientThread extends Thread {
	TalkClient tc = null; 

	public TalkClientThread(TalkClient tc) {
		this.tc = tc; //톡클라이언트의 주소값을 들고 온다
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
		while (!isStop) {
			try {
				String msg = "";// 100#apple // 입장할 때 프로토콜 100번
				msg = (String) tc.ois.readObject(); // 톡 서버쓰레드에서 넘어오는 메시지 기다리는중..
				StringTokenizer st = null;
				int protocol = 0;// 100|200|201|202|500
				if (msg != null) { 
					st = new StringTokenizer(msg, "#"); // 여기서 넘어온 100#메시지내용 을 분리
					protocol = Integer.parseInt(st.nextToken()); // 100. 프로토콜(String)을 숫자로 바꿈
				}
				switch (protocol) {
				case 100: {// 100#apple
					String nickName = st.nextToken();
					tc.jta_display.append(nickName + "님이 입장하였습니다.\n");
					Vector<String> v = new Vector<>(); // 백터에 현재 접속한 닉네임을 담는다.
					v.add(nickName);
					tc.dtm.addRow(v); /// 접속인원 보여주는 dtm에 닉네임 추가

				}
					break;
				case 200: {

				}
					break;
				// 채팅보내기 (프로토콜 201)
				case 201: {
					String nickName = st.nextToken();
					String message = st.nextToken();
					tc.jta_display.append("[" + nickName + "]" + message + "\n");
					tc.jta_display.setCaretPosition(tc.jta_display.getDocument().getLength());
				}
					break;
				// 대화명변경 (프로토콜 202)
				case 202: {
					String nickName = st.nextToken();
					String afterName = st.nextToken();
					String message = st.nextToken();
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
				case 203: {
					String nickName = st.nextToken();
					String notice = st.nextToken();
					String n = "[" + nickName + "]" + notice;
//					tc.jta_display.append("[" + nickName + "]" + notice + "\n"); // 채팅창에 찍기
					// tc.jta_display.setForeground(Color.red); // 색상변경 (테스트중..) 구글링에서 안된다고함..
					tc.jta_display.setCaretPosition(tc.jta_display.getDocument().getLength());
					tc.showmsg_Info(n); 
				}
					break;
				// 클라이언트 나가기 누름 (프로토콜 500)
				case 500: {
					String nickName = st.nextToken();
					tc.jta_display.append(nickName + "님이 퇴장 하였습니다.\n");
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
				case 501: {
					String nickName = st.nextToken();
					if (tc.user_Name.equals(nickName)) { // 같은 닉네임이면 종료
						for (int i = 0; i < tc.dtm.getRowCount(); i++) {
							String n = (String) tc.dtm.getValueAt(i, 0);
						}
//						tc.initDisplay(false);
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
