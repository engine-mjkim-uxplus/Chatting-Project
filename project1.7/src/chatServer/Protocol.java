package chatServer;

public class Protocol {
	//프로토콜의 경우 어플에서 일괄적으로 적용하고 변경될 수 있도록 설계하는 것이 좋을 것이다.
	public static final int ADMISSION		= 100; 
	public static final int ROOM_CREATE 	= 110;  
	public static final int MESSAGE 		= 200;
	public static final int GROUP_MESSAGE   = 201;
	public static final int NICKNAME_CHANGE  = 202;
	public static final int NOTICE		    = 203;
	public static final int WHISHER 		= 209;  
	public static final int ROOM_OUT 		= 500;
	public static final int EXPULSION   	= 501;

}
