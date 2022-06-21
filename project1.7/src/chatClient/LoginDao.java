package chatClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chatServer.ChatMsgVO;
import chatServer.DButil;
// 클라이언트에서 쓸 dao클래스

public class LoginDao {

	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	List<ChatMsgVO> list;

	// 회원가입 메소드(signUp) -- 회원가입 버튼과 매핑
	public int signUp(MemberVO pmVO) {
		int result = 0;
		String sql = "INSERT INTO MEMBER(ID,PW,NAME) VALUES(?,?,?)";
		
		con = DButil.getConnection(); // DButil에서 예외처리 했으므로 따로 해주지 않아도 됨
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, pmVO.getMem_id());
			pstmt.setString(2, pmVO.getMem_pw());
			pstmt.setString(3, pmVO.getMem_name());
			result = pstmt.executeUpdate();
			System.out.println("데이터 " + result + "건이 추가되었습니다");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		DButil.close(con, pstmt);
		}
		return result;
	}

	// 아이디 중복검사 메소드(idCheck) -- 회원가입 아이디 중복검사 번튼과 매핑처리
	public String idCheck(MemberVO pmVO) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT *                                    ");
		sql.append("  FROM (                                    ");
		sql.append("        SELECT                              ");
		sql.append("               CASE WHEN ID = ? THEN '1'	");
		sql.append("               ELSE '-1'                    ");
		sql.append("                END IDCHECK                 ");
		sql.append("          FROM MEMBER                       ");
		sql.append("        ORDER BY IDCHECK DESC               ");
		sql.append("       )                                    ");
		sql.append(" WHERE ROWNUM = 1                           ");
		String result = "";

		con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, pmVO.getMem_id());
			rs = pstmt.executeQuery();
			if (rs.next()) { // rs.next()가 true라는 것은 id가 있다는 것
				result = rs.getString("IDCHECK"); // 아이디 중복이 있으면 -1반환
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		DButil.close(con, pstmt, rs); // 닫기
		}
		return result;
	}

	// 로그인 확인 메소드 -- 로그인 버튼에서 호출
	// 로그인 성공시 1반환, 비밀번호 불일치 0반환, 아이디 없을 경우 -1반환
	public String login(MemberVO pmVO) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT *											");
		sql.append("  FROM (			 								");
		sql.append("		SELECT 									    ");
		sql.append("		       CASE WHEN ID = ? THEN				");
		sql.append("		            CASE WHEN PW = ? THEN NAME  ");
		sql.append("		            ELSE '0'					    ");
		sql.append("		            END								");
		sql.append("		       ELSE '-1'							");
		sql.append("		        END LOGIN							");
		sql.append("		  FROM MEMBER 								");
		sql.append("		ORDER BY LOGIN DESC							");
		sql.append("	   )											");
		sql.append(" WHERE ROWNUM = 1									");
		
		String result = "";
		con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, pmVO.getMem_id());
			pstmt.setString(2, pmVO.getMem_pw());
			rs = pstmt.executeQuery();

			if (rs.next()) { // 아이디가 일치하고 비밀번호가 일치할 경우 1, 비밀번호 틀렸을 경우 0 반환, 아이디 없을경우 -1
					result = rs.getString("LOGIN");
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		DButil.close(con, pstmt, rs);
		}
		return result;
	}

}
