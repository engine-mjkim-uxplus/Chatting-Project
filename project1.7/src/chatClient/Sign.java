package chatClient;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Sign extends JFrame implements ActionListener {
	// 선언부
	String nickName = ""; //
	String imgPath = "C:\\\\Users\\\\MJ\\\\Desktop\\\\새 폴더"; // 이미지 경로를 문자열로..지정??
	JLabel jlb_id = new JLabel("아이디"); // "[입력]" : 문자열을 화면에 그림
	JLabel jlb_pw = new JLabel("비밀번호"); // "[입력]" : 문자열을 화면에 그림
	JLabel jlb_repw = new JLabel("비밀번호 재확인"); // "[입력]" : 문자열을 화면에 그림
	JLabel jlb_name = new JLabel("이름"); // "[입력]" : 문자열을 화면에 그림
	JLabel jlb_number = new JLabel("전화번호"); // "[입력]" : 문자열을 화면에 그림

	Font jl_font = new Font("맑은고딕체", Font.BOLD, 17);
	JTextField jtf_id = new JTextField("");
	JPasswordField jpf_pw = new JPasswordField("");
	JPasswordField jpf_repw = new JPasswordField("");
	JTextField jtf_name = new JTextField("");
	JTextField jtf_number = new JTextField("");

	JButton jbtn_idcheck = new JButton(new ImageIcon(imgPath + "/버튼.png")); // 버튼
	JButton jbtn_ok = new JButton(new ImageIcon(imgPath + "/가입하기.png"));

	// JPanel에 쓰일 이미지아이콘
	ImageIcon ig = new ImageIcon(imgPath + "/main4.png");
	boolean isIDCheck = false;
	LoginDao logindao;

	// 생성자
	public Sign() {
		initDisplay();
		jbtn_ok.addActionListener(this);
		jbtn_idcheck.addActionListener(this);

//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (e.getSource() == jbtn_ok) {
//					new Login();
//					dispose();
//				}
//			}
//		});

	}
	/////////////////////////////////////////////////////
	/* jpanal 오버라이드 */
	/////////////////////////////////////////////////////

	/* 배경이미지 */
	class mypana2 extends JPanel {
		public void paintComponent(Graphics g) {
			g.drawImage(ig.getImage(), 0, 0, null);
			setOpaque(false);
			super.paintComponents(g);
		}
	}

	/////////////////////////////////////////////////////
	/* 화면처리 */
	/////////////////////////////////////////////////////
	public void initDisplay() {
		setContentPane(new mypana2());

		/* 버튼과 텍스트필드 구성 */
//		jbtn_ok.addActionListener(this);
//		jbtn_but.addActionListener(this);
		this.setLayout(null);
		this.setTitle("꽉자바패밀리 ver.1"); // 타이틀 제목
		this.setSize(350, 600); // 로그인 창 사이즈 - 2개의 파라미터로 지정
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true); // 창을 보여준다.
		this.setLocation(600, 150); // 창이 처음 띄어지는 위치 - (800, 250)은 중앙
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// id 라인
		jlb_id.setBounds(40, 100, 80, 40);
		jlb_id.setFont(jl_font);
		jtf_id.setBounds(40, 130, 250, 40);
		this.add(jlb_id);
		this.add(jtf_id);

		// pw 라인
		jlb_pw.setBounds(40, 180, 80, 40);
		jlb_pw.setFont(jl_font);
		jpf_pw.setBounds(40, 213, 250, 40); // 모음유무에 따라 실제 값 차이는 없지만 시각적인 줄간격 오차가 나서 모음에는 3씩 추가
		this.add(jlb_pw);
		this.add(jpf_pw);

		// 비번 재확인
		jlb_repw.setBounds(40, 260, 150, 40);
		jlb_repw.setFont(jl_font);
		jpf_repw.setBounds(40, 293, 250, 40);
		this.add(jlb_repw);
		this.add(jpf_repw);

		// 이름
		jlb_name.setBounds(40, 340, 80, 40);
		jlb_name.setFont(jl_font);
		jtf_name.setBounds(40, 373, 250, 40);
		this.add(jlb_name);
		this.add(jtf_name);

		// 가입하기 버튼
		jbtn_ok.setBounds(105, 465, 120, 40);
		this.add(jbtn_ok);

		// 버튼
		jbtn_idcheck.setBounds(295, 135, 25, 25);
		this.add(jbtn_idcheck);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 중복아이디 없을 경우 1, 아이디 중복있어서 사용 못할 경우 -1
		if (jbtn_idcheck == e.getSource()) {
			System.out.println("중복검사 버튼 눌러짐"); // 단위테스트용
			String user_id = jtf_id.getText();
			logindao = new LoginDao();
			int result = logindao.idCheck(user_id); // id 중복체크 메소드(Dao)
			// 입력아이디 사용 가능(중복없음) = 1
			if (result == 1) {
				// 입력 아이디 사용가능
				// 입력한 아이디는 사용 못해
				// 다시 입력해야 된다.
				JOptionPane.showMessageDialog(null, "입력한 아이디는 사용할 수 있습니다.", "INFO", JOptionPane.INFORMATION_MESSAGE);
				jtf_id.setEditable(false);
				jbtn_idcheck.setEnabled(false);
				isIDCheck = true;
				jbtn_ok.setEnabled(isIDCheck);
				return;

			}
			// 입력아이디 사용 불가(중복있음) = -1
			else if (result == -1) {
				jtf_id.setText("");
				JOptionPane.showMessageDialog(null, "입력하신 아이디는 이미 존재하는 아이디입니다.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} // 회원가입 버튼 이벤트 처리
		if (jbtn_ok == e.getSource()) {
			System.out.println("회원가입 버튼 눌러짐"); // 단위테스트용
			
			// 중복검사 버튼이 눌러졌는 지 체크하는 조건문 추가해야함
			// 모든 텍스트 필드들이 공백이 아닐때만 회원가입 처리(나중에 db에서 반환값 던져서 처리할 것)		
			if (!(jtf_id.getText().equals("")) && !(jpf_pw.getText().equals("")) && !(jpf_repw.getText().equals(""))
					&& !(jtf_name.getText().equals(""))) {
				
				// 비밀번호 중복검사
				if (jpf_pw.getText().equals(jpf_repw.getText())) {
					String user_id = jtf_id.getText();
					String user_pw = jpf_pw.getText();
					String user_name = jtf_name.getText();
					logindao = new LoginDao();
					logindao.signup(user_id, user_pw, user_name);
					JOptionPane.showMessageDialog(null, "회원가입을 축하합니다!!", "INFO", JOptionPane.INFORMATION_MESSAGE);
					new Login();
					dispose();
				} else {
					JOptionPane.showMessageDialog(null, "입력한 비밀번호가 다릅니다. 다시 작성해 주세요", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			} 
			
			else {
				JOptionPane.showMessageDialog(null, "모든 입력창에 값을 입력해 주세요.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}
	}

}