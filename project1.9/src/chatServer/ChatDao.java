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
	List<MsgVO> list;
	/**************************************************************************
	 * 단체 대화방 메시지 저장 메소드
	 * @param msg
	 * @param days
	 * @param hours
	 **************************************************************************/
	public void chatMsg(ChatMsgVO cvo) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into GROUP_MSG_LOG values(?,?,?,?)");

		con = DButil.getConnection(); // DButil에서 예외처리 했으므로 따로 해주지 않아도 됨
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, cvo.getChatmsg());
			pstmt.setString(2, cvo.getNicname());
			pstmt.setString(3, cvo.getDays());
			pstmt.setString(4, cvo.getHours());
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

	/**************************************************************************
	 * MemSearchView 클래스의 회원조회 메소드
	 * 
	 * @param search : 콤보박스에서 선택된 검색조건. 아이디 || 이름 || 전화번호 || 주소
	 * @param input  : 콤보박스 선택후 사용자가 입력한 값
	 * @return List<MemberVO>
	 ***************************************************************************/
	public List<MemberVO> memSearch(String search, String input) {
		List<MemberVO> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ID, PW, NAME, PHONE, ADDRESS FROM MEMBER WHERE");

		// PreparedStatement는 필드 바인딩이 되지 않는다. 즉 컬럼명은 '?' 처리할 수 없다.
		// 그러므로 컬러명 동적처리를 위해 밑과 같이 처리
		if (search.equals("아이디")) {
			sql.append(" ID LIKE '%' || ? || '%'");
		} else if (search.equals("이름")) {
			sql.append(" NAME LIKE '%' || ? || '%'");
		} else if (search.equals("전화번호")) {
			sql.append(" PHONE LIKE '010-'|| ? || '%'");
		} else if (search.equals("주소")) {
			sql.append(" ADDRESS LIKE '%' || ? || '%'");
		}

		con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, input);
			rs = pstmt.executeQuery();
			MemberVO mvo = null;
			while (rs.next()) { // 아이디가 일치하고 비밀번호가 일치할 경우 1, 비밀번호 틀렸을 경우 0 반환, 아이디 없을경우 -1
				String user_id = rs.getString("ID");
				String user_name = rs.getString("NAME");
				String user_phone = rs.getString("PHONE");
				String user_address= rs.getString("ADDRESS");
				mvo = new MemberVO();
				mvo.setMem_id(user_id);
				mvo.setMem_name(user_name);
				mvo.setMem_phone(user_phone);
				mvo.setMem_address(user_address);
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

	/**************************************************************************
	 * 개인 대화방 생성 메소드
	 * @param name1 : 대화 요청인
	 * @param name2 : 대화 수락인
	***************************************************************************/
	public void createRoomNum(String name1, String name2) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PRIVATE_ROOM (ROOMNUM, NAME1, NAME2 ) ");
		sql.append("VALUES(ROOMNUM_SEQ.NEXTVAL, ?, ?  )          ");

		con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, name1);
			pstmt.setString(2, name2);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pstmt);
		}

	}
	/**************************************************************************
	 * 개인 대화방 번호 찾기(방생성 되어있는지 여부 체크)
	 * @param name1 : 대화 요청인
	 * @param name2 : 대화 수락인
	 * @return : 방이 있으면 = 0 아닌 이전에 생성된 방번호 return
	 * 			 방이 없으면 = 0 return
	 ***************************************************************************/
	
	public int searchRoomNum(String name1, String name2) {
		int roomNum = 0;
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ROOMNUM                                ");
		sql.append("FROM (                                        ");
		sql.append("            SELECT ROOMNUM                    ");
		sql.append("                            FROM PRIVATE_ROOM ");
		sql.append("                    WHERE NAME1 = ? 	      ");
		sql.append("                        AND NAME2 = ? 	      ");
		sql.append("                    UNION ALL                 ");
		sql.append("            SELECT ROOMNUM                    ");
		sql.append("                            FROM PRIVATE_ROOM ");
		sql.append("                    WHERE NAME1 = ?     	  ");
		sql.append("                        AND NAME2 = ?      	  ");
		sql.append("               )                              ");
		con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, name1);
			pstmt.setString(2, name2);
			pstmt.setString(3, name2);
			pstmt.setString(4, name1);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				roomNum = rs.getInt("roomnum");
			}
		} catch (SQLException e) {
			e.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pstmt);
		}
		return roomNum;
	}
	/**************************************************************************
	 * 개인 대화내용 테이블에 저장 메소드
	 * @param msg       : 대화내용 
	 * @param roomNum   : 룸번호
	 * @param days		: 날짜
	 * @param hours     : 시간
	 ***************************************************************************/
	public void prchatMsg(ChatMsgVO cvo) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into PRIVATE_MSG_LOG values(?,?,?,?)");

		con = DButil.getConnection(); // DButil에서 예외처리 했으므로 따로 해주지 않아도 됨
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, cvo.getChatmsg());
			pstmt.setInt(2, cvo.getRoomnum());
			pstmt.setString(3, cvo.getDays());
			pstmt.setString(4, cvo.getHours());
			int i = pstmt.executeUpdate(); 
			System.out.println(" 데이터 " + i + "건이 추가되었습니다."); ////////// Test
		} catch (SQLException se) {
			se.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pstmt);
		}

	}

}
