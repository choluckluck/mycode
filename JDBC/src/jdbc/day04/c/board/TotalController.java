package jdbc.day04.c.board;

import java.text.SimpleDateFormat;
import java.util.*;

import jdbc.util.MyUtil;



// import jdbc.day03.MemberDTO; : 없애야함. 복사할 때마다 매번 체크하시귈! 

public class TotalController {
	
	// field
	InterMemberDAO mdao = new MemberDAO();
	InterBoardDAO bdao = new BoardDAO();
	
	// ******** 시작메뉴 ****************************************************************************************
	public void menu_Start(Scanner sc) {
		
		MemberDTO member = null; 
		String s_choice = "";

		do {
			String loginName = "";
			String login_logout = "로그인";
			String menu_myInfo = "";
			String menu_Admin = "";
			
			if(member != null) {
				loginName = "["+member.getName()+" 로그인중...]";
				login_logout = "로그아웃";
				menu_myInfo = "4.나의정보보기 \t 5.게시판보러가기 ";	
				
				if("admin".equals(member.getUserid())) {
					menu_myInfo += "6.관리자전용(모든회원조회) \n";
				}
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
				MyDBConnection.closeConnection();		// Connection 객체 자원 반납
				break;		
			
			case "4":	// 나의정보보기
				if (member != null){		
				// System.out.println(member.toString());
				// 또는
					member = mdao.select_myinfo(member.getUserseq()); // 이게 없으면... sysout 에서 로그인 당시의 member 정보만 조회됨.
					System.out.println(member);
					break;
				}

			case "5":	// 게시판가기
				if(member != null) {
					menu_Board(member, sc);		// 게시판 메뉴에 들어간다.
					break;
				}
				
			case "6":
				if(member != null && "admin".equals(member.getUserid())) {
					System.out.println(">> 정렬 [1. 회원명의 오름차순 / 2. 회원명의 내림차순 \n"
							+ "	3. 가입일자의 오름차순 / 4. 가입일자의 내림차순]");
					
					System.out.print(">> 정렬번호 선택: ");
					String sortchoice = sc.nextLine();
					showAllMember(sortchoice);
					
					break;
				}
				 
			default:
				
				System.out.println("메뉴에 없는 번호입니다. 다시 선택하세요\n");
				break;
			} // end of switch -----------------------------------------------------------------------------------------------
			
		} while (!("3".equals(s_choice)));
		
	} // end of public void menu_Statr(Scanner sc) ************************************************************

	
	
	
	

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
	private void showAllMember(String sortchoice) {
		
		
		if("1".equals(sortchoice) || "2".equals(sortchoice) || "3".equals(sortchoice) || "4".equals(sortchoice)) {
			
			System.out.println("------------------------------------------------------------------------");
			System.out.println("회원번호 \t 아이디 \t 회원명 \t 연락처 \t 포인트 \t 가입일자 \t 가입상태");
			System.out.println("------------------------------------------------------------------------");
			
			StringBuilder sb = new StringBuilder();
			List<MemberDTO> memberList= mdao.showAllMemeber(sortchoice);
			
			
			
			for(MemberDTO member :memberList) {
				
				String status = (member.getStatus() ==1 )? "가입중" : "탈퇴";
				
				sb.append(member.getUserseq() + "  " + member.getUserid() + "  " + member.getName() + "  " + member.getMobile() + "  " + member.getPoint()
						  + "  " + member.getRegisterday() + "  " + status + "\n");
			}// end of for ----------------------------
			
			System.out.println(sb.toString());
		}
		else {
			System.out.println(">> 정렬에 없는 번호입니다. <<");
		}

	}// end of private void showAllMember() **********************************************************************************

	
	
	
	
	
	// ******** 게시판 메뉴보기 메소드 *********************************************************************************************
	private void menu_Board(MemberDTO member, Scanner sc) {
		String s_menuNo = "";
		do {
			System.out.println("\n ------------- 게시판메뉴 [ " + member.getName() +"님로그인 중...] --------------\n"
 						 + "1.글목록보기 \t 2.글내용보기 \t 3.글쓰기 \t 4.댓글쓰기\n"
 						 + "5.글수정하기 \t 6.글삭제하기 \t 7.최근1주일간 일자별 게시글 작성건수 \n"
 						 + "8.이번달 일자별 게시글 작성건수 9.나가기(종료) \n"
 						 + "-----------------------------------------------------------------------------");

			System.out.print(">> 메뉴 번호를 선택해 주세요.");
			s_menuNo = sc.nextLine();
			
			switch (s_menuNo) {
			case "1": 		// 글목록보기
				boardList();
				break;
			case "2": 		// 글내용보기
				viewContents(member.getUserid(),sc);
				break;
			case "3": 		// 글쓰기(tbl_board에 insert + tbl_member 의 point 컬럼에 update)
							// tbl_board에 insert가 성공되면 tbl_member에서 글 작성자의 point 컬럼을 10씩 증가시킨다.

			/*
			 * -- *** Transaction(트랜잭션) 처리 *** --
			   --> Transaction(트랜잭션)이라 함은 관련된 일련의 DML로 이루어진 한꾸러미(한세트)를 말한다.
			   --> Transaction(트랜잭션)이라 함은 데이터베이스의 상태를 변환시키기 위하여 논리적 기능을 수행하는 하나의 작업단위를 말한다.        
			                  
			   Transaction(트랜잭션) 처리에서 가장 중요한 것은 모든 DML문이 성공해야만 최종적으로 모두 commit 을 해주고,
			   DML문중에 1개라도 실패하면 모두 rollback 을 해주어야 한다는 것이다.
			 */
				
				int n = write(member, sc);
				
				if(n==1) {
					System.out.println(" >> 글쓰기 성공 !! << \n");
				}
				
				else if(n==0) {
					System.out.println(" >> 글쓰기를 취소하셨습니다. << \n");
				}
				
				else if(n==-1)
					System.out.println(" >> 글쓰기 실패 !! << \\n");
				break;
			case "4": 		// 댓글쓰기 (tbl_comment 에 insert할 값이므로 int 를 받는다.)
				n = writeComment(member,sc);	// 입력받는 id 와 입력받은 id를 불러올 테니까...
				
				if(n==1) {
					System.out.println(">> 댓글 쓰기 성공!! <<");
				}
				else if(n==-1) {
					System.out.println(">> 입력하신 원글의 글 번호는 존재하지 않습니다. <<");
					break;
				}
				
				break;
			case "5": 		// 글수정하기
				n = updateBoard(member,sc);
				
				if(n==1) {
					System.out.println(">> 글 수정 성공!! <<");
				}
				else if(n==-1) {
					System.out.println(">> SQL 구문 오류 발생으로 글수정이 실패되었습니다. <<");
					break;
				}
				
				break;
			case "6": 		// 글삭제하기
				
				n = deleteBoard(member,sc);
				
				if(n==1) {
					System.out.println(">> 글 삭제 성공!! <<");
				}
				else if(n==-1) {
					System.out.println(">> SQL 구문 오류 발생으로 글수정이 실패되었습니다. <<");
					break;
				}
				
				break;
			case "7": 		// 최근1주일간 일자별 게시글 작성건수

				statisticsByWeek();
				break;
			case "8": 		// 이번달 일자별 게시글 작성건수	
				statisticsByCurrentMonth();
				break;
			case "9": 		// 나가기(종료)
				
				break;
			default:
				System.out.println(">> 메뉴에 없는 번호입니다.<< \n");
				break;
			} // end of switch
		} while (!("9".equals(s_menuNo)));
		
		
	} // end of private void menu_Board(MemberDTO member, Scanner sc) ********************************************************







