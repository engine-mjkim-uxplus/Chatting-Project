package chatClient;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ChatView extends JFrame implements ActionListener {

	JPanel jp_second 			= 	new JPanel();
	JPanel jp_second_south 		= 	new JPanel();
	JButton jbtn_one 			= 	new JButton("1:1");
	JButton jbtn_change 		= 	new JButton("대화명변경");
	JButton jbtn_font 			= 	new JButton("글자색");
	JButton jbtn_exit 			= 	new JButton("나가기");
	String cols[] 				= 	{ "대화명" };
	String data[][] 			= 	new String[0][1];
	DefaultTableModel dtm 		= 	new DefaultTableModel(data, cols);
	JTable jtb 					= 	new JTable(dtm);
	JScrollPane jsp 			= 	new JScrollPane(jtb);
	JPanel jp_first 			=	new JPanel();
	JPanel jp_first_south 		= 	new JPanel();
	JTextField jtf_msg 			=	new JTextField(20);// south속지 center
	JButton jbtn_send 			= 	new JButton("전송");// south속지 east
	JTextArea jta_display 		= 	null;
	JScrollPane jsp_display		= 	null;
	// 배경 이미지에 사용될 객체 선언-JTextArea에 페인팅
	TalkClient tc 				= 	null;
	Image back					= 	null;
	String nickName			    = 	null;
	
	
	// ChatView가 실행되면서 동시에 TalkClienThread가 생성되고
	// run()메소드로 쓰레드가 실행됩니다.
	public ChatView(TalkClient tc) {
		this.tc = tc;
		this.nickName =tc.nickName;
		TalkClientThread tct = new TalkClientThread(this);
		tct.start();
		initDisplay(true);
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

		jtf_msg.addActionListener(this);
		jbtn_change.addActionListener(this);
		jbtn_exit.addActionListener(this);
	}

	public String getMsg() {
		String msg = jtf_msg.getText();
		jtf_msg.setText("");
		return msg;
	}
	
	public void successMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	public void errorMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	
	// UI와 서버의 작업 분담을 위해 UI쪽에서는 서버에서 작성한 메소드 호출만 하도록 작성
	// 여기서 프로토콜 알고 던져줄 수 있지만 그렇게 되면 통신에 문제가 생겼을 때
	// UI와 서버 둘 다 확인 해야하기 때문에 msg와 관련된 파라미터만 TalkClient(컨트롤러 역할)에게 넘겨준다
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		// GROUP_MESSAGE
		if (jtf_msg == obj) {
		String msg = jtf_msg.getText();
		tc.groupMsg(msg);
		jtf_msg.setText("");
		
		// ROOM_OUT
		} else if (jbtn_exit == obj) {
		tc.roomOut();
		System.exit(0);
		
		// NICKNAME_CHANGE
		} else if (jbtn_change == obj) {
			String afterName = JOptionPane.showInputDialog("변경할 대화명을 입력하세요.");
			if (afterName == null || afterName.trim().length() < 1) {
				errorMsg("변경할 대화명을 입력해주세요.");
				return;
			}else {
				tc.changeNickName(afterName);
			}
		}
	}////////////////////// end of actionPerformed
}
