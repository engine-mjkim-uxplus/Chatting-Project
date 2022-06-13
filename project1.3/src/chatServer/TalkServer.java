package chatServer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class TalkServer extends JFrame implements Runnable, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ChatDao dao = null; // DB전담하여 쿼리문 질의하는 객체
	TalkServerThread tst = null; // 각 클라이언트의 통신담당하는 쓰레드1
	List<TalkServerThread> globalList = null; // 각 클라이언트의 정보를 받음 (vector로 구현)
	ServerSocket server = null; // ip와 port 바인드하여 클라이언트 접속을 받는 객체
	Socket socket = null; // 클라이언트와 연결 되면 얻어지는 객체
	JTextArea jta_log = new JTextArea(10, 30); //
	JTextField jtf_userCount = new JTextField(); // 접속자 수 표시
	JScrollPane jsp_log = new JScrollPane(jta_log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JPanel jp_south = new JPanel();
	JButton jbtn_log = new JButton("로그저장");
	JButton jbtn_notice = new JButton("공지사항 알림");
	JButton jbtn_chat = new JButton("대화내용 백업");
	JButton jbtn_user = new JButton("접속인원");
	Font font;
	JFrame frame2; // 현재 접속인원 관리
	DefaultTableModel dtm;
	JButton jbtn_expulsion;
	Vector<Object> v = new Vector<>(); // 각 사용자 닉네임, ip, 시간 담는 백터
	String logPath = "./txt/";
	JTable jtb;

	public void initDisplay() {
		this.setTitle("채팅프로그램 서버");
		// jp_south.setLayout(new GridLayout(1,0));
		jp_south.setLayout(new FlowLayout(FlowLayout.CENTER)); // 각 컴포넌트의 크기 동일하게 센터에 나오도록 배치
		// jta_log.setBackground(Color.orange);
		// 각 컴포넌트의 폰트 설정
		font = new Font("나눔고딕", Font.BOLD, 15); // 폰트사용
		jta_log.setFont(font);
		jbtn_log.setFont(font);
		jbtn_notice.setFont(font);
		jbtn_chat.setFont(font);
		jbtn_user.setFont(font);
		jtf_userCount.setFont(font);

		jp_south.add(jbtn_log); // 로그버튼 추가
		jp_south.add(jbtn_notice); // 알림버튼 추가
		jp_south.add(jbtn_chat); // 단톡대화 백업
		jp_south.add(jbtn_user); // 현재 접속인원

		// 현재 접속인원 보기
		jbtn_user.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initDisplay2();
			}

		});

		jbtn_notice.addActionListener(this); // 공지사항 이벤트
		jbtn_log.addActionListener(this); // 로그저장 이벤트
		jbtn_chat.addActionListener(this);

		this.add("South", jp_south);
		this.add("Center", jsp_log);
		this.add("North", jtf_userCount);
		jtf_userCount.setEditable(false); // 접속인원 수정할 수 없도록 설정
		this.setSize(550, 550);
		this.setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // x표시로 닫으면 완전히 종료
		setLocationRelativeTo(null); // 창 가운데 뜨게 설정
	}

	// 현재접속인원 보여주는 창 UI
	public void initDisplay2() {
		frame2 = new JFrame("현재 접속인원");
		JPanel jp = new JPanel();
		JPanel jp_south = new JPanel();
		String cols[] = { "접속 닉네임", "IP", "접속시간" }; // 컬럼네임. cols= 컬럼명
		String data[][] = new String[0][3]; // 닉네임, ip, 접속시간. data = 컬럼

		dtm = new DefaultTableModel(data, cols);
		JTable jtb = new JTable(dtm);
		jtb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 한개의 로우만 선택가능
		jbtn_expulsion = new JButton("클라이언트 접속 끊기"); // 클라이언트 강퇴하는 버튼

		// 클라이언트 강제퇴장시키는 이벤트 처리(시점의 문제로 inidisplay2()안으로)
		jbtn_expulsion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == jbtn_expulsion) {
					if (globalList.size() != 0) {
						int select = jtb.getSelectedRow();
						String n = (String) dtm.getValueAt(select, 0);
						for (TalkServerThread tst : globalList) {
							if (n.equals(tst.chatName)) {
								String msg = "501#" + tst.chatName;
								StringTokenizer st = new StringTokenizer(msg, "#");
								tst.broadCasting(msg);
								// tst.send(msg); // 강퇴메시지
								dtm.removeRow(select);
								globalList.remove(tst);
								break;
								// for each중 객체를 수정하면 ConcurrentModificationException 발생. 그러므로 수정후 꼭
								// break문으로 빠져 나갈 것
							}
						}
					}
				}
			}

		});

		JScrollPane jsp = new JScrollPane(jtb);

		jp.add(new JScrollPane(jtb));

		jbtn_expulsion.setFont(font);
		jbtn_expulsion.addActionListener(this);

		frame2.add("Center", jp);
		frame2.add("South", jp_south);
		frame2.setSize(500, 500);
		frame2.setVisible(true);

		jp.setLayout(new BorderLayout());
		jp.add("South", jp_south); // 패널에 밑쪽에 버튼 패널 붙이기
		jp_south.add(jbtn_expulsion);
		jp_south.setLayout(new FlowLayout(FlowLayout.CENTER));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setResizable(false); // 크기변경X

	}

	// 서버소켓과 클라이언트측 소켓을 연결하기
	@Override
	public void run() {
		// 서버에 접속해온 클라이언트 쓰레드 정보를 관리할 벡터 생성하기
		globalList = new Vector<>();
		dao = new ChatDao(); // DB전담

		try {
			boolean isStop = false;
			server = new ServerSocket(3002);
			jta_log.append("사용자의 접속을 기다리는 중입니다:)\n");
			while (!isStop) {
				jtf_userCount.setText("현재 접속인원은 " + globalList.size() + "명 입니다.");
				socket = server.accept();
				jta_log.append("client info:" + socket + "\n"); // 사용자 정보를 찍음 (ip, port)등
				TalkServerThread tst = new TalkServerThread(this); // 톡서버쓰레드 생성자에 자기자신이 들어간다.
				tst.start(); // 톡서버 쓰레드 시작
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TalkServer ts = new TalkServer();
		ts.initDisplay();
		ts.initDisplay2();
		Thread th = new Thread(ts); // 화면 뛰운후 RUN을 구현한 톡서버 객체 쓰레드에 넣어줌
		th.start(); // 접속을 받는 서버소켓 쓰레드 실행
	}

	// 해당 날짜 출력 메소드
	public String getDate() { // 변경
		Calendar today = Calendar.getInstance();
		int yyyy = today.get(Calendar.YEAR);
		int mm = today.get(Calendar.MONTH) + 1;
		int day = today.get(Calendar.DAY_OF_MONTH);
		return yyyy + "-" + (mm < 10 ? "0" + mm : "" + mm) + "-" + (day < 10 ? "0" + day : " " + day + " ");

	}//////////////// end of setTimer

	// 해당시간 출력 메소드
	public String getTime() {
		Calendar today = Calendar.getInstance();
		int h = today.get(Calendar.HOUR_OF_DAY);
		int m = today.get(Calendar.MINUTE);
		String todayTime = (h < 10 ? "0" + h + "시 " : "" + h + "시 ") + (m < 10 ? "0" + m + "분" : "" + m + "분");

		return todayTime;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// 로그저장 액션
		if (obj == jbtn_log) {
			String fileName = "log_" + getDate() + ".txt";
			System.out.println(fileName);// log_2020-03-13.txt
			try {
				// 자바는 모든 기능 사물 들을 클래스로 설계하도록 유도한다.
				// 파일명을 클래스로 만들어주는 API가 있다. -File
				File f = new File(logPath + fileName); // 경로 + 파일이름
				// 파일명만 생성될 뿐 내용까지 만들어주지는 않는다.
				// 내용부분을 담는 별도의 클래스가 필요하다.
				PrintWriter pw = new PrintWriter(new BufferedWriter(// 필터클래스-카메라 필터
						new FileWriter(f.getAbsolutePath())));
				// io패키지에는 단독으로 파일을 컨트롤할 수 있는 클래스가 있고
				// 그 클래스에 연결해서 사용하는 필터 클래스가 존재함.(기능을 향상해줌)
				pw.write(jta_log.getText());
				pw.close();// 사용한 입출력 클래스는 반드시 닫아줌.
			} catch (Exception e2) {
				// 예외가 발생했을 때 출력함.
				// 예외가 발생하지 않으면 실행이 안됨.
				System.out.println(e2.toString());
			}
			// 서버에서 공지사항 알림 (프로토콜 203 ) 
		} else if (obj == jbtn_notice) {
			String notice = JOptionPane.showInputDialog("공지사항을 입력하세요.");
			if (notice == null || notice.trim().length() < 1) {
				JOptionPane.showMessageDialog(this, "메시지는 공백일 수 없습니다. 다시 입력하세요", "INFO",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			} else if (globalList.size() != 0) {
				for (TalkServerThread tst : globalList)
					tst.send(203 + "#" + "운영자" + "#" + notice);
			} else if (notice != null && globalList.size() == 0) {
				JOptionPane.showMessageDialog(this, "현재 접속중인 사용자가 없습니다", "INFO", JOptionPane.INFORMATION_MESSAGE);
			}

		}
	///////////////////대화내용 txt파일로 백업하는 기능 추가 작성 해야함///////////////////////
		
	}
}