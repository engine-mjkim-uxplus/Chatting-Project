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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ChatView extends JFrame implements ActionListener, FocusListener, ItemListener {
	private static final long serialVersionUID = 1L;
	JTextField jtf_name = new JTextField("대화명을 입력하세요",20);
	JButton jbtn_search = new JButton("찾기");
	String comboicon[] = {"전체보기","닉네임으로 검색"};
	JComboBox<String> jcb = new JComboBox<String>(comboicon);
	Vector<String> userList = new Vector<>();
	String selectName = null;
	
	JPanel jp_first 			=	new JPanel();
	JPanel jp_first_south 		= 	new JPanel();
	JPanel jp_first_east		= 	new JPanel();
	
	JPanel jp_second 			= 	new JPanel();
	JPanel jp_second_north      =   new JPanel();
	JPanel jp_second_south 		= 	new JPanel();
	
	JButton jbtn_whisper 		= 	new JButton("1:1대화 신청");
	JButton jbtn_change 		= 	new JButton("회원정보 수정");
	JButton jbtn_exit 			= 	new JButton("나가기");
	JButton jbtn_send 			= 	new JButton("전송");
	JButton jbtn_emoticon 		= 	new JButton("이모티콘");
	JTextField jtf_msg 			=	new JTextField(20);
	JTextArea jta_display 		= 	null; // 채팅창 배경
	JScrollPane jsp_display		= 	null; // 채팅창
	TalkClient tc 				= 	null;
	Image back					= 	null;
	String nickName			    = 	null;
	
	String cols[] = {"현재 접속자"};
	String data[][] = new String[0][1];
	DefaultTableModel dtm = new DefaultTableModel(data,cols);
	JTable jtb = new JTable(dtm);
	JScrollPane jsp = new JScrollPane(jtb);
	JSplitPane jspp = new JSplitPane(SwingConstants.VERTICAL, jp_first,jp_second);
	
	
	
	// ChatView가 실행되면서 동시에 TalkClienThread가 생성되고
	// run()메소드로 쓰레드가 실행됩니다.
	public ChatView(String nickName) {
		this.nickName = nickName;
		JFrame.setDefaultLookAndFeelDecorated(true);
		TalkClient tc = new TalkClient(this, nickName);
		this.tc = tc;
		initDisplay(true);
		tc.init();
	}
	
	
	
	public void initDisplay(boolean is) {
		/////////////////////////배경 이미지/////////////////////////////
		back = getToolkit().getImage("./img/단체채팅 배경.png");
		jta_display = new JTextArea() {
			private static final long serialVersionUID = 1L;
			
			public void paint(Graphics g) {
				g.drawImage(back, 0, 0, this);
				Point p = jsp_display.getViewport().getViewPosition();
				g.drawImage(back, p.x, p.y, null);
				paintComponent(g);
			}
		};
		jp_second_north.setLayout(new BorderLayout());
		jp_second_north.add("West",jcb);
		jp_second_north.add("Center",jtf_name);
		jp_second_north.add("East",jbtn_search);
		
		jp_second_south.setLayout(new GridLayout(1,3));
		jp_second_south.add(jbtn_whisper); // 1대1 대화
		jp_second_south.add(jbtn_change); //회원정보 수정
		jp_second_south.add(jbtn_exit);	// 나가기
		
		jp_first_south.setLayout(new BorderLayout());
		jp_first_east.setLayout(new GridLayout(1,2));
		jp_first_east.add(jbtn_emoticon);
		jp_first_east.add(jbtn_send);
		jp_first_south.add("Center",jtf_msg);
		jp_first_south.add("East",jp_first_east);
		jspp.setPreferredSize(new Dimension(400,200));
//		jspp.setOrientation(SwingConstants.VERTICAL);
		jp_first.setLayout(new BorderLayout());
		jp_second.setLayout(new BorderLayout());
		jsp_display = new JScrollPane(jta_display);	
		jp_first.add("Center", jsp_display);
		jp_first.add("South", jp_first_south);
		jp_second.add("North", jp_second_north);
		jp_second.add("Center", jsp);
		jp_second.add("South", jp_second_south);
		
		jta_display.setLineWrap(true);
		jta_display.setOpaque(false);
		Font font = new Font("나눔고딕", Font.BOLD, 15);
		jta_display.setFont(font);
		this.add(jspp);          
		this.setTitle(nickName);  
		this.setSize(800, 550); 
		this.setVisible(is); 
		this.setLocation(600, 150);
		setResizable(false); 
		
		///////// 이벤트 처리 ////////////
		jtf_msg.addActionListener(this);
		jbtn_change.addActionListener(this);
		jbtn_exit.addActionListener(this);
		jtf_name.addFocusListener(this);
		jbtn_whisper.addActionListener(this);
		jcb.addItemListener(this);
		jbtn_search.addActionListener(this);
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
	public int private_chat(String nickname) {
		int result= JOptionPane.showConfirmDialog (this, nickname + "님이 대화를 신청하셨습니다","Invite", JOptionPane.YES_NO_OPTION);
		 if(result == JOptionPane.YES_OPTION) {
	            System.out.println("yes");
	            result = 1;
		 } else if(result == JOptionPane.NO_OPTION) {
	              System.out.println("no");
	              result = 0;
	     }
		 return result;
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
		
		dispose();
		// NICKNAME_CHANGE
		} else if (jbtn_change == obj) {
			new ChangeView(this);
		// ROOM_CREATE
		} else if (jbtn_whisper == obj) { // 1대1 대화 신청
			if(jtb.getSelectedRow() > -1) {
				int select = jtb.getSelectedRow();
				String otnickName = (String)jtb.getValueAt(select, 0);
				boolean isRoom = tc.isRoom(otnickName); // 대화방 유무 체크(있으면 flase)
				if(isRoom) {
					if(nickName.equals(otnickName)) {
						errorMsg("본인에게는 신청할 수 없습니다");
					} else {
						tc.roomCreate(otnickName);
					}
				}else if(!isRoom) // 대화방이 이미 존재할 경우
					errorMsg("이미 대화중입니다");
				}else 			 
					errorMsg("대화상대를 선택해 주세요");
		} else if (jbtn_search == obj) {
			int count = 0;
			String selectName = jtf_name.getText();
			dtm.setRowCount(0);
			jtf_name.setText("");
			for (String name : userList) {
				if (name.matches("(.*)"+selectName+"(.*)")) {
					dtm.addRow(new String[]{name});
				}else {
					count++;
				}
			}
			if (count == userList.size()) {
				errorMsg("검색하신 닉네임은 존재하지 않습니다, 다시 검색해주세요");
				jtf_name.setText("");
				dtm.setRowCount(0);
				for (String name : userList) {
					dtm.addRow(new String[]{name});
				}
				count = 0;
			}
		}
	}////////////////////// end of actionPerformed

	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() ==jtf_name) {
			jtf_name.setText("");
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();
		if (obj == jcb) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selectName = comboicon[jcb.getSelectedIndex()];
//				System.out.println(selectName);
				if (selectName.equals("전체보기")) {
					dtm.setRowCount(0);
					for (String name : userList) {
						dtm.addRow(new String[]{name});
					}
					return;
				}else if(selectName.equals("닉네임으로 검색")) {
					jtf_name.setText("");
				}
			}
		}
		
	}

	public void addRow(String nickName) {
		userList.add(nickName);
		System.out.println("userList 추가 : "+userList);
		dtm.addRow(new String[]{nickName}); /// 접속인원 보여주는 dtm에 닉네임 추가
	}
	
	public void deleteRow(String nickName) {
		String select = null;
		for (String userName : userList) {
			if (userName.equals(nickName)) {
				select = userName;
			}
		}
		userList.remove(select);
		
		System.out.println("몇명남았나 : "+userList);
		for (int i = 0; i < dtm.getRowCount(); i++) {
			String n = (String) dtm.getValueAt(i, 0);
			if (n.equals(nickName)) {
				dtm.removeRow(i); // 나가면 dtm(접속인원)에서 제거
			}
		}
	}

	public void changeNickName(String nickName) {
		tc.changeNickName(nickName);
		
	}
	
	public void reFresh(String nickName,String afterName) { // userList = [백종국] -> [김종국]
		// userList 대화면 변경
		int count = 0; 
		for (String userName : userList) {
			if(nickName.equals(userName)) {
				userList.set(count, afterName);
				System.out.println("userList 바꿈 : "+ userList);
			}else {
				count++;
			}
		}
	}
}
