package chatClient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PrivateChat extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	TalkClient 		 tc		   	 	 = 		null;
	TalkClientThread tct 	 		 = 		null;
	String 		     nickName  		 = 		null;   // 나의 닉네임
	String 			 otNickName 	 = 		null; // 다른사람 닉네임
	private int 	 roomNum 		 = 		0;	  // private으로 변경 해야함. getter로 얻도록 설정할 것
	
/////////////////////////////////////////////////////////////////////////	
	
	Image			 back 			 = 		null;
	JPanel 		     jp 			 = 		new JPanel();
	JPanel 			 jp_south 		 = 		new JPanel(); 
	JPanel 			 jp_east 		 = 		new JPanel(); 
	JTextArea 		 jta 			 = 		new JTextArea(5,20);
	JScrollPane 	 jsp_display	 = 		null; // 채팅창
	JTextArea 		 jta_display	 =   	null;
	JTextField 		 jtf_msg 		 =		new JTextField(10);
	JButton 		 jbtn_send		 = 		new JButton("전송");
	JButton 		 jbtn_emoticon 	 =		new JButton("이모티콘");
	JButton 		 jbtn_exit 		 = 		new JButton("나가기"); // --> 처리해야함
	Font 			 font 			 = 		new Font("나눔고딕", Font.BOLD, 15);
	Font 			 btn_font		 = 		new Font("나눔고딕", Font.BOLD, 10);
	
	public PrivateChat() {	
	}
	
	public static void main(String args[]){
		JFrame.setDefaultLookAndFeelDecorated(true);
		PrivateChat pc =new PrivateChat();
		pc.initDisplay();
	}
	
	// 방번호는 위변조 될 수 없게 getter로 얻어온다
	public int getRoomNum() {
		int roomNum = this.roomNum;
		return roomNum;
	}
	
	public PrivateChat(TalkClientThread tct, int roomNum, String nickName, String otNickName) {
		this.tc = tct.tc;
		this.tct = tct;
		this.roomNum = roomNum;
		this.nickName = nickName;
		this.otNickName = otNickName;
		initDisplay();
	}
	
	public void initDisplay() {
		back = getToolkit().getImage("C:\\Users\\MJ\\Desktop\\이미지\\보라.png");
		jta_display = new JTextArea() {
			private static final long serialVersionUID = 1L;
			public void paint(Graphics g) {
				g.drawImage(back, 0, 0, this);
				Point p = jsp_display.getViewport().getViewPosition();
				g.drawImage(back, p.x, p.y, null);
				paintComponent(g);
			}
		};
		// 버튼 폰트 설정
		jbtn_send.setFont(btn_font);
		jbtn_emoticon.setFont(btn_font);
		jbtn_exit.setFont(btn_font);
		
		// 프레임설정
		jp_south.setLayout(new BorderLayout());
		jp_east.setLayout(new GridLayout(1,2));
		jp_east.add(jbtn_emoticon);
		jp_east.add(jbtn_send);
		jp_east.add(jbtn_exit);
		jp_south.add("Center",jtf_msg);
		jp_south.add("East",jp_east);
		jp.setLayout(new BorderLayout());
		jsp_display = new JScrollPane(jta_display);	
		jp.add("Center", jsp_display);
		jp.add("South", jp_south);
		jta_display.setLineWrap(true); //자동 줄바꾸기 사용
		jta_display.setOpaque(false); //Opaque값을 true로 미리 설정해 주어야 배경색이 적용된다
		jta_display.setFont(font); // 채팅창 폰트
		this.setTitle(otNickName + "님과의 대화방");
		this.add(jp);	
		this.setSize(390,600);
		this.setVisible(true);
		setResizable(false); // 창의 크기를 조절할 수 없도록
		
		jtf_msg.addActionListener(this);
		jbtn_emoticon.addActionListener(this);
		jbtn_exit.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		
		// 메시지 보내기
		if (jtf_msg == obj || jbtn_send == obj) {
			String msg = jtf_msg.getText();
			tc.privateMsg(msg,roomNum,otNickName);
			jtf_msg.setText("");
		}
		// 채팅방 나가기
		else if(jbtn_exit == obj){
			// 상대방에게 나간 것 알리기
			tc.prRoomOut(otNickName, roomNum);
			// 나의 대화방 종료
			for(PrivateChat pc : tct.prlist) {
				if(pc.roomNum == roomNum) {
					tct.prlist.remove(pc);
					break;
				}
			}
			this.dispose();
		}
	}
	public void successMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void errorMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error!", JOptionPane.ERROR_MESSAGE);
	}

}