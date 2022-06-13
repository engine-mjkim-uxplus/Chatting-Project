package chatServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DB연동하여 쿼리문 실행 클래스

public class ChatDao {

	Connection con;
	PreparedStatement pst;
	ResultSet rs;
	List<ChatMsgVO> list;
	

// 회원가입 메소드(signUp)
	public void signUp(String id, String pwd, String name) {
		String sql = "insert into chatmember values(?,?,?)";

		con = DButil.getOracleConnection(); // DButil에서 예외처리 했으므로 따로 해주지 않아도 됨
		PreparedStatement pst;
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, id);
			pst.setString(2, pwd);
			pst.setString(3, name);
			int i = pst.executeUpdate(); 
			System.out.println("데이터 " + i + "건이 추가되었습니다");
			DButil.close(con, pst);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

// 아이디 중복검사 메소드(idCheck)
	public int idCheck(String id) {
		String sql = "SELECT ID FROM MEM WHERE ID = ?"; // id가 있다면
		int result = 0;

		con = DButil.getOracleConnection();
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				result = 0; // 아이디 중복이 있다면 0반환
			} else
				result = 1; // 중복이 있다면 1반환
			DButil.close(con, pst, rs); // 닫기
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

// 로그인 확인 메소드
// 로그인 성공시 1반환, 비밀번호 불일치 2반환, 아이디 없을 경우 3반환 
	public int login(String id, String pwd) {
		String sql = " SELECT * FROM MEM WHERE ID = ?";
		int result = 0;
		Connection con = DButil.getOracleConnection();
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, id);
			rs = pst.executeQuery();

			if (rs.next()) {
				// 조회된 아이디가 있을 경우 1.비밀번호가 일치하거나 , 2. 비밀번호가 일치하지 않거나
				if (rs.getString("pwd").equals(pwd)) { // String의 오버라이딩 된 equals로 비밀번호 확인
					System.out.println("로그인 합니다"); // 비밀번호 일이
					return 1;
				} else {
					System.out.println("비밀번호가 틀렸습니다");
					result = 2;
				}

			} else {
				System.out.println("입력하신 아이디는 존재하지 않습니다");
				result = 3;
			}
			DButil.close(con, pst, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

// 대화내용 테이블에 저장	
	public void chatMsg(String nicname, String msg, String days, String hours) {
		String sql = "insert into chatlog values(?,?,?,?)";

		con = DButil.getOracleConnection(); // DButil에서 예외처리 했으므로 따로 해주지 않아도 됨
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, nicname);
			pst.setString(2, msg);
			pst.setString(3, days);
			pst.setString(4, hours);
			int i = pst.executeUpdate(); // exceuteUpdate해줘야 쿼리문 실행
			System.out.println(" 데이터 " + i + "건이 추가되었습니다."); ////////// Test
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pst);
		}

	}
	// 대화내용 백업
	public List<ChatMsgVO> list() {
		String sql = "select chatmsg, nicname, days, hours from chatlog ";

		try {
			con = DButil.getOracleConnection();
			pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			list = new ArrayList<>();
			while (rs.next()) {
				String chatmsg = rs.getString("chatmsg");
				String nicname = rs.getString("nicname"); // 데이터베이스의 컬럼이 대문자인지 소문자인지 구분하지 않는다.
				String days = rs.getString("days");
				String hours = rs.getString("hours");
				ChatMsgVO msg = new ChatMsgVO(chatmsg, nicname, days, hours);
				list.add(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con,pst,rs);
		}
		return list;
	}

}
