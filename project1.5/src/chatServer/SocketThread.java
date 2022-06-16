package chatServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

////////////////////// SocketThread의 세 가지 역할 ///////////////////////
// 1. 사용자 접속 받는 역할(특정 Port열고 accept() 대기중)
// 2. 뷰에 대한 컨트롤러 역할 (뷰의 이벤트 처리) - 유지보수 용이
// 3. 서버에서 직접적으로 사용자에게 말하고자 할 때 사용 ( 공지사항, 강퇴처리 )
public class SocketThread extends Thread {
	ChatDao 		 		 dao 				= 		null; // DB전담하여 쿼리문 질의하는 객체
	TalkServerThread 		 tst 				= 		null; // 각 클라이언트의 통신담당하는 쓰레드1
	ServerSocket 			 server 			= 		null; // ip와 port 바인드하여 클라이언트 접속을 받는 객체
	Socket 					 socket 			= 		null; // 클라이언트와 연결 되면 얻어지는 객체
	TalkServerView 			 view				= 		null;
	List<TalkServerThread> 	 globalList 		= 		null; // 각 클라이언트의 정보를 받음 (vector로 구현)
	List<Map<String,Object>> userList     		=       null;
	String 					 logPath				 = 	"C:/Users/MJ/Desktop/로그저장/";
	public SocketThread(TalkServerView view){
		this.view = view;	
	}
	
	@Override
	public void run() {
		// 서버에 접속해온 클라이언트 쓰레드 정보를 관리할 벡터 생성하기
		globalList = 	new Vector<>(); // 각 회원 통신쓰레드
 		userList   =    new ArrayList<>(); // 회원 닉네임, ip, 접속시간
		dao 	   = 	new ChatDao();  // DB전담
		try {	
			boolean isStop 	= 	false;
			server 			= 	new ServerSocket(3002);
			view.jta_log.append("사용자의 접속을 기다리는 중입니다:)\n");
			while (!isStop) {
				userCount();
				socket = server.accept();
				view.jta_log.append("client info:" + socket + "\n"); // 사용자 정보를 찍음 (ip, port)등
				TalkServerThread tst = new TalkServerThread(this); // 톡서버쓰레드 생성자에 자기자신이 들어간다.
				tst.start(); // 톡서버 쓰레드 시작
				
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 현재 접속인원 표시
	public void userCount() {
		view.jtf_userCount.setText("현재 접속인원은 " + globalList.size() + "명 입니다.");
	}
	
	// 해당 날짜 출력 메소드
	public String getDate() { //변경
		Calendar today = Calendar.getInstance();
		int yyyy = today.get(Calendar.YEAR);
		int mm = today.get(Calendar.MONTH) + 1;
		int day = today.get(Calendar.DAY_OF_MONTH);
		return yyyy + "-" + (mm < 10 ? "0" + mm : "" + mm) + "-" + (day < 10 ? "0" + day : " " + day + " ");

	}

	// 해당시간 출력 메소드
	public String getTime() {
		Calendar today = Calendar.getInstance();
		int h = today.get(Calendar.HOUR_OF_DAY);
		int m = today.get(Calendar.MINUTE);
		String todayTime = (h < 10 ? "0" + h + "시 " : "" + h + "시 ") + (m < 10 ? "0" + m + "분" : "" + m + "분");

		return todayTime;
	}
	
	public void log() {
		String fileName = "log_" + getDate() + ".txt";
		System.out.println(fileName);// log_2020-03-13.txt
		try {
			File f = new File(logPath + fileName); // 경로 + 파일이름
			PrintWriter pw = new PrintWriter(new BufferedWriter(   // 필터클래스-카메라 필터
					new FileWriter(f)));
			pw.write(view.jta_log.getText());
			pw.close();// 사용한 입출력 클래스는 반드시 닫아줌.
		} catch (Exception e2) {
			System.out.println(e2.toString());
		}
		
	}
	
	
	// 서버에서 공지사항 알림( 프로토콜 203 )
	public void notice(String notice_msg) {
		if (notice_msg == null || notice_msg.trim().length() < 1) {
			JOptionPane.showMessageDialog(view, "메시지는 공백일 수 없습니다. 다시 입력하세요", "INFO",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		} else if (globalList.size() != 0) {
			for (TalkServerThread tst : globalList)
				tst.send(Protocol.NOTICE 
						+Protocol.seperator + "운영자"
						+Protocol.seperator + notice_msg);
		} else if (notice_msg != null && globalList.size() == 0) {
			JOptionPane.showMessageDialog(view, "현재 접속중인 사용자가 없습니다", "INFO", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	
	
	// 접속인원 버튼 이벤트 처리
	public void showNumber_Conpeople() {
		if(globalList.size() > 0) {
			while(view.dtm.getRowCount() > 0) {
				view.dtm.removeRow(0);
			}
			for(TalkServerThread tst : globalList) {
				view.dtm.addRow(tst.oneRow);
			}
		}
		
	}
	
	// 강퇴 이벤트처리
	public void expulsion(int select) {
		String nicname = (String) view.dtm.getValueAt(select, 0);

		for (TalkServerThread tst : globalList) {
			if (nicname.equals(tst.nickName)) {
				String msg = Protocol.EXPULSION
							+Protocol.seperator + tst.nickName;
				tst.broadCasting(msg); // 강퇴메시지
				globalList.remove(tst);
				view.dtm.removeRow(select);
				view.jta_log.append(tst.nickName + "님을 강퇴하였습니다.\n");
				userCount();		
				break;
			// for each중 객체를 수정하면 ConcurrentModificationException 발생. 그러므로 수정후 꼭 break문으로 빠져 나갈 것	
			}
		}
	}
			
}
