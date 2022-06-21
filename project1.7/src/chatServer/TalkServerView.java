package chatServer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class TalkServerView extends JFrame implements ActionListener {
	//////////////////////////////선언부///////////////////////////////////////
	private static final long serialVersionUID       = 		 1L;
	TalkServerThread 		  tst 	  	 		     = 		null; // 각 클라이언트의 통신담당하는 쓰레드1
	JTextArea 				  jta_log 	 	 	     = 		new JTextArea(10, 30); //
	JTextField 			      jtf_userCount 	     = 		new JTextField(); // 접속자 수 표시
	JScrollPane 			  jsp_log 	 			 = 		new JScrollPane(jta_log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
																JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JPanel 				      jp_south 	 	 		 = 		new JPanel();
	JButton 			      jbtn_log 	 	 	 	 = 		new JButton("로그저장");
	JButton 			      jbtn_notice 	 	 	 = 		new JButton("공지사항 알림");
	JButton 			      jbtn_memSearch 		 = 		new JButton("회원조회");
	JButton 				  jbtn_user		 		 = 		new JButton("현재 접속자");
	Font 					  font;
	Vector<Object> 			  v 					 = 		new Vector<>(); // 각 사용자 닉네임, ip, 시간 담는 백터
	////////////////////////// 현재 접속 인원 UI  /////////////////////////////
	JFrame 					  frame2 			 	 = 		new JFrame("현재 접속자");
	JButton 			      jbtn_expulsion		 =      new JButton("클라이언트 접속끊기");
	JPanel 					  jp 				     = 		new JPanel();
	JPanel 				      jp_south2 			 = 		new JPanel();
	String 					  cols[]			     = 		{ "접속 닉네임", "IP", "접속시간" };                                                           
	String 					  data[][]			 	 = 	    new String[0][3];      
	DefaultTableModel 		  dtm 				     = 		new DefaultTableModel(data, cols);                                       
	JTable 					  jtb 				     =		new JTable(dtm);                     
	JScrollPane 			  jsp				     = 		new JScrollPane(jtb);                                           
	SocketThread			  sk 					 =		null;      
	///////////////////////////////////////////////////////////////////////////
	MemSearchView 			  msView 				 = 		null;								
	
	///////////////////////////////생성자//////////////////////////////////////
	public TalkServerView() {
		this.msView = new MemSearchView();
		initDisplay();
		this.sk = new SocketThread(this); 
		sk.start();
	}
	////////////////////////// 화면처리 ///////////////////////////////////////
	public void initDisplay() {
		this.setTitle("채팅프로그램 서버");
		jp_south.setLayout(new FlowLayout(FlowLayout.CENTER)); // 각 컴포넌트의 크기 동일하게 센터에 나오도록 배치
		// jta_log.setBackground(Color.orange);				   // 배경은 추후에 지정예정
		// 각 컴포넌트의 폰트 설정 
		font = new Font("나눔고딕", Font.BOLD, 14); 	
		jta_log.setFont(font);
		jbtn_log.setFont(font);
		jbtn_notice.setFont(font);
		jbtn_memSearch.setFont(font);
		jbtn_user.setFont(font);
		jbtn_notice.addActionListener(this);   			// 공지사항 버튼 이벤트 매핑
		jbtn_log.addActionListener(this); 	    		// 로그저장 버튼 이벤트 매핑
		jbtn_memSearch.addActionListener(this); 		// 회원조회 버튼 이벤트 매핑
		jbtn_user.addActionListener(this);      		// 현재접속자보기 버튼 이벤트 매핑
		jbtn_expulsion.addActionListener(this); 		// 접속끊기(강퇴) 버튼 이벤트 매핑

		jp_south.add(jbtn_log);      					// 로그버튼 추가
		jp_south.add(jbtn_notice); 						// 알림버튼 추가
		jp_south.add(jbtn_memSearch); 					// 회원조회버튼 추가
		jp_south.add(jbtn_user); 						// 현재 접속인원 버튼 추가
		this.add("South", jp_south);					// 버튼 추가한 JPanel JFrame 남쪽에 추가
		this.add("Center", jsp_log);					// JFrame 중앙에 JScrollPane 추가
		this.setSize(700, 550);
		this.setVisible(true);
		this.setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // X표시로 닫으면 완전히 종료(메모리에서 지움)
		setLocationRelativeTo(null); 					// 창 가운데 뜨도록 설정
	}

	//////////////////////// 현재접속자 보여주는 창 UI //////////////////////////
	public void initDisplay2() {	
		jbtn_expulsion.addActionListener(this);
		jbtn_expulsion.setFont(font);
		jtf_userCount.setFont(font);
		jtf_userCount.setEditable(false); 				// 접속인원 수정할 수 없도록 설정
		jp_south2.add(jbtn_expulsion);	 
		
		frame2.add("North", jtf_userCount);				// view에 현재 접속인원 보여주는 jtf
		frame2.add("Center", jsp);
		frame2.add("South", jp_south2);
		frame2.setSize(500, 550);
		frame2.setVisible(true);
		
		jp_south2.setLayout(new FlowLayout(FlowLayout.CENTER));    
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			   // X표시로 닫으면 완전히 종료(메모리에서 지움)
		jtb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 한개의 로우만 선택가능
		frame2.setResizable(false); // 크기변경X

	}

	/////////////////////////////  메인메소드 ///////////////////////////////////////
	public static void main(String[] args) {
		new TalkServerView();	
	}
	/////////////////////////////////////////////////////////////////////////////////
	/********************************************************************************************
		1. 이벤트처리는 SocketThread에서 컨트롤러 역할하여 뷰에대한 이벤트를 처리한다
		2. View 담당 개발자는 이벤트 처리에 대한 메소드 이름만 알고 있으면 동시 개발이 가능해 지고
		    각자의 업무에만 집중할 수 있다.
		3. 또한 업무별로 소스를 나눔으로써 유지보수가 용이해진다 
	 ********************************************************************************************/
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// 로그저장 이벤트
		if (obj == jbtn_log) {
			sk.log();

		// 공지사항 알림 버튼 이벤트
		} else if (obj == jbtn_notice) {
			String notice_msg = JOptionPane.showInputDialog("공지사항을 입력하세요.");
			sk.notice(notice_msg);				   // 입력받은 msg를 notice()의 파라미터로 넘겨줌
		}
		// 회원조회 버튼 이벤트
		else if(obj == jbtn_memSearch) {
			System.out.println("회원조회 클릭됨"); // 만드는 중입니다
			msView.initDisplay();
			
			
		}
		// 현재 접속자 버튼 이벤트
		else if(obj == jbtn_user) {
			initDisplay2();				
			sk.showNumber_Conpeople();

		}	
		// 접속자 강퇴 버튼 이벤트
		else if (obj == jbtn_expulsion) {
			if (sk.globalList.size() != 0 && jtb.getSelectedRow() > -1) {
				int select = jtb.getSelectedRow(); //JTable에서 select한 로우수 반환
				sk.expulsion(select);			   // 반환받은 select값 expulsion()의 파라미터로 넘겨줌
			} 
				
			
		}
	} // end of actionPerformed
	
	public void successMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	public void errorMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error!", JOptionPane.ERROR_MESSAGE);
	}
}	// end of TalkServerView