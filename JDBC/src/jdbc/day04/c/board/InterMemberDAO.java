package jdbc.day04.c.board;

import java.util.List;
import java.util.Map;

import javax.naming.ldap.SortControl;

public interface InterMemberDAO {
	
	// ********************* 회원가입(insert) 메소드 **********************************************************************
	int memberRegister(MemberDTO member);
	
	// ********************* 로그인(select) 메소드 ***********************************************************************
	MemberDTO login(Map<String,String> map);
	
	// ********************* 탈퇴(update) 메소드 *************************************************************************
	int memberDelete(int userseq);
	
	// ********************* 모든회원조회(select) 메소드 ********************************************************************
	List<MemberDTO> showAllMemeber(String sortchoice);
	
	// ********************* 나의정보조회(select) 메소드  *******************************************************************
	MemberDTO select_myinfo(int userseq);
}
