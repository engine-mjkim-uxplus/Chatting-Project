package chatServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * DButil클래스는 connection을 매번하는게 번거롭기 때문에
 * 미리 이 코드들을 유틸클래스로 만들어놓고 가져다 쓰기 위해서 즉 편의성을 위해
 * 미리 만들어 놓는다.
 * 리턴으로 Connection 객체를 반환 받아와야한다.
 */
// DB연동 전담하는 클래스 
public class DButil {
	private static Connection con = null;
	private static final String url = "jdbc:oracle:thin:@localhost:1522/ORCL11";
	private static final String user = "MJ";
	private static final String pwd = "111111";
	private static final String driver = "oracle.jdbc.driver.OracleDriver";

	public static Connection getConnection() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			System.out.println("연결 성공");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("연결 실패");
		}
		return con;
	}

	// Connection 종료 메서드
	public static void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// PreparedStatement, Connection 종료 메서드
	public static void close(Connection con, PreparedStatement pstmt) {
		if (con != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
			close(con);
			}
		}
	}

	// Connection, PreparedStatement , ResultSet 닫기
	public static void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
		if (con != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();

			}finally {
			close(con, pstmt);
			}
		}

	}
}
