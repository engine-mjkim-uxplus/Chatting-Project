package chatServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 서버에서 쓸 dao클래스
public class ChatDao {

	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	List<ChatMsgVO> list;

// 대화내용 테이블에 저장	
	public void chatMsg(String nicname, String msg, String days, String hours) {
		String sql = "insert into chatlog values(?,?,?,?)";

		con = DButil.getOracleConnection(); // DButil에서 예외처리 했으므로 따로 해주지 않아도 됨
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, nicname);
			pstmt.setString(2, msg);
			pstmt.setString(3, days);
			pstmt.setString(4, hours);
			int i = pstmt.executeUpdate(); // exceuteUpdate해줘야 쿼리문 실행
			System.out.println(" 데이터 " + i + "건이 추가되었습니다."); ////////// Test
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pstmt);
		}

	}

	// 대화내용 백업
	public List<ChatMsgVO> list() {
		String sql = "select chatmsg, nicname, days, hours from chatlog ";

		try {
			con = DButil.getOracleConnection();
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
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
			DButil.close(con, pstmt, rs);
		}
		return list;
	}

}
