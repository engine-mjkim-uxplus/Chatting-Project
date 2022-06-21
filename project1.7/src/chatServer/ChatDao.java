package chatServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chatClient.MemberVO;

// 서버에서 쓸 dao클래스
public class ChatDao {

	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	List<ChatMsgVO> list;
	
// 대화내용 테이블에 저장	
	public void chatMsg(String nicname, String msg, String days, String hours) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into chatlog values(?,?,?,?)");
//		String sql = "insert into chatlog values(?,?,?,?)";

		con = DButil.getConnection(); // DButil에서 예외처리 했으므로 따로 해주지 않아도 됨
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, nicname);
			pstmt.setString(2, msg);
			pstmt.setString(3, days);
			pstmt.setString(4, hours);
			int i = pstmt.executeUpdate(); // exceuteUpdate해줘야 쿼리문 실행
			System.out.println(" 데이터 " + i + "건이 추가되었습니다."); ////////// Test
		} catch (SQLException se) {
			se.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pstmt);
		}

	}
	// 대화내용 백업
	public List<ChatMsgVO> list() {
		StringBuilder sql = new StringBuilder();
		sql.append("select chatmsg, nicname, days, hours from chatlog ");
//		String sql = "select chatmsg, nicname, days, hours from chatlog ";

		try {
			con = DButil.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
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
	/**************************************************************************
	 * MemSearchView 클래스의 회원조회 메소드
	 * @param search : 콤보박스에서 선택된 검색조건. 아이디 || 이름 || 전화번호
	 * @param input  : 콤보박스 선택후 사용자가 입력한 값
	 * @return List<MemberVO>
	 ***************************************************************************/
	
	public List<MemberVO> memSearch(String search, String input) {
		List<MemberVO> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ID, PW, NAME, PHONE FROM MEMBER WHERE");
		
		// PreparedStatement는 필드 바인딩이 되지 않는다. 즉 컬럼명은 '?' 처리할 수 없다.
		// 그러므로 컬러명 동적처리를 위해 밑과 같이 처리
		if(search.equals("아이디")) {
			sql.append(" ID LIKE '%' || ? || '%'");
		} else if (search.equals("이름")) {
			sql.append(" NAME LIKE '%' || ? || '%'");
		} else if (search.equals("전화번호")) {
			sql.append(" PHONE LIKE '010-'|| ? || '%'");
		}
       
        con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, input);
			rs= pstmt.executeQuery();
			MemberVO mvo  = null;
			while (rs.next()) { // 아이디가 일치하고 비밀번호가 일치할 경우 1, 비밀번호 틀렸을 경우 0 반환, 아이디 없을경우 -1
				String user_id = rs.getString("ID");
				String user_name = rs.getString("NAME");
				String user_phone = rs.getString("PHONE");
				mvo = new MemberVO();
				mvo.setMem_id(user_id);
				mvo.setMem_name(user_name);
				mvo.setMem_phone(user_phone);
				list.add(mvo);
				}
			// false면 list.size가 0
		} catch (SQLException e) {
			e.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			DButil.close(con, pstmt, rs);
		}
        return list;
	}
	
}
