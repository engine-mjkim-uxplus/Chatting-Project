package chatClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ChangeView extends JFrame implements ActionListener {
	/****************************************
	 * 선언부 *
	 ****************************************/
	Controller controller = Controller.getInstance();

	// J입력
	JLabel jlb_id = new JLabel("아이디"); // 아이디 라벨
	JLabel jlb_pw = new JLabel("현재 PW"); // 현재 비밀번호 라벨
	JLabel jlb_changepw = new JLabel("PW 변경"); // 비번변경 라벨
	JLabel jlb_repw = new JLabel("PW 확인"); // 비번변경 라벨
	JLabel jlb_nick = new JLabel("닉네임 변경"); // 닉네임변경 라벨

	JTextField jtf_id = null; // 로그인에서 받아오는 아이디
	JPasswordField jpf_pw = new JPasswordField(""); // 현재 비밀번호 입력칸
	JPasswordField jpf_changepw = new JPasswordField(""); // 변경 비밀번호 입력칸
	JPasswordField jpf_repw = new JPasswordField(""); // 재확인 입력칸
	JTextField jtf_nick = new JTextField(""); // 닉네임 텍스트필드

	// J버튼
	// 이미지 경로를 확인 해주세요.
	String imgPath = "./img/";
	JButton jbtn_update = new JButton(new ImageIcon(imgPath + "변경하기.png")); // 업데이트 버튼
	JButton jbtn_del = new JButton("아이디 삭제"); // 아이디 삭제 버튼
	JButton jbtn_pwok = new JButton(new ImageIcon(imgPath + "버튼.png")); // 비번 체크 버튼
	// 폰트설정
	Font jl_font = new Font("맑은고딕체", Font.BOLD, 17); // 폰트를 파라미터 값(글씨체, 폰트굵기, 크기)으로 생성한다.

	private MemberVO mVO = null;
	ChatView chatview = null;
	private int select = -1;

	/****************************************
	 * 생성자 *
	 ****************************************/
	public ChangeView(ChatView chatview) {
		this.chatview = chatview;
		mVO = controller.action(new MemberVO("check", chatview.nickName));
		jtf_id = new JTextField(mVO.getMem_id());
		initDisplay();
		successMsg("비밀번호를 입력 해주세요");
	}

	public ChangeView() {
		initDisplay();
	}

	/****************************************
	 * 화면처리 *
	 ****************************************/
	public void initDisplay() {
		////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////

		this.setLayout(null); // 레이아웃
		this.setTitle("꽉자바 ver.1"); // 타이틀 붙이기
		this.setSize(450, 550); // 실행창 크기
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ?
		this.setVisible(true); // 보인다.
		this.setLocation(600, 150); // 실행시 위치지정
		this.addWindowListener(new WindowAdapter() { // 익명리스너
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		jbtn_pwok.addActionListener(this); // 비번확인버튼의 이벤트를 듣는다.
		jbtn_update.addActionListener(this); // 변경하기 버튼
		jbtn_del.addActionListener(this);

		//////////////////////////// 아이디 /////////////////////////////////
		// 아이디
		jlb_id.setBounds(40, 40, 130, 40); // 위치지정
		jlb_id.setFont(jl_font); // 폰트세팅
		this.add(jlb_id); // 붙이기
		// 아이디 텍스트
		jtf_id.setBounds(140, 40, 200, 40); // 위치지정
		jtf_id.setEnabled(false); // 디폴트 : 비활성화 상태
		this.add(jtf_id); // 붙이기
		//////////////////////////// 아이디 /////////////////////////////////

		/////////////////////////// 비밀번호 ////////////////////////////////
		// 현재 비밀번호
		jlb_pw.setBounds(40, 100, 130, 40); // 위치지정
		jlb_pw.setFont(jl_font); // 폰트세팅
		this.add(jlb_pw); // 붙이기
		// 현재 비밀번호 텍스트입력칸
		jpf_pw.setBounds(140, 100, 200, 40); // 위치지정
		jpf_pw.setEnabled(true); // 디폴트 : 비활성화 상태
		this.add(jpf_pw); // 붙이기

		// 비밀번호 변경
		jlb_changepw.setBounds(40, 160, 130, 40); // 위치지정
		jlb_changepw.setFont(jl_font); // 폰트세팅
		this.add(jlb_changepw); // 붙이기
		// 비밀번호 텍스트입력칸
		jpf_changepw.setBounds(140, 160, 200, 40); // 위치지정
		jpf_changepw.setEnabled(false); // 디폴트 : 비활성화 상태
		this.add(jpf_changepw); // 붙이기

		// 비번재확인
		jlb_repw.setBounds(40, 220, 130, 40); // 위치지정
		jlb_repw.setFont(jl_font); // 폰트세팅
		this.add(jlb_repw); // 붙이기
		// 비번재확인 텍스트입력칸
		jpf_repw.setBounds(140, 220, 200, 40); // 위치지정
		jpf_repw.setEnabled(false); // 디폴트 : 비활성화 상태
		this.add(jpf_repw); // 붙이기

		// 비밀번호 확인버튼
		jbtn_pwok.setBounds(355, 105, 28, 28); // 위치지정
		this.add(jbtn_pwok); // 붙이기
		/////////////////////////// 비밀번호 ///////////////////////////////

		/////////////////////////// 닉네임 ////////////////////////////////
		// 닉네임
		jlb_nick.setBounds(40, 280, 130, 40); // 위치지정
		jlb_nick.setFont(jl_font); // 폰트세팅
		this.add(jlb_nick); // 붙이기
		// 닉네임 텍스트필드
		jtf_nick.setBounds(140, 280, 200, 40); // 위치지정
		jtf_nick.setEnabled(false); // 디폴트 : 비활성화 상태
		this.add(jtf_nick); // 붙이기

		// 변경하기 버튼
		jbtn_update.setBounds(160, 350, 140, 40); // 위치지정
		jbtn_update.setEnabled(false); // 디폴트 : 비활성화 상태
		this.add(jbtn_update); // 붙이기

		// 아이디 삭제 버튼
		jbtn_del.setBounds(160, 420, 140, 40); // 위치지정
		jbtn_del.setFont(jl_font); // 폰트지정
		jbtn_del.setEnabled(false); // 디폴트 : 비활성화 상태
		jbtn_del.setBackground(new Color(158, 9, 9)); // 색지정
		jbtn_del.setForeground(new Color(212, 212, 212)); // 색지정
		this.add(jbtn_del);

	}///////////////// [end of initDisplay]////////////////

	public void successMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Success!", JOptionPane.INFORMATION_MESSAGE);
	}

	public void errorMsg(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error!", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// 비밀번호 확인 버튼
		if (obj == jbtn_pwok) {
			if (jpf_pw.getText().equals(mVO.getMem_pw())) {
				jpf_pw.setEnabled(false);
				successMsg("비밀번호 확인 성공");
				String[] selects = { "비밀번호", "닉네임", "All" };
				select = JOptionPane.showOptionDialog(null, "어떤 데이터를 수정하시겠습니까?", "데이터를 선택해주세요.", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, selects, "All");
				if (select == 0) {
					jbtn_del.setEnabled(true);
					jbtn_update.setEnabled(true);
					jpf_changepw.setEnabled(true);
					jpf_repw.setEnabled(true);
				} else if (select == 1) {
					jbtn_del.setEnabled(true);
					jbtn_update.setEnabled(true);
					jtf_nick.setEnabled(true);
				} else if (select == 2) {
					jpf_changepw.setEnabled(true);
					jpf_repw.setEnabled(true);
					jtf_nick.setEnabled(true);
					jbtn_del.setEnabled(true);
					jbtn_update.setEnabled(true);
				}
			} else {
				errorMsg("비밀번호 확인 실패");
			}
		}

		if (obj == jbtn_update) {
			String pw = jpf_changepw.getText();
			String repw = jpf_repw.getText();
			String nickName = jtf_nick.getText();
			switch (select) {
			case 0: {
				// 비밀번호인 경우
				if (!pw.equals("")) {
					if (passwordCheck(pw, repw)) {
						MemberVO pmVO = new MemberVO();
						pmVO.setCommand("update");
						pmVO.setMem_id(mVO.getMem_id());
						pmVO.setMem_pw(pw);
						pmVO.setResult(1);
						controller.action(pmVO);
						mVO.setMem_pw(pw);
						successMsg("수정 성공!");
						dispose();
					}
				} else if (pw.equals("") || repw.equals("")) {
					errorMsg("작성하지않은 항목이 있습니다.1");
				}
			}
				break;
			case 1: {
				// 닉네임인경우
				if (!nickName.equals("")) {
					MemberVO pmVO = new MemberVO();
					pmVO.setCommand("update");
					pmVO.setMem_id(mVO.getMem_id());
					pmVO.setMem_name(nickName);
					pmVO.setResult(2);
					controller.action(pmVO);
					chatview.changeNickName(nickName);
					mVO.setMem_name(nickName);
					successMsg("수정 성공!");
					dispose();
				} else if (nickName.equals("")) {
					errorMsg("작성하지않은 항목이 있습니다.2");
				}
			}
				break;
			case 2: {
				// 둘다 인 경우
				if (!nickName.equals("") && !pw.equals("")) {
					MemberVO pmVO = new MemberVO();
					pmVO.setCommand("update");
					pmVO.setMem_id(mVO.getMem_id());
					pmVO.setMem_pw(pw);
					pmVO.setMem_name(nickName);
					pmVO.setResult(3);
					controller.action(pmVO);
					chatview.tc.changeNickName(nickName);
					mVO.setMem_pw(pw);
					mVO.setMem_name(nickName);
					successMsg("수정 성공!");
					dispose();
				} else if (nickName.equals("") || pw.equals("") || repw.equals("")) {
					errorMsg("작성하지않은 항목이 있습니다.3");
				}
			}
				break;
			}
		}

		if (obj == jbtn_del) {
			int result = JOptionPane.showConfirmDialog(this, "정말로 삭제 하시겠습니까?", "경고!", JOptionPane.YES_NO_OPTION);
			if (result == 0) {
				String pw = JOptionPane.showInputDialog("비밀번호를 다시 입력해주세요");
				if (mVO.getMem_pw().equals(pw)) {
					mVO.setCommand("delete");
					controller.action(mVO);
					successMsg("아이디가 삭제 되었습니다.");
					dispose();
					chatview.tc.roomOut();
					chatview.dispose();
				} else {
					errorMsg("비밀번호가 틀렸습니다.");
				}
			}
		}
	}////////////////// [end of actionPerformed]//////////////////////

	public boolean passwordCheck(String pw, String repw) {
		boolean isOk = false;
		if (pw.equals(repw)) {
			isOk = true;
		} else {
			jpf_repw.setText("");
			errorMsg("비밀번호가 다릅니다 다시 입력해주세요.");
		}
		return isOk;
	}

	public static void main(String[] args) {
		new ChangeView();
	}

}///////////////// [end of class changeView]////////////////
