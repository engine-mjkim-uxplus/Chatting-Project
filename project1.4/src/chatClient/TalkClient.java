package chatClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// 로그인 후 단톡 채팅방 UI 및 클라이언트 소켓 생성클래스
public class TalkClient extends JFrame implements ActionListener {
	//////////////// 통신과 관련한 전역변수 추가 시작//////////////
	Socket socket = null;
	ObjectOutputStream oos = null;// 말 하고 싶을 때
	ObjectInputStream ois = null;// 듣기 할 때
	String nickName = null;// 닉네임 등록
	//////////////// 통신과 관련한 전역변수 추가 끝 //////////////
	JPanel jp_second = new JPanel();
	JPanel jp_second_south = new JPanel();
	JButton jbtn_one = new JButton("1:1");
	JButton jbtn_change = new JButton("대화명변경");
	JButton jbtn_font = new JButton("글자색");
	JButton jbtn_exit = new JButton("나가기");
	String cols[] = { "대화명" };
	String data[][] = new String[0][1];
	DefaultTableModel dtm = new DefaultTableModel(data, cols);
	JTable jtb = new JTable(dtm);
	JScrollPane jsp = new JScrollPane(jtb);
	JPanel jp_first = new JPanel();
	JPanel jp_first_south = new JPanel();
	JTextField jtf_msg = new JTextField(20);// south속지 center
	JButton jbtn_send = new JButton("전송");// south속지 east
	JTextArea jta_display = null;
	JScrollPane jsp_display = null;
	// 배경 이미지에 사용될 객체 선언-JTextArea에 페인팅
	Image back = null;
	boolean is = true;
	public Login view = null;
	String user_Name;

	public TalkClient(Login view) { // 파라미터로 view의 주소값을 넘겨 받는다
		this.view = view; // 뷰에서 닉네임을 얻어오기 위해서...
		this.user_Name = view.user_Name;
		jtf_msg.addActionListener(this);
		jbtn_change.addActionListener(this);
		jbtn_exit.addActionListener(this);
	}

	// 서버에서 접속끊기 누르면 해당 클라이언트 종료되면서
	// 밑의 경고창 뜨게 하는 메소드
	public void showmsg_expulsion() {
		JOptionPane.showMessageDialog(this, "운영자에 의해 강퇴당하셨습니다.", "INFO", JOptionPane.INFORMATION_MESSAGE);

	}

	// 운영자가 보낸 메시지 받기
	public void showmsg_Info(String msg) {
		JOptionPane.showMessageDialog(this, msg, "INFO", JOptionPane.INFORMATION_MESSAGE);

	}

	public void initDisplay(boolean is) {
		this.setLayout(new GridLayout(1, 2));
		jp_second.setLayout(new BorderLayout());
		jp_second.add("Center", jsp);
		jp_second_south.setLayout(new GridLayout(2, 2));
		jp_second_south.add(jbtn_one);
		jp_second_south.add(jbtn_change);
		jp_second_south.add(jbtn_font);
		jp_second_south.add(jbtn_exit);
		jp_second.add("South", jp_second_south);
		jp_first.setLayout(new BorderLayout());
		jp_first_south.setLayout(new BorderLayout());
		jp_first_south.add("Center", jtf_msg);
		jp_first_south.add("East", jbtn_send);
		back = getToolkit().getImage("src\\chat\\step1\\accountmain.png");
		jta_display = new JTextArea() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				g.drawImage(back, 0, 0, this);
				Point p = jsp_display.getViewport().getViewPosition();
				g.drawImage(back, p.x, p.y, null);
				paintComponent(g);
			}
		};
		jta_display.setLineWrap(true);
		jta_display.setOpaque(false);
		Font font = new Font("나눔고딕", Font.BOLD, 15);
		jta_display.setFont(font);
		jsp_display = new JScrollPane(jta_display);
		jp_first.add("Center", jsp_display);
		jp_first.add("South", jp_first_south);
		this.add(jp_first);
		this.add(jp_second);
		this.setTitle(nickName);
		this.setSize(800, 550);
		this.setVisible(is);
		setResizable(false); // 창이 가운데 나오도록
	}

	// 소켓 관련 초기화
	public void init() { /// 닉네임 결정된 후 서버랑 연결요청한다.
		try {
			LoginDao login = new LoginDao();
			// 서버측의 ip주소 작성하기
			socket = new Socket("127.0.0.1", 3002);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			// initDisplay에서 닉네임이 결정된 후 init메소드가 호출되므로
			// 서버에게 내가 입장한 사실을 알린다.(말하기)
			oos.writeObject(100 + "#" + user_Name); // 100은 Integer객체이므로 ""안써도됨 #만 구분자로 ""붙인다
			// 서버에 말을 한 후 들을 준비를 한다.
			TalkClientThread tct = new TalkClientThread(this);
			tct.start();
		} catch (Exception e) {
			// 예외가 발생했을 때 직접적인 원인되는 클래스명 출력하기
			System.out.println(e.toString());
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		String msg = jtf_msg.getText(); // JTextField 즉 채팅내용 입력란 Enter이벤트
		if (jtf_msg == obj) {
			try {
				oos.writeObject(201 + "#" + user_Name + "#" + msg);
				jtf_msg.setText("");

			} catch (Exception e) {
				// TODO: handle exception
			}
		} else if (jbtn_exit == obj) {
			try {
				oos.writeObject(500 + "#" + this.user_Name);
				// 자바가상머신과 연결고리 끊기
				System.exit(0);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else if (jbtn_change == obj) {
			String afterName = JOptionPane.showInputDialog("변경할 대화명을 입력하세요.");
			if (afterName == null || afterName.trim().length() < 1) {
				JOptionPane.showMessageDialog(this, "변경할 대화명을 입력하세요", "INFO", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			try {
				oos.writeObject(202 + "#" + user_Name + "#" + afterName + "#" + user_Name + "의 대화명이 " + afterName
						+ "으로 변경되었습니다.");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}////////////////////// end of actionPerformed
}
