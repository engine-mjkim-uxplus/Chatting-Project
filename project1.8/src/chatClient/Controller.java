package chatClient;


// 로그인 회원가입에 대한 뷰와 dao사이에서의 Controller역할 담당
public class Controller {
	private final String _LOGIN    = "login";
	private final String _IDCHECK  = "idcheck";
	private final String _SIGNUP   = "signup";
	LoginDao logindao = new LoginDao();
	LoginView loginview = null;
	SignUpView signupview = null;
	private static Controller instance;
	
	private Controller() {}
	public static Controller getInstance() {
		if ( instance == null ) {
			instance = new Controller();
		}
		return instance;
	}

	public MemberVO action(MemberVO pmVO) {
		String command = pmVO.getCommand(); // 반복되는 코드 줄여줌
		String nickName = null;
		MemberVO rsVO = new MemberVO(); // 결과값 가지고 감
		// 로그인
		if(_LOGIN.equals(command)) {
			nickName = logindao.login(pmVO);
			rsVO.setMem_name(nickName);
		// 회원가입
		} else if(_SIGNUP.equals(command)) {
			int result = 0;
			result = logindao.signUp(pmVO);
			rsVO.setResult(result);
			System.out.println("result값은 " + result);
			}	
		// 아이디 중복체크
		 else if (_IDCHECK.equals(command)) {
			String result = logindao.idCheck(pmVO);
			rsVO.setResult(Integer.valueOf(result));
		}
		return rsVO;
	}
}
