package jdbc.day03;

import java.util.*;

public class MemberCtrl {

	
	// field, attribute, property, 속성
	InterMemberDAO mdao = new MemberDAO();

	// ************************* 시작 메뉴를 보여주는 method **********************************************************************
	public void menu_start(Scanner sc) {
		MemberDTO member = null; 
		String s_choice = "";

		do {
			String loginName = "";
			String login_logout = "로그인";
			String menu_myInfo = "";
			
			if(member != null) {
				loginName = "["+member.getName()+" 로그인중...]";
				login_logout = "로그아웃";
				menu_myInfo = "4.나의정보보기 \t 5.회원탈퇴하기 \t 6.모든회원조회하기";	
			}
			
			System.out.println("\n >>> ===== 시작 메뉴 "+loginName +"==== <<< \n"
							 + "1.회원가입	 \t 2."+login_logout+" \t 3. 프로그램종료\n"
							 + menu_myInfo
							 + "\n------------------------------------------ \n");
			
			System.out.print("@ 메뉴번호 선택: ");
			s_choice = sc.nextLine();
			
			switch (s_choice) {
			case "1":	// 회원가입
				memberRegister(sc);
				
				break;
				
			case "2":	// 로그인 or 로그아웃
				
				if ("로그인".equals(login_logout)) {	
					member = login(sc); 				//  로그인했을 때...
					}
				else {
					member = null;						// 로그아웃
					System.out.println(">>> 로그아웃되었습니다. <<< \n");
				}
				break;
				
			case "3":	// 프로그램 종료
				
				break;		
			
			case "4":	// 나의정보보기
				if (member != null){		
				// System.out.println(member.toString());
				// 또는
					System.out.println(member);
					break;
				}

			case "5":	// 회원탈퇴하기
				if (member != null){
					int n = mdao.memberDelete(member.getUserseq());
						if (n == 1) {
							System.out.println(">> 회원탈퇴가 성공했습니다. <<");
							member = null;
						}
					break;
				}
				
			case "6":
				
				if(member != null) {
					showAllMember();
					break;
				}
			default:
				
				System.out.println("메뉴에 없는 번호입니다. 다시 선택하세요\n");
				break;
			} // end of switch -----------------------------------------------------------------------------------------------
			
		} while (!("3".equals(s_choice)));
	} // end of public void menu_start(Scanner sc) ***************************************************************************
	
	
	// ************************* 회원 가입을 하게 해 주는 method *******************************************************************
	private void memberRegister(Scanner sc) {
		
		System.out.println("\n >>>> ------ 회원 가입 ------ <<<< \n");
		
		System.out.print("1. 아이디:");
		String userid = sc.nextLine();
		
		System.out.print("2. 비밀번호:");
		String passwd = sc.nextLine();
		
		System.out.print("3. 회원명:");
		String name = sc.nextLine();
		
		System.out.print("4. 연락처:");
		String mobile = sc.nextLine();

		MemberDTO member= new MemberDTO();
		member.setUserid(userid);
		member.setPasswd(passwd);
		member.setName(name);
		member.setMobile(mobile);
		
		int n = mdao.memberRegister(member);
		
		if(n==1) {
			System.out.println("\n >>> 회원가입을 축하드립니다. <<< \n");
		}
		else {
			System.out.println("\n >>> 회원가입을 실패하였습니다. <<< \n");
		}
	} // end of private void memberRegister(Scanner sc) **********************************************************************
	
	
	// ************************* 로그인 하는 method *****************************************************************************
	private MemberDTO login(Scanner sc) {
		
		MemberDTO member = null;
		
		System.out.println(" \n >>> --- 로그인 --- <<< ");
		
		System.out.println(" @ 아이디: ");
		String userid = sc.nextLine();
		
		System.out.println(" @ 비밀번호: ");
		String passwd = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("passwd", passwd);
		
		member = mdao.login(paraMap);
		
		if(member != null) {
			System.out.println("\n >>> 로그인 성공 <<< \n");
		}
		else {
			System.out.println("\n >>> 로그인 실패 <<< \n");
		}
		
		return member;
	} // end of private MemberDTO login(Scanner sc) **************************************************************************

	
	//************************* 모든회원을 조회하는 method ************************************************************************
	private void showAllMember() {
		System.out.println("------------------------------------------------------------------------");
		System.out.println("회원번호 \t 아이디 \t 회원명 \t 연락처 \t 포인트 \t 가입일자 \t 가입상태");
		
		
		System.out.println("------------------------------------------------------------------------");
		
		StringBuilder sb = new StringBuilder();
		List<MemberDTO> memberList= mdao.showAllMemeber();
		for(MemberDTO member :memberList) {
			
			String status = (member.getStatus() ==1 )? "가입중" : "탈퇴";
			
			sb.append(member.getUserseq() + "  " + member.getUserid() + "  " + member.getName() + "  " + member.getMobile() + "  " + member.getPoint()
					  + "  " + member.getRegisterday() + "  " + status + "\n");
			
		} // end of for ----------------------------
		System.out.println(sb.toString());
		
		
		
	}// end of private void showAllMember() **********************************************************************************
}
