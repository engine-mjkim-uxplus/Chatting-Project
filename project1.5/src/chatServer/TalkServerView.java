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
	/////////////////////////////선언부////////////////////////////
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
	JButton 				  jbtn_user		 		 = 		new JButton("접속인원");
	Font 					  font;
	Vector<Object> 			  v 					 = 		new Vector<>(); // 각 사용자 닉네임, ip, 시간 담는 백터
	String 					  logPath				 = 		"./txt/";

	////////////////// 현재 접속 인원 관리 ///////////////////////////
	JFrame 					  frame2 			 	 = 		new JFrame("현재 접속인원");
	JButton 			      jbtn_expulsion		 =      new JButton("클라이언트 접속끊기");
	JPanel 					  jp 				     = 		new JPanel();
	JPanel 				      jp_south2 			 = 		new JPanel();
	String 					  cols[]			     = 		{ "접속 닉네임", "IP", "접속시간" };                                                           
	String 					  data[][]			 	 = 	    new String[0][3];      
	DefaultTableModel 		  dtm 				     = 		new DefaultTableModel(data, cols);                                       
	JTable 					  jtb 				     =		new JTable(dtm);                     
	JScrollPane 			  jsp				     = 		new JScrollPane(jtb);                                           
	SocketThread			  sk 					 =		null;                                     
	
	/////////////////////////////생성자////////////////////////////
	public TalkServerView() {
		jbtn_notice.addActionListener(this); // 공지사항 이벤트
		jbtn_log.addActionListener(this); 	 // 로그저장 이벤트
		jbtn_memSearch.addActionListener(this);
		jbtn_user.addActionListener(this);
		initDisplay(); 					 	 // 서버 UI
		// Runnable을 구현한 객체를 넘겨준다.
		this. sk = new SocketThread(this);
		jbtn_expulsion.addActionListener(this);
		sk.start();
	}

	public void initDisplay() {
		this.setTitle("채팅프로그램 서버");
		// jp_south.setLayout(new GridLayout(1,0));
		jp_south.setLayout(new FlowLayout(FlowLayout.CENTER)); // 각 컴포넌트의 크기 동일하게 센터에 나오도록 배치
		// jta_log.setBackground(Color.orange);
		// 각 컴포넌트의 폰트 설정
		font = new Font("고딕", Font.BOLD, 15); // 폰트사용
		jta_log.setFont(font);
		jbtn_log.setFont(font);
		jbtn_notice.setFont(font);
		jbtn_memSearch.setFont(font);
		jbtn_user.setFont(font);
		jtf_userCount.setFont(font);

		jp_south.add(jbtn_log); // 로그버튼 추가
		jp_south.add(jbtn_notice); // 알림버튼 추가
		jp_south.add(jbtn_memSearch); // 회원조회
		jp_south.add(jbtn_user); // 현재 접속인원
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
		jbtn_expulsion.addActionListener(this);
		jbtn_expulsion.setFont(font);
		jp_south2.add(jbtn_expulsion);	
		frame2.add("Center", jsp);
		frame2.add("South", jp_south2);
		frame2.setSize(500, 500);
		frame2.setVisible(true);
		
		jp.setLayout(new BorderLayout());
		jp_south2.setLayout(new FlowLayout(FlowLayout.CENTER));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jtb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 한개의 로우만 선택가능
		frame2.setResizable(false); // 크기변경X

	}

	// 뷰 실행 메인
	public static void main(String[] args) {
		TalkServerView ts  = new TalkServerView();	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		// 로그저장 액션
		if (obj == jbtn_log) {
			String fileName = "log_" + sk.getDate() + ".txt";
			System.out.println(fileName);// log_2020-03-13.txt
			try {
				File f = new File(logPath + fileName); // 경로 + 파일이름
				PrintWriter pw = new PrintWriter(new BufferedWriter(// 필터클래스-카메라 필터
						new FileWriter(f.getAbsolutePath())));
				pw.write(jta_log.getText());
				pw.close();// 사용한 입출력 클래스는 반드시 닫아줌.
			} catch (Exception e2) {
				System.out.println(e2.toString());
			}
			// 서버에서 공지사항 알림 (프로토콜 203 )
		} else if (obj == jbtn_notice) {
			String notice = JOptionPane.showInputDialog("공지사항을 입력하세요.");
			if (notice == null || notice.trim().length() < 1) {
				JOptionPane.showMessageDialog(this, "메시지는 공백일 수 없습니다. 다시 입력하세요", "INFO",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			} else if (sk.globalList.size() != 0) {
				for (TalkServerThread tst : sk.globalList)
					tst.send(Protocol.NOTICE 
							+Protocol.seperator + "운영자"
							+Protocol.seperator + notice);
			} else if (notice != null && sk.globalList.size() == 0) {
				JOptionPane.showMessageDialog(this, "현재 접속중인 사용자가 없습니다", "INFO", JOptionPane.INFORMATION_MESSAGE);
			}

		}
		// 회원조회 이벤트
		if(obj == jbtn_memSearch) {
			System.out.println("회원조회 클릭됨");
			
		}
		// 접속인원 관리 이벤트
		if(obj == jbtn_user) {
			initDisplay2();
			if(sk.globalList.size() > 0) {
				while(dtm.getRowCount() > 0) {
					dtm.removeRow(0);
				}
				for(TalkServerThread tst : sk.globalList) {
					dtm.addRow(tst.oneRow);
				}
			}

		}	
		// 클라이언트 강퇴 이벤트 (프로토콜 501)
		if (obj == jbtn_expulsion) {
			if (sk.globalList.size() != 0 && jtb.getSelectedRow() > -1) {
				int select = jtb.getSelectedRow();
				String n = (String) dtm.getValueAt(select, 0);
				for (TalkServerThread tst : sk.globalList) {
					if (n.equals(tst.nickName)) {
						String msg = Protocol.EXPULSION
									+Protocol.seperator + tst.nickName;
						tst.broadCasting(msg); // 강퇴메시지
						sk.globalList.remove(tst);
						dtm.removeRow(select);
						jta_log.append(tst.nickName + "님을 강퇴하였습니다.\n");
						jtf_userCount.setText("현재 접속인원은 " + sk.globalList.size() + "명 입니다.");		
						break;
					// for each중 객체를 수정하면 ConcurrentModificationException 발생. 그러므로 수정후 꼭 break문으로 빠져 나갈 것	
					}
				}		
			} 
				
			
		}

	}
}