	// ******* 글쓰기(tbl_board에 insert + tbl_member 의 point 컬럼에 10 update) ***************************************************
	// == Transaction 처리 ==
	private int write(MemberDTO member, Scanner sc) {
		
		int result = 0;
		
		System.out.println(" \n >>> 글쓰기 <<< ");
		
		System.out.println(" \n 1. 작성자명: " + member.getName());
		
		System.out.print(" \n 2. 글제목: " );
		String subject = sc.nextLine();
		
		System.out.print(" \n 3. 글내용: " );
		String contents = sc.nextLine();
		
		System.out.print(" \n 4. 글암호: " );
		String boardpasswd = sc.nextLine();

		int flag = 0;
		do {
			System.out.println(" >> 정말로 글쓰기를 하시겠습니까? [Y/N] => ");
			String yn = sc.nextLine();
			
			if("y".equalsIgnoreCase(yn)) {
				flag = 1;
				break;
			}
			else if ("n".equalsIgnoreCase(yn)) {
				break;
			}
			else {
				System.out.println("[오류]Y 또는 N 값만 입력하세요!!!");
			}
		} while(true);
		
		
		if (flag == 1 ) {
			BoardDTO bdto = new BoardDTO();
			bdto.setFk_userid(member.getUserid());
			bdto.setSubject(subject);
			bdto.setContents(contents);
			bdto.setBoardpasswd(boardpasswd);
			
			result = bdao.write(bdto);
			// 1 또는 -1
		}
		
		else { // 글쓰기를 취소한 겨우,
			result = 0;
		}
		
		return result;
		
	} // end of private int write(MemberDTO member, Scanner sc) ************************************************************

	
	
	
	
	
	// ***** 글목록보기를 해주는 메소드 *************************************************************************************************
	private void boardList() {
		
		List<BoardDTO> boardList = bdao.boardList();
		
		
		if(boardList.size()>0) {
			// 게시글이 존재하는 경우, 
			System.out.println("\n ----------------------------- [게시글 목록] -------------------------------");
			System.out.println("글번호 \t 글제목 \t\t 작성자명 \t 작성일자 \t\t 조회수");
			System.out.println("---------------------------------------------------------------------------");
			
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<boardList.size(); i++) {
				
				sb.append(boardList.get(i).showBoardList() + "\n");
				// boardList.get(i) 는 BoardDTO 다.
			} // end of for -----------------------------------------------------
			
			System.out.println(sb.toString());
		}
		else {
			// 게시글이 1개도 존재하지 않는 경우,
			System.out.println(">> 글목록이 없습니다. << \n");
		}
	} // end of private void boardList() ******************************************************************************************

	



	// ****** 글 내용 보는 메소드 **********************************************************************************************
	private void viewContents(String userid, Scanner sc) {
	
		System.out.println(" \n >>> 글내용 보기 <<<");
		
		System.out.println("> 글번호: ");
		String boardno = sc.nextLine();
		
		Map<String , String> paraMap = new HashMap<>();
		paraMap.put("boardno", boardno);
		// paraMap.put("userid", userid);
		
		
		BoardDTO bdto = bdao.viewContents(paraMap);
		
		if(bdto != null) {
			System.out.println("[글내용]" + bdto.getContents());
			
			// 현재 로그인되어진 사용자가 자신의 글이 아닌 다른 사용자가 쓴 글을 조회했을 때만 조회수를 1 증가 시킨다.
			if(!bdto.getFk_userid().equals(userid)) {
				bdao.updateViewCount(boardno);
				// spring 에서는 모든 값을 하나만 받기 때문에, 보통은 Map 을 이용해 여러 파라미터를 가져올 수 있도록 한다.
				// 하지만 이 경우에는 분류 값이 boardno 하나라서 paramap 안써도 됨
			}
			
			////////////////////////////////////////////////////////////////////////////////////////
			System.out.println("[댓글]\n--------------------------------");
		
			List<CommentDTO> commentList = bdao.commentList(boardno);
			// 원글에 대한 댓글을 가져오는 것. (특정 게시글 글번호에 대한 tbl_Comment 테이블과 tbl_member 테이블에 JOIN 해서 보여준다.)
			
			if(commentList != null) {
				// 댓글이 존재하는 원길은 경우
				
				System.out.println("댓글내용 \t\t 작성자 \t 작성일자");
				System.out.println("-------------------------------------------------------");
				
				StringBuilder sb = new StringBuilder();
				
				for(CommentDTO cmtdto : commentList) {
					sb.append(cmtdto.viewCommentInfo() + "\n");
				}
				
				System.out.println(sb.toString());
				
			}
			else {
				// 댓글이 존재하지 않는 원글인 경우
				System.out.println(">> 댓글 없음 <<\n");
			}
		}
		else {
			// 존재하지 않는... 글번호를 입력한 경우
			System.out.println(">> 글번호 " + boardno +"은 글 목록에 없습니다.<<\n");
		}
		
		
	} // end of private void viewContents(String userid, Scanner sc) ******************************************************


	
	

	// ********* 댓글쓰기 기능 메소드 *******************************************************************************************
	private int writeComment(MemberDTO member, Scanner sc) {
		
		int result = 0;
		int n_boardno = 0;
		String contents = "";
		
		System.out.println("\n >> 댓글쓰기 << ");
		System.out.println("1. 작성자명: " + member.getName());
		do {
			System.out.println("2. 원글의 글번호: ");
			String boardno = sc.nextLine(); // but,,, 존재하지 않는 글번호를 입력할 수도 있다.
			try {
				n_boardno = Integer.parseInt(boardno);
				break;
			} catch(NumberFormatException e) {
				System.out.println(">> [경고] 원글의 글번호는 정수로만 입력하셔야 합니다!! <<");
			}
		} while(true);
		
		do {
			System.out.println("3. 댓글 내용: ");
			contents = sc.nextLine(); // but,,, 댓글 입력 시 그냥 엔터 또는 공백을 입력할 수 있다...또는 엄청 많은 글자. 이럴 때는 등록되면 안됨
			
			if(contents.trim().isEmpty()) { // 엔터 또는 공백만 입력한 경우
				System.out.print("[경고] 댓글 내용은 필수로 입력하여야 합니다. \n");
			}
			else if (contents.length() > 100) {
				System.out.print("[경고] 댓글 내용은 최대 100글자 이내만 가능합니다. \n");
			}
			else {
				break;
			}		
		} while (true);
		
		
		String yn = "";
		do {
			System.out.println(" == 정말로 댓글쓰기를 하시겠습니까? [Y/N] : ");
			yn = sc.nextLine();
			
			if ("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)) {
				break;
			}
			else {
				System.out.println(">> [경고] 대소문자 구분없이 Y 또는 N 만 입력 가능합니다. <<\n");
			}
		} while(true);
		
		if("y".equalsIgnoreCase(yn)) {
			
		CommentDTO cmdto = new CommentDTO();
		cmdto.setFk_boardno(n_boardno);  					// 원글의 글번호
		cmdto.setFk_userid(member.getUserid());				// 댓글을 작성하는 사용자 id
		cmdto.setContents(contents);						// 댓글 내용
		
		result = bdao.writeComment(cmdto);
		
		};
		
		System.out.println(" >> 댓글 쓰기를 취소합니다. <<");
		
		return result;
		
		/*
		 * result 값이 0이면 댓글쓰기를 취소한 경우, 
		 * result 값이 1이면 댓글쓰기를 성공한 경우.
		 * result 값이 -1이면 댓글쓰기가 실패한 경우(원글의 번호가 존재하지 않은 경우 foreign key 제약 조건에 위배)
		 */
	} // end of private int writeComment(MemberDTO member, Scanner sc) ****************************************************



	
	
	// 글 수정하기 메소드
	private int updateBoard(MemberDTO member, Scanner sc) {
		int result = 0;
		
		System.out.print("\n>>>> 글 수정하기 <<<");
		
		String boardno = "";
		do {
		
		System.out.println("> 수정할 글번호: ");
		boardno = sc.nextLine();
		
		try {
			Integer.parseInt(boardno);
			
			break;
		}catch (NumberFormatException e) {
			System.out.println("[경고] 수정할 글번호는 숫자만 입력하세요!");
		}

		} while(true);
		
		BoardDTO bdto = bdao.viewContents(boardno);
		
		if(bdto != null) {
			// 수정할 글번호가 글 목록에 존재하는 경우
			
			if (!bdto.getFk_userid().equals(member.getUserid())) {
				// 수정할 글번호가 다른 사용자가 쓴 글일 경우
				System.out.println("[경고] 자신이 작성한 글만 수정할 수 있습니다.");	// 다른 사용자의 글은 수정이 불가합니다.
			}
			else {
				// 본인 작성 글일 경우
				System.out.println("> 글 암호: ");
				String boardpasswd = sc.nextLine();
				
				if(bdto.getBoardpasswd().equals(boardpasswd)) {
					// 글 암호가 일치하는 경우
					
					System.out.println("---------------------------------------------------\n");
					System.out.println("[수정 전 글제목] : " + bdto.getSubject()+"\n");
					System.out.println("[수정 전 글내용] : " + bdto.getContents()+"\n");
					System.out.println("---------------------------------------------------\n");
					
					System.out.println("ㅁ 글 제목(변경하지 않으려면 enter) : ");
					String subject = sc.nextLine();
					
					if(subject != null && subject.trim().isEmpty()){
						subject = bdto.getSubject();
					}
					
					System.out.println("ㅁ 글 내용(변경하지 않으려면 enter) : ");
					String contents = sc.nextLine();
					if(contents != null && contents.trim().isEmpty()){
						contents = bdto.getContents();
					}
					
					if(subject.length() > 100 || contents.length() > 200) {
						System.out.println("[경고] 작성 가능한 글자수를 초과하였습니다.(글제목 최대 100 글자, 글내용 최대 200 글자)");
					}
					else {
						Map<String,String> paraMap = new HashMap<>();
						paraMap.put("subject", subject);
						paraMap.put("contents", contents);
						paraMap.put("boardno", boardno);
						
						result = bdao.updateBoard(paraMap);
						// 1(글 수정이 성공된 경우) or -1 (글수정을 하려고 하나 SQLException 이 발생한 경우)
					}			
				}
				else {
					// 글 암호가 일치하지 않는 경우
					System.out.println("[경고] 글 암호가 일치하지 않아 수정이 불가합니다.");
				}
			}
		} 
		else {
			// 수정할 글번호가 글목록에 존재하지 않는 경우
			System.out.println(">> 수정할 글번호는 "+ boardno +"글목록에 존재하지 않습니다.\n");
		}
			
		return result;
		// 0 or 1 or -1
	} // end of private int updateBoard(MemberDTO member, Scanner sc) ----------------------------------------------------

	
	
	
	// *********** 글 삭제하기 메소드 *******************************************************************************
		private int deleteBoard(MemberDTO member, Scanner sc) {
			int result = 0;
			
			System.out.print("\n>>>> 글 삭제하기 <<<");
			
			String boardno = "";
			do {
			
			System.out.println("> 삭제할 글번호: ");
			boardno = sc.nextLine();
			
			try {
				Integer.parseInt(boardno);
				
				break;
			}catch (NumberFormatException e) {
				System.out.println("[경고] 삭제할 글번호는 숫자만 입력하세요!");
			}

			} while(true);
			
			BoardDTO bdto = bdao.viewContents(boardno);
			
			if(bdto != null) {
				// 삭제할 글번호가 글 목록에 존재하는 경우
				
				if (!bdto.getFk_userid().equals(member.getUserid())) {
					// 삭제할 글번호가 다른 사용자가 쓴 글일 경우
					System.out.println("[경고] 자신이 작성한 글만 삭제할 수 있습니다.");	// 다른 사용자의 글은 수정이 불가합니다.
				}
				else {
					// 본인 작성 글일 경우
					System.out.println("> 글 암호: ");
					String boardpasswd = sc.nextLine();
					
					if(bdto.getBoardpasswd().equals(boardpasswd)) {
						// 글 암호가 일치하는 경우
						
						System.out.println("---------------------------------------------------\n");
						System.out.println("[삭제 전 글제목] : " + bdto.getSubject()+"\n");
						System.out.println("[삭제 전 글내용] : " + bdto.getContents()+"\n");
						System.out.println("---------------------------------------------------\n");
						
						do {
							System.out.println("> 정말로 글을 삭제하시겠습니까? [Y/N]");
							String yn = sc.nextLine();
							
							if("y".equalsIgnoreCase(yn)) {
								result = bdao.deleteBoard(boardno);
								// result 값이 1(삭제) 또는 -1(SQL 오류) 
								
								break;
							}
							else if ("n".equalsIgnoreCase(yn)) {
								System.out.println(">> 글 삭제가 취소되었습니다!");
								break;
							}
							else {
								System.out.println("[경고] y 또는 n 만 입력하세요. \n");
							}
						} while (true);
						
					}
					else {
						// 글 암호가 일치하지 않는 경우
						System.out.println("[경고] 글 암호가 일치하지 않아 삭제가 불가합니다.");
					}
				}
			} 
			else {
				// 삭제할 글번호가 글목록에 존재하지 않는 경우
				System.out.println(">> 삭제할 글번호 "+ boardno +" 는 글목록에 존재하지 않습니다.\n");
			}
				
			return result;
			// 0 or 1 or -1
		} // end of private int deleteBoard(MemberDTO member, Scanner sc) ----------------------------------------------------

		

		// 최근 1주일간 일자별 게시글 작성 건수
		private void statisticsByWeek() {
			System.out.println("\n-------------------------[최근 1주일간 일자별 게시글 작성건수]----------------------------");
					
			String title = "전체\t";
			
			for(int i = 0; i<7; i++) {
				title += MyUtil.addDay(i-6) + "   "; // -6	-5	-4	-3	-2	-1	0
			} // end of for--------------------------------------------------------------------------
			
			// 만약 오늘이 2022-07-27 이라면
			// 전체   2022-07-21    2022-07-22    2022-07-23    2022-07-24    2022-07-25   2022-07-26   2022-07-27
			System.out.println(title);
			System.out.println("--------------------------------------------------------------------------------");
			
			// 최근 일주일 내에 작성된 게시글만 출력하기
			Map<String,Integer> resultMap = bdao.statisticsByWeek();
			// DTO 는 insert 와 select 가 겸해질 때 사용하는 것이다. 근데 지금은 넣거나 뽑는게 아니라 읽어만 온 것이기 때문에...?
			// MAP 으로 그 DTO 값을 불러온다~
			
			String result = resultMap.get("TOTAL") + "\t"
						  + resultMap.get("PREVIOUS6") + "\t"
						  + resultMap.get("PREVIOUS5") + "\t"
						  + resultMap.get("PREVIOUS4") + "\t"
						  + resultMap.get("PREVIOUS3") + "\t"
						  + resultMap.get("PREVIOUS2") + "\t"
						  + resultMap.get("PREVIOUS1") + "\t"
						  + resultMap.get("TODAY") ;
			
			System.out.println(result);
		} // end of private void statisticsByWeek() --------------------------------------------------------------------------


		
		
		
		// ***** 이번달 일자별 게시글 작성 건수 ************************************************************************
		private void statisticsByCurrentMonth() {

			Calendar currentDate = Calendar.getInstance();
			// 현재 날짜와 시간을 얻어온다.
			
			SimpleDateFormat dateft = new SimpleDateFormat("yyyy년 MM월");
			String currentMonth = dateft.format(currentDate.getTime());
			
			System.out.println("\n >> [" + currentMonth +" 일자별 게시글 작성건수] <<<");
			System.out.println("--------------------------------------");
			System.out.println("작성일자 \t 작성건수");
			System.out.println("--------------------------------------");
			
			// 이번달 일자별 게시글 작성건수를 DB에서 가져온 결과물
			List< Map<String,String>> mapList = bdao.statisticsByCurrentMonth();
			
			if(mapList.size() > 0) {
				StringBuilder sb = new StringBuilder();
				
				for(Map<String,String> map : mapList) {
					sb.append(map.get("WRITEDAY") + "\t" + map.get("CNT") + "\n");
				}// end of for ----------------------------------
				
				System.out.println(sb.toString());
			}
			else {
				System.out.println(" 작성된 게시글이 없습니다. ");
			}
		} // end of private void statisticsByCurrentMonth() ************************************************************
}
