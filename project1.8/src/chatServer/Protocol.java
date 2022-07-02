package chatServer;

public class Protocol {
	//프로토콜의 경우 어플에서 일괄적으로 적용하고 변경될 수 있도록 설계하는 것이 좋을 것이다.
	public static final int ADMISSION		   = 100; // 입장
	
	public static final int ROOM_CREATE 	   = 110; // 룸 생성
	public static final int ROOM_ACCEPT 	   = 111; // 초대 수락
	public static final int LOGIN			   = 150;
	public static final int IDCHECK			   = 151;
	public static final int SIGNUP			   = 152;
	
	public static final int MESSAGE 		   = 200;  // 개인 메시지
	public static final int GROUP_MESSAGE      = 201;  // 단체 메시지
	public static final int NICKNAME_CHANGE    = 202;  // 닉네임 변경
	public static final int NOTICE		       = 203;  // 공지사항
	public static final int WHISHER 		   = 209;  // 귓속말
	
	public static final int ROOM_OUT 		   = 500;  // 단톡방 나가기
	public static final int PRROOM_OUT		   = 501;  // 개인채팅방 나가기
	public static final int EXPULSION   	   = 502;  // 강퇴
	public static final int EXPULSION_RESPONSE = 503;  // 강퇴 응답

}
