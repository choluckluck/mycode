package jdbc.day03;

import java.util.List;
import java.util.Map;

public interface InterMemberDAO {
	
	// ********************* 회원가입(insert) 메소드 **********************************************************************
	int memberRegister(MemberDTO member);
	
	// ********************* 로그인(select) 메소드 ***********************************************************************
	MemberDTO login(Map<String,String> map);
	
	// ********************* 탈퇴(update) 메소드 *************************************************************************
	int memberDelete(int userseq);
	
	// ********************* 모든회원조회(select) 메소드 ********************************************************************
	List<MemberDTO> showAllMemeber();
}
