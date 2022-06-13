package chatClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginView extends JFrame implements ActionListener {
	JPanel jp_center = new JPanel();
	JTextField jtf_id = new JTextField("", 10);
	JTextField jtf_pw = new JTextField("", 10);
	JPanel jp_east = new JPanel();
	JButton jbtn_login = new JButton("로그인");
	boolean isDisplay = true;
	
	String user_Id;    // 톡클라이언트에게 넘기기 위해 전역변수로 설정
	String user_Name;  // 톡클라이언트에게 넘기기 위해 전역변수로 설정
	

	LoginView() {
		initDisplay(isDisplay);				
		jbtn_login.addActionListener(this);
		jtf_id.addActionListener(this);
		jtf_pw.addActionListener(this);
	}

	public void initDisplay(boolean is) {
		jp_center.setBackground(Color.orange);
		jp_center.setLayout(new GridLayout(2, 1));
		jp_center.add(jtf_id);
		jp_center.add(jtf_pw);
		jp_east.setBackground(Color.green);
		jp_east.setLayout(new BorderLayout());
		jp_east.add("Center", jbtn_login);
		this.setTitle("토마토톡 로그인");
		this.setSize(250, 100);
		this.add("Center", jp_center);
		this.add("East", jp_east);
		this.setVisible(isDisplay);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // x표시 누르면 완전히 종료 되도록
		setLocationRelativeTo(null); // 창 가운데서 뜨도록 설정
	}


	public static void main(String[] args) {
		new LoginView();		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// JTextField 엔터 이벤트 처리
		if(jtf_id != null && jtf_pw != null) {
			if(jtf_id == e.getSource() || jtf_pw == e.getSource()) {
				System.out.println("로그인 호출 성공");
				// 사용자가 화면에 입력하는 아이디를 담기
				user_Id = jtf_id.getText();
				// 사용자가 화면에 입력하는 비번을 담기
				String user_pw = jtf_pw.getText();
				// 오라클 서버에서 반환 값 담기
				int result = 0; // 이름(1) or 0(비번이 틀림) or -1(아이디가 존재하지 않음)
				LoginDao loginDao = new LoginDao();
				// 사용자가 입력한 아이디와 비번을 Dao클래스의 login메소드에 파라미터로 넘김
				result = loginDao.login(user_Id, user_pw);
				// 위에서 오라클 서버에 요청한 결과를 출력하기
				user_Name = loginDao.name(user_Id);
				System.out.println("result : " + result);
				
				if(result==1) {
					isDisplay = false;
					this.initDisplay(isDisplay);
					TalkClient tc = new TalkClient(this);
					tc.initDisplay(tc.is);
					tc.init();
				} else if ( result ==0) {
					JOptionPane.showMessageDialog(this
							   , "비밀번호가 틀렸습니다"
								, "ERROR", JOptionPane.ERROR_MESSAGE);
								return;
				} else if (result == -1) {
				JOptionPane.showMessageDialog(this
						   , "존재하지 않는 아이디입니다."
							, "ERROR", JOptionPane.ERROR_MESSAGE);
							return;
				}
				
			}
		}
				
		if (jbtn_login == e.getSource()) {
			System.out.println("로그인 호출 성공");
			// 사용자가 화면에 입력하는 아이디를 담기
			user_Id = jtf_id.getText();
			// 사용자가 화면에 입력하는 비번을 담기
			String user_pw = jtf_pw.getText();
			// 오라클 서버에서 반환 값 담기
			int result = 0; // 이름(1) or 0(비번이 틀림) or -1(아이디가 존재하지 않음)
			LoginDao loginDao = new LoginDao();
			// 사용자가 입력한 아이디와 비번을 Dao클래스의 login메소드에 파라미터로 넘김
			result = loginDao.login(user_Id, user_pw);
			// 위에서 오라클 서버에 요청한 결과를 출력하기
			user_Name = loginDao.name(user_Id);
			System.out.println("result : " + result);
			
			if(result==1) {
				isDisplay = false;
				this.initDisplay(isDisplay);
				TalkClient tc = new TalkClient(this);
				tc.initDisplay(tc.is);
				tc.init();
			} else if ( result ==0) {
				JOptionPane.showMessageDialog(this
						   , "비밀번호가 틀렸습니다"
							, "ERROR", JOptionPane.ERROR_MESSAGE);
							return;
			} else if (result == -1) {
			JOptionPane.showMessageDialog(this
					   , "존재하지 않는 아이디입니다."
						, "ERROR", JOptionPane.ERROR_MESSAGE);
						return;
			}
			
		}

	}

}