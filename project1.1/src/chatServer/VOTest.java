package chatServer;

import java.util.List;

public class VOTest {

	public static void main(String[] args) {
		ChatDao dao = new ChatDao();
		dao.signUp("pppps","1234","민준");
//		List<ChatMsgVO> list = dao.list();
		
//		for(ChatMsgVO d : list) {
//			System.out.printf("%s  %s  %s  %s \n",
//					d.getChatmsg(),
//					d.getNicname(),
//					d.getDays(),
//					d.getHours()
//					);
//			
//			
//		}
		
	}

}
