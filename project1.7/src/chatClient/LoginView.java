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
// LoginView
public class LoginView extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//선언부
	String imgPath = "C:\\Users\\MJ\\Desktop\\이미지\\";
	JLabel jlb_id = new JLabel("아이디");
	JLabel jlb_pw = new JLabel("패스워드");
	Font jl_font = new Font("맑은고딕체", Font.BOLD, 14);
	JTextField jtf_id = new JTextField("");
	JPasswordField jpf_pw = new JPasswordField("");
	JButton jbtn_login = new JButton(new ImageIcon(imgPath + "로그인2.png"));
	JButton jbtn_join = new JButton(new ImageIcon(imgPath + "회원가입2.png"));
	// JPanel에 쓰일 이미지아이콘
	ImageIcon ig = new ImageIcon(imgPath + "둥이.png");
	// 컨트롤러 싱긍톤으로 생성
	
	Controller controller = Controller.getInstance();
	public LoginView() {
		initDisplay();
	}

	/* 배경이미지 */
	class mypanal extends JPanel {
		private static final long serialVersionUID = 1L;
		
		public void paintComponent(Graphics g) {
			g.drawImage(ig.getImage(), 0, 0, null);
			setOpaque(false);
			super.paintComponents(g);
		}
	}

	// 화면처리
	public void initDisplay() {
		setContentPane(new mypanal());
		/* 버튼과 텍스트필드 구성 */
		/* 회원가입 버튼 */
		jbtn_join.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == jbtn_join) {
					dispose();
					new SignUpView();
				}
			}
		});
		jbtn_login.addActionListener(this);
		jtf_id.addActionListener(this);
		jpf_pw.addActionListener(this);

		this.setLayout(null);
		this.setTitle("꽉자바 ver.1");
		this.setSize(427, 443);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLocation(600, 150);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// id 라인
		jlb_id.setBounds(45, 200, 80, 40);
		jlb_id.setFont(jl_font);
		this.add(jlb_id);

		// 아이디 입력창
		jtf_id.setBounds(110, 200, 185, 40);
		this.add(jtf_id);

		// pw 라인
		jlb_pw.setBounds(45, 240, 80, 40);
		jlb_pw.setFont(jl_font);
		this.add(jlb_pw);

		// pw 입력창
		jpf_pw.setBounds(110, 240, 185, 40);
		this.add(jpf_pw);

		// 로그인 버튼 라인
		jbtn_login.setBounds(175, 285, 120, 40);
		this.add(jbtn_login);

		// 회원가입 버튼 라인
		jbtn_join.setBounds(45, 285, 120, 40);
		this.add(jbtn_join);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// JTextField 엔터 이벤트 처리
		if (jtf_id == e.getSource() || jpf_pw == e.getSource() || jbtn_login == e.getSource()) {
			if (!(jtf_id.getText().equals("")) && !(jpf_pw.getText().equals(""))) {
				System.out.println("로그인 호출 성공");
				MemberVO pmVO = new MemberVO();
				pmVO.setCommand("login");
				pmVO.setMem_id(getId());
				pmVO.setMem_pw(getPw());
				MemberVO rsVO = new MemberVO(); // 리턴받을 rsVO생성
				rsVO = controller.action(pmVO); // return값 rsVO
				String nickName = rsVO.getMem_name();
				System.out.println("result : " + nickName);
					if (nickName.equals("0")) {
						errorMsg("비밀번호가 틀렸습니다!");
						return;
					} else if (nickName.equals("-1")) {
						errorMsg("존재하지 않는 아이디입니다.");
						return;
					}else {
						TalkClient tc = new TalkClient(nickName);
						new ChatView(tc);
						tc.init();
						dispose();
					}
			} else if (jtf_id.getText().equals("")) {
				errorMsg("아이디를 입력 해주세요");
				return;

			} else if (jpf_pw.getText().equals("")) {
				errorMsg("비밀번호를 입력 해주세요");
				return;
			}
		}
	
	}
	public void successMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Success!", JOptionPane.INFORMATION_MESSAGE);
	}
	public void errorMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	public String getId() {
		return jtf_id.getText();
	}
	public String getPw() {
		return jpf_pw.getText();
	}
	
	public static void main(String[] args) {
		new LoginView();
		
	}
}
