package chatClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import chatServer.DButil;
// 클라이언트에서 쓸 dao클래스
import chatServer.MsgVO;

public class LoginDao {

	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	List<MsgVO> list;
	/**********************************************************************
	 * 회원가입 메소드(signUp) -- 회원가입 버튼과 매핑
	 * @return : 회원가입 성공 시 = 1 반환
	 * 			 회원가입 실패 시 = 0 반환
	 **********************************************************************/
	public int signUp(MemberVO pmVO) {
		int result = 0;
		String sql = "INSERT INTO MEMBER(ID,PW,NAME) VALUES(?,?,?)";
		
		con = DButil.getConnection();
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
	/**********************************************************************
	 * 아이디 중복검사 메소드(idCheck)
	 * @param pmVO
	 * @return id가 있다면 1 반환
	 * 	       id가 없다면 -1 반환
	 **********************************************************************/
	
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
	/**********************************************************************
	 *  로그인 확인 메소드 -- 로그인 버튼에서 호출
	 * @param pmVO
	 * @return 로그인 성공시 1반환 
	 * 		   비밀번호 불일치 0반환
	 * 		   아이디 없을 경우 -1반환
	 **********************************************************************/
	//
	// 
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
			// 아이디가 일치하고 비밀번호가 일치할 경우 1, 비밀번호 틀렸을 경우 0 반환, 아이디 없을경우 -1
			if (rs.next()) { 
					result = rs.getString("LOGIN");
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		DButil.close(con, pstmt, rs);
		}
		return result;
	}
	/**********************************************************************
	 * 개인 대화방 내용 불러오기 메소드
	 * @param roomnum
	 * @return MsgVO : 대화내용
	 **********************************************************************/
	
	public List<MsgVO> prchatBring(int roomnum) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT CHATMSG		        ");
		sql.append("		FROM PRIVATE_MSG_LOG");
		sql.append("	WHERE ROOMNUM = ?       ");
		sql.append(" ORDER BY DAYS, HOURS	    ");
        
        con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setInt(1, roomnum);
			rs = pstmt.executeQuery();
			list = new ArrayList<>();
			while (rs.next()) {
				String chatmsg = rs.getString("chatmsg"); // 데이터베이스의 컬럼이 대문자인지 소문자인지 구분하지 않는다.
				MsgVO mvo = new MsgVO(chatmsg);
				list.add(mvo);
			}
			System.out.println("가져온 메시지 건수는 " + list.size() + "건 입니다");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pstmt, rs);
		}
		return list;
	}
	
	/******************************************************************
	 * 회원정보 수정 구현
	 * 
	 * @param String name, pw - 사용자가 입력한 name, pw
	 * @return int result - 0: 회원정보 수정 성공
	 * 
	 *UPDATE MEMBER SET NAME = '유리123', PW = '1234' WHERE ID = 'yuri'
	 ******************************************************************/
	// 회원정보 수정 메소드
	public MemberVO editMember(MemberVO mVO) {
		System.out.println("회원정보 수정 메소드 호출 성공");
		String nickname = mVO.getMem_name();
		String pw = mVO.getMem_pw();
		String id = mVO.getMem_id();
		int result = mVO.getResult();
		String sql = null;
		
		if (result == 1) {
			sql = "UPDATE MEMBER SET PW=? WHERE ID=?";
		}
		else if (result == 2) {
			sql = "UPDATE MEMBER SET NAME=? WHERE ID=?";
		}
		else if (result == 3) {
			sql = "UPDATE MEMBER SET NAME=?,PW=? WHERE ID=?";
		}

		con = DButil.getConnection();
		
		try {
			pstmt = con.prepareStatement(sql);
			
			if (result == 1) {
				pstmt.setString(1, pw);
				pstmt.setString(2, id);
			}
			else if (result == 2) {
				pstmt.setString(1, nickname);
				pstmt.setString(2, id);
			}
			else if (result == 3) {
				pstmt.setString(1, nickname);
				pstmt.setString(2, pw);
				pstmt.setString(3, id);
			}
			
			int success = pstmt.executeUpdate();
			System.out.println(success+"건 회원정보 수정 성공");

		} catch (SQLException se) {
			se.printStackTrace();
			System.out.println("회원정보 수정 실패");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("회원정보 수정 실패");
		} finally {
			DButil.close(con, pstmt);
		}
		return mVO;
	}
	
	
	/**********************************************************
	 * 회원탈퇴하기
	 * 
	 * @param 
	 * @return
	 * 
	 * DELETE FROM MEMBER WHERE id = ?
	 **********************************************************/
	// 회원탈퇴 메소드
	public MemberVO withdrawal(MemberVO pmVO) {
		System.out.println("회원탈퇴 메소드 호출 성공");
		String sql ="DELETE FROM MEMBER	WHERE ID = ?";
		String id = pmVO.getMem_id();
		con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			pmVO.setResult(1);
			System.out.println("탈퇴 성공");
		} catch (SQLException se) {
			pmVO.setResult(-1);
			se.printStackTrace();
			System.out.println("회원 탈퇴 실패");
		} catch (Exception e) {
			pmVO.setResult(-1);
			e.printStackTrace();
			System.out.println("회원 탈퇴 실패");
		} finally {
			DButil.close(con,pstmt);
		}
		return pmVO;
	}
	
	/**********************************************************
	 * 닉네임 값으로 아이디,비밀번호를 가져오는 메소드
	 * @param mVO
	 * @return 
	 **********************************************************/
	public MemberVO memberCheck(MemberVO mVO) {
		String nickName = mVO.getMem_name();
		String sql = "SELECT ID,PW FROM MEMBER WHERE NAME = ?";
		
		con = DButil.getConnection();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, nickName);
			rs = pstmt.executeQuery();
			if (rs.next()) { 
				String id = rs.getString("ID");
				String pw = rs.getString("PW");
				mVO.setMem_id(id);
				mVO.setMem_pw(pw);
				mVO.setResult(1); // 성공시 1
			}else {
				mVO.setResult(-1); // 실패시 -1
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con, pstmt, rs); // 닫기
		}
		
		return mVO;
	}

//	public static void main(String[] args) {
//		LoginDao ld = new LoginDao();
//		MemberVO vo = new MemberVO();
//		vo.setMem_id("test");
//		vo.setResult(3);
//		vo.setMem_pw("1234");
//		vo.setMem_name("종국");
//		ld.editMember(vo);
//	}
	
}
