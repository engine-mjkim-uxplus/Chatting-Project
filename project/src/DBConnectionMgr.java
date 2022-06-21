
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionMgr {
	public static Connection getConnection() throws SQLException {
		String url = "jdbc:oracle:thin:@localhost:1522:orcl11";
		Connection con = null;
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, "scott", "tiger");
		}catch(Exception e){
			System.out.println("Exception=["+ e +"]");
		}  
		return con;
	}
}
