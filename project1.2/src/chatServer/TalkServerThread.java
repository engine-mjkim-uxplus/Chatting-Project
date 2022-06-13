package chatServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

// 클라이언트와의 통신전용 쓰레드, 서버에 로그를 찍는 역할도 한다

public class TalkServerThread extends Thread {
	public TalkServer ts = null;
	Socket client = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	String chatName = null;// 현재 서버에 입장한 클라이언트 스레드 닉네임 저장

	public TalkServerThread(TalkServer ts) {
		this.ts = ts; // ts는 톡서버. 톡서의 전역변수 및 메서드 사용가능
		this.client = ts.socket; // 방금 접속한 클라이언트의 정보(ip,port)
		try {
			oos = new ObjectOutputStream(client.getOutputStream()); // client소켓으로부터 아웃풋스트림 얻음
			ois = new ObjectInputStream(client.getInputStream()); // client소켓으로부터 인풋스트림 얻음
			String msg = (String) ois.readObject(); // 사용자 nickName(JoptionPane) 읽어들임
			// ts.jta_log.append(msg + "\n"); // jta_log는 서버의 ui에 TextArea부분에 msg붙인다.
			StringTokenizer st = new StringTokenizer(msg, "#"); // ( msg = 100#nickName임 )
			st.nextToken(); // msg = 100#nickName에서 100을 읽어들인다.
			chatName = st.nextToken(); // msg에 남은 #nicKname에서 구분자 #을 빼고 nickName을 읽어들인다
			ts.jta_log.append(chatName + "님이 입장하였습니다.\n"); // 서버에 찍음
			// 이전에 입장해 있는 친구들 정보 받아내기 ------------해야할 것
			for (TalkServerThread tst : ts.globalList) {
				// String currentName = tst.chatName;
				this.send(100 + "#" + tst.chatName); // ※※※이전 참여자의 정보를 가져오는 부분 ※※※
			}									     // 방금 접속한 사용자에게 이전 접속자들 접속했다고 화면에 뛰운다
			// 서버 dtm에 현재 접속인원 찍기
			if (ts.dtm != null) {
				ts.v = new Vector<>(); // 백터에 현재 접속한 닉네임을 담는다.
				ts.v.add((String) chatName);
				ts.v.add(client.getInetAddress());
				ts.v.add((String) (ts.getDate() + ts.getTime()));
				ts.dtm.addRow(ts.v);
			}
			// 현재 서버에 입장한 클라이언트 스레드 추가하기
			ts.globalList.add(this); // 입장한 각 클라이언트 쓰레드를 백터에 담는다
			this.broadCasting(msg); // ※※※ 현재접속자를 포함한 각 클라이언트 쓰레드에게 접속하였다고 여기서 전달 ※※※
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	// 현재 입장해 있는 친구들 모두에게 메시지 전송하기 구현
	public void broadCasting(String msg) {
		for (TalkServerThread tst : ts.globalList) {
			tst.send(msg); // 각 클라이언트와 소통하는 소켓들이 클라이언트 쓰레드에게 메시지 보낸다
		}
	}

	// 클라이언트에게 말하기 구현
	public void send(String msg) {
		try {
			oos.writeObject(msg);
			// ts.dao.chatMsg(msg); //////////// 대화내용DB에 찍기(테스트중...)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 클라이언트가 말하는 것을 듣는 역할. 클라이언트가 말할 때 까지 기다린다. //
	public void run() {
		String msg = null;
		boolean isStop = false;
		try {
			// while(true) {//무한루프에 빠질 수 있다.
			run_start: while (!isStop) { // 클라이언트의 말 들을 준비중...

				msg = (String) ois.readObject(); // 사용자에게 입력 받을 때 까지 기다린다.
				ts.jta_log.append(msg + "\n");
				ts.jta_log.setCaretPosition(ts.jta_log.getDocument().getLength());
				StringTokenizer st = null;
				int protocol = 0;// 100|200|201|202|500
				if (msg != null) {
					st = new StringTokenizer(msg, "#");
					protocol = Integer.parseInt(st.nextToken());// 100
				}
				switch (protocol) {
				case 200: {
					
				}
					break;
				case 201: { // 채팅보냈을 때 (DB에 대화내용 저장)
					String nickName = st.nextToken();
					String message = st.nextToken();
					broadCasting(201 // 프로토콜 201이면 단체로 전달
							+ "#" + nickName + "#" + message);
					String days = ts.getDate();
					String hours = ts.getTime();
					ts.dao.chatMsg(message, nickName, days, hours); // 대화내용 DB 테이블에 기록하기( 테스트중.... )
				}
					break;
				case 202: { // 프로토콜 202면 대화명 변경 및 단체로 전달
					String nickName = st.nextToken();
					String afterName = st.nextToken();
					String message = st.nextToken();
					this.chatName = afterName;
					broadCasting(202 + "#" + nickName + "#" + afterName + "#" + message);
				}
					break;
				case 500: { // 클라이언트 퇴장 시
					String nickName = st.nextToken();
					ts.globalList.remove(this); // 클라이언트 나갔으므로 통신 쓰레드 지움
					broadCasting(500 + "#" + nickName);
					if (ts.dtm != null) {
						for (int i = 0; i < ts.dtm.getRowCount(); i++) {
							String n = (String) ts.dtm.getValueAt(i, 0);
							if (n.equals(nickName)) {
								ts.dtm.removeRow(i); // 나가기 누르면 서버의 dtm(접속인원)에서 제거
							}
						}
					}
				}
					break run_start;
				}///////////// end of switch
			} ///////////////// end of while
		} catch (Exception e) {
			// TODO: handle exception
		}
	}///////////////////////// end of run

}