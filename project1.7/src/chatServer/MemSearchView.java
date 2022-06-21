package chatServer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import chatClient.MemberVO;

public class MemSearchView extends JFrame implements ActionListener,FocusListener,ItemListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel			  jp_north 	   = new JPanel();
	JButton 		  jbtn_sel 	   = new JButton("조회");
	JButton 		  jbtn_del 	   = new JButton("삭제");
	JLabel			  jb_id	  	   = new JLabel("아이디");
	String 			  search_item[] = {"검색조건","아이디","이름","전화번호"};
	String			  search	   = null;
	String			  title		   = "검색 조건을 선택하고 입력하세요";
	JComboBox		  jcb	   	   = new JComboBox(search_item);
	JTextField		  jtf_search   = new JTextField(title);
	JLabel			  jb_phone	   = new JLabel("");
	JLabel			  jb_name	   = new JLabel("");
	String cols[] = {"아이디","이름","전화번호"}; // header부분
	String data[][] = new String[0][3]; //body부분
	DefaultTableModel dtm = new DefaultTableModel(data,cols);
	JTable			  jtb = new JTable(dtm);
	JScrollPane		  jsp  = new JScrollPane(jtb);
	Font			  font = new Font("나눔고딕",Font.BOLD,15);
	ChatDao			  chatDao	= new ChatDao();	
	
	public void initDisplay() {
		jcb.addItemListener(this);
		jtf_search.addActionListener(this);
		jtf_search.addFocusListener(this);
		jbtn_sel.addActionListener(this);
		jp_north.setLayout(new BorderLayout());
		jp_north.add("West",jcb);
		jp_north.add("Center",jtf_search);
		jp_north.add("East",jbtn_sel);
		this.add("North",jp_north);
		this.add("Center",jsp);
		this.setTitle("회원 조회");
		this.setSize(600,500);
		this.setVisible(true);
		this.setResizable(false);
		}
	
	// 데이터 초기화 메소드
	public void refleshData() {
		while(dtm.getRowCount() > 0) {
			dtm.removeRow(0);
		}
	}
	
	// 조죄한 데이터 dtm에 추가하는 메소드
	public void dataAdd(List<MemberVO> list) {
		refleshData();
		Vector<String>oneRow= null;
		for(int i = 0; i<list.size(); i++) {
			 oneRow = new Vector<>();
			 MemberVO mvo = list.get(i);
			 String user_id = mvo.getMem_id();
			 String user_name = mvo.getMem_name();
			 String user_phone = mvo.getMem_phone();
			 oneRow.add(user_id);
			 oneRow.add(user_name);
			 oneRow.add(user_phone);
			 dtm.addRow(oneRow);
		 }	
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {  // 현재 작성중입니다.
		Object obj = e.getSource();
		if(jbtn_sel == obj || jtf_search == obj) {
			if(!(jtf_search.getText().equals(title)) && jtf_search.getText().length() > 0 ) {
				if(search != null) {
					if(search.equals("아이디")) {
						List<MemberVO> list = new ArrayList<>();
						String input = jtf_search.getText();
						System.out.println(input); // 단위 테스트
						list = chatDao.memSearch(search, input);
						dataAdd(list); // 테이블에 추가
											
					System.out.println("아이디로 검색");	
				} else if(search.equals("이름")) {
					List<MemberVO> list = new ArrayList<>();
					String input = jtf_search.getText();
					list = chatDao.memSearch(search, input);
					dataAdd(list);
					System.out.println("이름으로 검색");		
				} else if(search.equals("전화번호")) {
					List<MemberVO> list = new ArrayList<>();
					String input = jtf_search.getText();
					list = chatDao.memSearch(search, input);
					dataAdd(list);
					System.out.println("전화번호로 검색");
				}				
			}else {
				JOptionPane.showMessageDialog(this, "검색할 조건을 선택하세요");
			}
			}
			else {
				JOptionPane.showMessageDialog(this, "검색할 조건을 입력하세요");
			}
				
		}
		
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == jtf_search) {
			jtf_search.setText("");
			
			
		}
		
	}
	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();
		if(obj == jcb) {
			if(e.getStateChange() == ItemEvent.SELECTED) { 
				System.out.println("선택한 ITEM ===> "+search_item[jcb.getSelectedIndex()]);
				search = search_item[jcb.getSelectedIndex()]; // search값 얻어냄
				System.out.println(search);
				}
		}
	} // end of	itemStateChanged
} // end of MemSearchView	


